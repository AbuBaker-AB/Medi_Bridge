package com.aas.medi_bridge.Activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aas.medi_bridge.Adapter.NotificationAdapter
import com.aas.medi_bridge.Domain.AppointmentNotification
import com.aas.medi_bridge.R
import com.aas.medi_bridge.databinding.ActivityNotificationBinding

class NotificationActivity : BaseActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var notificationAdapter: NotificationAdapter
    private val appointmentsList = mutableListOf<AppointmentNotification>()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        loadLocalNotifications()
    }

    override fun onResume() {
        super.onResume()
        android.util.Log.d("NotificationActivity", "🔄 onResume() called - reloading notifications")
        loadLocalNotifications()
    }

    private fun setupUI() {
        // Set up toolbar
        binding.backBtn.setOnClickListener {
            finish()
        }

        // Set up RecyclerView for notifications
        android.util.Log.d("NotificationActivity", "🔧 Setting up RecyclerView...")
        notificationAdapter = NotificationAdapter(appointmentsList)
        android.util.Log.d("NotificationActivity", "📱 Created adapter with ${appointmentsList.size} initial items")

        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(this)
        android.util.Log.d("NotificationActivity", "📐 Set LinearLayoutManager")

        binding.notificationRecyclerView.adapter = notificationAdapter
        android.util.Log.d("NotificationActivity", "🔗 Attached adapter to RecyclerView")

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("appointment_notifications", MODE_PRIVATE)

        android.util.Log.d("NotificationActivity", "✅ UI setup completed")
    }

    private fun loadLocalNotifications() {
        android.util.Log.d("NotificationActivity", "📱 === LOADING NOTIFICATIONS DEBUG ===")

        // Show loading initially
        showLoading()

        // Load appointments with a short delay to see loading state
        binding.root.postDelayed({
            try {
                // Check SharedPreferences directly
                val allPrefs = sharedPreferences.all
                android.util.Log.d("NotificationActivity", "🔍 All SharedPreferences keys: ${allPrefs.keys}")
                allPrefs.forEach { (key, value) ->
                    android.util.Log.d("NotificationActivity", "🔑 $key = $value")
                }

                // Load appointments from the simple string format
                val appointmentsStringSet = sharedPreferences.getStringSet("appointments_simple", mutableSetOf()) ?: mutableSetOf()
                android.util.Log.d("NotificationActivity", "📋 Found ${appointmentsStringSet.size} raw appointment strings")

                if (appointmentsStringSet.isEmpty()) {
                    android.util.Log.w("NotificationActivity", "⚠️ No appointments found in SharedPreferences!")
                    android.util.Log.w("NotificationActivity", "⚠️ This means either no appointments were saved, or they were saved with a different key")
                }

                // Debug: Print all appointment strings
                appointmentsStringSet.forEachIndexed { index, appointment ->
                    android.util.Log.d("NotificationActivity", "📝 Raw appointment $index: '$appointment'")
                }

                appointmentsList.clear()

                // Parse each appointment string
                appointmentsStringSet.forEachIndexed { index, appointmentString ->
                    android.util.Log.d("NotificationActivity", "🔄 Processing appointment $index: $appointmentString")

                    try {
                        val parts = appointmentString.split("|")
                        android.util.Log.d("NotificationActivity", "📊 Split into ${parts.size} parts: $parts")

                        if (parts.size >= 5) {
                            val patientName = parts[0].trim()
                            val doctorName = parts[1].trim()
                            val appointmentDate = parts[2].trim()
                            val appointmentTime = parts[3].trim()
                            val timestamp = parts[4].toLongOrNull() ?: System.currentTimeMillis()

                            android.util.Log.d("NotificationActivity", "👤 Patient: '$patientName'")
                            android.util.Log.d("NotificationActivity", "👨‍⚕️ Doctor: '$doctorName'")
                            android.util.Log.d("NotificationActivity", "📅 Date: '$appointmentDate'")
                            android.util.Log.d("NotificationActivity", "⏰ Time: '$appointmentTime'")

                            val appointment = AppointmentNotification(
                                id = timestamp.toString(),
                                patientName = patientName,
                                patientPhone = "",
                                appointmentDate = appointmentDate,
                                appointmentTime = appointmentTime,
                                doctorId = doctorName,
                                doctorEmail = "",
                                doctorName = doctorName,
                                status = "confirmed",
                                symptoms = "Appointment booked successfully",
                                timestamp = timestamp
                            )

                            appointmentsList.add(appointment)
                            android.util.Log.d("NotificationActivity", "✅ Successfully created appointment object")
                        } else {
                            android.util.Log.w("NotificationActivity", "⚠️ Invalid appointment format: $appointmentString (${parts.size} parts)")
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("NotificationActivity", "❌ Error parsing appointment: $appointmentString - ${e.message}")
                    }
                }

                // Sort by timestamp (newest first)
                appointmentsList.sortByDescending { it.timestamp }

                android.util.Log.d("NotificationActivity", "📊 Final appointments count: ${appointmentsList.size}")

                // Hide loading and update UI
                hideLoading()

                if (appointmentsList.isEmpty()) {
                    android.util.Log.d("NotificationActivity", "📭 No appointments found - showing empty state")
                    showEmptyState()
                } else {
                    android.util.Log.d("NotificationActivity", "📋 Found ${appointmentsList.size} appointments - showing list")
                    showNotificationsList()

                    // THIS IS THE MISSING LINE - Update the adapter with new data
                    android.util.Log.d("NotificationActivity", "🔄 About to update adapter with ${appointmentsList.size} appointments")

                    // Add null check and detailed logging
                    if (::notificationAdapter.isInitialized) {
                        android.util.Log.d("NotificationActivity", "✅ Adapter is initialized, calling updateNotifications")
                        android.util.Log.d("NotificationActivity", "🔍 Adapter class: ${notificationAdapter.javaClass.simpleName}")
                        android.util.Log.d("NotificationActivity", "🔍 Current adapter item count: ${notificationAdapter.itemCount}")
                        try {
                            // CRITICAL FIX: Pass a copy of the list, not the same reference
                            notificationAdapter.updateNotifications(appointmentsList.toList())
                            android.util.Log.d("NotificationActivity", "✅ Successfully called updateNotifications")
                            android.util.Log.d("NotificationActivity", "🔍 Adapter item count after update: ${notificationAdapter.itemCount}")
                        } catch (e: Exception) {
                            android.util.Log.e("NotificationActivity", "❌ Error calling updateNotifications: ${e.message}")
                            android.util.Log.e("NotificationActivity", "Stack trace: ${e.stackTrace?.joinToString("\n")}")
                        }
                    } else {
                        android.util.Log.e("NotificationActivity", "❌ Adapter is not initialized!")
                    }

                    updateNotificationCount(appointmentsList.size)

                    // Debug: Log each appointment that will be displayed
                    appointmentsList.forEachIndexed { index, appointment ->
                        android.util.Log.d("NotificationActivity", "📱 Appointment $index to display: ${appointment.patientName} with Dr. ${appointment.doctorName}")
                    }
                }

            } catch (e: Exception) {
                android.util.Log.e("NotificationActivity", "💥 Critical error loading appointments: ${e.message}")
                android.util.Log.e("NotificationActivity", "Stack trace: ${e.stackTrace?.joinToString("\n") ?: "No stack trace"}")
                hideLoading()
                showEmptyState()
            }
        }, 300) // Short delay to show loading
    }

    private fun showLoading() {
        android.util.Log.d("NotificationActivity", "🔄 Showing loading state")
        binding.progressBar.visibility = View.VISIBLE
        binding.emptyStateLayout.visibility = View.GONE
        binding.notificationRecyclerView.visibility = View.GONE
    }

    private fun hideLoading() {
        android.util.Log.d("NotificationActivity", "⏹️ Hiding loading state")
        binding.progressBar.visibility = View.GONE
    }

    private fun showEmptyState() {
        android.util.Log.d("NotificationActivity", "📭 === SHOWING EMPTY STATE ===")
        binding.progressBar.visibility = View.GONE
        binding.notificationRecyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.VISIBLE
        android.util.Log.d("NotificationActivity", "📭 Empty state is now visible")
    }

    private fun showNotificationsList() {
        android.util.Log.d("NotificationActivity", "📋 === SHOWING NOTIFICATIONS LIST ===")
        binding.progressBar.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.GONE
        binding.notificationRecyclerView.visibility = View.VISIBLE
        android.util.Log.d("NotificationActivity", "📋 RecyclerView is now visible with ${appointmentsList.size} items")
    }

    private fun updateNotificationCount(count: Int) {
        val sharedPref = getSharedPreferences("notifications", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("notification_count", count)
            apply()
        }
        android.util.Log.d("NotificationActivity", "📊 Updated notification count to: $count")
    }

    companion object {
        // Simple method to save appointment notifications using the string format
        fun saveAppointmentNotification(
            context: android.content.Context,
            doctorName: String,
            patientName: String,
            appointmentDate: String,
            appointmentTime: String,
            specialization: String = ""
        ) {
            try {
                android.util.Log.d("NotificationActivity", "💾 Saving appointment with simple format...")
                android.util.Log.d("NotificationActivity", "Doctor: $doctorName, Patient: $patientName, Date: $appointmentDate, Time: $appointmentTime")

                val sharedPreferences = context.getSharedPreferences("appointment_notifications", android.content.Context.MODE_PRIVATE)

                // Create appointment string in the same format as AppointmentFormActivity
                val appointmentData = "$patientName|$doctorName|$appointmentDate|$appointmentTime|${System.currentTimeMillis()}"

                // Get existing appointments
                val existingAppointments = sharedPreferences.getStringSet("appointments_simple", mutableSetOf()) ?: mutableSetOf()
                val updatedAppointments = existingAppointments.toMutableSet()
                updatedAppointments.add(appointmentData)

                // Save back to SharedPreferences
                sharedPreferences.edit()
                    .putStringSet("appointments_simple", updatedAppointments)
                    .commit()

                android.util.Log.d("NotificationActivity", "✅ Appointment saved successfully: $appointmentData")
                android.util.Log.d("NotificationActivity", "📊 Total appointments now: ${updatedAppointments.size}")

            } catch (e: Exception) {
                android.util.Log.e("NotificationActivity", "❌ Error saving appointment: ${e.message}")
            }
        }
    }
}
