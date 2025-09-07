package com.aas.medi_bridge.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aas.medi_bridge.R

class DashboardActivity : AppCompatActivity() {

    private lateinit var welcomeText: TextView
    private lateinit var appointmentsRecyclerView: RecyclerView
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var backBtn: ImageView
    private lateinit var logoutBtn: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    }

    private fun setupClickListeners() {
        backBtn.setOnClickListener { finish() }

        logoutBtn.setOnClickListener {
            // Sign out from Firebase Auth
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
            // Clear stored doctor info
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            // Go back to login activity
            val intent = Intent(this@DashboardActivity, DoctorActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadDoctorInfo() {
        // Load doctor info in background to avoid blocking UI
        Thread {
            val doctorName = sharedPreferences.getString("doctor_name", "Doctor")
            val doctorSpecialty = sharedPreferences.getString("doctor_specialty", "General Practice")

            // Update UI on main thread
            runOnUiThread {
                welcomeText.text = "Welcome, Dr. $doctorName"
            }
        }.start()
    }

    private fun loadAppointments() {
        // For now, show empty state
        // In the future, this will load appointments from Firebase
        val appointments = ArrayList<String>()

        if (appointments.isEmpty()) {
            appointmentsRecyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
        } else {
            appointmentsRecyclerView.visibility = View.VISIBLE
            emptyStateLayout.visibility = View.GONE
            // Setup adapter here when appointments are available
        }
    }
}