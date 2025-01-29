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

    //function to fetch a user by id
    fun fetchUserById(userId: Int, onResult: (User) -> Unit, onError: (String) -> Unit) {
        RetrofitClient.instance.getUserById(userId).enqueue(object : Callback<IdUserResponse> {
            override fun onResponse(call: Call<IdUserResponse>, response: Response<IdUserResponse>) {
                if (response.isSuccessful) {
                    val user = response.body()?.user
                    if (user != null) {
                        onResult(user)  // Return the user
                    } else {
                        onError("User not found.")
                    }
                } else {
                    onError("Failed to fetch user. Response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<IdUserResponse>, t: Throwable) {
                onError("Error fetching user: ${t.message}")
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
    //function to fetch product by id
    fun fetchProductByID(id: Int, onResult: (Product) -> Unit, onError: (String) -> Unit) {
        RetrofitClient.instance.getProductByID(id).enqueue(object : Callback<IdProductResponse> {
            override fun onResponse(call: Call<IdProductResponse>, response: Response<IdProductResponse>) {
                if (response.isSuccessful) {
                    val product = response.body()?.product
                    if (product != null) {
                        onResult(product)  // Return the user
                    } else {
                        onError("Product not found.")
                    }
                } else {
                    onError("Failed to fetch product. Response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<IdProductResponse>, t: Throwable) {
                onError("Error fetching product: ${t.message}")
            }
        })
    }
    //function to fetch product by barcode
    fun fetchProductByCode(code: Long, onResult: (Product) -> Unit, onError: (String) -> Unit) {
        RetrofitClient.instance.getProductByCode(code).enqueue(object : Callback<CodeProductsResponse> {
            override fun onResponse(call: Call<CodeProductsResponse>, response: Response<CodeProductsResponse>) {
                if (response.isSuccessful) {

                    val products = response.body()?.products
                    // Find the product matching the productCode
                    val product = products?.find { it.productCode == code }

                    if (product != null) {
                        onResult(product)  // Return the specific product
                    } else {
                        onError("Product with code $code not found.")
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