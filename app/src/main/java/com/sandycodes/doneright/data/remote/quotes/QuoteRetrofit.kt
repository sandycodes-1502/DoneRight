package com.sandycodes.doneright.data.remote.quotes

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object QuoteRetrofit {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://quoterism.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: QuoteApi by lazy {
        retrofit.create(QuoteApi::class.java)
    }

}