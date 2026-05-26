package com.pixielook.facefocus;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.pixielook.facefocus.core.FaceFocusProcessor;

@RunWith(AndroidJUnit4.class)
public class FaceFocusProcessorTest {
    @Test
    public void testProcessorInstantiates() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Ensure ONNX model loads from assets without crashing
        FaceFocusProcessor processor = new FaceFocusProcessor(appContext, "head_nano.onnx");
        assertNotNull(processor);
        processor.close();
    }
}
