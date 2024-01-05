package com.fbiego.tweet

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fbiego.tweet.app.ActionAdapter
import com.fbiego.tweet.app.ActionData
import com.fbiego.tweet.app.EventListener
import com.fbiego.tweet.app.EventReceiver
import com.fbiego.tweet.app.TweetData
import com.fbiego.tweet.databinding.ActivitySetupBinding

class SetupActivity : AppCompatActivity(), EventListener {

    private lateinit var binding: ActivitySetupBinding

    private val notification = arrayListOf<ActionData>()
    private val actionAdapter = ActionAdapter(notification, this@SetupActivity::actionButton)

    private var setup = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.recyclerAction.layoutManager = LinearLayoutManager(this)
        binding.recyclerAction.isNestedScrollingEnabled = false
        binding.recyclerAction.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        binding.recyclerAction.apply {
            layoutManager =
                LinearLayoutManager(this@SetupActivity)
            adapter = actionAdapter
        }

        binding.okayButton.setOnClickListener {

            if (setup){
                val pref = PreferenceManager.getDefaultSharedPreferences(this)
                pref.edit().putBoolean(MainActivity.PREF_INTRO_COMPLETE, true).apply()

                startActivity(Intent(this, MainActivity::class.java))
                finish()

            } else {
                setupActions(false)
                actionAdapter.update(notification)
                binding.okayButton.visibility = View.GONE
            }

        }
        
        
    }

    private fun notificationActions(click: Boolean){
        notification.clear()
        notification.add(ActionData(3001, getString(R.string.read_notifications), if (readEnabled()) getString(R.string.enabled) else getString(R.string.enable), 0, true, readEnabled()))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notification.add(ActionData(3002, getString(R.string.post_title), if (postEnabled()) getString(R.string.enabled) else getString(R.string.enable), 0, true, postEnabled()))
        }
        if (readEnabled() && postEnabled()){
            notification.add(ActionData(3003, getString(R.string.run_test), getString(R.string.run), 0,
                actionable = true,
                complete = false
            ))
        }

        if (click && !readEnabled()){
            notification.add(ActionData(3004, getString(R.string.feature_unavailable), getString(R.string.help), 0,
                actionable = true,
                complete = false
            ))
        }

    }

    private fun setupActions(complete: Boolean){

        binding.appIcon.setImageResource(R.drawable.ic_setup)
        binding.welcomeText.text = getString(R.string.twitter_setup_title)
        binding.taglineText.text = getString(R.string.twitter_setup)


        notification.clear()
        notification.add(ActionData(4003, getString(R.string.twitter_required), if (!twitterInstalled()) getString(R.string.install) else getString(R.string.installed), 0, true, twitterInstalled()))
        notification.add(
            ActionData(
                4004,
                getString(R.string.enable_twitter_check),
                getString(R.string.enable),
                0,
                actionable = false,
                complete = false
            )
        )
        notification.add(
            ActionData(
                4005,
                getString(R.string.turn_on),
                "@_retweets__",
                0,
                actionable = true,
                complete = false
            )
        )
        if (complete){
            notification.add(
                ActionData(
                    4007,
                    getString(R.string.detect_notification),
                    getString(R.string.complete),
                    0,
                    actionable = false,
                    complete = true
                )
            )
        } else {
            notification.add(
                ActionData(
                    4006,
                    getString(R.string.setup_alerts),
                    getString(R.string.install),
                    0,
                    actionable = false,
                    complete = false
                )
            )
            notification.add(
                ActionData(
                    4007,
                    getString(R.string.setup_detect),
                    getString(R.string.install),
                    0,
                    actionable = false,
                    complete = false
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()

        EventReceiver.bindListener(this)

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        setup = pref.getBoolean(MainActivity.PREF_TEST_OKAY, false)
        val click = pref.getBoolean(MainActivity.PREF_CLICKED_NOTIFY, false)

        if (!setup) {
            notificationActions(click)
        } else {
            val complete = pref.getBoolean(MainActivity.PREF_RECEIVED_TWEET, false)
            if (complete){
                binding.okayButton.visibility = View.VISIBLE
            }
            setupActions(complete)
        }
        actionAdapter.update(notification)



    }

    override fun onPause() {
        super.onPause()

        EventReceiver.unBindListener(this)
        if (twitterInstalled() && setup){
            finish()
        }

    }

    private fun actionButton(action: ActionData){
        when(action.id){
            3001 -> {
                val pref = PreferenceManager.getDefaultSharedPreferences(this)
                pref.edit().putBoolean(MainActivity.PREF_CLICKED_NOTIFY, true).apply()
                startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))

            }
            3002 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        3102
                    )
                }
            }
            3003 -> {
                MainActivity().notify(this, getString(R.string.setup), getString(R.string.not_test))
            }
            3004 -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://android.stackexchange.com/questions/202234/how-to-fix-notification-access-is-not-available-on-android-go")
                startActivity(i)
            }
            4003 -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://play.google.com/store/apps/details?id=com.twitter.android")
                startActivity(i)
            }
            4005 -> {
                if (action.longClick){
//                    Toast.makeText(this, "Bypass notification detection", Toast.LENGTH_SHORT ).show()
//                    val pref = PreferenceManager.getDefaultSharedPreferences(this)
//                    pref.edit().putBoolean(MainActivity.PREF_RECEIVED_TWEET, true).apply()
//                    binding.okayButton.visibility = View.VISIBLE
                } else {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse("https://twitter.com/_retweets___")
                    startActivity(i)
                }
            }
        }
//        Toast.makeText(this, action.text, Toast.LENGTH_SHORT ).show()
    }

    private fun readEnabled(): Boolean{
        return NotificationManagerCompat.getEnabledListenerPackages(this).contains(
            BuildConfig.APPLICATION_ID
        )
    }

    private fun twitterInstalled(): Boolean{

        return isPackageInstalled("com.twitter.android", this.packageManager)
    }

    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun postEnabled(): Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
                    )
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onTest() {

//        Toast.makeText(this, "Notification test okay", Toast.LENGTH_SHORT).show()
        notificationActions(false)

        val f: ActionData? = notification.firstOrNull { it.id == 3003 }

        if (f != null){
            notification.remove(f)
        }
        notification.add(ActionData(3003, "Run notification test", getString(R.string.complete), 0,
            actionable = true,
            complete = true
        ))

        actionAdapter.update(notification)

        binding.okayButton.visibility = View.VISIBLE

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        pref.edit().putBoolean(MainActivity.PREF_TEST_OKAY, true).apply()
        MainActivity().cancelNotification(MainActivity.NOTIFY_ID, this)

    }

    override fun onRetweet(tweetData: TweetData) {
        super.onRetweet(tweetData)

        val f: ActionData? = notification.firstOrNull { it.id == 4007 }
        val e: ActionData? = notification.firstOrNull { it.id == 4006 }

        if (f != null){
            notification.remove(f)
        }
        if (e != null){
            notification.remove(e)
        }
        notification.add(ActionData(
            4007,
            getString(R.string.detect_notification),
            getString(R.string.complete),
            0,
            actionable = false,
            complete = true
        ))
    }
}