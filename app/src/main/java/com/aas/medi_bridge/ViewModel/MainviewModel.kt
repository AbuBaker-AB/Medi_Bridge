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

    init {
        // Add connection status logging
        android.util.Log.d("MainviewModel", "MainviewModel initialized")
        android.util.Log.d("MainviewModel", "Firebase database instance: ${firebaseDatabase}")

        // Test Firebase connection
        val connectedRef = firebaseDatabase.getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                android.util.Log.d("MainviewModel", "Firebase connected: $connected")
            }
            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MainviewModel", "Firebase connection check failed: ${error.message}")
            }
        })
    }

    fun loadCategory() {
        android.util.Log.d("MainviewModel", "Starting loadCategory()")
        val Ref = firebaseDatabase.getReference("Category")
        Ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                android.util.Log.d("MainviewModel", "Category onDataChange - exists: ${snapshot.exists()}, children: ${snapshot.childrenCount}")
                val lists = mutableListOf<CategoryModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(CategoryModel::class.java)
                    if (list != null) {
                        lists.add(list)
                        android.util.Log.d("MainviewModel", "Added category: ${list.name}")
                    }
                }

                android.util.Log.d("MainviewModel", "Setting category value with ${lists.size} items")
                _catagory.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MainviewModel", "Category database error: ${error.message}")
            }
        })
    }

    fun loadDoctors() {
        android.util.Log.d("MainviewModel", "Starting loadDoctors()")
        val Ref = firebaseDatabase.getReference("Doctors")
        Ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                android.util.Log.d("MainviewModel", "Doctors onDataChange - exists: ${snapshot.exists()}, children: ${snapshot.childrenCount}")
                val lists = mutableListOf<DoctorsModel>()
                for (childSnapshot in snapshot.children) {
                    val doctor = childSnapshot.getValue(DoctorsModel::class.java)
                    if (doctor != null) {
                        lists.add(doctor)
                        android.util.Log.d("MainviewModel", "Added doctor: ${doctor.name}")
                    }
                }

                android.util.Log.d("MainviewModel", "Setting doctors value with ${lists.size} items")
                _doctors.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MainviewModel", "Doctors database error: ${error.message}")
            }
        })
    }
}