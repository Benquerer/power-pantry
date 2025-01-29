package pt.ipt.dam.powerpantry

import retrofit2.Call
import retrofit2.http.*

interface Api {
    @GET("users/")
    fun getRows(): Call<UsersResponse>

}