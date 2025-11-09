package com.borsahalal.presentation.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.borsahalal.data.database.entities.Profile
import com.borsahalal.presentation.ui.components.AddProfileDialog
import com.borsahalal.presentation.ui.components.EditProfileDialog
import com.borsahalal.presentation.ui.components.ProfileCard
import com.borsahalal.presentation.viewmodels.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileManagementScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val profiles by viewModel.allProfiles.collectAsState()
    val activeProfile by viewModel.activeProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedProfile by remember { mutableStateOf<Profile?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Profile")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Manage Profiles",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else if (profiles.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No profiles yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap + to create your first profile",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(profiles) { profile ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProfileCard(
                                profile = profile,
                                isActive = profile.id == activeProfile?.id,
                                onClick = { viewModel.setActiveProfile(profile.id) },
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = {
                                    selectedProfile = profile
                                    showEditDialog = true
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                            }

                            IconButton(
                                onClick = {
                                    selectedProfile = profile
                                    showDeleteDialog = true
                                },
                                enabled = profiles.size > 1
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete Profile",
                                    tint = if (profiles.size > 1) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add Profile Dialog
        if (showAddDialog) {
            AddProfileDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, currency ->
                    viewModel.createProfile(name, currency)
                    showAddDialog = false
                }
            )
        }

        // Edit Profile Dialog
        if (showEditDialog && selectedProfile != null) {
            EditProfileDialog(
                profile = selectedProfile!!,
                onDismiss = {
                    showEditDialog = false
                    selectedProfile = null
                },
                onConfirm = { name, currency ->
                    viewModel.updateProfile(
                        selectedProfile!!.copy(
                            name = name,
                            currency = currency
                        )
                    )
                    showEditDialog = false
                    selectedProfile = null
                }
            )
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog && selectedProfile != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    selectedProfile = null
                },
                title = { Text("Delete Profile?") },
                text = {
                    Text("Are you sure you want to delete '${selectedProfile!!.name}'? This will delete all associated stocks and transactions.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteProfile(selectedProfile!!)
                            showDeleteDialog = false
                            selectedProfile = null
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            selectedProfile = null
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
