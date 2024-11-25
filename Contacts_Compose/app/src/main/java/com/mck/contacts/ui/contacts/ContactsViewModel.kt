package com.mck.contacts.ui.contacts

import android.util.Log
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

    fun searchContacts(query: String) {
        viewModelScope.launch {
            dao.search("%$query%").observeForever { results ->
                _searchResults.postValue(results)
            }
        }
    }

    //
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