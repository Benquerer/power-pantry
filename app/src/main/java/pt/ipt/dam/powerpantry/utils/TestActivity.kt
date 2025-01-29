package pt.ipt.dam.powerpantry.utils

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam.powerpantry.R
import pt.ipt.dam.powerpantry.api.RetrofitClient
import pt.ipt.dam.powerpantry.data.UsersResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        // Fetch users from the API
        fetchUsers()
    }

    private fun fetchUsers() {
        // Make the API call using Retrofit
        RetrofitClient.instance.getRows().enqueue(object : Callback<UsersResponse> {
            override fun onResponse(call: Call<UsersResponse>, response: Response<UsersResponse>) {
                // This is the successful response from the API
                if (response.isSuccessful) {
                    // Log the successful response data
                    val users = response.body()?.users ?: emptyList()
                    Log.d("API_RESPONSE", "Users fetched successfully: $users")
                } else {
                    // Handle failure in case the response code is not 200
                    Log.e("API_ERROR", "Failed to fetch data. Response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UsersResponse>, t: Throwable) {
                // This is the error case (e.g., no internet connection, wrong endpoint, etc.)
                Log.e("API_ERROR", "Error fetching data: ${t.message}")
            }
        })
    }
}
