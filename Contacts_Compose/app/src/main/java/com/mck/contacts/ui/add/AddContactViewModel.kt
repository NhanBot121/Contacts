package com.mck.contacts.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mck.contacts.model.ContactDao
import kotlinx.coroutines.launch
import com.mck.contacts.model.Contact
import com.mck.contacts.ui.shared.BaseContactViewModel

class AddContactViewModel(dao: ContactDao) : BaseContactViewModel(dao) {

    private val _newContact = MutableLiveData<Contact>()
    val newContact: LiveData<Contact> = _newContact

    fun addContact() {
        viewModelScope.launch {
            dao.insert(_newContact.value!!)
        }
    }



    // Handle the button onClick
    fun onBackClick() {
        _navigateToContacts.value = true
    }
    fun onSaveClick() {
        addContact()
        _navigateToContacts.value = true
    }
    fun onAddPictureClick() {
        _openImagePickerEvent.value = true
    }

}
