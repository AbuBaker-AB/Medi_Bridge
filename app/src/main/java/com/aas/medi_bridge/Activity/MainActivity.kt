package com.aas.medi_bridge.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aas.medi_bridge.Adapter.CategoryAdapter
import com.aas.medi_bridge.Adapter.TopDoctorAdapter
import com.aas.medi_bridge.Domain.DoctorsModel
import com.aas.medi_bridge.R
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
        binding.progressBarTopDoctor.visibility = View.VISIBLE
        viewModel.doctors.observe(this, { doctorsList ->
            allDoctors = doctorsList
            // Show only first 4 doctors in the main screen
            val limitedDoctors = allDoctors.take(4).toMutableList()
            doctorAdapter = TopDoctorAdapter(limitedDoctors)
            binding.recyclerViewTopDoctor.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            binding.recyclerViewTopDoctor.adapter = doctorAdapter
            binding.progressBarTopDoctor.visibility = View.GONE
        })
        viewModel.loadDoctors()

        binding.doctorListTxt.setOnClickListener {
            val intent = Intent(this, TopDoctorsActivity::class.java)
            startActivity(intent)
        }

        // Search functionality moved to SearchActivity via bottom navigation
    }

    private fun intCategroy() {
        binding.progressBarCategory.visibility = View.VISIBLE
        viewModel.category.observe(this,{
            binding.viewCategory.layoutManager= LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
            binding.viewCategory.adapter= CategoryAdapter(it)
            binding.progressBarCategory.visibility = View.GONE

        })
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