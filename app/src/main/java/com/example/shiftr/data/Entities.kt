package com.example.shiftr.data

import com.squareup.moshi.Json

data class GetResponse<T>(
    @field:Json(name = "data") val data: T,
)

data class EmailLoginBody(
    @field:Json(name = "email") val email: String,
    @field:Json(name = "password") val password: String,
)

data class EmailLoginResponse(
    @field:Json(name = "tokens") val tokens: Tokens,
)

data class GoogleLoginBody(
    @field:Json(name = "token") val idToken: String,
    @field:Json(name = "phone_number") val phoneNumber: String,
    @field:Json(name = "profession") val profession: String,
)

data class GoogleLoginResponse(
    @field:Json(name = "token") val tokenData: EmailLoginResponse,
)

data class RefreshTokenBody(
    @field:Json(name = "refresh") val refreshToken: String,
)

data class RefreshTokenResponse(
    @field:Json(name = "refresh") val newAccessToken: String,
)

data class Tokens(
    @field:Json(name = "access") val accessToken: String = "",
    @field:Json(name = "refresh") val refreshToken: String = "",
)

data class RegisterBody(
    @field:Json(name = "email") val email: String,
    @field:Json(name = "username") val username: String,
    @field:Json(name = "password") val password: String,
    @field:Json(name = "phone_number") val phoneNumber: String,
    @field:Json(name = "profession") val profession: String,
)

data class Todo(
    @field:Json(name = "id") val id: Int = 0,
    @field:Json(name = "name") val title: String = "",
    @field:Json(name = "description") val description: String = "",
    @field:Json(name = "color") val color: String = "#FFFFFF",
)

data class TodoBody(
    @field:Json(name = "name") val title: String = "",
    @field:Json(name = "description") val description: String = "",
    @field:Json(name = "color") val color: String = "#FFFFFF",
)

data class TodoItem(
    @field:Json(name = "id") val id: Int = 0,
    @field:Json(name = "todo") val title: String = "",
    @field:Json(name = "todo_list") val todoId: Int = 0,
    @field:Json(name = "done") val color: String = "#FFFFFF",
    @field:Json(name = "priority") val priority: Int = 0,
    @field:Json(name = "deadline") val deadline: String = "",
)
