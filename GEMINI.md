# Smart Mirror AI Camera Prototype - Development Blueprint

This file tracks the transition from the legacy IPCamViewer to the modern **Smart Mirror AI Camera Prototype**.

## 🏗 Product Architecture

- **UI Layer:** Jetpack Compose (Smart Mirror UI).
- **Camera Layer:** CameraX + UVCCamera (for USB support).
- **Tracking Layer:** MediaPipe (Face) + YOLOv8 (Body/Head).
- **Motion Layer:** PID Controller + Virtual Camera Smoothing.
- **Rendering Layer:** OpenGL / Compose Canvas Overlays.

## 🛠 Technical Stack

| Component | Technology |
|-----------|------------|
| Language | **Kotlin** |
| UI Framework | **Jetpack Compose** |
| Camera API | **CameraX** |
| AI Tracking | **MediaPipe** (Face), **TFLite/YOLOv8** (Body) |
| Smoothing | **PID Controller** |
| Architecture | **MVVM** |
| Dependency Injection | **Hilt** |
| Concurrency | **Coroutines + Flow** |

## 📐 Tablet Specifications (Landscape)

- **Target:** Android Tablet (Landscape 16:9).
- **Base Resolution:** 1920x1080.
- **Responsiveness:** ConstraintLayout + Compose Adaptive.

## 📁 Project Structure

```text
app/
 ├── ai/                # MediaPipe, YOLO, Tracking, Smoothing
 ├── camera/            # USB, Preview, Renderer
 ├── ui/                # Screens, Overlays, Components, Theme
 ├── motion/            # PID, VirtualCamera, Interpolation
 ├── data/              # Repositories, Data Sources
 ├── domain/            # Use Cases, Models
 └── utils/             # Helpers
```

## 🚀 Development Roadmap & Screens

1. [ ] **Splash Screen:** Branding & Initializing AI engines.
2. [ ] **Camera Mirror Screen:** Primary smart mirror interface.
3. [ ] **Tracking Debug Screen:** Visual bounding boxes & inference data.
4. [ ] **Settings Screen:** Real-time PID tuning & AI thresholds.
5. [ ] **Calibration Screen:** Camera alignment & FOV setup.
6. [ ] **Demo Mode Screen:** Automatic feature showcase.

## 📝 Active Task

1. **Architecture Setup:** Creating the new package structure.
2. **Dependency Update:** Adding Kotlin, Compose, Hilt, and CameraX to `build.gradle`.
3. **Figma Analysis:** Pending design review and asset extraction.
