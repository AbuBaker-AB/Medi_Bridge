package com.aas.medi_bridge.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aas.medi_bridge.Domain.CategoryModel
import com.aas.medi_bridge.Domain.DoctorsModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainviewModel(): ViewModel() {

    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private val _catagory = MutableLiveData<MutableList<CategoryModel>>()
    private val _doctors = MutableLiveData<MutableList<DoctorsModel>>()


    val category: LiveData<MutableList<CategoryModel>> = _catagory
    val doctors: LiveData<MutableList<DoctorsModel>> = _doctors


    fun loadCategory() {
        val Ref = firebaseDatabase.getReference("Category")
        Ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<CategoryModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(CategoryModel::class.java)
                    if (list != null) {
                        // Map Picture property to drawable resource IDs
                        val updatedList = list.copy(
                            Picture = list.Picture // Use the Picture field directly as a URL
                        )
                        lists.add(updatedList)
                    }
                }


                _catagory.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error appropriately instead of crashing
                android.util.Log.e("MainviewModel", "Database error: ${error.message}")
            }

        })


    }

    fun loadDoctors() {
        val Ref = firebaseDatabase.getReference("Doctors")
        Ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<DoctorsModel>()
                for (childSnapshot in snapshot.children) {
                    val doctor = childSnapshot.getValue(DoctorsModel::class.java)
                    if (doctor != null) {
                        // No need to copy - the model already matches the JSON structure
                        lists.add(doctor)
                    }
                }

                _doctors.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error appropriately instead of crashing
                android.util.Log.e("MainviewModel", "Database error: ${error.message}")
            }
        })
    }
}