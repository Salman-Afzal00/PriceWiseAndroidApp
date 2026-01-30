package com.nex.pricewiseandroidapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.nex.pricewiseandroidapp.data.model.UserProfile
import com.nex.pricewiseandroidapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    // Initialize your repository
    private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    /* ---------------- UI setters ---------------- */

    fun setEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun setPassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun setUsername(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun setConfirmPassword(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword) }
    }

    /* ---------------- Email / Password Login ---------------- */

    fun signInWithEmailAndPassword() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth
                    .signInWithEmailAndPassword(
                        uiState.value.email.trim(),
                        uiState.value.password
                    )
                    .await()

                val user = result.user

                if (user != null && user.isEmailVerified) {
                    _authState.value = AuthState.Success
                } else {
                    auth.signOut()
                    _authState.value = AuthState.VerificationNeeded(
                        "Please verify your email before logging in."
                    )
                }

            } catch (e: Exception) {
                _authState.value =
                    AuthState.Error(e.localizedMessage ?: "Login failed")
            }
        }
    }

    /* ---------------- Google Sign-In ---------------- */

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                val firebaseUser = result.user

                if (firebaseUser != null) {
                    // Create UserProfile from Google Data
                    val userProfile = UserProfile(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        username = firebaseUser.displayName ?: "Google User",
                        profilePictureUrl = firebaseUser.photoUrl?.toString() ?: ""
                    )

                    // Save to Firestore
                    saveUserToFirestore(userProfile)
                } else {
                    _authState.value = AuthState.Error("Google Sign-In succeeded but user is null")
                }

            } catch (e: Exception) {
                _authState.value =
                    AuthState.Error(e.localizedMessage ?: "Google sign-in failed")
            }
        }
    }

    /* ---------------- Registration ---------------- */

    fun createUserWithEmailAndPassword() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth
                    .createUserWithEmailAndPassword(
                        uiState.value.email.trim(),
                        uiState.value.password
                    )
                    .await()

                val firebaseUser = result.user

                if (firebaseUser != null) {
                    // 1. Create User object with the username input
                    val userProfile = UserProfile(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        username = uiState.value.username.trim()
                    )

                    // 2. Save to Firestore immediately (while authenticated)
                    userRepository.saveUserToFirestore(
                        user = userProfile,
                        onSuccess = {
                            // 3. Send Verification Email only after saving data
                            viewModelScope.launch {
                                try {
                                    firebaseUser.sendEmailVerification().await()
                                    auth.signOut() // Sign out forces them to verify
                                    _authState.value = AuthState.AccountCreated(
                                        "Account created. Please verify your email."
                                    )
                                } catch (e: Exception) {
                                    _authState.value = AuthState.Error("Account created, but failed to send verification email.")
                                }
                            }
                        },
                        onFailure = { e ->
                            // Even if DB save fails, the Auth account exists, but we report error
                            _authState.value = AuthState.Error("Failed to save user data: ${e.message}")
                        }
                    )
                }

            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is FirebaseAuthUserCollisionException -> {
                        "An account already exists with this email address. Please try to log in."
                    }
                    else -> {
                        e.message ?: "An unknown error occurred during registration."
                    }
                }
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }

    /* ---------------- Helper: Save to Firestore ---------------- */

    private fun saveUserToFirestore(user: UserProfile) {
        userRepository.saveUserToFirestore(
            user = user,
            onSuccess = {
                _authState.value = AuthState.Success
            },
            onFailure = { e ->
                _authState.value = AuthState.Error("Failed to save user data: ${e.localizedMessage}")
            }
        )
    }

    /* ---------------- Password Reset ---------------- */

    fun sendPasswordResetEmail() {
        viewModelScope.launch {
            val email = uiState.value.email.trim()

            if (email.isBlank()) {
                _authState.value =
                    AuthState.Error("Email address cannot be empty.")
                return@launch
            }

            _authState.value = AuthState.Loading
            try {
                auth.sendPasswordResetEmail(email).await()
                _authState.value = AuthState.PasswordResetSent
            } catch (e: Exception) {
                _authState.value =
                    AuthState.Error(e.localizedMessage ?: "Failed to send reset email.")
            }
        }
    }

    /* ---------------- Verification ---------------- */

    fun resendVerificationEmail() {
        viewModelScope.launch {
            try {
                auth.currentUser?.sendEmailVerification()?.await()
                _authState.value = AuthState.AccountCreated(
                    "Verification email sent again."
                )
            } catch (e: Exception) {
                _authState.value =
                    AuthState.Error("Failed to resend verification email.")
            }
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}

/* ---------------- Models ---------------- */

data class UiState(
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val confirmPassword: String = ""
)

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
    data class AccountCreated(val message: String) : AuthState()
    data class VerificationNeeded(val message: String) : AuthState()
    object PasswordResetSent : AuthState()
}