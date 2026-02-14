package com.sandycodes.doneright.ui.activity

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.sandycodes.doneright.R
import com.sandycodes.doneright.data.local.database.DoneRightDatabase
import com.sandycodes.doneright.data.local.notificationReminder.NotificationHelper
import com.sandycodes.doneright.data.remote.FirebaseAnonymousAuthManager
import com.sandycodes.doneright.data.remote.FirebaseGoogleAuthManager
import com.sandycodes.doneright.data.remote.FirebaseGoogleAuthManager.AuthResult.LinkedAnonymous
import com.sandycodes.doneright.data.remote.FirebaseGoogleAuthManager.AuthResult.SignedInExisting
import com.sandycodes.doneright.data.repository.TaskRepository
import com.sandycodes.doneright.databinding.ActivityMainBinding
import com.sandycodes.doneright.ui.help.HelpFragment
import com.sandycodes.doneright.ui.home.HomeFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var toolbarInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NotificationHelper.createChannel(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )

                requestPermissions(
                    arrayOf(android.Manifest.permission.INTERNET),
                    1002
                )
            }
        }

        FirebaseAnonymousAuthManager.ensureSignedIn {
            Log.d("AUTH", "Signed in as ${FirebaseAnonymousAuthManager.uid()}")
        }

        window.statusBarColor = getColor(R.color.bg_dark)

        logAuthState("AUTH_ON_START")

        val dao = DoneRightDatabase.getInstance(this).taskDao()
        val repository = TaskRepository(this, dao)
        val menu = binding.navigationmenu
        updateAuthUi(menu)
        updateDrawerHeader(menu)

        if (FirebaseAuth.getInstance().currentUser != null &&
            !FirebaseAuth.getInstance().currentUser!!.isAnonymous
        ) {
            repository.syncFromFirestore()
        }

        val titleTextView = TextView(this).apply {
            text = "DONERIGHT"
            textSize = 25f
            gravity = Gravity.CENTER
            setTextColor(resources.getColor(R.color.white, theme))
            typeface = ResourcesCompat.getFont(this@MainActivity, R.font.afacad_bold)
        }

        if (!toolbarInitialized) {
            (this)
                .setSupportActionBar(binding.toolbar)

            (this)
                .supportActionBar?.title = null

            binding.toolbar.addView(titleTextView)

            val toggle = ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.toolbar,
                R.string.open_drawer,
                R.string.close_drawer
            )

            binding.drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            toolbarInitialized = true
        }

        updateAuthUi(menu)
        updateDrawerHeader(menu)

        val drawerLayout = binding.drawerLayout
        val navigationView = binding.navigationmenu

        val homeFragment = HomeFragment()
        val helpFragment = HelpFragment()

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.signinbtn -> {
                    lifecycleScope.launch {
                        Toast.makeText(this@MainActivity, "Signing In...", Toast.LENGTH_SHORT)
                            .show()
                        FirebaseGoogleAuthManager.signIn(
                            this@MainActivity,
                            onResult = { result ->
                                when (result) {
                                    LinkedAnonymous -> {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Signed in with Google",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        updateDrawerHeader(menu)
                                        repository.syncFromFirestore()
                                        updateAuthUi(menu)
                                    }

                                    SignedInExisting -> {
                                        val user = FirebaseAuth.getInstance().currentUser
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Welcome back ${user?.email?.substringBefore("@")}!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        lifecycleScope.launch {
                                            repository.clearLocalTasks()
                                            repository.syncFromFirestore()
                                            updateAuthUi(menu)
                                            updateDrawerHeader(menu)

                                            delay(350)
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Tasks Synced",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        }
                                    }
                                }
                            },
                            onError = { error ->
                                Toast.makeText(
                                    this@MainActivity,
                                    "Sign in failed: ${error.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                updateAuthUi(menu)
                                updateDrawerHeader(menu)
                            }
                        )
                    }
                }

                R.id.menu_sign_out -> {
                    FirebaseAuth.getInstance().signOut()
                    FirebaseAuth.getInstance()
                        .signInAnonymously()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()
                            updateAuthUi(menu)
                            updateDrawerHeader(menu)
                            FirebaseAnonymousAuthManager.ensureSignedIn {
                                Log.d(
                                    "AUTH_After_SignOut",
                                    "Signed in as ${FirebaseAnonymousAuthManager.uid()}"
                                )
                                lifecycleScope.launch {
                                    repository.clearLocalTasks()
                                }
                            }
                        }
                }

                R.id.menu_help -> {
                    //help menu
                    supportFragmentManager.beginTransaction()
                        .replace(binding.navHostFragment.id, helpFragment)
                        .addToBackStack(null)
                        .commit()
                }

                R.id.menu_home -> {
                    //homepage
                    supportFragmentManager.beginTransaction()
                        .replace(binding.navHostFragment.id, homeFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

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

     fun updateAuthUi(menu: NavigationView) {
        val user = FirebaseAuth.getInstance().currentUser
        val menu = menu

        menu.menu.clear()
        menu.inflateMenu(R.menu.drawer_menu)
        val header = menu.getHeaderView(0)
        val userWithoutAuth = header.findViewById<TextView>(R.id.tvuserWithoutAuth)

        val isSignedIn = user != null && !user.isAnonymous
        Log.d("AUTH", "UpdateAuthUI called with anonymous = ${user?.isAnonymous}")
        menu.menu.findItem(R.id.signinbtn).isVisible = !isSignedIn
        menu.menu.findItem(R.id.menu_sign_out).isVisible = isSignedIn
        if (user != null && user.isAnonymous) {
            userWithoutAuth.visibility = View.VISIBLE
        } else {
            userWithoutAuth.visibility = View.GONE
        }
    }

    fun updateDrawerHeader(menu: NavigationView) {
        val user = FirebaseAuth.getInstance().currentUser
        Log.d("AUTH_Name", "Signed in as ${user?.displayName} with ${user?.email}")
        val headerView = menu.getHeaderView(0)
        val tvGreeting = headerView.findViewById<TextView>(R.id.tvGreeting)
        user?.reload()

        if (user != null && !user.isAnonymous) {
            var name: String?
            if (user.displayName == " " || user.displayName =="" || user.displayName == null){
                name = user.email!!.substringBefore("@")
            } else {
                name = user.displayName
            }
            tvGreeting.text = "Hi, $name 👋"
        } else {
            tvGreeting.text = "Hi there 👋"
        }
    }
