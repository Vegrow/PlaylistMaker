package com.example.playlistmaker

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setUpViews()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setUpViews() {
        setUpToolbar()
        setUpOptions()
    }

    private fun setUpToolbar() {
        setSupportActionBar(findViewById(R.id.topAppBar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpOptions() {
        val clickListener = View.OnClickListener { view -> onButtonClick(view) }
        findViewById<TextView>(R.id.text_view_share_app).setOnClickListener(clickListener)
        findViewById<TextView>(R.id.text_view_write_to_support).setOnClickListener(clickListener)
        findViewById<TextView>(R.id.text_view_terms_of_use).setOnClickListener(clickListener)
    }

    private fun onButtonClick(view: View) {
        when (view.id) {
            R.id.text_view_share_app -> shareApp()
            R.id.text_view_write_to_support -> sendEmail()
            R.id.text_view_terms_of_use -> openTermsOfUse()
        }
    }

    private fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, getString(R.string.url_practicum_android_course))
        startActivity(Intent.createChooser(intent, null))
    }

    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
            .putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email_target)))
            .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_header))
            .putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_body))
        openInDefaultApp(intent)
    }

    private fun openTermsOfUse() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_of_use_url)))
        openInDefaultApp(intent)
    }

    private fun openInDefaultApp(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                getString(R.string.no_required_app),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}