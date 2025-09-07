package com.aas.medi_bridge.Adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aas.medi_bridge.Domain.CategoryModel
import com.aas.medi_bridge.databinding.ViewholderCategoryBinding
import android.content.Context
import android.view.LayoutInflater
import com.bumptech.glide.Glide

class CategoryAdapter(
    val items: MutableList<CategoryModel>,
    private val onItemClick: ((CategoryModel) -> Unit)? = null
):
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private lateinit var context: Context
    inner class ViewHolder(val binding: ViewholderCategoryBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryAdapter.ViewHolder {
        context=parent.context
        val binding= ViewholderCategoryBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.titleTxt.text=item.Name

        // Convert Imgur URL to direct image URL
        val imageUrl = if (item.Picture.contains("imgur.com") && !item.Picture.contains("i.imgur.com")) {
            // Convert imgur.com/abc to i.imgur.com/abc.jpg
            val imageId = item.Picture.substringAfterLast("/")
            "https://i.imgur.com/$imageId.jpg"
        } else {
            item.Picture
        }

        Glide.with(context)
            .load(imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery) // Show placeholder while loading
            .error(android.R.drawable.ic_menu_gallery) // Show error image if loading fails
            .into(holder.binding.img)
        // Add click listener
        holder.binding.root.setOnClickListener {
            onItemClick?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size // Return the size of the items list
    }

}