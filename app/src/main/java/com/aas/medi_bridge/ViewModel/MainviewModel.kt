package com.aas.medi_bridge.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aas.medi_bridge.Domain.CategoryModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainviewModel(): ViewModel() {

    private val firebaseDatabase= FirebaseDatabase.getInstance()

    private val _catagory= MutableLiveData<MutableList<CategoryModel>>()

    val category: LiveData<MutableList<CategoryModel>> = _catagory

    fun loadCategory(){
        val Ref=firebaseDatabase.getReference("Category")
        Ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<CategoryModel>()
                for (childSnapshot in snapshot.children){
                    val list= childSnapshot.getValue(CategoryModel::class.java)
                    if(list!=null){
                        // Map Picture property to drawable resource IDs
                        val updatedList = list.copy(
                            Picture = list.Picture // Use the Picture field directly as a URL
                        )
                        lists.add(updatedList)
                    }
                }


            _catagory.value=lists
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error appropriately instead of crashing
                android.util.Log.e("MainviewModel", "Database error: ${error.message}")
            }

        })


    }
}