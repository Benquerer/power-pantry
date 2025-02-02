package pt.ipt.dam.powerpantry.api

import pt.ipt.dam.powerpantry.data.AllProductsResponse
import pt.ipt.dam.powerpantry.data.AllUsersResponse
import pt.ipt.dam.powerpantry.data.CodeProductsResponse
import pt.ipt.dam.powerpantry.data.Product
import pt.ipt.dam.powerpantry.data.ProductRequest
import pt.ipt.dam.powerpantry.data.User
import pt.ipt.dam.powerpantry.data.UserRequest
import pt.ipt.dam.powerpantry.data.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Repository of API-related methods
 */
object DataRepository {

    /**
     * Method for fetching all registered users from the database
     *
     * @param onResult List<Users> - Callback to handle successful response from API.
     * @param onError String - Callback to handle errors
     */
    fun fetchAllUsers(onResult:(List<User>) -> Unit, onError: (String) -> Unit) {
        //make api call using retrofit (async)
        RetrofitClient.instance.getAllUsers().enqueue(object : Callback<AllUsersResponse> {
            //if request goes through
            override fun onResponse(call: Call<AllUsersResponse>, response: Response<AllUsersResponse>) {
                //if the response is successful pass the list of users in callback
                if (response.isSuccessful) {
                    val users = response.body()?.users ?: emptyList() //uses empty list if answer is null
                    onResult(users)
                } else {
                    //send error message on callback if response not successful
                    onError("Failed to fetch data. Response code: ${response.code()}")
                }
            }
            //if api request fails
            override fun onFailure(call: Call<AllUsersResponse>, t: Throwable) {
                //send message on callback
                onError("ERROR FETCHING API")
            }
        })
    }

    /**
     * Method for fetching a filtered list of users, matching the username in params.
     * Should only ever return a single user at most, since there are no users with same username.
     *
     * @param username String - username to be used as filter in fetch
     * @param onResult User - Callback to handle successful response from API
     * @param onError String - Callback to handle errors
     */
    fun getUser(username: String, onResult: (User) -> Unit, onError: (String) -> Unit) {
        //make api call using retrofit (async)
        RetrofitClient.instance.getUserByUsername(username).enqueue(object : Callback<AllUsersResponse> {
            //if request goes through
            override fun onResponse(call: Call<AllUsersResponse>, response: Response<AllUsersResponse>) {
                //if the response was successful return the user on callback
                if (response.isSuccessful) {
                    //fetched list
                    val users = response.body()?.users
                    //find matching user in users list
                    val user = users?.find { it.userName == username }
                    if (user != null) {
                        //send user on callback
                        onResult(user)
                    }else{
                        //callback of user not found
                        onError("User not found. Maybe wrong credentials?")
                    }
                } else {
                    //callback on response error
                    onError("ERROR RESPONSE")
                }
            }
            //if api request fails
            override fun onFailure(call: Call<AllUsersResponse>, t: Throwable) {
                //callback on api error
                onError("API ERROR")
            }
        })
    }

    /**
     * Method for checking if a user is registered in database
     *
     * @param username String - Username to be used as filter in fetch
     * @param onResult Boolean - Whether the user exists in database or not
     * @param onError String - Callback to handle errors
     */
    fun checkUserExists(username: String, onResult: (Boolean) -> Unit, onError: (String) -> Unit) {
        //make api call using retrofit (async)
        RetrofitClient.instance.getUserByUsername(username).enqueue(object : Callback<AllUsersResponse> {
            //if request goes through
            override fun onResponse(call: Call<AllUsersResponse>, response: Response<AllUsersResponse>) {
                //if the response was successful return on callback if user exists
                if (response.isSuccessful) {
                    //fetched list of users
                    val users = response.body()?.users
                    //get matching user in users list
                    val user = users?.find { it.userName == username }
                    if (user != null) {
                        //if user exists return true on callback
                        onResult(true)
                    } else {
                        //if user doesn't exist return false on callback
                        onResult(false)
                    }
                } else {
                    //return error message on callback
                    onError("A problem ocurred, please try again later")
                }
            }
            //if api request fails
            override fun onFailure(call: Call<AllUsersResponse>, t: Throwable) {
                //return error message on callback
                onError("A problem ocurred, please try again later")
            }
        })
    }

    /**
     * Method for posting a new user to the database
     *
     * @param user User - User to be posted
     * @param onResult Boolean - Callback on whether the user was posted or not
     */
    fun createUser(user: User, onResult: (Boolean) -> Unit) {
        val userJson = UserRequest(user)
        //make api call using retrofit (async)
        RetrofitClient.instance.createUser(userJson).enqueue(object : Callback<UserResponse> {
            //if request goes through
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    //if the response was successful return true on callback
                    onResult(true)
                } else {
                    //return false on callback otherwise
                    onResult(false)
                }
            }
            //if api request fails
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                //if there were any errors return false on callback
                onResult(false)
            }
        })
    }


    /**
     * Method for fetching all products from the database
     *
     * @param onResult List<Product> - Callback to handle successful response from API
     * @param onError String - Callback to handle errors
     */
    fun fetchAllProducts(onResult:(List<Product>) -> Unit, onError: (String) -> Unit) {
        //make api call using retrofit (async)
        RetrofitClient.instance.getAllProducts().enqueue(object : Callback<AllProductsResponse> {
            //if request goes through
            override fun onResponse(call: Call<AllProductsResponse>, response: Response<AllProductsResponse>) {
                if (response.isSuccessful) {
                    //get products list from api response
                    val products = response.body()?.products ?: emptyList()
                    //return on callback
                    onResult(products)
                } else {
                    //return error on callback
                    onError("Failed to fetch all products. Response code: ${response.code()}")
                }
            }
            //if api request fails
            override fun onFailure(call: Call<AllProductsResponse>, t: Throwable) {
                //return error on callback
                onError("ERROR FETCHING API FOR ALL PRODUCTS")
            }
        })
    }

    /**
     * Method for fetching a product by its barcode (functions as ID's in this apps context)
     *
     * @param code Long - Barcode to be used as filter in fetch
     * @param onResult Product - Callback to handle successful response from API
     * @param onNotFound String - Callback to handle not found
     * @param onError String - Callback to handle errors
     */
    //function to fetch product by barcode
    fun fetchProductByCode(code: Long, onResult: (Product) -> Unit, onNotFound: (String) -> Unit, onError: (String) -> Unit) {
        //make api call using retrofit (async)
        RetrofitClient.instance.getProductByCode(code).enqueue(object : Callback<CodeProductsResponse> {
            //if request goes through
            override fun onResponse(call: Call<CodeProductsResponse>, response: Response<CodeProductsResponse>) {
                if (response.isSuccessful) {
                    //get products list from api response
                    val products = response.body()?.products
                    //find the product that matches the barcode
                    val product = products?.find { it.productCode == code }
                    if (product != null) {
                        //return product on callback
                        onResult(product)
                    } else {
                        //pass message on callback for user not found
                        onNotFound("No product was found associated with code ${code}")
                    }
                } else {
                    //callback for errors
                    onError("Failed to fetch product. Response code: ${response.code()}")
                }
            }
            //if api request fails
            override fun onFailure(call: Call<CodeProductsResponse>, t: Throwable) {
                //callback for error
                onError("Error fetching product: ${t.message}")
            }
        })
    }

    /**
     * Method for posting a new product to the database
     *
     * @param newProduct Product - Product to be posted
     * @param onResult Boolean - Callback on whether the product was posted or not
     */
    fun createProduct(newProduct: Product, onResult: (Boolean) -> Unit) {
        val productJson = ProductRequest(newProduct)
        //make api call using retrofit (async)
        RetrofitClient.instance.createProduct(productJson).enqueue(object : Callback<ProductRequest> {
            //if request goes through
            override fun onResponse(call: Call<ProductRequest>, response: Response<ProductRequest>) {
                if (response.isSuccessful) {
                    //callback added product
                    onResult(true)
                } else {
                    //callback failed post
                    onResult(false)
                }
            }
            //if api request fails
            override fun onFailure(call: Call<ProductRequest>, t: Throwable) {
                //callback failed post
                onResult(false)
            }
        })
    }

}