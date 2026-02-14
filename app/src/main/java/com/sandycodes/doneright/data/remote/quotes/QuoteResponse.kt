package com.sandycodes.doneright.data.remote.quotes

data class QuoteResponse(
    val id: String,
    val text: String,
    val author: Author
)

data class Author(
    val id: String,
    val name: String
)

data class Quote(
    val quote: String,
    val author: String
)