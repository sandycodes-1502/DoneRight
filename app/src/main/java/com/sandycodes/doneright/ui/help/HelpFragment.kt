package com.sandycodes.doneright.ui.help

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.sandycodes.doneright.R

class HelpFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_help, container, false)

        val contactDev = view.findViewById<Button>(R.id.contactDev)

        contactDev.setOnClickListener {
            Log.d("AUTH", "Contact Clicked")
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:sandycodes.dev2205@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "DoneRight App Feedback")
            }
            startActivity(intent)
        }

        return view
    }

}