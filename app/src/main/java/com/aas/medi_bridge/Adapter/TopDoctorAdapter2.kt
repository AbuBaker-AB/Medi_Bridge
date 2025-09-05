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
import com.aas.medi_bridge.databinding.ViewholderTopDoctorBinding
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
        holder.binding.nameTxt.text = items[position].name
        holder.binding.specialTxt.text = items[position].specialization
        holder.binding.scoreTxt.text = items[position].rating.toString()
        holder.binding.ratingBar.rating = items[position].rating.toFloat()
        holder.binding.scoreTxt.text = items[position].rating.toString()
        holder.binding.degreeTxt.text = "Professional Doctor"

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
            .transform(CenterCrop(), RoundedCorners(16)) // CenterCrop and rounded corners
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