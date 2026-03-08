package com.prometal.app

import android.os.Bundle
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import java.util.concurrent.TimeUnit
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Handyman
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.ui.window.Dialog

// ─── ЛОКАЛИЗАЦИЯ ──────────────────────────────────────
data class AppStrings(
    val appName: String,
    val metalMarket: String,
    val gold: String,
    val silver: String,
    val today: String,
    val week: String,
    val month: String,
    val updatedJustNow: String,
    val noConnection: String,
    val loading: String,
    val gram: String,
    val oz: String,
    val quickValuation: String,
    val quickValuationSub: String,
    val weightG: String,
    val purity: String,
    val pureWeight: String,
    val value: String,
    val deal: String,
    val journal: String,
    val market: String,
    val alerts: String,
    val settings: String,
    val dealCalc: String,
    val weight: String,
    val purityHint: String,
    val purityHintSilver: String,
    val buyPrice: String,
    val sellPrice: String,
    val commission: String,
    val commissionHint: String,
    val calculate: String,
    val result: String,
    val resultFormula: String,
    val pureWeightResult: String,
    val buyCost: String,
    val sellCost: String,
    val profit: String,
    val saveToJournal: String,
    val saved: String,
    val journalTitle: String,
    val totalProfit: String,
    val deals: String,
    val noDeals: String,
    val noDealsHint: String,
    val select: String,
    val cancel: String,
    val clearAll: String,
    val clearAllConfirm: String,
    val deleteSelected: String,
    val deleteAll: String,
    val signalsTitle: String,
    val signalsSub: String,
    val signalsActive: String,
    val notifyWhen: String,
    val priceAbove: String,
    val priceBelow: String,
    val targetPrice: String,
    val activate: String,
    val disableAll: String,
    val signalSet: String,
    val settingsTitle: String,
    val yourCountry: String,
    val changeCountry: String,
    val selectCountry: String,
    val welcomeTitle: String,
    val welcomeSub: String,
    val forToday: String,
    val lastData: String,
    val perGram: String,
    val perGram999: String,
    val perGram999Silver: String,
    val pureGoldPrice: String,
    val pureSilverPrice: String
)

val stringsRu = AppStrings(
    appName = "ProMetal", metalMarket = "РЫНОК МЕТАЛЛОВ",
    gold = "ЗОЛОТО", silver = "СЕРЕБРО",
    today = "День", week = "Неделя", month = "Месяц",
    updatedJustNow = "Обновлено: только что", noConnection = "Нет соединения",
    loading = "Загрузка...", gram = "Грам", oz = "Унц",
    quickValuation = "БЫСТРАЯ ОЦЕНКА", quickValuationSub = "по мировой цене",
    weightG = "Вес (г)", purity = "Проба",
    pureWeight = "Чистый металл", value = "Стоимость",
    deal = "Сделка", journal = "Журнал", market = "Рынок",
    alerts = "Сигналы", settings = "Настройки",
    dealCalc = "КАЛЬКУЛЯТОР СДЕЛКИ",
    weight = "Вес металла", purityHint = "напр. 585, 750, 999",
    purityHintSilver = "напр. 800, 925, 999",
    buyPrice = "Цена покупки", sellPrice = "Цена продажи",
    commission = "Комиссия", commissionHint = "% (необязательно)",
    calculate = "РАССЧИТАТЬ", result = "РЕЗУЛЬТАТ",
    resultFormula = "Вес × (Проба/1000) × Цена 999.9",
    pureWeightResult = "Чистый металл", buyCost = "Стоимость покупки",
    sellCost = "Стоимость продажи", profit = "ПРИБЫЛЬ",
    saveToJournal = "✓ В журнал", saved = "✓ Сохранено",
    journalTitle = "ЖУРНАЛ СДЕЛОК", totalProfit = "СУММАРНАЯ ПРИБЫЛЬ",
    deals = "сделок", noDeals = "Сделок пока нет",
    noDealsHint = "Рассчитайте сделку и нажмите ✓\nчтобы сохранить её в журнал",
    select = "Выбрать", cancel = "Отмена", clearAll = "Очистить",
    clearAllConfirm = "Все сделок будут удалены.",
    deleteSelected = "🗑  Удалить выбранные", deleteAll = "Удалить всё",
    signalsTitle = "СИГНАЛЫ", signalsSub = "Уведомление придёт даже когда приложение закрыто",
    signalsActive = "Сигналы активны • проверка каждые 15 мин",
    notifyWhen = "Уведомить когда цена:", priceAbove = "📈  Поднимется выше",
    priceBelow = "📉  Опустится ниже", targetPrice = "Целевая цена",
    activate = "🔔  АКТИВИРОВАТЬ СИГНАЛЫ", disableAll = "Отключить все сигналы",
    signalSet = "✓ установлен", settingsTitle = "НАСТРОЙКИ",
    yourCountry = "ВАША СТРАНА", changeCountry = "Изменить страну",
    selectCountry = "ВЫБЕРИТЕ СТРАНУ", welcomeTitle = "Добро пожаловать!",
    welcomeSub = "Выберите вашу страну.\nЦены будут в местной валюте.",
    forToday = "за сегодня", lastData = "Последние данные",
    perGram = "р/г", perGram999 = "за грамм 999.9", perGram999Silver = "за грамм 999",
    pureGoldPrice = "цена чистого золота", pureSilverPrice = "цена чистого серебра"
)

val stringsEn = AppStrings(
    appName = "ProMetal", metalMarket = "METALS MARKET",
    gold = "GOLD", silver = "SILVER",
    today = "Day", week = "Week", month = "Month",
    updatedJustNow = "Updated: just now", noConnection = "No connection",
    loading = "Loading...", gram = "Gram", oz = "Oz",
    quickValuation = "QUICK VALUATION", quickValuationSub = "at world price",
    weightG = "Weight (g)", purity = "Purity",
    pureWeight = "Pure metal", value = "Value",
    deal = "Deal", journal = "Journal", market = "Market",
    alerts = "Alerts", settings = "Settings",
    dealCalc = "DEAL CALCULATOR",
    weight = "Metal weight", purityHint = "e.g. 585, 750, 999",
    purityHintSilver = "e.g. 800, 925, 999",
    buyPrice = "Buy price", sellPrice = "Sell price",
    commission = "Commission", commissionHint = "% (optional)",
    calculate = "CALCULATE", result = "RESULT",
    resultFormula = "Weight × (Purity/1000) × Price 999.9",
    pureWeightResult = "Pure metal", buyCost = "Buy cost",
    sellCost = "Sell cost", profit = "PROFIT",
    saveToJournal = "✓ To journal", saved = "✓ Saved",
    journalTitle = "DEAL JOURNAL", totalProfit = "TOTAL PROFIT",
    deals = "deals", noDeals = "No deals yet",
    noDealsHint = "Calculate a deal and press ✓\nto save it to the journal",
    select = "Select", cancel = "Cancel", clearAll = "Clear all",
    clearAllConfirm = "All deals will be deleted.",
    deleteSelected = "🗑  Delete selected", deleteAll = "Delete all",
    signalsTitle = "ALERTS", signalsSub = "Notification comes even when app is closed",
    signalsActive = "Alerts active • check every 15 min",
    notifyWhen = "Notify when price:", priceAbove = "📈  Rises above",
    priceBelow = "📉  Falls below", targetPrice = "Target price",
    activate = "🔔  ACTIVATE ALERTS", disableAll = "Disable all alerts",
    signalSet = "✓ set", settingsTitle = "SETTINGS",
    yourCountry = "YOUR COUNTRY", changeCountry = "Change country",
    selectCountry = "SELECT COUNTRY", welcomeTitle = "Welcome!",
    welcomeSub = "Select your country.\nPrices will be in local currency.",
    forToday = "for today", lastData = "Last data",
    perGram = "/g", perGram999 = "per gram 999.9", perGram999Silver = "per gram 999",
    pureGoldPrice = "pure gold price", pureSilverPrice = "pure silver price"
)

val stringsHy = AppStrings(
    appName = "ProMetal", metalMarket = "ՄԵՏԱՂՆԵՐԻ ՇՈՒԿԱ",
    gold = "ՈՍԿԻ", silver = "ԱՐԾԱԹ",
    today = "Օր", week = "Շաբ", month = "Ամիս",
    updatedJustNow = "Թարմացվել է: հենց նոր", noConnection = "Կապ չկա",
    loading = "Բեռնվում է...", gram = "Գ", oz = "Ունց",
    quickValuation = "ԱՐԱԳ ԳՆԱՀԱՏՈՒՄ", quickValuationSub = "համաշխարհային գնով",
    weightG = "Քաշ (գ)", purity = "Նմուշ",
    pureWeight = "Մաքուր մետաղ", value = "Արժեք",
    deal = "Գործարք", journal = "Մատյան", market = "Շուկա",
    alerts = "Ազդանշան", settings = "Կարգավ.",
    dealCalc = "ԳՈՐԾԱՐՔԻ ՀԱՇՎԻՉ",
    weight = "Մետաղի քաշ", purityHint = "օր. 585, 750, 999",
    purityHintSilver = "օր. 800, 925, 999",
    buyPrice = "Գնման գին", sellPrice = "Վաճառքի գին",
    commission = "Միջնորդավճար", commissionHint = "% (կամընտիր)",
    calculate = "ՀԱՇՎԵԼ", result = "ԱՐԴՅՈՒՆՔ",
    resultFormula = "Քաշ × (Նմուշ/1000) × Գին 999.9",
    pureWeightResult = "Մաքուր մետաղ", buyCost = "Գնման արժեք",
    sellCost = "Վաճառքի արժեք", profit = "ՇԱՀՈՒՅԹ",
    saveToJournal = "✓ Մատյան", saved = "✓ Պահված",
    journalTitle = "ԳՈՐԾԱՐՔՆԵՐԻ ՄԱՏՅԱՆ", totalProfit = "ԸՆԴՀԱՆՈՒՐ ՇԱՀՈՒՅԹ",
    deals = "գործարք", noDeals = "Գործարքներ չկան",
    noDealsHint = "Հաշվեք գործարքը և սեղմեք ✓\nպահելու համար",
    select = "Ընտրել", cancel = "Չեղարկել", clearAll = "Մաքրել",
    clearAllConfirm = "Բոլոր գործարքները կջնջվեն.",
    deleteSelected = "🗑  Ջնջել ընտրվածները", deleteAll = "Ջնջել բոլորը",
    signalsTitle = "ԱԶԴԱՆՇԱՆՆԵՐ", signalsSub = "Ծանուցումը կգա նույնիսկ երբ հավելվածը փակ է",
    signalsActive = "Ազդանշանները ակտիվ են • ստուգում 15 ր-ն մեկ",
    notifyWhen = "Ծանուցել երբ գինը:", priceAbove = "📈  Բարձրանա",
    priceBelow = "📉  Իջնի", targetPrice = "Նպատակային գին",
    activate = "🔔  ԱԿՏԻՎԱՑՆԵԼ ԱԶԴԱՆՇԱՆՆԵՐԸ", disableAll = "Անջատել բոլոր ազդանշանները",
    signalSet = "✓ սահmanված", settingsTitle = "ԿԱՐԳԱՎՈՐՈՒՄՆԵՐ",
    yourCountry = "ՁԵՐ ԵՐԿԻՐԸ", changeCountry = "Փոխել երկիրը",
    selectCountry = "ԸՆՏՐԵՔ ԵՐԿԻՐ", welcomeTitle = "Բարի գալուստ!",
    welcomeSub = "Ընտրեք ձեր երկիրը.\nԳները կլինեն տեղական արժույթով.",
    forToday = "այսօրվա", lastData = "Վերջին տվյալներ",
    perGram = "դ/գ", perGram999 = "մեկ գ 999.9", perGram999Silver = "մեկ գ 999",
    pureGoldPrice = "մաքուր ոսկու գին", pureSilverPrice = "մաքուր արծաթի գին"
)

val stringsTr = AppStrings(
    appName = "ProMetal", metalMarket = "METAL PİYASASI",
    gold = "ALTIN", silver = "GÜMÜŞ",
    today = "Gün", week = "Hafta", month = "Ay",
    updatedJustNow = "Güncellendi: az önce", noConnection = "Bağlantı yok",
    loading = "Yükleniyor...", gram = "Gram", oz = "Ons",
    quickValuation = "HIZLI DEĞERLEMEDİR", quickValuationSub = "dünya fiyatıyla",
    weightG = "Ağırlık (g)", purity = "Ayar",
    pureWeight = "Saf metal", value = "Değer",
    deal = "İşlem", journal = "Günlük", market = "Piyasa",
    alerts = "Uyarılar", settings = "Ayarlar",
    dealCalc = "İŞLEM HESAPLAYICI",
    weight = "Metal ağırlığı", purityHint = "örn. 585, 750, 999",
    purityHintSilver = "örn. 800, 925, 999",
    buyPrice = "Alış fiyatı", sellPrice = "Satış fiyatı",
    commission = "Komisyon", commissionHint = "% (isteğe bağlı)",
    calculate = "HESAPLA", result = "SONUÇ",
    resultFormula = "Ağırlık × (Ayar/1000) × Fiyat 999.9",
    pureWeightResult = "Saf metal", buyCost = "Alış maliyeti",
    sellCost = "Satış maliyeti", profit = "KÂR",
    saveToJournal = "✓ Günlüğe", saved = "✓ Kaydedildi",
    journalTitle = "İŞLEM GÜNLÜĞÜ", totalProfit = "TOPLAM KÂR",
    deals = "işlem", noDeals = "Henüz işlem yok",
    noDealsHint = "Bir işlem hesaplayın ve ✓ tuşuna basın\ngünlüğe kaydetmek için",
    select = "Seç", cancel = "İptal", clearAll = "Temizle",
    clearAllConfirm = "Tüm işlemler silinecek.",
    deleteSelected = "🗑  Seçilenleri sil", deleteAll = "Hepsini sil",
    signalsTitle = "UYARILAR", signalsSub = "Uygulama kapalıyken bile bildirim gelir",
    signalsActive = "Uyarılar aktif • her 15 dakikada kontrol",
    notifyWhen = "Fiyat olduğunda bildir:", priceAbove = "📈  Yükselirse",
    priceBelow = "📉  Düşerse", targetPrice = "Hedef fiyat",
    activate = "🔔  UYARILARI ETKİNLEŞTİR", disableAll = "Tüm uyarıları devre dışı bırak",
    signalSet = "✓ ayarlandı", settingsTitle = "AYARLAR",
    yourCountry = "ÜLKENİZ", changeCountry = "Ülkeyi değiştir",
    selectCountry = "ÜLKE SEÇİN", welcomeTitle = "Hoş geldiniz!",
    welcomeSub = "Ülkenizi seçin.\nFiyatlar yerel para biriminde görünecek.",
    forToday = "bugün için", lastData = "Son veriler",
    perGram = "/g", perGram999 = "gram 999.9 başına", perGram999Silver = "gram 999 başına",
    pureGoldPrice = "saf altın fiyatı", pureSilverPrice = "saf gümüş fiyatı"
)

fun getStrings(context: Context): AppStrings {
    val saved = context.getSharedPreferences("prometal_prefs", Context.MODE_PRIVATE)
        .getString("language", null)
    val lang = saved ?: context.resources.configuration.locales[0].language
    return when (lang) {
        "ru" -> stringsRu
        "hy" -> stringsHy
        "tr" -> stringsTr
        else -> stringsEn
    }
}

fun saveLanguage(context: Context, lang: String) {
    context.getSharedPreferences("prometal_prefs", Context.MODE_PRIVATE)
        .edit().putString("language", lang).apply()
}

// Глобальный объект строк — обновляется при старте и смене языка
var S = stringsRu


val Gold = Color(0xFFD4AF37)
val GoldDim = Color(0xFF9A7D20)
val Silver = Color(0xFFB8B8B8)
val GoldDark = Color(0xFF8B6914)
val BgDark = Color(0xFF0A0A0A)
val BgCard = Color(0xFF111111)
val BgCardBorder = Color(0xFF2A2A2A)
val NavBg = Color(0xFF0D0D0D)

data class Country(val flag: String, val name: String, val currency: String, val symbol: String, val rate: Double, val cbName: String = "LBMA")

val countries = listOf(
    Country("🇺🇸", "США", "USD", "$", 1.0, "LBMA"),
    Country("🇷🇺", "Россия", "RUB", "₽", 89.5, "ЦБ РФ"),
    Country("🇦🇲", "Армения", "AMD", "֏", 387.0, "ЦБ Армении"),
    Country("🇹🇷", "Турция", "TRY", "₺", 32.1, "ЦБ Турции"),
    Country("🇦🇪", "ОАЭ", "AED", "د.إ", 3.67, "ЦБ ОАЭ"),
    Country("🇪🇺", "Евросоюз", "EUR", "€", 0.92, "ЕЦБ")
)

val goldPricesDay = listOf(2310f, 2318f, 2305f, 2325f, 2330f, 2320f, 2335f, 2328f, 2340f, 2338f, 2345f, 2346f)
val goldPricesWeek = listOf(2280f, 2295f, 2310f, 2290f, 2315f, 2330f, 2346f)
val goldPricesMonth = listOf(2200f, 2220f, 2210f, 2240f, 2260f, 2250f, 2270f, 2280f, 2295f, 2310f, 2330f, 2346f)
val silverPricesDay = listOf(28.1f, 28.3f, 28.0f, 28.4f, 28.2f, 28.5f, 28.3f, 28.6f, 28.4f, 28.5f, 28.4f, 28.45f)
val silverPricesWeek = listOf(27.8f, 28.0f, 27.9f, 28.2f, 28.1f, 28.3f, 28.45f)
val silverPricesMonth = listOf(27.0f, 27.2f, 27.5f, 27.3f, 27.6f, 27.8f, 27.9f, 28.0f, 28.1f, 28.2f, 28.3f, 28.45f)

// Нижнее меню: Сделка | Журнал | РЫНОК(центр) | Сигналы | Настройки
sealed class Screen(val route: String) {
    object Calculator : Screen("calculator")
    object Journal : Screen("journal")
    object Market : Screen("market")
    object Alerts : Screen("alerts")
    object Settings : Screen("settings")
}

val screens = listOf(Screen.Calculator, Screen.Journal, Screen.Market, Screen.Alerts, Screen.Settings)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        S = getStrings(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        setContent { ProMetalApp() }
    }
}

@Composable
fun ProMetalApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Market) }
    var selectedCountry by remember { mutableStateOf<Country?>(null) }
    var currentLang by remember { mutableStateOf("ru") }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    // Загружаем данные при первом запуске
    LaunchedEffect(Unit) {
        val savedCountry = loadSelectedCountry(context)
        val savedDeals = loadJournal(context)
        if (savedDeals.isNotEmpty()) {
            dealJournal.clear()
            dealJournal.addAll(savedDeals)
        }
        selectedCountry = savedCountry
        S = getStrings(context)
        currentLang = context.getSharedPreferences("prometal_prefs", Context.MODE_PRIVATE)
            .getString("language", null) ?: context.resources.configuration.locales[0].language
    }

    // При смене языка обновляем S и перерисовываем UI
    val onLanguageChange: (String) -> Unit = { lang ->
        saveLanguage(context, lang)
        S = when (lang) {
            "ru" -> stringsRu
            "hy" -> stringsHy
            "tr" -> stringsTr
            else -> stringsEn
        }
        currentLang = lang
    }

    // Сохраняем журнал при каждом изменении
    LaunchedEffect(dealJournal.size) {
        saveJournal(context, dealJournal.toList())
    }

    if (selectedCountry == null) {
        OnboardingScreen {
            selectedCountry = it
            saveSelectedCountry(context, it.currency)
        }
    } else {
        val country = selectedCountry!!
        Box(modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                focusManager.clearFocus()
            }
        ) {
            val navBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            val bottomNavHeight = 72.dp + navBarHeight
            Box(modifier = Modifier.fillMaxSize().padding(bottom = bottomNavHeight)) {
                when (currentScreen) {
                    Screen.Market -> MarketScreen(country) { selectedCountry = it; saveSelectedCountry(context, it.currency) }
                    Screen.Calculator -> CalculatorScreen(country)
                    Screen.Journal -> JournalScreen()
                    Screen.Alerts -> AlertsScreen(country)
                    Screen.Settings -> SettingsScreen(country, currentLang, onLanguageChange) { selectedCountry = it; saveSelectedCountry(context, it.currency) }
                }
            }
            BottomNavBar(
                currentScreen = currentScreen,
                onScreenSelected = { currentScreen = it },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

// ─── ОНБОРДИНГ ────────────────────────────────────────

@Composable
fun OnboardingScreen(onCountrySelected: (Country) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(BgDark).padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        // Тёмный логотип
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("PRO", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GoldDim, letterSpacing = 3.sp)
            Text("METAL", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.6f), letterSpacing = 3.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = GoldDark.copy(alpha = 0.5f), thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(32.dp))
        Text(S.welcomeTitle, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Выберите вашу страну.\nЦены будут в местной валюте.",
            fontSize = 14.sp, color = Color.White.copy(alpha = 0.5f), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(28.dp))
        countries.forEach { country ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onCountrySelected(country) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BgCard),
                border = BorderStroke(0.5.dp, BgCardBorder)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(country.flag, fontSize = 26.sp)
                        Column {
                            Text(country.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(country.currency, fontSize = 12.sp, color = Color.White.copy(alpha = 0.4f))
                        }
                    }
                    Text(country.symbol, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Gold)
                }
            }
        }
    }
}

// ─── ГЛАВНЫЙ ЭКРАН ────────────────────────────────────

@Composable
fun MarketScreen(country: Country, onCountryChange: (Country) -> Unit) {
    var selectedUnit by remember { mutableStateOf("oz") }
    var showCountryPicker by remember { mutableStateOf(false) }

    var goldUsd by remember { mutableStateOf(2345.67) }   // цена в USD за унцию
    var silverUsd by remember { mutableStateOf(28.45) }   // цена в USD за унцию
    var goldChange by remember { mutableStateOf("+0.00") }
    var goldChangePct by remember { mutableStateOf("+0.00%") }
    var silverChange by remember { mutableStateOf("+0.00") }
    var silverChangePct by remember { mutableStateOf("+0.00%") }
    var isLoading by remember { mutableStateOf(true) }
    var lastUpdated by remember { mutableStateOf(S.loading) }

    // Исторические данные для графиков (в локальной валюте за грамм)
    var goldHistDay by remember { mutableStateOf(goldPricesDay) }
    var goldHistWeek by remember { mutableStateOf(goldPricesWeek) }
    var goldHistMonth by remember { mutableStateOf(goldPricesMonth) }
    var silverHistDay by remember { mutableStateOf(silverPricesDay) }
    var silverHistWeek by remember { mutableStateOf(silverPricesWeek) }
    var silverHistMonth by remember { mutableStateOf(silverPricesMonth) }
    // usdRate = сколько единиц валюты страны за 1 USD
    // Для RUB = 89.5, для AMD = 387, для USD = 1.0 и т.д.
    var usdRate by remember { mutableStateOf(country.rate) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(country.currency) {
        isLoading = true
        lastUpdated = S.loading
        usdRate = country.rate

        scope.launch {
            withContext(Dispatchers.IO) {
                val client = OkHttpClient()

                // Шаг 1: Курс USD из ЦБ выбранной страны
                try {
                    when (country.currency) {
                        "USD" -> usdRate = 1.0

                        "AED" -> usdRate = 3.6725 // дирхам жёстко привязан к USD

                        "RUB" -> {
                            val req = Request.Builder()
                                .url("https://www.cbr.ru/scripts/XML_daily.asp")
                                .header("User-Agent", "Mozilla/5.0").build()
                            val xml = client.newCall(req).execute().body?.string() ?: ""
                            val doc = javax.xml.parsers.DocumentBuilderFactory.newInstance()
                                .newDocumentBuilder().parse(xml.byteInputStream())
                            val valutes = doc.getElementsByTagName("Valute")
                            for (i in 0 until valutes.length) {
                                val v = valutes.item(i) as org.w3c.dom.Element
                                if (v.getElementsByTagName("CharCode").item(0)?.textContent == "USD") {
                                    val value = v.getElementsByTagName("Value").item(0)?.textContent
                                        ?.replace(",", ".")?.toDoubleOrNull() ?: break
                                    val nominal = v.getElementsByTagName("Nominal").item(0)?.textContent
                                        ?.toIntOrNull() ?: 1
                                    usdRate = value / nominal
                                    break
                                }
                            }
                        }

                        "AMD" -> {
                            // ЦБ Армении — cb.am
                            val req = Request.Builder()
                                .url("https://cb.am/latest.json.php?currency=USD")
                                .header("User-Agent", "Mozilla/5.0").build()
                            val json = client.newCall(req).execute().body?.string() ?: ""
                            val obj = JSONObject(json)
                            usdRate = obj.getDouble("USD")
                        }

                        "TRY" -> {
                            // ЦБ Турции — today.xml
                            val req = Request.Builder()
                                .url("https://www.tcmb.gov.tr/kurlar/today.xml")
                                .header("User-Agent", "Mozilla/5.0").build()
                            val xml = client.newCall(req).execute().body?.string() ?: ""
                            val doc = javax.xml.parsers.DocumentBuilderFactory.newInstance()
                                .newDocumentBuilder().parse(xml.byteInputStream())
                            val currencies = doc.getElementsByTagName("Currency")
                            for (i in 0 until currencies.length) {
                                val c = currencies.item(i) as org.w3c.dom.Element
                                if (c.getAttribute("CurrencyCode") == "USD") {
                                    val selling = c.getElementsByTagName("ForexSelling").item(0)?.textContent
                                        ?.replace(",", ".")?.toDoubleOrNull() ?: break
                                    // TCMB даёт TRY за 1 USD
                                    usdRate = selling
                                    break
                                }
                            }
                        }

                        "EUR" -> {
                            // ЕЦБ
                            val req = Request.Builder()
                                .url("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml")
                                .header("User-Agent", "Mozilla/5.0").build()
                            val xml = client.newCall(req).execute().body?.string() ?: ""
                            val doc = javax.xml.parsers.DocumentBuilderFactory.newInstance()
                                .newDocumentBuilder().parse(xml.byteInputStream())
                            val cubes = doc.getElementsByTagName("Cube")
                            var eurPerUsd = 0.92
                            for (i in 0 until cubes.length) {
                                val c = cubes.item(i) as org.w3c.dom.Element
                                if (c.getAttribute("currency") == "USD") {
                                    // ЕЦБ даёт USD за 1 EUR, нам нужно EUR за 1 USD
                                    val usdPerEur = c.getAttribute("rate").toDoubleOrNull() ?: break
                                    eurPerUsd = 1.0 / usdPerEur
                                    break
                                }
                            }
                            usdRate = eurPerUsd
                        }
                    }
                } catch (e: Exception) {
                    usdRate = country.rate // запасной фиксированный курс
                }

                // Шаг 2: Получаем цену металла
                // Для России — ЦБ РФ. Для остальных — сначала международный API
                var gotPrice = false

                if (country.currency != "RUB") {
                    // Пробуем международный API
                    try {
                        val req = Request.Builder()
                            .url("https://data-asg.goldprice.org/dbXRates/USD")
                            .header("User-Agent", "Mozilla/5.0")
                            .header("Accept", "application/json").build()
                        val resp = client.newCall(req).execute()
                        if (resp.isSuccessful) {
                            val body = resp.body?.string() ?: ""
                            val obj = JSONObject(body)
                            val newGold = obj.getDouble("xauPrice")
                            val newSilver = obj.getDouble("xagPrice")
                            val gd = newGold - goldUsd
                            val sd = newSilver - silverUsd
                            goldChange = if (gd >= 0) "+${"%.2f".format(gd)}" else "${"%.2f".format(gd)}"
                            goldChangePct = if (gd >= 0) "+${"%.2f".format(gd/goldUsd*100)}%" else "${"%.2f".format(gd/goldUsd*100)}%"
                            silverChange = if (sd >= 0) "+${"%.2f".format(sd)}" else "${"%.2f".format(sd)}"
                            silverChangePct = if (sd >= 0) "+${"%.2f".format(sd/silverUsd*100)}%" else "${"%.2f".format(sd/silverUsd*100)}%"
                            goldUsd = newGold
                            silverUsd = newSilver
                            val priceSource = when (country.currency) {
                                "AMD" -> "ЦБ Армении"
                                "TRY" -> "ЦБ Турции"
                                "EUR" -> "ЕЦБ"
                                "AED" -> "ЦБ ОАЭ"
                                else -> "LBMA / NYMEX"
                            }
                            lastUpdated = "Обновлено: только что  •  $priceSource"
                            gotPrice = true
                        }
                    } catch (e: Exception) { }
                }

                if (!gotPrice) {
                    // ЦБ РФ — для России или если международный недоступен
                    try {
                        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy")
                        val cal = java.util.Calendar.getInstance()
                        // Берём последние 5 дней — на случай выходных
                        var xml: String? = null
                        for (daysBack in 0..4) {
                            val date = sdf.format(cal.time)
                            val req = Request.Builder()
                                .url("https://www.cbr.ru/scripts/xml_metall.asp?date_req1=$date&date_req2=$date&P1=1&P2=1&P3=1&P4=1")
                                .header("User-Agent", "Mozilla/5.0").build()
                            val resp = client.newCall(req).execute()
                            if (resp.isSuccessful) {
                                val body = resp.body?.string() ?: ""
                                // Проверяем что есть реальные данные (не пустой XML)
                                if (body.contains("<Record")) { xml = body; break }
                            }
                            cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
                        }
                        xml?.let {
                            val factory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
                            val doc = factory.newDocumentBuilder().parse(it.byteInputStream())
                            val records = doc.getElementsByTagName("Record")
                            for (i in 0 until records.length) {
                                val record = records.item(i) as org.w3c.dom.Element
                                val code = record.getAttribute("Code")
                                val sellStr = record.getElementsByTagName("Sell").item(0)?.textContent
                                    ?.replace(",", ".")?.trim() ?: continue
                                val sellRub = sellStr.toDoubleOrNull() ?: continue
                                // ЦБ РФ: рублей за грамм → USD за унцию
                                val rubPerUsd = usdRate.let { r -> if (country.currency == "RUB") r else 89.5 }
                                val priceUsdOz = sellRub * 31.1035 / rubPerUsd
                                when (code) {
                                    "1" -> {
                                        val d = priceUsdOz - goldUsd
                                        goldChange = if (d >= 0) "+${"%.2f".format(d)}" else "${"%.2f".format(d)}"
                                        goldChangePct = if (d >= 0) "+${"%.2f".format(d/goldUsd*100)}%" else "${"%.2f".format(d/goldUsd*100)}%"
                                        goldUsd = priceUsdOz
                                    }
                                    "2" -> {
                                        val pSilver = sellRub * 31.1035 / rubPerUsd
                                        val d = pSilver - silverUsd
                                        silverChange = if (d >= 0) "+${"%.2f".format(d)}" else "${"%.2f".format(d)}"
                                        silverChangePct = if (d >= 0) "+${"%.2f".format(d/silverUsd*100)}%" else "${"%.2f".format(d/silverUsd*100)}%"
                                        silverUsd = pSilver
                                    }
                                }
                            }
                            val source = when (country.currency) {
                                "RUB" -> "ЦБ РФ"
                                "AMD" -> "ЦБ Армении"
                                "TRY" -> "ЦБ Турции"
                                "EUR" -> "ЕЦБ"
                                "AED" -> "ЦБ ОАЭ"
                                else -> "LBMA / NYMEX"
                            }
                            lastUpdated = "Обновлено: только что  •  $source"
                            gotPrice = true
                        }
                    } catch (e: Exception) { }
                }

                if (!gotPrice) {
                    lastUpdated = "Нет соединения  •  Данные недоступны"
                }

                // Шаг 3: Загружаем историю цен из ЦБ РФ (работает для всех стран)
                try {
                    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy")
                    val calEnd = java.util.Calendar.getInstance()
                    val calStart = java.util.Calendar.getInstance()
                    calStart.add(java.util.Calendar.DAY_OF_YEAR, -32)
                    val dateFrom = sdf.format(calStart.time)
                    val dateTo = sdf.format(calEnd.time)
                    val req = okhttp3.Request.Builder()
                        .url("https://www.cbr.ru/scripts/xml_metall.asp?date_req1=$dateFrom&date_req2=$dateTo&P1=1&P2=1&P3=1&P4=1")
                        .header("User-Agent", "Mozilla/5.0").build()
                    val resp = client.newCall(req).execute()
                    if (resp.isSuccessful) {
                        val body = resp.body?.string() ?: ""
                        if (body.contains("<Record")) {
                            val doc = javax.xml.parsers.DocumentBuilderFactory.newInstance()
                                .newDocumentBuilder().parse(body.byteInputStream())
                            val records = doc.getElementsByTagName("Record")
                            val goldMonthRub = mutableListOf<Float>()
                            val silverMonthRub = mutableListOf<Float>()
                            for (i in 0 until records.length) {
                                val r = records.item(i) as org.w3c.dom.Element
                                val sell = r.getElementsByTagName("Sell").item(0)?.textContent
                                    ?.replace(",", ".")?.toFloatOrNull() ?: continue
                                when (r.getAttribute("Code")) {
                                    "1" -> goldMonthRub.add(sell)
                                    "2" -> silverMonthRub.add(sell)
                                }
                            }
                            if (goldMonthRub.size >= 2) {
                                val rubPerUsd = if (country.currency == "RUB") usdRate else 89.5
                                fun conv(rub: Float): Float = if (country.currency == "RUB") rub
                                    else (rub / rubPerUsd * usdRate).toFloat()

                                val gMonth = goldMonthRub.map { conv(it) }
                                val sMonth = silverMonthRub.map { conv(it) }

                                goldHistMonth = gMonth
                                silverHistMonth = sMonth
                                goldHistWeek = gMonth.takeLast(7)
                                silverHistWeek = sMonth.takeLast(7)
                                // День: берём последние 5 точек для красивой линии
                                goldHistDay = gMonth.takeLast(5)
                                silverHistDay = sMonth.takeLast(5)

                                // Пересчитываем прирост от реального вчерашнего значения
                                val gYesterday = gMonth.dropLast(1).lastOrNull()?.toDouble()
                                val sYesterday = sMonth.dropLast(1).lastOrNull()?.toDouble()
                                val gToday = gMonth.lastOrNull()?.toDouble()
                                val sToday = sMonth.lastOrNull()?.toDouble()

                                if (gYesterday != null && gToday != null && gYesterday > 0) {
                                    val gd = gToday - gYesterday
                                    goldChange = if (gd >= 0) "+${"%.2f".format(gd)}" else "${"%.2f".format(gd)}"
                                    goldChangePct = if (gd >= 0) "+${"%.2f".format(gd/gYesterday*100)}%" else "${"%.2f".format(gd/gYesterday*100)}%"
                                }
                                if (sYesterday != null && sToday != null && sYesterday > 0) {
                                    val sd = sToday - sYesterday
                                    silverChange = if (sd >= 0) "+${"%.2f".format(sd)}" else "${"%.2f".format(sd)}"
                                    silverChangePct = if (sd >= 0) "+${"%.2f".format(sd/sYesterday*100)}%" else "${"%.2f".format(sd/sYesterday*100)}%"
                                }
                            }
                        }
                    }
                } catch (e: Exception) { }
            }
            isLoading = false
        }
    }

    // goldLocal = цена в валюте страны за унцию (просто умножаем USD на курс)
    val goldLocal = goldUsd * usdRate
    val silverLocal = silverUsd * usdRate
    fun fmt(v: Double) = if (v >= 1000) "%,.0f".format(v) else "%.2f".format(v)

    // Диалог выбора страны
    if (showCountryPicker) {
        Dialog(onDismissRequest = { showCountryPicker = false }) {
            Card(shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161616)),
                border = BorderStroke(0.5.dp, BgCardBorder)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(S.selectCountry, fontSize = 11.sp, color = Gold,
                        fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    countries.forEach { c ->
                        val isSelected = c.currency == country.currency
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .background(
                                    if (isSelected) Gold.copy(alpha = 0.1f) else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onCountryChange(c); showCountryPicker = false }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(c.flag, fontSize = 22.sp)
                                Text(c.name, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                                    color = if (isSelected) Gold else Color.White)
                            }
                            if (isSelected) Text("✓", fontSize = 16.sp, color = Gold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    val marketScrollState = rememberScrollState()
    val marketScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().background(BgDark)
            .padding(horizontal = 20.dp).verticalScroll(marketScrollState)
    ) {
        Spacer(modifier = Modifier.height(52.dp))

        // Заголовок — яркий логотип
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("PRO", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    color = Gold, letterSpacing = 4.sp)
                Text("METAL", fontSize = 28.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, letterSpacing = 2.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Кнопка смены страны
                Box(
                    modifier = Modifier
                        .background(BgCard, RoundedCornerShape(8.dp))
                        .border(0.5.dp, BgCardBorder, RoundedCornerShape(8.dp))
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { showCountryPicker = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(country.flag, fontSize = 16.sp)
                        Text(country.currency, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Gold)
                        Text("▾", fontSize = 10.sp, color = Gold)
                    }
                }
                UnitToggle(selectedUnit) { selectedUnit = it }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = GoldDark.copy(alpha = 0.6f), thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(24.dp))

        Text(S.metalMarket, fontSize = 10.sp, color = GoldDim,
            letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Gold, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Загрузка цен...", fontSize = 12.sp, color = Color.White.copy(alpha = 0.4f))
                }
            }
        } else {
        MetalCardWithChart(
            name = S.gold, symbol = "XAU",
            priceOz = fmt(goldLocal), priceG = fmt(goldLocal / 31.1035),
            change = goldChangePct, changeAbs = goldChange,
            isPositive = !goldChange.startsWith("-"), color = Gold,
            selectedUnit = selectedUnit, currencySymbol = country.symbol,
            dayPrices = goldHistDay, weekPrices = goldHistWeek, monthPrices = goldHistMonth
        )
        Spacer(modifier = Modifier.height(12.dp))
        MetalCardWithChart(
            name = S.silver, symbol = "XAG",
            priceOz = fmt(silverLocal), priceG = fmt(silverLocal / 31.1035),
            change = silverChangePct, changeAbs = silverChange,
            isPositive = !silverChange.startsWith("-"), color = Silver,
            selectedUnit = selectedUnit, currencySymbol = country.symbol,
            dayPrices = silverHistDay, weekPrices = silverHistWeek, monthPrices = silverHistMonth
        )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(lastUpdated,
            fontSize = 10.sp, color = Color.White.copy(alpha = 0.25f),
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))

        // Быстрая оценка стоимости
        QuickValuationCard(
            country = country,
            goldPriceLocal = goldUsd * usdRate,
            silverPriceLocal = silverUsd * usdRate,
            onResultShown = { marketScope.launch { marketScrollState.animateScrollTo(marketScrollState.maxValue) } }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ─── КАЛЬКУЛЯТОР СДЕЛКИ ───────────────────────────────

@Composable
fun CalculatorScreen(country: Country) {
    var weight by remember { mutableStateOf("") }
    var purity by remember { mutableStateOf("") }
    var buyPrice by remember { mutableStateOf("") }
    var sellPrice by remember { mutableStateOf("") }
    var commission by remember { mutableStateOf("") }
    var selectedMetal by remember { mutableStateOf("Золото") }
    var result by remember { mutableStateOf<CalcResult?>(null) }
    var savedToJournal by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().background(BgDark)
            .padding(horizontal = 20.dp).verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(52.dp))
        Text("КАЛЬКУЛЯТОР", fontSize = 10.sp, color = Gold, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Сделки", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(24.dp))

        // Выбор металла
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Золото", "Серебро").forEach { metal ->
                val isSelected = selectedMetal == metal
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f)
                        .background(if (isSelected) Gold.copy(alpha = 0.15f) else BgCard, RoundedCornerShape(12.dp))
                        .border(if (isSelected) 1.dp else 0.5.dp, if (isSelected) Gold else BgCardBorder, RoundedCornerShape(12.dp))
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { selectedMetal = metal }
                        .padding(vertical = 12.dp)
                ) {
                    Text(metal, fontSize = 14.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Gold else Color.White.copy(alpha = 0.5f))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Поля ввода
        val metalLabel = if (selectedMetal == "Золото") "золота" else "серебра"
        val topPurity = if (selectedMetal == "Золото") "999.9" else "999"

        CalcInputField("Вес металла", weight, "граммы") { weight = it }
        Spacer(modifier = Modifier.height(10.dp))
        CalcInputField("Проба", purity, "напр. ${if (selectedMetal == "Золото") "585, 750, 999" else "800, 925, 999"}") { purity = it }
        Spacer(modifier = Modifier.height(10.dp))
        CalcInputField("Цена покупки (за грамм $topPurity)", buyPrice, "цена чистого $metalLabel") { buyPrice = it }
        Spacer(modifier = Modifier.height(10.dp))
        CalcInputField("Цена продажи (за грамм $topPurity)", sellPrice, "цена чистого $metalLabel") { sellPrice = it }
        Spacer(modifier = Modifier.height(10.dp))
        CalcInputField("Комиссия", commission, "% (необязательно)") { commission = it }

        // Мгновенный результат чистого металла
        val w0 = weight.toDoubleOrNull() ?: 0.0
        val p0 = purity.toDoubleOrNull() ?: 0.0
        if (w0 > 0 && p0 > 0) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(Gold.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                    .border(0.5.dp, Gold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Чистый металл", fontSize = 11.sp, color = Gold.copy(alpha = 0.7f), letterSpacing = 1.sp)
                    Text("${w0}г × ${p0}/1000", fontSize = 10.sp, color = Color.White.copy(alpha = 0.3f))
                }
                Text("${"%.3f".format(w0 * (p0 / 1000.0))} г",
                    fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Gold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Кнопка — активна только когда все обязательные поля заполнены
        val canCalculate = weight.isNotEmpty() && purity.isNotEmpty() && buyPrice.isNotEmpty() && sellPrice.isNotEmpty()

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
                .background(
                    if (canCalculate) Gold else Gold.copy(alpha = 0.25f),
                    RoundedCornerShape(14.dp)
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    enabled = canCalculate
                ) {
                    val w = weight.toDoubleOrNull() ?: 0.0
                    val p = purity.toDoubleOrNull() ?: 0.0
                    val buy = buyPrice.toDoubleOrNull() ?: 0.0
                    val sell = sellPrice.toDoubleOrNull() ?: 0.0
                    val comm = commission.toDoubleOrNull() ?: 0.0
                    val pureWeight = w * (p / 1000.0)
                    val buyCost = pureWeight * buy
                    val sellCost = pureWeight * sell
                    val commAmount = sellCost * (comm / 100.0)
                    val profit = sellCost - buyCost - commAmount
                    result = CalcResult(pureWeight, buyCost, sellCost, commAmount, profit)
                    savedToJournal = false
                    // Небольшая задержка чтобы результат успел отрисоваться
                    scope.launch {
                        kotlinx.coroutines.delay(150)
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }
                }
                .padding(vertical = 16.dp)
        ) {
            Text(S.calculate, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                color = if (canCalculate) BgDark else Color.White.copy(alpha = 0.3f), letterSpacing = 2.sp)
        }

        // Результат
        result?.let { r ->
            Spacer(modifier = Modifier.height(24.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BgCard),
                border = BorderStroke(0.5.dp, Gold.copy(alpha = 0.3f))) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(S.result, fontSize = 10.sp, color = Gold, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
                            Text("Вес × (Проба/1000) × Цена 999.9", fontSize = 10.sp, color = Color.White.copy(alpha = 0.3f))
                        }
                        // Кнопка сохранения в журнал
                        Box(
                            modifier = Modifier
                                .background(
                                    if (savedToJournal) Color(0xFF4CAF50) else Gold,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                                    if (!savedToJournal) {
                                        val sdf = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm")
                                        val deal = Deal(
                                            metal = selectedMetal,
                                            weight = weight.toDoubleOrNull() ?: 0.0,
                                            purity = purity.toDoubleOrNull() ?: 0.0,
                                            pureWeight = r.pureWeight,
                                            buyPrice = buyPrice.toDoubleOrNull() ?: 0.0,
                                            sellPrice = sellPrice.toDoubleOrNull() ?: 0.0,
                                            commission = r.commission,
                                            buyCost = r.buyCost,
                                            sellCost = r.sellCost,
                                            profit = r.profit,
                                            currency = country.currency,
                                            symbol = country.symbol,
                                            date = sdf.format(java.util.Date())
                                        )
                                        dealJournal.add(deal)
                                        savedToJournal = true
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (savedToJournal) "✓ Сохранено" else "✓ В журнал",
                                fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                color = if (savedToJournal) Color.White else BgDark
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ResultRow("Чистый металл", "%.3f г".format(r.pureWeight), Color.White)
                    HorizontalDivider(color = BgCardBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))
                    ResultRow("Стоимость покупки", "%.2f".format(r.buyCost), Color.White)
                    ResultRow("Стоимость продажи", "%.2f".format(r.sellCost), Color.White)
                    if (r.commission > 0) ResultRow("Комиссия", "-%.2f".format(r.commission), Color(0xFFE53935))
                    HorizontalDivider(color = BgCardBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))
                    ResultRow(
                        S.profit,
                        "%.2f".format(r.profit),
                        if (r.profit >= 0) Color(0xFF4CAF50) else Color(0xFFE53935),
                        bold = true
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

data class CalcResult(
    val pureWeight: Double,
    val buyCost: Double,
    val sellCost: Double,
    val commission: Double,
    val profit: Double
)

@Composable
fun CalcInputField(label: String, value: String, placeholder: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, fontSize = 11.sp, color = Gold.copy(alpha = 0.8f),
            fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(BgCard, RoundedCornerShape(12.dp))
                .border(0.5.dp, BgCardBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            if (value.isEmpty()) {
                Text(placeholder, fontSize = 14.sp, color = Color.White.copy(alpha = 0.2f))
            }
            androidx.compose.foundation.text.BasicTextField(
                value = value,
                onValueChange = { new -> if (new.all { it.isDigit() || it == '.' }) onValueChange(new) },
                textStyle = TextStyle(
                    fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Medium
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }
    }
}

@Composable
fun ResultRow(label: String, value: String, color: Color, bold: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = if (bold) 14.sp else 13.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = Color.White.copy(alpha = if (bold) 1f else 0.6f))
        Text(value, fontSize = if (bold) 16.sp else 13.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium, color = color)
    }
}

// ─── БЫСТРАЯ ОЦЕНКА ──────────────────────────────────

@Composable
fun QuickValuationCard(country: Country, goldPriceLocal: Double, silverPriceLocal: Double, onResultShown: () -> Unit = {}) {
    var weight by remember { mutableStateOf("") }
    var purity by remember { mutableStateOf("") }
    var selectedMetal by remember { mutableStateOf("Золото") }

    val w = weight.toDoubleOrNull() ?: 0.0
    val p = purity.toDoubleOrNull() ?: 0.0
    val pricePerGram = if (selectedMetal == "Золото") goldPriceLocal / 31.1035 else silverPriceLocal / 31.1035
    val pureWeight = if (w > 0 && p > 0) w * (p / 1000.0) else 0.0
    val value = pureWeight * pricePerGram

    // Автоскролл когда появляется результат
    LaunchedEffect(pureWeight > 0) {
        if (pureWeight > 0) onResultShown()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        border = BorderStroke(0.5.dp, Gold.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(S.quickValuation, fontSize = 10.sp, color = Gold,
                letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
            Text(S.quickValuationSub, fontSize = 10.sp, color = Color.White.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Золото", "Серебро").forEach { metal ->
                    val isSel = selectedMetal == metal
                    val mColor = if (metal == "Золото") Gold else Silver
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.weight(1f)
                            .background(if (isSel) mColor.copy(alpha = 0.12f) else Color.Transparent, RoundedCornerShape(8.dp))
                            .border(0.5.dp, if (isSel) mColor else BgCardBorder, RoundedCornerShape(8.dp))
                            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { selectedMetal = metal }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(metal, fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) mColor else Color.White.copy(alpha = 0.4f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(S.weightG, fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(Color(0xFF1A1A1A), RoundedCornerShape(8.dp))
                            .border(0.5.dp, BgCardBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        if (weight.isEmpty()) Text("0", fontSize = 14.sp, color = Color.White.copy(alpha = 0.2f))
                        androidx.compose.foundation.text.BasicTextField(
                            value = weight,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) weight = it },
                            textStyle = TextStyle(fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.Medium),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Проба", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(Color(0xFF1A1A1A), RoundedCornerShape(8.dp))
                            .border(0.5.dp, BgCardBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        if (purity.isEmpty()) Text("585", fontSize = 14.sp, color = Color.White.copy(alpha = 0.2f))
                        androidx.compose.foundation.text.BasicTextField(
                            value = purity,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) purity = it },
                            textStyle = TextStyle(fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.Medium),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            if (pureWeight > 0) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = BgCardBorder, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Чистый металл", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                        Text("${"%.3f".format(pureWeight)} г", fontSize = 16.sp,
                            fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Стоимость (${country.currency})", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
                        Text("${country.symbol} ${"%.0f".format(value)}", fontSize = 20.sp,
                            fontWeight = FontWeight.Bold, color = if (selectedMetal == "Золото") Gold else Silver)
                    }
                }
            }
        }
    }
}

// ─── МОДЕЛЬ СДЕЛКИ ────────────────────────────────────
data class Deal(
    val id: Long = System.currentTimeMillis(),
    val metal: String,
    val weight: Double,
    val purity: Double,
    val pureWeight: Double,
    val buyPrice: Double,
    val sellPrice: Double,
    val commission: Double,
    val buyCost: Double,
    val sellCost: Double,
    val profit: Double,
    val currency: String,
    val symbol: String,
    val date: String
)

// Глобальный список сделок (в памяти сессии)
// ─── СОХРАНЕНИЕ ЖУРНАЛА ───────────────────────────────
fun saveJournal(context: Context, deals: List<Deal>) {
    val prefs = context.getSharedPreferences("prometal_prefs", Context.MODE_PRIVATE)
    val sb = StringBuilder()
    deals.forEach { d ->
        sb.append("${d.id}|${d.metal}|${d.weight}|${d.purity}|${d.pureWeight}|${d.buyPrice}|${d.sellPrice}|${d.commission}|${d.buyCost}|${d.sellCost}|${d.profit}|${d.currency}|${d.symbol}|${d.date}\n")
    }
    prefs.edit().putString("journal", sb.toString()).apply()
}

fun loadJournal(context: Context): List<Deal> {
    val prefs = context.getSharedPreferences("prometal_prefs", Context.MODE_PRIVATE)
    val raw = prefs.getString("journal", "") ?: return emptyList()
    return raw.trim().lines().filter { it.isNotBlank() }.mapNotNull { line ->
        try {
            val p = line.split("|")
            Deal(id = p[0].toLong(), metal = p[1], weight = p[2].toDouble(), purity = p[3].toDouble(),
                pureWeight = p[4].toDouble(), buyPrice = p[5].toDouble(), sellPrice = p[6].toDouble(),
                commission = p[7].toDouble(), buyCost = p[8].toDouble(), sellCost = p[9].toDouble(),
                profit = p[10].toDouble(), currency = p[11], symbol = p[12], date = p[13])
        } catch (e: Exception) { null }
    }
}

fun saveSelectedCountry(context: Context, currency: String) {
    context.getSharedPreferences("prometal_prefs", Context.MODE_PRIVATE)
        .edit().putString("country", currency).apply()
}

fun loadSelectedCountry(context: Context): Country? {
    val currency = context.getSharedPreferences("prometal_prefs", Context.MODE_PRIVATE)
        .getString("country", null) ?: return null
    return countries.find { it.currency == currency }
}

val dealJournal = mutableStateListOf<Deal>()

// ─── ЖУРНАЛ СДЕЛОК ────────────────────────────────────
@Composable
fun JournalScreen() {
    val totalProfit = dealJournal.sumOf { it.profit }
    val profitColor = if (totalProfit >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
    var selectionMode by remember { mutableStateOf(false) }
    val selectedIds = remember { mutableStateListOf<Long>() }
    var showClearAllDialog by remember { mutableStateOf(false) }

    if (showClearAllDialog) {
        Dialog(onDismissRequest = { showClearAllDialog = false }) {
            Card(shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161616)),
                border = BorderStroke(0.5.dp, Color(0xFFE53935).copy(alpha = 0.4f))) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Очистить журнал?", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Все ${dealJournal.size} сделок будут удалены.",
                        fontSize = 13.sp, color = Color.White.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.weight(1f)
                            .background(BgCard, RoundedCornerShape(10.dp))
                            .border(0.5.dp, BgCardBorder, RoundedCornerShape(10.dp))
                            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { showClearAllDialog = false }
                            .padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                            Text(S.cancel, fontSize = 14.sp, color = Color.White.copy(alpha = 0.6f))
                        }
                        Box(modifier = Modifier.weight(1f)
                            .background(Color(0xFFE53935), RoundedCornerShape(10.dp))
                            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                                dealJournal.clear(); selectedIds.clear(); selectionMode = false; showClearAllDialog = false
                            }
                            .padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                            Text(S.deleteAll, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(BgDark)) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(S.journalTitle, fontSize = 11.sp, color = Gold,
                    fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                if (dealJournal.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier
                            .background(if (selectionMode) Gold.copy(alpha=0.15f) else BgCard, RoundedCornerShape(8.dp))
                            .border(0.5.dp, if (selectionMode) Gold else BgCardBorder, RoundedCornerShape(8.dp))
                            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                                selectionMode = !selectionMode
                                if (!selectionMode) selectedIds.clear()
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text(if (selectionMode) S.cancel else S.select,
                                fontSize = 11.sp, color = if (selectionMode) Gold else Color.White.copy(alpha=0.5f))
                        }
                        Box(modifier = Modifier
                            .background(Color(0xFFE53935).copy(alpha=0.1f), RoundedCornerShape(8.dp))
                            .border(0.5.dp, Color(0xFFE53935).copy(alpha=0.3f), RoundedCornerShape(8.dp))
                            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { showClearAllDialog = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text(S.clearAll, fontSize = 11.sp, color = Color(0xFFE53935))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BgCard),
                border = BorderStroke(0.5.dp, if (totalProfit >= 0) Color(0xFF4CAF50).copy(alpha=0.4f) else Color(0xFFE53935).copy(alpha=0.4f))) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(S.totalProfit, fontSize = 10.sp, color = Color.White.copy(alpha=0.4f), letterSpacing = 2.sp)
                        Text("${dealJournal.size} сделок", fontSize = 12.sp, color = Color.White.copy(alpha=0.5f))
                    }
                    Text("${if (totalProfit >= 0) "+" else ""}${"%.2f".format(totalProfit)}",
                        fontSize = 22.sp, fontWeight = FontWeight.Bold, color = profitColor)
                }
            }
            if (selectionMode && selectedIds.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.fillMaxWidth()
                    .background(Color(0xFFE53935), RoundedCornerShape(10.dp))
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                        dealJournal.removeAll { it.id in selectedIds }
                        selectedIds.clear(); selectionMode = false
                    }
                    .padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                    Text("🗑  Удалить выбранные (${selectedIds.size})",
                        fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        if (dealJournal.isEmpty()) {
            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("📋", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(S.noDeals, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha=0.4f))
                Spacer(modifier = Modifier.height(6.dp))
                Text("Рассчитайте сделку и нажмите ✓\nчтобы сохранить её в журнал",
                    fontSize = 13.sp, color = Color.White.copy(alpha=0.25f), textAlign = TextAlign.Center)
            }
        } else {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
                dealJournal.reversed().forEach { deal ->
                    val isSelected = deal.id in selectedIds
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(modifier = Modifier.fillMaxWidth()
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                            if (selectionMode) {
                                if (isSelected) selectedIds.remove(deal.id) else selectedIds.add(deal.id)
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFE53935).copy(alpha=0.08f) else BgCard),
                        border = BorderStroke(if (isSelected) 1.dp else 0.5.dp,
                            if (isSelected) Color(0xFFE53935).copy(alpha=0.6f) else BgCardBorder)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (selectionMode) {
                                        Box(modifier = Modifier
                                            .background(if (isSelected) Color(0xFFE53935) else Color.Transparent, RoundedCornerShape(4.dp))
                                            .border(1.dp, if (isSelected) Color(0xFFE53935) else Color.White.copy(alpha=0.3f), RoundedCornerShape(4.dp))
                                            .size(18.dp), contentAlignment = Alignment.Center) {
                                            if (isSelected) Text("✓", fontSize = 10.sp, color = Color.White)
                                        }
                                    }
                                    Text(if (deal.metal == "Золото") "🥇" else "🥈", fontSize = 18.sp)
                                    Column {
                                        Text(deal.metal, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                                            color = if (deal.metal == "Золото") Gold else Silver)
                                        Text(deal.date, fontSize = 11.sp, color = Color.White.copy(alpha=0.35f))
                                    }
                                }
                                Text("${if (deal.profit >= 0) "+" else ""}${"%.2f".format(deal.profit)} ${deal.symbol}",
                                    fontSize = 15.sp, fontWeight = FontWeight.Bold,
                                    color = if (deal.profit >= 0) Color(0xFF4CAF50) else Color(0xFFE53935))
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = BgCardBorder)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Вес: ${"%.3f".format(deal.weight)} г  •  Проба: ${deal.purity.toInt()}",
                                    fontSize = 11.sp, color = Color.White.copy(alpha=0.45f))
                                Text("Чистый: ${"%.3f".format(deal.pureWeight)} г",
                                    fontSize = 11.sp, color = Color.White.copy(alpha=0.45f))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Покупка: ${"%.2f".format(deal.buyCost)} ${deal.symbol}",
                                    fontSize = 12.sp, color = Color.White.copy(alpha=0.6f))
                                Text("Продажа: ${"%.2f".format(deal.sellCost)} ${deal.symbol}",
                                    fontSize = 12.sp, color = Color.White.copy(alpha=0.6f))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ─── СИСТЕМА СИГНАЛОВ ─────────────────────────────────

fun saveAlerts(context: Context, goldTarget: Double?, goldDir: String?,
               silverTarget: Double?, silverDir: String?) {
    val prefs = context.getSharedPreferences("prometal_prefs", Context.MODE_PRIVATE)
    prefs.edit()
        .putString("gold_target", goldTarget?.toString() ?: "")
        .putString("gold_dir", goldDir ?: "")
        .putString("silver_target", silverTarget?.toString() ?: "")
        .putString("silver_dir", silverDir ?: "")
        .apply()
}

fun loadAlerts(context: Context): Map<String, String> {
    val prefs = context.getSharedPreferences("prometal_prefs", Context.MODE_PRIVATE)
    return mapOf(
        "gold_target" to (prefs.getString("gold_target", "") ?: ""),
        "gold_dir" to (prefs.getString("gold_dir", "") ?: ""),
        "silver_target" to (prefs.getString("silver_target", "") ?: ""),
        "silver_dir" to (prefs.getString("silver_dir", "") ?: "")
    )
}

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "prometal_alerts", "ProMetal Сигналы", NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Уведомления о достижении целевой цены" }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}

fun scheduleAlertWorker(context: Context) {
    val request = PeriodicWorkRequestBuilder<PriceAlertWorker>(15, TimeUnit.MINUTES)
        .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
        .build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "price_alert_worker", ExistingPeriodicWorkPolicy.UPDATE, request
    )
}

fun cancelAlertWorker(context: Context) {
    WorkManager.getInstance(context).cancelUniqueWork("price_alert_worker")
}

class PriceAlertWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val context = applicationContext
        val alerts = loadAlerts(context)
        val goldTarget = alerts["gold_target"]?.toDoubleOrNull() ?: return Result.success()
        val goldDir = alerts["gold_dir"] ?: return Result.success()
        val silverTarget = alerts["silver_target"]?.toDoubleOrNull()
        val silverDir = alerts["silver_dir"] ?: ""

        return try {
            val client = okhttp3.OkHttpClient()
            val prefs = context.getSharedPreferences("prometal_prefs", Context.MODE_PRIVATE)
            val currency = prefs.getString("country", "RUB") ?: "RUB"

            // Получаем текущую цену из ЦБ РФ
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy")
            val cal = java.util.Calendar.getInstance()
            var goldRub = 0.0
            var silverRub = 0.0

            for (d in 0..4) {
                val date = sdf.format(cal.time)
                val req = okhttp3.Request.Builder()
                    .url("https://www.cbr.ru/scripts/xml_metall.asp?date_req1=$date&date_req2=$date&P1=1&P2=1&P3=1&P4=1")
                    .header("User-Agent", "Mozilla/5.0").build()
                val resp = client.newCall(req).execute()
                if (resp.isSuccessful) {
                    val body = resp.body?.string() ?: ""
                    if (body.contains("<Record")) {
                        val factory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
                        val doc = factory.newDocumentBuilder().parse(body.byteInputStream())
                        val records = doc.getElementsByTagName("Record")
                        for (i in 0 until records.length) {
                            val r = records.item(i) as org.w3c.dom.Element
                            val sell = r.getElementsByTagName("Sell").item(0)?.textContent
                                ?.replace(",", ".")?.toDoubleOrNull() ?: continue
                            when (r.getAttribute("Code")) {
                                "1" -> goldRub = sell
                                "2" -> silverRub = sell
                            }
                        }
                        break
                    }
                }
                cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
            }
            if (goldRub == 0.0) return Result.retry()

            // Конвертируем в нужную валюту
            var rate = 1.0
            if (currency != "RUB") {
                try {
                    val req = okhttp3.Request.Builder()
                        .url("https://www.cbr.ru/scripts/XML_daily.asp")
                        .header("User-Agent", "Mozilla/5.0").build()
                    val xml = client.newCall(req).execute().body?.string() ?: ""
                    val doc = javax.xml.parsers.DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder().parse(xml.byteInputStream())
                    val valutes = doc.getElementsByTagName("Valute")
                    var usdRub = 89.5; var targetRub = 1.0; var nom = 1
                    for (i in 0 until valutes.length) {
                        val v = valutes.item(i) as org.w3c.dom.Element
                        val code = v.getElementsByTagName("CharCode").item(0)?.textContent
                        val value = v.getElementsByTagName("Value").item(0)?.textContent
                            ?.replace(",", ".")?.toDoubleOrNull() ?: continue
                        val nominal = v.getElementsByTagName("Nominal").item(0)?.textContent?.toIntOrNull() ?: 1
                        if (code == "USD") usdRub = value / nominal
                        if (code == currency) { targetRub = value; nom = nominal }
                    }
                    rate = if (currency == "USD") 1.0 / usdRub else (targetRub / nom) / usdRub
                } catch (e: Exception) { }
            }

            val goldPrice = if (currency == "RUB") goldRub else goldRub / 89.5 * rate * 31.1035
            val silverPrice = if (currency == "RUB") silverRub else silverRub / 89.5 * rate * 31.1035

            createNotificationChannel(context)

            // Проверяем золото
            val goldTriggered = when (goldDir) {
                "above" -> goldPrice >= goldTarget
                "below" -> goldPrice <= goldTarget
                else -> false
            }
            if (goldTriggered) {
                val dir = if (goldDir == "above") "поднялась выше" else "опустилась ниже"
                sendNotification(context, 1001, "🥇 Золото — сигнал сработал!",
                    "Цена $dir ${goldTarget.toLong()} и сейчас составляет ${goldPrice.toLong()}")
            }

            // Проверяем серебро
            if (silverTarget != null && silverTarget > 0) {
                val silverTriggered = when (silverDir) {
                    "above" -> silverPrice >= silverTarget
                    "below" -> silverPrice <= silverTarget
                    else -> false
                }
                if (silverTriggered) {
                    val dir = if (silverDir == "above") "поднялась выше" else "опустилась ниже"
                    sendNotification(context, 1002, "🥈 Серебро — сигнал сработал!",
                        "Цена $dir ${silverTarget.toLong()} и сейчас составляет ${silverPrice.toLong()}")
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

fun sendNotification(context: Context, id: Int, title: String, message: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) return
    }
    val notification = NotificationCompat.Builder(context, "prometal_alerts")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()
    NotificationManagerCompat.from(context).notify(id, notification)
}

@Composable
fun AlertsScreen(country: Country) {
    val context = LocalContext.current
    val alerts = remember { loadAlerts(context) }

    var goldTarget by remember { mutableStateOf(alerts["gold_target"] ?: "") }
    var goldDir by remember { mutableStateOf(alerts["gold_dir"] ?: "above") }
    var silverTarget by remember { mutableStateOf(alerts["silver_target"] ?: "") }
    var silverDir by remember { mutableStateOf(alerts["silver_dir"] ?: "above") }
    var alertsActive by remember { mutableStateOf(
        (alerts["gold_target"] ?: "").isNotEmpty() || (alerts["silver_target"] ?: "").isNotEmpty()
    )}
    var permissionGranted by remember { mutableStateOf(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        else true
    )}

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
        if (granted) {
            saveAlerts(context,
                goldTarget.toDoubleOrNull(), if (goldTarget.isNotEmpty()) goldDir else null,
                silverTarget.toDoubleOrNull(), if (silverTarget.isNotEmpty()) silverDir else null)
            scheduleAlertWorker(context)
            alertsActive = true
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(BgDark)
        .padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.height(52.dp))
        Text(S.signalsTitle, fontSize = 11.sp, color = Gold, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Уведомление придёт даже когда приложение закрыто",
            fontSize = 12.sp, color = Color.White.copy(alpha = 0.35f))
        Spacer(modifier = Modifier.height(20.dp))

        // Статус
        if (alertsActive) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)),
                border = BorderStroke(0.5.dp, Color(0xFF4CAF50).copy(alpha = 0.4f))) {
                Row(modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("●", fontSize = 10.sp, color = Color(0xFF4CAF50))
                    Text("Сигналы активны • проверка каждые 15 мин",
                        fontSize = 12.sp, color = Color(0xFF4CAF50))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Золото
        AlertMetalCard(
            metal = "Золото", symbol = country.symbol, color = Gold,
            emoji = "🥇", target = goldTarget, direction = goldDir,
            onTargetChange = { goldTarget = it },
            onDirectionChange = { goldDir = it }
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Серебро
        AlertMetalCard(
            metal = "Серебро", symbol = country.symbol, color = Silver,
            emoji = "🥈", target = silverTarget, direction = silverDir,
            onTargetChange = { silverTarget = it },
            onDirectionChange = { silverDir = it }
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Кнопка активации
        val canActivate = goldTarget.isNotEmpty() || silverTarget.isNotEmpty()
        Box(contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
                .background(if (canActivate) Gold else Gold.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() },
                    enabled = canActivate) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !permissionGranted) {
                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        saveAlerts(context,
                            goldTarget.toDoubleOrNull(), if (goldTarget.isNotEmpty()) goldDir else null,
                            silverTarget.toDoubleOrNull(), if (silverTarget.isNotEmpty()) silverDir else null)
                        scheduleAlertWorker(context)
                        alertsActive = true
                    }
                }
                .padding(vertical = 16.dp)) {
            Text("🔔  АКТИВИРОВАТЬ СИГНАЛЫ", fontSize = 14.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 1.sp,
                color = if (canActivate) BgDark else Color.White.copy(alpha = 0.3f))
        }

        // Кнопка отключить
        if (alertsActive) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
                    .background(Color(0xFFE53935).copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                    .border(0.5.dp, Color(0xFFE53935).copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                        cancelAlertWorker(context)
                        saveAlerts(context, null, null, null, null)
                        goldTarget = ""; silverTarget = ""
                        alertsActive = false
                    }
                    .padding(vertical = 14.dp)) {
                Text("Отключить все сигналы", fontSize = 13.sp, color = Color(0xFFE53935))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun AlertMetalCard(metal: String, symbol: String, color: Color, emoji: String,
                   target: String, direction: String,
                   onTargetChange: (String) -> Unit, onDirectionChange: (String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        border = BorderStroke(0.5.dp, if (target.isNotEmpty()) color.copy(alpha = 0.4f) else BgCardBorder)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(emoji, fontSize = 20.sp)
                Text(metal.uppercase(), fontSize = 12.sp,
                    fontWeight = FontWeight.Bold, color = color, letterSpacing = 2.sp)
                if (target.isNotEmpty()) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text("✓ установлен", fontSize = 10.sp, color = color.copy(alpha = 0.6f))
                }
            }
            Spacer(modifier = Modifier.height(14.dp))

            // Направление — выше или ниже
            Text("Уведомить когда цена:", fontSize = 11.sp, color = Color.White.copy(alpha = 0.45f))
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("above" to "📈  Поднимется выше", "below" to "📉  Опустится ниже").forEach { (dir, label) ->
                    val isSel = direction == dir
                    Box(contentAlignment = Alignment.Center,
                        modifier = Modifier.weight(1f)
                            .background(if (isSel) color.copy(alpha = 0.12f) else Color.Transparent, RoundedCornerShape(10.dp))
                            .border(0.5.dp, if (isSel) color else BgCardBorder, RoundedCornerShape(10.dp))
                            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onDirectionChange(dir) }
                            .padding(vertical = 10.dp)) {
                        Text(label, fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) color else Color.White.copy(alpha = 0.4f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))

            // Целевая цена
            Text("Целевая цена ($symbol за грамм):", fontSize = 11.sp, color = Color.White.copy(alpha = 0.45f))
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth()
                .background(Color(0xFF1A1A1A), RoundedCornerShape(10.dp))
                .border(0.5.dp, if (target.isNotEmpty()) color.copy(alpha = 0.5f) else BgCardBorder, RoundedCornerShape(10.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp)) {
                if (target.isEmpty()) Text("напр. 7500", fontSize = 15.sp, color = Color.White.copy(alpha = 0.2f))
                androidx.compose.foundation.text.BasicTextField(
                    value = target,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) onTargetChange(it) },
                    textStyle = TextStyle(fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Medium),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(currentCountry: Country, currentLang: String, onLanguageChange: (String) -> Unit, onCountryChange: (Country) -> Unit) {
    val languages = listOf(
        Triple("ru", "🇷🇺", "Русский"),
        Triple("en", "🇬🇧", "English"),
        Triple("hy", "🇦🇲", "Հայերեն"),
        Triple("tr", "🇹🇷", "Türkçe")
    )
    Column(modifier = Modifier.fillMaxSize().background(BgDark)
        .padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.height(52.dp))
        Text(S.settingsTitle, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldDim, letterSpacing = 3.sp)
        Spacer(modifier = Modifier.height(24.dp))

        // Язык
        Text("ЯЗЫК / LANGUAGE", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f), letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            languages.forEach { (code, flag, name) ->
                val isSelected = currentLang == code
                Box(modifier = Modifier.weight(1f)
                    .background(if (isSelected) Gold.copy(alpha = 0.12f) else BgCard, RoundedCornerShape(12.dp))
                    .border(if (isSelected) 1.dp else 0.5.dp,
                        if (isSelected) Gold else BgCardBorder, RoundedCornerShape(12.dp))
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                        onLanguageChange(code)
                    }
                    .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(flag, fontSize = 20.sp)
                        Text(name, fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Gold else Color.White.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Страна
        Text("СТРАНА И ВАЛЮТА", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f), letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(12.dp))
        countries.forEach { country ->
            val isSelected = country.currency == currentCountry.currency
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onCountryChange(country) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) Gold.copy(alpha = 0.1f) else BgCard),
                border = BorderStroke(if (isSelected) 1.dp else 0.5.dp, if (isSelected) Gold else BgCardBorder)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(country.flag, fontSize = 24.sp)
                        Column {
                            Text(country.name, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                                color = if (isSelected) Gold else Color.White)
                            Text(country.currency, fontSize = 11.sp, color = Color.White.copy(alpha = 0.4f))
                        }
                    }
                    if (isSelected) Text("✓", fontSize = 18.sp, color = Gold, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─── КОМПОНЕНТЫ ───────────────────────────────────────

@Composable
fun BottomNavBar(currentScreen: Screen, onScreenSelected: (Screen) -> Unit, modifier: Modifier = Modifier) {
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Box(modifier = modifier.fillMaxWidth().background(NavBg.copy(alpha = 0.95f))) {
        HorizontalDivider(color = GoldDark.copy(alpha = 0.3f), thickness = 0.5.dp)
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp + navBarPadding),
            horizontalArrangement = Arrangement.SpaceAround) {
            screens.forEach { screen ->
                val isSelected = currentScreen == screen
                val isCenter = screen == Screen.Market
                val icon = when (screen) {
                    Screen.Calculator -> Icons.Outlined.Calculate
                    Screen.Journal -> Icons.Outlined.EditNote
                    Screen.Market -> Icons.Outlined.ShowChart
                    Screen.Alerts -> Icons.Outlined.NotificationsNone
                    Screen.Settings -> Icons.Outlined.Settings
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(64.dp)
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onScreenSelected(screen) }
                        .padding(vertical = if (isCenter) 2.dp else 4.dp)
                ) {
                    if (isCenter) {
                        // Центральная кнопка — выделенная
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.CenterHorizontally)
                                .background(
                                    if (isSelected) Color(0xFF2A2000) else Color(0xFF1A1500),
                                    RoundedCornerShape(14.dp)
                                )
                                .border(1.dp, if (isSelected) Gold else GoldDark.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ShowChart,
                                contentDescription = "Рынок",
                                tint = if (isSelected) Gold else GoldDark,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(S.market, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = if (isSelected) Gold else Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth())
                    } else {
                        val screenLabel = when (screen) {
                            Screen.Calculator -> S.deal
                            Screen.Journal -> S.journal
                            Screen.Alerts -> S.alerts
                            Screen.Settings -> S.settings
                            else -> ""
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = screenLabel,
                            tint = if (isSelected) Gold else Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(if (isSelected) 24.dp else 22.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(screenLabel, fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Gold else Color.White.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(modifier = Modifier.width(16.dp).height(2.dp)
                            .background(if (isSelected) Gold else Color.Transparent, RoundedCornerShape(1.dp)))
                    }
                }
            }
        }
    }
}

@Composable
fun MetalCardWithChart(
    name: String, symbol: String, priceOz: String, priceG: String,
    change: String, changeAbs: String, isPositive: Boolean, color: Color,
    selectedUnit: String, currencySymbol: String,
    dayPrices: List<Float>, weekPrices: List<Float>, monthPrices: List<Float>
) {
    val price = if (selectedUnit == "oz") priceOz else priceG
    val unit = if (selectedUnit == "oz") "${currencySymbol}/унц" else "${currencySymbol}/г"
    var selectedPeriod by remember { mutableStateOf(S.today) }
    val prices = when (selectedPeriod) { S.today -> dayPrices; S.week -> weekPrices; else -> monthPrices }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        border = BorderStroke(0.5.dp, BgCardBorder)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Text(name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color, letterSpacing = 2.sp)
                    Text(symbol, fontSize = 11.sp, color = Color.White.copy(alpha = 0.3f), letterSpacing = 1.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(price, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(unit, fontSize = 11.sp, color = Color.White.copy(alpha = 0.3f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val clr = if (isPositive) Color(0xFF4CAF50) else Color(0xFFE53935)
                Text(changeAbs, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = clr)
                Text(change, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = clr)
                Text("за сегодня", fontSize = 11.sp, color = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.align(Alignment.CenterVertically))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(S.today, S.week, S.month).forEach { period ->
                    val isSel = selectedPeriod == period
                    Box(contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(if (isSel) color.copy(alpha = 0.15f) else Color.Transparent, RoundedCornerShape(6.dp))
                            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { selectedPeriod = period }
                            .padding(horizontal = 12.dp, vertical = 4.dp)) {
                        Text(period, fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) color else Color.White.copy(alpha = 0.4f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            PriceChart(prices = prices, color = color, modifier = Modifier.fillMaxWidth().height(100.dp))
        }
    }
}

@Composable
fun PriceChart(prices: List<Float>, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        if (prices.size < 2) return@Canvas
        val minP = prices.min(); val maxP = prices.max()
        val range = if (maxP - minP == 0f) 1f else maxP - minP
        val stepX = size.width / (prices.size - 1)
        val pt = 10f; val pb = 10f; val ch = size.height - pt - pb
        val path = Path(); val fill = Path()
        prices.forEachIndexed { i, p ->
            val x = i * stepX; val y = pt + ch - ((p - minP) / range) * ch
            if (i == 0) { path.moveTo(x, y); fill.moveTo(x, size.height); fill.lineTo(x, y) }
            else { path.lineTo(x, y); fill.lineTo(x, y) }
        }
        fill.lineTo((prices.size - 1) * stepX, size.height); fill.close()
        drawPath(path = fill, color = color.copy(alpha = 0.08f))
        drawPath(path = path, color = color, style = Stroke(width = 2f))
        val lx = (prices.size - 1) * stepX
        val ly = pt + ch - ((prices.last() - minP) / range) * ch
        drawCircle(color = color, radius = 5f, center = Offset(lx, ly))
        drawCircle(color = BgCard, radius = 3f, center = Offset(lx, ly))
    }
}

@Composable
fun UnitToggle(selected: String, onSelect: (String) -> Unit) {
    Row(modifier = Modifier.background(BgCard, RoundedCornerShape(8.dp)).padding(4.dp)) {
        listOf("oz" to S.oz, "g" to S.gram).forEach { (key, label) ->
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(if (selected == key) Gold else Color.Transparent, RoundedCornerShape(6.dp))
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onSelect(key) }
                    .padding(horizontal = 14.dp, vertical = 6.dp)) {
                Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    color = if (selected == key) BgDark else Color.White.copy(alpha = 0.5f))
            }
        }
    }
}

