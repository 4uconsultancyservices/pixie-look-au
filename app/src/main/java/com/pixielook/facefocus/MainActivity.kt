package com.pixielook.facefocus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.pixielook.facefocus.ui.components.NavigationContainer
import com.pixielook.facefocus.ui.screens.*
import com.pixielook.facefocus.ui.theme.PixieLookTheme

enum class AppStep {
    SPLASH,
    INTRO1, INTRO2, INTRO3, INTRO4, INTRO5, INTRO6, INTRO7, INTRO9, INTRO10, INTRO11, INTRO12,
    MAIN_SCREEN, SHOP_SCREEN, ACCOUNT_SCREEN, MESSAGE_SCREEN, REWARDS_SCREEN,
    VIRTUAL_TRY_ONS, TOP_MENTOR, SKIN_ANALYSE, MENTOR_PROFILE, MENTOR_DETAILS, MENTOR_BOOKING, MENTOR_REVIEW,
    SKIN_REC_MASSAGE, SKIN_REC_PRODUCTS,
    HAIR_STYLE_FOR_MEN_1, HAIR_STYLE_FOR_MEN_2, HAIR_STYLE_FOR_MEN_3,
    HAIR_STYLE_FOR_WOMEN,
    HAIR_STYLE_SEARCH_MEN, HAIR_STYLE_SEARCH_WOMEN,
    HAIR_STYLE_SEARCH_SEL_MEN, HAIR_STYLE_SEARCH_SEL_WOMEN,
    HAIR_STYLE_ACCESSORIES, HAIR_STYLE_ELECTRONIC,
    FACE_FOCUS_SELECTION, FACE_FOCUS_AFTER_SEL, FACE_FOCUS_CONGRATS,
    GENDER_SEL_MOCK, TIME_SEL_MOCK, TYPE_SEL_MOCK
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PixieLookTheme {
                val navigationHistory = remember { mutableStateListOf(AppStep.SPLASH) }
                val currentStep = navigationHistory.last()
                var isBackNavigation by remember { mutableStateOf(false) }

                fun navigateTo(step: AppStep, clearHistory: Boolean = false) {
                    isBackNavigation = false
                    if (clearHistory) {
                        navigationHistory.clear()
                    }
                    navigationHistory.add(step)
                }

                fun goBack() {
                    // Only allow going back if there's more than one screen in history
                    // and we aren't currently on the first screen after Splash (Intro1)
                    if (navigationHistory.size > 1) {
                        isBackNavigation = true
                        navigationHistory.removeAt(navigationHistory.size - 1)
                    }
                }

                // Handle physical back button / system back gesture
                BackHandler(enabled = navigationHistory.size > 1) {
                    goBack()
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationContainer(
                        targetState = currentStep,
                        isBack = isBackNavigation
                    ) { step ->
                        when (step) {
                            AppStep.SPLASH -> SplashScreen { 
                                // Moving to Intro1 replaces Splash in history
                                navigateTo(AppStep.INTRO1, clearHistory = true) 
                            }
                            
                            AppStep.INTRO1 -> IntroScreen1(onNext = { navigateTo(AppStep.INTRO2) }, onBack = { /* No back from first screen */ })
                            AppStep.INTRO2 -> IntroScreen2(onNext = { navigateTo(AppStep.INTRO3) }, onBack = { goBack() })
                            AppStep.INTRO3 -> IntroScreen3(onNext = { navigateTo(AppStep.INTRO4) }, onBack = { goBack() })
                            AppStep.INTRO4 -> IntroScreen4(onNext = { navigateTo(AppStep.INTRO5) }, onBack = { goBack() })
                            AppStep.INTRO5 -> IntroScreen5(onNext = { navigateTo(AppStep.INTRO6) }, onBack = { goBack() })
                            AppStep.INTRO6 -> IntroScreen6(onNext = { navigateTo(AppStep.INTRO7) }, onBack = { goBack() })
                            AppStep.INTRO7 -> IntroScreen7(onNext = { navigateTo(AppStep.INTRO9) }, onBack = { goBack() })
                            AppStep.INTRO9 -> IntroScreen9(onNext = { navigateTo(AppStep.INTRO10) }, onBack = { goBack() })
                            AppStep.INTRO10 -> IntroScreen10(onNext = { navigateTo(AppStep.INTRO11) }, onBack = { goBack() })
                            AppStep.INTRO11 -> IntroScreen11(onNext = { navigateTo(AppStep.INTRO12) }, onBack = { goBack() })
                            AppStep.INTRO12 -> IntroScreen12(onNext = { navigateTo(AppStep.MAIN_SCREEN) }, onBack = { goBack() })

                            AppStep.MAIN_SCREEN -> MainScreen(
                                onNext = { navigateTo(AppStep.SHOP_SCREEN) },
                                onBack = { goBack() },
                                onNavigateFaceFitness = { navigateTo(AppStep.FACE_FOCUS_SELECTION) },
                                onNavigateRewards = { navigateTo(AppStep.REWARDS_SCREEN) },
                                onNavigateShop = { navigateTo(AppStep.SHOP_SCREEN) },
                                onNavigateFashionNews = { navigateTo(AppStep.TOP_MENTOR) },
                                onNavigateStudy = { navigateTo(AppStep.SKIN_ANALYSE) },
                                onNavigateHairstyles = { navigateTo(AppStep.GENDER_SEL_MOCK) },
                                onNavigateVirtualTryOn = { navigateTo(AppStep.VIRTUAL_TRY_ONS) },
                                onNavigateAccount = { navigateTo(AppStep.ACCOUNT_SCREEN) },
                                onNavigateMessage = { navigateTo(AppStep.MESSAGE_SCREEN) }
                            )
                            AppStep.SHOP_SCREEN -> ShopScreen(onNext = { navigateTo(AppStep.ACCOUNT_SCREEN) }, onBack = { goBack() })
                            AppStep.ACCOUNT_SCREEN -> AccountScreen(onNext = { navigateTo(AppStep.MESSAGE_SCREEN) }, onBack = { goBack() })
                            AppStep.MESSAGE_SCREEN -> MessageScreen(onNext = { navigateTo(AppStep.REWARDS_SCREEN) }, onBack = { goBack() })
                            AppStep.REWARDS_SCREEN -> RewardsScreen(onNext = { navigateTo(AppStep.VIRTUAL_TRY_ONS) }, onBack = { goBack() })

                            AppStep.VIRTUAL_TRY_ONS -> VirtualTryOnsScreen(onNext = { navigateTo(AppStep.TOP_MENTOR) }, onBack = { goBack() })
                            AppStep.TOP_MENTOR -> TopMentorScreen(onNext = { navigateTo(AppStep.SKIN_ANALYSE) }, onBack = { goBack() })
                            AppStep.SKIN_ANALYSE -> SkinAnalyseScreen(onNext = { navigateTo(AppStep.MENTOR_PROFILE) }, onBack = { goBack() })
                            AppStep.MENTOR_PROFILE -> MentorProfileScreen(onNext = { navigateTo(AppStep.MENTOR_DETAILS) }, onBack = { goBack() })
                            AppStep.MENTOR_DETAILS -> MentorDetailsScreen(onNext = { navigateTo(AppStep.MENTOR_BOOKING) }, onBack = { goBack() })
                            AppStep.MENTOR_BOOKING -> MentorBookingScreen(onNext = { navigateTo(AppStep.MENTOR_REVIEW) }, onBack = { goBack() })
                            AppStep.MENTOR_REVIEW -> MentorReviewScreen(onNext = { navigateTo(AppStep.SKIN_REC_MASSAGE) }, onBack = { goBack() })

                            AppStep.SKIN_REC_MASSAGE -> SkinRecomendationMassageScreen(onNext = { navigateTo(AppStep.SKIN_REC_PRODUCTS) }, onBack = { goBack() })
                            AppStep.SKIN_REC_PRODUCTS -> SkinRecomendationProductsScreen(onNext = { navigateTo(AppStep.HAIR_STYLE_FOR_MEN_1) }, onBack = { goBack() })

                            AppStep.HAIR_STYLE_FOR_MEN_1 -> HairStyleForMenScreen1(onNext = { navigateTo(AppStep.HAIR_STYLE_FOR_MEN_2) }, onBack = { goBack() })
                            AppStep.HAIR_STYLE_FOR_MEN_2 -> HairStyleForMenScreen2(onNext = { navigateTo(AppStep.HAIR_STYLE_FOR_MEN_3) }, onBack = { goBack() })
                            AppStep.HAIR_STYLE_FOR_MEN_3 -> HairStyleForMenScreen3(onNext = { navigateTo(AppStep.HAIR_STYLE_FOR_WOMEN) }, onBack = { goBack() })

                            AppStep.HAIR_STYLE_FOR_WOMEN -> HairStyleForWomeScreen(onNext = { navigateTo(AppStep.HAIR_STYLE_SEARCH_MEN) }, onBack = { goBack() })
                            AppStep.HAIR_STYLE_SEARCH_MEN -> HairStyleSearchForMenScreen(onNext = { navigateTo(AppStep.HAIR_STYLE_SEARCH_WOMEN) }, onBack = { goBack() })
                            AppStep.HAIR_STYLE_SEARCH_WOMEN -> HairStyleSearchForWomeScreen(onNext = { navigateTo(AppStep.HAIR_STYLE_SEARCH_SEL_MEN) }, onBack = { goBack() })
                            
                            AppStep.HAIR_STYLE_SEARCH_SEL_MEN -> HairStyleSearchSelectionForMenScreen(onNext = { navigateTo(AppStep.HAIR_STYLE_SEARCH_SEL_WOMEN) }, onBack = { goBack() })
                            AppStep.HAIR_STYLE_SEARCH_SEL_WOMEN -> HairStyleSearchSelectionForWomenScreen(onNext = { navigateTo(AppStep.HAIR_STYLE_ACCESSORIES) }, onBack = { goBack() })
                            
                            AppStep.HAIR_STYLE_ACCESSORIES -> HairStyleWithAccessoriesScreen(onNext = { navigateTo(AppStep.HAIR_STYLE_ELECTRONIC) }, onBack = { goBack() })
                            AppStep.HAIR_STYLE_ELECTRONIC -> HairStyleWithElectronicDevicesScreen(onNext = { navigateTo(AppStep.FACE_FOCUS_SELECTION) }, onBack = { goBack() })

                            AppStep.FACE_FOCUS_SELECTION -> FaceFocusSelectionScreen(onNext = { navigateTo(AppStep.FACE_FOCUS_AFTER_SEL) }, onBack = { goBack() })
                            AppStep.FACE_FOCUS_AFTER_SEL -> FaceFocusAfterSelectionScreen(onNext = { navigateTo(AppStep.FACE_FOCUS_CONGRATS) }, onBack = { goBack() })
                            AppStep.FACE_FOCUS_CONGRATS -> FaceFocusCongratulationScreen(onNext = { navigateTo(AppStep.GENDER_SEL_MOCK) }, onBack = { goBack() })
                            
                            AppStep.GENDER_SEL_MOCK -> HairStyleGenderSelectionScreenMock(onNext = { navigateTo(AppStep.TIME_SEL_MOCK) }, onBack = { goBack() })
                            AppStep.TIME_SEL_MOCK -> HairStyleTimeSelectionScreenMock(onNext = { navigateTo(AppStep.TYPE_SEL_MOCK) }, onBack = { goBack() })
                            AppStep.TYPE_SEL_MOCK -> HairStyleTypeSelectionScreenMock(onNext = { navigateTo(AppStep.SPLASH, clearHistory = true) }, onBack = { goBack() })
                        }
                    }
                }
            }
        }
    }
}
