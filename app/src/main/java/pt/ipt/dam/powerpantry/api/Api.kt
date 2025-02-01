package pt.ipt.dam.powerpantry.api

import pt.ipt.dam.powerpantry.data.*
import retrofit2.Call
import retrofit2.http.*

interface Api {
    //Users
    //get all users
    @GET("users/")
    fun getAllUsers(): Call<AllUsersResponse>
    //get user by username
    @GET("users/")
    fun getUserByUsername(@Query("filter[userName]") username:String) : Call<AllUsersResponse>
    //create user
    @POST("users/")
    fun createUser(@Body userJson: UserRequest) : Call<UserResponse>

    //Products
    //get all products
    @GET("products/")
    fun getAllProducts() : Call<AllProductsResponse>
    //get product by id
    //get product by barcode (filter)
    @GET("products")
    fun getProductByCode(@Query("filter[productCode]") code: Long) : Call<CodeProductsResponse>
    //post a product
    @POST("products/")
    fun createProduct(@Body productJason: ProductRequest) : Call<ProductRequest>




}