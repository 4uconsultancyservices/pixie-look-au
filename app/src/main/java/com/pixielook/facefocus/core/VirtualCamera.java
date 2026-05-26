package com.pixielook.facefocus.core;

import android.graphics.RectF;

/**
 * VirtualCamera drives a digital sliding crop window around the
 * full camera frame, aggressively centering on the face while interpolating
 * motion smoothly using PID controllers to feel like a steady physical gimbal.
 */
public class VirtualCamera {

    // Current state (Normalized 0.0 to 1.0)
    public double x = 0.5;
    public double y = 0.5;
    public double zoom = 1.0;

    // Target state
    private double targetX = 0.5;
    private double targetY = 0.5;
    private double targetZoom = 1.0;

    // Internal state for zoom slew limiting
    private double slewZoom = 1.0;

    // Input Smoothing (Low-pass filter for face detection jitter)
    private final ExponentialSmoothing smoothX = new ExponentialSmoothing(0.1);
    private final ExponentialSmoothing smoothY = new ExponentialSmoothing(0.1);
    private final ExponentialSmoothing smoothSize = new ExponentialSmoothing(0.1);

    // PID Controllers for physical-feeling movement
    private final PIDController pidX = new PIDController(5.0, 0.0, 0.2);
    private final PIDController pidY = new PIDController(5.0, 0.0, 0.2);
    private final PIDController pidZoom = new PIDController(10.0, 0.0, 0.1);

    // Configuration
    private final double minZoom = 1.0;
    private final double maxZoom = 3.0;
    
    // Target face size as a fraction of screen (roughly 40%)
    private final double targetFaceSize = 0.4;
    
    // Max zoom acceleration speed (units per sec)
    private final double zoomSlewRate = 0.2;

    public VirtualCamera() {
        // Defaults initialized above.
    }

    /**
     * Updates the virtual camera position towards the face bounding box.
     * @param faceBbox RectF holding normalized [0,1] coordinates: [left, top, right, bottom], or null if no face.
     * @param dt Delta time in seconds since last frame update (e.g. 0.033 for 30fps)
     */
    public void update(RectF faceBbox, double dt) {
        if (faceBbox != null) {
            double fw = faceBbox.right - faceBbox.left;
            double fh = faceBbox.bottom - faceBbox.top;
            
            // Center of the bounding box
            double cx = faceBbox.left + fw / 2.0;
            double cy = faceBbox.top + fh / 2.0;
            
            // Robust size metric resistant to head rotation: sqrt(Area)
            double faceSize = Math.sqrt(fw * fh);

            // 0. Smooth the raw detector inputs
            cx = smoothX.update(cx);
            cy = smoothY.update(cy);
            faceSize = smoothSize.update(faceSize);

            // 1. Update target position (Strict center lock logic based on smoothed input)
            this.targetX = cx;
            this.targetY = cy;

            // 2. Update target zoom (Rate limited)
            if (faceSize > 0) {
                double rawDesiredZoom = this.targetFaceSize / faceSize;
                rawDesiredZoom = Math.max(minZoom, Math.min(rawDesiredZoom, maxZoom));

                // Slew rate limit the zoom target to create a smooth tracking ramp
                double diff = rawDesiredZoom - this.slewZoom;
                double maxStep = this.zoomSlewRate * dt;

                if (Math.abs(diff) > maxStep) {
                    this.slewZoom += maxStep * Math.signum(diff);
                } else {
                    this.slewZoom = rawDesiredZoom;
                }
                this.targetZoom = this.slewZoom;
            }
        }

        // Apply PID Control
        
        // X-Axis
        double errorX = this.targetX - this.x;
        double velocityX = pidX.update(errorX, dt);
        this.x += velocityX * dt;

        // Y-Axis
        double errorY = this.targetY - this.y;
        double velocityY = pidY.update(errorY, dt);
        this.y += velocityY * dt;

        // Zoom (Decoupled from XY)
        double errorZ = this.targetZoom - this.zoom;
        double velocityZ = pidZoom.update(errorZ, dt);

        // Clamp Zoom velocity so it never scales jarringly fast
        double maxZoomVel = 0.5;
        velocityZ = Math.max(-maxZoomVel, Math.min(velocityZ, maxZoomVel));
        this.zoom += velocityZ * dt;

        // Absolute boundaries
        this.zoom = Math.max(minZoom, Math.min(this.zoom, maxZoom));
        this.x = Math.max(0.0, Math.min(this.x, 1.0));
        this.y = Math.max(0.0, Math.min(this.y, 1.0));
    }

    /**
     * Extracts the current bounding box of what the user should actually see 
     * out of the original 16:9 camera feed, warped to the final aspect ratio (e.g. portrait).
     * @param inputAspectRatio original feed dimension Width / Height (e.g. 1.77 for 16:9)
     * @return RectF containing normalized coordinates [left, top, right, bottom] representing the crop
     */
    public RectF getCropRect(double inputAspectRatio) {
        double targetAR = 3.0 / 4.0; // Portrait mode aspect ratio

        // Width and Height of crop window in normalized coordinates
        double cropH = 1.0 / this.zoom;
        double cropW = cropH * (targetAR / inputAspectRatio);

        // Clamp crop size to 1.0 max (prevent zooming fully out of the bounds of the base image)
        if (cropW > 1.0) cropW = 1.0;
        if (cropH > 1.0) cropH = 1.0;

        // Center the crop window upon our current smoothed X/Y coordinates
        double x1 = this.x - (cropW / 2.0);
        double y1 = this.y - (cropH / 2.0);

        // Boundary reflection/walls to keep window strictly inside input frame
        if (x1 < 0) x1 = 0;
        if (y1 < 0) y1 = 0;
        if (x1 + cropW > 1.0) x1 = 1.0 - cropW;
        if (y1 + cropH > 1.0) y1 = 1.0 - cropH;

        return new RectF((float) x1, (float) y1, (float) (x1 + cropW), (float) (y1 + cropH));
    }
}
