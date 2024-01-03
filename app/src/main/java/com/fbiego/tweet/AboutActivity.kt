package com.fbiego.tweet

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fbiego.tweet.databinding.ActivityAboutBinding
import java.io.InputStream

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    }

}