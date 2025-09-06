package com.aas.medi_bridge.Activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.aas.medi_bridge.Adapter.DateChipAdapter
import com.aas.medi_bridge.Adapter.TimeChipAdapter
import com.aas.medi_bridge.Domain.DoctorsModel
import com.aas.medi_bridge.R
import com.aas.medi_bridge.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : BaseActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: DoctorsModel
    private lateinit var dateAdapter: DateChipAdapter
    private lateinit var timeAdapter: TimeChipAdapter
    private var selectedDate: String = ""
    private var selectedTime: String = ""

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

            // Setup available dates and times RecyclerViews
            setupAvailableDatesAndTimes()

            // Make Appointment button logic - updated to include validation
            makeBtn.setOnClickListener {
                if (selectedDate.isEmpty()) {
                    Toast.makeText(this@DetailActivity, "Please select a date", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (selectedTime.isEmpty()) {
                    Toast.makeText(this@DetailActivity, "Please select a time", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Start appointment form activity with selected date and time
                val intent = Intent(this@DetailActivity, AppointmentFormActivity::class.java)
                intent.putExtra("doctor_name", item.name)
                intent.putExtra("doctor_specialization", item.specialization)
                intent.putExtra("selected_date", selectedDate)
                intent.putExtra("selected_time", selectedTime)
                startActivity(intent)
            }

            // Debug: Log the processed image URL
            android.util.Log.d("DetailActivity", "Original image URL: ${item.image}")
            android.util.Log.d("DetailActivity", "Processed image URL: $imageUrl")
            android.util.Log.d("DetailActivity", "Chambers available: ${item.chambers.size}")
        }
    }

    private fun setupAvailableDatesAndTimes() {
        // Generate available dates (next 7 days)
        val availableDates = generateAvailableDates()

        // Get available times from doctor's visiting hours or generate default times
        val availableTimes = getAvailableTimesForDoctor()

        // Setup dates RecyclerView
        dateAdapter = DateChipAdapter(availableDates) { date ->
            selectedDate = date
            android.util.Log.d("DetailActivity", "Selected date: $date")
        }

        binding.availableDatesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = dateAdapter
        }

        // Setup times RecyclerView
        timeAdapter = TimeChipAdapter(availableTimes) { time ->
            selectedTime = time
            android.util.Log.d("DetailActivity", "Selected time: $time")
        }

        binding.availableTimesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = timeAdapter
        }
    }

    private fun generateAvailableDates(): List<String> {
        val dates = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault())

        // Get closed days from visiting hours
        val closedDays = getClosedDaysFromVisitingHours()
        android.util.Log.d("DetailActivity", "Closed days for ${item.name}: $closedDays")

        var addedDates = 0
        var attemptedDays = 0

        // Generate dates until we have 7 available days (excluding closed days)
        while (addedDates < 7 && attemptedDays < 14) { // Max 14 days to avoid infinite loop
            val dayOfWeek = dayOfWeekFormat.format(calendar.time)

            // Check if this day is not in closed days
            if (!closedDays.contains(dayOfWeek.lowercase())) {
                dates.add(dateFormat.format(calendar.time))
                addedDates++
                android.util.Log.d("DetailActivity", "Added available date: ${dateFormat.format(calendar.time)} ($dayOfWeek)")
            } else {
                android.util.Log.d("DetailActivity", "Skipped closed date: ${dateFormat.format(calendar.time)} ($dayOfWeek)")
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1)
            attemptedDays++
        }

        return dates
    }

    private fun getClosedDaysFromVisitingHours(): List<String> {
        val closedDays = mutableListOf<String>()

        // Get visiting hour from main model or chambers
        val visitingHour = when {
            item.visiting_hour.isNotBlank() -> item.visiting_hour
            item.chambers.isNotEmpty() && item.chambers[0].visiting_hour.isNotBlank() -> item.chambers[0].visiting_hour
            else -> ""
        }

        if (visitingHour.isNotBlank()) {
            try {
                // Extract closed days from parentheses like "(Closed: Friday)" or "(Closed: Friday, Sunday)"
                val closedPattern = Regex("\\(.*?[Cc]losed:?\\s*([^)]+)\\)")
                val matches = closedPattern.findAll(visitingHour)

                for (match in matches) {
                    val closedDaysText = match.groupValues[1].trim()
                    android.util.Log.d("DetailActivity", "Found closed days text: '$closedDaysText'")

                    // Split by common separators and clean up
                    val days = closedDaysText.split(",", ";", "&", "and").map { day ->
                        day.trim().lowercase()
                            .replace("days", "")
                            .replace("day", "")
                            .trim()
                    }

                    for (day in days) {
                        if (day.isNotEmpty()) {
                            // Convert day names to full names
                            val fullDayName = when {
                                day.startsWith("sun") -> "sunday"
                                day.startsWith("mon") -> "monday"
                                day.startsWith("tue") -> "tuesday"
                                day.startsWith("wed") -> "wednesday"
                                day.startsWith("thu") -> "thursday"
                                day.startsWith("fri") -> "friday"
                                day.startsWith("sat") -> "saturday"
                                else -> day
                            }
                            if (fullDayName.isNotEmpty()) {
                                closedDays.add(fullDayName)
                                android.util.Log.d("DetailActivity", "Added closed day: $fullDayName")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("DetailActivity", "Error parsing closed days: ${e.message}")
            }
        }

        return closedDays.distinct()
    }

    private fun getAvailableTimesForDoctor(): List<String> {
        // First, try to extract times from doctor's visiting hour
        val visitingHour = when {
            item.visiting_hour.isNotBlank() -> item.visiting_hour
            item.chambers.isNotEmpty() && item.chambers[0].visiting_hour.isNotBlank() -> item.chambers[0].visiting_hour
            else -> ""
        }

        android.util.Log.d("DetailActivity", "Doctor visiting hour: '$visitingHour'")

        return if (visitingHour.isNotBlank()) {
            val parsedTimes = parseVisitingHoursToTimeSlots(visitingHour)
            android.util.Log.d("DetailActivity", "Parsed ${parsedTimes.size} time slots for ${item.name}")
            parsedTimes
        } else {
            android.util.Log.d("DetailActivity", "No visiting hours found for ${item.name}, using default times")
            // Default time slots if no visiting hours available - but make them different per doctor
            when (item.specialization.lowercase()) {
                "cardiologist", "cardiology" -> listOf("8:00 AM", "8:15 AM", "8:30 AM", "8:45 AM", "9:00 AM", "9:15 AM", "9:30 AM", "9:45 AM", "10:00 AM", "10:15 AM", "10:30 AM", "10:45 AM")
                "dermatologist", "dermatology" -> listOf("10:00 AM", "10:15 AM", "10:30 AM", "10:45 AM", "11:00 AM", "11:15 AM", "11:30 AM", "11:45 AM", "2:00 PM", "2:15 PM", "2:30 PM", "2:45 PM")
                "neurologist", "neurology" -> listOf("9:00 AM", "9:15 AM", "9:30 AM", "9:45 AM", "10:00 AM", "10:15 AM", "10:30 AM", "10:45 AM", "3:00 PM", "3:15 PM", "3:30 PM", "3:45 PM")
                else -> listOf("9:00 AM", "9:15 AM", "9:30 AM", "9:45 AM", "10:00 AM", "10:15 AM", "10:30 AM", "10:45 AM", "11:00 AM", "11:15 AM", "11:30 AM", "11:45 AM", "2:00 PM", "2:15 PM", "2:30 PM", "2:45 PM")
            }
        }
    }

    private fun parseVisitingHoursToTimeSlots(visitingHour: String): List<String> {
        val timeSlots = mutableListOf<String>()
        try {
            android.util.Log.d("DetailActivity", "Parsing visiting hour: '$visitingHour'")

            // Clean the visiting hour string - remove extra info like (Closed: Friday), (Everyday), etc.
            val cleanedVisitingHour = visitingHour
                .replace(Regex("\\(.*?\\)"), "") // Remove anything in parentheses
                .replace(Regex("\\[.*?\\]"), "") // Remove anything in brackets
                .trim()

            android.util.Log.d("DetailActivity", "Cleaned visiting hour: '$cleanedVisitingHour'")

            // Split by multiple separators: comma, semicolon, pipe, ampersand
            val timeRanges = cleanedVisitingHour.split(",", ";", "|", "&")
            for (range in timeRanges) {
                val cleanRange = range.trim()
                if (cleanRange.isEmpty()) continue
                android.util.Log.d("DetailActivity", "Processing time range: '$cleanRange'")

                // Split by common separators including "to", "-", "–", "—"
                val times = cleanRange.split("-", "to", "–", "—").map { it.trim() }
                if (times.size >= 2) {
                    val startTime = normalizeTimeFormat(times[0])
                    val endTime = normalizeTimeFormat(times[1])
                    android.util.Log.d("DetailActivity", "Start time: '$startTime', End time: '$endTime'")
                    if (startTime.isNotEmpty() && endTime.isNotEmpty()) {
                        val slots = generateTimeSlots(startTime, endTime)
                        timeSlots.addAll(slots)
                        android.util.Log.d("DetailActivity", "Generated "+slots.size+" slots for range $startTime - $endTime")
                    }
                } else {
                    android.util.Log.w("DetailActivity", "Could not parse time range: '$cleanRange'")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DetailActivity", "Error parsing visiting hours: ${e.message}")
            e.printStackTrace()
        }

        android.util.Log.d("DetailActivity", "Total time slots generated: ${timeSlots.size}")
        return if (timeSlots.isEmpty()) {
            // Fallback based on doctor specialty
            when (item.specialization.lowercase()) {
                "cardiologist", "cardiology" -> generateTimeSlots("8:00 AM", "12:00 PM")
                "dermatologist", "dermatology" -> generateTimeSlots("10:00 AM", "4:00 PM")
                "neurologist", "neurology" -> generateTimeSlots("9:00 AM", "1:00 PM")
                else -> generateTimeSlots("9:00 AM", "5:00 PM")
            }
        } else {
            timeSlots.distinct() // Remove duplicate
        }
    }

    private fun normalizeTimeFormat(timeStr: String): String {
        try {
            val cleaned = timeStr.trim().lowercase()

            // Handle formats like "7pm", "10pm", "9am", "12pm"
            if (cleaned.matches(Regex("\\d{1,2}(am|pm)"))) {
                val hour = cleaned.replace(Regex("[^\\d]"), "").toInt()
                val period = if (cleaned.contains("am")) "AM" else "PM"
                return "$hour:00 $period"
            }

            // Handle formats like "7:30pm", "10:15am"
            if (cleaned.matches(Regex("\\d{1,2}:\\d{2}(am|pm)"))) {
                val timeWithoutPeriod = cleaned.replace(Regex("[^\\d:]"), "")
                val period = if (cleaned.contains("am")) "AM" else "PM"
                return "$timeWithoutPeriod $period"
            }

            // Handle 24-hour format (e.g., "14:00" -> "2:00 PM")
            if (cleaned.matches(Regex("\\d{1,2}:\\d{2}")) && !cleaned.contains("am") && !cleaned.contains("pm")) {
                val parts = cleaned.split(":")
                val hour = parts[0].toInt()
                val minute = parts[1]
                return if (hour == 0) {
                    "12:$minute AM"
                } else if (hour < 12) {
                    "$hour:$minute AM"
                } else if (hour == 12) {
                    "12:$minute PM"
                } else {
                    "${hour - 12}:$minute PM"
                }
            }

            // Handle format like "9 AM" -> "9:00 AM"
            val upperCleaned = cleaned.uppercase()
            if (upperCleaned.matches(Regex("\\d{1,2}\\s*(AM|PM)"))) {
                val parts = upperCleaned.split(Regex("\\s+"))
                return "${parts[0]}:00 ${parts[1]}"
            }

            // Handle format like "9:30AM" -> "9:30 AM"
            if (upperCleaned.matches(Regex("\\d{1,2}:\\d{2}(AM|PM)"))) {
                return upperCleaned.replace(Regex("(AM|PM)"), " $1")
            }

            // Already in correct format
            if (upperCleaned.matches(Regex("\\d{1,2}:\\d{2}\\s+(AM|PM)"))) {
                return upperCleaned
            }

            android.util.Log.w("DetailActivity", "Could not normalize time format: '$timeStr'")
            return cleaned.uppercase()
        } catch (e: Exception) {
            android.util.Log.e("DetailActivity", "Error normalizing time format: $timeStr", e)
            return timeStr.uppercase()
        }
    }

    private fun generateTimeSlots(startTime: String, endTime: String): List<String> {
        val slots = mutableListOf<String>()

        try {
            val format = SimpleDateFormat("h:mm a", Locale.getDefault())
            val startCal = Calendar.getInstance()
            val endCal = Calendar.getInstance()

            startCal.time = format.parse(startTime) ?: return slots
            endCal.time = format.parse(endTime) ?: return slots

            while (startCal.before(endCal)) {
                slots.add(format.format(startCal.time))
                startCal.add(Calendar.MINUTE, 15) // 15-minute intervals
            }
        } catch (e: Exception) {
            android.util.Log.e("DetailActivity", "Error generating time slots: ${e.message}")
        }

        return slots
    }
}
