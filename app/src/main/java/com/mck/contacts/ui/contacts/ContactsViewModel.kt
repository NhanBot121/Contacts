package com.mck.contacts.ui.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mck.contacts.model.ContactDao

class ContactsViewModel(dao: ContactDao) : ViewModel() {
    // get all contacts in database to display
    val contacts = dao.getAll()

    // will navigate to contact by id
    private val _navigateToContact = MutableLiveData<Long?>()
    val navigateToContact: LiveData<Long?>
        get() = _navigateToContact

    //
    fun onContactClicked(id: Long) {
        _navigateToContact.value = id
    }

    fun onContactNavigated() {
        _navigateToContact.value = null
    }

}