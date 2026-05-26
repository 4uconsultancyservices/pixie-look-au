package com.pixielook.facefocus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.pixielook.facefocus.ui.screens.HairLengthScreen
import com.pixielook.facefocus.ui.screens.SplashScreen
import com.pixielook.facefocus.ui.screens.StyleSelectionScreen
import com.pixielook.facefocus.ui.theme.PixieLookTheme
import com.pixielook.facefocus.ui.screens.HairTypeScreen
import com.pixielook.facefocus.ui.screens.AgeScreen
import com.pixielook.facefocus.ui.screens.HairStylingTimeScreen
import com.pixielook.facefocus.ui.screens.LifestyleScreen
import com.pixielook.facefocus.ui.screens.SuccessScreen
import com.pixielook.facefocus.ui.screens.FaceScanScreen
import com.pixielook.facefocus.ui.screens.CameraFaceScanScreen
import com.pixielook.facefocus.ui.screens.FaceScanningScreen
import com.pixielook.facefocus.ui.screens.FaceScanCompleteScreen
enum class AppStep {
    SPLASH, STYLE_SELECTION, HAIR_LENGTH,HAIR_TYPE,AGE_SELECTION, HAIR_STYLE_TIMING, LIFE_STYLES,SUCCESS_SCREEN, FACE_SCAN, CAMERA_FACE_SCAN, FACE_SCANNING, FACE_SCANNING_COMPLETED, MAIN_CONTENT
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PixieLookTheme {
                var currentStep by remember { mutableStateOf(AppStep.SPLASH) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentStep) {
                        AppStep.SPLASH -> {
                            SplashScreen(onTimeout = { currentStep = AppStep.STYLE_SELECTION })
                        }
                        AppStep.STYLE_SELECTION -> {
                            StyleSelectionScreen(onNext = { currentStep = AppStep.HAIR_LENGTH })
                        }
                        AppStep.HAIR_LENGTH -> {
                            HairLengthScreen(onNext = { currentStep = AppStep.HAIR_TYPE })
                        }
                        AppStep.HAIR_TYPE -> {
                            HairTypeScreen(onNext = { currentStep = AppStep.AGE_SELECTION })
                        }
                        AppStep.AGE_SELECTION -> {
                            AgeScreen(onNext = { currentStep = AppStep.HAIR_STYLE_TIMING })
                        }
                        AppStep.HAIR_STYLE_TIMING -> {
                            HairStylingTimeScreen(onNext = { currentStep = AppStep.LIFE_STYLES })
                        }
                        AppStep.LIFE_STYLES -> {
                            LifestyleScreen(onNext = { currentStep = AppStep.SUCCESS_SCREEN })
                        }
                        AppStep.SUCCESS_SCREEN -> {
                            SuccessScreen(onNext = { currentStep = AppStep.FACE_SCAN })
                        }
                        AppStep.FACE_SCAN -> {
                            FaceScanScreen(onGetStarted = { currentStep = AppStep.CAMERA_FACE_SCAN })
                        }
                        AppStep.CAMERA_FACE_SCAN -> {
                            CameraFaceScanScreen(onHome = { currentStep = AppStep.FACE_SCANNING })
                        }
                        AppStep.FACE_SCANNING -> {
                            FaceScanningScreen(onComplete = { currentStep = AppStep.FACE_SCANNING_COMPLETED })
                        }
                        AppStep.FACE_SCANNING_COMPLETED -> {
                            FaceScanCompleteScreen(onFinish = { currentStep = AppStep.MAIN_CONTENT })
                        }
                        AppStep.MAIN_CONTENT -> {
                            MainScreenContent()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreenContent() {
    Greeting("Pixie Look Smart Mirror Prototype")
}

@Composable
fun Greeting(name: String) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(), 
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
