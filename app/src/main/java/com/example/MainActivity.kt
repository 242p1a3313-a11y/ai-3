package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Color tokens matching the Immersive UI theme
val EcoBg = Color(0xFF040D06)
val EcoPrimaryColor = Color(0xFF22C55E)
val EcoSecondaryColor = Color(0xFF34D399)
val EcoMutedColor = Color(0xFF94A3B8)
val GlassBg = Color(0x0CFFFFFF)
val GlassBorder = Color(0x14FFFFFF)

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainScreen()
      }
    }
  }
}

// Data Classes for UI Simulation
data class PlantRecommendation(
  val name: String,
  val icon: String,
  val climate: String,
  val soil: String,
  val water: String,
  val difficulty: String,
  val care: String
)

data class SoilInfo(
  val type: String,
  val ph: String,
  val nutrients: String,
  val fertilizer: String,
  val indicator: String
)

data class ChatMessage(
  val sender: String,
  val text: String,
  val lang: String
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen() {
  var selectedTab by remember { mutableStateOf(0) }
  var ecoPoints by remember { mutableStateOf(150) }
  val context = LocalContext.current
  val listBadges = remember { mutableStateListOf("🌟 Eco Pioneer", "💧 Hydro Cadet") }

  fun awardPoints(points: Int, badgeName: String? = null) {
    ecoPoints += points
    if (badgeName != null && !listBadges.contains(badgeName)) {
      listBadges.add(badgeName)
      Toast.makeText(context, "🏆 New Badge Unlocked: $badgeName (+$points Points!)", Toast.LENGTH_LONG).show()
    } else {
      Toast.makeText(context, "🌿 Earned +$points Eco Points!", Toast.LENGTH_SHORT).show()
    }
  }

  Scaffold(
    bottomBar = {
      CustomBottomNavBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
    },
    containerColor = EcoBg,
    modifier = Modifier.fillMaxSize().navigationBarsPadding()
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(top = 16.dp)
    ) {
      // Top Navigation / Header Panel
      HeaderBar(ecoPoints = ecoPoints, badgesCount = listBadges.size)

      // Active tab screen layout transitions
      AnimatedContent(
        targetState = selectedTab,
        transitionSpec = {
          fadeIn(animationSpec = tween(220)) with fadeOut(animationSpec = tween(220))
        },
        modifier = Modifier.weight(1f)
      ) { targetTab ->
        when (targetTab) {
          0 -> DashboardTab(
            onNavigateToScanner = { selectedTab = 3 },
            onNavigateToChat = { selectedTab = 4 },
            onCheckIn = { awardPoints(10) }
          )
          1 -> PlantRecommendationTab(
            onRecommended = { awardPoints(15, "🌿 Smart Botanist") }
          )
          2 -> SoilAndWaterTab(
            onAmended = { awardPoints(15) },
            onWaterPredicted = { awardPoints(15, "💧 Hydro Cadet") }
          )
          3 -> ScannerTab(
            onScanFinished = { awardPoints(30, "🔍 Leaf Surgeon Detector") }
          )
          4 -> ChatBotTab()
        }
      }
    }
  }
}

// Custom Glassmorphic Card Helper Composable
@Composable
fun ImmersiveGlassCard(
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit
) {
  Card(
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = GlassBg),
    border = BorderStroke(1.dp, GlassBorder),
    modifier = modifier
      .then(
        if (onClick != null) {
          Modifier.clickable(onClick = onClick)
        } else Modifier
      )
  ) {
    Column(
      modifier = Modifier.padding(20.dp),
      content = content
    )
  }
}

// ----------------- SUB-COMPONENTS -----------------

@Composable
fun HeaderBar(ecoPoints: Int, badgesCount: Int) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .statusBarsPadding()
      .padding(horizontal = 24.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(EcoPrimaryColor),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Rounded.Eco,
          contentDescription = "EcoFriend Logo",
          tint = Color.White,
          modifier = Modifier.size(22.dp)
        )
      }
      Column {
        Text(
          text = "EcoFriend",
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = Color.White,
          letterSpacing = (-0.02).sp
        )
        Text(
          text = "AI Smart Assistant",
          fontSize = 11.sp,
          color = EcoSecondaryColor,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    // Points display badge
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0x1422C55E))
        .border(1.dp, Color(0x3322C55E), RoundedCornerShape(16.dp))
        .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
      Icon(
        imageVector = Icons.Rounded.Eco,
        contentDescription = "XP Icon",
        tint = EcoSecondaryColor,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "$ecoPoints XP",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = EcoSecondaryColor
      )
    }
  }
}

// ----------------- TAB 0: DASHBOARD -----------------
@Composable
fun DashboardTab(
  onNavigateToScanner: () -> Unit,
  onNavigateToChat: () -> Unit,
  onCheckIn: () -> Unit
) {
  var cityInput by remember { mutableStateOf("Bangalore") }
  var weatherTemp by remember { mutableStateOf("26°C") }
  var weatherHumidity by remember { mutableStateOf("60%") }
  var weatherPrecip by remember { mutableStateOf("0.0 mm/h") }
  var weatherDesc by remember { mutableStateOf("Scattered Clouds") }
  var weatherAdvice by remember { mutableStateOf("🟢 STANDARD ADVICE: Climate conditions stable. Proceed with standard recommended irrigation (240ml Ficus Luna).") }
  var weatherSource by remember { mutableStateOf("⚠️ Simulation (Key not set)") }
  var isWeatherLoading by remember { mutableStateOf(false) }
  var weatherPrecipVal by remember { mutableStateOf(0.0f) }

  val scope = rememberCoroutineScope()

  fun queryWeather(city: String) {
    isWeatherLoading = true
    scope.launch(kotlinx.coroutines.Dispatchers.IO) {
      val apiKey = try {
        BuildConfig.OPENWEATHER_API_KEY
      } catch (e: Exception) {
        ""
      }

      if (apiKey.isEmpty() || apiKey == "your_openweather_api_key_here" || apiKey == "OPENWEATHER_API_KEY") {
        // Safe simulation fallback logic
        kotlinx.coroutines.delay(800)
        val hash = city.lowercase().trim().hashCode()
        val hasRain = hash % 3 == 0
        val isHot = hash % 5 == 0
        val simPVal = if (hasRain) (1.2f + (Math.abs(hash) % 7).toFloat() * 0.8f) else 0.0f
        val simTVal = if (isHot) 34f else (23f + (Math.abs(hash) % 6).toFloat())

        weatherTemp = "${simTVal.toInt()}°C"
        weatherHumidity = if (hasRain) "82%" else "58%"
        weatherPrecip = "${String.format("%.1f", simPVal)} mm/h"
        weatherDesc = if (hasRain) "Light Rain Showers" else "Scattered Clouds"
        weatherPrecipVal = simPVal

        weatherAdvice = if (simPVal > 5.0f) {
          "🌧️ CRITICAL ADVICE: Heavy Precipitation Detected ($simPVal mm/h). DO NOT water plants today. Nature has supplied extensive root moisture. Waterlogging causes hypoxia."
        } else if (simPVal > 0.5f) {
          "🌦️ MODERATE ADVICE: Light Showers ($simPVal mm/h). Reduce standard watering by 50%. Postpone ongoing irrigation cycles."
        } else if (simTVal > 32f) {
          "🔥 ADVISORY: Scorch Temperature (${simTVal.toInt()}°C). Increase standard irrigation by 25% to support soil hydration against high transpiration rates."
        } else {
          "🟢 STANDARD ADVICE: Climate conditions stable. Proceed with standard recommended irrigation (240ml Ficus Luna)."
        }
        weatherSource = "⚠️ Simulated Node"
        isWeatherLoading = false
        return@launch
      }

      val url = "https://api.openweathermap.org/data/2.5/weather?q=${city.trim()}&appid=$apiKey&units=metric"
      val client = okhttp3.OkHttpClient()
      val request = okhttp3.Request.Builder().url(url).build()
      try {
        client.newCall(request).execute().use { response ->
          if (response.isSuccessful) {
            val body = response.body?.string()
            if (body != null) {
              val json = org.json.JSONObject(body)
              val main = json.getJSONObject("main")
              val tempVal = main.getDouble("temp").toFloat()
              val humVal = main.getInt("humidity")
              val weatherArray = json.getJSONArray("weather")
              val descVal = if (weatherArray.length() > 0) weatherArray.getJSONObject(0).getString("description") else "clear sky"

              var precipVal = 0.0f
              if (json.has("rain")) {
                val rain = json.getJSONObject("rain")
                precipVal = if (rain.has("1h")) rain.getDouble("1h").toFloat() else if (rain.has("3h")) rain.getDouble("3h").toFloat() else 0.0f
              } else if (json.has("snow")) {
                val snow = json.getJSONObject("snow")
                precipVal = if (snow.has("1h")) snow.getDouble("1h").toFloat() else if (snow.has("3h")) snow.getDouble("3h").toFloat() else 0.0f
              }

              weatherTemp = "${tempVal.toInt()}°C"
              weatherHumidity = "$humVal%"
              weatherPrecip = "${String.format("%.1f", precipVal)} mm/h"
              weatherDesc = descVal
              weatherPrecipVal = precipVal

              weatherAdvice = if (precipVal > 5.0f) {
                "🌧️ CRITICAL ADVICE: Heavy Local Rain Detected ($precipVal mm/h). DO NOT water today. Root hypoxia danger."
              } else if (precipVal > 0.5f) {
                "🌦️ MODERATE ADVICE: Light Showers ($precipVal mm/h). Reduce active irrigation by 50%. Topsoil remains damp."
              } else if (tempVal > 32.0f) {
                "🔥 ADVISORY: Scorch Temperature (${tempVal.toInt()}°C). Increase standard irrigation by 25% to preserve leaf density."
              } else {
                "🟢 STANDARD ADVICE: Ambient weather stable. Maintain standard scheduled waterings (240ml Ficus Luna)."
              }
              weatherSource = "🟢 Live API Node"
            }
          } else {
            weatherAdvice = "❌ API Error: Status ${response.code} (e.g. invalid city or bad API Key)."
            weatherSource = "❌ API Error"
          }
        }
      } catch (e: Exception) {
        weatherAdvice = "⚠️ Connection error: ${e.localizedMessage}. Serving mock parameters."
        weatherSource = "⚠️ Connection Error"
      } finally {
        isWeatherLoading = false
      }
    }
  }

  LaunchedEffect(Unit) {
    queryWeather("Bangalore")
  }

  val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.96f,
    targetValue = 1.04f,
    animationSpec = infiniteRepeatable(
      animation = tween(1800, easing = EaseInOutSine),
      repeatMode = RepeatMode.Reverse
    ),
    label = "PulseScale"
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 24.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Core AI Status Hero circle & statistics matching the design
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
      ) {
        // Glowing background behind the ring
        Box(
          modifier = Modifier
            .size(190.dp)
            .background(Brush.radialGradient(colors = listOf(Color(0x1A22C55E), Color.Transparent)))
        )

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
          ) {
            // Native Canvas drawing of the overall 85% Conic Health Ring
            Canvas(modifier = Modifier.fillMaxSize()) {
              // Deep background base track
              drawArc(
                color = Color(0xFF14532D),
                startAngle = -210f,
                sweepAngle = 240f,
                useCenter = false,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
              )
              // Highly visible glowing emerald tracker arc
              drawArc(
                color = EcoPrimaryColor,
                startAngle = -210f,
                sweepAngle = 240f * 0.85f,
                useCenter = false,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
              )
            }

            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.graphicsLayer {
                scaleX = pulseScale
                scaleY = pulseScale
              }
            ) {
              Text(
                text = "85%",
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color = EcoSecondaryColor,
                letterSpacing = (-0.04).sp
              )
              Text(
                text = "OVERALL HEALTH",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = EcoMutedColor,
                letterSpacing = 0.08.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = "Ficus Lyrata \"Luna\"",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Text(
            text = "Needs 240ml of water in 2 hours",
            fontSize = 13.sp,
            fontStyle = FontStyle.Italic,
            color = EcoMutedColor,
            modifier = Modifier.padding(top = 2.dp)
          )
        }
      }
    }

    // 2. Quick Stats Bento (Moisture, Light, Temp)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StatCard(
          icon = Icons.Rounded.WaterDrop,
          iconColor = EcoPrimaryColor,
          label = "Moisture",
          value = "42%",
          modifier = Modifier.weight(1f)
        )
        StatCard(
          icon = Icons.Rounded.WbSunny,
          iconColor = Color(0xFFFBBF24),
          label = "Light",
          value = "780lx",
          modifier = Modifier.weight(1f)
        )
        StatCard(
          icon = Icons.Rounded.Thermostat,
          iconColor = Color(0xFF60A5FA),
          label = "Temp",
          value = "24°C",
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 3. AI Assistant Tip Card
    item {
      ImmersiveGlassCard {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(Color(0x2B22C55E)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Rounded.Eco,
              contentDescription = "Tips",
              tint = EcoPrimaryColor,
              modifier = Modifier.size(24.dp)
            )
          }
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "AI Assistant Tip",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = EcoSecondaryColor
            )
            Text(
              text = "\"Luna's foliage capillary cells show minor transpirational dryness. Increase surrounding humidity by 10% this evening for optimal growth vectors.\"",
              fontSize = 12.sp,
              color = Color.White,
              lineHeight = 16.sp,
              modifier = Modifier.padding(top = 2.dp)
            )
          }
        }
      }
    }

    // 3.5. Real-time Weather & AI Watering Recommendation Card
    item {
      ImmersiveGlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Icon(
                imageVector = Icons.Rounded.WbSunny,
                contentDescription = "Weather Icon",
                tint = EcoSecondaryColor,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = "Weather & AI Watering Advisor",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
              )
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (weatherSource.startsWith("🟢")) Color(0x1422C55E) else Color(0x14FBBF24))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = weatherSource,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (weatherSource.startsWith("🟢")) EcoSecondaryColor else Color(0xFFFBBF24)
              )
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = cityInput,
              onValueChange = { cityInput = it },
              label = { Text("Query City Location") },
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = EcoPrimaryColor,
                unfocusedBorderColor = GlassBorder,
                focusedLabelColor = EcoPrimaryColor,
                unfocusedLabelColor = EcoMutedColor
              ),
              modifier = Modifier.weight(1.2f).height(56.dp)
            )

            Button(
              onClick = { queryWeather(cityInput) },
              colors = ButtonDefaults.buttonColors(containerColor = EcoPrimaryColor),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1f).height(50.dp)
            ) {
              if (isWeatherLoading) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
              } else {
                Text(
                  text = "Fetch Weather", 
                  color = Color.Black, 
                  fontWeight = FontWeight.Bold, 
                  fontSize = 11.sp,
                  textAlign = TextAlign.Center
                )
              }
            }
          }

          // Weather Grid Panel (Temp, Humidity, Precipitation, Condition)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0x08FFFFFF))
              .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
              Text("Temperature", fontSize = 9.sp, color = EcoMutedColor)
              Text(weatherTemp, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(top = 2.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
              Text("Humidity", fontSize = 9.sp, color = EcoMutedColor)
              Text(weatherHumidity, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(top = 2.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
              Text("Precipitation", fontSize = 9.sp, color = EcoMutedColor)
              Text(weatherPrecip, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EcoSecondaryColor, modifier = Modifier.padding(top = 2.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1.2f)) {
              Text("Condition", fontSize = 9.sp, color = EcoMutedColor)
              Text(
                text = weatherDesc, 
                fontSize = 12.sp, 
                fontWeight = FontWeight.Bold, 
                color = Color(0xFFA7F3D0), 
                modifier = Modifier.padding(top = 2.dp),
                maxLines = 1,
                textAlign = TextAlign.Center
              )
            }
          }

          // Custom styled AI Recommendation Block
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0x0DFFFFFF))
              .border(
                1.dp, 
                if (weatherPrecipVal > 3.0f) Color(0x33EF4444) else if (weatherPrecipVal > 0.0f) Color(0x33FBBF24) else Color(0x1AFFFFFF), 
                RoundedCornerShape(16.dp)
              )
              .padding(12.dp)
          ) {
            Column {
              Text(
                text = "🤖 AI Precision Irrigation Recommendation:",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = EcoSecondaryColor,
                letterSpacing = 0.05.sp
              )
              Text(
                text = weatherAdvice,
                fontSize = 11.sp,
                color = Color(0xFFF1F5F9),
                lineHeight = 15.sp,
                modifier = Modifier.padding(top = 4.dp)
              )
            }
          }
        }
      }
    }

    // 4. Compact Main Quick Action triggers
    item {
      Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Button(
          onClick = onNavigateToScanner,
          colors = ButtonDefaults.buttonColors(containerColor = GlassBg),
          border = BorderStroke(1.dp, GlassBorder),
          shape = RoundedCornerShape(18.dp),
          modifier = Modifier.weight(1f).height(54.dp).testTag("action_scan_leaf")
        ) {
          Icon(Icons.Rounded.PhotoCamera, contentDescription = "Scan", tint = EcoPrimaryColor)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Scan Leaf", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        Button(
          onClick = onNavigateToChat,
          colors = ButtonDefaults.buttonColors(containerColor = GlassBg),
          border = BorderStroke(1.dp, GlassBorder),
          shape = RoundedCornerShape(18.dp),
          modifier = Modifier.weight(1f).height(54.dp).testTag("action_ai_chat")
        ) {
          Icon(Icons.Rounded.Chat, contentDescription = "Chat", tint = EcoPrimaryColor)
          Spacer(modifier = Modifier.width(8.dp))
          Text("AI Chat", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
      }
    }

    // Daily Claim Points Check
    item {
      ImmersiveGlassCard {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Daily Care Check-in",
              fontWeight = FontWeight.Bold,
              color = Color.White,
              fontSize = 15.sp
            )
            Text(
              text = "Log environmental indices to earn dynamic points.",
              fontSize = 12.sp,
              color = EcoMutedColor
            )
          }
          Button(
            onClick = onCheckIn,
            colors = ButtonDefaults.buttonColors(containerColor = EcoPrimaryColor),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Claim +10 XP", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }
    }
    
    item { Spacer(modifier = Modifier.height(20.dp)) }
  }
}

@Composable
fun StatCard(
  icon: ImageVector,
  iconColor: Color,
  label: String,
  value: String,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = GlassBg),
    border = BorderStroke(1.dp, GlassBorder),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 14.dp, horizontal = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = iconColor,
        modifier = Modifier.size(22.dp)
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(text = label, fontSize = 11.sp, color = EcoMutedColor, fontWeight = FontWeight.Medium)
      Text(text = value, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
    }
  }
}

// ----------------- TAB 1: PLANT RECOMMENDATION -----------------
@Composable
fun PlantRecommendationTab(onRecommended: () -> Unit) {
  var userSunHours by remember { mutableFloatStateOf(4f) }
  var selectedCategory by remember { mutableStateOf("Indoor / Low Sunlight") }
  var selectedSoil by remember { mutableStateOf("Loam") }
  var listRecs by remember { mutableStateOf<List<PlantRecommendation>>(emptyList()) }
  var loadingState by remember { mutableStateOf(false) }

  val scope = rememberCoroutineScope()

  val mockRecsMap = mapOf(
    "Indoor / Low Sunlight" to listOf(
      PlantRecommendation("Snake Plant", "🌿", "Temperate", "Sandy/Loam", "Low", "Easy", "Thrives in neglect. Purifies surrounding indoor airspace."),
      PlantRecommendation("Peace Lily", "🌸", "Mild Warm", "Peat-rich", "Medium", "Medium", "Keep moist. Distinct flowers droop to flag underwatering."),
      PlantRecommendation("ZZ Plant", "🌱", "Warm", "Well-draining", "Low", "Easy", "Extremely resilient rhizomes store rich volumes of irrigation fluids.")
    ),
    "Edibles / Kitchen Garden" to listOf(
      PlantRecommendation("Cherry Tomato", "🍅", "Hot / Sunny", "Loam (Rich)", "High", "Medium", "6+ hours solar light required. Support vines with stakes."),
      PlantRecommendation("Sweet Basil", "🌿", "Temperate", "Well-drained Fertile", "Medium", "Easy", "Clip top leaves weekly to expand foliage bushes."),
      PlantRecommendation("Chili Pepper", "🌶️", "Hot / Sunny", "Sandy-Loam", "Medium", "Easy", "Needs heat. Soil stress increases active hot spiciness.")
    ),
    "Commercial / High Yield" to listOf(
      PlantRecommendation("Marigold Flower", "🌼", "Sunny", "Clay/Loam", "Medium", "Easy", "Outstanding organic companion plant to control garden pests."),
      PlantRecommendation("Aloe Vera", "🌵", "Arid / Desert", "Sandy Mix", "Low", "Easy", "Soothes skin. High commercial cosmetic extract sales revenue.")
    )
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 24.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Text(
        text = "AI Smart Plant Recommender",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
      Text(
        text = "Enter available resources to find optimal crops.",
        fontSize = 13.sp,
        color = EcoMutedColor,
        modifier = Modifier.padding(top = 2.dp)
      )
    }

    item {
      ImmersiveGlassCard {
        Text("Plantation Category Focus", fontSize = 12.sp, color = EcoSecondaryColor, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("Indoor / Low Sunlight", "Edibles / Kitchen Garden", "Commercial / High Yield").forEach { cat ->
            val active = selectedCategory == cat
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(if (active) EcoPrimaryColor else Color(0x0AFFFFFF))
                .clickable { selectedCategory = cat }
                .padding(vertical = 8.dp, horizontal = 4.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = if (cat.contains("Indoor")) "Indoor" else if (cat.contains("Edibles")) "Edibles" else "Commercial",
                color = if (active) Color.Black else Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Primary Soil Type", fontSize = 12.sp, color = EcoSecondaryColor, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("Loam", "Clay", "Sandy", "Peat").forEach { soil ->
            val active = selectedSoil == soil
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (active) EcoPrimaryColor else Color(0x0AFFFFFF))
                .clickable { selectedSoil = soil }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = soil,
                color = if (active) Color.Black else Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Sunlight Availability", fontSize = 12.sp, color = EcoSecondaryColor, fontWeight = FontWeight.Bold)
          Text("${userSunHours.toInt()} Hours/Day", fontSize = 12.sp, color = Color.White)
        }
        Slider(
          value = userSunHours,
          onValueChange = { userSunHours = it },
          valueRange = 0f..12f,
          colors = SliderDefaults.colors(
            thumbColor = EcoPrimaryColor,
            activeTrackColor = EcoPrimaryColor,
            inactiveTrackColor = Color(0x1AFFFFFF)
          )
        )

        Spacer(modifier = Modifier.height(10.dp))
        Button(
          onClick = {
            scope.launch {
              loadingState = true
              delay(1000)
              listRecs = mockRecsMap[selectedCategory] ?: emptyList()
              loadingState = false
              onRecommended()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = EcoPrimaryColor),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().height(48.dp).testTag("trigger_recommender")
        ) {
          if (loadingState) {
            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
          } else {
            Text("Run Environmental Match Neural Filter", color = Color.Black, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    if (listRecs.isNotEmpty()) {
      item {
        Text("AI Recommended Matches", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EcoSecondaryColor)
      }
      items(listRecs) { rec ->
        ImmersiveGlassCard {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            Text(rec.icon, fontSize = 32.sp)
            Column(modifier = Modifier.weight(1f)) {
              Text(rec.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 4.dp)
              ) {
                Text(
                  text = "Diff: ${rec.difficulty}",
                  fontSize = 10.sp,
                  color = EcoSecondaryColor,
                  modifier = Modifier
                    .background(Color(0x1422C55E))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                )
                Text(
                  text = "Water: ${rec.water}",
                  fontSize = 10.sp,
                  color = Color(0xFFFBBF24),
                  modifier = Modifier
                    .background(Color(0x14FBBF24))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              Text(
                text = rec.care,
                fontSize = 12.sp,
                color = EcoMutedColor,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
              )
            }
          }
        }
      }
    }
    
    item { Spacer(modifier = Modifier.height(20.dp)) }
  }
}

// ----------------- TAB 2: SOIL AND WATER LAB -----------------
@Composable
fun SoilAndWaterTab(onAmended: () -> Unit, onWaterPredicted: () -> Unit) {
  var activeSoil by remember { mutableStateOf("Loam") }
  var areaSquareMeters by remember { mutableStateOf("10") }
  var resultFertilizerDose by remember { mutableStateOf("") }
  
  var targetCrop by remember { mutableStateOf("Tomato") }
  var ambientTemp by remember { mutableFloatStateOf(28f) }
  var ambientHumidity by remember { mutableFloatStateOf(55f) }
  var calculatedWaterMl by remember { mutableStateOf(-1) }

  val mockSoilData = mapOf(
    "Loam" to SoilInfo("Loam", "6.2 - 7.2", "Balanced N-P-K concentrations", "Worm castings, balanced organic slow-release solutions", "🟢 PERFECT BALANCE"),
    "Clay" to SoilInfo("Clay", "6.5 - 7.5", "High Calcium & minerals", "Gypsum soil aggregates, structural organic composts", "🟤 HEAVY HIGH-WATER"),
    "Sandy" to SoilInfo("Sandy", "6.0 - 7.0", "Potassium-High, Nitrogen-Dry", "Blood meal, natural fish emulsion drenches", "🟡 DRY & RAPID"),
    "Peat" to SoilInfo("Peat", "4.5 - 5.5", "High Organic decaying matter", "Agricultural lime amendment to neutralize Acid", "⚫ ACIDIC SPONGE")
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 24.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Text("🧪 Smart Soil and Moisture Lab", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }

    // Soil block
    item {
      ImmersiveGlassCard {
        Text("1. Soil Health Evaluation", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = EcoSecondaryColor)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("Loam", "Clay", "Sandy", "Peat").forEach { s ->
            val active = activeSoil == s
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (active) EcoPrimaryColor else Color(0x0AFFFFFF))
                .clickable { activeSoil = s }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(s, color = if (active) Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))
        val soilDetails = mockSoilData[activeSoil]!!
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x08FFFFFF))
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Visual health code indicator:", fontSize = 10.sp, color = EcoMutedColor)
            Text(soilDetails.indicator, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, modifier = Modifier.padding(top = 2.dp))
          }
          Column(horizontalAlignment = Alignment.End) {
            Text("Optimum pH bounds:", fontSize = 10.sp, color = EcoMutedColor)
            Text(soilDetails.ph, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EcoSecondaryColor, modifier = Modifier.padding(top = 2.dp))
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text("Inherent Nutrients:", fontSize = 11.sp, color = EcoMutedColor)
        Text(soilDetails.nutrients, fontSize = 13.sp, color = Color.White)
        
        Spacer(modifier = Modifier.height(6.dp))
        Text("Soil Organic Amendments suggested:", fontSize = 11.sp, color = EcoMutedColor)
        Text(soilDetails.fertilizer, fontSize = 13.sp, color = EcoSecondaryColor, fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(16.dp))
        Text("Fertilizer Area Calculator", fontSize = 12.sp, color = EcoSecondaryColor, fontWeight = FontWeight.Bold)
        Row(
          modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = areaSquareMeters,
            onValueChange = { areaSquareMeters = it },
            label = { Text("Area Size (Sq Meters)") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = EcoPrimaryColor,
              unfocusedBorderColor = GlassBorder,
              focusedLabelColor = EcoPrimaryColor,
              unfocusedLabelColor = EcoMutedColor
            ),
            modifier = Modifier.weight(1.2f).height(56.dp)
          )
          Button(
            onClick = {
              val size = areaSquareMeters.toFloatOrNull() ?: 10f
              val coefficientN = if (activeSoil == "Sandy") 0.5f else 0.35f
              resultFertilizerDose = "${String.format("%.2f", size * coefficientN)} kg of dynamic compost"
              onAmended()
            },
            colors = ButtonDefaults.buttonColors(containerColor = EcoPrimaryColor),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f).height(50.dp)
          ) {
            Text("Calculate Dose", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
          }
        }
        if (resultFertilizerDose.isNotEmpty()) {
          Text(
            text = "AI Required dose: $resultFertilizerDose",
            fontSize = 13.sp,
            color = EcoSecondaryColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp)
          )
        }
      }
    }

    // Irrigation water calculation
    item {
      ImmersiveGlassCard {
        Text("2. Precision Water Predictor", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = EcoSecondaryColor)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("Tomato", "Chili Pepper", "Aloe Vera", "Snake Plant").forEach { crop ->
            val active = targetCrop == crop
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (active) EcoPrimaryColor else Color(0x0AFFFFFF))
                .clickable { targetCrop = crop }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(crop, color = if (active) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Ambient Temperature", fontSize = 11.sp, color = EcoMutedColor)
          Text("${ambientTemp.toInt()}°C", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Slider(
          value = ambientTemp,
          onValueChange = { ambientTemp = it },
          valueRange = 10f..45f,
          colors = SliderDefaults.colors(thumbColor = EcoPrimaryColor, activeTrackColor = EcoPrimaryColor)
        )

        Row(
          modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Surrounding Capillary Humidity", fontSize = 11.sp, color = EcoMutedColor)
          Text("${ambientHumidity.toInt()}%", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Slider(
          value = ambientHumidity,
          onValueChange = { ambientHumidity = it },
          valueRange = 15f..95f,
          colors = SliderDefaults.colors(thumbColor = EcoPrimaryColor, activeTrackColor = EcoPrimaryColor)
        )

        Spacer(modifier = Modifier.height(10.dp))
        Button(
          onClick = {
            val baseScale = when (targetCrop) {
              "Tomato" -> 450
              "Chili Pepper" -> 300
              "Aloe Vera" -> 100
              "Snake Plant" -> 50
              else -> 250
            }
            val tempFactor = 1f + ((ambientTemp - 25f) * 0.04f)
            val humFactor = 1f - ((ambientHumidity - 50f) * 0.005f)
            calculatedWaterMl = maxOf(20, (baseScale * tempFactor * humFactor).toInt())
            onWaterPredicted()
          },
          colors = ButtonDefaults.buttonColors(containerColor = EcoPrimaryColor),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
          Text("Run Precision Modeling Solver", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        if (calculatedWaterMl != -1) {
          Spacer(modifier = Modifier.height(16.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0x1422C55E))
              .border(1.dp, Color(0x3322C55E), RoundedCornerShape(12.dp))
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Target Moisture Fluid Daily:", fontSize = 11.sp, color = EcoMutedColor)
              Text("$calculatedWaterMl mL / plant", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = EcoSecondaryColor, modifier = Modifier.padding(top = 2.dp))
            }
            Text(
              text = "Rem: Water every ${if (calculatedWaterMl < 150) "4 days" else "2 days"}",
              fontSize = 11.sp,
              color = Color.White,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }
    
    item { Spacer(modifier = Modifier.height(20.dp)) }
  }
}

// ----------------- TAB 3: LEAF CAMERA SCANNER -----------------
@Composable
fun ScannerTab(onScanFinished: () -> Unit) {
  var isScanning by remember { mutableStateOf(false) }
  var simulatedResult by remember { mutableStateOf<SoilInfo?>(null) }
  var scanCategoryIndex by remember { mutableStateOf(0) }
  val scope = rememberCoroutineScope()

  val itemsDiseases = listOf(
    SoilInfo("Ficus Powdery Mildew", "Fungal infection detected.", "89.4% CNN Confidence Scale", "Spray dilute organic neem oil, improve ventilation", "MODERATE"),
    SoilInfo("Bacterial Leaf Spot", "Bacterial pathogens present.", "74.8% CNN Confidence Scale", "Prune diseased parts carefully, apply bio-copper fungicide", "CRITICAL"),
    SoilInfo("Iron Deficiency Chlorosis", "Alkaline soil block nutrient locks.", "93.1% CNN Confidence Scale", "Water with chelated liquid iron solution, adjust soil pH", "DEFICIENCY"),
    SoilInfo("Perfectly Healthy Foliole", "No pathogen signals found.", "98.6% Confidence Scale", "Excellent environment indices met. Continue regular fertilization.", "PERFECT")
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 24.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text("📸 Leaf Disease Neural Classifier", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
    Text("Capture leaf photograph or use mock presets to scan plant diseases in real time.", fontSize = 13.sp, color = EcoMutedColor)

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(260.dp)
        .clip(RoundedCornerShape(24.dp))
        .background(Color(0xFF0F1E12))
        .border(2.dp, if (isScanning) EcoPrimaryColor else GlassBorder, RoundedCornerShape(24.dp)),
      contentAlignment = Alignment.Center
    ) {
      if (isScanning) {
        val infiniteTransition = rememberInfiniteTransition("LineScan")
        val animateY by infiniteTransition.animateFloat(
          initialValue = 0f,
          targetValue = 260f,
          animationSpec = infiniteRepeatable(animation = tween(1200), repeatMode = RepeatMode.Reverse),
          label = "Line"
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
          drawLine(
            color = EcoPrimaryColor,
            start = androidx.compose.ui.geometry.Offset(0f, animateY.dp.toPx()),
            end = androidx.compose.ui.geometry.Offset(size.width, animateY.dp.toPx()),
            strokeWidth = 3.dp.toPx()
          )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          CircularProgressIndicator(color = EcoPrimaryColor)
          Spacer(modifier = Modifier.height(10.dp))
          Text("Analyzing foliage capillary patterns...", color = EcoSecondaryColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
      } else if (simulatedResult == null) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Rounded.PhotoCamera,
            contentDescription = "Camera",
            tint = EcoMutedColor,
            modifier = Modifier.size(54.dp)
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text("Foliage Camera Tracker Active", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
          Text("Position specimen leaf inside the viewfinder box", color = EcoMutedColor, fontSize = 12.sp)
        }
      } else {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(20.dp),
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Rounded.Eco,
            contentDescription = "Success Leaf",
            tint = if (simulatedResult?.indicator == "PERFECT") EcoPrimaryColor else Color(0xFFFBBF24),
            modifier = Modifier.size(48.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(simulatedResult!!.type, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
          Text(simulatedResult!!.nutrients, fontSize = 14.sp, color = EcoSecondaryColor, fontWeight = FontWeight.Bold)
          Text(
            text = "Remedy: ${simulatedResult!!.fertilizer}",
            modifier = Modifier.padding(top = 10.dp),
            fontSize = 12.sp,
            color = EcoMutedColor,
            textAlign = TextAlign.Center
          )
        }
      }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text("Select Leaf Specimen to Simulate", fontSize = 11.sp, color = EcoMutedColor, fontWeight = FontWeight.Bold)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        listOf("Powdery", "Spot", "Chlorosis", "Healthy").forEachIndexed { i, s ->
          val active = scanCategoryIndex == i
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (active) EcoPrimaryColor else Color(0x0AFFFFFF))
              .clickable { scanCategoryIndex = i }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(s, color = if (active) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    Button(
      onClick = {
        scope.launch {
          isScanning = true
          simulatedResult = null
          delay(2000)
          isScanning = false
          simulatedResult = itemsDiseases[scanCategoryIndex]
          onScanFinished()
        }
      },
      colors = ButtonDefaults.buttonColors(containerColor = EcoPrimaryColor),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier.fillMaxWidth().height(50.dp).testTag("trigger_leaf_scan")
    ) {
      Text("Activate Visual Diagnosis", color = Color.Black, fontWeight = FontWeight.Bold)
    }
  }
}

// ----------------- TAB 4: MULTILINGUAL CHATBOT -----------------
@Composable
fun ChatBotTab() {
  var chatInputText by remember { mutableStateOf("") }
  var activeLang by remember { mutableStateOf("English") }
  val chatbotHistory = remember {
    mutableStateListOf(
      ChatMessage("bot", "Hello, I am EcoFriend! 🌿 How can I help you grow plants or treat leaf capillary issues today?", "English")
    )
  }
  val scope = rememberCoroutineScope()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 24.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column {
      Text("🗣️ Multilingual Conversational Assistant", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
      Text("Chat natively with specialized botanical guidelines.", fontSize = 12.sp, color = EcoMutedColor)

      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        listOf("English", "Telugu (తెలుగు)", "Hindi (हिन्दी)", "Tamil (தமிழ்)").forEach { ln ->
          val active = activeLang == ln
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (active) EcoPrimaryColor else Color(0x0AFFFFFF))
              .clickable { activeLang = ln }
              .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (ln.contains("Telugu")) "Telugu" else if (ln.contains("Hindi")) "Hindi" else if (ln.contains("Tamil")) "Tamil" else "English",
              color = if (active) Color.Black else Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // Message lists scrolling
    Box(
      modifier = Modifier
        .weight(1f)
        .padding(vertical = 12.dp)
        .clip(RoundedCornerShape(18.dp))
        .background(Color(0x05FFFFFF))
        .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
        .padding(12.dp)
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(chatbotHistory) { chat ->
          val isUser = chat.sender == "user"
          Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
          ) {
            Box(
              modifier = Modifier
                .widthIn(max = 240.dp)
                .clip(
                  RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 0.dp,
                    bottomEnd = if (isUser) 0.dp else 16.dp
                  )
                )
                .background(if (isUser) Color(0x1C22C55E) else GlassBg)
                .border(1.dp, if (isUser) Color(0x3322C55E) else GlassBorder, RoundedCornerShape(16.dp))
                .padding(12.dp)
            ) {
              Column {
                Text(
                  text = if (isUser) "You" else "EcoFriend AI",
                  fontSize = 10.sp,
                  color = EcoSecondaryColor,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = chat.text,
                  fontSize = 12.sp,
                  color = Color.White,
                  lineHeight = 16.sp,
                  modifier = Modifier.padding(top = 2.dp)
                )
              }
            }
          }
        }
      }
    }

    // Preset chips
    Column(modifier = Modifier.padding(bottom = 6.dp)) {
      Text("Simulation Presets Click:", fontSize = 10.sp, color = EcoMutedColor)
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        listOf("Water Ficus?", "Cure powdery leaves?", "Compost tips?").forEach { phrase ->
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0x0DFFFFFF))
              .clickable { chatInputText = phrase }
              .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(phrase, fontSize = 10.sp, color = EcoSecondaryColor, textAlign = TextAlign.Center)
          }
        }
      }
    }

    // Footer Send Panel
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      OutlinedTextField(
        value = chatInputText,
        onValueChange = { chatInputText = it },
        placeholder = { Text("Ask botanical query...") },
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White,
          focusedBorderColor = EcoPrimaryColor,
          unfocusedBorderColor = GlassBorder,
          focusedPlaceholderColor = EcoMutedColor,
          unfocusedPlaceholderColor = EcoMutedColor
        ),
        modifier = Modifier.weight(1f).height(50.dp).testTag("chatbot_input_box")
      )

      IconButton(
        onClick = {
          if (chatInputText.isNotEmpty()) {
            val originalQuery = chatInputText
            chatbotHistory.add(ChatMessage("user", originalQuery, activeLang))
            chatInputText = ""
            scope.launch {
              delay(800)
              val replyEn: String
              val replyReg: String
              if (originalQuery.lowercase().contains("water") || originalQuery.lowercase().contains("ficus")) {
                replyEn = "Prick topsoil before irrigating to verify hydration bounds."
                replyReg = "తెలుగు: నేల పూర్తిగా ఎండిన తర్వాత మాత్రమే నీళ్ళు పోయండి."
              } else if (originalQuery.lowercase().contains("leaves") || originalQuery.lowercase().contains("powdery")) {
                replyEn = "Apply dilute baking soda spray or bio-neem leaf spray immediately."
                replyReg = "హిందी: फफूंद से बचाने के लिए नीम के तेल का छिड़काव करें।"
              } else {
                replyEn = "Add worm castings to prolong vegetation minerals in container plantings safely."
                replyReg = "தமிழ்: மண்புழு உரம் தாவரங்களின் வேர்களுக்கு சிறந்த ஊட்டம் அளிக்கும்."
              }
              chatbotHistory.add(
                ChatMessage("bot", "$replyEn\n\n💬 Translation: $replyReg", activeLang)
              )
            }
          }
        },
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(EcoPrimaryColor)
          .testTag("chatbot_send_btn")
      ) {
        Icon(Icons.Rounded.Eco, contentDescription = "Send", tint = Color.Black)
      }
    }
  }
}

// Custom Bottom Navigation Bar matching the specified Immersive UI bottom navigation shape
@Composable
fun CustomBottomNavBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .height(68.dp)
      .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
    colors = CardDefaults.cardColors(containerColor = GlassBg),
    border = BorderStroke(1.dp, GlassBorder)
  ) {
    Row(
      modifier = Modifier.fillMaxSize(),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      NavBarItem(
        icon = Icons.Rounded.Eco,
        label = "Dashboard",
        active = selectedTab == 0,
        onClick = { onTabSelected(0) }
      )
      NavBarItem(
        icon = Icons.Rounded.Settings,
        label = "Recommend",
        active = selectedTab == 1,
        onClick = { onTabSelected(1) }
      )
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(EcoPrimaryColor)
          .clickable { onTabSelected(2) },
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Rounded.Add, contentDescription = "Lab Center", tint = Color.Black, modifier = Modifier.size(24.dp))
      }
      NavBarItem(
        icon = Icons.Rounded.Analytics,
        label = "Scanner",
        active = selectedTab == 3,
        onClick = { onTabSelected(3) }
      )
      NavBarItem(
        icon = Icons.Rounded.Chat,
        label = "Voice Bot",
        active = selectedTab == 4,
        onClick = { onTabSelected(4) }
      )
    }
  }
}

@Composable
fun NavBarItem(
  icon: ImageVector,
  label: String,
  active: Boolean,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clickable(onClick = onClick)
      .padding(6.dp)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = if (active) EcoSecondaryColor else EcoMutedColor,
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.height(3.dp))
    Text(
      text = label,
      fontSize = 9.sp,
      fontWeight = FontWeight.Bold,
      color = if (active) EcoSecondaryColor else EcoMutedColor
    )
  }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
  MyApplicationTheme {
    MainScreen()
  }
}
