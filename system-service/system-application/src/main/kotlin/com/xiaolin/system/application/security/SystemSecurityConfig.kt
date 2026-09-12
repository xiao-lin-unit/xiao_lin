package com.xiaolin.system.application.security

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.OctetSequenceKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.xiaolin.system.application.security.EncodeConfig.Companion.KEY_ID
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.*
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Instant
import javax.crypto.spec.SecretKeySpec

// 配置属性
@Configuration
data class JwtProperties(
    @Value($$"${spring.security.oauth2.resourceserver.jwt.secret-value}")
    var secret: String = "",               // ⚠️ 必须与 Gateway 的 secret-value 完全一致
    @Value($$"${spring.application.name}")
    var issuer: String = "my-system",
    var accessTokenTtl: Long = 7200,
    var refreshTokenTtl: Long = 604800,
)

// infrastructure/security/JwtDecoderConfig.kt
@Configuration
class EncodeConfig(private val props: JwtProperties) {

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder()

    /** ★ 唯一的密钥来源，Encoder / Decoder 都用它 → 配对关系显式化 */
    private val secretKey: SecretKeySpec by lazy {
        require(props.secret.toByteArray(StandardCharsets.UTF_8).size >= 32) {
            "HS256 要求密钥 ≥ 32 字节，当前 ${props.secret.toByteArray().size}"
        }
        SecretKeySpec(props.secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
    }

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwk = OctetSequenceKey.Builder(secretKey).keyID(KEY_ID).build()
        return NimbusJwtEncoder(ImmutableJWKSet(JWKSet(jwk)))
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val decoder = NimbusJwtDecoder.withSecretKey(secretKey).build()

        // ⚠️ 显式声明类型，否则 Kotlin 平台类型推断会让 DelegatingOAuth2TokenValidator 编译不过
        val validators: List<OAuth2TokenValidator<Jwt>> = listOf(
            JwtValidators.createDefaultWithIssuer(props.issuer),
            // BlacklistValidator(redisTemplate),
        )
        decoder.setJwtValidator(DelegatingOAuth2TokenValidator(validators))
        return decoder
    }

    companion object {
        const val KEY_ID = "system-key"
    }
}

@Component
class JwtTokenUtil(
    private val props: JwtProperties,
    private val encoder: JwtEncoder,
    private val decoder: JwtDecoder,
    ) {

    fun encode(subject: String, claims: Map<String, Any>): Jwt {
        val now = Instant.now()
        val builder = JwtClaimsSet.builder()
            .issuer(props.issuer)
            .subject(subject)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(props.accessTokenTtl))

        if (claims.isNotEmpty()) {
            claims.forEach { (string, any) -> builder.claim(string, any) }
        }

        val claimsSet = builder.build()
        val header = JwsHeader.with(MacAlgorithm.HS256).keyId(KEY_ID).build()
        return encoder.encode(JwtEncoderParameters.from(header, claimsSet))
    }

    fun decode(token: String): Jwt? {
        return decoder.decode(token)
    }

}




