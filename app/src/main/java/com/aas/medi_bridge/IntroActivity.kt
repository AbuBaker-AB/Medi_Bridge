package com.aas.medi_bridge

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.aas.medi_bridge.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {
    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {
            buttonCloud.setOnClickListener {
                startActivity(Intent(this@IntroActivity, MainActivity::class.java))
            }
        }

    }
}