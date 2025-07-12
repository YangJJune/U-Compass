package com.ikseong.ucompass.ui.create.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikseong.ucompass.domain.CreateRoomUseCase
import com.ikseong.ucompass.domain.GetDeviceIdUseCase
import com.ikseong.ucompass.mapper.toRequest
import com.ikseong.ucompass.ui.util.viewutil.ToastUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val createRoomUseCase: CreateRoomUseCase,
    private val getDeviceIdUseCase: GetDeviceIdUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<CreateUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onCreateUiAction(action: CreateUiAction) {
        when (action) {
            CreateUiAction.OnConfirmClick -> navigateToHomeScreen()
            CreateUiAction.OnCreateClick -> createRoom()
            CreateUiAction.OnShareClick -> shareRoomInfo()
            is CreateUiAction.UpdateTitleField -> updateTitle(action.text)
        }
    }

    private fun updateTitle(text: String) {
        _uiState.value = _uiState.value.copy(title = text)
    }

    private fun navigateToFinishScreen() {
        viewModelScope.launch {
            _uiEvent.send(CreateUiEvent.NavigateToFinish)
        }
    }

    private fun navigateToHomeScreen() {
        viewModelScope.launch {
            _uiEvent.send(CreateUiEvent.NavigateToHome)
        }
    }

    private fun shareRoomInfo() {
        viewModelScope.launch {
            _uiEvent.send(CreateUiEvent.ShareLink)
        }
    }

    private fun createRoom() {
        viewModelScope.launch {
            val deviceId = getDeviceIdUseCase().first()

            val request = _uiState.value.toRequest(
                creator = deviceId ?: "",
            )

            createRoomUseCase(request).fold(
                onSuccess = { data ->
                    Log.d("CreateViewModel", "createRoom: $data")
                    _uiState.update {
                        it.copy(
                            roomNumber = data.data
                        )
                    }
                    
                    // 방 생성 완료 토스트 메시지 표시
                    ToastUtil.showToast(
                        context = context,
                        message = "방 생성이 완료되었습니다! 방 번호: ${data.data}"
                    )
                    
                    navigateToFinishScreen()
                },
                onFailure = { error ->
                    Log.e("CreateViewModel", "createRoom: $error")
                    
                    // 방 생성 실패 토스트 메시지 표시
                    ToastUtil.showToast(
                        context = context,
                        message = "방 생성에 실패했습니다. 다시 시도해주세요."
                    )
                }
            )
        }
    }

}

