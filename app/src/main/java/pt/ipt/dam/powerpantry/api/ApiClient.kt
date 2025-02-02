package pt.ipt.dam.powerpantry.api

import okhttp3.OkHttpClient
import pt.ipt.dam.powerpantry.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit object to facilitate handling API interactions
 */
object RetrofitClient {
    //base url of sheety api
    private const val BASE_URL = "https://api.sheety.co/933c1e54117bebf9492ad891fe3f0b73/powerPantryApi/"

    // instance of retrofit user
    val instance: Api by lazy {
        //build of the HTTP request
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    //auth with the API_KEY from local properties (buildconfig)
                    .header("Authorization", "Bearer ${BuildConfig.SHEETY_API_KEY}") // Accessing the API key from BuildConfig
                    .build()
                chain.proceed(request)
            }
            .build()

        //build retrofit instance with gson converter
        Retrofit.Builder()
            .baseUrl(BASE_URL) //base url
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }
}