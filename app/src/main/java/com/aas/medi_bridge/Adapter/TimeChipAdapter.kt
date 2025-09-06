package com.aas.medi_bridge.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aas.medi_bridge.databinding.ItemTimeChipBinding

class TimeChipAdapter(
    private val times: List<String>,
    private val onTimeSelected: (String) -> Unit
) : RecyclerView.Adapter<TimeChipAdapter.TimeViewHolder>() {

    private var selectedPosition = -1

    inner class TimeViewHolder(private val binding: ItemTimeChipBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(time: String, position: Int) {
            binding.timeText.text = time
            binding.timeText.isSelected = position == selectedPosition

            binding.root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = position

                // Refresh the previously selected item
                if (previousPosition != -1) {
                    notifyItemChanged(previousPosition)
                }
                // Refresh the newly selected item
                notifyItemChanged(selectedPosition)

                onTimeSelected(time)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val binding = ItemTimeChipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        holder.bind(times[position], position)
    }

    override fun getItemCount(): Int = times.size

    fun getSelectedTime(): String? {
        return if (selectedPosition != -1) times[selectedPosition] else null
    }
}
