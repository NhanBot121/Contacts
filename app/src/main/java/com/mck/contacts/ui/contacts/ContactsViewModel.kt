package com.mck.contacts.ui.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mck.contacts.model.Contact
import com.mck.contacts.model.ContactDao
import kotlinx.coroutines.launch

class ContactsViewModel(val dao: ContactDao) : ViewModel() {
    // get all contacts in database to display
    val contacts = dao.getAll()

    // will navigate to contact by id
    private val _navigateToContact = MutableLiveData<Long?>()
    val navigateToContact: LiveData<Long?>
        get() = _navigateToContact

    // LiveData for search results
    private val _searchResults = MutableLiveData<List<Contact>>()
    val searchResults: LiveData<List<Contact>> get() = _searchResults

    fun searchContacts(query: String) {
        viewModelScope.launch {
            dao.search("%$query%").observeForever { results ->
                _searchResults.postValue(results)
            }
        }
    }

    //
    fun onContactClicked(id: Long) {
        _navigateToContact.value = id
    }

    fun onContactNavigated() {
        _navigateToContact.value = null
    }

}