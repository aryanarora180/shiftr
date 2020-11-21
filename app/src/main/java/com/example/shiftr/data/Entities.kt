package com.example.shiftr.data

import android.os.Parcelable
import com.example.shiftr.data.TodoItem.Companion.PRIORITY_LOW
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import okhttp3.internal.Util.UTC
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter

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

data class TodoItemBody(
    @field:Json(name = "todo") val itemText: String = "",
    @field:Json(name = "todo_list") val todoId: Int = 0,
    @field:Json(name = "done") val done: Boolean = false,
    @field:Json(name = "priority") val priority: String = PRIORITY_LOW,
    @field:Json(name = "deadline") val deadline: String = "",
)

data class TodoItem(
    @field:Json(name = "id") val itemId: Int = 0,
    @field:Json(name = "todo") val itemText: String = "",
    @field:Json(name = "todo_list") val todoId: Int = 0,
    @field:Json(name = "done") val done: Boolean = false,
    @field:Json(name = "priority") val priority: String = PRIORITY_LOW,
    @field:Json(name = "deadline") val deadline: String = "",
) {
    companion object {
        const val PRIORITY_LOW = "low"
        const val PRIORITY_IMPORTANT = "imp"
        const val PRIORITY_URGENT = "urg"
    }

    fun getPriorityAsInt() = when (priority) {
        PRIORITY_LOW -> 2
        PRIORITY_IMPORTANT -> 1
        PRIORITY_URGENT -> 0
        else -> 2
    }

    fun getPriorityColor()  = when (priority) {
        PRIORITY_LOW -> "#FFA000"
        PRIORITY_IMPORTANT -> "#E64A19"
        PRIORITY_URGENT -> "#D32F2F"
        else -> "#FFA000"
    }

    fun getPriorityText()  = when (priority) {
        PRIORITY_LOW -> "Low"
        PRIORITY_IMPORTANT -> "Important"
        PRIORITY_URGENT -> "Urgent"
        else -> "Low"
    }

    fun getEpochTime() = LocalDateTime.parse(deadline, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toEpochSecond(ZoneOffset.UTC)
}

data class TodoItemUpdateDoneBody(
    @field:Json(name = "todo_list") val todoId: Int = 0,
    @field:Json(name = "done") val done: Boolean = false,
)