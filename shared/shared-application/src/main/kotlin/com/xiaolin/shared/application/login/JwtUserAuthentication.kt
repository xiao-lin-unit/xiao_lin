package com.xiaolin.shared.application.login

import com.xiaolin.shared.common.login.LoginUserInfo
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt


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