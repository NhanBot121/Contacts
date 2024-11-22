package com.mck.contacts.ui.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mck.contacts.R
import com.mck.contacts.databinding.FragmentContactBinding
import com.mck.contacts.model.ContactDatabase

class ContactsFragment : Fragment() {
    var _binding : FragmentContactBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        val view = binding.root

        val application = requireNotNull(this.activity).application
        val dao = ContactDatabase.getInstance(application).contactDao

        val viewModelFactory = ContactsViewModelFactory(dao)
        val viewModel = ViewModelProvider(
            this, viewModelFactory
        )[ContactsViewModel::class.java]

        // Set up the toolbar
        val toolbar = binding.contactToolbar
        toolbar.setSubtitle("Contacts")
        toolbar.inflateMenu(R.menu.contact_toolbar_menu)


        // The add contact button
        binding.addFab.setOnClickListener {
            findNavController().navigate(R.id.action_contactFragment_to_addContactFragment)
        }

        // contact item adapter
        val adapter =
            ContactItemAdapter { contactId -> viewModel.onContactClicked(contactId) } // the onBind() in adapter gives you the desired contactId

        // recylcer view
        binding.contactList.adapter = adapter
        //
        viewModel.contacts.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.contacts = it
            }
        })

        viewModel.navigateToContact.observe(viewLifecycleOwner, Observer { contactId ->
            contactId?.let {
                val action = ContactsFragmentDirections
                    .actionContactFragmentToContactInfoFragment(contactId)
                this.findNavController().navigate(action)
                viewModel.onContactNavigated()
            }
        })

        return view
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}