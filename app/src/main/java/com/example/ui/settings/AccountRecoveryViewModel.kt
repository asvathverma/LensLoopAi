package com.example.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TrustedContact(val name: String, val phone: String)

data class AccountRecoveryState(
    val emailVerified: Boolean = true,
    val phoneVerified: Boolean = false,
    val trustedContacts: List<TrustedContact> = emptyList(),
    val isIdVerificationPending: Boolean = false
)

class AccountRecoveryViewModel : ViewModel() {
    private val _state = MutableStateFlow(AccountRecoveryState())
    val state: StateFlow<AccountRecoveryState> = _state.asStateFlow()

    fun verifyPhone() {
        _state.update { it.copy(phoneVerified = true) }
    }

    fun addTrustedContact(contact: TrustedContact) {
        if (_state.value.trustedContacts.size < 5) {
            _state.update { it.copy(trustedContacts = it.trustedContacts + contact) }
        }
    }
    
    fun removeTrustedContact(contact: TrustedContact) {
        _state.update { it.copy(trustedContacts = it.trustedContacts - contact) }
    }

    fun submitIdVerification() {
        _state.update { it.copy(isIdVerificationPending = true) }
    }
}
