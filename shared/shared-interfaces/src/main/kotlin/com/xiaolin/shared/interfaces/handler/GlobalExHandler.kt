package com.xiaolin.shared.interfaces.handler

import com.xiaolin.shared.interfaces.model.ApiResult
import org.slf4j.MDC
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.security.sasl.AuthenticationException

@RestControllerAdvice
class GlobalExHandler {

//    @ExceptionHandler(Exception::class)
//    fun handleEx(ex: Exception): ApiResult<Nothing?> {
//        ex.printStackTrace()
//        return ApiResult.error(ex.message ?: "Unknown error")
//    }

//    @ExceptionHandler(BusinessException::class)
//    fun biz(e: BusinessException) = ApiResult.error(e.code, e.message ?: "业务异常")

    @ExceptionHandler(AuthenticationException::class)
    fun auth(e: AuthenticationException): ResponseEntity<ApiResult<Nothing?>> =
        ResponseEntity.status(401).body(ApiResult.error(401, e.message ?: "账号或密码错误"))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun valid(e: MethodArgumentNotValidException) =
        ApiResult.error(400, e.bindingResult.fieldErrors.joinToString(";") { "${it.field}:${it.defaultMessage}" })

    @ExceptionHandler(Exception::class)
    fun ex(e: Exception): ApiResult<Nothing?> {
        val traceId = MDC.get("traceId")
//        log.error("Unhandled exception, traceId={}", traceId, e)   // 不回显
        return ApiResult.error(500, "服务异常，请联系管理员（$traceId）")
    }

}