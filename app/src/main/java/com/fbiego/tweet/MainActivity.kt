package com.fbiego.tweet

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fbiego.tweet.app.*
import com.fbiego.tweet.databinding.ActivityMainBinding
import com.github.angads25.toggle.widget.LabeledSwitch
import timber.log.Timber

class MainActivity : AppCompatActivity(), EventListener {


    private lateinit var binding: ActivityMainBinding

    private var dataList = ArrayList<TweetData>()
    private var testResult: TextView? = null
    private var testIcon: ImageView? = null
    private var testLoad: ProgressBar? = null
    private var testPass = false

    companion object{
        const val PREF_FOLLOWS = "pref_follows"
        const val PREF_RETWEETS = "pref_retweets"

        const val PREF_AUTO_LIKE = "pref_auto_like"
        const val PREF_AUTO_RETWEET = "pref_auto_retweet"
        const val PREF_AUTO_FOLLOW = "pref_auto_follow"

        const val PREF_REMOVE = "pref_remove"

        const val PREF_ACCEPTED = "pref_accepted_terms"
        const val PREF_INTRO_COMPLETE = "pref_intro_complete"
        const val PREF_RECEIVED_TWEET = "pref_received_tweet"
        const val PREF_TEST_OKAY = "pref_test_okay"

        const val PREF_CLICKED_NOTIFY = "pref_clicked_notify"


        const val NOTIFICATION_CHANNEL = "Test Notifications"
        const val NOTIFY_ID = 0xFB1E40

        lateinit var tweetAdapter : TweetAdapter
        lateinit var tweetRecycler : RecyclerView

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        notificationChannel(this)


        tweetAdapter = TweetAdapter(dataList)
        tweetRecycler = findViewById<View>(R.id.recycler_view) as RecyclerView
        tweetRecycler.layoutManager = LinearLayoutManager(this)
        tweetRecycler.isNestedScrollingEnabled = false
        tweetRecycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = tweetAdapter
        }
        tweetRecycler.itemAnimator?.changeDuration = 0

        binding.settingIcon.setOnClickListener {
            openSettings()
        }

        binding.silent.setOnClickListener {
            openSettings()
        }
        binding.about.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
            //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }


        binding.swipeLayout.setOnRefreshListener {
            updateData()

            Handler(Looper.getMainLooper()).postDelayed({
                binding.swipeLayout.isRefreshing = false
            }, 3000)

        }
    }

    private fun checkNotification(){
        notify(this, getString(R.string.test), getString(R.string.not_test))
    }

    override fun onResume() {
        super.onResume()

        updateData()
        appsList()
        EventReceiver.bindListener(this)

    }

    override fun onPause() {
        super.onPause()
        EventReceiver.unBindListener(this)
    }

    private fun openSettings(){

        val pref = PreferenceManager.getDefaultSharedPreferences(this)

        val enabled = NotificationManagerCompat.getEnabledListenerPackages(this).contains(
            BuildConfig.APPLICATION_ID
        )

        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.settings, null)

        val alertDialog: AlertDialog?


        val buttonText: TextView = layout.findViewById(R.id.button_text)
        val statusText: TextView = layout.findViewById(R.id.status_text)
        val buttonAction: LinearLayout = layout.findViewById(R.id.button_action)

        val retweet: LabeledSwitch = layout.findViewById(R.id.retweet_button)
        val like: LabeledSwitch = layout.findViewById(R.id.like_button)
        val follow: LabeledSwitch = layout.findViewById(R.id.follow_button)
        val clear: LabeledSwitch = layout.findViewById(R.id.clear_button)
        val testCheck: LinearLayout = layout.findViewById(R.id.button_test)
        testResult = layout.findViewById(R.id.test_text)
        testIcon = layout.findViewById(R.id.test_icon)
        testLoad = layout.findViewById(R.id.testing)


        retweet.isOn = pref.getBoolean(PREF_AUTO_RETWEET, true)
        like.isOn = pref.getBoolean(PREF_AUTO_LIKE, true)
        follow.isOn = pref.getBoolean(PREF_AUTO_FOLLOW, true)
        clear.isOn = pref.getBoolean(PREF_REMOVE, false)

        retweet.setOnToggledListener{_, b ->
            pref.edit().putBoolean(PREF_AUTO_RETWEET, b).apply()
        }
        like.setOnToggledListener{_, b ->
            pref.edit().putBoolean(PREF_AUTO_LIKE, b).apply()
        }
        follow.setOnToggledListener{_, b ->
            pref.edit().putBoolean(PREF_AUTO_FOLLOW, b).apply()
        }
        clear.setOnToggledListener { _, b ->
            pref.edit().putBoolean(PREF_REMOVE, b).apply()
        }
        statusText.text = if (enabled) getString(R.string.access_enabled) else getString(R.string.access_disabled)
        buttonText.text = if (enabled) getString(R.string.change) else getString(R.string.enable)

        buttonAction.setOnClickListener {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }
        val dialog = AlertDialog.Builder(this)


//        saveButton.setOnClickListener {
//            alertDialog!!.dismiss()
//        }

        testCheck.setOnClickListener {
            testPass = false
            testIcon!!.visibility = View.GONE
            testLoad!!.visibility = View.VISIBLE
            checkNotification()

            Handler(Looper.getMainLooper()).postDelayed({
                if (!testPass){
                    testResult!!.text = getString(R.string.test_fail)
                    testIcon!!.visibility = View.VISIBLE
                    testLoad!!.visibility = View.GONE
                }
                notificationChannel(this).cancel(NOTIFY_ID)
            }, 3000)
        }


        dialog.setView(layout)

        dialog.setOnDismissListener {
            Timber.e("Dialog dismissed")
        }


        alertDialog = dialog.create()

        alertDialog.window?.setBackgroundDrawableResource(R.drawable.popup_dialog)
        alertDialog.show()
    }

    private fun updateData(){
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val cur = pref.getInt(PREF_FOLLOWS, 0)
        val rts = pref.getInt(PREF_RETWEETS, 0)
        binding.follows.text = "$cur " + getString(R.string.follows)
        binding.retweets.text = "$rts " + getString(R.string.repost)
        dataList = DBHandler(this, null, null, 1).getLastRt()

        if (dataList.size < 10){
            dataList.add(TweetData(System.currentTimeMillis(), getString(R.string.complete_title), getString(R.string.complete_text)))
            dataList.add(TweetData(System.currentTimeMillis(), getString(R.string.rate_title), getString(R.string.rate_text)))
            dataList.add(TweetData(System.currentTimeMillis(), getString(R.string.post_title), getString(R.string.post_text)))
            dataList.add(TweetData(System.currentTimeMillis(), getString(R.string.optimization_title), getString(R.string.optimization_text)))
            dataList.add(TweetData(System.currentTimeMillis(), getString(R.string.next_title), getString(R.string.next_text)))
        }

        tweetAdapter.update(dataList)
    }


    private fun appsList(){

        val enabled = NotificationManagerCompat.getEnabledListenerPackages(this).contains(
            BuildConfig.APPLICATION_ID
        )

        if (enabled){
            binding.silent.visibility = View.GONE
        } else {
            binding.silent.visibility = View.VISIBLE
        }



    }

    fun notify(context: Context, title: String, text: String): Notification {

        val notBuild = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
        notBuild.setSmallIcon(R.drawable.ic_about)
        notBuild.color = ContextCompat.getColor(context, R.color.gradEnd)

        val intent = Intent(context, LaunchActivity::class.java)

        val pendingIntent = pendingIntent(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        notBuild.setContentIntent(pendingIntent)
        notBuild.setContentTitle(title)
        notBuild.setContentText(text)

        notBuild.priority = NotificationCompat.PRIORITY_DEFAULT
        notBuild.setShowWhen(true)
        notBuild.setOnlyAlertOnce(true)

        val notification= notBuild.build()
        notificationChannel(context).notify(NOTIFY_ID, notification)
        return notification
    }

    fun cancelNotification(notifyId: Int, context: Context) {
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(notifyId)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun notificationChannel(context: Context): NotificationManager {
        val notificationMgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL, BuildConfig.APPLICATION_ID, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.description = "Retweet test"
            notificationChannel.lightColor = ContextCompat.getColor(context, R.color.gradEnd)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationMgr.createNotificationChannel(notificationChannel)
        }
        return notificationMgr

    }

    override fun onTest() {
        testPass = true
        if (testResult != null) {
            testResult!!.text = getString(R.string.test_passed)
        }
        if (testIcon != null) {
            testIcon!!.visibility = View.VISIBLE
        }
        if (testLoad != null) {
            testLoad!!.visibility = View.GONE
        }
        cancelNotification(NOTIFY_ID, this)

    }

    private fun pendingIntent(context: Context, requestCode: Int, intent: Intent, flags: Int): PendingIntent {
        return PendingIntent.getActivity(context, requestCode, intent, flags or PendingIntent.FLAG_IMMUTABLE)
    }

}