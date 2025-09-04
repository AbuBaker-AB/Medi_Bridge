package com.aas.medi_bridge.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aas.medi_bridge.Domain.DoctorsModel
import com.aas.medi_bridge.databinding.ViewholderTopDoctorBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions

class TopDoctorAdapter(val items: MutableList<DoctorsModel>): RecyclerView.Adapter<TopDoctorAdapter.ViewHolder>() {
    private var context: Context?=null

    class ViewHolder(val binding: ViewholderTopDoctorBinding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context=parent.context
        val binding=
            ViewholderTopDoctorBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.nameTxt.text = items[position].name
        holder.binding.specialTxt.text = items[position].specialization
        holder.binding.scoreTxt.text = items[position].rating.toString()

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
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_gallery)
            .apply(RequestOptions().transform(CenterCrop()))
            .into(holder.binding.imageview6)

    }

    override fun getItemCount(): Int = items.size

}