package com.example.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountRecoveryScreen(
    viewModel: AccountRecoveryViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showAddContactDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account Recovery", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Secure your account to ensure you never lose access.",
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Verification Methods
            SecuritySectionTitle("Verification Methods")
            ListItem(
                headlineContent = { Text("Email Address") },
                supportingContent = { Text(if (state.emailVerified) "Verified" else "Unverified") },
                leadingContent = { Icon(Icons.Default.Email, contentDescription = null) },
                trailingContent = {
                    if (state.emailVerified) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green)
                    else TextButton(onClick = { /* Verify Email */ }) { Text("Verify") }
                }
            )
            ListItem(
                headlineContent = { Text("Phone Number") },
                supportingContent = { Text(if (state.phoneVerified) "Verified" else "Add phone number") },
                leadingContent = { Icon(Icons.Default.Phone, contentDescription = null) },
                trailingContent = {
                    if (state.phoneVerified) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green)
                    else TextButton(onClick = { viewModel.verifyPhone() }) { Text("Verify") }
                }
            )

            HorizontalDivider()

            // Trusted Contacts
            SecuritySectionTitle("Trusted Contacts")
            Text(
                "Add 3-5 friends who can securely help you regain access if you are locked out.",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            state.trustedContacts.forEach { contact ->
                ListItem(
                    headlineContent = { Text(contact.name) },
                    supportingContent = { Text(contact.phone) },
                    leadingContent = { Icon(Icons.Default.Person, contentDescription = null) },
                    trailingContent = {
                        IconButton(onClick = { viewModel.removeTrustedContact(contact) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove Contact")
                        }
                    }
                )
            }
            
            if (state.trustedContacts.size < 5) {
                TextButton(
                    onClick = { showAddContactDialog = true },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Trusted Contact (${state.trustedContacts.size}/5)")
                }
            }

            HorizontalDivider()

            // Identity Verification
            SecuritySectionTitle("Identity Verification")
            Text(
                "For hacked accounts, you can submit government ID to regain access.",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (state.isIdVerificationPending) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Pending, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("ID Verification Pending", fontWeight = FontWeight.Bold)
                            Text("Estimated resolution: 24-48 hours", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            } else {
                OutlinedButton(
                    onClick = { viewModel.submitIdVerification() },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Icon(Icons.Default.Badge, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit Government ID")
                }
            }
        }
    }
    
    if (showAddContactDialog) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showAddContactDialog = false },
            title = { Text("Add Trusted Contact") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (name.isNotBlank() && phone.isNotBlank()) {
                            viewModel.addTrustedContact(TrustedContact(name, phone))
                            showAddContactDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddContactDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
