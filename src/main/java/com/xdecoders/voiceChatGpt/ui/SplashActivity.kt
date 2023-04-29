package com.xdecoders.voiceChatGpt.ui

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.xdecoders.voiceChatGpt.databinding.ActivitySplashBinding
import com.xdecoders.voiceChatGpt.util.postDelayed

class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        postDelayed(2000){
           val i =  Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(i)
            finish()
        }
    }

}