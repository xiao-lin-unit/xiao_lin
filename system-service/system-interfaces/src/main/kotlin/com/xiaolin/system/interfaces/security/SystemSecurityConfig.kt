package com.xiaolin.system.interfaces.security


import com.xiaolin.shared.application.login.JwtUserAuthentication
import com.xiaolin.shared.common.constants.UserKind
import com.xiaolin.shared.common.login.LoginUserInfo
import com.xiaolin.shared.interfaces.model.ApiResult
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class AuthEntryPoint(private val objectMapper: ObjectMapper) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        if (response.isCommitted) return

        val error = (authException as? OAuth2AuthenticationException)?.error
        val code = error?.errorCode ?: "invalid_token"      // invalid_token / insufficient_scope
        val message = error?.description ?: "未认证或令牌无效"

        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = "application/json;charset=UTF-8"

        // ⚠️ HTTP header 值不能含非 ASCII（中文会抛异常），这里只放英文错误码
        response.setHeader(
            HttpHeaders.WWW_AUTHENTICATE,
            """Bearer realm="api", error="$code""""
        )

        response.writer.use {
            it.write(objectMapper.writeValueAsString(ApiResult.error(401, message)))
        }
    }
}

// interfaces/security/JsonAccessDeniedHandler.kt
@Component
class JsonAccessDeniedHandler(private val objectMapper: ObjectMapper) : AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        if (response.isCommitted) return

        response.status = HttpStatus.FORBIDDEN.value()
        response.contentType = "application/json;charset=UTF-8"
        response.writer.use {
            it.write(objectMapper.writeValueAsString(ApiResult.error(403, "权限不足")))
        }
    }
}

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SystemSecurityConfig(
    private val authEntryPoint: AuthenticationEntryPoint,
    private val deniedHandler: AccessDeniedHandler,
    private val decoder: JwtDecoder,
) {
    @Bean
    fun jwtAuthenticationConverter(): Converter<Jwt, AbstractAuthenticationToken> = Converter { jwt ->
        val roles = jwt.getClaimAsStringList("roles").orEmpty()
        val authorities = roles.map { SimpleGrantedAuthority("ROLE_${it.uppercase()}") } +
                // scope 场景可加：jwt.getClaimAsStringList("scope")?.flatMap { it.split(" ") }
                //     ?.map { SimpleGrantedAuthority("SCOPE_$it") }.orEmpty()
                emptyList()

        val user = LoginUserInfo.create(
            userId = jwt.subject?.toLong() ?: 0L,
            username = jwt.getClaimAsString("username").orEmpty(),
            roles = roles.toSet(),
            tenantId = jwt.getClaim("tenantId") as? Long,
            userKind = jwt.getClaimAsString("userKind")?.let { runCatching { UserKind.valueOf(it) }.getOrDefault(UserKind.NORMAL) } ?: UserKind.NORMAL,
        )
        JwtUserAuthentication.authenticate(jwt, user, authorities)
    }



    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            httpBasic { disable() }
            formLogin { disable() }
            logout { disable() }

            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }

//            requestCache{  }        // ★ 不缓存请求，避免被重定向到登录页
            anonymous { }                     // 保持默认匿名即可

            authorizeHttpRequests {
                authorize(HttpMethod.OPTIONS, "/**", permitAll)
                authorize("/auth/**", permitAll )
                authorize("/user/register", permitAll )
//                skipProps.skipPaths.forEach { path -> authorize(path, permitAll) }
                authorize("/admin/**", hasRole("ADMIN"))
                authorize(anyRequest, authenticated)
            }

            oauth2ResourceServer {
                jwt {
                    jwtDecoder = decoder
                    jwtAuthenticationConverter = jwtAuthenticationConverter()  // 需要时再加
                }
            }

            exceptionHandling {
                authenticationEntryPoint = authEntryPoint
                accessDeniedHandler = deniedHandler
            }
        }
        return http.build()
    }

}