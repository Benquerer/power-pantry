package pt.ipt.dam.powerpantry.api

import pt.ipt.dam.powerpantry.data.*
import retrofit2.Call
import retrofit2.http.*

interface Api {
    //Users
    //get all users
    @GET("users/")
    fun getAllUsers(): Call<AllUsersResponse>
    @GET("users")
    fun getUserById(@Query("filter[userName]") username:String) : Call<AllUsersResponse>

    //Products
    //get all products
    @GET("products/")
    fun getAllProducts() : Call<AllProductsResponse>
    //get product by id
    //get product by barcode (filter)
    @GET("products")
    fun getProductByCode(@Query("filter[productCode]") code: Long) : Call<CodeProductsResponse>




}