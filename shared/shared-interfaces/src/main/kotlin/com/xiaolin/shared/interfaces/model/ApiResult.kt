package com.xiaolin.shared.interfaces.model

open class ApiResult<D>(
    open val code: Int,
    open val message: String,
    open val data: D
) {
    companion object {
        fun <D> success(data: D): ApiResult<D> {
            return success(200, "success", data)
        }
        fun success(): ApiResult<Nothing?> {
            return success(200, "success", null)
        }

        fun <D> success(code: Int, message: String, data: D): ApiResult<D> {
            return ApiResult(code, message, data)
        }

        fun error(message: String): ApiResult<Nothing?> {
            return error(500, message)
        }

        fun error(code: Int, message: String): ApiResult<Nothing?> {
            return ApiResult(code, message, null)
        }
    }
}