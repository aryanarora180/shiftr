package com.example.shiftr.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

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
    @field:Json(name = "access") val newAccessToken: String,
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

@Parcelize
data class Todo(
    @field:Json(name = "id") val id: Int = 0,
    @field:Json(name = "name") val title: String = "",
    @field:Json(name = "description") val description: String = "",
    @field:Json(name = "color") val color: String = "#FFFFFF",
) : Parcelable

data class TodoBody(
    @field:Json(name = "name") val title: String = "",
    @field:Json(name = "description") val description: String = "",
    @field:Json(name = "color") val color: String = "#FFFFFF",
)

data class TodoItem(
    @field:Json(name = "id") val itemId: Int = 0,
    @field:Json(name = "todo") val itemText: String = "",
    @field:Json(name = "todo_list") val todoId: Int = 0,
    @field:Json(name = "done") val done: Boolean = false,
    @field:Json(name = "priority") val priority: String = DEADLINE_LOW,
    @field:Json(name = "deadline") val deadline: String = "",
) {
    companion object {
        const val DEADLINE_LOW = "low"
        const val DEADLINE_IMPORTANT = "imp"
        const val DEADLINE_URGENT = "urg"
    }

    fun getPriorityAsInt() = when (priority) {
        DEADLINE_LOW -> 0
        DEADLINE_IMPORTANT -> 1
        DEADLINE_URGENT -> 2
        else -> 0
    }

    fun getPriorityColor()  = when (priority) {
        DEADLINE_LOW -> "#FFA000"
        DEADLINE_IMPORTANT -> "#E64A19"
        DEADLINE_URGENT -> "#D32F2F"
        else -> "#FFA000"
    }

    fun getPriorityText()  = when (priority) {
        DEADLINE_LOW -> "Low"
        DEADLINE_IMPORTANT -> "Important"
        DEADLINE_URGENT -> "Urgent"
        else -> "Low"
    }
}