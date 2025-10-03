package com.plcoding.auth.presentation.register

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chirp.feature.auth.presentation.generated.resources.Res
import chirp.feature.auth.presentation.generated.resources.error_account_exists
import chirp.feature.auth.presentation.generated.resources.error_invalid_email
import chirp.feature.auth.presentation.generated.resources.error_invalid_password
import chirp.feature.auth.presentation.generated.resources.error_invalid_username
import com.plcoding.auth.domain.EmailValidator
import com.plcoding.core.domain.auth.AuthService
import com.plcoding.core.domain.util.DataError
import com.plcoding.core.domain.util.onFailure
import com.plcoding.core.domain.util.onSuccess
import com.plcoding.core.domain.validation.PasswordValidator
import com.plcoding.core.presentation.util.UiText
import com.plcoding.core.presentation.util.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authService: AuthService,
) : ViewModel() {
    
    private val eventChannel = Channel<RegisterEvent>()
    val events = eventChannel.receiveAsFlow()
    private var hasLoadedInitialData = false
    private val _state = MutableStateFlow(RegisterState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeValidationStates()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RegisterState(),
        )
    private val isEmailValidFlow = snapshotFlow { state.value.emailTextState.text.toString() }
        .map { email -> EmailValidator.validate(email) }
        .distinctUntilChanged()
    private val isUsernameValidFlow = snapshotFlow { state.value.usernameTextState.text.toString() }
        .map { username -> username.length in 3..20 }
        .distinctUntilChanged()
    private val isPasswordValidFlow = snapshotFlow { state.value.passwordTextState.text.toString() }
        .map { password -> PasswordValidator.validate(password) }
        .distinctUntilChanged()
    
    private fun observeValidationStates() {
        combine(
            isEmailValidFlow,
            isUsernameValidFlow,
            isPasswordValidFlow,
        ) { isEmailValid, isUsernameValid, passwordValidationState ->
            val allValid = isEmailValid && isUsernameValid && passwordValidationState.isValidPassword
            _state.update {
                it.copy(
                    canRegister = !it.isRegistering && allValid,
                )
            }
        }.launchIn(viewModelScope)
    }
    
    fun onAction(action: RegisterAction) {
        when (action) {
            RegisterAction.OnInputTextFocusGain -> clearTextFieldErrors()
            RegisterAction.OnLoginClick -> validateFormInputs()
            RegisterAction.OnRegisterClick -> register()
            RegisterAction.OnTogglePasswordVisibilityClick -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible,
                    )
                }
            }
        }
    }
    
    private fun register() {
        if (!validateFormInputs()) {
            return
        }
        
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isRegistering = true,
                )
            }
            
            val email = _state.value.emailTextState.toString()
            val username = _state.value.usernameTextState.toString()
            val password = _state.value.passwordTextState.toString()
            
            authService
                .register(
                    email = email,
                    username = username,
                    password = password,
                )
                .onSuccess {
                    _state.update {
                        it.copy(
                            isRegistering = false,
                        )
                    }
                }
                .onFailure { error ->
                    val registrationError = when (error) {
                        DataError.Remote.CONFLICT -> UiText.Resource(Res.string.error_account_exists)
                        else -> error.toUiText()
                    }
                    _state.update {
                        it.copy(
                            isRegistering = false,
                            registrationError = registrationError,
                        )
                    }
                }
        }
    }
    
    private fun validateFormInputs(): Boolean {
        clearTextFieldErrors()
        
        val currentState = _state.value
        val email = currentState.usernameTextState.text.toString()
        val username = currentState.usernameTextState.text.toString()
        val password = currentState.passwordTextState.text.toString()
        
        val isEmailValid = EmailValidator.validate(email)
        val isUsernameValid = username.length in 3..20
        val passwordValidationState = PasswordValidator.validate(password)
        
        val emailError = if (!isEmailValid) {
            UiText.Resource(Res.string.error_invalid_email)
        } else null
        val usernameError = if (!isUsernameValid) {
            UiText.Resource(Res.string.error_invalid_username)
        } else null
        val passwordError = if (!passwordValidationState.isValidPassword) {
            UiText.Resource(Res.string.error_invalid_password)
        } else null
        
        _state.update {
            it.copy(
                emailError = emailError,
                usernameError = usernameError,
                passwordError = passwordError,
            )
        }
        
        return isUsernameValid && isEmailValid && passwordValidationState.isValidPassword
    }
    
    private fun clearTextFieldErrors() {
        _state.update {
            it.copy(
                emailError = null,
                usernameError = null,
                passwordError = null,
                registrationError = null,
            )
        }
    }
}