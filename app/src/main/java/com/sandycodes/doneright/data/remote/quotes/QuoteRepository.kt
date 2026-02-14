package com.sandycodes.doneright.data.remote.quotes

class QuoteRepository {

    suspend fun fetchQuote(): Quote {

        return try {
            val response = QuoteRetrofit.api.getRandomQuote()
            Quote(response.text, response.author.name)
        } catch (e: Exception) {
            getFallbackQuote()
        }
    }

    private fun getFallbackQuote(): Quote {
        val localQuotes = listOf(
            Quote("It is never too late to be what you might have been.", "George Eliot"),
            Quote("The only way to do great work is to love what you do.", "Steve Jobs"),
            Quote("Success is not final, failure is not fatal: It is the courage to continue that counts.", "Winston Churchill"),
            Quote("We are all in the gutter, but some of us are looking at the stars.", "Oscar Wilde"),
            Quote("The best way to predict the future is to create it.", "Peter Drucker"),
            Quote("The only limit to our realization of tomorrow will be our doubts of today.", "Franklin D. Roosevelt"),
            Quote("Believe in yourself. You are braver than you think, more talented than you know, and capable of more than you imagine.", "Roy T. Benett"),
            Quote("The only thing we have to fear is fear itself.", "Franklin D. Roosevelt" ),
            Quote("Do one thing every day that scares you", "Eleanor Roosevelt"),
            Quote("Success is not in what you have, but who you are.", "Bo Bennett"),
            Quote("When you want something, all universe conspires in helping you to achieve it.", "Paulo Coelho"),
            Quote("We are all fools in love", "Jane Austen")
        )
        return localQuotes.random()
    }
}