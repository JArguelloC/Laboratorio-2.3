package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.data.ReviewPreferences
import com.example.myapplication.ui.ReviewViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val TAG = "LIFECYCLE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate invocado")
        enableEdgeToEdge()

        val preferences = ReviewPreferences(this)
        val viewModel: ReviewViewModel by viewModels {
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                    val savedStateHandle = extras.createSavedStateHandle()
                    return ReviewViewModel(savedStateHandle, preferences) as T
                }
            }
        }

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ReviewScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onStart() { super.onStart(); Log.d(TAG, "onStart invocado") }
    override fun onResume() { super.onResume(); Log.d(TAG, "onResume invocado") }
    override fun onPause() { super.onPause(); Log.d(TAG, "onPause invocado") }
    override fun onStop() { super.onStop(); Log.d(TAG, "onStop invocado") }
    override fun onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy invocado") }
}

@Composable
fun ReviewScreen(viewModel: ReviewViewModel, modifier: Modifier = Modifier) {
    val diskDraft by viewModel.savedDraft.observeAsState("")

    // Sincronizar el borrador persistido desde disco al inicializar
    LaunchedEffect(diskDraft) {
        if (diskDraft.isNotEmpty() && viewModel.comment.isEmpty()) {
            viewModel.updateComment(diskDraft)
        }
    }

    // Regla de Navegación: Interceptar retroceso si hay texto escrito
    BackHandler(enabled = viewModel.comment.isNotEmpty()) {
        Log.d("NAV", "Gesto Atrás interceptado: El usuario tiene un comentario en curso")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Sistema de Reseñas", style = MaterialTheme.typography.headlineMedium)

        Text("Calificación del producto: ${viewModel.rating.toInt()} / 5")
        Slider(
            value = viewModel.rating,
            onValueChange = { viewModel.updateRating(it) },
            valueRange = 1f..5f,
            steps = 3
        )

        OutlinedTextField(
            value = viewModel.comment,
            onValueChange = { viewModel.updateComment(it) },
            label = { Text("Escribe tu comentario") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Button(
            onClick = { viewModel.saveDraftToDisk() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Borrador en Disco")
        }
    }
}