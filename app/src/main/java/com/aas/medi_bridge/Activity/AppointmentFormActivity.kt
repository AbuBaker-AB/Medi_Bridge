package com.aas.medi_bridge.Activity

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.aas.medi_bridge.Adapter.DateChipAdapter
import com.aas.medi_bridge.Adapter.TimeChipAdapter
import com.aas.medi_bridge.Domain.DoctorsModel
import com.aas.medi_bridge.R
import com.aas.medi_bridge.databinding.ActivityAppointmentFormBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AppointmentFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppointmentFormBinding
    private lateinit var item: DoctorsModel
    private var selectedDate: String = ""
    private var selectedTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get doctor data from intent
        item = intent.getParcelableExtra("Object") ?: return

        setupDoctorInfo()
        setupSpinners()
        setupForm()
        setupDatePicker()
        setupAvailableDatesAndTimes()
    }

    private fun setupDoctorInfo() {
        // Set doctor name in the form
        binding.tvDoctorName.text = item.name

        // Load doctor image with corner radius
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

        // Load image with corner radius using Glide
        com.bumptech.glide.Glide.with(this)
            .load(imageUrl)
            .transform(
                com.bumptech.glide.load.resource.bitmap.CenterCrop(),
                com.bumptech.glide.load.resource.bitmap.RoundedCorners(24)
            )
            .placeholder(R.drawable.blank_profile)
            .error(R.drawable.blank_profile)
            .into(binding.ivMedicalIcon)
    }

    private fun setupAvailableDatesAndTimes() {
        // Get visiting hours from database
        val visitingHour = when {
            item.visiting_hour.isNotBlank() -> item.visiting_hour
            item.chambers.isNotEmpty() && item.chambers[0].visiting_hour.isNotBlank() -> item.chambers[0].visiting_hour
            else -> "9am to 5pm"
        }

        val availableDates = generateAvailableDates()
        val dateAdapter = DateChipAdapter(availableDates) { date ->
            selectedDate = date

            // Update available times for selected date
            val availableTimes = getAvailableTimesForDoctor()
            val timeAdapter = TimeChipAdapter(availableTimes) { time ->
                selectedTime = time
            }
            binding.availableTimesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.availableTimesRecyclerView.adapter = timeAdapter
        }

        binding.availableDatesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.availableDatesRecyclerView.adapter = dateAdapter

        // Show available times immediately (not just when date is selected)
        val availableTimes = getAvailableTimesForDoctor()
        val timeAdapter = TimeChipAdapter(availableTimes) { time ->
            selectedTime = time
        }
        binding.availableTimesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.availableTimesRecyclerView.adapter = timeAdapter
    }

    private fun generateAvailableDates(): List<String> {
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val dates = mutableListOf<String>()
        val calendar = Calendar.getInstance()

        // Get closed days from visiting hours
        val closedDays = getClosedDaysFromVisitingHours()

        // Generate next 14 days excluding closed days
        for (i in 0 until 14) {
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val dayName = when (dayOfWeek) {
                Calendar.SUNDAY -> "sunday"
                Calendar.MONDAY -> "monday"
                Calendar.TUESDAY -> "tuesday"
                Calendar.WEDNESDAY -> "wednesday"
                Calendar.THURSDAY -> "thursday"
                Calendar.FRIDAY -> "friday"
                Calendar.SATURDAY -> "saturday"
                else -> ""
            }

            // Only add if not a closed day
            if (!closedDays.contains(dayName)) {
                dates.add(dateFormat.format(calendar.time))
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dates
    }

    private fun getClosedDaysFromVisitingHours(): List<String> {
        val visitingHour = when {
            item.visiting_hour.isNotBlank() -> item.visiting_hour
            item.chambers.isNotEmpty() && item.chambers[0].visiting_hour.isNotBlank() -> item.chambers[0].visiting_hour
            else -> ""
        }

        val closedDays = mutableListOf<String>()

        if (visitingHour.contains("closed:", ignoreCase = true)) {
            val closedPart = visitingHour.substringAfter("closed:", "").trim()
            when {
                closedPart.contains("friday", ignoreCase = true) -> closedDays.add("friday")
                closedPart.contains("saturday", ignoreCase = true) -> closedDays.add("saturday")
                closedPart.contains("sunday", ignoreCase = true) -> closedDays.add("sunday")
                closedPart.contains("monday", ignoreCase = true) -> closedDays.add("monday")
                closedPart.contains("tuesday", ignoreCase = true) -> closedDays.add("tuesday")
                closedPart.contains("wednesday", ignoreCase = true) -> closedDays.add("wednesday")
                closedPart.contains("thursday", ignoreCase = true) -> closedDays.add("thursday")
            }
        }

        return closedDays
    }

    private fun getAvailableTimesForDoctor(): List<String> {
        val visitingHour = when {
            item.visiting_hour.isNotBlank() -> item.visiting_hour
            item.chambers.isNotEmpty() && item.chambers[0].visiting_hour.isNotBlank() -> item.chambers[0].visiting_hour
            else -> "9am to 5pm"
        }

        return parseVisitingHoursToTimeSlots(visitingHour)
    }

    private fun parseVisitingHoursToTimeSlots(visitingHours: String): List<String> {
        val timeSlots = mutableListOf<String>()

        try {
            // Clean the visiting hours string - remove parentheses content first
            val cleanHours = visitingHours.replace(Regex("\\([^)]*\\)"), "").trim()

            // Handle multiple time ranges separated by "&"
            val timeRanges = cleanHours.split("&")

            for (range in timeRanges) {
                val trimmedRange = range.trim()

                // Extract start and end times using " to " as separator
                val parts = trimmedRange.split(" to ")
                if (parts.size == 2) {
                    val startTimeRaw = parts[0].trim()
                    val endTimeRaw = parts[1].trim()

                    val startTime = normalizeTimeFormat(startTimeRaw)
                    val endTime = normalizeTimeFormat(endTimeRaw)

                    val slots = generateTimeSlots(startTime, endTime)
                    timeSlots.addAll(slots)
                }
            }
        } catch (e: Exception) {
            // Fallback to default times
            timeSlots.addAll(listOf("9:00 AM", "10:00 AM", "11:00 AM", "2:00 PM", "3:00 PM", "4:00 PM"))
        }

        val result = timeSlots.ifEmpty { listOf("9:00 AM", "10:00 AM", "11:00 AM", "2:00 PM", "3:00 PM", "4:00 PM") }
        return result
    }

    private fun normalizeTimeFormat(time: String): String {
        // Remove any extra spaces and unwanted characters but keep the basic time format
        val cleaned = time.trim().replace(Regex("\\s+"), " ")

        // Handle different time formats from database
        return when {
            // Format: "7pm" -> "7:00 PM"
            cleaned.matches(Regex("\\d{1,2}pm", RegexOption.IGNORE_CASE)) -> {
                val hour = cleaned.replace(Regex("[pm]", RegexOption.IGNORE_CASE), "")
                "$hour:00 PM"
            }
            // Format: "7am" -> "7:00 AM"
            cleaned.matches(Regex("\\d{1,2}am", RegexOption.IGNORE_CASE)) -> {
                val hour = cleaned.replace(Regex("[am]", RegexOption.IGNORE_CASE), "")
                "$hour:00 AM"
            }
            // Format: "10:30pm" -> "10:30 PM"
            cleaned.matches(Regex("\\d{1,2}:\\d{2}pm", RegexOption.IGNORE_CASE)) -> {
                cleaned.replace(Regex("pm", RegexOption.IGNORE_CASE), " PM")
            }
            // Format: "10:30am" -> "10:30 AM"
            cleaned.matches(Regex("\\d{1,2}:\\d{2}am", RegexOption.IGNORE_CASE)) -> {
                cleaned.replace(Regex("am", RegexOption.IGNORE_CASE), " AM")
            }
            // If already in correct format, return as is
            else -> cleaned
        }
    }

    private fun generateTimeSlots(startTime: String, endTime: String): List<String> {
        val timeSlots = mutableListOf<String>()
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())

        try {
            val start = sdf.parse(startTime) ?: return emptyList()
            val end = sdf.parse(endTime) ?: return emptyList()

            val calendar = Calendar.getInstance()
            calendar.time = start

            while (calendar.time.before(end)) {
                timeSlots.add(sdf.format(calendar.time))
                calendar.add(Calendar.MINUTE, 15) // 15-minute intervals
            }
        } catch (e: Exception) {
            // Handle error silently
        }

        return timeSlots
    }

    private fun setupSpinners() {
        // Setup Gender Spinner with Male, Female options
        val genderOptions = arrayOf("Select Gender", "Male", "Female")

        // Create custom adapter to handle placeholder styling
        val genderAdapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderOptions) {

            override fun isEnabled(position: Int): Boolean {
                // Disable the first item (placeholder)
                return position != 0
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view as TextView

                if (position == 0) {
                    // Set placeholder text color to gray
                    textView.setTextColor(Color.parseColor("#9CA3AF"))
                } else {
                    textView.setTextColor(Color.parseColor("#000000"))
                }

                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                // Skip the placeholder entirely - don't create view for it
                if (position == 0) {
                    val emptyView = TextView(this@AppointmentFormActivity)
                    emptyView.layoutParams = ViewGroup.LayoutParams(0, 0)
                    emptyView.visibility = View.GONE
                    return emptyView
                }

                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                textView.setTextColor(Color.parseColor("#000000"))

                return view
            }

            override fun getCount(): Int {
                return super.getCount()
            }
        }

        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = genderAdapter

        // Add selection listener
        binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) { // Ignore placeholder selection
                    val selectedGender = genderOptions[position]
                    // Handle selection if needed
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun setupForm() {
        // Set up red asterisks for required fields
        setupRedAsterisks()

        // Submit button click listener
        binding.btnSubmit.setOnClickListener {
            if (validateForm()) {
                collectFormData()

                // Show appointment confirmation notification using actual doctor data
                showAppointmentConfirmationNotification(
                    item.name,
                    item.specialization,
                    selectedDate,
                    selectedTime
                )

                finish()
            }
        }
    }

    private fun setupDatePicker() {
        binding.etDateOfBirth.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                    binding.etDateOfBirth.setText(formattedDate)
                },
                year, month, day
            )

            // Set maximum date to today (no future dates for birth date)
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

            // Set minimum date to 100 years ago
            val minCalendar = Calendar.getInstance()
            minCalendar.add(Calendar.YEAR, -100)
            datePickerDialog.datePicker.minDate = minCalendar.timeInMillis

            datePickerDialog.show()
        }
    }

    private fun setupRedAsterisks() {
        // Helper function to create text with red asterisk
        fun createLabelWithRedAsterisk(text: String): android.text.SpannableString {
            val spannableString = android.text.SpannableString("$text *")
            val foregroundColorSpan = android.text.style.ForegroundColorSpan(Color.parseColor("#dc2626"))
            spannableString.setSpan(foregroundColorSpan, text.length + 1, spannableString.length, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            return spannableString
        }

        // Apply red asterisks to required field labels
        binding.tvFirstNameLabel.text = createLabelWithRedAsterisk("First Name")
        binding.tvLastNameLabel.text = createLabelWithRedAsterisk("Last Name")
        binding.tvDobLabel.text = createLabelWithRedAsterisk("Date of Birth")
        binding.tvGenderLabel.text = createLabelWithRedAsterisk("Gender")
        binding.tvContactLabel.text = createLabelWithRedAsterisk("Contact Number")
        // Add red asterisks for Available Dates and Available Times
        binding.tvAvailableDatesLabel.text = createLabelWithRedAsterisk("Available Dates")
        binding.tvAvailableTimesLabel.text = createLabelWithRedAsterisk("Available Times")
    }

    private fun validateForm(): Boolean {
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val dob = binding.etDateOfBirth.text.toString().trim()
        val contactNumber = binding.etContactNumber.text.toString().trim()

        when {
            firstName.isEmpty() -> {
                binding.etFirstName.error = "First name is required"
                binding.etFirstName.requestFocus()
                return false
            }
            lastName.isEmpty() -> {
                binding.etLastName.error = "Last name is required"
                binding.etLastName.requestFocus()
                return false
            }
            dob.isEmpty() -> {
                Toast.makeText(this, "Please select date of birth", Toast.LENGTH_SHORT).show()
                return false
            }
            contactNumber.isEmpty() -> {
                binding.etContactNumber.error = "Contact number is required"
                binding.etContactNumber.requestFocus()
                return false
            }
            binding.spinnerGender.selectedItemPosition == 0 -> {
                Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show()
                return false
            }
            selectedDate.isEmpty() -> {
                Toast.makeText(this, "Please select an appointment date", Toast.LENGTH_SHORT).show()
                return false
            }
            selectedTime.isEmpty() -> {
                Toast.makeText(this, "Please select an appointment time", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun collectFormData() {
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val dob = binding.etDateOfBirth.text.toString().trim()
        val gender = binding.spinnerGender.selectedItem.toString()
        val contactNumber = binding.etContactNumber.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val message = binding.etMessage.text.toString().trim()

        // Use the selected date and time from the RecyclerViews
        val doctorName = item.name
        val doctorSpecialization = item.specialization
        val patientName = "$firstName $lastName"

        // Save appointment notification locally
        saveAppointmentNotificationLocally(doctorName, patientName, doctorSpecialization)
    }

    private fun saveAppointmentNotificationLocally(
        doctorName: String,
        patientName: String,
        doctorSpecialization: String
    ) {
        try {
            // Use a simple direct approach to save the notification
            val sharedPreferences = getSharedPreferences("appointment_notifications", MODE_PRIVATE)

            // Create a simple appointment string instead of JSON object
            val appointmentData = "$patientName|$doctorName|$selectedDate|$selectedTime|${System.currentTimeMillis()}"

            // Get existing appointments and add the new one
            val existingAppointments = sharedPreferences.getStringSet("appointments_simple", mutableSetOf()) ?: mutableSetOf()

            val updatedAppointments = existingAppointments.toMutableSet()
            updatedAppointments.add(appointmentData)

            sharedPreferences.edit()
                .putStringSet("appointments_simple", updatedAppointments)
                .commit()

            Toast.makeText(this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Appointment booked, but failed to save notification", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAppointmentConfirmationNotification(doctorName: String, specialization: String, date: String, time: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "appointment_channel"

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Appointment Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notifications for appointment confirmations"
            notificationManager.createNotificationChannel(channel)
        }

        // Create the detailed message with doctor info, specialization, date, time and reminder
        val patientName = "${binding.etFirstName.text.toString().trim()} ${binding.etLastName.text.toString().trim()}"
        val message = "Dear $patientName,\n\nYour appointment has been confirmed with $doctorName from $specialization department.\n\nDate: $date\nTime: $time\n\nPlease arrive 15 minutes before your appointed time.\n\nThank you!"

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.medi_bridge_logo) // Use your app logo
            .setContentTitle("Appointment Confirmed - Dr. $doctorName")
            .setContentText("$specialization Department • $date at $time")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)

        // Also show a success toast
        Toast.makeText(this, "Appointment confirmed! Check notification for details.", Toast.LENGTH_LONG).show()
    }
}
