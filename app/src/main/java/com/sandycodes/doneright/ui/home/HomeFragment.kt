package com.sandycodes.doneright.ui.home

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.sandycodes.doneright.R
import com.sandycodes.doneright.data.local.database.DoneRightDatabase
import com.sandycodes.doneright.data.remote.FirebaseAnonymousAuthManager
import com.sandycodes.doneright.data.remote.FirebaseGoogleAuthManager
import com.sandycodes.doneright.data.remote.FirebaseGoogleAuthManager.AuthResult.*
import com.sandycodes.doneright.data.repository.TaskRepository
import com.sandycodes.doneright.databinding.FragmentHomeBinding
import com.sandycodes.doneright.ui.addtask.AddEditTaskBottomSheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: TaskAdapter
    private var isLoading = true
    private var toolbarInitialized = false
    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!toolbarInitialized) {
            (requireActivity() as AppCompatActivity)
                .setSupportActionBar(binding.toolbar)
            (requireActivity() as AppCompatActivity).supportActionBar?.title = null
            val toggle = ActionBarDrawerToggle(
                requireActivity(),
                binding.drawerLayout,
                binding.toolbar,
                R.string.open_drawer,
                R.string.close_drawer
            )

            binding.drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            toolbarInitialized = true
        }

        updateAuthUi()
        updateDrawerHeader()

        val cm = requireContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val isOnline = cm.activeNetworkInfo?.isConnected == true

        if (!isOnline) {
            Toast.makeText(
                requireContext(),
                "Offline mode — changes will sync later",
                Toast.LENGTH_SHORT
            ).show()
        }

        val dao = DoneRightDatabase.getInstance(requireContext()).taskDao()
        val repository = TaskRepository(dao)
        viewModel = HomeViewModel(repository)

        adapter = TaskAdapter( onStatusClick = { task ->
            viewModel.updateTaskStatus(task)
            requireContext().vibrateShort()

        }, onItemClick = { task ->
            AddEditTaskBottomSheet(task).show(parentFragmentManager, "AddEditTaskBottomSheet")
            }
        )

        val recyclerView = binding.taskRecyclerView
        val emptyLayout = binding.emptyLayout
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            Log.d("TASK_LIST", "Received ${tasks.size} tasks")

            if (tasks.isEmpty() && !isLoading) {
                recyclerView.visibility = View.GONE
                emptyLayout.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyLayout.visibility = View.GONE
            }
            adapter.submitList(tasks)
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading = it
            binding.progressBar.visibility =
                if(it) View.VISIBLE else View.GONE
        }

        val itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.absoluteAdapterPosition
                    val task = adapter.getTaskAt(position)

                    viewModel.deleteTask(task)

                    Snackbar.make(
                        binding.root, "Task Deleted", Snackbar.LENGTH_LONG,
                    ).setAction("UNDO") {
                        viewModel.insertTask(task)
                    }.show()
                }
            }
        )

        itemTouchHelper.attachToRecyclerView(binding.taskRecyclerView)

        val drawerLayout = binding.drawerLayout
        val navigationView = binding.navigationmenu

        updateAuthUi()

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.signinbtn -> {
                    lifecycleScope.launch {
                        Toast.makeText(requireContext(), "Signing In...", Toast.LENGTH_SHORT).show()
                        FirebaseGoogleAuthManager.signIn(
                            requireContext(),
                            onResult = { result ->
                                when (result) {
                                    LinkedAnonymous -> {
                                        Toast.makeText(
                                            requireContext(),
                                            "Signed in with Google",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        updateDrawerHeader()
                                        repository.syncFromFirestore()
                                        updateAuthUi()
                                    }

                                    SignedInExisting -> {
                                        val user = FirebaseAuth.getInstance().currentUser
                                        Toast.makeText(
                                            requireContext(),
                                            "Welcome back ${user?.email?.substringBefore("@")}!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        lifecycleScope.launch {
                                            repository.clearLocalTasks()
                                            repository.syncFromFirestore()
                                            updateAuthUi()
                                            updateDrawerHeader()

                                            delay(350)
                                            Toast.makeText(
                                                requireContext(),
                                                "Tasks Synced",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        }
                                    }
                                }
                            },
                            onError = { error ->
                                Toast.makeText(requireContext(), "Sign in failed: ${error.message}", Toast.LENGTH_SHORT).show()
                                updateAuthUi()
                                updateDrawerHeader()
                            }
                        )
                    }
                }

                R.id.menu_sign_out -> {
                    FirebaseAuth.getInstance().signOut()
                    FirebaseAuth.getInstance()
                        .signInAnonymously()
                        .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Signed out", Toast.LENGTH_SHORT).show()
                            updateAuthUi()
                            updateDrawerHeader()
                            FirebaseAnonymousAuthManager.ensureSignedIn {
                                Log.d("AUTH_After_Signout", "Signed in as ${FirebaseAnonymousAuthManager.uid()}")
                                lifecycleScope.launch {
                                    repository.clearLocalTasks()
                                }
                            }
                        }
                }

                R.id.menu_help -> {
                    //help menu
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        val addtaskbtn = binding.addtask

        addtaskbtn.setOnClickListener {
            AddEditTaskBottomSheet().show(parentFragmentManager, "AddEditTaskBottomSheet")
        }
    }

    private fun updateAuthUi() {
        val user = FirebaseAuth.getInstance().currentUser
        val menu = binding.navigationmenu

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

    private fun updateDrawerHeader() {
        val user = FirebaseAuth.getInstance().currentUser
        Log.d("AUTH_Name", "Signed in as ${user?.displayName} with ${user?.email}")
        val headerView = binding.navigationmenu.getHeaderView(0)
        val tvGreeting = headerView.findViewById<TextView>(R.id.tvGreeting)
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

    fun Context.vibrateShort() {
        val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (vibrator.hasVibrator()) {
            val effect = VibrationEffect.createOneShot(
                50,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
            vibrator.vibrate(effect)
        }
    }

}
