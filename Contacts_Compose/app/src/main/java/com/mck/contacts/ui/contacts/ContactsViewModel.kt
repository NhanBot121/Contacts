package com.mck.contacts.ui.contacts

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mck.contacts.model.Contact
import com.mck.contacts.model.ContactDao
import kotlinx.coroutines.launch
import androidx.lifecycle.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow



class ContactsViewModel(val dao: ContactDao) : ViewModel() {

    // get all contacts in database to display
    val allContacts : LiveData<List<Contact>> = dao.getAll()

    // MutableStateFlow to store the filtered contacts for LazyList
    private val _filteredContacts = MutableStateFlow<List<Contact>>(emptyList())
    val filteredContacts: StateFlow<List<Contact>> = _filteredContacts

    init {
        // Initialize with all contacts when ViewModel is created
        allContacts.observeForever { contacts ->
            _filteredContacts.value = contacts ?: emptyList()
        }
    }

    fun searchContacts(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                // Reset to all contacts if search query is empty
                _filteredContacts.value = allContacts.value ?: emptyList()
            } else {
                // Perform a search with the query
                dao.search("%$query%").observeForever { results ->
                    _filteredContacts.value = results ?: emptyList()
                }
            }
        }
    }

    // will navigate to contact by id
    private val _navigateToAdd = MutableLiveData<Boolean>(false)
    val navigateToAdd: LiveData<Boolean>
        get() = _navigateToAdd

    // will navigate to contact by id
    private val _navigateToInfo = MutableLiveData<Long?>()
    val navigateToInfo: LiveData<Long?>
        get() = _navigateToInfo

    // LiveData for search results
    private val _searchResults = MutableLiveData<List<Contact>>()
    val searchResults: LiveData<List<Contact>>
        get() = _searchResults

    fun onInfoClicked(id: Long) {
        _navigateToInfo.value = id
    }

    fun onInfoNavigated() {
        _navigateToInfo.value = null
    }

    fun onAddClick() {
        _navigateToAdd.value = true
        Log.d("ContactsViewModel", "onAddClick triggered")
    }

    fun onAddNavigated() {
        _navigateToAdd.value = false
    }

}