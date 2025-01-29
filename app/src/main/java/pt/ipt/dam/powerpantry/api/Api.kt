package pt.ipt.dam.powerpantry.api

import pt.ipt.dam.powerpantry.data.*
import retrofit2.Call
import retrofit2.http.*

interface Api {
    //Users
    //get all users
    @GET("users/")
    fun getAllUsers(): Call<AllUsersResponse>
    //get user by id
    @GET("users/{id}")
    fun getUserById(@Path("id") id:Int) : Call<IdUserResponse>

    //Products
    //get all products
    @GET("products/")
    fun getAllProducts() : Call<AllProductsResponse>
    //get product by id
    @GET("products/{id}")
    fun getProductByID(@Path("id") id: Int) : Call<IdProductResponse>
    //get product by barcode (filter)
    @GET("products")
    fun getProductByCode(@Query("filter[productCode]") code: Long) : Call<CodeProductsResponse>

}