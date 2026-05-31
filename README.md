# 🌱 EcoFriend – AI Smart Plantation Assistant
*A futuristic, eco-friendly, and premium AI application helping students, beginners, farmers, and gardening enthusiasts grow plants intelligently.*

Welcome to the **EcoFriend** repository. This project includes two first-class implementation clients:
1. **Streamlit Web Platform (`app.py`)**: A gorgeous, highly visual, glassmorphic dashboard showcasing complex data analytic models, interactive weather triggers, ML diagnostic outputs, voice assistants, and fully documented dummy datasets.
2. **Android Native Client (Jetpack Compose)**: A premium, mobile-first, edge-to-edge application designed with responsive layouts, fluid Material 3 components, and a live AI chat assistant connected directly to the **Gemini 3.5 Flash** model.

---

## 🚀 Key Features

### 1. Smart Plant Recommendation
* Matches geographic location, sunlight availability (hours/day), and purpose back to optimized soil categories (Loam, Clay, Sandy, Peat).
* Suggests optimum plant species with visual icons, maintenance difficulties, climate rules, and specialized caring notes.

### 2. Soil Analysis & Fertilizer Lab
* Calculates inherent nutrient ratios and pH bands for chosen soils.
* Offers real organic fertilizer suggestions (including worm castings, gypsum, and bio-nutrients) with an interactive area-based dosing calculator.

### 3. Precision Irrigation Water Predictor
* Infuses weather telemetry (ambient temperature, humidity, and annual climate season) with individual plant properties to calculate exact daily target milliliter demands.
* Alerts users with specific water cadences to prevent over-watering and root rot.

### 4. Climate Compatibility Checker
* Simulates plant survivability indices based on regional parameters to verify compatibility, alerting growers to potential issues before field planting.

### 5. Deep Learning Leaf Disease Diagnosis
* Mimics advanced leaf spot and infection classifiers using diagnostic simulator options.
* Flags pathogen causes and issues prompt treatment plans.

### 6. Multilingual Voice & Chatbot Assistant
* Chatbot similar to ChatGPT offering conversational help.
* Supports speech modeling, translation indicators, and voice input queries for Telugu (తెలుగు), Tamil (தமிழ்), Hindi (हिन्दी), and English.

### 7. Predictive Growth Analyzer
* Plots regression charts projecting future heights and vegetative progression speeds over multiple weeks based on caring accuracy coordinates.

### 8. Premium Gamification & Forum Dashboards
* Reward mechanics adding Eco Points (XP) for following health checks and claiming daily points.
* Marketplace where users can exchange points for heirloom seeds or organic fertilizer.
* Personal diary with history logging.

---

## 💻 Tech Stack & Requirements

### Frontend & Analytics (Web)
* **Streamlit Framework** (Python 3.8+)
* **Pandas & NumPy** (Math, regression curves, and dataframe modeling)
* **Custom CSS** (Premium dark radial backdrop, green neon gradients, glassmorphism card properties)

### Mobile Frontend & Client (Android Native)
* **Kotlin & Jetpack Compose**
* **Material Design 3 (M3)** (Edge-to-edge screens with status bar and gesture-pill safe padding bounds)
* **Retrofit & OkHttp** (High performance background communication with the Gemini REST endpoints)
* **Kotlin Coroutines & Flows** (Safe asynchronous operation handling)

---

## 🛠️ Step-by-Step Installation & Run Instructions

### 🌐 Running the Streamlit Web Client
Ensure Python 3.8+ is installed on your local computer or server environment, then complete the following steps:

1. **Install dependencies:**
   ```bash
   pip install streamlit pandas numpy
   ```

2. **Launch the web server:**
   ```bash
   streamlit run app.py
   ```

3. **Enjoy the Assistant:** Open the browser local-address (usually `http://localhost:8501`) to experience the premium EcoFriend terminal.

---

### 📱 Running the Jetpack Compose Android Client
The Android project is pre-configured and ready to build.

1. **Open the project** in Android Studio.
2. **Add Gemini API Key:** Open the **Secrets Panel** or place your API key inside `.env` on the root workspace:
   ```properties
   GEMINI_API_KEY=your_actual_gemini_key_here
   ```
3. **Assemble and Run:** Run `gradle assembleDebug` or hit **Run** in Android Studio to launch the interactive app inside your device or the Streaming Emulator!

---

## 📂 Project Organization & Code Architecture
```text
├── app.py                      # Master Streamlit Python Web Dashboard Interface
├── README.md                   # Comprehensive technical documentation
├── metadata.json               # Platform identity registration (EcoFriend configuration)
├── app/
│   ├── build.gradle.kts        # Android build scripts, SDK indices, and library configs
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml   # Intent filters and Internet permissions
│           └── java/com/example/
│               ├── MainActivity.kt   # Core Android view incorporating Gemini REST and UI
│               └── ui/theme/         # Custom Material 3 color palettes and typography
```

---

*Contact User Support via the marketplace dashboard or the developer's registered community board to collaborate on expanding EcoFriend globally! Happy Growing!* 🌱
