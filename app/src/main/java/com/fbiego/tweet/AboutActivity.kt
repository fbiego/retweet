package com.fbiego.tweet

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.fbiego.tweet.databinding.ActivityAboutBinding
import com.fbiego.tweet.utils.WheelView
import com.fbiego.tweet.MainActivity as MA
import java.io.InputStream

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding



    companion object {
        val values = arrayListOf("en", "ru", "in", "vi", "pt", "el", "de", "es", "cs", "it", "pl", "fr", "zh", "sw", "hi", "ar", "ko", "ja", "uk", "dev")
        val items = arrayListOf("English", "Русский", "Indonesia", "Tiếng Việt", "Português", "Ελληνικά", "Deutsch", "Español", "čeština", "Italiano", "Polski", "Français", "中国人", "Swahili", "हिन्दी", "اَلْعَرَبِيَّةُ", "한국인", "日本語", "українська", "Debug")

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val setPref =  PreferenceManager.getDefaultSharedPreferences(this)

        binding.aboutTitle.setOnClickListener {

            binding.legalInfo.visibility = View.GONE
            binding.aboutInfo.visibility = View.VISIBLE
        }

        val imageStream: InputStream = this.resources.openRawResource(R.raw.enable_access)
        val bitmap: Bitmap = BitmapFactory.decodeStream(imageStream)

        binding.imgAccess.setImageBitmap(bitmap)

        val imageStream2: InputStream = this.resources.openRawResource(R.raw.not_available)
        val bitmap2: Bitmap = BitmapFactory.decodeStream(imageStream2)
        binding.imgNa.setImageBitmap(bitmap2)

        val imageStream3: InputStream = this.resources.openRawResource(R.raw.enable_twitter)
        val bitmap3: Bitmap = BitmapFactory.decodeStream(imageStream3)
        binding.imgTwitter.setImageBitmap(bitmap3)

        val imageStream4: InputStream = this.resources.openRawResource(R.raw.turn_on)
        val bitmap4: Bitmap = BitmapFactory.decodeStream(imageStream4)
        binding.imgPost.setImageBitmap(bitmap4)

        binding.btnTerms.setOnClickListener {
            binding.legalInfo.visibility = View.VISIBLE
            binding.aboutInfo.visibility = View.GONE
            binding.webView.loadUrl("file:///android_asset/terms.html")
            binding.webView.setBackgroundColor(Color.TRANSPARENT)
        }

        binding.btnPrivacy.setOnClickListener {
            binding.legalInfo.visibility = View.VISIBLE
            binding.aboutInfo.visibility = View.GONE
            binding.webView.loadUrl("file:///android_asset/policy.html")
            binding.webView.setBackgroundColor(Color.TRANSPARENT)
        }

        binding.btnTwitter.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://twitter.com/_retweets___")
            startActivity(i)
        }

        binding.btnGithub.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://github.com/fbiego/retweet")
            startActivity(i)
        }

        val ln = setPref.getString(MA.PREF_APP_LANG, "en").toString()

        if (values.contains(ln)){
            binding.appLanguageText.text = items[values.indexOf(ln)]
        }

        binding.appLanguage.setOnClickListener {

            val lang = setPref.getString(MA.PREF_APP_LANG, "en").toString()


            val layout = layoutInflater.inflate(R.layout.spinner_dialog, null)
            val wheelView = layout.findViewById<WheelView>(R.id.wheel_view)
            val save = layout.findViewById<LinearLayout>(R.id.saveButton)
            val titleText = layout.findViewById<TextView>(R.id.titleText)

            wheelView.setItems(items)
            if (values.contains(lang)){
                wheelView.setSelection(values.indexOf(lang))
            } else {
                wheelView.setSelection(0)
            }

            val builder = AlertDialog.Builder(this)
            var dialog : AlertDialog? = null

            titleText.text = "Language" //getString(R.string.language)

            save.setOnClickListener {

                setPref.edit().putString(MA.PREF_APP_LANG, values[wheelView.selectedIndex]).apply()
                binding.appLanguageText.text = items[wheelView.selectedIndex]

                dialog!!.dismiss()
                val intent = this.baseContext.packageManager.getLaunchIntentForPackage(this.packageName)!!
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)

                //setPref.edit().putString(MA.PREF_APP_LANG, "es").apply()

            }

            builder.setView(layout)

            dialog = builder.create()
            dialog.window?.setBackgroundDrawableResource(R.drawable.popup_dialog)
            dialog.show()


        }

    }

}