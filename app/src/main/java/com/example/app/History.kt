package com.example.app

import Account
import AccountViewModel
import TransactionRecord
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.theme.ColorBloque
import com.example.app.ui.theme.ColorBoton
import com.example.app.ui.theme.ColorFondo
import com.google.firebase.firestore.FirebaseFirestore
import toMap
import java.time.LocalDate

class History : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cuenta = intent.getStringExtra("cuenta") ?: "Desconocido"
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val viewModel = remember { AccountViewModel() }
                Historial (cuenta, viewModel) {
                    finish()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Historial(nombreCuenta: String, viewModel: AccountViewModel, onBack: () -> Unit) {
    // Instancia de Firebase Firestore
    val cuenta by viewModel.cuenta
    val userId = getCurrentUserUID()

    LaunchedEffect(Unit) {
        if (userId != null) {
            viewModel.observarCuenta(userId, nombreCuenta)
        }
    }

    val transacciones = cuenta.transacciones
    val transaccionesMap: List<Map<String, Any>> = transacciones.map { it.toMap() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ColorFondo,
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
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = cuenta.nombre, fontWeight = FontWeight.Medium, fontSize = 20.sp, color = ColorBoton)
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Últimos movimientos", color = Color.White)
            InfoCuentaGrande(transaccionesMap, cuenta)
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
fun InfoCuentaGrande(transaccionesMap: List<Map<String, Any>>, cuenta: Account){
    var mostrarDialogo by remember { mutableStateOf(false) }
    var itemSeleccionado by remember { mutableStateOf<Map<String, Any>?>(null) }
    var enEdicion by remember { mutableStateOf(false) }
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
                        }, colors = ButtonDefaults.buttonColors(containerColor = ColorBoton)) {
                            Text("Editar")
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Button(onClick = {
                            itemSeleccionado?.get("id")?.let { id ->
                                if (id is Int) {
                                    eliminarTransaccion(
                                        cuenta,
                                        id,
                                        onSuccess = {
                                            mostrarDialogo = false
                                        },
                                        onError = { Log.e("Firestore", "Error al borrar transaccion", it) }
                                    )
                                }
                            }
                        }, colors = ButtonDefaults.buttonColors(containerColor = ColorBoton)) {
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
                            label = { Text("Mensaje") },
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = ColorBoton,
                                focusedLabelColor = ColorBoton
                            ),
                        )
                        TextField(
                            value = nuevoMonto,
                            onValueChange = { nuevoMonto = it },
                            label = { Text("Monto") },
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = ColorBoton,
                                focusedLabelColor = ColorBoton
                            ),
                        )
                        TextField(
                            value = nuevaFecha,
                            onValueChange = { nuevaFecha = it },
                            label = { Text("Fecha") },
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = ColorBoton,
                                focusedLabelColor = ColorBoton
                            ),
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val updatedTransaction = TransactionRecord(
                            id = itemSeleccionado!!["id"].toString().toInt(),
                            monto = nuevoMonto.toInt(),
                            mensaje = nuevoNombre,
                            fecha = LocalDate.parse(nuevaFecha).toString(),
                            total = itemSeleccionado!!["total"].toString().toInt()
                        )
                        actualizarTransaccion(
                            cuenta = cuenta,
                            transaccionActualizada = updatedTransaction,
                            onSuccess = {
                                enEdicion = false
                                itemSeleccionado = null
                            },
                            onError = { Log.e("Firestore", "Error al setear transaccion", it) }
                        )
                    }, colors = ButtonDefaults.buttonColors(containerColor = ColorBoton)) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    Button(onClick = { enEdicion = false }, colors = ButtonDefaults.buttonColors(containerColor = ColorBoton)) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

fun actualizarTransaccion(cuenta: Account, transaccionActualizada: TransactionRecord, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("cuentas")
        .document(cuenta.id)
        .get()
        .addOnSuccessListener { document ->
            val account = document.toObject(Account::class.java)
            if (account != null) {
                // Actualizar la transacción en la lista
                val transaccionesActualizadas = account.transacciones.map { transaccion ->
                    if (transaccion.id == transaccionActualizada.id) {
                        transaccionActualizada
                    } else {
                        transaccion
                    }
                }

                // Recalcular el total para TODAS las transacciones
                val transaccionesConTotales = mutableListOf<TransactionRecord>()
                var total = 0

                transaccionesActualizadas.forEach { t ->
                    total += t.monto
                    transaccionesConTotales.add(t.copy(total = total))
                }

                // Guardar en Firebase
                db.collection("cuentas")
                    .document(cuenta.id)
                    .update("transacciones", transaccionesConTotales.map { it.toMap() })
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onError(it) }
            } else {
                onError(Exception("Cuenta no encontrada"))
            }
        }
        .addOnFailureListener { onError(it) }
}

fun eliminarTransaccion(cuenta: Account, transaccionId: Int, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("cuentas")
        .document(cuenta.id)
        .get()
        .addOnSuccessListener { document ->
            val account = document.toObject(Account::class.java)
            if (account != null) {
                // Filtrar la transacción que queremos eliminar
                val transaccionesFiltradas = account.transacciones.filter {
                    it.id != transaccionId
                }

                // Recalcular el total para TODAS las transacciones
                val transaccionesConTotales = mutableListOf<TransactionRecord>()
                var total = 0

                transaccionesFiltradas.forEach { t ->
                    total += t.monto
                    transaccionesConTotales.add(t.copy(total = total))
                }

                // Actualizar con la lista filtrada
                db.collection("cuentas")
                    .document(cuenta.id)
                    .update("transacciones", transaccionesConTotales.map { it.toMap() })
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onError(it) }
            }
        }
        .addOnFailureListener { onError(it) }
}

fun totalCuenta(cuenta: List<Map<String, Any>>): Int {
    return cuenta.sumOf { item ->
        item["monto"].toString().toInt()
    }
}
