package pt.ipt.dam.powerpantry.network

import pt.ipt.dam.powerpantry.model.AddUserResponse
import pt.ipt.dam.powerpantry.model.User
import pt.ipt.dam.powerpantry.model.UserResponse
import retrofit2.Call
import retrofit2.http.*

interface SheetyApiService {
    @GET("users") // Fetch all users
    fun getUsers(): Call<UserResponse>

    @POST("users") // Add a new user
    fun addUser(@Body user: User): Call<AddUserResponse>

    @PUT("users/{id}") // Update a user's information
    fun updateUser(@Path("id") id: Int, @Body user: User): Call<AddUserResponse>

    @DELETE("users/{id}") // Delete a user
    fun deleteUser(@Path("id") id: Int): Call<Unit>
}
