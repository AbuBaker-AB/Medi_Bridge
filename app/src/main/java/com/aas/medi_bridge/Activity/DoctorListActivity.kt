package com.aas.medi_bridge.Activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aas.medi_bridge.Adapter.TopDoctorAdapter3
import com.aas.medi_bridge.ViewModel.MainviewModel
import com.aas.medi_bridge.databinding.ActivityDoctorListBinding

class DoctorListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDoctorListBinding
    private val viewModel = MainviewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val specialization = intent.getStringExtra("specialization") ?: ""
        binding.doctorListTitle.text = specialization
        Log.d("DoctorListActivity", "Specialization received: $specialization")

        // Back button functionality
        binding.backBtn.setOnClickListener {
            finish()
        }

        // Observe doctors from ViewModel
        viewModel.doctors.observe(this) { allDoctors ->
            Log.d("DoctorListActivity", "Total doctors loaded: ${allDoctors.size}")

            // Debug: Print all specializations in the loaded doctor list
            val allSpecs = allDoctors.map { it.specialization.trim() }.distinct().joinToString()
            Log.d("DoctorListActivity", "All specializations in DB: $allSpecs")

            // Use robust filtering: trim and contains (case-insensitive)
            val filtered = allDoctors.filter {
                it.specialization.trim().equals(specialization.trim(), ignoreCase = true) ||
                it.specialization.trim().contains(specialization.trim(), ignoreCase = true) ||
                specialization.trim().contains(it.specialization.trim(), ignoreCase = true)
            }
            Log.d("DoctorListActivity", "Filtered doctors count: ${filtered.size}")
            if (filtered.isEmpty()) {
                binding.emptyStateTxt.visibility = android.view.View.VISIBLE
            } else {
                binding.emptyStateTxt.visibility = android.view.View.GONE
            }

            // Use TopDoctorAdapter3 for the new design with hospital names
            val adapter = TopDoctorAdapter3(filtered.toMutableList())
            binding.doctorRecyclerView.layoutManager = LinearLayoutManager(this)
            binding.doctorRecyclerView.adapter = adapter
        }
        viewModel.loadDoctors()
    }
}
