package com.xiaolin.gateway.router

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.cloud.gateway.route.builder.PredicateSpec
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import java.util.function.Function


@Configuration
class GatewayRoutesConfig {
    @Bean
    fun customRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route("system-service", Function { r: PredicateSpec? ->
                r!!
                    .path("/system/**")
//                    .filters(Function { f: GatewayFilterSpec? -> f!!.addRequestHeader("X-From-Service", "gateway") })
                    .uri("http://localhost:8081")
            })

            .route("order-service", Function { r: PredicateSpec? ->
                r!!
                    .path("/api/order/**")
                    .and()
                    .method(HttpMethod.GET, HttpMethod.POST)
//                    .filters(Function { f: GatewayFilterSpec? ->
//                        f!!.addRequestHeader("X-From-Service", "gateway")
//                    })
                    .uri("http://127.0.0.1:8082")
            })

            .route("fallback", Function { r: PredicateSpec? ->
                r!!
                    .path("/fallback")
                    .uri("forward:/default-fallback")
            }) // 转发到网关自身接口
            .build()
    }
}