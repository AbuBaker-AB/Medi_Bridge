package com.aas.medi_bridge.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.aas.medi_bridge.Domain.DoctorsModel
import com.aas.medi_bridge.R
import com.aas.medi_bridge.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide

class DetailActivity : BaseActivity() {
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

            // Add debug logging to verify button initialization
            android.util.Log.d("DetailActivity", "Setting up click listeners")
            android.util.Log.d("DetailActivity", "Doctor data - Site: '${item.Site}', Mobile: '${item.Mobile}', Location: '${item.location}'")

            // Ensure buttons are clickable
            websiteBtn.isClickable = true
            messageBtn.isClickable = true
            callBtn.isClickable = true
            directionBtn.isClickable = true

            websiteBtn.setOnClickListener {
                android.util.Log.d("DetailActivity", "Website button clicked")
                android.util.Log.d("DetailActivity", "Site value: '${item.Site}' (length: ${item.Site.length})")
                try {
                    // Test with a default website if Site is empty
                    val websiteUrl = if (item.Site.isNotEmpty()) {
                        item.Site
                    } else {
                        "https://www.google.com" // Default website for testing
                    }
                    android.util.Log.d("DetailActivity", "Opening website: $websiteUrl")
                    val intent = Intent(Intent.ACTION_VIEW, websiteUrl.toUri())
                    startActivity(intent)
                } catch (e: Exception) {
                    android.util.Log.e("DetailActivity", "Error opening website: ${e.message}")
                }
            }

            messageBtn.setOnClickListener {
                android.util.Log.d("DetailActivity", "Message button clicked")
                android.util.Log.d("DetailActivity", "Mobile value: '${item.Mobile}' (length: ${item.Mobile.length})")
                try {
                    // Test with a default number if Mobile is empty
                    val phoneNumber = if (item.Mobile.isNotEmpty()) {
                        item.Mobile
                    } else {
                        "1234567890" // Default number for testing
                    }
                    android.util.Log.d("DetailActivity", "Sending SMS to: $phoneNumber")
                    val uri = "smsto:$phoneNumber".toUri()
                    val intent = Intent(Intent.ACTION_SENDTO, uri)
                    intent.putExtra("sms_body", "Hello, I would like to schedule an appointment")
                    startActivity(intent)
                } catch (e: Exception) {
                    android.util.Log.e("DetailActivity", "Error opening SMS: ${e.message}")
                }
            }

            callBtn.setOnClickListener {
                android.util.Log.d("DetailActivity", "Call button clicked")
                android.util.Log.d("DetailActivity", "Mobile value: '${item.Mobile}' (length: ${item.Mobile.length})")
                try {
                    // Test with a default number if Mobile is empty
                    val phoneNumber = if (item.Mobile.isNotEmpty()) {
                        item.Mobile.trim()
                    } else {
                        "1234567890" // Default number for testing
                    }
                    android.util.Log.d("DetailActivity", "Calling: $phoneNumber")
                    val uri = "tel:$phoneNumber".toUri()
                    val intent = Intent(Intent.ACTION_DIAL, uri)
                    startActivity(intent)
                } catch (e: Exception) {
                    android.util.Log.e("DetailActivity", "Error opening dialer: ${e.message}")
                }
            }

            directionBtn.setOnClickListener {
                android.util.Log.d("DetailActivity", "Direction button clicked")
                android.util.Log.d("DetailActivity", "Location value: '${item.location}' (length: ${item.location.length})")
                try {
                    // Test with a default location if location is empty
                    val locationUrl = if (item.location.isNotEmpty()) {
                        item.location
                    } else {
                        "geo:0,0?q=Hospital" // Default location search for testing
                    }
                    android.util.Log.d("DetailActivity", "Opening location: $locationUrl")
                    val intent = Intent(Intent.ACTION_VIEW, locationUrl.toUri())
                    startActivity(intent)
                } catch (e: Exception) {
                    android.util.Log.e("DetailActivity", "Error opening maps: ${e.message}")
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

            // Enhanced image loading logic matching the adapter
            var imageUrl = item.image

            // Fallback to first chamber image if main image is blank
            if (imageUrl.isBlank() && item.chambers.isNotEmpty()) {
                imageUrl = item.chambers[0].image
            }

            // Convert Imgur URL to direct image URL if needed
            if (imageUrl.isNotBlank() && imageUrl.contains("imgur.com") && !imageUrl.contains("i.imgur.com")) {
                val imageId = imageUrl.substringAfterLast("/")
                imageUrl = "https://i.imgur.com/$imageId.jpg"
            }

            Glide.with(this@DetailActivity)
                .load(imageUrl)
                .placeholder(R.drawable.women) // Show placeholder while loading
                .error(R.drawable.women) // Show fallback image if loading fails
                .centerCrop()
                .into(img)

            // Debug: Log the processed image URL
            android.util.Log.d("DetailActivity", "Original image URL: ${item.image}")
            android.util.Log.d("DetailActivity", "Processed image URL: $imageUrl")
            android.util.Log.d("DetailActivity", "Chambers available: ${item.chambers.size}")
        }
    }
}