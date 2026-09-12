package com.xiaolin.gateway.security
//
//import com.xiaolin.gateway.security.JwtTokenProvider
//import com.xiaolin.shared.common.login.LoginUserInfo
//import io.jsonwebtoken.Claims
//import io.jsonwebtoken.ExpiredJwtException
//import org.springframework.cloud.gateway.filter.GatewayFilterChain
//import org.springframework.cloud.gateway.filter.GlobalFilter
//import org.springframework.core.Ordered
//import org.springframework.http.HttpStatus
//import org.springframework.http.MediaType
//import org.springframework.http.server.reactive.ServerHttpRequest
//import org.springframework.http.server.reactive.ServerHttpResponse
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
//import org.springframework.security.core.Authentication
//import org.springframework.security.core.authority.SimpleGrantedAuthority
//import org.springframework.security.core.context.ReactiveSecurityContextHolder
//import org.springframework.security.oauth2.jwt.JwtException
//import org.springframework.stereotype.Component
//import org.springframework.util.AntPathMatcher
//import org.springframework.web.server.ServerWebExchange
//import reactor.core.publisher.Mono
//import tools.jackson.databind.ObjectMapper
//import java.net.URLEncoder
//import java.nio.charset.StandardCharsets

//@Component
//class JwtTokenProvider(@Value($$"${security.jwt.secret}") secret: String) {
//    // HS256 要求密钥 >= 256 bit（32 字节），否则启动就抛 WeakKeyException
//    private val key: SecretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))
//
//    fun parse(token: String?): Claims? {
//        return Jwts.parser()
//            .verifyWith(key)
//            .build()
//            .parseSignedClaims(token)
//            .getPayload()
//    }
//}

//@Component
//class AuthGlobalFilter(
//    private val jwtTokenProvider: JwtTokenProvider,
//    private val authProperties: AuthProperties,
//    private val objectMapper: ObjectMapper
//) : GlobalFilter, Ordered {
//    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
//        val request: ServerHttpRequest = exchange.request
//        val path: String = request.uri.getPath()
//
//        // 1）白名单直接放行（但仍要清理外部伪造的身份头）
//        if (isSkip(path)) {
//            return chain.filter(cleanup(exchange))
//        }
//
//        // 2）取 token
//        val token = resolveToken(request)
//        if (token.isNullOrBlank()) {
//            return writeJson(exchange, HttpStatus.UNAUTHORIZED, "缺少令牌")
//        }
//
//        // 3）解析
//        val userInfo: LoginUserInfo?
//        try {
//            userInfo = buildUser(jwtTokenProvider.parse(token)!!)
//        } catch (e: ExpiredJwtException) {
//            return writeJson(exchange, HttpStatus.UNAUTHORIZED, "令牌已过期")
//        } catch (e: JwtException) {
////            log.warn("非法 token, path={}, reason={}", path, e.message)
//            return writeJson(exchange, HttpStatus.UNAUTHORIZED, "令牌无效")
//        } catch (e: IllegalArgumentException) {
////            log.warn("非法 token, path={}, reason={}", path, e.message)
//            return writeJson(exchange, HttpStatus.UNAUTHORIZED, "令牌无效")
//        }
//
//        // 4）构造新请求：先清掉外部伪造头，再注入解析结果
//        val mutated: ServerHttpRequest = request.mutate()
//            .headers { headers ->
//                headers.remove(AuthConst.HEADER_USER_ID)
//                headers.remove(AuthConst.HEADER_USERNAME)
//                headers.remove(AuthConst.HEADER_ROLES)
//                headers.set(AuthConst.HEADER_USER_ID, userInfo.userId.toString())
//                // 中文用户名必须 URL 编码，否则 Netty 写 header 会抛非法字符异常
//                headers.set(
//                    AuthConst.HEADER_USERNAME,
//                    URLEncoder.encode(userInfo.username, StandardCharsets.UTF_8)
//                )
//                headers.set(
//                    AuthConst.HEADER_ROLES,
//                    java.lang.String.join(",", userInfo.roles)
//                )
//            }
//            .build()
//
//        val newExchange: ServerWebExchange = exchange.mutate().request(mutated).build()
//
//        // ① 网关内部上下文：后续 GlobalFilter / 自定义断言可读
//        newExchange.attributes[AuthConst.ATTR_USER_INFO] = userInfo
//
//        // ③ 写入 Security 上下文（网关内若用 @PreAuthorize 需要；不用 Security 可删掉 contextWrite）
//        val auth: Authentication = UsernamePasswordAuthenticationToken(
//            userInfo.userId, null,
//            userInfo.roles.map { r -> SimpleGrantedAuthority("ROLE_$r") }.toList()
//        ) as Authentication
//
//        return chain.filter(newExchange)
//            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
//    }
//
//    private fun buildUser(claims: Claims): LoginUserInfo {
//        val rolesStr = claims.get("roles", String::class.java)
//        return LoginUserInfo(
//            userId = claims.subject.toLong(),
//            username = claims.get("username", String::class.java),
//            roles = (if (!rolesStr.isNullOrBlank()) mutableSetOf(*rolesStr.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) else mutableSetOf()),
//            tenantId = claims.get("tenantId", Long::class.java)
//        )
//    }
//
//    private fun resolveToken(request: ServerHttpRequest): String? {
//        val header: String? = request.headers.getFirst(AuthConst.HEADER_AUTH)
//        if (!header.isNullOrBlank() && header.startsWith(AuthConst.BEARER)) {
//            return header.substring(AuthConst.BEARER.length).trim { it <= ' ' }
//        }
//        return null
//    }
//
//    private fun isSkip(path: String): Boolean {
//        return authProperties.skipPaths.stream()
//            .anyMatch { p -> PATH_MATCHER.match(p, path) }
//    }
//
//    /** 放行路径上也要清头，否则客户端可以自己伪造 X-User-Id 直接冒充任意用户  */
//    private fun cleanup(exchange: ServerWebExchange): ServerWebExchange {
//        val cleaned: ServerHttpRequest = exchange.request.mutate()
//            .headers { h ->
//                h.remove(AuthConst.HEADER_USER_ID)
//                h.remove(AuthConst.HEADER_USERNAME)
//                h.remove(AuthConst.HEADER_ROLES)
//            }.build()
//        return exchange.mutate().request(cleaned).build()
//    }
//
//    private fun writeJson(exchange: ServerWebExchange, status: HttpStatus, msg: String): Mono<Void> {
//        val response: ServerHttpResponse = exchange.response
//        response.statusCode = status
//        response.headers.contentType = MediaType.APPLICATION_JSON
//        try {
//            val bytes = objectMapper.writeValueAsBytes(
//                mapOf("code" to status.value(), "message" to msg)
//            )
//            return response.writeWith(
//                Mono.just(
//                    response.bufferFactory().wrap(bytes)
//                )
//            )
//        } catch (e: Exception) {
//            return response.setComplete()
//        }
//    }
//
//    /** 必须排在路由转发之前；NettyRoutingFilter 是 Integer.MAX_VALUE  */
//    override fun getOrder(): Int {
//        return -100
//    }
//
//    companion object {
//        private val PATH_MATCHER = AntPathMatcher()
//    }
//}
