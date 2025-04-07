package com.ikseong.ucompass.create.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ikseong.ucompass.navigation.Routes

fun NavGraphBuilder.createNavGraph(
    navigateToFinished: () -> Unit,
    navigateToHome: () -> Unit
) {
    composable<Routes.CreateRoomTitle> {

    }
    composable<Routes.CreateRoomFinished> {

    }
}