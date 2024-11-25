package com.mck.contacts.ui.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mck.contacts.model.ContactDao
import kotlinx.coroutines.launch
import com.mck.contacts.model.Contact
import com.mck.contacts.ui.base.BaseContactViewModel

class AddContactViewModel(dao: ContactDao) : BaseContactViewModel(dao) {

    // Fields as LiveData for two-way binding
    val newContactName = MutableLiveData<String>()
    val newContactNumber = MutableLiveData<String>()
    val newContactEmail = MutableLiveData<String>()
    val newContactPicture = MutableLiveData<String>()

    fun addContact() {
        viewModelScope.launch {
            val contact = Contact().apply {
                name = newContactName.value.orEmpty()
                number = newContactNumber.value.orEmpty()
                email = newContactEmail.value.orEmpty()
                picture = newContactPicture.value.orEmpty()
            }
            dao.insert(contact)
        }
    }

    fun onInputChanged() {
        validateInputs(
            newContactName.value.orEmpty(),
            newContactNumber.value.orEmpty(),
            newContactEmail.value.orEmpty()
        )
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
