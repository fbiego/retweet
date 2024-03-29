package com.fbiego.tweet

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import com.fbiego.tweet.databinding.ActivityLaunchBinding
import com.fbiego.tweet.utils.LocaleHelper
import com.fbiego.tweet.MainActivity as MA
import timber.log.Timber
import java.util.Locale

class LaunchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaunchBinding
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        if (pref.getBoolean(MA.PREF_INTRO_COMPLETE, false)){
            startActivity(Intent(this, MA::class.java))
            finish()
        } else if (pref.getBoolean(MA.PREF_ACCEPTED, false)){
            startActivity(Intent(this, SetupActivity::class.java))
            finish()
        }

        binding.termsText.setOnClickListener {
            textDialog(getString(R.string.terms), R.raw.terms)
        }

        binding.policyText.setOnClickListener {
            textDialog(getString(R.string.policy), R.raw.policy)
        }


        binding.okayButton.setOnClickListener {

            if (binding.acceptCheck.isChecked) {
                pref.edit().putBoolean(MA.PREF_ACCEPTED, true).apply()
                startActivity(Intent(this, SetupActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, getString(R.string.agree_alert), Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun attachBaseContext(newBase: Context?) {
        pref = PreferenceManager.getDefaultSharedPreferences(newBase!!)

        val phoneLang = Locale.getDefault().language // check phone default language

        val lPref = pref.getString(MA.PREF_APP_LANG, "null")
            .toString() // check if there is preferred language set

        val lang = if (AboutActivity.values.contains(phoneLang) && lPref == "null") {
            // phone language translation available and preferred not set
            pref.edit().putString(MA.PREF_APP_LANG, phoneLang).apply()
            phoneLang
        } else if (lPref != "null") {
            // preferred was already set
            lPref
        } else {
            // default language
            "en"
        }

        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
        Timber.e("attach base context, language: $lang")
    }


    private fun textDialog(title: String, resource: Int){
        val builder = AlertDialog.Builder(this)
        var dialog : AlertDialog? = null
//        builder.setTitle(title)
        val inflater = layoutInflater
        val dialogInflater = inflater.inflate(R.layout.text_dialog, null)
        val textView = dialogInflater.findViewById<TextView>(R.id.textView)
        val okayAction = dialogInflater.findViewById<LinearLayout>(R.id.okayAction)


        val rsc = this.resources.openRawResource(resource)
        val buffer = ByteArray(rsc.available())
        while (rsc.read(buffer) != -1);
        val text = String(buffer)
        Timber.w(text)
        textView.text = Html.fromHtml(text)
        textView.movementMethod = LinkMovementMethod.getInstance()

        builder.setView(dialogInflater)

        okayAction.setOnClickListener {
            dialog!!.dismiss()
        }

        dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.popup_dialog)
//        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.show()
    }
}