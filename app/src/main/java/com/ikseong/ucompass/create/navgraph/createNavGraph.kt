package com.ikseong.ucompass.create.navgraph

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ikseong.ucompass.create.viewmodel.CreateViewModel
import com.ikseong.ucompass.create.screen.CreateRoomFinishRoute
import com.ikseong.ucompass.create.screen.CreateRoomTitleRoute
import com.ikseong.ucompass.navigation.Routes

fun NavGraphBuilder.createNavGraph(
    paddingValues: PaddingValues,
    navigateToFinish: () -> Unit,
    navigateToHome: () -> Unit,
    getBackStackCreateViewModel: @Composable (NavBackStackEntry) -> CreateViewModel
) {
    composable<Routes.CreateRoomTitle> {
        CreateRoomTitleRoute(
            padding = paddingValues,
            navigateToFinish = navigateToFinish,
            viewModel = getBackStackCreateViewModel(it)
        )
    }
    composable<Routes.CreateRoomFinished> {
        CreateRoomFinishRoute(
            padding = paddingValues,
            navigateToHome = navigateToHome,
            shareRoomLink = { /*TODO*/ },
        )
    }
}