package com.adammcneilly.toa

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.adammcneilly.toa.core.ui.rememberWindowSizeClass
import com.adammcneilly.toa.core.ui.theme.TOATheme
import com.adammcneilly.toa.destinations.LoginScreenDestination
import com.adammcneilly.toa.destinations.TaskListScreenDestination
import com.adammcneilly.toa.session.SessionState
import com.adammcneilly.toa.session.SessionViewModel
import com.adammcneilly.toa.tasklist.ui.TaskListScreen
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.spec.Route
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalAnimationApi
@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val sessionViewModel: SessionViewModel by viewModels()

    @OptIn(ExperimentalMaterialNavigationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val windowSize = rememberWindowSizeClass()

            TOATheme {
                ConfigureSystemBars()

                ProvideWindowInsets {
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                    ) {

                        val sessionState = sessionViewModel.sessionState.collectAsState()

                        val startRoute: Route? = when (sessionState.value) {
                            SessionState.UNINITIALIZED -> null
                            SessionState.LOGGED_IN -> TaskListScreenDestination
                            SessionState.LOGGED_OUT -> LoginScreenDestination
                        }

                        if (startRoute != null) {
                            DestinationsNavHost(
                                startRoute = startRoute,
                                navGraph = NavGraphs.root,
                                engine = rememberAnimatedNavHostEngine(
                                    rootDefaultAnimations = RootNavGraphDefaultAnimations(
                                        enterTransition = {
                                            slideInHorizontally()
                                        },
                                        exitTransition = {
                                            fadeOut()
                                        },
                                    ),
                                ),
                                manualComposableCallsBuilder = {
                                    composable(TaskListScreenDestination) {
                                        TaskListScreen(
                                            navigator = destinationsNavigator,
                                            windowSize = windowSize,
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ConfigureSystemBars() {
        val systemUiController = rememberSystemUiController()
        val useDarkIcons = !isSystemInDarkTheme()

        SideEffect {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = useDarkIcons
            )
        }
    }
}
