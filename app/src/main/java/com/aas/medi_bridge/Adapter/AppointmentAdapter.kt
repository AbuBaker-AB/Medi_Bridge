package com.aas.medi_bridge.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aas.medi_bridge.Domain.AppointmentModel
import com.aas.medi_bridge.R

class AppointmentAdapter(val appointments: MutableList<AppointmentModel>) :
    RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.bind(appointment)
    }

    override fun getItemCount(): Int = appointments.size

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val patientNameTxt: TextView = itemView.findViewById(R.id.patientNameTxt)
        private val appointmentDateTxt: TextView = itemView.findViewById(R.id.appointmentDateTxt)
        private val appointmentTimeTxt: TextView = itemView.findViewById(R.id.appointmentTimeTxt)
        private val patientPhoneTxt: TextView = itemView.findViewById(R.id.patientPhoneTxt)
        private val symptomsTxt: TextView = itemView.findViewById(R.id.symptomsTxt)
        private val statusTxt: TextView = itemView.findViewById(R.id.statusTxt)

        fun bind(appointment: AppointmentModel) {
            patientNameTxt.text = appointment.patientName
            appointmentDateTxt.text = appointment.appointmentDate
            appointmentTimeTxt.text = appointment.appointmentTime
            patientPhoneTxt.text = appointment.patientPhone
            symptomsTxt.text = if (appointment.symptoms.isNotBlank()) appointment.symptoms else "No symptoms provided"
            statusTxt.text = appointment.status.capitalize()

            // Set status color based on appointment status
            when (appointment.status.lowercase()) {
                "scheduled" -> statusTxt.setTextColor(itemView.context.getColor(R.color.purple))
                "completed" -> statusTxt.setTextColor(itemView.context.getColor(R.color.darkgrey))
                "cancelled" -> statusTxt.setTextColor(android.graphics.Color.RED)
            }
        }
    }
}
