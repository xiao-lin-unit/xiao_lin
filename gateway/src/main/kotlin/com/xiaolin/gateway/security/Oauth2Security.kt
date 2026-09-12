package com.xiaolin.gateway.security

import com.xiaolin.gateway.security.AuthConst.HEADER_ROLES
import com.xiaolin.gateway.security.AuthConst.HEADER_TENANT_ID
import com.xiaolin.gateway.security.AuthConst.HEADER_USERNAME
import com.xiaolin.gateway.security.AuthConst.HEADER_USER_ID
import com.xiaolin.shared.common.login.LoginUserInfo
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimNames
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.crypto.spec.SecretKeySpec


class JwtUserAuthentication(
    private val jwt: Jwt,
    val user: LoginUserInfo,
    authorities: Collection<GrantedAuthority>,
) : AbstractAuthenticationToken(authorities) {

    init { isAuthenticated = true }

    /** @AuthenticationPrincipal 注入的就是它 */
    override fun getPrincipal(): Any = user
    override fun getCredentials(): Any = jwt.tokenValue
    override fun getName(): String = user.username

    fun getJwt(): Jwt = jwt

    companion object {
        fun authenticate(jwt: Jwt, user: LoginUserInfo, authorities: Collection<GrantedAuthority>): JwtUserAuthentication {
            val authentication = JwtUserAuthentication(jwt, user, authorities)
            authentication.isAuthenticated = true
            return authentication
        }

        fun unauthenticate(jwt: Jwt, user: LoginUserInfo): JwtUserAuthentication {
            val authentication = JwtUserAuthentication(jwt, user, emptyList())
            authentication.isAuthenticated = false
            return authentication
        }
    }
}

@Configuration
class JwtDecoderConfig {

    /** ③ 自己签发 HS256 的场景 */
    @Bean
    @ConditionalOnProperty(name = ["spring.security.oauth2.resourceserver.jwt.secret-value"])
    fun hmacJwtDecoder(
        @Value($$"${spring.security.oauth2.resourceserver.jwt.secret-value}") secret: String,
    ): ReactiveJwtDecoder {
        val key = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        val decoder = NimbusReactiveJwtDecoder.withSecretKey(key).build()

//        decoder.setJwtValidator(
//            DelegatingOAuth2TokenValidator(
//                JwtValidators.createDefault(),
//                { token ->
//                    if (token.getClaimAsString("tenantId").isNullOrBlank()) {
//                        OAuth2TokenValidatorResult.failure(
//                            OAuth2Error("invalid_token", "缺少 tenantId 声明", null)
//                        )
//                    } else {
//                        OAuth2TokenValidatorResult.success()
//                    }
//                }
//            )
//        )
        return decoder
    }

    /** ② JWKS 场景：显式指定地址 + 只接受 RS256（防 alg 混淆攻击） */
    @Bean
    @ConditionalOnProperty(name = ["spring.security.oauth2.resourceserver.jwt.jwk-set-uri"])
    fun jwkDecoder(
        @Value($$"${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") uri: String)
            : ReactiveJwtDecoder =
        NimbusReactiveJwtDecoder
            .withJwkSetUri(uri)
            .jwsAlgorithms { it.add(SignatureAlgorithm.RS256) }   // 白名单算法
            .build()
}

// 自测签发一个token
// util/TokenFactory.kt
//@Service
//class TokenFactory(@Value("\${spring.security.oauth2.resourceserver.jwt.secret-value}") secret: String) {
//
//    private val encoder: JwtEncoder = run {
//        val jwk = OctetSequenceKey.Builder(secret.toByteArray(StandardCharsets.UTF_8))
//            .keyID("gateway-key").build()
//        NimbusJwtEncoder(ImmutableJWKSet(JWKSet(jwk)))
//    }
//
//    fun create(userId: String, username: String, roles: List<String>, tenantId: String): String {
//        val now = Instant.now()
//        val claims = JwtClaimsSet.builder()
//            .issuer("self")
//            .subject(userId)
//            .issuedAt(now)
//            .expiresAt(now.plusSeconds(7200))
//            .claim("username", username)
//            .claim("roles", roles)
//            .claim("tenantId", tenantId)
//            .build()
//        val header = JwsHeader.with(SignatureAlgorithm.HS256).keyId("gateway-key").build()
//        return encoder.encode(JwtEncoderParameters.from(header, claims)).tokenValue
//    }
//}

interface ReactiveUserStore {
    fun findById(userId: Long): Mono<LoginUserInfo>
}

@Repository
class InMemoryUserStore : ReactiveUserStore {
    private val cache = mapOf(
        1L to LoginUserInfo(1, "张三", roles = mutableSetOf("admin"), tenantId = 1),
        2L to LoginUserInfo(2, "李四", roles = mutableSetOf("user"), tenantId = 1),
    )
    override fun findById(userId: Long): Mono<LoginUserInfo> =
        Mono.justOrEmpty(cache[userId])
}

// config/JwtAuthConverterConfig.kt
@Configuration
class JwtAuthConverterConfig {

    companion object {
        /** 通用权限提取：顶层 roles + Keycloak 的 realm_access.roles */
        fun extractAuthorities(jwt: Jwt): List<GrantedAuthority> {
            val result = mutableListOf<GrantedAuthority>()

            // 顶层 roles: ["ADMIN","USER"]
            jwt.getClaimAsStringList("roles")?.forEach {
                result += SimpleGrantedAuthority("ROLE_${it.uppercase()}")
            }

            // Keycloak 嵌套结构：realm_access.roles
            jwt.getClaimAsMap("realm_access")
                ?.get("roles")
                ?.let { it as? Collection<*> }
                ?.forEach { result += SimpleGrantedAuthority("ROLE_${it.toString().uppercase()}") }

            // scope / scp（OAuth2 标准）
            (jwt.getClaimAsStringList("scope") ?: jwt.getClaimAsStringList("scp"))
                ?.flatMap { it.split(" ") }
                ?.forEach { result += SimpleGrantedAuthority("SCOPE_$it") }

            return result
        }
    }

    // ── L1/L2：只做权限映射，principal 仍是 JWT 的 sub 字符串 ──
    @Bean
    @ConditionalOnMissingBean(name = ["jwtAuthConverter"])
    fun simpleJwtAuthConverter(): ReactiveJwtAuthenticationConverter =
        ReactiveJwtAuthenticationConverter().apply {
            setJwtGrantedAuthoritiesConverter { jwt ->
                Flux.fromIterable(extractAuthorities(jwt))
            }
            setPrincipalClaimName(JwtClaimNames.SUB)   // 换成 "user_id" 可改 principal 来源
        }

    // ── L3：异步查库补全用户，principal 直接是业务对象 ──
    // @Bean
    fun enrichedJwtAuthConverter(
        userStore: ReactiveUserStore,
    ): Converter<Jwt, Mono<AbstractAuthenticationToken>> = Converter { jwt ->
        val userId = jwt.subject ?: throw OAuth2AuthenticationException(
            OAuth2Error("invalid_token", "令牌缺少 sub", null)
        )
        userStore.findById(userId.toLong())                       // Mono<LoginUser>，非阻塞
            .defaultIfEmpty(LoginUserInfo.anonymous(userId.toLong()))
            .flatMap { user ->
                if (!user.enabled) {
                    Mono.error(
                        OAuth2AuthenticationException(
                            OAuth2Error("invalid_token", "账号已禁用", null)
                        )
                    )
                } else {
                    val authorities = extractAuthorities(jwt).toMutableSet()
                    user.roles.forEach { authorities += SimpleGrantedAuthority("ROLE_${it.uppercase()}") }
                    Mono.just<AbstractAuthenticationToken>(
                        JwtUserAuthentication.authenticate(jwt, user, authorities)
                    )
                }
            }
    }
}

@Component
class UserInfoRelayFilter : GlobalFilter, Ordered {

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> =
        ReactiveSecurityContextHolder.getContext()
            .map { it.authentication as Authentication }
            .flatMap { auth -> chain.filter(inject(exchange, auth)) }
            .switchIfEmpty(Mono.defer { chain.filter(strip(exchange)) })  // 白名单路径：只清头不透传

    private fun inject(exchange: ServerWebExchange, auth: Authentication): ServerWebExchange {
        val user = (auth.principal as? LoginUserInfo)
        val jwt = (auth as? JwtAuthenticationToken)?.token

        val newRequest = exchange.request.mutate()
            .headers { h ->
                // 安全底线：先清掉外部伪造的身份头
                h.remove(HEADER_USER_ID); h.remove(HEADER_USERNAME); h.remove(HEADER_ROLES); h.remove(HEADER_TENANT_ID)
                if (user != null) {
                    h[HEADER_USER_ID] = user.userId.toString()
                    // 中文必须编码，否则 Netty 写 header 抛非法字符异常
                    h[HEADER_USERNAME] = URLEncoder.encode(user.username, StandardCharsets.UTF_8)
                    h[HEADER_ROLES] = user.roles.joinToString(",")
                    user.tenantId?.let { h[HEADER_TENANT_ID] = it.toString() }
                } else {
                    // principal 是字符串（L1 模式）时的兜底
                    h[HEADER_USER_ID] = auth.name
                    jwt?.getClaimAsStringList("roles")?.let { h[HEADER_ROLES] = it.joinToString(",") }
                }
            }
            .build()
        return exchange.mutate().request(newRequest).build()
    }

    private fun strip(exchange: ServerWebExchange): ServerWebExchange {
        val newRequest = exchange.request.mutate()
            .headers { h ->
                h.remove(HEADER_USER_ID); h.remove(HEADER_USERNAME); h.remove(HEADER_ROLES); h.remove(HEADER_TENANT_ID)
            }.build()
        return exchange.mutate().request(newRequest).build()
    }

    /** 必须早于路由转发（NettyRoutingFilter = Int.MAX_VALUE） */
    override fun getOrder(): Int = -100

}