package com.xiaolin.gateway.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import tools.jackson.databind.ObjectMapper

object AuthConst {
    const val HEADER_AUTH: String = "Authorization"
    const val BEARER: String = "Bearer "
    const val HEADER_USER_ID: String = "X-User-Id"
    const val HEADER_USERNAME: String = "X-User-Name"
    const val HEADER_ROLES: String = "X-User-Roles"
    const val HEADER_TENANT_ID: String = "X-Tenant-Id"

    /** 存进 exchange attributes 的 key  */
    const val ATTR_USER_INFO: String = "LOGIN_USER_INFO"
}

@ConfigurationProperties(prefix = "gateway.security")
data class AuthProperties(
    var skipPaths: List<String> = listOf(
        "/system/user/register",
        "/system/auth/login"
    )
)

@Component
class JsonAuthEntryPoint() : ServerAuthenticationEntryPoint {

    override fun commence(
        exchange: ServerWebExchange,
        ex: AuthenticationException
    ): Mono<Void> {
        val error = (ex as? OAuth2AuthenticationException)?.error
        val code = error?.errorCode ?: "invalid_token"
        val desc = error?.description ?: "需要有效的 Bearer 令牌"

        val response = exchange.response
        response.statusCode = HttpStatus.UNAUTHORIZED
        response.headers.contentType = MediaType.APPLICATION_JSON
        response.headers[HttpHeaders.WWW_AUTHENTICATE] =
            """Bearer error="$code", error_description="${desc.replace("\"", "'")}" """

        return response.writeBody(mapOf("code" to 401, "message" to desc))
    }
}

@Component
class JsonAccessDeniedHandler() : ServerAccessDeniedHandler {

    override fun handle(
        exchange: ServerWebExchange,
        denied: org.springframework.security.access.AccessDeniedException
    ): Mono<Void> {
        val response = exchange.response
        response.statusCode = HttpStatus.FORBIDDEN
        response.headers.contentType = MediaType.APPLICATION_JSON
        return response.writeBody(mapOf("code" to 403, "message" to "权限不足"))
    }
}

private fun ServerHttpResponse.writeBody(payload: Any): Mono<Void> =
    writeWith(Mono.just(bufferFactory().wrap(ObjectMapper().writeValueAsBytes(payload))))


@Configuration
@EnableWebFluxSecurity                 // WebFlux 专用，不是 @EnableWebSecurity
@EnableReactiveMethodSecurity          // 用 @PreAuthorize 才加
class GatewaySecurityConfig(
    private val authProps: AuthProperties,
    private val jwtAuthConverter: Converter<Jwt, Mono<AbstractAuthenticationToken>>,
    private val jwtAuthDecoder: ReactiveJwtDecoder,
    private val entryPoint: ServerAuthenticationEntryPoint,
    private val deniedHandler: ServerAccessDeniedHandler,
) {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain = http {
        csrf { disable() }             // 纯 API 转发，无表单提交
        httpBasic { disable() }
        formLogin { disable() }

        // 无状态：不持久化 SecurityContext，避免网关侧产生 Session
        securityContextRepository = NoOpServerSecurityContextRepository.getInstance()

        authorizeExchange {
            authorize(
                ServerWebExchangeMatchers.pathMatchers(HttpMethod.OPTIONS, "/**"),
                permitAll
            )
            authProps.skipPaths.forEach { path -> authorize(path, permitAll) }

            authorize("/admin/**", hasRole("ADMIN"))
            authorize("/api/order/**", hasAnyRole("USER", "ADMIN"))
            // authorize("/api/msg/**", hasScope("message:read"))   // OAuth2 scope 场景

            authorize(anyExchange, authenticated)
        }

        oauth2ResourceServer {
            jwt {
                // decoder 已在 JwtDecoderConfig 声明为 Bean，这里可省略
                jwtAuthenticationConverter = jwtAuthConverter
                jwtDecoder = jwtAuthDecoder
                // 从自定义 header / query 取 token（默认只读 Authorization: Bearer）
//                 bearerTokenConverter = customBearerConverter()
            }

        }

        exceptionHandling {
            authenticationEntryPoint = entryPoint       // 401
            accessDeniedHandler = deniedHandler   // 403
        }

    }

    /** 可选：改从自定义头或 URL 参数取 token */
    private fun customBearerConverter(): ServerAuthenticationConverter =
        ServerBearerTokenAuthenticationConverter().apply {
            setBearerTokenHeaderName("X-Access-Token")
            setAllowUriQueryParameter(true)
        }
}