package com.pixielook.facefocus.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.Map;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OrtException;

/**
 * On-device YOLOv8 Head Detection using ONNX Runtime.
 * Ported from Python YOLO implementation.
 */
public class YoloInference {
    private static final String TAG = "YoloInference";
    private static final int INPUT_SIZE = 640;
    private static final float CONFIDENCE_THRESHOLD = 0.5f;

    private OrtEnvironment env;
    private OrtSession session;

    public YoloInference(Context context, String modelAssetName) {
        try {
            env = OrtEnvironment.getEnvironment();
            byte[] modelBytes = loadModel(context, modelAssetName);
            OrtSession.SessionOptions options = new OrtSession.SessionOptions();
            session = env.createSession(modelBytes, options);
            Log.i(TAG, "ONNX Model loaded successfully: " + modelAssetName);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing ONNX runtime", e);
        }
    }

    private byte[] loadModel(Context context, String assetName) throws Exception {
        InputStream is = context.getAssets().open(assetName);
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        is.close();
        return buffer;
    }

    /**
     * Run inference and return the largest detected head bounding box.
     * @param inputBitmap The frame to process
     * @return RectF representing normalized [0,1] coordinates, or null if no head found
     */
    public RectF detectLargestFace(Bitmap inputBitmap) {
        if (session == null) return null;

        try {
            // 1. Preprocess: Resize and normalize to [1, 3, 640, 640]
            Bitmap resized = Bitmap.createScaledBitmap(inputBitmap, INPUT_SIZE, INPUT_SIZE, false);
            FloatBuffer inputBuffer = bitmapToFloatBuffer(resized);
            long[] shape = {1, 3, INPUT_SIZE, INPUT_SIZE};
            OnnxTensor inputTensor = OnnxTensor.createTensor(env, inputBuffer, shape);

            // 2. Run Inference
            String inputName = session.getInputNames().iterator().next();
            OrtSession.Result result = session.run(Collections.singletonMap(inputName, inputTensor));

            // 3. Postprocess YOLOv8 output [1, 5, 8400]
            // where 5 = [cx, cy, w, h, confidence]
            float[][][] outputArr = (float[][][]) result.get(0).getValue();
            float[][] output = outputArr[0]; // [5][8400]

            RectF largestFace = null;
            float maxArea = 0;

            for (int i = 0; i < 8400; i++) {
                float conf = output[4][i];
                if (conf > CONFIDENCE_THRESHOLD) {
                    float cx = output[0][i] / INPUT_SIZE;
                    float cy = output[1][i] / INPUT_SIZE;
                    float w = output[2][i] / INPUT_SIZE;
                    float h = output[3][i] / INPUT_SIZE;

                    float area = w * h;
                    if (area > maxArea) {
                        maxArea = area;
                        float left = cx - w / 2;
                        float top = cy - h / 2;
                        float right = cx + w / 2;
                        float bottom = cy + h / 2;

                        // Clamp to [0,1]
                        left = Math.max(0, left);
                        top = Math.max(0, top);
                        right = Math.min(1, right);
                        bottom = Math.min(1, bottom);

                        largestFace = new RectF(left, top, right, bottom);
                    }
                }
            }

            inputTensor.close();
            result.close();

            return largestFace;

        } catch (OrtException e) {
            Log.e(TAG, "Inference failed", e);
            return null;
        }
    }

    private FloatBuffer bitmapToFloatBuffer(Bitmap bitmap) {
        FloatBuffer buffer = ByteBuffer.allocateDirect(3 * INPUT_SIZE * INPUT_SIZE * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        
        int[] pixels = new int[INPUT_SIZE * INPUT_SIZE];
        bitmap.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE);

        for (int i = 0; i < pixels.length; i++) {
            int val = pixels[i];
            float r = ((val >> 16) & 0xFF) / 255.0f;
            float g = ((val >> 8) & 0xFF) / 255.0f;
            float b = (val & 0xFF) / 255.0f;

            // Planar format: RRR GGG BBB
            buffer.put(i, r);
            buffer.put(i + pixels.length, g);
            buffer.put(i + 2 * pixels.length, b);
        }
        return buffer;
    }

    public void close() {
        try {
            if (session != null) session.close();
            if (env != null) env.close();
        } catch (OrtException e) {
            e.printStackTrace();
        }
    }
}
