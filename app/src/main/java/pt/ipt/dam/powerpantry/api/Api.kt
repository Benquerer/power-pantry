package pt.ipt.dam.powerpantry.api

import pt.ipt.dam.powerpantry.data.*
import retrofit2.Call
import retrofit2.http.*

/**
 * API interface to interact with retrofit
 */
interface Api {
    //===== Users
    /**
     * API request for fetching all users from endpoint users
     */
    @GET("users/")
    fun getAllUsers(): Call<AllUsersResponse>

    /**
     * API request for fetching all users matching a specified username
     *
     * @param username String - Filter for the fetch
     */
    @GET("users/")
    fun getUserByUsername(@Query("filter[userName]") username:String) : Call<AllUsersResponse>

    /**
     * API request for posting a user to the database
     *
     * @param userJson UserRequest - Formatted JSON of a user to be post
     */
    @POST("users/")
    fun createUser(@Body userJson: UserRequest) : Call<UserResponse>

    //===== Products
    /**
     * API request for fetching all products from endpoint products
     */
    @GET("products/")
    fun getAllProducts() : Call<AllProductsResponse>

    /**
     * API request for fetching all products matching a product code filter
     *
     * @param code Long - Barcode to filter for
     */
    @GET("products")
    fun getProductByCode(@Query("filter[productCode]") code: Long) : Call<CodeProductsResponse>

    /**
     * API request for posting a product to the database
     *
     * @param productJason ProductRequest - Formatted JSON of a product to be posted
     */
    @POST("products/")
    fun createProduct(@Body productJason: ProductRequest) : Call<ProductRequest>




}