package com.fbiego.tweet

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun onResume() {
        super.onResume()
        appsList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun appsList(){

        val enabled = NotificationManagerCompat.getEnabledListenerPackages(this).contains(
            BuildConfig.APPLICATION_ID
        )
        Timber.d("Notification Listener Enabled $enabled")

        if (enabled) {

            //Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()

        } else {
            AlertDialog.Builder(this)
                .setTitle("Notification Access")
                .setMessage("Grant notification access?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes) { _: DialogInterface?, _: Int ->

                    startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))

                }
                .show()

        }


    }
}