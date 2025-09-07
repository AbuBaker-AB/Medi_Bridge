package com.aas.medi_bridge.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
        val intentItem = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("Object", DoctorsModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("Object")
        }

        if (intentItem == null) {
            android.util.Log.e("DetailActivity", "No doctor data received")
            finish()
            return
        }

        item = intentItem

        binding.apply {
            titleTxt.text = item.name
            specialTxt.text = item.specialization
            // Removed rating, patient, bio, and experience fields as requested

            backBtn.setOnClickListener { finish() }

            // Ensure buttons are clickable
            callBtn.isClickable = true

            callBtn.setOnClickListener {
                try {
                    val chamber = item.chambers.firstOrNull()
                    val phoneNumber = chamber?.appointment_number?.takeIf { !it.isNullOrBlank() } ?: "1234567890"
                    val uri = "tel:$phoneNumber".toUri()
                    val intent = Intent(Intent.ACTION_DIAL, uri)
                    startActivity(intent)
                } catch (e: Exception) {
                    android.util.Log.e("DetailActivity", "Error opening phone dialer: ${e.message}")
                }
            }

            locationBtn.setOnClickListener {
                try {
                    // Get location from first chamber or use default
                    val locationUrl = when {
                        item.chambers.isNotEmpty() && item.chambers[0].location.isNotBlank() -> item.chambers[0].location
                        item.location.isNotBlank() -> item.location
                        else -> "https://maps.google.com" // Default Google Maps
                    }

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
                    "${item.name} - ${item.address} - ${item.mobile}"
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

            // Make Appointment button logic - now simply opens the appointment form
            makeBtn.setOnClickListener {
                // Start appointment form activity with doctor data
                val intent = Intent(this@DetailActivity, AppointmentFormActivity::class.java)
                intent.putExtra("Object", item) // Pass the entire doctor object
                startActivity(intent)
            }

            // Debug: Log the processed image URL
            android.util.Log.d("DetailActivity", "Original image URL: ${item.image}")
            android.util.Log.d("DetailActivity", "Processed image URL: $imageUrl")
            android.util.Log.d("DetailActivity", "Chambers available: ${item.chambers.size}")
        }
    }
}
