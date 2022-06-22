package com.adammcneilly.toa.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase,
) : ViewModel() {
    private val _sessionState: MutableStateFlow<SessionState> = MutableStateFlow(SessionState.UNINITIALIZED)
    val sessionState = _sessionState.asStateFlow()

    init {
        viewModelScope.launch {
            getSessionStateFromLoggedInStatus()
        }
    }

    private suspend fun getSessionStateFromLoggedInStatus() {
        val isLoggedIn = isUserLoggedInUseCase.isUserLoggedIn()

        val newSessionState = if (isLoggedIn) {
            SessionState.LOGGED_IN
        } else {
            SessionState.LOGGED_OUT
        }

        _sessionState.update {
            newSessionState
        }
    }
}
