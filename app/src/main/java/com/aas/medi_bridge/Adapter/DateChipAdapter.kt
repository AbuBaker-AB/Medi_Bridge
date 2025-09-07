package com.aas.medi_bridge.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aas.medi_bridge.databinding.ItemDateChipBinding

class DateChipAdapter(
    private val dates: List<String>,
    private val onDateSelected: (String) -> Unit
) : RecyclerView.Adapter<DateChipAdapter.DateViewHolder>() {

    private var selectedPosition = -1

    inner class DateViewHolder(private val binding: ItemDateChipBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(date: String, position: Int) {
            binding.dateText.text = date
            binding.dateText.isSelected = position == selectedPosition

            binding.root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = position

                // Refresh the previously selected item
                if (previousPosition != -1) {
                    notifyItemChanged(previousPosition)
                }
                // Refresh the newly selected item
                notifyItemChanged(selectedPosition)

                onDateSelected(date)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding = ItemDateChipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(dates[position], position)
    }

    override fun getItemCount(): Int = dates.size

    fun getSelectedDate(): String? {
        return if (selectedPosition != -1) dates[selectedPosition] else null
    }
}
