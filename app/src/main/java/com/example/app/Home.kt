package com.example.app

import TransactionRecord
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.theme.ColorBloque
import com.example.app.ui.theme.ColorBoton
import com.example.app.ui.theme.ColorFondo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import toMap
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class Home : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cuenta = intent.getStringExtra("cuenta") ?: "Desconocido"
        enableEdgeToEdge()
        setContent {
            AppTheme {
                HomePage(cuenta) {
                    finish()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(cuenta: String, onBack: () -> Unit) {
    val context = LocalContext.current
    // Instancia de DataStore
    val accountsPreferences = AccountsPreferences(context)
    val scope = rememberCoroutineScope()

    // Obtener transacciones y pasar a Map (FALTA LIMITAR)
    val transacciones by accountsPreferences.getAccountTransactionsFlow(cuenta).collectAsState(initial = emptyList())
    val transaccionesMap: List<Map<String, Any>> = transacciones.map { it.toMap() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            AgregarMovButton(transaccionesMap, cuenta, scope, accountsPreferences)
        },
        // Logo de la App
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ColorFondo, // Azul
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                navigationIcon = { // Zona izquierda (para ícono de navegación)
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver atrás",
                        )
                    }
                },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.circle),
                            contentDescription = "Logo",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            )
        }
    ) {
            innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorFondo)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = cuenta, fontWeight = FontWeight.Medium, fontSize = 20.sp, color = ColorBoton)
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Últimos movimientos", color = Color.White)
            InfoCuenta(transaccionesMap)
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { navigateToHistory(context, cuenta) },
                colors = ButtonDefaults.buttonColors(containerColor = ColorBoton)
            )
            {
                Text("Historial Completo")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { navigateToAnalysis(context, cuenta) },
                colors = ButtonDefaults.buttonColors(containerColor = ColorBoton)
            )
            {
                Text("Análisis y Predicción")
            }
            Spacer(modifier = Modifier.height(20.dp))
            EliminarButton(cuenta, scope, accountsPreferences, context)
        }
    }
}

@Composable
fun InfoCuenta(ejemplos: List<Map<String, Any>>){
    val costos: List<Int> = ejemplos.map { it["monto"] as Int }
    val cuentas: List<String> = ejemplos.map { it["mensaje"] as String }

    Row(
        modifier = Modifier
            .background(
                color = ColorBloque,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 2.dp,
                color = ColorBloque,
                shape = RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp)) // Recorta el contenido interno
    ){
        Column(modifier = Modifier.padding(20.dp)){
            cuentas.forEach{ cuenta ->
                Column(horizontalAlignment = Alignment.Start){
                    Text(cuenta, color = Color.White)
                }
            }
        }
        Column(modifier = Modifier.padding(20.dp)){
            costos.forEach{ costo ->
                Column(horizontalAlignment = Alignment.End){
                    val txt = if (costo>0) "${'$'}$costo" else "-${'$'}${abs(costo)}"
                    Text(txt, color = Color.White)
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
    Row {
        Column {
            Text("Total: ", fontWeight = FontWeight.Medium, color = Color.White)
        }
        Column {
            Text(text = totalCuenta(ejemplos).toString(), color = Color.White)
        }
    }
}

@Composable
fun AgregarMovButton(cuentas: List<Map<String, Any>>, cuenta: String, scope: CoroutineScope, accountsPreferences: AccountsPreferences) {
    var showDialog by remember { mutableStateOf(false) }
    // Variables de estado
    var monto by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf(LocalDate.now()) }
    val formatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    var fechaText by remember { mutableStateOf(LocalDate.now().format(formatter)) }
    val total = totalCuenta(cuentas)

    Button(
        onClick = { showDialog = true  },
        modifier = Modifier.size(48.dp),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ColorBoton)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Añadir"
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Agregar Movimiento") },
            text = {
                Column {
                    TextField(
                        value = monto,
                        onValueChange = { monto = it },
                        label = { Text("Monto") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(Modifier.height(8.dp))
                    TextField(
                        value = mensaje,
                        onValueChange = { mensaje = it },
                        label = { Text("Mensaje") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    TextField(
                        value = fechaText,
                        onValueChange = { input ->
                            fechaText = input
                            runCatching { LocalDate.parse(input, formatter) }
                                .onSuccess {
                                    fecha = it
                                }
                        },
                        label = { Text("Fecha (yyyy-MM-dd)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Lógica para guardar
                        if (monto.isNotBlank() && mensaje.isNotBlank()){
                            scope.launch {
                                try {
                                    val id = accountsPreferences.generateId(cuenta)
                                    val transaction = TransactionRecord(id, monto.toInt(), mensaje, fecha, total)
                                    accountsPreferences.addTransaction(cuenta, transaction)
                                    monto = ""
                                    mensaje = ""
                                    fecha = LocalDate.now()
                                    showDialog = false
                                } catch (e: Exception) {
                                    println("Exception message: ${e.message}")
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorBoton)
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancelar", color = ColorBoton)
                }
            }
        )
    }
}

@Composable
fun EliminarButton(cuenta: String, scope: CoroutineScope, accountsPreferences: AccountsPreferences, context: Context) {
    var showDialog by remember { mutableStateOf(false) }

    Button(onClick = { showDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = ColorBoton)) {
        Text("Eliminar Cuenta")
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Eliminar Cuenta") },
            text = {
                Column {
                    Text("¿Está seguro/a de que desea eliminar $cuenta?")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Lógica para eliminar
                        scope.launch {
                            accountsPreferences.deleteAccount(cuenta)
                            showDialog = false
                            navigatetoMain(context)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorBoton)
                ) {
                    Text("Borrar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancelar", color = ColorBoton)
                }
            }
        )
    }
}

fun navigatetoMain(context: Context) {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    context.startActivity(intent)
}

fun navigateToAnalysis(context: Context, cuenta: String) {
    val intent = Intent(context, Analysis::class.java).apply {
        putExtra("cuenta", cuenta)
    }
    context.startActivity(intent)
}

fun navigateToHistory(context: Context, cuenta: String) {
    val intent = Intent(context, History::class.java).apply {
        putExtra("cuenta", cuenta)
    }
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    AppTheme {
        HomePage("Cuenta 1", onBack = {})
    }
}
