package com.aas.medi_bridge.Adapter

import android.R
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aas.medi_bridge.Activity.DetailActivity
import com.aas.medi_bridge.Domain.DoctorsModel
import com.aas.medi_bridge.databinding.ViewholderTopDoctor2Binding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class TopDoctorAdapter2(val items: MutableList<DoctorsModel>): RecyclerView.Adapter<TopDoctorAdapter2.ViewHolder>() {
    private var context: Context?=null

    class ViewHolder(val binding: ViewholderTopDoctor2Binding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context=parent.context
        val binding=
            ViewholderTopDoctor2Binding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val doctor = items[position]

            // Safely bind text fields with null checks
            holder.binding.nameTxt.text = doctor.name.takeIf { it.isNotBlank() } ?: "Unknown Doctor"
            holder.binding.specialTxt.text = doctor.specialization.takeIf { it.isNotBlank() } ?: "General Practice"
            holder.binding.scoreTxt.text = if (doctor.rating > 0.0) doctor.rating.toString() else "N/A"
            holder.binding.ratingBar.rating = doctor.rating.toFloat()
            holder.binding.degreeTxt.text = doctor.degrees.takeIf { it.isNotBlank() } ?: "Professional Doctor"

            // Safely get doctor image with proper fallback chain
            var imageUrl = ""
            when {
                doctor.image.isNotBlank() -> imageUrl = doctor.image
                doctor.chambers.isNotEmpty() && doctor.chambers[0].image.isNotBlank() -> imageUrl = doctor.chambers[0].image
                else -> imageUrl = "" // Will use placeholder
            }

            // Convert Imgur URL to direct image URL if needed
            if (imageUrl.isNotBlank() && imageUrl.contains("imgur.com") && !imageUrl.contains("i.imgur.com")) {
                val imageId = imageUrl.substringAfterLast("/")
                imageUrl = "https://i.imgur.com/$imageId.jpg"
            }

            // Load image with better error handling
            Glide.with(holder.itemView.context)
                .load(imageUrl.takeIf { it.isNotBlank() })
                .transform(CenterCrop(), RoundedCorners(16))
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(holder.binding.img)

            holder.binding.makeBtn.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("Object", doctor)
                context?.startActivity(intent)
            }
        } catch (e: Exception) {
            android.util.Log.e("TopDoctorAdapter2", "Error binding doctor at position $position: ${e.message}")
            // Set fallback values to prevent blank items
            holder.binding.nameTxt.text = "Error Loading Doctor"
            holder.binding.specialTxt.text = "Please try again"
            holder.binding.scoreTxt.text = "N/A"
            holder.binding.ratingBar.rating = 0f
            holder.binding.degreeTxt.text = "Please refresh"
        }
    }

    override fun getItemCount(): Int = items.size

}