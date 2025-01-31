package pt.ipt.dam.powerpantry.api

import android.util.Log
import pt.ipt.dam.powerpantry.data.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object DataRepository {

    //function to fetch all users
    fun fetchAllUsers(onResult:(List<User>) -> Unit, onError: (String) -> Unit) {
        RetrofitClient.instance.getAllUsers().enqueue(object : Callback<AllUsersResponse> {

            override fun onResponse(call: Call<AllUsersResponse>, response: Response<AllUsersResponse>) {
                if (response.isSuccessful) {
                    val users = response.body()?.users ?: emptyList()
                    onResult(users)
                } else {
                    onError("Failed to fetch data. Response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AllUsersResponse>, t: Throwable) {
                onError("ERROR FETCHING API")
            }
        })
    }

    //get user by username
    fun getUser(username: String, onResult: (User) -> Unit, onError: (String) -> Unit) {
        RetrofitClient.instance.getUserByUsername(username).enqueue(object : Callback<AllUsersResponse> {
            override fun onResponse(call: Call<AllUsersResponse>, response: Response<AllUsersResponse>) {
                if (response.isSuccessful) {
                    val users = response.body()?.users
                    val user = users?.find { it.userName == username }

                    if (user != null) {
                        onResult(user)
                    }else{
                        onError("User not found. Maybe wrong credentials?")
                    }
                } else {
                    onError("ERROR RESPONSE")
                }
            }

            override fun onFailure(call: Call<AllUsersResponse>, t: Throwable) {
                onError("API ERROR")
            }
        })
    }

    //check a userName
    //function to fetch a user by id
    fun checkUserExists(username: String, onResult: (Boolean) -> Unit, onError: (String) -> Unit) {
        RetrofitClient.instance.getUserByUsername(username).enqueue(object : Callback<AllUsersResponse> {
            override fun onResponse(call: Call<AllUsersResponse>, response: Response<AllUsersResponse>) {
                if (response.isSuccessful) {
                    val users = response.body()?.users
                    val user = users?.find { it.userName == username }
                    if (user != null) {
                        onResult(true)
                    } else {
                        onResult(false)
                    }
                } else {
                    onError("A problem ocurred, please try again later")
                }
            }

            override fun onFailure(call: Call<AllUsersResponse>, t: Throwable) {
                onError("A problem ocurred, please try again later")
            }
        })
    }


    //function to fetch all products
    fun fetchAllProducts(onResult:(List<Product>) -> Unit, onError: (String) -> Unit) {
        RetrofitClient.instance.getAllProducts().enqueue(object : Callback<AllProductsResponse> {

            override fun onResponse(call: Call<AllProductsResponse>, response: Response<AllProductsResponse>) {
                if (response.isSuccessful) {
                    val products = response.body()?.products ?: emptyList()
                    onResult(products)
                } else {
                    onError("Failed to fetch all products. Response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AllProductsResponse>, t: Throwable) {
                onError("ERROR FETCHING API FOR ALL PRODUCTS")
            }
        })
    }
    //function to fetch product by barcode
    fun fetchProductByCode(code: Long, onResult: (Product) -> Unit, onNotFound: (String) -> Unit, onError: (String) -> Unit) {
        RetrofitClient.instance.getProductByCode(code).enqueue(object : Callback<CodeProductsResponse> {
            override fun onResponse(call: Call<CodeProductsResponse>, response: Response<CodeProductsResponse>) {
                if (response.isSuccessful) {

                    val products = response.body()?.products
                    // Find the product matching the productCode
                    val product = products?.find { it.productCode == code }

                    if (product != null) {
                        onResult(product)  // Return the specific product
                    } else {
                        onNotFound("No product was found associated with code ${code}")
                    }
                } else {
                    onError("Failed to fetch product. Response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CodeProductsResponse>, t: Throwable) {
                onError("Error fetching product: ${t.message}")
            }
        })
    }

}