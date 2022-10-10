package com.fbiego.tweet

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_about.*
import java.io.InputStream

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        aboutTitle.setOnClickListener {

            legalInfo.visibility = View.GONE
            aboutInfo.visibility = View.VISIBLE
        }

        val imageStream: InputStream = this.resources.openRawResource(R.raw.enable_access)
        val bitmap: Bitmap = BitmapFactory.decodeStream(imageStream)

        img_access.setImageBitmap(bitmap)

        val imageStream2: InputStream = this.resources.openRawResource(R.raw.not_available)
        val bitmap2: Bitmap = BitmapFactory.decodeStream(imageStream2)
        img_na.setImageBitmap(bitmap2)

        val imageStream3: InputStream = this.resources.openRawResource(R.raw.enable_twitter)
        val bitmap3: Bitmap = BitmapFactory.decodeStream(imageStream3)
        img_twitter.setImageBitmap(bitmap3)

        val imageStream4: InputStream = this.resources.openRawResource(R.raw.turn_on)
        val bitmap4: Bitmap = BitmapFactory.decodeStream(imageStream4)
        img_post.setImageBitmap(bitmap4)

        btn_terms.setOnClickListener {
            legalInfo.visibility = View.VISIBLE
            aboutInfo.visibility = View.GONE
            webView.loadUrl("file:///android_asset/terms.html")
            webView.setBackgroundColor(Color.TRANSPARENT)
        }

        btn_privacy.setOnClickListener {
            legalInfo.visibility = View.VISIBLE
            aboutInfo.visibility = View.GONE
            webView.loadUrl("file:///android_asset/policy.html")
            webView.setBackgroundColor(Color.TRANSPARENT)
        }

        btn_twitter.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://twitter.com/_retweets___")
            startActivity(i)
        }

        btn_github.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://github.com/fbiego/retweet")
            startActivity(i)
        }

    }

    override fun onStart() {
        super.onStart()
        starsAbout.onStart()
    }

    override fun onStop() {
        super.onStop()
        starsAbout.onStop()
    }
}