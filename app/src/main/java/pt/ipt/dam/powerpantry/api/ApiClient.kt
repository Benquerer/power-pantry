package pt.ipt.dam.powerpantry.api

import okhttp3.OkHttpClient
import pt.ipt.dam.powerpantry.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.sheety.co/933c1e54117bebf9492ad891fe3f0b73/powerPantryApi/"


    val instance: Api by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Authorization", "Bearer ${BuildConfig.SHEETY_API_KEY}") // Accessing the API key from BuildConfig
                    .build()
                chain.proceed(request)
            }
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }
}