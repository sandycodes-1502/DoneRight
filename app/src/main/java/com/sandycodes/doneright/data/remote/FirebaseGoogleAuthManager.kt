package com.sandycodes.doneright.data.remote

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.sandycodes.doneright.R

object FirebaseGoogleAuthManager {

    suspend fun signIn(
        context: Context,
        onResult: (AuthResult) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(
                    context.getString(R.string.default_web_client_id)
                )
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)

            val googleCredential =
                GoogleIdTokenCredential.createFrom(result.credential.data)

            firebaseSignIn(
                googleCredential.idToken,
                onResult,
                onError
            )

        } catch (e: Exception) {
            onError(e)
        }
    }

    private fun firebaseSignIn(
        idToken: String,
        onResult: (AuthResult) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val user = auth.currentUser

        if (user != null && user.isAnonymous) {
            user.linkWithCredential(credential)
                .addOnSuccessListener {
                    onResult(AuthResult.LinkedAnonymous)
                }
                .addOnFailureListener { e ->
                    if (e is FirebaseAuthUserCollisionException) {
                        auth.signInWithCredential(credential)
                            .addOnSuccessListener {
                                onResult(AuthResult.SignedInExisting)
                            }
                            .addOnFailureListener { onError(it) }
                    } else {
                        onError(e)
                    }
                }

        } else {
            auth.signInWithCredential(credential)
                .addOnSuccessListener {
                    onResult(AuthResult.SignedInExisting)
                }
                .addOnFailureListener { onError(it) }
        }
    }

    sealed class AuthResult {
        object LinkedAnonymous : AuthResult()
        object SignedInExisting : AuthResult()
    }
}
