package com.aas.medi_bridge.Activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.aas.medi_bridge.Adapter.AppointmentAdapter
import com.aas.medi_bridge.Domain.AppointmentModel
import com.aas.medi_bridge.Domain.DoctorModel
import com.aas.medi_bridge.databinding.ActivityDoctorBinding
import java.text.SimpleDateFormat
import java.util.*

class DoctorActivity : BaseActivity() {

    private lateinit var binding: ActivityDoctorBinding
    private var isLoginMode = true
    private lateinit var sharedPreferences: SharedPreferences
    private var currentDoctor: DoctorModel? = null
    private lateinit var appointmentAdapter: AppointmentAdapter
    private val appointments = mutableListOf<AppointmentModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("DoctorPrefs", Context.MODE_PRIVATE)

        setupUI()
        checkLoginStatus()
        loadSampleAppointments()
    }

    private fun setupUI() {
        binding.apply {
            // Back button
            backBtn.setOnClickListener { finish() }

            // Switch between login and signup modes
            switchModeText.setOnClickListener {
                toggleAuthMode()
            }

            // Primary action button (Login/Signup)
            primaryActionBtn.setOnClickListener {
                if (isLoginMode) {
                    performLogin()
                } else {
                    performSignup()
                }
            }

            // Logout button
            logoutBtn.setOnClickListener {
                performLogout()
            }
        }

        setupAppointmentsRecyclerView()
    }

    private fun setupAppointmentsRecyclerView() {
        appointmentAdapter = AppointmentAdapter(appointments)
        binding.appointmentsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.appointmentsRecyclerView.adapter = appointmentAdapter
    }

    private fun checkLoginStatus() {
        val doctorId = sharedPreferences.getString("doctor_id", null)
        val doctorName = sharedPreferences.getString("doctor_name", null)

        if (!doctorId.isNullOrBlank() && !doctorName.isNullOrBlank()) {
            // Doctor is logged in
            currentDoctor = DoctorModel(
                id = doctorId,
                name = doctorName,
                email = sharedPreferences.getString("doctor_email", "") ?: "",
                specialization = sharedPreferences.getString("doctor_specialization", "") ?: "",
                isRegistered = true
            )
            showDashboard()
        } else {
            showAuthForm()
        }
    }

    private fun toggleAuthMode() {
        isLoginMode = !isLoginMode

        binding.apply {
            if (isLoginMode) {
                // Switch to Login mode
                formTitle.text = "Login to Your Account"
                formSubtitle.text = "Enter your credentials to access doctor portal"
                loginFieldsContainer.visibility = View.VISIBLE
                signupFieldsContainer.visibility = View.GONE
                primaryActionBtn.text = "Login"
                switchModeText.text = "Don't have an account? Sign Up"
            } else {
                // Switch to Signup mode
                formTitle.text = "Create Doctor Account"
                formSubtitle.text = "Fill in your details to register as a doctor"
                loginFieldsContainer.visibility = View.VISIBLE
                signupFieldsContainer.visibility = View.VISIBLE
                primaryActionBtn.text = "Sign Up"
                switchModeText.text = "Already have an account? Login"
            }
        }
    }

    private fun performLogin() {
        val name = binding.loginNameEditText.text.toString().trim()
        val doctorId = binding.loginIdEditText.text.toString().trim()

        if (name.isEmpty() || doctorId.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if doctor is registered (simulate database check)
        val isRegistered = checkDoctorRegistration(name, doctorId)

        if (isRegistered) {
            // Login successful
            val doctor = DoctorModel(
                id = doctorId,
                name = name,
                email = "dr.${name.replace(" ", "").lowercase()}@medibbridge.com",
                specialization = "General Medicine", // Default or fetch from database
                isRegistered = true
            )

            saveLoginSession(doctor)
            currentDoctor = doctor
            showDashboard()
            Toast.makeText(this, "Login successful! Welcome, Dr. $name", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Doctor not found. Please sign up first.", Toast.LENGTH_SHORT).show()
            toggleAuthMode() // Switch to signup mode
        }
    }

    private fun performSignup() {
        val name = binding.loginNameEditText.text.toString().trim()
        val doctorId = binding.loginIdEditText.text.toString().trim()
        val email = binding.signupEmailEditText.text.toString().trim()
        val phone = binding.signupPhoneEditText.text.toString().trim()
        val specialization = binding.signupSpecializationEditText.text.toString().trim()
        val experience = binding.signupExperienceEditText.text.toString().trim()

        if (name.isEmpty() || doctorId.isEmpty() || email.isEmpty() ||
            phone.isEmpty() || specialization.isEmpty() || experience.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        // Create new doctor account
        val doctor = DoctorModel(
            id = doctorId,
            name = name,
            email = email,
            phone = phone,
            specialization = specialization,
            experience = experience.toIntOrNull() ?: 0,
            isRegistered = true
        )

        // Save doctor registration (simulate database save)
        saveRegistration(doctor)
        saveLoginSession(doctor)
        currentDoctor = doctor
        showDashboard()
        Toast.makeText(this, "Registration successful! Welcome, Dr. $name", Toast.LENGTH_SHORT).show()
    }

    private fun checkDoctorRegistration(name: String, doctorId: String): Boolean {
        // Simulate checking if doctor is registered
        // In a real app, this would check Firebase/database
        val savedDoctorId = sharedPreferences.getString("registered_doctor_id", null)
        val savedDoctorName = sharedPreferences.getString("registered_doctor_name", null)

        return savedDoctorId == doctorId && savedDoctorName == name
    }

    private fun saveRegistration(doctor: DoctorModel) {
        // Save registration data (simulate database save)
        sharedPreferences.edit().apply {
            putString("registered_doctor_id", doctor.id)
            putString("registered_doctor_name", doctor.name)
            putString("registered_doctor_email", doctor.email)
            putString("registered_doctor_phone", doctor.phone)
            putString("registered_doctor_specialization", doctor.specialization)
            putInt("registered_doctor_experience", doctor.experience)
            apply()
        }
    }

    private fun saveLoginSession(doctor: DoctorModel) {
        // Save login session
        sharedPreferences.edit().apply {
            putString("doctor_id", doctor.id)
            putString("doctor_name", doctor.name)
            putString("doctor_email", doctor.email)
            putString("doctor_specialization", doctor.specialization)
            apply()
        }
    }

    private fun performLogout() {
        // Clear login session
        sharedPreferences.edit().clear().apply()
        currentDoctor = null

        // Reset UI to login form
        showAuthForm()
        clearAllFields()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }

    private fun showAuthForm() {
        binding.apply {
            loginContainer.visibility = View.VISIBLE
            dashboardContainer.visibility = View.GONE
        }
    }

    private fun showDashboard() {
        binding.apply {
            loginContainer.visibility = View.GONE
            dashboardContainer.visibility = View.VISIBLE

            currentDoctor?.let { doctor ->
                welcomeText.text = "Welcome, Dr. ${doctor.name}"
            }
        }

        loadAppointments()
    }

    private fun clearAllFields() {
        binding.apply {
            loginNameEditText.text?.clear()
            loginIdEditText.text?.clear()
            signupEmailEditText.text?.clear()
            signupPhoneEditText.text?.clear()
            signupSpecializationEditText.text?.clear()
            signupExperienceEditText.text?.clear()
        }
    }

    private fun loadSampleAppointments() {
        // Create sample appointments for demo purposes
        val sampleAppointments = listOf(
            AppointmentModel(
                id = "1",
                patientName = "John Smith",
                patientPhone = "+1234567890",
                appointmentDate = "2025-09-08",
                appointmentTime = "10:00 AM",
                doctorId = "DOC001",
                status = "scheduled",
                symptoms = "Fever and headache for 2 days"
            ),
            AppointmentModel(
                id = "2",
                patientName = "Sarah Johnson",
                patientPhone = "+1234567891",
                appointmentDate = "2025-09-09",
                appointmentTime = "2:30 PM",
                doctorId = "DOC001",
                status = "scheduled",
                symptoms = "Regular checkup and blood pressure monitoring"
            ),
            AppointmentModel(
                id = "3",
                patientName = "Mike Wilson",
                patientPhone = "+1234567892",
                appointmentDate = "2025-09-10",
                appointmentTime = "11:15 AM",
                doctorId = "DOC001",
                status = "completed",
                symptoms = "Back pain and muscle stiffness"
            )
        )

        appointments.clear()
        appointments.addAll(sampleAppointments)
    }

    private fun loadAppointments() {
        // Filter appointments for current week and current doctor
        val currentWeekAppointments = appointments.filter { appointment ->
            appointment.doctorId == currentDoctor?.id && isCurrentWeek(appointment.appointmentDate)
        }

        if (currentWeekAppointments.isEmpty()) {
            binding.appointmentsRecyclerView.visibility = View.GONE
            binding.noAppointmentsCard.visibility = View.VISIBLE
        } else {
            binding.appointmentsRecyclerView.visibility = View.VISIBLE
            binding.noAppointmentsCard.visibility = View.GONE

            appointmentAdapter.appointments.clear()
            appointmentAdapter.appointments.addAll(currentWeekAppointments)
            appointmentAdapter.notifyDataSetChanged()
        }
    }

    private fun isCurrentWeek(dateString: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val appointmentDate = sdf.parse(dateString)
            val currentDate = Date()

            val calendar = Calendar.getInstance()
            calendar.time = currentDate
            calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            val weekStart = calendar.time

            calendar.add(Calendar.DAY_OF_YEAR, 6)
            val weekEnd = calendar.time

            appointmentDate != null && appointmentDate.after(weekStart) && appointmentDate.before(weekEnd)
        } catch (e: Exception) {
            false
        }
    }
}