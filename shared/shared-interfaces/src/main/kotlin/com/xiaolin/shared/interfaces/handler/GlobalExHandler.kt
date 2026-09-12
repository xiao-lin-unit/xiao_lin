package com.xiaolin.shared.interfaces.handler

import com.xiaolin.shared.interfaces.model.ApiResult
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.UUID

/**
 * 全局异常处理。
 *
 * 三条原则：
 *  1. 状态码必须反映语义（401/403/400/500），不能一律 200；
 *  2. 异常原文绝不回显给前端（防表结构、内网信息泄露），只回 traceId；
 *  3. 服务端必须留痕 —— 未捕获异常一律 ERROR 级日志 + 完整堆栈。
 */
@RestControllerAdvice
class GlobalExHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 登录失败。
     *
     * ⚠️ 必须是 org.springframework.security.core.AuthenticationException。
     * javax.security.sasl.AuthenticationException 继承 SaslException → IOException，
     * 与 Spring Security 的 BadCredentialsException 毫无关系，导错则本分支永不命中，
     * 登录失败会退化成「200 OK + code=500」。
     */
    @ExceptionHandler(AuthenticationException::class)
    fun auth(e: AuthenticationException): ResponseEntity<ApiResult<Nothing?>> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResult.error(401, "账号或密码错误"))

    @ExceptionHandler(AccessDeniedException::class)
    fun denied(e: AccessDeniedException): ResponseEntity<ApiResult<Nothing?>> =
        ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResult.error(403, "权限不足"))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun valid(e: MethodArgumentNotValidException): ResponseEntity<ApiResult<Nothing?>> {
        val msg = e.bindingResult.fieldErrors
            .joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        return ResponseEntity.badRequest().body(ApiResult.error(400, msg))
    }

    /** 兜底：记录完整堆栈，traceId 返回给前端便于用户报障时定位 */
    @ExceptionHandler(Exception::class)
    fun ex(e: Exception): ResponseEntity<ApiResult<Nothing?>> {
        val traceId = UUID.randomUUID().toString().substring(0, 8)
        log.error("Unhandled exception, traceId={}", traceId, e)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResult.error(500, "服务异常，请联系管理员（$traceId）"))
    }
}
