package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpView()
    }

    private fun setUpView() {
        val searchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(view: View) {
                onButtonClick(view)
            }
        }
        val otherClickListener = View.OnClickListener { view -> onButtonClick(view) }
        findViewById<Button>(R.id.button_search).setOnClickListener(searchClickListener)
        findViewById<Button>(R.id.button_library).setOnClickListener(otherClickListener)
        findViewById<Button>(R.id.button_settings).setOnClickListener(otherClickListener)
    }

    private fun onButtonClick(view: View) {
        getActivity(view)?.let { activity ->
            startActivity(Intent(this, activity))
        }
    }

    private fun getActivity(view: View) = when (view.id) {
        R.id.button_search -> SearchActivity::class.java
        R.id.button_library -> MediaLibraryActivity::class.java
        R.id.button_settings -> SettingsActivity::class.java
        else -> null
    }

}
