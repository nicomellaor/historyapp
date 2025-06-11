package com.example.app

import TransactionRecord
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.theme.ColorFondo
import com.example.app.ui.theme.ColorBloque
import com.example.app.ui.theme.ColorBoton
import kotlinx.coroutines.launch
import toMap
import java.time.LocalDate

class History : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cuenta = intent.getStringExtra("cuenta") ?: "Desconocido"
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Historial (cuenta = cuenta) {
                    finish()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Historial(cuenta: String, onBack: () -> Unit) {
    val context = LocalContext.current
    // Instancia de DataStore
    val accountsPreferences = AccountsPreferences(context)

    val transacciones by accountsPreferences.getAccountTransactionsFlow(cuenta).collectAsState(initial = emptyList())
    val transaccionesMap: List<Map<String, Any>> = transacciones.map { it.toMap() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
    ){
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
            InfoCuentaGrande(transaccionesMap, accountsPreferences, cuenta)
            Spacer(modifier = Modifier.height(20.dp))
            Row {
                Column {
                    Text(text = "Total: ", color = Color.White)
                }
                Column {
                    Text(text = totalCuenta(transaccionesMap).toString(), color = Color.White)
                }
            }
        }
    }
}

@Composable
fun InfoCuentaGrande(transaccionesMap: List<Map<String, Any>>, accountsPreferences: AccountsPreferences, cuenta: String){
    var mostrarDialogo by remember { mutableStateOf(false) }
    var itemSeleccionado by remember { mutableStateOf<Map<String, Any>?>(null) }
    var enEdicion by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Column(
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
            .padding(horizontal = 4.dp)
    ){
        if(transaccionesMap.isNotEmpty()){
            var ultimoDia = transaccionesMap[0]["fecha"]
            for (movimiento in transaccionesMap) {
                if (ultimoDia != movimiento["fecha"]){
                    HorizontalDivider(color = Color.White)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            itemSeleccionado = movimiento
                            mostrarDialogo = true
                        }
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween

                ) {
                    Text(text = movimiento["fecha"].toString().substring(5), color = Color.White)
                    Text(text = movimiento["mensaje"].toString(), color = Color.White)
                    Text(text = movimiento["monto"].toString(), color = Color.White)
                }
                ultimoDia = movimiento["fecha"]
            }} else {
            Text(text = "No hay transacciones", color = Color.White)
        }
        if (mostrarDialogo && itemSeleccionado != null) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                title = { Text(text = "Detalle del Movimiento") },
                text = {
                    Text(
                        "Nombre: ${itemSeleccionado!!["mensaje"]}\n" +
                                "Fecha: ${itemSeleccionado!!["fecha"]}\n" +
                                "Monto: ${itemSeleccionado!!["monto"]}"
                    )
                },
                confirmButton = {
                    Row {
                        Button(onClick = {
                            mostrarDialogo = false
                            enEdicion = true
                        }) {
                            Text("Editar")
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Button(onClick = {
                            mostrarDialogo = false
                            itemSeleccionado?.get("id")?.let { id ->
                                if (id is Int) {
                                    scope.launch {
                                        accountsPreferences.deleteTransaction(cuenta,id)
                                    }
                                }
                            }
                        }) {
                            Text("Eliminar")
                        }
                    }
                }
            )
        }
        if (enEdicion && itemSeleccionado != null) {
            var nuevoNombre by remember { mutableStateOf(itemSeleccionado!!["mensaje"].toString()) }
            var nuevoMonto by remember { mutableStateOf(itemSeleccionado!!["monto"].toString()) }
            var nuevaFecha by remember { mutableStateOf(itemSeleccionado!!["fecha"].toString()) }


            AlertDialog(
                onDismissRequest = { enEdicion = false },
                title = { Text("Editar Movimiento") },
                text = {
                    Column {
                        TextField(
                            value = nuevoNombre,
                            onValueChange = { nuevoNombre = it },
                            label = { Text("Mensaje") }
                        )
                        TextField(
                            value = nuevoMonto,
                            onValueChange = { nuevoMonto = it },
                            label = { Text("Monto") }
                        )
                        TextField(
                            value = nuevaFecha,
                            onValueChange = { nuevaFecha = it },
                            label = { Text("Fecha") }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        scope.launch {
                            val updatedTransaction = TransactionRecord(
                                id = itemSeleccionado!!["id"].toString().toInt(),
                                monto = nuevoMonto.toInt(),
                                mensaje = nuevoNombre,
                                fecha = LocalDate.parse(nuevaFecha),
                                total = 0
                            )
                            accountsPreferences.updateTransaction(cuenta, updatedTransaction)
                            enEdicion = false
                            itemSeleccionado = null
                        }
                    }) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    Button(onClick = { enEdicion = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun totalCuenta(cuenta: List<Map<String, Any>>): Int {
    var total = 0
    for (item in cuenta){
        total += item["monto"].toString().toInt()
    }
    return total
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview5() {
    AppTheme {
        Historial("Cuenta 1", onBack = {})
    }
}
