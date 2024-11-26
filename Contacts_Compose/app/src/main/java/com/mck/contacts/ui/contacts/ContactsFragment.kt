package com.mck.contacts.ui.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mck.contacts.model.ContactDatabase
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import com.mck.contacts.model.Contact
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import kotlin.math.round


class ContactsFragment : Fragment() {
    private lateinit var viewModel: ContactsViewModel

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
    viewModel: ContactsViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            if (isSearching) {
                // Search TextField
                TextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query
                        viewModel.searchContacts(query) // Trigger search in ViewModel
                    },
                    placeholder = { Text("Search...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            } else {
                Text(text = title) // Default title when not searching
            }
        },
        actions = {
            if (isSearching) {
                // Clear Search Query
                IconButton(onClick = {
                    searchQuery = ""
                    viewModel.searchContacts("") // Reset to full list
                }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear Search")
                }
            } else {
                // Start Searching
                IconButton(onClick = { isSearching = true }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
        },
        navigationIcon = {
            if (isSearching) {
                // Back Icon to exit search mode
                IconButton(onClick = {
                    isSearching = false
                    searchQuery = ""
                    viewModel.searchContacts("") // Reset to full list
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit Search")
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
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfilePictureInfo(contact.picture)

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = contact.number,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(viewModel: ContactsViewModel) {
    // Observe LiveData as State
    val contacts by viewModel.filteredContacts.collectAsState(initial = emptyList()) // Provide a default empty list

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
fun ProfilePictureInfo(pictureUrl: String?) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        if (pictureUrl != null) {
            // Load the picture from the URL
            AsyncImage(
                model = pictureUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Placeholder Initials
            Text(
                text = "N/A",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
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
