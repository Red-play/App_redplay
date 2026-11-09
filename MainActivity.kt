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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject

private val Bg = Color(0xFF080808)
private val Panel = Color(0xFF171717)
private val Panel2 = Color(0xFF222222)
private val Red = Color(0xFFFF1744)
private val Muted = Color(0xFFAAAAAA)

private data class Game(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val size: String = "راجع المتجر الرسمي",
    val mode: String = "يتطلب التحقق",
    val url: String = "",
    val featured: Boolean = false
)

private val starterGames = listOf(
    Game("dls", "Dream League Soccer", "كرة قدم", "كوّن فريق أحلامك، ونافس في مباريات كرة القدم وطوّر ناديك.", mode="أونلاين وميزات متصلة", url="https://play.google.com/store/search?q=Dream%20League%20Soccer&c=apps", featured=true),
    Game("efootball", "eFootball", "كرة قدم", "تجربة كرة قدم من KONAMI مع فرق ولاعبين ومنافسات متنوعة.", mode="يتطلب الإنترنت", url="https://play.google.com/store/search?q=eFootball&c=apps", featured=true),
    Game("fc", "EA SPORTS FC Mobile", "كرة قدم", "كوّن فريقك والعب مباريات كرة قدم على الهاتف.", mode="يتطلب الإنترنت", url="https://play.google.com/store/search?q=EA%20SPORTS%20FC%20Mobile&c=apps"),
    Game("pubg", "PUBG MOBILE", "أكشن", "معارك باتل رويال وتحديات جماعية على الهاتف.", mode="يتطلب الإنترنت", url="https://play.google.com/store/search?q=PUBG%20MOBILE&c=apps", featured=true),
    Game("freefire", "Free Fire", "أكشن", "مواجهات باتل رويال سريعة ومنافسات متعددة اللاعبين.", mode="يتطلب الإنترنت", url="https://play.google.com/store/search?q=Free%20Fire&c=apps"),
    Game("cod", "Call of Duty: Mobile", "أكشن", "أطوار لعب جماعية ومعارك ومنافسات تصويب.", mode="يتطلب الإنترنت", url="https://play.google.com/store/search?q=Call%20of%20Duty%20Mobile&c=apps"),
    Game("shadow", "Shadow Fight 2", "قتال", "قاتل خصوماً متنوعين واكتسب مهارات ومعدات جديدة.", mode="تختلف حسب الميزة", url="https://play.google.com/store/search?q=Shadow%20Fight%202&c=apps"),
    Game("minecraft", "Minecraft", "مغامرات", "استكشف وابنِ واصنع عوالمك الخاصة.", mode="أوفلاين وبعض الميزات أونلاين", url="https://play.google.com/store/search?q=Minecraft&c=apps", featured=true),
    Game("roblox", "Roblox", "مغامرات", "اكتشف تجارب وألعاباً متنوعة من صنع المجتمع.", mode="يتطلب الإنترنت", url="https://play.google.com/store/search?q=Roblox&c=apps"),
    Game("gta", "GTA: San Andreas", "مغامرات", "لعبة عالم مفتوح؛ تحقق من التوافق والتوفر في منطقتك.", mode="تجربة فردية", url="https://play.google.com/store/search?q=Grand%20Theft%20Auto%20San%20Andreas&c=apps"),
    Game("asphalt", "Asphalt Legends", "سباقات", "سباقات سيارات سريعة ومجموعة متنوعة من المركبات.", mode="ميزات متصلة", url="https://play.google.com/store/search?q=Asphalt%20Legends&c=apps"),
    Game("hill", "Hill Climb Racing", "سباقات", "قد سيارتك عبر تضاريس وتحديات فيزيائية ممتعة.", mode="تتوفر تجربة فردية", url="https://play.google.com/store/search?q=Hill%20Climb%20Racing&c=apps"),
    Game("traffic", "Traffic Rider", "سباقات", "قد دراجتك النارية عبر الطرق وتجاوز حركة المرور.", mode="تتوفر تجربة فردية", url="https://play.google.com/store/search?q=Traffic%20Rider&c=apps"),
    Game("subway", "Subway Surfers", "أركيد", "اركض وتفادى القطارات واجمع العملات في مغامرة لا تنتهي.", mode="تتوفر تجربة فردية", url="https://play.google.com/store/search?q=Subway%20Surfers&c=apps", featured=true),
    Game("temple", "Temple Run 2", "أركيد", "اهرب واجمع الكنوز وتجاوز العقبات.", mode="تتوفر تجربة فردية", url="https://play.google.com/store/search?q=Temple%20Run%202&c=apps"),
    Game("pvz", "Plants vs. Zombies", "استراتيجية", "استخدم النباتات للدفاع عن منزلك ضد موجات الزومبي.", mode="تختلف حسب الإصدار", url="https://play.google.com/store/search?q=Plants%20vs%20Zombies&c=apps"),
    Game("angry", "Angry Birds 2", "أركيد", "أطلق الطيور لهزيمة الخنازير واجتز المراحل.", mode="ميزات متصلة", url="https://play.google.com/store/search?q=Angry%20Birds%202&c=apps"),
    Game("stumble", "Stumble Guys", "أركيد", "تجاوز مسارات مليئة بالعقبات وتنافس مع لاعبين آخرين.", mode="يتطلب الإنترنت", url="https://play.google.com/store/search?q=Stumble%20Guys&c=apps"),
    Game("vector", "Vector", "أكشن", "اركض ونفّذ حركات باركور في عالم مليء بالتحديات.", mode="تتوفر تجربة فردية", url="https://play.google.com/store/search?q=Vector%20game&c=apps"),
    Game("candy", "Candy Crush Saga", "ألغاز", "طابق الحلوى واجتز مئات المراحل والألغاز.", mode="تتوفر تجربة فردية", url="https://play.google.com/store/search?q=Candy%20Crush%20Saga&c=apps")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RedPlayApp() }
    }
}

@Composable
private fun RedPlayApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("redplay_store", 0) }
    var games by remember { mutableStateOf(loadGames(prefs.getString("games", null))) }
    var favorites by remember { mutableStateOf(prefs.getStringSet("favorites", emptySet())?.toSet() ?: emptySet()) }
    var tab by remember { mutableStateOf("الرئيسية") }
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("الكل") }
    var selected by remember { mutableStateOf<Game?>(null) }
    var admin by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Game?>(null) }

    fun persistGames(list: List<Game>) { games = list; prefs.edit().putString("games", encodeGames(list)).apply() }
    fun toggleFavorite(game: Game) {
        favorites = if (game.id in favorites) favorites - game.id else favorites + game.id
        prefs.edit().putStringSet("favorites", favorites).apply()
    }

    MaterialTheme(colorScheme = darkColorScheme(primary = Red, background = Bg, surface = Panel, onSurface = Color.White)) {
        Surface(Modifier.fillMaxSize(), color = Bg) {
            when {
                admin -> AdminScreen(games, editing, onBack = { admin = false; editing = null }, onSave = { g ->
                    val list = if (games.any { it.id == g.id }) games.map { if (it.id == g.id) g else it } else games + g
                    persistGames(list); admin = false; editing = null
                }, onDelete = { g -> persistGames(games.filterNot { it.id == g.id }) }, onEdit = { editing = it })
                selected != null -> DetailsScreen(selected!!, selected!!.id in favorites, onBack = { selected = null }, onFavorite = { toggleFavorite(selected!!) })
                else -> Scaffold(containerColor = Bg, bottomBar = {
                    NavigationBar(containerColor = Color(0xFF101010), contentColor = Color.White) {
                        listOf("الرئيسية" to Icons.Default.Home, "الألعاب" to Icons.Default.SportsEsports, "المفضلة" to Icons.Default.Favorite, "حسابي" to Icons.Default.Person).forEach { (label, icon) ->
                            NavigationBarItem(selected = tab == label, onClick = { tab = label }, icon = { Icon(icon, null) }, label = { Text(label, fontSize = 10.sp) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Red, selectedTextColor = Red, indicatorColor = Color(0xFF351018), unselectedIconColor = Muted, unselectedTextColor = Muted))
                        }
                    }
                }) { padding ->
                    when (tab) {
                        "حسابي" -> ProfileScreen(games.size, favorites.size, onAdmin = { admin = true })
                        "المفضلة" -> GameListScreen("ألعابي المفضلة", games.filter { it.id in favorites }, query, { query = it }, category, { category = it }, { selected = it }, { toggleFavorite(it) }, favorites, padding)
                        "الألعاب" -> GameListScreen("مكتبة الألعاب", games, query, { query = it }, category, { category = it }, { selected = it }, { toggleFavorite(it) }, favorites, padding)
                        else -> HomeScreen(games, query, { query = it }, { selected = it }, { toggleFavorite(it) }, favorites, padding, onAll = { tab = "الألعاب" })
                    }
                }
            }
        }
    }
}

@Composable
private fun BrandHeader(onAdmin: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("REDPLAY", color = Red, fontSize = 29.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Text("GAMES  •  PLAY MORE. PLAY RED.", color = Muted, fontSize = 9.sp, letterSpacing = 1.5.sp)
        }
        if (onAdmin != null) IconButton(onClick = onAdmin) { Icon(Icons.Default.Settings, "الإدارة", tint = Muted) }
    }
}

@Composable
private fun SearchBox(query: String, onQuery: (String) -> Unit) {
    OutlinedTextField(value = query, onValueChange = onQuery, modifier = Modifier.fillMaxWidth(), placeholder = { Text("ابحث عن لعبة أو تصنيف...", color = Muted) }, leadingIcon = { Icon(Icons.Default.Search, null, tint = Red) }, singleLine = true, shape = RoundedCornerShape(15.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Red, unfocusedBorderColor = Panel2, focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = Red))
}

@Composable
private fun HomeScreen(games: List<Game>, query: String, onQuery: (String) -> Unit, onOpen: (Game) -> Unit, onFavorite: (Game) -> Unit, favorites: Set<String>, padding: PaddingValues, onAll: () -> Unit) {
    val featured = games.firstOrNull { it.featured } ?: games.firstOrNull()
    Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp)); BrandHeader(); Spacer(Modifier.height(16.dp)); SearchBox(query, onQuery); Spacer(Modifier.height(18.dp))
        if (featured != null) {
            Box(Modifier.fillMaxWidth().background(brush = androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF5B071B), Color(0xFF171717))), shape = RoundedCornerShape(22.dp)).padding(20.dp)) {
                Column {
                    Text("اختيار REDPLAY", color = Color(0xFFFFA6B6), fontSize = 11.sp)
                    Spacer(Modifier.height(7.dp)); Text(featured.title, color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Black)
                    Text("اكتشف اللعبة وتعرّف على تفاصيلها", color = Muted, fontSize = 12.sp)
                    Spacer(Modifier.height(13.dp)); Button(onClick = { onOpen(featured) }, colors = ButtonDefaults.buttonColors(containerColor = Red), shape = RoundedCornerShape(12.dp)) { Text("اكتشف الآن") }
                }
            }
        }
        Spacer(Modifier.height(20.dp)); Row(verticalAlignment = Alignment.CenterVertically) { Text("الألعاب الرائجة", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); TextButton(onClick = onAll) { Text("عرض الكل", color = Red) } }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 18.dp)) {
            items(games.filter { it.title.contains(query, true) || it.category.contains(query, true) }.take(12), key = { it.id }) { game -> GameCard(game, game.id in favorites, { onOpen(game) }, { onFavorite(game) }) }
        }
    }
}

@Composable
private fun GameListScreen(title: String, games: List<Game>, query: String, onQuery: (String) -> Unit, category: String, onCategory: (String) -> Unit, onOpen: (Game) -> Unit, onFavorite: (Game) -> Unit, favorites: Set<String>, padding: PaddingValues) {
    val categories = listOf("الكل", "كرة قدم", "أكشن", "قتال", "مغامرات", "سباقات", "أركيد", "استراتيجية", "ألغاز")
    val filtered = games.filter { (it.title.contains(query, true) || it.category.contains(query, true)) && (category == "الكل" || it.category == category) }
    Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp)); Text(title, color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(13.dp)); SearchBox(query, onQuery); Spacer(Modifier.height(10.dp))
        androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) { items(categories) { c -> FilterChip(selected = category == c, onClick = { onCategory(c) }, label = { Text(c, fontSize = 11.sp) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Red, selectedLabelColor = Color.White, labelColor = Muted)) } }
        Spacer(Modifier.height(10.dp)); Text("${filtered.size} لعبة", color = Muted, fontSize = 11.sp)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
            items(filtered, key = { it.id }) { game -> GameCard(game, game.id in favorites, { onOpen(game) }, { onFavorite(game) }) }
            if (filtered.isEmpty()) item { Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { Text("لا توجد ألعاب مطابقة حالياً", color = Muted) } }
        }
    }
}

@Composable
private fun GameCard(game: Game, favorite: Boolean, onClick: () -> Unit, onFavorite: () -> Unit) {
    Row(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(18.dp)).clickable(onClick = onClick).padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(68.dp).background(Color(0xFF351018), RoundedCornerShape(15.dp)), contentAlignment = Alignment.Center) { Text(game.category.take(1), color = Red, fontSize = 28.sp, fontWeight = FontWeight.Black) }
        Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) {
            Text(game.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp)); Text(game.category, color = Red, fontSize = 11.sp); Text(game.mode, color = Muted, fontSize = 10.sp, maxLines = 1)
        }
        IconButton(onClick = onFavorite) { Icon(if (favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, "المفضلة", tint = if (favorite) Red else Muted) }
    }
}

@Composable
private fun DetailsScreen(game: Game, favorite: Boolean, onBack: () -> Unit, onFavorite: () -> Unit) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().padding(18.dp)) {
        Text("‹  رجوع", color = Red, modifier = Modifier.clickable { onBack() }, fontSize = 17.sp); Spacer(Modifier.height(18.dp))
        Box(Modifier.fillMaxWidth().height(190.dp).background(brush = androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF5B071B), Panel2)), shape = RoundedCornerShape(24.dp)), contentAlignment = Alignment.Center) { Text("REDPLAY", color = Red, fontSize = 35.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp) }
        Spacer(Modifier.height(20.dp)); Text(game.title, color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black); Text(game.category, color = Red, fontSize = 13.sp)
        Spacer(Modifier.height(18.dp)); Text("عن اللعبة", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(7.dp)); Text(game.description, color = Muted, fontSize = 14.sp, lineHeight = 22.sp)
        Spacer(Modifier.height(18.dp)); InfoRow("التصنيف", game.category); InfoRow("حالة الاتصال", game.mode); InfoRow("الحجم", game.size)
        Spacer(Modifier.weight(1f)); Button(onClick = { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(game.url))) } }, enabled = game.url.startsWith("https://"), modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = Red), shape = RoundedCornerShape(15.dp)) { Icon(Icons.Default.Download, null); Spacer(Modifier.width(8.dp)); Text("افتح صفحة التحميل الرسمية", fontWeight = FontWeight.Bold) }
        TextButton(onClick = onFavorite, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text(if (favorite) "إزالة من المفضلة" else "إضافة إلى المفضلة", color = Color.White) }
        Text("زر التحميل يفتح صفحة البحث عن اللعبة في Google Play؛ تحقق من المطوّر والتوافق قبل التثبيت.", color = Muted, fontSize = 10.sp)
    }
}

@Composable private fun InfoRow(label: String, value: String) { Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) { Text(label, color = Muted, modifier = Modifier.weight(1f)); Text(value, color = Color.White) } }

@Composable
private fun ProfileScreen(gameCount: Int, favoriteCount: Int, onAdmin: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(35.dp)); Box(Modifier.size(94.dp).background(Color(0xFF351018), RoundedCornerShape(30.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Default.SportsEsports, null, tint = Red, modifier = Modifier.size(48.dp)) }
        Spacer(Modifier.height(15.dp)); Text("لاعب REDPLAY", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Black); Text("PLAY MORE. PLAY RED.", color = Muted, fontSize = 11.sp)
        Spacer(Modifier.height(25.dp)); Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { StatCard("الألعاب", gameCount.toString()); StatCard("المفضلة", favoriteCount.toString()) }
        Spacer(Modifier.height(25.dp)); Button(onClick = onAdmin, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Red), shape = RoundedCornerShape(14.dp)) { Icon(Icons.Default.AdminPanelSettings, null); Spacer(Modifier.width(8.dp)); Text("إدارة مكتبة الألعاب") }
        Spacer(Modifier.height(18.dp)); Text("REDPLAY GAMES • الإصدار 1.1", color = Muted, fontSize = 11.sp)
    }
}
@Composable private fun StatCard(label: String, value: String) { Column(Modifier.width(130.dp).background(Panel, RoundedCornerShape(16.dp)).padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(value, color = Red, fontSize = 25.sp, fontWeight = FontWeight.Black); Text(label, color = Muted, fontSize = 12.sp) } }

@Composable
private fun AdminScreen(games: List<Game>, editing: Game?, onBack: () -> Unit, onSave: (Game) -> Unit, onDelete: (Game) -> Unit, onEdit: (Game) -> Unit) {
    var title by remember(editing?.id) { mutableStateOf(editing?.title ?: "") }
    var category by remember(editing?.id) { mutableStateOf(editing?.category ?: "أكشن") }
    var description by remember(editing?.id) { mutableStateOf(editing?.description ?: "") }
    var size by remember(editing?.id) { mutableStateOf(editing?.size ?: "") }
    var mode by remember(editing?.id) { mutableStateOf(editing?.mode ?: "") }
    var url by remember(editing?.id) { mutableStateOf(editing?.url ?: "") }
    var error by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(15.dp)); Row(verticalAlignment = Alignment.CenterVertically) { Text("‹ رجوع", color = Red, modifier = Modifier.clickable { onBack() }); Spacer(Modifier.width(16.dp)); Text("لوحة إدارة المتجر", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(12.dp)); Text("${games.size} لعبة في المكتبة", color = Muted, fontSize = 12.sp)
        LazyColumn(contentPadding = PaddingValues(bottom = 25.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Text(if (editing == null) "إضافة لعبة" else "تعديل اللعبة", color = Red, fontWeight = FontWeight.Bold, fontSize = 17.sp); Spacer(Modifier.height(8.dp))
                AdminField("اسم اللعبة", title, { title = it }); AdminField("التصنيف", category, { category = it }); AdminField("الوصف", description, { description = it }, false); AdminField("الحجم (اختياري)", size, { size = it }); AdminField("حالة الاتصال", mode, { mode = it }); AdminField("رابط الصفحة الرسمية أو التحميل", url, { url = it }, keyboard = KeyboardType.Uri)
                if (error.isNotBlank()) Text(error, color = Red, fontSize = 12.sp)
                Button(onClick = {
                    error = when { title.isBlank() || category.isBlank() || description.isBlank() -> "أكمل الاسم والتصنيف والوصف."; !url.startsWith("https://") -> "استخدم رابط HTTPS صحيحاً."; else -> "" }
                    if (error.isEmpty()) onSave(Game(editing?.id ?: "custom_${System.currentTimeMillis()}", title.trim(), category.trim(), description.trim(), size.ifBlank { "غير محدد" }, mode.ifBlank { "غير محدد" }, url.trim(), editing?.featured ?: false))
                }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Red)) { Text(if (editing == null) "حفظ اللعبة" else "حفظ التعديلات") }
                Spacer(Modifier.height(18.dp)); Divider(color = Panel2); Spacer(Modifier.height(12.dp)); Text("إدارة الألعاب الموجودة", color = Color.White, fontWeight = FontWeight.Bold)
            }
            items(games, key = { it.id }) { game ->
                Row(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(13.dp)).padding(11.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) { Text(game.title, color = Color.White, fontWeight = FontWeight.Bold); Text(game.category, color = Muted, fontSize = 11.sp) }
                    TextButton(onClick = { onEdit(game) }) { Text("تعديل", color = Red) }
                    TextButton(onClick = { onDelete(game) }) { Text("حذف", color = Color(0xFFFF6B6B)) }
                }
            }
        }
    }
}

@Composable private fun AdminField(label: String, value: String, onValueChange: (String) -> Unit, singleLine: Boolean = true, keyboard: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label) }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), singleLine = singleLine, keyboardOptions = KeyboardOptions(keyboardType = keyboard), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Red, unfocusedBorderColor = Panel2, focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedLabelColor = Red, unfocusedLabelColor = Muted))
}

private fun encodeGames(games: List<Game>): String = JSONArray().apply { games.forEach { g -> put(JSONObject().apply { put("id", g.id); put("title", g.title); put("category", g.category); put("description", g.description); put("size", g.size); put("mode", g.mode); put("url", g.url); put("featured", g.featured) }) } }.toString()
private fun loadGames(raw: String?): List<Game> {
    if (raw.isNullOrBlank()) return starterGames
    return runCatching { val a = JSONArray(raw); (0 until a.length()).map { i -> val o = a.getJSONObject(i); Game(o.optString("id"), o.optString("title"), o.optString("category"), o.optString("description"), o.optString("size"), o.optString("mode"), o.optString("url"), o.optBoolean("featured")) } }.getOrDefault(starterGames)
}
