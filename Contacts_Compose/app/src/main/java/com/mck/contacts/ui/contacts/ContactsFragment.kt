package com.mck.contacts.ui.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mck.contacts.R
import com.mck.contacts.model.ContactDatabase

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.lazy.items

import com.mck.contacts.model.Contact

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Alignment


class ContactsFragment : Fragment() {
    private lateinit var viewModel: ContactsViewModel
    //private lateinit var adapter: ContactItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupViewModel()

        setupObservers()

        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    Surface {
                        ContactsFragmentContent(viewModel)
                    }
                }
            }
        }
    }

    private fun setupViewModel() {
        val application = requireNotNull(this.activity).application
        val dao = ContactDatabase.getInstance(application).contactDao
        val viewModelFactory = ContactsViewModelFactory(dao)
        viewModel = ViewModelProvider(this, viewModelFactory)[ContactsViewModel::class.java]
    }

    private fun setupObservers() {
        // Observe navigation to contact info
        viewModel.navigateToInfo.observe(viewLifecycleOwner) { contactId ->
            contactId?.let {
                navigateToInfo(it)
                viewModel.onInfoNavigated()
            }
        }

        viewModel.navigateToAdd.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                navigateToAdd()
                viewModel.onAddNavigated()
            }
        }
    }

    private fun navigateToInfo(contactId: Long) {
        val action = ContactsFragmentDirections.actionContactFragmentToContactInfoFragment(contactId)
        findNavController().navigate(action)
    }

    private fun navigateToAdd() {
        val action = ContactsFragmentDirections.actionContactFragmentToAddContactFragment()
        findNavController().navigate(action)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchToolbar(
    title: String,
    //onSearch: (String) -> Unit,
    viewModel: ContactsViewModel
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var isSearching by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            if (isSearching) {
                BasicTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.searchContacts(it.text)
                        //onSearch(it.text)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (searchQuery.text.isEmpty()) {
                            Text(text = "Search...", style = MaterialTheme.typography.body2)
                        }
                        innerTextField()
                    }
                )
            } else {
                Text(text = title)
            }
        },
        actions = {
            if (isSearching) {
                IconButton(onClick = { searchQuery = TextFieldValue("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            } else {
                IconButton(onClick = { isSearching = true }) {
                    Text("🔍") // Placeholder for Search Icon
                }
            }
        }
    )
}

@Composable
fun ContactItem(contact: Contact, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick

    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Name: ${contact.name}", style = MaterialTheme.typography.subtitle1)
            Text(text = "Phone: ${contact.number}", style = MaterialTheme.typography.body1)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(viewModel: ContactsViewModel) {
    // Observe LiveData as State
    val contacts by viewModel.contacts.observeAsState(initial = emptyList()) // Provide a default empty list

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        items(contacts) { contact -> // Use the observed data
            ContactItem(contact = contact) {
                viewModel.onInfoClicked(contact.id)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactButton (modifier: Modifier = Modifier, onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add Contact")
    }
}

@Composable
fun ContactsFragmentContent(viewModel: ContactsViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            SearchToolbar("Contacts", viewModel)
            ContactListScreen(viewModel)
        }
        // Floating Action Button positioned at the bottom-right corner
        AddContactButton(
            modifier = Modifier
                .align(Alignment.BottomEnd) // Align FAB to bottom-end (bottom-right)
                .padding(16.dp) // Add padding from the edges
        ) {
            viewModel.onAddClick()
        }
    }
}
