package pt.ipt.dam.powerpantry.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.sheety.co/fbdeb11d5ff39b33516d3da032f62ddf/powerPantryApi/folha1"

    val instance: SheetyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SheetyApiService::class.java)
    }
}
