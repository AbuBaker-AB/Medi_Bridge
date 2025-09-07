package com.aas.medi_bridge.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aas.medi_bridge.Adapter.CategoryAdapter
import com.aas.medi_bridge.Adapter.TopDoctorAdapter
import com.aas.medi_bridge.Domain.DoctorsModel
import com.aas.medi_bridge.ViewModel.MainviewModel
import com.aas.medi_bridge.databinding.ActivityMainBinding


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel = MainviewModel()

    private var allDoctors: MutableList<DoctorsModel> = mutableListOf()
    private lateinit var doctorAdapter: TopDoctorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intCategroy()
        initDoctor()
        setupBottomNavigation()
    }

    private fun initDoctor() {
        android.util.Log.d("MainActivity", "=== STARTING DOCTOR INITIALIZATION ===")
        binding.progressBarTopDoctor.visibility = View.VISIBLE
        viewModel.doctors.observe(this) { doctorsList ->
            android.util.Log.d("MainActivity", "Doctors LiveData changed. Data received: ${doctorsList != null}")
            android.util.Log.d("MainActivity", "Doctors count: ${doctorsList?.size ?: 0}")

            try {
                if (doctorsList != null && doctorsList.isNotEmpty()) {
                    allDoctors = doctorsList
                    // Show only first 4 doctors in the main screen
                    val limitedDoctors = allDoctors.take(6).toMutableList()
                    android.util.Log.d("MainActivity", "Creating adapter with ${limitedDoctors.size} doctors")
                    doctorAdapter = TopDoctorAdapter(limitedDoctors)
                    binding.recyclerViewTopDoctor.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                    binding.recyclerViewTopDoctor.adapter = doctorAdapter
                    android.util.Log.d("MainActivity", "Doctor adapter set successfully")
                } else {
                    allDoctors = mutableListOf()
                    android.util.Log.w("MainActivity", "No doctors data available - showing empty state")
                }
                binding.progressBarTopDoctor.visibility = View.GONE
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Error initializing doctors: ${e.message}")
                binding.progressBarTopDoctor.visibility = View.GONE
            }
        }

        android.util.Log.d("MainActivity", "Calling viewModel.loadDoctors()")
        viewModel.loadDoctors()

        binding.doctorListTxt.setOnClickListener {
            val intent = Intent(this, TopDoctorsActivity::class.java)
            startActivity(intent)
        }

        // Search functionality moved to SearchActivity via bottom navigation
    }

    private fun intCategroy() {
        android.util.Log.d("MainActivity", "=== STARTING CATEGORY INITIALIZATION ===")
        binding.progressBarCategory.visibility = View.VISIBLE
        viewModel.category.observe(this) { categoryList ->
            android.util.Log.d("MainActivity", "Categories LiveData changed. Data received: ${categoryList != null}")
            android.util.Log.d("MainActivity", "Categories count: ${categoryList?.size ?: 0}")

            try {
                if (categoryList != null && categoryList.isNotEmpty()) {
                    binding.viewCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                    binding.viewCategory.adapter = CategoryAdapter(categoryList.toMutableList()) { category ->
                        // On specialization click, start DoctorListActivity
                        val intent = Intent(this, DoctorListActivity::class.java)
                        intent.putExtra("specialization", category.name)
                        startActivity(intent)
                    }
                    android.util.Log.d("MainActivity", "Category adapter set successfully")
                } else {
                    android.util.Log.w("MainActivity", "No categories data available - showing empty state")
                }
                binding.progressBarCategory.visibility = View.GONE
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Error initializing categories: ${e.message}")
                binding.progressBarCategory.visibility = View.GONE
            }
        }

        android.util.Log.d("MainActivity", "Calling viewModel.loadCategory()")
        viewModel.loadCategory()
    }

    private fun setupBottomNavigation() {
        // Set click listener for search button using the ID we added
        binding.searchLayout.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
    }
}