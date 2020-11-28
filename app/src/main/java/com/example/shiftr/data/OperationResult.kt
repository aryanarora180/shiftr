package com.example.shiftr.data

sealed class OperationResult<out T> {

    data class Success<T>(val data: T) : OperationResult<T>()
    data class Error(val message: String) : OperationResult<Nothing>()

    companion object {
        const val ERROR_CODE_UNDETERMINED = 1000

        fun getErrorMessage(errorCode: Int): String {
            return when (errorCode) {
                400 -> "Bad request"
                401 -> "Your session has expired. Please sign in again"
                422 -> "Invalid details"
                500 -> "A server error occurred. Please try again later"
                else -> "Unable to connect to our servers"
            }
        }
    }
}