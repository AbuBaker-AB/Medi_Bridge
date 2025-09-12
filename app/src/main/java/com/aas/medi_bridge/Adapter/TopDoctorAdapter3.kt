package com.aas.medi_bridge.Adapter

import android.R
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aas.medi_bridge.Activity.DetailActivity
import com.aas.medi_bridge.Domain.DoctorsModel
import com.aas.medi_bridge.databinding.ViewholderTopDoctor3Binding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TopDoctorAdapter3(val items: MutableList<DoctorsModel>): RecyclerView.Adapter<TopDoctorAdapter3.ViewHolder>() {
    private var context: Context?=null

    class ViewHolder(val binding: ViewholderTopDoctor3Binding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context=parent.context
        val binding=
            ViewholderTopDoctor3Binding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.nameTxt.text = items[position].name
        holder.binding.specialTxt.text = items[position].specialization

        // Set hospital name from first chamber or fallback
        val hospitalName = if (items[position].chambers.isNotEmpty()) {
            items[position].chambers[0].name
        } else {
            "Not Available"
        }
        holder.binding.hospitalNameTxt.text = hospitalName

        // Debug logging to check if chambers data is being loaded correctly
        android.util.Log.d("TopDoctorAdapter3", "Doctor: ${items[position].name}")
        android.util.Log.d("TopDoctorAdapter3", "Chambers count: ${items[position].chambers.size}")
        if (items[position].chambers.isNotEmpty()) {
            android.util.Log.d("TopDoctorAdapter3", "Hospital name: ${items[position].chambers[0].name}")
        } else {
            android.util.Log.d("TopDoctorAdapter3", "No chambers found for doctor: ${items[position].name}")
        }

        // Get doctor image or fallback to first chamber image
        var imageUrl = items[position].image
        if (imageUrl.isBlank() && items[position].chambers.isNotEmpty()) {
            imageUrl = items[position].chambers[0].image
        }

        // Convert Imgur URL to direct image URL if needed
        if (imageUrl.isNotBlank() && imageUrl.contains("imgur.com") && !imageUrl.contains("i.imgur.com")) {
            val imageId = imageUrl.substringAfterLast("/")
            imageUrl = "https://i.imgur.com/$imageId.jpg"
        }

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .transform(CenterCrop(), RoundedCorners(16))
            .placeholder(R.drawable.ic_menu_gallery)
            .error(R.drawable.ic_menu_gallery)
            .into(holder.binding.img)

        holder.binding.makeBtn.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("Object", items[position])
            context?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

}
