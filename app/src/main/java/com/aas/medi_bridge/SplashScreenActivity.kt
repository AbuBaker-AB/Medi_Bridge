package com.aas.medi_bridge

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper

import androidx.appcompat.app.AppCompatActivity
import com.aas.medi_bridge.databinding.ActivitySplashScreenBinding
import com.google.android.material.animation.AnimationUtils


class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        var binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val animation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.blink_zoom)
        binding.splashImageView.startAnimation(animation)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        },3000) // 3000 milliseconds =

    }

}