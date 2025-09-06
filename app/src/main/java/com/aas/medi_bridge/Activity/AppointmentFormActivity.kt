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
import com.aas.medi_bridge.R
import com.aas.medi_bridge.databinding.ActivityAppointmentFormBinding
import java.util.*

class AppointmentFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppointmentFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinners()
        setupForm()
        setupDatePicker()
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
        // Retrieve doctor info from intent
        val doctorName = intent.getStringExtra("doctor_name")
        val doctorSpecialization = intent.getStringExtra("doctor_specialization")
        val selectedDate = intent.getStringExtra("selected_date")
        val selectedTime = intent.getStringExtra("selected_time")

        binding.tvDoctorName.text = doctorName ?: "Doctor"

        // Set up red asterisks for required fields
        setupRedAsterisks()

        // Submit button click listener
        binding.btnSubmit.setOnClickListener {
            if (validateForm()) {
                collectFormData()

                // Show appointment confirmation notification ONLY after successful form submission
                showAppointmentConfirmationNotification(
                    doctorName ?: "Unknown Doctor",
                    doctorSpecialization ?: "General",
                    selectedDate ?: "TBD",
                    selectedTime ?: "TBD"
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

        val doctorName = intent.getStringExtra("doctor_name") ?: "Unknown Doctor"
        val selectedDate = intent.getStringExtra("selected_date") ?: "TBD"
        val selectedTime = intent.getStringExtra("selected_time") ?: "TBD"

        // Log the collected data
        android.util.Log.d("AppointmentForm", "=== Appointment Data ===")
        android.util.Log.d("AppointmentForm", "Doctor: $doctorName")
        android.util.Log.d("AppointmentForm", "Patient: $firstName $lastName")
        android.util.Log.d("AppointmentForm", "DOB: $dob")
        android.util.Log.d("AppointmentForm", "Gender: $gender")
        android.util.Log.d("AppointmentForm", "Contact: $contactNumber")
        android.util.Log.d("AppointmentForm", "Email: $email")
        android.util.Log.d("AppointmentForm", "Message: $message")
        android.util.Log.d("AppointmentForm", "Date: $selectedDate")
        android.util.Log.d("AppointmentForm", "Time: $selectedTime")
        android.util.Log.d("AppointmentForm", "========================")
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
        val message = "Dear $patientName,\n\nYour appointment has been confirmed with Dr. $doctorName from $specialization department.\n\nDate: $date\nTime: $time\n\nPlease arrive 15 minutes before your appointed time.\nThank you!"

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_medical_icon)
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