package pt.ipt.dam.powerpantry.ui.home


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pt.ipt.dam.powerpantry.R
import pt.ipt.dam.powerpantry.api.RetrofitClient
import pt.ipt.dam.powerpantry.data.UsersResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Now that the view is created, call the API
        fetchUsers()
    }

    private fun fetchUsers() {
        // Make the API call using Retrofit
        RetrofitClient.instance.getRows().enqueue(object : Callback<UsersResponse> {
            override fun onResponse(call: Call<UsersResponse>, response: Response<UsersResponse>) {
                if (response.isSuccessful) {
                    // Log the users on successful response
                    val users = response.body()?.users ?: emptyList()
                    Log.d("API_RESPONSE", "Users fetched successfully: $users")
                } else {
                    // Log an error if the response is not successful
                    Log.e("API_ERROR", "Failed to fetch data. Response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UsersResponse>, t: Throwable) {
                // Log the error if the network request fails
                Log.e("API_ERROR", "Error fetching data: ${t.message}")
            }
        })
    }

}