package com.sandycodes.doneright.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.sandycodes.doneright.data.local.database.DoneRightDatabase
import com.sandycodes.doneright.data.remote.FirebaseAnonymousAuthManager
import com.sandycodes.doneright.data.remote.FirebaseGoogleAuthManager
import com.sandycodes.doneright.data.repository.TaskRepository
import com.sandycodes.doneright.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseAnonymousAuthManager.ensureSignedIn {
            Log.d("AUTH", "Signed in as ${FirebaseAnonymousAuthManager.uid()}")
        }

        logAuthState("AUTH_ON_START")

        val dao = DoneRightDatabase.getInstance(this).taskDao()
        val repository = TaskRepository(dao)

        if (FirebaseAuth.getInstance().currentUser != null &&
            !FirebaseAuth.getInstance().currentUser!!.isAnonymous
        ) {
            repository.syncFromFirestore()
        }

    }
    private fun logAuthState(tag: String = "AUTH_STATE") {
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            Log.d(tag, "User = null")
            return
        }

        Log.d(tag, "UID = ${user.uid}")
        Log.d(tag, "isAnonymous = ${user.isAnonymous}")

        user.providerData.forEach {
            Log.d(tag, "Provider: ${it.providerId}")
        }
    }

}