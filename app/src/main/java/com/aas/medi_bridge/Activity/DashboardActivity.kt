package com.aas.medi_bridge.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aas.medi_bridge.Adapter.AppointmentAdapter
import com.aas.medi_bridge.R
import com.aas.medi_bridge.Domain.AppointmentModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardActivity : AppCompatActivity() {

    private lateinit var welcomeText: TextView
    private lateinit var appointmentsRecyclerView: RecyclerView
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var backBtn: ImageView
    private lateinit var logoutBtn: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var appointmentAdapter: AppointmentAdapter
    private val appointments = mutableListOf<AppointmentModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        initViews()
        setupClickListeners()
        loadDoctorInfo()
        loadAppointments()

        // Apply window insets to the root view instead of non-existent "main" view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        welcomeText = findViewById(R.id.welcomeText)
        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        backBtn = findViewById(R.id.backBtn)
        logoutBtn = findViewById(R.id.logoutBtn)

        sharedPreferences = getSharedPreferences("DoctorPrefs", MODE_PRIVATE)

        // Setup RecyclerView
        appointmentsRecyclerView.layoutManager = LinearLayoutManager(this)
        appointmentAdapter = AppointmentAdapter(appointments)
        appointmentsRecyclerView.adapter = appointmentAdapter
    }

    private fun setupClickListeners() {
        // Fix back button to navigate properly
        backBtn.setOnClickListener {
            // Instead of onBackPressed, finish the activity to go back to login
            finish()
        }

        logoutBtn.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        try {
            // Clear stored doctor info first
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            // Sign out from Firebase Auth (if signed in)
            try {
                com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
            } catch (authError: Exception) {
                // Handle auth error silently
            }

            // Go back to DoctorActivity (login screen) with proper flags
            val intent = Intent(this@DashboardActivity, DoctorActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()

        } catch (e: Exception) {
            // Even if there's an error, try to go back to login
            val intent = Intent(this@DashboardActivity, DoctorActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadDoctorInfo() {
        val doctorName = sharedPreferences.getString("doctor_name", "Doctor")
        val doctorSpecialty = sharedPreferences.getString("doctor_specialty", "General Practice")
        welcomeText.text = "Welcome, Dr. $doctorName"
    }

    private fun loadAppointments() {
        val doctorName = sharedPreferences.getString("doctor_name", "")
        val doctorId = sharedPreferences.getString("doctor_id", "")
        val doctorEmail = sharedPreferences.getString("doctor_email", "")

        if (doctorName.isNullOrEmpty() && doctorId.isNullOrEmpty() && doctorEmail.isNullOrEmpty()) {
            showEmptyState()
            return
        }

        val database = FirebaseDatabase.getInstance()
        val appointmentsRef = database.getReference("appointments")

        appointmentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                appointments.clear()

                for (appointmentSnapshot in snapshot.children) {
                    try {
                        val appointment = appointmentSnapshot.getValue(AppointmentModel::class.java)
                        if (appointment != null) {
                            // Filter by doctor name, doctor ID, or doctor email
                            val matchesByName = appointment.doctorName.equals(doctorName, ignoreCase = true)
                            val matchesById = appointment.doctorId == doctorId
                            val matchesByEmail = appointment.doctorEmail.equals(doctorEmail, ignoreCase = true)

                            if (matchesByName || matchesById || matchesByEmail) {
                                // Filter appointments for the current week
                                if (isAppointmentInCurrentWeek(appointment.appointmentDate)) {
                                    appointments.add(appointment)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // Handle parsing error silently
                    }
                }

                // Sort appointments by date and time
                appointments.sortWith { a1, a2 ->
                    when {
                        a1.appointmentDate != a2.appointmentDate -> a1.appointmentDate.compareTo(a2.appointmentDate)
                        else -> a1.appointmentTime.compareTo(a2.appointmentTime)
                    }
                }

                runOnUiThread {
                    if (appointments.isEmpty()) {
                        showEmptyState()
                    } else {
                        showAppointments()
                    }
                    appointmentAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                runOnUiThread {
                    showEmptyState()
                }
            }
        })
    }

    private fun isAppointmentInCurrentWeek(appointmentDate: String): Boolean {
        try {
            // Simple week check - can be enhanced with proper date parsing
            val currentTimeMillis = System.currentTimeMillis()
            val oneWeekInMillis = 7 * 24 * 60 * 60 * 1000L

            // For now, show all appointments (can be refined based on date format)
            // This assumes appointments are for the current week by default
            return true
        } catch (e: Exception) {
            return true // Show all appointments if date parsing fails
        }
    }

    private fun showEmptyState() {
        appointmentsRecyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.VISIBLE
    }

    private fun showAppointments() {
        appointmentsRecyclerView.visibility = View.VISIBLE
        emptyStateLayout.visibility = View.GONE
    }
}