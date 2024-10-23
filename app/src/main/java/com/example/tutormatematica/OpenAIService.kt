package com.example.tutormatematica

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OpenAIService(private val apiKey: String) {

  private val api = RetrofitClient.instance.create(OpenAIApi::class.java)

  fun generateText(prompt: String, onResult: (String) -> Unit) {
    val request = OpenAIRequest("text-davinci-003", prompt, 50)
    val call = api.generateText(request)

    call.enqueue(object : Callback<OpenAIResponse> {
      override fun onResponse(call: Call<OpenAIResponse>, response: Response<OpenAIResponse>) {
        if (response.isSuccessful) {
          val text = response.body()?.choices?.get(0)?.text?.trim() ?: "No response"
          onResult(text)
        } else {
          Log.e("OpenAI", "API Error: ${response.code()} - ${response.message()}")
          onResult("Error in API response: ${response.code()} - ${response.message()}")
        }
      }

      override fun onFailure(call: Call<OpenAIResponse>, t: Throwable) {
        Log.e("OpenAI", "Request failed: ${t.message}", t)
        onResult("Request failed: ${t.message}")
      }

    })
  }
}
