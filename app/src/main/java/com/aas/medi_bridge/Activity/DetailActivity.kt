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
//            patientsTxt.text = item.patients
//            bioTxt.text = item.bio
//            addressTxt.text = item.address
//            experiensTxt.text = "${item.experience} years"
//            ratingTxt.text = item.rating.toString()

            // Debug: Log all doctor data fields to see what's populated
            android.util.Log.d("DetailActivity", "=== Doctor Data Debug ===")
            android.util.Log.d("DetailActivity", "name: '${item.name}'")
            android.util.Log.d("DetailActivity", "specialization: '${item.specialization}'")
            android.util.Log.d("DetailActivity", "degrees: '${item.degrees}'")
            android.util.Log.d("DetailActivity", "designation: '${item.designation}'")
            android.util.Log.d("DetailActivity", "city: '${item.city}'")
            android.util.Log.d("DetailActivity", "patients: '${item.patients}'")
            android.util.Log.d("DetailActivity", "rating: '${item.rating}'")
            android.util.Log.d("DetailActivity", "image: '${item.image}'")
            android.util.Log.d("DetailActivity", "bio: '${item.bio}'")
            android.util.Log.d("DetailActivity", "address: '${item.address}'")
            android.util.Log.d("DetailActivity", "experience: '${item.experience}'")
            android.util.Log.d("DetailActivity", "Mobile: '${item.Mobile}'")
            android.util.Log.d("DetailActivity", "Site: '${item.Site}'")
            android.util.Log.d("DetailActivity", "location: '${item.location}'")
            android.util.Log.d("DetailActivity", "chambers: ${item.chambers.size} items")
            android.util.Log.d("DetailActivity", "========================")

            // Show placeholder text for empty bio
            if (item.bio.isBlank()) {
             //   bioTxt.text = "Biography information will be available soon."
            }

            backBtn.setOnClickListener { finish() }

            // Add debug logging to verify button initialization
            android.util.Log.d("DetailActivity", "Setting up click listeners")
            android.util.Log.d("DetailActivity", "Doctor data - Site: '${item.Site}', Mobile: '${item.Mobile}', Location: '${item.location}'")

            // Ensure buttons are clickable
//            websiteBtn.isClickable = true
//            messageBtn.isClickable = true
            callBtn.isClickable = true
//            directionBtn.isClickable = true

//            websiteBtn.setOnClickListener {
//                android.util.Log.d("DetailActivity", "Website button clicked")
//                android.util.Log.d("DetailActivity", "Site value: '${item.Site}' (length: ${item.Site.length})")
//                try {
//                    // Test with a default website if Site is empty
//                    val websiteUrl = if (item.Site.isNotEmpty()) {
//                        item.Site
//                    } else {
//                        "https://www.google.com" // Default website for testing
//                    }
//                    android.util.Log.d("DetailActivity", "Opening website: $websiteUrl")
//                    val intent = Intent(Intent.ACTION_VIEW, websiteUrl.toUri())
//                    startActivity(intent)
//                } catch (e: Exception) {
//                    android.util.Log.e("DetailActivity", "Error opening website: ${e.message}")
//                }
//            }

//            messageBtn.setOnClickListener {
//                android.util.Log.d("DetailActivity", "Message button clicked")
//                android.util.Log.d("DetailActivity", "Mobile value: '${item.Mobile}' (length: ${item.Mobile.length})")
//                try {
//                    // Test with a default number if Mobile is empty
//                    val phoneNumber = if (item.Mobile.isNotEmpty()) {
//                        item.Mobile
//                    } else {
//                        "1234567890" // Default number for testing
//                    }
//                    android.util.Log.d("DetailActivity", "Sending SMS to: $phoneNumber")
//                    val uri = "smsto:$phoneNumber".toUri()
//                    val intent = Intent(Intent.ACTION_SENDTO, uri)
//                    intent.putExtra("sms_body", "Hello, I would like to schedule an appointment")
//                    startActivity(intent)
//                } catch (e: Exception) {
//                    android.util.Log.e("DetailActivity", "Error opening SMS: ${e.message}")
//                }
//            }

            callBtn.setOnClickListener {
                val chamber = item.chambers.firstOrNull()
                val phoneNumber = chamber?.appointment_number?.takeIf { !it.isNullOrBlank() } ?: "1234567890"
                val uri = "tel:$phoneNumber".toUri()
                val intent = Intent(Intent.ACTION_DIAL, uri)
                startActivity(intent)
            }

            locationBtn.setOnClickListener {
                android.util.Log.d("DetailActivity", "Location button clicked")
                try {
                    // Get location from first chamber or use default
                    val locationUrl = if (item.chambers.isNotEmpty() && item.chambers[0].location.isNotBlank()) {
                        item.chambers[0].location
                    } else if (item.location.isNotBlank()) {
                        item.location
                    } else {
                        "https://maps.google.com" // Default Google Maps
                    }

                    android.util.Log.d("DetailActivity", "Opening location: $locationUrl")
                    val intent = Intent(Intent.ACTION_VIEW, locationUrl.toUri())
                    startActivity(intent)
                } catch (e: Exception) {
                    android.util.Log.e("DetailActivity", "Error opening location: ${e.message}")
                    // Fallback to open Google Maps app or web
                    try {
                        val fallbackIntent = Intent(Intent.ACTION_VIEW, "https://maps.google.com".toUri())
                        startActivity(fallbackIntent)
                    } catch (fallbackException: Exception) {
                        android.util.Log.e("DetailActivity", "Error opening fallback location: ${fallbackException.message}")
                    }
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

            // Bind degrees, designation, hospital name, and visiting hour from Firebase data with bold labels
            degreesTxt.text = android.text.SpannableStringBuilder().apply {
                append("Degrees: ", android.text.style.StyleSpan(android.graphics.Typeface.BOLD), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                append(item.degrees)
            }

            designationTxt.text = android.text.SpannableStringBuilder().apply {
                append("Designation: ", android.text.style.StyleSpan(android.graphics.Typeface.BOLD), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                append(item.designation)
            }

            // Get hospital name from first chamber or use default
            val hospitalName = if (item.chambers.isNotEmpty()) {
                item.chambers[0].name
            } else {
                "Not Available"
            }
            hospitalNameTxt.text = android.text.SpannableStringBuilder().apply {
                append("Hospital Name: ", android.text.style.StyleSpan(android.graphics.Typeface.BOLD), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                append(hospitalName)
            }

            // Get visiting hour from main model or first chamber with better logic
            val visitingHour = when {
                item.visiting_hour.isNotBlank() -> {
                    android.util.Log.d("DetailActivity", "Using main visiting_hour: '${item.visiting_hour}'")
                    item.visiting_hour
                }
                item.chambers.isNotEmpty() && item.chambers[0].visiting_hour.isNotBlank() -> {
                    android.util.Log.d("DetailActivity", "Using chamber visiting_hour: '${item.chambers[0].visiting_hour}'")
                    item.chambers[0].visiting_hour
                }
                else -> {
                    android.util.Log.d("DetailActivity", "No visiting hour found, using default")
                    "Not Available"
                }
            }
            visitingHourTxt.text = android.text.SpannableStringBuilder().apply {
                append("Visiting Hour: ", android.text.style.StyleSpan(android.graphics.Typeface.BOLD), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                append(visitingHour)
            }

            // Remove dynamic chambers population - now using static fields above
            // chambersContainer.removeAllViews()
            // val context = chambersContainer.context
            // for (chamber in item.chambers) {
            //     val chamberLayout = android.widget.LinearLayout(context).apply {
            //         orientation = android.widget.LinearLayout.VERTICAL
            //         setPadding(0, 0, 0, 24)
            //     }
            //     val nameView = android.widget.TextView(context).apply {
            //         text = "Hospital: ${chamber.name}"
            //         setTextColor(resources.getColor(R.color.black))
            //         textSize = 16f
            //         setTypeface(null, android.graphics.Typeface.BOLD)
            //     }
            //     val visitingHourView = android.widget.TextView(context).apply {
            //         text = "Visiting hour: ${chamber.visiting_hour}"
            //         setTextColor(resources.getColor(R.color.black))
            //         textSize = 15f
            //     }
            //     chamberLayout.addView(nameView)
            //     chamberLayout.addView(visitingHourView)
            //     chambersContainer.addView(chamberLayout)
            // }

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
                .placeholder(R.drawable.blank_profile) // Show placeholder while loading
                .error(R.drawable.blank_profile) // Show fallback image if loading fails
                .centerCrop()
                .into(img)

            // Debug: Log the processed image URL
            android.util.Log.d("DetailActivity", "Original image URL: ${item.image}")
            android.util.Log.d("DetailActivity", "Processed image URL: $imageUrl")
            android.util.Log.d("DetailActivity", "Chambers available: ${item.chambers.size}")
        }
    }
}