package com.pixielook.facefocus.ui.tutorial

import android.app.Application
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

    private val _isTrackingEnabled = MutableStateFlow(true)
    val isTrackingEnabled = _isTrackingEnabled.asStateFlow()

    private val _isAutoZoomEnabled = MutableStateFlow(true)
    val isAutoZoomEnabled = _isAutoZoomEnabled.asStateFlow()

    fun startCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        _cameraState.value = CameraState.STARTING
        viewModelScope.launch {
            try {
                cameraPipeline = CameraPipeline(getApplication(), lifecycleOwner, trackingService)
                cameraPipeline?.bindCamera(previewView)
                _cameraState.value = CameraState.RUNNING
            } catch (e: Exception) {
                _cameraState.value = CameraState.ERROR
            }
        }
    }

    fun stopCamera() {
        cameraPipeline?.release()
        _cameraState.value = CameraState.IDLE
    }

    fun toggleTracking() {
        val newState = !_isTrackingEnabled.value
        _isTrackingEnabled.value = newState
        trackingService.setTrackingEnabled(newState)
    }

    fun toggleAutoZoom() {
        val newState = !_isAutoZoomEnabled.value
        _isAutoZoomEnabled.value = newState
        trackingService.setAutoZoomEnabled(newState)
    }

    fun updateVideoState(state: VideoState) {
        _videoState.value = state
    }

    fun updateCurrentStep(step: Int) {
        _videoState.update { it.copy(currentStep = step) }
    }

    override fun onCleared() {
        super.onCleared()
        trackingService.release()
        cameraPipeline?.release()
    }
}
