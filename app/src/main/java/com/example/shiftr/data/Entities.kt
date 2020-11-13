package com.example.shiftr.data

import com.squareup.moshi.Json

data class GetResponse<T>(
    @field:Json(name = "data") val data: T,
)

data class LoginBody(
    @field:Json(name = "email") val email: String,
    @field:Json(name = "password") val password: String,
)

data class LoginResponse(
    @field:Json(name = "email") val email: String,
    @field:Json(name = "password") val password: String,
    @field:Json(name = "tokens") val tokens: Tokens,
)

data class Tokens(
    @field:Json(name = "access") val accessToken: String = "",
    @field:Json(name = "refresh") val refreshToken: String = "",
)

data class TodoItem(
    @field:Json(name = "name") val title: String = "",
    @field:Json(name = "desc") val description: String = "",
    @field:Json(name = "category") val category: String = "",
    @field:Json(name = "color") val color: Int = 100,
    @field:Json(name = "priority") val isPriority: Int = NOT_PRIORITY,
    @field:Json(name = "notification_enabled") val notificationEnabled: Int = IS_NOT_NOTIFICATION_ENABLED,
) {
    companion object {
        const val NOT_PRIORITY = 0
        const val IS_PRIORITY = 1

        const val IS_NOT_NOTIFICATION_ENABLED = 0
        const val IS_NOTIFICATION_ENABLED = 1

        const val COLOR_RED = 100
        const val COLOR_ORANGE = 101
        const val COLOR_YELLOW = 102
    }
}
