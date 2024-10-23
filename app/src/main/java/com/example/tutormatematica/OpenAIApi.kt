package com.example.tutormatematica

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class OpenAIRequest(val model: String, val prompt: String, val max_tokens: Int)
data class OpenAIResponse(val choices: List<Choice>)
data class Choice(val text: String)

interface OpenAIApi {
  @Headers("Content-Type: application/json")
  @POST("completions")
  fun generateText(@Body request: OpenAIRequest): Call<OpenAIResponse>
}
