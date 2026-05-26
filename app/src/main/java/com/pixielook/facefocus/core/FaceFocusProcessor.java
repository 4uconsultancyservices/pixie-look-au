package com.pixielook.facefocus.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;

/**
 * High-level orchestration.
 * Ties the YOLO ONNX inference with the PID-based VirtualCamera physics.
 */
public class FaceFocusProcessor {
    private YoloInference yolo;
    private VirtualCamera camera;
    private long lastTimeMs;

    public FaceFocusProcessor(Context context, String modelAssetName) {
        yolo = new YoloInference(context, modelAssetName);
        camera = new VirtualCamera();
        lastTimeMs = System.currentTimeMillis();
    }

    /**
     * Feed a frame into the system. Runs AI and updates the smooth sliding crop window.
     * @param frame The uncropped raw camera frame.
     */
    public void processFrame(Bitmap frame) {
        // Run AI Head Detection
        RectF face = yolo.detectLargestFace(frame);

        // Calculate precise delta time for PID physics
        long now = System.currentTimeMillis();
        double dt = (now - lastTimeMs) / 1000.0;
        
        // Clamp dt in case of extreme lag or first frame jump
        if (dt > 0.2 || dt <= 0) {
            dt = 0.033; // Default 30 FPS gap
        }
        lastTimeMs = now;

        // Drive the virtual camera
        camera.update(face, dt);
    }
    
    /**
     * Read the final smooth sliding crop window.
     * @param inputAspectRatio Aspect ratio of the raw camera feed (e.g. 1.77 for 16:9).
     * @return Normalized coordinates [left, top, right, bottom].
     */
    public RectF getCropRect(double inputAspectRatio) {
        return camera.getCropRect(inputAspectRatio);
    }
    
    public void close() {
        if (yolo != null) {
            yolo.close();
        }
    }
}
