package com.mck.contacts.ui.info

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mck.contacts.R
import com.mck.contacts.model.ContactDatabase

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter


class ContactInfoFragment : Fragment() {

    private lateinit var viewModel: ContactInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val contactId = ContactInfoFragmentArgs.fromBundle(requireArguments()).contactId

        setUpViewModel(contactId)

        setUpObservers(contactId)

        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    Surface {
                        InfoFragmentContent(viewModel)
                    }
                }
            }
        }
    }

    private fun setUpViewModel(contactId: Long) {
        val application = requireNotNull(this.activity).application
        val dao = ContactDatabase.getInstance(application).contactDao
        // get the view model
        val viewModelFactory = ContactInfoViewModelFactory(contactId, dao)
        viewModel = ViewModelProvider(
            this, viewModelFactory
        )[ContactInfoViewModel::class.java]
    }

    private fun setUpObservers(contactId: Long) {
        // navigate to list
        viewModel.navigateToContacts.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                findNavController()
                    .navigate(R.id.action_contactInfoFragment_to_contactFragment)
                viewModel.onNavigatedToList() // reset
            }
        })

        viewModel.navigateToEdit.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                val action = ContactInfoFragmentDirections.actionContactInfoFragmentToEditContactFragment(contactId)
                findNavController().navigate(action)
                viewModel.onNavigatedToEdit() // reset
            }
        }

        // Observe the call phone event
        viewModel.callPhoneEvent.observe(viewLifecycleOwner) { phoneNumber ->
            phoneNumber?.let {
                Toast.makeText(requireContext(), "Make call", Toast.LENGTH_SHORT).show()
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$it")
                }
                startActivity(intent)
                viewModel.onCallPhoneHandled() // Reset the event
            }
            Toast.makeText(requireContext(), "Make call", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun InfoFragmentContent(viewModel: ContactInfoViewModel) {
    val contact by viewModel.contact.observeAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // Profile Picture
            ProfilePictureInfo(imageUri = contact?.picture)

            Spacer(modifier = Modifier.height(16.dp))

            // Name Field
            Text(
                text = contact?.name.orEmpty(),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Phone Field
            ContactInfoRow(
                iconRes = R.drawable.ic_phone,
                content = contact?.number.orEmpty()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Email Field
            ContactInfoRow(
                iconRes = R.drawable.ic_email,
                content = contact?.email.orEmpty()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Call Button
            IconButton(
                onClick = { viewModel.onCallClick() },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_call),
                    contentDescription = "Call Contact"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


        }

        // Edit Button
        IconButton(
            onClick = { viewModel.onEditClick() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp)
                .size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = "Edit"
            )
        }

        // Back Button
        IconButton(
            onClick = { viewModel.onBackClick() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back"
            )
        }
    }
}

@Composable
fun ProfilePictureInfo(imageUri: String?) {
    Image(
        painter = rememberAsyncImagePainter(model = imageUri),
        contentDescription = "Profile Picture",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(CircleShape) // Add rounded edges if desired
    )
}

@Composable
fun ContactInfoRow(iconRes: Int, content: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}