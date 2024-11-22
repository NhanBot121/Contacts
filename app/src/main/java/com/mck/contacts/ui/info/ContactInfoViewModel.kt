package com.mck.contacts.ui.info


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mck.contacts.model.ContactDao

class ContactInfoViewModel(contactId: Long ,dao: ContactDao) : ViewModel() {
    //
    val contact = dao.get(contactId)

    // will navigate to contact by id
    private val _navigateToList = MutableLiveData<Boolean>(false)
    val navigateToList: LiveData<Boolean>
        get() = _navigateToList

    // LiveData
    private val _navigateToEdit = MutableLiveData<Boolean>(false)
    val navigateToEdit: LiveData<Boolean>
        get() = _navigateToEdit

    fun onEditClick() {
        _navigateToEdit.value = true
    }

    fun onNavigatedToEdit() {
        _navigateToEdit.value = false
    }


    fun onBackClick() {
        _navigateToList.value = true
    }

    fun onNavigatedToList() {
        _navigateToList.value = false
    }

    private val _callPhoneEvent = MutableLiveData<String?>()
    val callPhoneEvent: LiveData<String?> get() = _callPhoneEvent

    fun onCallClick() {
        _callPhoneEvent.value = contact.value?.number
    }

    fun onCallPhoneHandled() {
        _callPhoneEvent.value = null // Reset the event after it's handled
    }

}