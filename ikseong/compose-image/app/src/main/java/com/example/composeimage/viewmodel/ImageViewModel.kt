package com.example.composeimage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeimage.repository.ImageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImageViewModel : ViewModel() {
    private val repository = ImageRepository()

    private val _images = MutableStateFlow<List<String>>(emptyList())
    val images = _images.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        getImages()
    }

    private fun getImages() {
        viewModelScope.launch {
            repository.getImages().fold(
                onSuccess = { response ->
                    val newList = response.response.homeImgs.map { it.homeImgUrl }
                    _images.value = newList
                },
                onFailure = { exception ->
                    _error.value = exception.message
                }
            )
        }
    }
}