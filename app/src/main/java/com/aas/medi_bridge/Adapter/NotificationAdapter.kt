package com.aas.medi_bridge.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aas.medi_bridge.Domain.AppointmentNotification
import com.aas.medi_bridge.R
import com.aas.medi_bridge.databinding.ViewholderNotificationBinding

class NotificationAdapter(private val notifications: MutableList<AppointmentNotification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        android.util.Log.d("NotificationAdapter", "🏗️ Creating ViewHolder")
        val binding = ViewholderNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        android.util.Log.d("NotificationAdapter", "✅ ViewHolder created successfully")
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        android.util.Log.d("NotificationAdapter", "🔗 onBindViewHolder called for position $position")
        holder.bind(notifications[position])
    }

    override fun getItemCount(): Int {
        android.util.Log.d("NotificationAdapter", "📊 getItemCount called, returning ${notifications.size}")
        return notifications.size
    }

    fun updateNotifications(newNotifications: List<AppointmentNotification>) {
        android.util.Log.d("NotificationAdapter", "🔄 === UPDATING NOTIFICATIONS ===")
        android.util.Log.d("NotificationAdapter", "📥 Received ${newNotifications.size} new notifications")
        android.util.Log.d("NotificationAdapter", "📋 Current notifications count: ${notifications.size}")

        // Log each notification being added
        newNotifications.forEachIndexed { index, notification ->
            android.util.Log.d("NotificationAdapter", "📝 Notification $index: ${notification.patientName} with ${notification.doctorName}")
        }

        notifications.clear()
        notifications.addAll(newNotifications)

        android.util.Log.d("NotificationAdapter", "📊 After update - notifications count: ${notifications.size}")
        android.util.Log.d("NotificationAdapter", "🔄 Calling notifyDataSetChanged()")

        notifyDataSetChanged()

        android.util.Log.d("NotificationAdapter", "✅ notifyDataSetChanged() called")
    }

    class NotificationViewHolder(private val binding: ViewholderNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: AppointmentNotification) {
            android.util.Log.d("NotificationAdapter", "🔗 === BINDING NOTIFICATION ===")
            android.util.Log.d("NotificationAdapter", "👤 Patient: '${notification.patientName}'")
            android.util.Log.d("NotificationAdapter", "👨‍⚕️ Doctor: '${notification.doctorName}'")
            android.util.Log.d("NotificationAdapter", "📅 Date: '${notification.appointmentDate}'")
            android.util.Log.d("NotificationAdapter", "⏰ Time: '${notification.appointmentTime}'")

            binding.apply {
                tvDoctorName.text = "Dr. ${notification.doctorName}"
                tvAppointmentDate.text = notification.appointmentDate
                tvAppointmentTime.text = notification.appointmentTime
                tvPatientName.text = "Patient: ${notification.patientName}"
                tvStatus.text = notification.status.uppercase()

                android.util.Log.d("NotificationAdapter", "📝 Setting doctor name: 'Dr. ${notification.doctorName}'")
                android.util.Log.d("NotificationAdapter", "📝 Setting patient name: 'Patient: ${notification.patientName}'")

                // Set status color based on appointment status
                when (notification.status.lowercase()) {
                    "pending" -> {
                        tvStatus.setTextColor(itemView.context.getColor(R.color.purple))
                        statusIndicator.setBackgroundColor(itemView.context.getColor(R.color.purple))
                    }
                    "confirmed" -> {
                        tvStatus.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
                        statusIndicator.setBackgroundColor(itemView.context.getColor(android.R.color.holo_green_dark))
                    }
                    "cancelled" -> {
                        tvStatus.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
                        statusIndicator.setBackgroundColor(itemView.context.getColor(android.R.color.holo_red_dark))
                    }
                    else -> {
                        tvStatus.setTextColor(itemView.context.getColor(R.color.darkgrey))
                        statusIndicator.setBackgroundColor(itemView.context.getColor(R.color.darkgrey))
                    }
                }

                // Create the same detailed message format as the in-app notification
                val formattedMessage = "Dear ${notification.patientName},\n\n" +
                        "Your appointment has been confirmed with Dr. ${notification.doctorName}.\n\n" +
                        "Date: ${notification.appointmentDate}\n" +
                        "Time: ${notification.appointmentTime}\n\n" +
                        "Please arrive 15 minutes before your appointed time.\n\n" +
                        "Thank you!"

                android.util.Log.d("NotificationAdapter", "💬 Setting message: '$formattedMessage'")
                tvMessage.text = formattedMessage
                android.util.Log.d("NotificationAdapter", "✅ Message set to tvMessage")
            }
            android.util.Log.d("NotificationAdapter", "🔗 === BINDING COMPLETE ===")
        }
    }
}
