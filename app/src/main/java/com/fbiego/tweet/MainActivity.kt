package com.fbiego.tweet

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fbiego.tweet.app.DBHandler
import com.fbiego.tweet.app.TweetAdapter
import com.fbiego.tweet.app.TweetData
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private var dataList = ArrayList<TweetData>()
    companion object{
        const val PREF_FOLLOWS = "pref_follows"
        const val PREF_RETWEETS = "pref_retweets"

        lateinit var tweetAdapter : TweetAdapter
        lateinit var tweetRecycler : RecyclerView
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        tweetAdapter = TweetAdapter(dataList)
        tweetRecycler = findViewById<View>(R.id.recycler_view) as RecyclerView
        tweetRecycler.layoutManager = LinearLayoutManager(this)
        tweetRecycler.isNestedScrollingEnabled = false
        tweetRecycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = tweetAdapter
        }
        tweetRecycler.itemAnimator?.changeDuration = 0
    }

    override fun onResume() {
        super.onResume()

        updateData()
        appsList()

    }

    private fun updateData(){
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val cur = pref.getInt(PREF_FOLLOWS, 0)
        val rts = pref.getInt(PREF_RETWEETS, 0)
        follows.text = "$cur Follows"
        retweets.text = "$rts Retweets"
        dataList = DBHandler(this, null, null, 1).getLastRt()
        tweetAdapter.update(dataList)
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