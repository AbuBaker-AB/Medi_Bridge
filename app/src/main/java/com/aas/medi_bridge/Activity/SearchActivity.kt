package com.aas.medi_bridge.Activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aas.medi_bridge.Adapter.TopDoctorAdapter
import com.aas.medi_bridge.Domain.DoctorsModel
import com.aas.medi_bridge.ViewModel.MainviewModel
import com.aas.medi_bridge.databinding.ActivitySearchBinding

class SearchActivity : BaseActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel = MainviewModel()
    private var allDoctors: MutableList<DoctorsModel> = mutableListOf()
    private lateinit var searchResultsAdapter: TopDoctorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSearch()
        loadDoctors()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        searchResultsAdapter = TopDoctorAdapter(mutableListOf())
        binding.searchResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.searchResultsRecyclerView.adapter = searchResultsAdapter
    }

    private fun initSearch() {
        // Set up search functionality - search immediately as user types
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Back button functionality
        binding.backBtn.setOnClickListener {
            finish()
        }

        // Auto-focus on search field when activity opens
        binding.searchEditText.requestFocus()
    }

    private fun loadDoctors() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.doctors.observe(this) { doctorsList ->
            allDoctors = doctorsList
            binding.progressBar.visibility = View.GONE
        }
        viewModel.loadDoctors()
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            // Show hint and hide results
            binding.searchHintText.visibility = View.VISIBLE
            binding.searchResultsRecyclerView.visibility = View.GONE
            binding.searchHintText.text = "Search by doctor name, specialization, designation, or hospital name"
            return
        }

        // Search across ALL relevant fields - not just name
        val matchingDoctors = allDoctors.filter { doctor ->
            val hospitalName = if (doctor.chambers.isNotEmpty()) {
                doctor.chambers[0].name
            } else {
                ""
            }

            doctor.name.contains(query, ignoreCase = true) ||
            doctor.specialization.contains(query, ignoreCase = true) ||
            doctor.designation.contains(query, ignoreCase = true) ||
            hospitalName.contains(query, ignoreCase = true) ||
            doctor.address.contains(query, ignoreCase = true) ||
            doctor.location.contains(query, ignoreCase = true)
        }

        // Show results or no results message
        if (matchingDoctors.isEmpty()) {
            binding.searchHintText.visibility = View.VISIBLE
            binding.searchResultsRecyclerView.visibility = View.GONE
            binding.searchHintText.text = "No doctors found for \"$query\""
        } else {
            // Show the list of matching doctors
            binding.searchHintText.visibility = View.GONE
            binding.searchResultsRecyclerView.visibility = View.VISIBLE

            searchResultsAdapter.apply {
                items.clear()
                items.addAll(matchingDoctors)
                notifyDataSetChanged()
            }
        }
    }
}
