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
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mck.contacts.R
import com.mck.contacts.databinding.FragmentContactBinding
import com.mck.contacts.model.ContactDatabase

class ContactsFragment : Fragment() {
    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ContactsViewModel
    private lateinit var adapter: ContactItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactBinding.inflate(inflater, container, false)

        setupViewModel()
        setupRecyclerView()
        setupAddContactButton()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the toolbar as the ActionBar
        val toolbar = binding.contactToolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        setupObservers()
        setupMenu()
    }

    private fun setupViewModel() {
        val application = requireNotNull(this.activity).application
        val dao = ContactDatabase.getInstance(application).contactDao
        val viewModelFactory = ContactsViewModelFactory(dao)
        viewModel = ViewModelProvider(this, viewModelFactory)[ContactsViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = ContactItemAdapter { contactId ->
            viewModel.onContactClicked(contactId)
        }
        binding.contactList.adapter = adapter
    }

    private fun setupObservers() {
        // Observe all contacts
        viewModel.contacts.observe(viewLifecycleOwner) { contacts ->
            contacts?.let { adapter.contacts = it }
        }

        // Observe search results
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            results?.let { adapter.contacts = it }
        }

        // Observe navigation to contact info
        viewModel.navigateToContact.observe(viewLifecycleOwner) { contactId ->
            contactId?.let {
                navigateToContactInfo(it)
                viewModel.onContactNavigated()
            }
        }
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.contact_toolbar_menu, menu)

                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        query?.let { viewModel.searchContacts(it) }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        newText?.let { viewModel.searchContacts(it) }
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle menu item clicks if needed
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupAddContactButton() {
        binding.addFab.setOnClickListener {
            findNavController().navigate(R.id.action_contactFragment_to_addContactFragment)
        }
    }

    private fun navigateToContactInfo(contactId: Long) {
        val action = ContactsFragmentDirections.actionContactFragmentToContactInfoFragment(contactId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
