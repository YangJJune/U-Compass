package com.ikseong.ucompass.mapper

import com.ikseong.ucompass.data.network.request.CreateRoomRequest
import com.ikseong.ucompass.ui.create.viewmodel.CreateUiState

fun CreateUiState.toRequest(
    creator: String,
): CreateRoomRequest =
    CreateRoomRequest(
        title = this.title,
        creator = creator,
    )
