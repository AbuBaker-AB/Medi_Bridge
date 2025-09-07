package com.aas.medi_bridge.Domain

import android.os.Parcel
import android.os.Parcelable

data class DoctorModel(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val specialization: String = "",
    val experience: Int = 0,
    val isRegistered: Boolean = false
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(phone)
        parcel.writeString(specialization)
        parcel.writeInt(experience)
        parcel.writeByte(if (isRegistered) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DoctorModel> {
        override fun createFromParcel(parcel: Parcel): DoctorModel {
            return DoctorModel(parcel)
        }

        override fun newArray(size: Int): Array<DoctorModel?> {
            return arrayOfNulls(size)
        }
    }
}

data class AppointmentModel(
    val id: String = "",
    val patientName: String = "",
    val patientPhone: String = "",
    val appointmentDate: String = "",
    val appointmentTime: String = "",
    val doctorId: String = "",
    val status: String = "scheduled", // scheduled, completed, cancelled
    val symptoms: String = ""
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(patientName)
        parcel.writeString(patientPhone)
        parcel.writeString(appointmentDate)
        parcel.writeString(appointmentTime)
        parcel.writeString(doctorId)
        parcel.writeString(status)
        parcel.writeString(symptoms)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AppointmentModel> {
        override fun createFromParcel(parcel: Parcel): AppointmentModel {
            return AppointmentModel(parcel)
        }

        override fun newArray(size: Int): Array<AppointmentModel?> {
            return arrayOfNulls(size)
        }
    }
}
