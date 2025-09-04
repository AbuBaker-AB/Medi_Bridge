package com.aas.medi_bridge.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.aas.medi_bridge.Domain.DoctorsModel
import com.aas.medi_bridge.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: DoctorsModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getbundle()
    }

    private fun getbundle() {
        item = intent.getParcelableExtra("Object") ?: return

        binding.apply {
            titleTxt.text = item.name
            specialTxt.text = item.specialization
            patiensTxt.text = item.patients
            bioTxt.text = item.bio
            addressTxt.text = item.address
            experiensTxt.text = "${item.experience} years"
            ratingTxt.text = item.rating.toString()

            backBtn.setOnClickListener { finish() }

            websiteBtn.setOnClickListener {
                if (item.Site.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, item.Site.toUri())
                    startActivity(intent)
                }
            }

            messageBtn.setOnClickListener {
                if (item.Mobile.isNotEmpty()) {
                    val uri = "smsto:${item.Mobile}".toUri()
                    val intent = Intent(Intent.ACTION_SENDTO, uri)
                    intent.putExtra("sms_body", "Hello, I would like to schedule an appointment")
                    startActivity(intent)
                }
            }

            callBtn.setOnClickListener {
                if (item.Mobile.isNotEmpty()) {
                    val uri = "tel:${item.Mobile.trim()}".toUri()
                    val intent = Intent(Intent.ACTION_DIAL, uri)
                    startActivity(intent)
                }
            }

            directionBtn.setOnClickListener {
                if (item.location.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, item.location.toUri())
                    startActivity(intent)
                }
            }

            shareBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, item.name)
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    "${item.name} - ${item.address} - ${item.Mobile}"
                )
                startActivity(Intent.createChooser(intent, "Share via"))
            }

            Glide.with(this@DetailActivity)
                .load(item.image)
                .into(img)
        }
    }
}