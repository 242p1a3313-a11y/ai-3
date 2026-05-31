import streamlit as st
import pandas as pd
import numpy as np
import time
import random
import os
import requests
import json

# Set page configuration with premium dark eco-theme setup
st.set_page_config(
    page_title="EcoFriend - AI Smart Plantation Assistant",
    page_icon="🌱",
    layout="wide",
    initial_sidebar_state="collapsed"
)

# Initialize Session States
if "is_logged_in" not in st.session_state:
    st.session_state.is_logged_in = False
if "username" not in st.session_state:
    st.session_state.username = ""
if "eco_points" not in st.session_state:
    st.session_state.eco_points = 180
if "badges" not in st.session_state:
    st.session_state.badges = ["🌟 Eco Pioneer", "💧 Hydro Cadet", "🦠 Microbe Spotter"]
if "chatbot_history" not in st.session_state:
    st.session_state.chatbot_history = [
        {"role": "assistant", "text": "Namaste! I am Prakirtimitra (Prakriti Mitra) 🌱, your wise plantation guide. Ask me any plant or agricultural question in English, Hindi (हिन्दी), Telugu (తెలుగు), Tamil (தமிழ்), or any language! I will help you garden with AI.", "lang": "Multilingual"}
    ]
if "diary_entries" not in st.session_state:
    st.session_state.diary_entries = [
        {"date": "2026-05-24", "text": "Planted Ficus Lyrata named 'Luna'. Soil structure is organic rich forest clay.", "health": "Healthy 🟢"},
        {"date": "2026-05-28", "text": "Watered with 240ml ambient spring mix. First lateral leaf stems expanding nicely.", "health": "Sprouting 🌱"}
    ]
if "current_page" not in st.session_state:
    st.session_state.current_page = "Dashboard"

# Custom Premium Immersive UI CSS with styling extracted from user design instructions
st.markdown("""
<style>
    /* Immersive UI Theme Styles */
    @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700&display=swap');
    
    .stApp {
        background: radial-gradient(circle at top left, #0D2213 0%, #040D06 100%) !important;
        color: #F1F5F9 !important;
        font-family: 'Inter', system-ui, sans-serif !important;
    }
    
    /* Base typography overrides */
    h1, h2, h3, h4, h5, h6 {
        color: #34D399 !important;
        font-family: 'Inter', sans-serif !important;
        font-weight: 700 !important;
        letter-spacing: -0.02em !important;
    }
    
    .neon-glow {
        color: #34D399 !important;
        text-shadow: 0 0 12px rgba(52, 211, 153, 0.5);
    }
    
    /* Advanced Glassmorphism Cards with Emerald Border accents */
    .glass-card {
        background: rgba(255, 255, 255, 0.04) !important;
        backdrop-filter: blur(12px) !important;
        border: 1px solid rgba(255, 255, 255, 0.08) !important;
        border-radius: 20px !important;
        padding: 24px !important;
        margin-bottom: 24px !important;
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5) !important;
        transition: transform 0.3s cubic-bezier(0.16, 1, 0.3, 1), border-color 0.3s ease !important;
    }
    .glass-card:hover {
        transform: translateY(-4px);
        border-color: rgba(52, 211, 153, 0.25) !important;
    }
    
    /* Custom Badge elements */
    .badge-capsule {
        display: inline-block;
        padding: 6px 14px;
        border-radius: 9999px;
        font-size: 0.75rem;
        font-weight: 600;
        text-transform: uppercase;
        margin: 4px 6px 4px 0;
        letter-spacing: 0.05em;
    }
    .badge-green { background: rgba(34, 197, 94, 0.15); color: #34D399; border: 1px solid rgba(52, 211, 153, 0.3); }
    .badge-amber { background: rgba(245, 158, 11, 0.15); color: #FBBF24; border: 1px solid rgba(245, 158, 11, 0.3); }
    .badge-red { background: rgba(239, 68, 68, 0.15); color: #FCA5A5; border: 1px solid rgba(239, 68, 68, 0.3); }
    
    /* Custom sleek inputs and selectboxes styling */
    div[data-baseweb="select"] {
        background-color: rgba(255, 255, 255, 0.05) !important;
        border-radius: 12px !important;
        border: 1px solid rgba(255, 255, 255, 0.1) !important;
    }
    
    /* Premium button styles mimicking the design */
    div.stButton > button:first-child {
        background: linear-gradient(135deg, #10B981 0%, #059669 100%) !important;
        color: #FFFFFF !important;
        border: none !important;
        padding: 12px 28px !important;
        border-radius: 14px !important;
        font-weight: 600 !important;
        letter-spacing: 0.02em !important;
        box-shadow: 0 4px 20px rgba(16, 185, 129, 0.35) !important;
        transition: all 0.25s ease !important;
    }
    div.stButton > button:first-child:hover {
        transform: scale(1.03) !important;
        box-shadow: 0 6px 25px rgba(52, 211, 153, 0.6) !important;
    }
    
    /* Simulated diagnostic flashing dots */
    .pulse-dot {
        width: 10px;
        height: 10px;
        background-color: #34D399;
        border-radius: 50%;
        display: inline-block;
        margin-right: 8px;
        box-shadow: 0 0 10px #10B981;
        animation: blinker 1.5s linear infinite;
    }
    @keyframes blinker {
        50% { opacity: 0; }
    }
    
    /* Custom styling for Bottom Navigation mimicking Immersive UI specifications */
    .bottom-nav-container {
        position: fixed;
        bottom: 0;
        left: 0;
        right: 0;
        height: 75px;
        background: rgba(4, 13, 6, 0.85);
        backdrop-filter: blur(20px);
        border-top: 1px solid rgba(255, 255, 255, 0.08);
        border-radius: 20px 20px 0 0;
        display: flex;
        justify-content: space-around;
        align-items: center;
        z-index: 9999;
        padding: 0 20px;
        box-shadow: 0 -10px 30px rgba(0, 0, 0, 0.5);
    }
    
    /* Floating circular Avatar */
    .avatar-glow {
        border: 2px solid rgba(52, 211, 153, 0.4);
        box-shadow: 0 0 15px rgba(52, 211, 153, 0.3);
    }
</style>
""", unsafe_allow_html=True)

# Helper function to query real Gemini 3.5 Flash Model
def query_prakirtimitra_ai(prompt):
    api_key = os.getenv("GEMINI_API_KEY")
    if not api_key:
        return query_prakirtimitra_offline(prompt)
    
    url = f"https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key={api_key}"
    headers = {"Content-Type": "application/json"}
    payload = {
        "contents": [
            {
                "parts": [{"text": prompt}]
            }
        ],
        "systemInstruction": {
            "parts": [
                {
                    "text": (
                        "You are Prakirtimitra (Prakriti Mitra) 🌱, the wise and friendly AI Smart Plantation Assistant. "
                        "Answer plant questions, farming concerns, microgreens tips, or pest diagnoses in a highly supportive, knowledgeable style. "
                        "Answer in the user's input language automatically (English, Hindi/हिन्दी, Telugu/తెలుగు, Tamil/தமிழ், etc.). "
                        "Be encouraging, concise, scientific, and conversational like ChatGPT."
                    )
                }
            ]
        }
    }
    
    try:
        response = requests.post(url, headers=headers, data=json.dumps(payload), timeout=15)
        if response.status_code == 200:
            res_json = response.json()
            return res_json['candidates'][0]['content']['parts'][0]['text']
        else:
            return f"Service status ({response.status_code}). Serving offline insights.\n\n" + query_prakirtimitra_offline(prompt)
    except Exception:
        return "Offline botanical reserve activated.\n\n" + query_prakirtimitra_offline(prompt)

# Offline botanical response matching
def query_prakirtimitra_offline(prompt):
    prompt_lower = prompt.lower()
    
    # Check for Telugu Commands
    if "నివారణ" in prompt or "రోగం" in prompt or "spots" in prompt_lower:
        return (
            "🌿 **Prakirtimitra Diagnosis:**\n"
            "• **ఆకు మచ్చలు (Leaf Spots):** ఆకులపై నీటి మచ్చలు పడకుండా ఉంచండి. వేప నూనె (Neem oil) లేదా రాగి ఆధారిత శిలీంద్ర సంహారిణి స్ప్రే చేయండి.\n"
            "• **English Summary:** Prune yellow leaves immediately, spray organic neem wash, and water near root soil to limit splash fungal spread."
        )
    # Check for Hindi Commands
    elif "तुलसी" in prompt or "care" in prompt_lower or "care tips" in prompt_lower:
        return (
            "🌱 **तुलसी की देखभाल (Holy Basil Care):**\n"
            "• **मिट्टी और सिंचाई:** मिट्टी को भुरभुरा (Well-drained) रखें। अत्यधिक पानी देने से बचें।\n"
            "• **मंजरी कतरन:** समय-समय पर मंजरी (Seeds heads) को काटें। इससे पौधा घना रहता है।"
        )
    # Check for general plant question or weather/fertilizer in South Asian regional classes
    elif "నీరు" in prompt or "తண்ணீர்" in prompt or "water" in prompt_lower:
        return (
            "💧 **Watering Insights By Prakirtimitra:**\n"
            "• Ensure top-soil is completely dry before irrgating again.\n"
            "• టమోటా/మిర్చి మొక్కలకు రోజుకు 150ml (వేసవిలో 250ml) సరిపోతుంది.\n"
            "• செடிகளுக்கு காலையில் தண்ணீர் ஊற்றுவது வேர் அழுகல் நோயை தடுக்கும்."
        )
    else:
        return (
            "🌱 **Prakirtimitra Expert Guidance:**\n"
            "Use organic compost to richify the soil, supply 3-4 hours of indirect sunlight daily, and keep ventilation high. Let me know if you need specific guides in Telugu (తెలుగు), Hindi (हिन्दी), or Tamil (தமிழ்)!"
        )

# OpenWeather API integration helper
def fetch_weather_api(city_name):
    # Retrieve the API key securely from environment variables
    api_key = os.getenv("OPENWEATHER_API_KEY")
    if not api_key or api_key == "your_openweather_api_key_here":
        # Realistic fallback simulation with gentle variations to match typical tropical/subcontinental climates
        random.seed(city_name)
        sim_temp = round(random.uniform(22.0, 36.0), 1)
        sim_humidity = random.randint(45, 85)
        # Randomly simulate rainy status for mock showcase transitions based on seed
        has_rain = (hash(city_name) % 3 == 0)
        sim_precip = round(random.uniform(1.2, 8.5), 1) if has_rain else 0.0
        sim_desc = "light rain showers" if has_rain else "scattered clouds"
        return {
            "city": city_name.strip().title(),
            "temp": sim_temp,
            "humidity": sim_humidity,
            "description": sim_desc,
            "precipitation": sim_precip,
            "is_mock": True
        }
    
    url = f"https://api.openweathermap.org/data/2.5/weather?q={city_name.strip()}&appid={api_key}&units=metric"
    try:
        res = requests.get(url, timeout=5)
        if res.status_code == 200:
            data = res.json()
            # OpenWeather reports rain under rain.1h or rain.3h
            precip = 0.0
            if "rain" in data:
                precip = data["rain"].get("1h", data["rain"].get("3h", 0.0))
            elif "snow" in data:
                precip = data["snow"].get("1h", data["snow"].get("3h", 0.0))
            
            return {
                "city": data.get("name", city_name.strip().title()),
                "temp": data["main"]["temp"],
                "humidity": data["main"]["humidity"],
                "description": data["weather"][0]["description"],
                "precipitation": precip,
                "is_mock": False
            }
        else:
            # Graceful error matching
            return {
                "city": city_name.strip().title(),
                "temp": 28.0,
                "humidity": 60,
                "description": f"unfavorable conditions (API Status: {res.status_code})",
                "precipitation": 0.0,
                "is_mock": True,
                "error": f"API Error {res.status_code}"
            }
    except Exception as e:
        return {
            "city": city_name.strip().title(),
            "temp": 27.5,
            "humidity": 55,
            "description": "gale clouds (Connection timeout)",
            "precipitation": 0.0,
            "is_mock": True,
            "error": str(e)
        }

# App Data Setup
CROP_DATA = {
    "🌱 Ficus Lyrata (Luna)": {"moisture": "42%", "light": "780 lx", "temp": "24°C", "health": 85, "water": "Needs 240ml of water in 2 hrs"},
    "🍅 Cherry Tomato": {"moisture": "65%", "light": "1200 lx", "temp": "28°C", "health": 92, "water": "Watered 1 hr ago. Next cycle in 18 hrs"},
    "🌿 Organic Sweet Basil": {"moisture": "52%", "light": "950 lx", "temp": "26°C", "health": 78, "water": "Slight dry alert! Irrigate 120ml soon"},
    "🌵 Aloe Vera Succulent": {"moisture": "18%", "light": "1500 lx", "temp": "31°C", "health": 95, "water": "High drought resistance. No watering needed for 6 days"}
}

SOILS = {
    "Loam Wood Soil": {"ph": "6.5 - 7.0", "nutrients": "High organic compost, balanced N-P-K", "indicator": "🟢 Outstanding visual biological balance"},
    "Clay Field Soil": {"ph": "7.2 - 7.8", "nutrients": "High mineral salts, compact water bounds", "indicator": "🟤 Dense structure (Needs gypsum)"},
    "Sandy Coast Soil": {"ph": "6.0 - 6.5", "nutrients": "Low organic matter, quick drain profile", "indicator": "🟡 Dry granular matrix"},
    "Acidic Peat Humus": {"ph": "4.5 - 5.5", "nutrients": "High decomposition humus, low phosphorus", "indicator": "⚫ Spongy acid moisture mix"}
}

# ----------------- COHESIVE HEADER -----------------
st.markdown("""
<div style="display: flex; justify-content: space-between; align-items: center; padding: 15px 0; margin-bottom: 20px; border-bottom: 1px solid rgba(255,255,255,0.08)">
    <div style="display: flex; align-items: center; gap: 12px;">
        <div style="width: 44px; height: 44px; rounded: 12px; background: linear-gradient(135deg, #10B981 0%, #059669 100%); display: flex; align-items: center; justify-content: center; border-radius: 12px; box-shadow: 0 4px 15px rgba(16,185,129,0.3)">
            <span style="font-size: 24px;">🌱</span>
        </div>
        <div>
            <h2 style="margin: 0; font-size: 1.6rem; letter-spacing: -0.01em;" class="neon-glow">EcoFriend AI</h2>
            <p style="margin: 0; font-size: 0.8rem; color: #34D399; font-weight: 600;">PORTABLE PLANTATION INTELLIGENCE</p>
        </div>
    </div>
    <div style="display: flex; align-items: center; gap: 15px;">
        <div class="glass-card" style="padding: 10px 18px !important; margin: 0 !important; border-radius: 14px; display: flex; align-items: center; gap: 8px;">
            <span style="font-weight: 700; color: #34D399; font-size: 1rem;">🌿 {st.session_state.eco_points} XP</span>
        </div>
        <div style="width: 42px; height: 42px; border-radius: 50%; overflow: hidden; background: rgba(52,211,153,0.1); display: flex; align-items: center; justify-content: center;" class="avatar-glow">
            <span style="font-size: 20px;">🧑‍🌾</span>
        </div>
    </div>
</div>
""", unsafe_allow_html=True)

# ----------------- AUTH GATE -----------------
if not st.session_state.is_logged_in:
    st.markdown("""
    <div style="text-align: center; margin: 40px 0;">
        <h1 style="font-size: 2.8rem; margin-bottom: 10px;" class="neon-glow">Secure Gateway</h1>
        <p style="color: #94A3B8; max-width: 600px; margin: 0 auto; font-size: 1.1rem;">
            Unlock advanced plantation CNN diagnosis, custom interactive weather telemetries, and personalized advice from Prakirtimitra.
        </p>
    </div>
    """, unsafe_allow_html=True)

    col_l, col_center, col_r = st.columns([1, 1.8, 1])
    with col_center:
        st.markdown("""<div class="glass-card">""", unsafe_allow_html=True)
        st.markdown("<h3 style='margin-top:0; text-align:center;'>🔐 Sign In to EcoFriend</h3>", unsafe_allow_html=True)
        
        login_opt = st.radio("Access Credentials", ["🔐 Local Simulation Account", "🌐 Single Sign-On (Google Authentication)"], label_visibility="collapsed")
        
        if "Local" in login_opt:
            usr = st.text_input("Botany Student / Farmer Email", value="gardener@domain.com")
            pwd = st.text_input("Secure Passcode Token", type="password", value="••••••••••")
            
            st.markdown("<br/>", unsafe_allow_html=True)
            col_b1, col_b2 = st.columns(2)
            with col_b1:
                if st.button("Unlock Dashboard 🔓"):
                    st.session_state.is_logged_in = True
                    st.session_state.username = usr.split("@")[0].title()
                    st.toast("👋 Authorization Unlocked successfully!", icon="🔥")
                    st.rerun()
            with col_b2:
                st.markdown("<p style='text-align:right; font-size:0.85rem; padding-top:12px; color:#A0AEC0;'>No account yet? Register</p>", unsafe_allow_html=True)
        else:
            st.markdown("""
            <div style="text-align:center; padding: 20px 0;">
                <p style="color: #94A3B8; font-size:0.95rem;">Authenticate securely via registered academic or G-Workspace identities:</p>
            </div>
            """, unsafe_allow_html=True)
            if st.button("Continue with Google 🌐"):
                st.session_state.is_logged_in = True
                st.session_state.username = "Green Pioneer"
                st.toast("👋 Authorized safely via Google SSO!", icon="🛡️")
                st.rerun()
                
        st.markdown("""</div>""", unsafe_allow_html=True)

    # Informational CTA footer
    st.markdown("""
    <div style="text-align: center; margin-top: 50px; opacity: 0.65; font-size: 0.85rem;">
        <p>Built with ❤️ for educational institutes, botanists, and local agricultural hubs.</p>
        <p>EcoFriend Smart Plantation Core Platform Engine • Version 3.5-Streamlit</p>
    </div>
    """, unsafe_allow_html=True)
    st.stop()


# ----------------- LOGGED IN APPLICATION ----------------- if authorized
st.markdown(f"#### Welcome back, {st.session_state.username}! 🌱 | Overall Eco Standing: Premium")

# --- CUSTOM FANCY BOTTOM TAB NAVIGATION HUB ---
# To make navigation extremely immersive and visually impactful, we construct gorgeous green-neon cards that act as tabs!
tab1, tab2, tab3, tab4, tab5, tab6 = st.columns(6)

with tab1:
    if st.button("🏡 Dashboard", use_container_width=True):
        st.session_state.current_page = "Dashboard"
        st.rerun()
with tab2:
    if st.button("⚙️ Prakirtimitra", use_container_width=True):
        st.session_state.current_page = "Prakirtimitra"
        st.rerun()
with tab3:
    if st.button("🧬 Diagnosis", use_container_width=True):
        st.session_state.current_page = "Diagnosis"
        st.rerun()
with tab4:
    if st.button("📈 Growth Proj", use_container_width=True):
        st.session_state.current_page = "Growth Proj"
        st.rerun()
with tab5:
    if st.button("🌸 Recommender", use_container_width=True):
        st.session_state.current_page = "Recommender"
        st.rerun()
with tab6:
    if st.button("👥 Forum/Mkt", use_container_width=True):
        st.session_state.current_page = "Community"
        st.rerun()

st.markdown("<hr style='margin:10px 0; border: none; border-top: 1px solid rgba(255,255,255,0.08);'/>", unsafe_allow_html=True)


# ================= PAGE 1: IMMERSIVE DASHBOARD =================
if st.session_state.current_page == "Dashboard":
    
    # 1. AI STATUS HERO SECTION (Drawn exactly as specified in the UI Theme instructions)
    col_hero, col_bento = st.columns([1, 1.3])
    
    with col_hero:
        # Create interactive plant selector
        chosen_crop = st.selectbox("Select Active Plant Focus", list(CROP_DATA.keys()))
        crop_details = CROP_DATA[chosen_crop]
        
        # Draw circular health ring in premium dynamic HTML SVG
        health_color = "#34D399" if crop_details["health"] >= 85 else "#FBBF24"
        stroke_dash = 2 * 3.14159 * 70
        stroke_offset = stroke_dash * (1 - crop_details["health"]/100)
        
        st.markdown(f"""
        <div class="glass-card" style="text-align: center; position: relative; overflow: hidden; background: rgba(52, 211, 153, 0.05) !important;">
            <div style="position: absolute; top: -50px; right: -50px; opacity: 0.15; font-size: 110px;">eco</div>
            
            <div style="display: flex; justify-content: center; align-items: center; margin: 15px 0;">
                <svg width="180" height="180" viewBox="0 0 180 180">
                    <circle cx="90" cy="90" r="70" stroke="rgba(255,255,255,0.03)" stroke-width="12" fill="transparent" />
                    <circle cx="90" cy="90" r="70" stroke="{health_color}" stroke-dasharray="{stroke_dash}" stroke-dashoffset="{stroke_offset}" stroke-width="12" fill="transparent" stroke-linecap="round" transform="rotate(-90 90 90)" />
                    <text x="90" y="95" text-anchor="middle" fill="#FFFFFF" font-size="34" font-weight="700" font-family="'Inter', sans-serif">{crop_details["health"]}%</text>
                    <text x="90" y="125" text-anchor="middle" fill="#94A3B8" font-size="10" font-weight="600" letter-spacing="1">OVERALL HEALTH</text>
                </svg>
            </div>
            
            <h3 style="margin: 5px 0;">{chosen_crop}</h3>
            <p style="color: #A7F3D0; font-size: 0.95rem; font-style: italic;">{crop_details["water"]}</p>
        </div>
        """, unsafe_allow_html=True)
        
    with col_bento:
        # QUICK STATS BENTO BOX (From immersive UI)
        st.markdown("### 📊 Active Telemetry Bento")
        
        col_b1, col_b2, col_b3 = st.columns(3)
        with col_b1:
            st.markdown(f"""
            <div class="glass-card" style="text-align: center; padding: 18px !important;">
                <p style="font-size: 24px; margin: 0 0 5px 0;">💧</p>
                <p style="font-size: 0.75rem; color:#94A3B8; margin:0;">Moisture</p>
                <h4 style="margin:5px 0 0 0; font-size:1.3rem;">{crop_details["moisture"]}</h4>
            </div>
            """, unsafe_allow_html=True)
        with col_b2:
            st.markdown(f"""
            <div class="glass-card" style="text-align: center; padding: 18px !important;">
                <p style="font-size: 24px; margin: 0 0 5px 0;">☀️</p>
                <p style="font-size: 0.75rem; color:#94A3B8; margin:0;">Light Intensity</p>
                <h4 style="margin:5px 0 0 0; font-size:1.3rem;">{crop_details["light"]}</h4>
            </div>
            """, unsafe_allow_html=True)
        with col_b3:
            st.markdown(f"""
            <div class="glass-card" style="text-align: center; padding: 18px !important;">
                <p style="font-size: 24px; margin: 0 0 5px 0;">🌡️</p>
                <p style="font-size: 0.75rem; color:#94A3B8; margin:0;">Temperature</p>
                <h4 style="margin:5px 0 0 0; font-size:1.3rem;">{crop_details["temp"]}</h4>
            </div>
            """, unsafe_allow_html=True)
            
        # AI Assistant Tip Box exactly as shown in html theme
        st.markdown(f"""
        <div class="glass-card" style="display: flex; gap: 16px; align-items: center; margin-top: -10px;">
            <div style="font-size: 32px;">🤖</div>
            <div>
                <b style="color: #34D399; font-size: 0.95rem;">Prakirtimitra Wisdom</b>
                <p style="color: #CBD5E0; font-size: 0.85rem; margin: 3px 0 0 0;">
                    "In India, direct solar heat at {crop_details["temp"]} requires slight indirect canopy shade during 12pm-3pm to prevent margin scorching."
                </p>
            </div>
        </div>
        """, unsafe_allow_html=True)

    # 2. REAL-TIME WEATHER & WATERING RECOMMENDATION (OpenWeather API)
    st.markdown("### 🌦️ Local Real-time Weather & AI Watering Advisor")
    col_w_input, col_w_details = st.columns([1, 1.8])
    with col_w_input:
        st.markdown('<div class="glass-card" style="padding: 20px !important;">', unsafe_allow_html=True)
        city_name = st.text_input("📍 Current City Location:", value="Bangalore")
        fetch_clicked = st.button("Query OpenWeather Node 🚀")
        st.markdown('</div>', unsafe_allow_html=True)
        
    with col_w_details:
        # Fetch weather data dynamically
        weather = fetch_weather_api(city_name)
        
        # Analyze parameters to formulate precision advice
        p_lvl = weather.get("precipitation", 0.0)
        t_lvl = weather.get("temp", 26.0)
        h_lvl = weather.get("humidity", 55)
        desc = weather.get("description", "clear sky")
        
        if p_lvl > 5.0:
            advice = "🌧️ **CRITICAL ADVICE: Heavy Local Precipitation Detected ({:.1f} mm/h).** DO NOT water your plants today. Your garden soil has received optimal rainfall. Waterlogging blocks oxygen to the root structure."
            advice_color = "#EF4444"  # Red theme for heavy rain halt
            badge_class = "badge-red"
        elif p_lvl > 0.5:
            advice = "🌦️ **MODERATE ADVICE: Light Showers Detected ({:.1f} mm/h).** Automated watering quantity should be adjusted by **-50%**. Topsoil capillary moisture remains high. Postpone scheduled irrigation cycles by 12-24 hours."
            advice_color = "#F59E0B"  # Yellow/Amber theme for adjustment
            badge_class = "badge-amber"
        elif t_lvl > 32.0:
            advice = "🔥 **ADVISORY: Scorching Climate / Heat Wave ({:.1f}°C).** No precipitation registered. Automated advice: Increase standard irrigation quantity by **+25%** to offset high transpiration rates and maintain root hydration."
            advice_color = "#38BDF8"  # Blue theme to counter high heat
            badge_class = "badge-amber"
        else:
            advice = "🟢 **STANDARD ADVICE: Normal Local Weather Conditions ({:.1f}°C).** Continue with standard recommended plantation schedules (e.g. 240ml for Ficus Lyrata, or direct soil moisture probe instructions)."
            advice_color = "#10B981"  # Pure Emerald for normal
            badge_class = "badge-green"
            
        weather_source = "🟢 Live Web API Node" if not weather.get("is_mock", True) else "⚠️ Fallback Simulated Node (API Key not set)"
        
        st.markdown(f"""
        <div class="glass-card" style="border-left: 5px solid {advice_color} !important; padding: 22px !important;">
            <div style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 12px;">
                <h4 style="margin: 0; color: #FFFFFF !important;">{weather['city']} Weather Telemetry</h4>
                <span class="badge-capsule {badge_class}" style="margin: 0;">{weather_source}</span>
            </div>
            
            <div style="display: flex; justify-content: space-around; background: rgba(255,255,255,0.02); padding: 10px; border-radius: 12px; margin-bottom: 15px;">
                <div style="text-align: center;">
                    <span style="font-size: 0.75rem; color: #94A3B8;">Temperature</span>
                    <p style="margin: 3px 0 0 0; font-size: 1.15rem; font-weight: 700; color: #FFFFFF;">{t_lvl:.1f}°C</p>
                </div>
                <div style="text-align: center;">
                    <span style="font-size: 0.75rem; color: #94A3B8;">Humidity</span>
                    <p style="margin: 3px 0 0 0; font-size: 1.15rem; font-weight: 700; color: #FFFFFF;">{h_lvl}%</p>
                </div>
                <div style="text-align: center;">
                    <span style="font-size: 0.75rem; color: #94A3B8;">Precipitation</span>
                    <p style="margin: 3px 0 0 0; font-size: 1.15rem; font-weight: 700; color: #34D399;">{p_lvl:.1f} mm/h</p>
                </div>
                <div style="text-align: center;">
                    <span style="font-size: 0.75rem; color: #94A3B8;">Condition</span>
                    <p style="margin: 3px 0 0 0; font-size: 0.9rem; font-weight: 700; color: #A7F3D0; text-transform: capitalize;">{desc}</p>
                </div>
            </div>
            
            <div style="background: rgba(255, 255, 255, 0.04); padding: 14px; border-radius: 12px;">
                <span style="font-size: 0.75rem; font-weight: 700; color: #34D399; letter-spacing: 0.05em; text-transform: uppercase;">🤖 AI Precision Irrigation Advice:</span>
                <p style="margin: 6px 0 0 0; color: #F1F5F9; font-size: 0.95rem; line-height: 1.5;">{advice.format(p_lvl if 'Precipitation' in advice or 'Showers' in advice else t_lvl)}</p>
            </div>
        </div>
        """, unsafe_allow_html=True)

    # Historical Action and Diary Updates
    st.markdown("### 📖 Dynamic Plantation Log & Progress Tracker")
    col_l1, col_l2 = st.columns([1.5, 1])
    
    with col_l1:
        for entry in st.session_state.diary_entries:
            st.markdown(f"""
            <div style="border-left: 4px solid #10B981; padding: 12px 20px; background: rgba(255,255,255,0.02); border-radius: 0 12px 12px 0; margin-bottom: 12px;">
                <span class="badge-capsule badge-green">{entry['health']}</span>
                <span style="font-size: 0.8rem; color:#94A3B8;">Log date: {entry['date']}</span>
                <p style="margin: 8px 0 0 0; color:#E2E8F0; font-size:0.95rem;">{entry['text']}</p>
            </div>
            """, unsafe_allow_html=True)
            
    with col_l2:
        with st.expander("📝 Write Log Entry to Decentralized DB"):
            log_date = st.date_input("Observation Record Date")
            log_state = st.selectbox("Plant Health Index", ["Excellent Health 🟢", "Vegetating Rapidly 🌿", "Dry Alert ⚠️", "Wilt Action Required 🔴"])
            log_obs = st.text_area("Observations (Soil moisture status, new sprouts, pest traces...)")
            
            if st.button("Broadcast Plantation Log"):
                st.session_state.diary_entries.insert(0, {
                    "date": str(log_date),
                    "text": log_obs if log_obs else "Manual care routine verified.",
                    "health": log_state
                })
                st.session_state.eco_points += 15
                st.toast("🎯 Diary Log Saved! Earned +15 XP", icon="🏆")
                st.rerun()


# ================= PAGE 2: PRAKIRTIMITRA CHATBOT =================
elif st.session_state.current_page == "Prakirtimitra":
    st.markdown("## 🗣️ Prakirtimitra (Prakriti Mitra) 🌱 - Live Assistant Node")
    st.write("Prakirtimitra is your specialized agrarian AI. Ask any questions about microgreens, organic fertilizers, crop cycles, or pest controls in all major languages!")
    
    # Showcase trilingual input commands
    st.markdown("""
    <div class="glass-card" style="padding: 15px !important;">
        <span style="font-size:0.85rem; color:#34D399; font-weight:600;">🗣️ EXAMPLES TRILINGUAL SYSTEM PROMPTS / триязычный:</span>
        <div style="margin-top:8px;">
            <span class="badge-capsule badge-green">ENG: "What are natural organic remedies to treat leaf spot?"</span>
            <span class="badge-capsule badge-amber">हिन्दी: "तुलसी के पौधे में मंजरी हटाने के क्या फायदे हैं?"</span>
            <span class="badge-capsule badge-red">తెలుగు/தமிழ்: "టమోటా ఆకు ముడత నివారణ సూచనలు ఏమిటి?"</span>
        </div>
    </div>
    """, unsafe_allow_html=True)
    
    # Active Listening Pulse Logo
    st.markdown("""
    <div style="text-align: center; margin: 25px 0;">
        <div class="pulse-dot" style="width: 15px; height: 15px;"></div>
        <span style="font-size:0.9rem; color:#34D399; font-weight:700; letter-spacing:0.05em;">PRAKIRTIMITRA SPEECH CHANNELS ACTIVE</span>
    </div>
    """, unsafe_allow_html=True)

    # Chat render
    for chat in st.session_state.chatbot_history:
        is_bot = chat["role"] == "assistant"
        role_title = "🤖 Bot" if is_bot else "👤 Student/Farmer"
        border_col = "rgba(52, 211, 153, 0.2)" if is_bot else "rgba(255,255,255,0.06)"
        bg_col = "rgba(16, 185, 129, 0.05)" if is_bot else "rgba(255,255,255,0.02)"
        
        st.markdown(f"""
        <div class="glass-card" style="border-color: {border_col} !important; background: {bg_col} !important; padding: 18px !important;">
            <b style="color:#34D399; font-size:0.95rem;">{role_title}</b>
            <p style="margin: 5px 0 0 0; color:#F1F5F9; font-size:0.95rem; line-height: 1.5;">{chat['text']}</p>
        </div>
        """, unsafe_allow_html=True)

    # Input elements
    voice_preset = st.selectbox(
        "🎙️ Speech Simulator Input:",
        ["Type or speak manually...", 
         "What are natural organic remedies to treat leaf spot?", 
         "तुलसी के पौधे में मंजरी हटाने के क्या फायदे हैं? (Hindi)", 
         "టమోటా ఆకు ముడత వర్షాకాలంలో ఎలా వస్తుంది? (Telugu)"]
    )
    
    raw_text = st.text_input("Ask Prakirtimitra any doubt:", placeholder="Ask questions about soil nutrients, pH, water capacity...")
    
    col_btn, _ = st.columns([1, 2])
    with col_btn:
        if st.button("Synthesize Question 🚀"):
            input_query = raw_text if raw_text else (voice_preset if voice_preset != "Type or speak manually..." else "")
            
            if input_query:
                st.session_state.chatbot_history.append({"role": "user", "text": input_query, "lang": "Autodetect"})
                
                with st.spinner("Prakirtimitra AI is evaluating neural responses..."):
                    response_text = query_prakirtimitra_ai(input_query)
                    st.session_state.chatbot_history.append({"role": "assistant", "text": response_text, "lang": "Multilingual"})
                    st.session_state.eco_points += 10
                    st.rerun()


# ================= PAGE 3: LEAF DISEASE DIAGNOSIS =================
elif st.session_state.current_page == "Diagnosis":
    st.markdown("## 📸 CNN Leaf Disease Diagnostics Deep Scanner")
    st.write("EcoFriend integrates a Deep Leaf spot Convolutional Neural Network. Scan a sample leaf to localize fungal, spider mite, or chlorosis infections instantly.")
    
    col_inp, col_out = st.columns(2)
    
    with col_inp:
        st.markdown("""<div class="glass-card">""", unsafe_allow_html=True)
        st.markdown("<h4>📥 Image Diagnostic Port</h4>", unsafe_allow_html=True)
        file_val = st.file_uploader("Upload leaf picture coordinates (.jpeg / .png)", type=["jpg", "png", "jpeg"])
        
        simulated_preset = st.selectbox("Or trigger quick-test calibration sample:", ["-- Select Specimen --", "Tobacco Mosaic Virus (Foliage Wilt)", "Powdery Leaf Mildew (Rose Spore)", "Healthy Sweet Basil Specimen"])
        st.markdown("""</div>""", unsafe_allow_html=True)
        
    with col_out:
        if file_val is not None or simulated_preset != "-- Select Specimen --":
            status_cls = simulated_preset if file_val is None else "Diagnostic Specimen File"
            
            with st.spinner("Processing CNN activation maps..."):
                time.sleep(1.2)
                
                # Setup analytical results mock
                if "Tobacco" in status_cls:
                    lbl = "Tobacco Mosaic Virus (Bacterial Strain)"
                    conf = "94.2%"
                    severity = "Critical Warning - Quarantine Area"
                    cause = "Mechanical transfer between tool assets during heavy damp seasons."
                    cure = "Isolate pot structure immediately. Apply copper-sulfate spray mix dynamically twice a week."
                    col_alert = "#EF4444"
                elif "Powdery" in status_cls:
                    lbl = "Powdery Leaf Mildew (Fungal Growth)"
                    conf = "88.7%"
                    severity = "Moderate"
                    cause = "Poor gaseous exchange in highly tight closed structures."
                    cure = "Spray water combined with 1% organic neem wash solution. Enhance horizontal fan ventilation."
                    col_alert = "#F59E0B"
                else:
                    lbl = "Healthy Plant Tissue Matrix"
                    conf = "99.1%"
                    severity = "Perfect Index"
                    cause = "Outstanding water telemetry alignment with local moisture counts."
                    cure = "Continue scheduled daily organic trace feedings."
                    col_alert = "#34D399"
                    
                st.markdown(f"""
                <div class="glass-card" style="border-left: 5px solid {col_alert} !important;">
                    <span class="pulse-dot"></span><b style="color: {col_alert};">AI TELEMETRY VERDICT:</b>
                    <h3 style="margin-top: 5px; color:#FFFFFF !important;">{lbl}</h3>
                    <p style="color: #94A3B8;">Prediction Confidence Score: <b>{conf}</b></p>
                    <p style="color: #F8FAFC;"><b>Root Cause Analysis:</b> {cause}</p>
                    <div style="background: rgba(255,255,255,0.04); padding: 12px; border-radius: 10px; margin-top: 10px;">
                        <span style="color: #34D399; font-weight:700; font-size:0.85rem;">🏥 INTEGRATED CURE TREATMENT PLAN:</span>
                        <p style="margin: 4px 0 0 0; color: #E2E8F0; font-size: 0.9rem;">{cure}</p>
                    </div>
                </div>
                """, unsafe_allow_html=True)
                
                # Visual localization overlay simulation
                st.image("https://images.unsplash.com/photo-1599599810769-bcde5a160d32?auto=format&fit=crop&w=600&q=80", caption="CNN Spot Localizer Bounding Box (Infected Leaf Tissue Region Outlines highlighted in green/amber)", width=350)
                
                if st.session_state.eco_points < 300:
                    st.session_state.eco_points += 30
                    st.toast("🦠 Fungal diagnostic data logged. Earned +30 XP!", icon="💚")


# ================= PAGE 4: PREDICTIVE GROWTH =================
elif st.session_state.current_page == "Growth Proj":
    st.markdown("## 📈 AI Growth Vector Predictor")
    st.write("Utilize machine learning linear-regression algorithms to project plant foliage growth limits based on moisture, pH, and local sunshine durations.")
    
    col_left, col_right = st.columns(2)
    
    with col_left:
        st.markdown("""<div class="glass-card">""", unsafe_allow_html=True)
        foliage_species = st.selectbox("Select Target Species for Model", ["Ficus Lyrata Dwarf", "Cherry Tomato Shrub", "Medicinal Aloe Succulent"])
        growth_weeks = st.slider("Forecast Timeline Range (Weeks)", 1, 12, 6)
        caring_perf = st.slider("Water Irrigation Discipline %", 50, 100, 90)
        start_cm = st.number_input("Current Height Basis (cm)", min_value=1.0, max_value=150.0, value=12.0)
        st.markdown("""</div>""", unsafe_allow_html=True)
        
    with col_right:
        # Generate prediction dataframes dynamically
        growth_mult = {"Ficus Lyrata Dwarf": 1.4, "Cherry Tomato Shrub": 3.8, "Medicinal Aloe Succulent": 0.4}[foliage_species]
        factor = growth_mult * (caring_perf / 100.0)
        
        index_scale = [f"Week {i}" for i in range(1, growth_weeks + 1)]
        projections = []
        cur_h = start_cm
        for idx in range(1, growth_weeks + 1):
            cur_h += factor * random.uniform(0.85, 1.15)
            projections.append(round(cur_h, 1))
            
        chart_data = pd.DataFrame({
            "Evaluation Intervals": index_scale,
            "Foliage Projections (cm)": projections
        })
        
        st.success(f"Regression growth curve successfully synthesized for {foliage_species}!")
        st.line_chart(chart_data.set_index("Evaluation Intervals"))
        
        st.markdown(f"""
        <div class="glass-card">
            <h4>💡 Predictive Analytics Insights</h4>
            <p style="color: #A7F3D0; margin: 0 0 5px 0;">• Target Projected Heights inside <b>{growth_weeks} Weeks</b>: <b>{projections[-1]} cm</b>.</p>
            <p style="margin: 0;">• Expected weekly vegetative velocity coefficient: <b>{factor:.2f} cm / week</b>.</p>
        </div>
        """, unsafe_allow_html=True)


# ================= PAGE 5: ECO RECOMMENDATION =================
elif st.session_state.current_page == "Recommender":
    st.markdown("## 🌸 Geographic Plantation Match Engine")
    st.write("Find the optimum species according to physical properties, localized sunshine counts, and soil limitations.")
    
    col_inputs, col_soil_info = st.columns(2)
    
    with col_inputs:
        st.markdown("""<div class="glass-card">""", unsafe_allow_html=True)
        user_region = st.text_input("📍 Soil Collection / Regional City", value="Bangalore Urban, IN")
        soil_profile = st.selectbox("🎯 Target Mineral Foundation Match", list(SOILS.keys()))
        sunlight_val = st.slider("☀️ Ambient Direct Daily Sunlight Exposure (Hours)", 0, 12, 6)
        purpose_selected = st.multiselect("🌿 Cultivation Utility Goals", ["Air Purification", "Kitchen Spices", "Medicinal Ingredients", "Commercial Flowers"])
        st.markdown("""</div>""", unsafe_allow_html=True)
        
    with col_soil_info:
        data_soil = SOILS[soil_profile]
        st.markdown(f"""
        <div class="glass-card" style="border-color: rgba(52,211,153,0.3) !important;">
            <h4>🧪 Soil Grade Telemetry: {soil_profile}</h4>
            <p>Soil Acid Levels Index: <b>{data_soil["ph"]} pH</b></p>
            <p>Inherent Nutrient Profile: <b>{data_soil["nutrients"]}</b></p>
            <span class="badge-capsule badge-green">{data_soil["indicator"]}</span>
        </div>
        """, unsafe_allow_html=True)
        
    if st.button("Query Recommender AI System"):
        with st.spinner("Compiling compatible crop variables..."):
            time.sleep(1.0)
            st.success(f"Optimized Species List Generated for {user_region} matching {soil_profile}!")
            
            # Formulated recommended matches
            matches = [
                {"name": "Cherry Tomato Vineyard", "category": "Kitchen Spices", "difficulty": "Medium", "water": "High Volume", "benefit": "Fast yield", "emoji": "🍅"},
                {"name": "Calm ZZ Botanical", "category": "Air Purification", "difficulty": "Easy", "water": "Low Volume", "benefit": "High drought resistance", "emoji": "🌱"},
                {"name": "Heirloom Marigold Garland", "category": "Commercial Flowers", "difficulty": "Easy", "water": "Medium Volume", "benefit": "Natural wasp/pest deterrent", "emoji": "🌼"}
            ]
            
            for m in matches:
                st.markdown(f"""
                <div class="glass-card">
                    <span style="font-size: 24px; margin-right:6px;">{m["emoji"]}</span> <b style="font-size:1.1rem; color:#FFFFFF;">{m["name"]}</b>
                    <div style="margin-top: 5px;">
                        <span class="badge-capsule badge-green">Goal: {m["category"]}</span>
                        <span class="badge-capsule badge-amber">Care scale: {m["difficulty"]}</span>
                        <span class="badge-capsule badge-red">Moisture demand: {m["water"]}</span>
                    </div>
                    <p style="margin: 8px 0 0 0; color:#A1A1AA; font-size: 0.9rem;">Primary Benefit Match: {m["benefit"]}</p>
                </div>
                """, unsafe_allow_html=True)
                
            if st.session_state.eco_points < 400:
                st.session_state.eco_points += 20
                st.toast("🌸 Recommendation system processed. Earned +20 XP!", icon="🏆")


# ================= PAGE 6: COMMUNITY & MARKETPLACE =================
elif st.session_state.current_page == "Community":
    st.markdown("## 👥 EcoFriend Verified Grower Hub")
    st.write("Join botanical student circles, exchange microgreen seeds, trade pest guidelines, and swap your eco points in the marketplace.")
    
    col_forum_left, col_mkt_right = st.columns(2)
    
    with col_forum_left:
        st.markdown("### 💬 Verified Seed Discussion Forum")
        st.markdown("""
        <div class="glass-card" style="padding:18px !important;">
            <b style="color:#34D399;">👤 BotanyStudent_44:</b>
            <p style="margin: 4px 0 5px 0; font-size: 0.9rem; color:#F1F5F9;">My cherry tomato seedlings sprouted inside 4 days using Prakirtimitra's advice! Anyone in the area want to exchange marigold pods?</p>
            <span style="font-size:0.75rem; color:#94A3B8;">🌿 16 Upvotes • 3 comments</span>
        </div>
        <div class="glass-card" style="padding:18px !important;">
            <b style="color:#34D399;">👤 AgroVeteran_Hub:</b>
            <p style="margin: 4px 0 5px 0; font-size: 0.9rem; color:#F1F5F9;">Soil pH warning! If your soil is acidic peat, compost with ground eggshells to raise the lime index immediately.</p>
            <span style="font-size:0.75rem; color:#94A3B8;">🌿 34 Upvotes • 12 comments</span>
        </div>
        """, unsafe_allow_html=True)
        
        post_inp = st.text_input("Enter message on community forum:")
        if st.button("Publish Topic (+5 XP)"):
            st.session_state.eco_points += 5
            st.toast("🌿 Topic added! +5 XP Added", icon="💚")
            st.rerun()
            
    with col_mkt_right:
        st.markdown("### 🛒 Eco Rewards Marketplace")
        st.write(f"Spend accumulated Eco Points (**{st.session_state.eco_points} XP** available in safe-deposit).")
        
        st.markdown("""
        <div style="display:flex; justify-content:space-between; align-items:center; background: rgba(255,255,255,0.03); border:1px solid rgba(255,255,255,0.08); padding:10px 15px; margin-bottom:8px; border-radius:12px;">
            <span>🍅 <b>Organic Hybrid Tomato Pod Pack</b></span>
            <b style="color:#34D399;">100 XP</b>
        </div>
        <div style="display:flex; justify-content:space-between; align-items:center; background: rgba(255,255,255,0.03); border:1px solid rgba(255,255,255,0.08); padding:10px 15px; margin-bottom:8px; border-radius:12px;">
            <span>🪱 <b>Premium Worm castings (2kg)</b></span>
            <b style="color:#34D399;">150 XP</b>
        </div>
        <div style="display:flex; justify-content:space-between; align-items:center; background: rgba(255,255,255,0.03); border:1px solid rgba(255,255,255,0.08); padding:10px 15px; margin-bottom:8px; border-radius:12px;">
            <span>💧 <b>Autonomous Soil Moisture Sensor Tool</b></span>
            <b style="color:#34D399;">300 XP</b>
        </div>
        """, unsafe_allow_html=True)
        
        col_ex, _ = st.columns([1.5, 1])
        with col_ex:
            if st.button("Exchange 100 XP for Tomato Pod Pack"):
                if st.session_state.eco_points >= 100:
                    st.session_state.eco_points -= 100
                    st.toast("🎉 Seeds Pack claimed! Shipped coordinates registered to profile.", icon="📦")
                    st.rerun()
                else:
                    st.error("Insufficient Eco points balance! Complete diagnostic logs to gain XP.")


# ----------------- FOOTER CAPTURING ECO TECHNOLOGY BRANDING -----------------
st.markdown("""
<div style="text-align: center; margin-top: 50px; padding: 20px 0; border-top: 1px solid rgba(255,255,255,0.08); opacity: 0.7;">
    <p style="font-size: 0.85rem; color: #94A3B8; margin: 0;">EcoFriend Smart Plantation Tech Assistant AI System. Immersive Design Interface Theme active.</p>
</div>
""", unsafe_allow_html=True)
