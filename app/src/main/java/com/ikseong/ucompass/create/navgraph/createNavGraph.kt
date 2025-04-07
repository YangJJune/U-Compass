package com.ikseong.ucompass.create.navgraph

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ikseong.ucompass.create.screen.CreateRoomFinishRoute
import com.ikseong.ucompass.create.screen.CreateRoomFinishScreen
import com.ikseong.ucompass.create.screen.CreateRoomTitleRoute
import com.ikseong.ucompass.create.screen.CreateRoomTitleScreen
import com.ikseong.ucompass.navigation.Routes

fun NavGraphBuilder.createNavGraph(
    paddingValues: PaddingValues,
    navigateToFinish: () -> Unit,
    navigateToHome: () -> Unit
) {
    composable<Routes.CreateRoomTitle> {
        CreateRoomTitleRoute(
            padding = paddingValues,
            navigateToFinish = navigateToFinish,
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