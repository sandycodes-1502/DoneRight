package com.sandycodes.doneright.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth

object FirebaseAnonymousAuthManager {

    private val auth = FirebaseAuth.getInstance()

    fun ensureSignedIn(onComplete: () -> Unit) {
        if (auth.currentUser != null) {
            onComplete()
        } else {
            auth.signInAnonymously()
                .addOnSuccessListener { onComplete() }
                .addOnFailureListener {
                    Log.e("AUTH", "Anonymous auth failed", it)
                }
        }
    }

    fun uid(): String? = auth.currentUser?.uid
}
