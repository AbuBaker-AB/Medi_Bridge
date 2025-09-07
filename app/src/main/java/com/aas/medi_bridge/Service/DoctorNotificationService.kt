package com.aas.medi_bridge.Service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.aas.medi_bridge.Activity.DoctorActivity
import com.aas.medi_bridge.R
import com.google.firebase.database.*

object DoctorNotificationService {

    private const val CHANNEL_ID = "doctor_approval_channel"
    private const val NOTIFICATION_ID = 1001

    fun startListeningForApproval(context: Context, doctorId: String) {
        val database = FirebaseDatabase.getInstance().reference

        // Listen for changes to the doctor's approval status
        database.child("doctors").child(doctorId).child("approved")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val approved = snapshot.getValue(Boolean::class.java) ?: false
                    val prefs = context.getSharedPreferences("DoctorPrefs", Context.MODE_PRIVATE)
                    val wasApproved = prefs.getBoolean("was_approved_$doctorId", false)

                    // If doctor just got approved (wasn't approved before but is now)
                    if (approved && !wasApproved) {
                        showApprovalNotification(context, doctorId)
                        // Mark as notified
                        prefs.edit().putBoolean("was_approved_$doctorId", true).apply()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    private fun showApprovalNotification(context: Context, doctorId: String) {
        createNotificationChannel(context)

        val intent = Intent(context, DoctorActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle("🎉 Registration Approved!")
            .setContentText("Congratulations! Your doctor registration has been approved. You can now login with ID: $doctorId")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Congratulations! Your doctor registration has been approved. You can now login anytime with your Doctor ID: $doctorId"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Doctor Approval Notifications"
            val descriptionText = "Notifications for doctor registration approval"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
