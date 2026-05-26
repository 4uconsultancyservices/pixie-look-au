package com.pixielook.facefocus.core;

/**
 * Exponential moving average filter to smooth bounding box coordinates
 * and mitigate face detection jitter from frame to frame.
 * Ported directly from Python logic.
 */
public class ExponentialSmoothing {
    
    private final double alpha;
    private Double value = null;

    /**
     * @param alpha The smoothing factor [0.0, 1.0]. Lower is smoother but slower.
     */
    public ExponentialSmoothing(double alpha) {
        this.alpha = alpha;
    }

    /**
     * Feed a new raw value into the filter and get the smoothed result.
     */
    public double update(double newValue) {
        if (this.value == null) {
            this.value = newValue; // Initialize immediately on first frame
        } else {
            this.value = (this.alpha * newValue) + ((1.0 - this.alpha) * this.value);
        }
        return this.value;
    }

    /**
     * Hard reset the smooth state (e.g. if tracking is completely lost).
     */
    public void reset() {
        this.value = null;
    }
}
