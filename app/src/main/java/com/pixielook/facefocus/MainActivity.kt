package com.pixielook.facefocus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.pixielook.facefocus.ui.screens.*
import com.pixielook.facefocus.ui.theme.PixieLookTheme

enum class AppStep {
    SPLASH,
    INTRO1, INTRO2, INTRO3, INTRO4, INTRO5, INTRO6, INTRO7, INTRO9, INTRO10, INTRO11, INTRO12,
    MAIN_SCREEN, SHOP_SCREEN, ACCOUNT_SCREEN, MESSAGE_SCREEN,
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
                var currentStep by remember { mutableStateOf(AppStep.SPLASH) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentStep) {
                        AppStep.SPLASH -> SplashScreen { currentStep = AppStep.INTRO1 }
                        
                        AppStep.INTRO1 -> IntroScreen1 { currentStep = AppStep.INTRO2 }
                        AppStep.INTRO2 -> IntroScreen2 { currentStep = AppStep.INTRO3 }
                        AppStep.INTRO3 -> IntroScreen3 { currentStep = AppStep.INTRO4 }
                        AppStep.INTRO4 -> IntroScreen4 { currentStep = AppStep.INTRO5 }
                        AppStep.INTRO5 -> IntroScreen5 { currentStep = AppStep.INTRO6 }
                        AppStep.INTRO6 -> IntroScreen6 { currentStep = AppStep.INTRO7 }
                        AppStep.INTRO7 -> IntroScreen7 { currentStep = AppStep.INTRO9 }
                        AppStep.INTRO9 -> IntroScreen9 { currentStep = AppStep.INTRO10 }
                        AppStep.INTRO10 -> IntroScreen10 { currentStep = AppStep.INTRO11 }
                        AppStep.INTRO11 -> IntroScreen11 { currentStep = AppStep.INTRO12 }
                        AppStep.INTRO12 -> IntroScreen12 { currentStep = AppStep.MAIN_SCREEN }

                        AppStep.MAIN_SCREEN -> MainScreen { currentStep = AppStep.SHOP_SCREEN }
                        AppStep.SHOP_SCREEN -> ShopScreen { currentStep = AppStep.ACCOUNT_SCREEN }
                        AppStep.ACCOUNT_SCREEN -> AccountScreen { currentStep = AppStep.MESSAGE_SCREEN }
                        AppStep.MESSAGE_SCREEN -> MessageScreen { currentStep = AppStep.VIRTUAL_TRY_ONS }

                        AppStep.VIRTUAL_TRY_ONS -> VirtualTryOnsScreen { currentStep = AppStep.TOP_MENTOR }
                        AppStep.TOP_MENTOR -> TopMentorScreen { currentStep = AppStep.SKIN_ANALYSE }
                        AppStep.SKIN_ANALYSE -> SkinAnalyseScreen { currentStep = AppStep.MENTOR_PROFILE }
                        AppStep.MENTOR_PROFILE -> MentorProfileScreen { currentStep = AppStep.MENTOR_DETAILS }
                        AppStep.MENTOR_DETAILS -> MentorDetailsScreen { currentStep = AppStep.MENTOR_BOOKING }
                        AppStep.MENTOR_BOOKING -> MentorBookingScreen { currentStep = AppStep.MENTOR_REVIEW }
                        AppStep.MENTOR_REVIEW -> MentorReviewScreen { currentStep = AppStep.SKIN_REC_MASSAGE }

                        AppStep.SKIN_REC_MASSAGE -> SkinRecomendationMassageScreen { currentStep = AppStep.SKIN_REC_PRODUCTS }
                        AppStep.SKIN_REC_PRODUCTS -> SkinRecomendationProductsScreen { currentStep = AppStep.HAIR_STYLE_FOR_MEN_1 }

                        AppStep.HAIR_STYLE_FOR_MEN_1 -> HairStyleForMenScreen1 { currentStep = AppStep.HAIR_STYLE_FOR_MEN_2 }
                        AppStep.HAIR_STYLE_FOR_MEN_2 -> HairStyleForMenScreen2 { currentStep = AppStep.HAIR_STYLE_FOR_MEN_3 }
                        AppStep.HAIR_STYLE_FOR_MEN_3 -> HairStyleForMenScreen3 { currentStep = AppStep.HAIR_STYLE_FOR_WOMEN }

                        AppStep.HAIR_STYLE_FOR_WOMEN -> HairStyleForWomeScreen { currentStep = AppStep.HAIR_STYLE_SEARCH_MEN }
                        AppStep.HAIR_STYLE_SEARCH_MEN -> HairStyleSearchForMenScreen { currentStep = AppStep.HAIR_STYLE_SEARCH_WOMEN }
                        AppStep.HAIR_STYLE_SEARCH_WOMEN -> HairStyleSearchForWomeScreen { currentStep = AppStep.HAIR_STYLE_SEARCH_SEL_MEN }
                        
                        AppStep.HAIR_STYLE_SEARCH_SEL_MEN -> HairStyleSearchSelectionForMenScreen { currentStep = AppStep.HAIR_STYLE_SEARCH_SEL_WOMEN }
                        AppStep.HAIR_STYLE_SEARCH_SEL_WOMEN -> HairStyleSearchSelectionForWomenScreen { currentStep = AppStep.HAIR_STYLE_ACCESSORIES }
                        
                        AppStep.HAIR_STYLE_ACCESSORIES -> HairStyleWithAccessoriesScreen { currentStep = AppStep.HAIR_STYLE_ELECTRONIC }
                        AppStep.HAIR_STYLE_ELECTRONIC -> HairStyleWithElectronicDevicesScreen { currentStep = AppStep.FACE_FOCUS_SELECTION }

                        AppStep.FACE_FOCUS_SELECTION -> FaceFocusSelectionScreen { currentStep = AppStep.FACE_FOCUS_AFTER_SEL }
                        AppStep.FACE_FOCUS_AFTER_SEL -> FaceFocusAfterSelectionScreen { currentStep = AppStep.FACE_FOCUS_CONGRATS }
                        AppStep.FACE_FOCUS_CONGRATS -> FaceFocusCongratulationScreen { currentStep = AppStep.GENDER_SEL_MOCK }
                        
                        AppStep.GENDER_SEL_MOCK -> HairStyleGenderSelectionScreenMock { currentStep = AppStep.TIME_SEL_MOCK }
                        AppStep.TIME_SEL_MOCK -> HairStyleTimeSelectionScreenMock { currentStep = AppStep.TYPE_SEL_MOCK }
                        AppStep.TYPE_SEL_MOCK -> HairStyleTypeSelectionScreenMock { currentStep = AppStep.SPLASH }
                    }
                }
            }
        }
    }
}
