package com.example.tutormatematica

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
  private const val BASE_URL = "https://api.openai.com/v1/"
  // Valor de la API
  private const val API_KEY = ""

  // Cliente HTTP que añade el encabezado de autorización
  private val client = OkHttpClient.Builder()
    .addInterceptor(Interceptor { chain ->
      val original: Request = chain.request()
      val requestBuilder: Request.Builder = original.newBuilder()
        .header("Authorization", "Bearer $API_KEY") // Encabezado con Bearer token
      val request: Request = requestBuilder.build()
      chain.proceed(request)
    })
    .build()

  // Retrofit instance que utiliza el cliente HTTP modificado
  val instance: Retrofit by lazy {
    Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(client) // Añade el cliente con el interceptor
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }
}
