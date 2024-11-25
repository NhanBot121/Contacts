package com.mck.contacts.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mck.contacts.model.ContactDao

class AddContactViewModelFactory(private val dao: ContactDao)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddContactViewModel::class.java)) {
            return AddContactViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}