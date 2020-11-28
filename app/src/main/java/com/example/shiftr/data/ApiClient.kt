package com.example.shiftr.data

import android.content.Context
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

object ApiClient {

    private const val BASE_URL = "https://shiftrio.herokuapp.com/api/v1/"

    private fun okHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .authenticator(AuthInterceptor(context))
            .addInterceptor(AuthInterceptor(context))
            .build()
    }

    private lateinit var apiService: ApiService
    fun build(context: Context): ApiService {
        if (!(ApiClient::apiService.isInitialized)) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .client(okHttpClient(context))
                .build()
            apiService = retrofit.create(ApiService::class.java)
        }
        return apiService
    }

    interface ApiService {

        @POST("auth/login/")
        suspend fun loginUserWithEmail(
            @Body bodyEmail: EmailLoginBody,
        ): GetResponse<EmailLoginResponse>

        @POST("auth/register/")
        suspend fun registerUserWithEmail(
            @Body body: RegisterBody,
        ): ResponseBody

        @POST("auth/google-auth/")
        suspend fun loginUserWithGoogle(
            @Body body: GoogleLoginBody,
        ): GetResponse<GoogleLoginResponse>

        @POST("auth/token/refresh/")
        suspend fun refreshToken(
            @Body body: RefreshTokenBody,
        ): RefreshTokenResponse

        @GET("todo/")
        suspend fun getTodo(): GetResponse<List<Todo>>

        @POST("todo/")
        suspend fun addTodo(
            @Body body: TodoBody
        ): Todo

        @DELETE("todo/{id}")
        suspend fun deleteTodo(
            @Path("id") id: Int
        )

        @GET("todo/todo-items/")
        suspend fun getTodoItems(): GetResponse<List<TodoItem>>

        @POST("todo/todo-items/")
        suspend fun addTodoItem(
            @Body body: TodoItemBody
        ): GetResponse<TodoItem>

        @PATCH("todo/todo-items/{id}")
        suspend fun setTodoItemDone(
            @Path("id") todoItemId: Int,
            @Body body: TodoItemUpdateDoneBody,
        )

        @DELETE("todo/todo-items/{id}")
        suspend fun deleteTodoItem(
            @Path("id") id: Int
        )

        @GET("inventory/")
        suspend fun getInventory(): GetResponse<List<InventoryItem>>

        @POST("inventory/")
        suspend fun addInventoryItem(
            @Body body: InventoryItemBody,
        )

        @PATCH("inventory/{id}")
        suspend fun updateInventoryQuantity(
            @Path("id") id: Int,
            @Body body: InventoryItemQuantityUpdateBody,
        )

        @DELETE("inventory/{id}")
        suspend fun deleteInventoryItem(
            @Path("id") id: Int,
        )

        @GET("auth/dashboard/")
        suspend fun getDashboard(): GetResponse<DashboardResponse>

        @Multipart
        @POST("todo/documents/")
        suspend fun uploadDocumentForTodo(
            @Part file: MultipartBody.Part,
            @Part("todo_item") todoId: RequestBody,
        )

        @POST("todo/schedule-email/")
        suspend fun scheduleEmail(
            @Body body: ScheduleEmailTodoItemBody,
        )
    }
}