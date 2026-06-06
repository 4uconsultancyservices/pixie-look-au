package com.pixielook.facefocus.ui.tutorial

import android.app.Application
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.pixielook.facefocus.camera.CameraPipeline
import com.pixielook.facefocus.models.*
import com.pixielook.facefocus.tracking.TrackingService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TutorialViewModel(application: Application) : AndroidViewModel(application) {

    private val trackingService = TrackingService(application, viewModelScope)
    private var cameraPipeline: CameraPipeline? = null

    val trackingResult = trackingService.trackingResult

    private val _cameraState = MutableStateFlow(CameraState.IDLE)
    val cameraState = _cameraState.asStateFlow()

    private val _videoState = MutableStateFlow(VideoState())
    val videoState = _videoState.asStateFlow()

    private val _settings = MutableStateFlow(TutorialSettings())
    val settings = _settings.asStateFlow()

    fun startCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        _cameraState.value = CameraState.STARTING
        viewModelScope.launch {
            try {
                cameraPipeline?.release()
                val lensFacing = when(_settings.value.lensFacing) {
                    0 -> CameraSelector.LENS_FACING_FRONT
                    1 -> CameraSelector.LENS_FACING_BACK
                    2 -> 2 // LENS_FACING_EXTERNAL
                    else -> CameraSelector.LENS_FACING_BACK // Fallback for IP Cam
                }
                cameraPipeline = CameraPipeline(getApplication(), lifecycleOwner, trackingService)
                cameraPipeline?.bindCamera(previewView, lensFacing)
                _cameraState.value = CameraState.RUNNING
            } catch (e: Exception) {
                _cameraState.value = CameraState.ERROR
            }
        }
    }

    fun switchCamera() {
        val nextLens = (_settings.value.lensFacing + 1) % 4
        updateSettings(_settings.value.copy(lensFacing = nextLens))
    }

    fun stopCamera() {
        cameraPipeline?.release()
        _cameraState.value = CameraState.IDLE
    }

    fun updateSettings(newSettings: TutorialSettings) {
        val lensChanged = newSettings.lensFacing != _settings.value.lensFacing
        _settings.value = newSettings
        trackingService.updateSettings(newSettings)
        
        if (lensChanged) {
            _cameraState.value = CameraState.IDLE
        }
    }

    fun toggleTracking() {
        val newSettings = _settings.value.copy(isFaceTrackingEnabled = !_settings.value.isFaceTrackingEnabled)
        updateSettings(newSettings)
    }

    fun updateVideoState(state: VideoState) {
        _videoState.value = state
    }

    override fun onCleared() {
        super.onCleared()
        trackingService.release()
        cameraPipeline?.release()
    }
}
