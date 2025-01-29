package pt.ipt.dam.powerpantry

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

                    // Show a Toast with the result
                    Toast.makeText(this@TestActivity, "Fetched ${users.size} users", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle failure in case the response code is not 200
                    Log.e("API_ERROR", "Failed to fetch data. Response code: ${response.code()}")
                    Toast.makeText(this@TestActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UsersResponse>, t: Throwable) {
                // This is the error case (e.g., no internet connection, wrong endpoint, etc.)
                Log.e("API_ERROR", "Error fetching data: ${t.message}")
                Toast.makeText(this@TestActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
