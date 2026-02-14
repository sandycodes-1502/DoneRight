package com.sandycodes.doneright.data.remote.quotes

import retrofit2.http.GET

interface QuoteApi {

    @GET("/api/quotes/random")
    suspend fun getRandomQuote(): QuoteResponse

}

