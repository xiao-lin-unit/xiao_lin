package com.xiaolin.shared.interfaces.handler

import com.xiaolin.shared.interfaces.model.ApiResult
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExHandler {

    @ExceptionHandler(Exception::class)
    fun handleEx(ex: Exception): ApiResult<Nothing?> {
        ex.printStackTrace()
        return ApiResult.error(ex.message ?: "Unknown error")
    }

}