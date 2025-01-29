package pt.ipt.dam.powerpantry.api

import pt.ipt.dam.powerpantry.data.UsersResponse
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @GET("users/")
    fun getRows(): Call<UsersResponse>

}