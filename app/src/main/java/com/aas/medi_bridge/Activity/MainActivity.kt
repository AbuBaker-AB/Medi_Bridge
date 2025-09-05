package com.aas.medi_bridge.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aas.medi_bridge.Adapter.CategoryAdapter
import com.aas.medi_bridge.Adapter.TopDoctorAdapter
import com.aas.medi_bridge.Domain.CategoryModel
import com.aas.medi_bridge.R
import com.aas.medi_bridge.ViewModel.MainviewModel
import com.aas.medi_bridge.databinding.ActivityMainBinding


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel = MainviewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intCategroy()
        initDoctor()

    }

    private fun initDoctor() {
        binding.progressBarTopDoctor.visibility = View.VISIBLE
        viewModel.doctors.observe(this,{
            binding.recyclerViewTopDoctor.layoutManager= LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL,false)
            binding.recyclerViewTopDoctor.adapter= TopDoctorAdapter(it)
            binding.progressBarTopDoctor.visibility= View.GONE
        })
        viewModel.loadDoctors()

        binding.doctorListTxt.setOnClickListener {
            val intent = Intent(this, TopDoctorsActivity::class.java)
            startActivity(intent)
        }

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
}