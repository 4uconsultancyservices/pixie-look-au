package com.pixielook.facefocus.core;

/**
 * PID Controller used for driving the Virtual Camera smoothly
 * towards target X, Y, and Zoom targets.
 * Ported directly from Python logic.
 */
public class PIDController {
    
    private final double kp;
    private final double ki;
    private final double kd;

    private double prevError = 0;
    private double integral = 0;

    public PIDController(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }

    /**
     * Calculate output velocity/correction based on current error and delta time.
     * @param error Distance to target
     * @param dt Elapsed time in seconds since last frame
     * @return Output applied to current state
     */
    public double update(double error, double dt) {
        // Enforce tiny floor on dt to prevent division by zero or explosive jumps
        if (dt < 0.001) {
            dt = 0.001;
        }

        // Integral accumulation
        this.integral += error * dt;

        // Anti-windup (clamp integral between -5.0 and 5.0)
        this.integral = Math.max(-5.0, Math.min(this.integral, 5.0));

        // Basic derivative
        double derivative = (error - this.prevError) / dt;

        // Calculate control output
        double output = (this.kp * error) + (this.ki * this.integral) + (this.kd * derivative);

        // Store error for next derivative pass
        this.prevError = error;

        return output;
    }

    /**
     * Reset the internal integral/derivative state if target totally changes tracking.
     */
    public void reset() {
        this.prevError = 0;
        this.integral = 0;
    }
}
