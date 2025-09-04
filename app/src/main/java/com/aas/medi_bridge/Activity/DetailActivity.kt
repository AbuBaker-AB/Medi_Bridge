package com.aas.medi_bridge.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aas.medi_bridge.Domain.DoctorsModel
import com.aas.medi_bridge.R
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
        item = intent.getParcelableExtra("Object")!!

            binding.apply {
                titleTxt.text = item.name
                specialTxt.text = item.specialization
                patiensTxt.text = item.patients
                bioTxt.text = item.bio
                addressTxt.text = item.address
                experienceTxt.text = item.experience.toString()+" years"
                ratingTxt.text = "${item.rating}"
                backBtn.setOnClickListener { finish() }

                websiteBtn.setOnClickListener {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.setData(Uri.parse(item.Site))
                    startActivity(i)
                }

                messageBtn.setOnClickListener {
                    val uri = Uri.parse("smsto:${item.Mobile}")
                    val intent = Intent(Intent.ACTION_SENDTO, uri)
                    intent.putExtra("sms_body", "the SMS text")
                    startActivity(intent)
                }

                callBtn.setOnClickListener {
                    val uri = "tel:"+item.Mobile.trim()
                    val intent = Intent(Intent.ACTION_DIAL,
                        Uri.parse(uri))
                    startActivity(intent)
                }

                directionBtn.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW,
                        Uri.parse(item.location))
                    startActivity(intent)
                }

                shareBtn.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.setType("text/plain")
                    intent.putExtra(Intent.EXTRA_SUBJECT, item.name)
                    intent.putExtra(
                        Intent.EXTRA_TEXT,
                        item.name +" " + item.address + " " + item.Mobile
                    )
                    startActivity(Intent.createChooser(intent, "Share via"))
                }

                Glide.with(this@DetailActivity)
                    .load(item.image)
                    .into(img)
            }
    }
}