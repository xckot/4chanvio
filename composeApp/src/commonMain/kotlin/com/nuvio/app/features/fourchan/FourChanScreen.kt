package com.nuvio.app.features.fourchan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

@Composable
fun FourChanScreen(onBack: () -> Unit) {
    var threads by remember { mutableStateOf<List<FChanThread>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val client = remember { HttpClient() }
    val json = remember { Json { ignoreUnknownKeys = true } }

    LaunchedEffect(Unit) {
        try {
            val response = client.get("https://a.4cdn.org/g/catalog.json").bodyAsText()
            val catalog = json.decodeFromString<List<FChanCatalog>>(response)
            threads = catalog.flatMap { it.threads }
        } catch (e: Exception) {
            errorMsg = e.message
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("4chan /g/") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<-", style = MaterialTheme.typography.titleLarge)
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMsg != null) {
                Text("Error: $errorMsg", modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(threads) { thread ->
                        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (thread.tim != null && thread.ext != null) {
                                    AsyncImage(
                                        model = "https://i.4cdn.org/g/${thread.tim}s.jpg",
                                        contentDescription = "Thumbnail",
                                        modifier = Modifier.fillMaxWidth().height(200.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                Text(
                                    text = thread.sub ?: "No Subject",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                if (thread.com != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = thread.com.replace(Regex("<[^>]*>"), "").replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&"),
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 3
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Replies: ${thread.replies}", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
