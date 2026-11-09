package com.redplay.games

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject

private val Bg = Color(0xFF080808)
private val Panel = Color(0xFF171717)
private val Red = Color(0xFFE50925)
private val Muted = Color(0xFFAAAAAA)

data class Game(
    val title: String,
    val category: String,
    val description: String,
    val size: String,
    val downloadUrl: String = ""
)

private val starterGames = listOf(
    Game("Dream League Soccer", "رياضة", "كوّن فريقك وخض مباريات كرة قدم مثيرة.", "يُضاف لاحقاً"),
    Game("Shadow Fight", "قتال", "قاتل خصوماً أقوياء في عالم مليء بالتحديات.", "يُضاف لاحقاً"),
    Game("Subway Surfers", "أركيد", "اركض وتفادى العقبات واجمع العملات.", "يُضاف لاحقاً"),
    Game("Minecraft", "مغامرات", "استكشف وابنِ عوالمك بطريقتك.", "يُضاف لاحقاً")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RedPlayApp() }
    }
}

@Composable
fun RedPlayApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("redplay_data", 0) }
    var games by remember {
        mutableStateOf(loadGames(prefs.getString("games", null)))
    }
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("الكل") }
    var selectedGame by remember { mutableStateOf<Game?>(null) }
    var showAdmin by remember { mutableStateOf(false) }
    var favorites by remember { mutableStateOf(setOf<String>()) }

    MaterialTheme(colorScheme = darkColorScheme(primary = Red, background = Bg, surface = Panel)) {
        Surface(Modifier.fillMaxSize(), color = Bg) {
            when {
                showAdmin -> AdminScreen(
                    onBack = { showAdmin = false },
                    onAdd = { game ->
                        games = games + game
                        prefs.edit().putString("games", saveGames(games)).apply()
                        showAdmin = false
                    }
                )
                selectedGame != null -> GameDetails(
                    game = selectedGame!!,
                    isFavorite = selectedGame!!.title in favorites,
                    onFavorite = {
                        val name = selectedGame!!.title
                        favorites = if (name in favorites) favorites - name else favorites + name
                    },
                    onBack = { selectedGame = null }
                )
                else -> HomeScreen(
                    games = games,
                    query = query,
                    onQuery = { query = it },
                    category = category,
                    onCategory = { category = it },
                    favorites = favorites,
                    onOpenGame = { selectedGame = it },
                    onFavorite = { game ->
                        favorites = if (game.title in favorites) favorites - game.title else favorites + game.title
                    },
                    onAdmin = { showAdmin = true }
                )
            }
        }
    }
}

@Composable
private fun HomeScreen(
    games: List<Game>, query: String, onQuery: (String) -> Unit,
    category: String, onCategory: (String) -> Unit,
    favorites: Set<String>, onOpenGame: (Game) -> Unit,
    onFavorite: (Game) -> Unit, onAdmin: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
        Spacer(Modifier.height(22.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("REDPLAY", color = Red, fontSize = 29.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                Text("GAMES  •  PLAY MORE", color = Color.White, fontSize = 10.sp, letterSpacing = 3.sp)
            }
            TextButton(onClick = onAdmin) { Text("الإدارة", color = Muted) }
        }
        Spacer(Modifier.height(18.dp))
        OutlinedTextField(
            value = query, onValueChange = onQuery, modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("ابحث عن لعبة...", color = Muted) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Red) },
            singleLine = true, shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Red, unfocusedBorderColor = Panel,
                focusedTextColor = Color.White, unfocusedTextColor = Color.White
            )
        )
        Spacer(Modifier.height(18.dp))
        Text("اكتشف الألعاب", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("مكتبتك للألعاب في مكان واحد", color = Muted, fontSize = 13.sp)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            listOf("الكل", "رياضة", "قتال", "أركيد", "مغامرات").forEach { item ->
                FilterChip(
                    selected = category == item, onClick = { onCategory(item) },
                    label = { Text(item, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Red, selectedLabelColor = Color.White,
                        labelColor = Muted
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true, selected = category == item,
                        borderColor = Panel, selectedBorderColor = Red
                    )
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        val filtered = games.filter {
            (it.title.contains(query, true) || it.category.contains(query, true)) &&
            (category == "الكل" || it.category == category)
        }
        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text("مفيش ألعاب مطابقة لبحثك حالياً", color = Muted)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(11.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
                items(filtered) { game ->
                    GameCard(game, game.title in favorites, { onOpenGame(game) }, { onFavorite(game) })
                }
            }
        }
    }
}

@Composable
private fun GameCard(game: Game, favorite: Boolean, onClick: () -> Unit, onFavorite: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(66.dp).background(Color(0xFF351018), RoundedCornerShape(15.dp)), contentAlignment = Alignment.Center) {
            Text("RP", color = Red, fontWeight = FontWeight.Black, fontSize = 23.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(game.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(4.dp))
            Text(game.category, color = Muted, fontSize = 11.sp)
            Text("عرض التفاصيل", color = Red, fontSize = 11.sp)
        }
        IconButton(onClick = onFavorite) {
            Icon(if (favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "المفضلة", tint = if (favorite) Red else Muted)
        }
    }
}

@Composable
private fun GameDetails(game: Game, isFavorite: Boolean, onFavorite: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("‹  رجوع", color = Red, fontSize = 17.sp, modifier = Modifier.clickable { onBack() })
        Spacer(Modifier.height(28.dp))
        Box(Modifier.fillMaxWidth().height(170.dp).background(Color(0xFF351018), RoundedCornerShape(22.dp)), contentAlignment = Alignment.Center) {
            Text("REDPLAY", color = Red, fontSize = 34.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(22.dp))
        Text(game.title, color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Text(game.category, color = Muted, fontSize = 13.sp)
        Spacer(Modifier.height(18.dp))
        Text("عن اللعبة", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(game.description, color = Muted, fontSize = 14.sp, lineHeight = 22.sp)
        Spacer(Modifier.height(10.dp))
        Text("حجم الملف: ${game.size}", color = Muted, fontSize = 12.sp)
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                if (game.downloadUrl.startsWith("https://") || game.downloadUrl.startsWith("http://")) {
                    runCatching {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(game.downloadUrl)))
                    }
                }
            },
            enabled = game.downloadUrl.startsWith("https://") || game.downloadUrl.startsWith("http://"),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Red, disabledContainerColor = Panel),
            shape = RoundedCornerShape(14.dp)
        ) { Text("تحميل اللعبة", fontWeight = FontWeight.Bold) }
        TextButton(onClick = onFavorite, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(if (isFavorite) "إزالة من المفضلة" else "إضافة إلى المفضلة", color = Color.White)
        }
        Text("استخدم روابط تملك حق توزيعها، وافحص ملفات APK قبل تثبيتها.", color = Muted, fontSize = 11.sp)
    }
}

@Composable
private fun AdminScreen(onBack: () -> Unit, onAdd: (Game) -> Unit) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("أكشن") }
    var description by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("‹  رجوع", color = Red, modifier = Modifier.clickable { onBack() }, fontSize = 17.sp)
        Spacer(Modifier.height(18.dp))
        Text("لوحة إدارة RedPlay", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("أضف لعبة جديدة إلى المكتبة", color = Muted, fontSize = 13.sp)
        Spacer(Modifier.height(18.dp))
        AdminField("اسم اللعبة", title, { title = it })
        AdminField("التصنيف (مثال: رياضة)", category, { category = it })
        AdminField("وصف اللعبة", description, { description = it }, singleLine = false)
        AdminField("حجم الملف (مثال: 850 MB)", size, { size = it })
        AdminField("رابط التحميل المباشر (https://...)", url, { url = it }, keyboard = KeyboardType.Uri)
        if (error.isNotBlank()) Text(error, color = Red, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {
                error = when {
                    title.isBlank() || category.isBlank() || description.isBlank() -> "اكتب اسم اللعبة والتصنيف والوصف."
                    !(url.startsWith("https://") || url.startsWith("http://")) -> "أدخل رابط تحميل يبدأ بـ https:// أو http://"
                    else -> ""
                }
                if (error.isEmpty()) onAdd(Game(title.trim(), category.trim(), description.trim(), size.ifBlank { "غير محدد" }, url.trim()))
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Red),
            shape = RoundedCornerShape(14.dp)
        ) { Text("حفظ اللعبة", fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(10.dp))
        Text("تنبيه: هذه لوحة محلية أولية داخل التطبيق وليست لوحة ويب محمية بحسابات. لا تضع روابط لملفات لا تملك حق توزيعها.", color = Muted, fontSize = 11.sp)
    }
}

@Composable
private fun AdminField(
    label: String, value: String, onValueChange: (String) -> Unit,
    singleLine: Boolean = true, keyboard: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(bottom = 9.dp),
        singleLine = singleLine, keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        shape = RoundedCornerShape(13.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Red, unfocusedBorderColor = Panel,
            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
            focusedLabelColor = Red, unfocusedLabelColor = Muted
        )
    )
}

private fun saveGames(games: List<Game>): String {
    val array = JSONArray()
    games.forEach { game ->
        array.put(JSONObject().apply {
            put("title", game.title); put("category", game.category)
            put("description", game.description); put("size", game.size)
            put("downloadUrl", game.downloadUrl)
        })
    }
    return array.toString()
}

private fun loadGames(raw: String?): List<Game> {
    if (raw.isNullOrBlank()) return starterGames
    return runCatching {
        val array = JSONArray(raw)
        (0 until array.length()).map { i ->
            val obj = array.getJSONObject(i)
            Game(
                obj.optString("title"), obj.optString("category"),
                obj.optString("description"), obj.optString("size"),
                obj.optString("downloadUrl")
            )
        }
    }.getOrDefault(starterGames)
}
