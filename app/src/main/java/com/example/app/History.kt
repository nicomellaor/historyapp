package com.example.app

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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.theme.ColorFondo
import com.example.app.ui.theme.ColorBloque
import com.example.app.ui.theme.ColorBoton

class History : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cuenta = intent.getStringExtra("cuenta") ?: "Desconocido"
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Historial (cuenta) {
                    finish()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Historial(cuenta: String, onBack: () -> Unit) {
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
            infoCuentaGrande(ejemplos)
            Spacer(modifier = Modifier.height(20.dp))
            Row(){
                Column(){
                    Text(text = "Total: ", color = Color.White)
                }
                Column(){
                    Text(text = TotalCuenta(ejemplos).toString(), color = Color.White)
                }
            }
        }
    }
}


val ejemplos: List<Map<String, Any>> = listOf(
    mapOf(
        "nombre" to "Compra Supermercado",
        "fecha" to "2025-06-01",
        "monto" to -450
    ),
    mapOf(
        "nombre" to "Salario",
        "fecha" to "2025-06-01",
        "monto" to 1200
    ),
    mapOf(
        "nombre" to "Pago Luz",
        "fecha" to "2025-06-02",
        "monto" to -600
    ),
    mapOf(
        "nombre" to "Verduras",
        "fecha" to "2025-06-03",
        "monto" to -20000
    ),
    mapOf(
        "nombre" to "Depósito",
        "fecha" to "2025-06-04",
        "monto" to 15000
    ),
    mapOf(
        "nombre" to "Netflix",
        "fecha" to "2025-06-04",
        "monto" to -7000
    ),
    mapOf(
        "nombre" to "Jamón y pan",
        "fecha" to "2025-06-06",
        "monto" to -3600
    ),
    mapOf(
        "nombre" to "Deuda",
        "fecha" to "2025-06-07",
        "monto" to 40000
    ),
    mapOf(
        "nombre" to "Supermercado",
        "fecha" to "2025-06-10",
        "monto" to -30000
    ),
    mapOf(
        "nombre" to "Chocolate",
        "fecha" to "2025-06-10",
        "monto" to -600
    ),
    mapOf(
        "nombre" to "Once",
        "fecha" to "2025-06-12",
        "monto" to -2500
    )
)


@Composable
fun infoCuentaGrande(ejemplos: List<Map<String, Any>>){
    var mostrarDialogo by remember { mutableStateOf(false) }
    var itemSeleccionado by remember { mutableStateOf<Map<String, Any>?>(null) }


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
        var ultimoDia = ejemplos[0]["fecha"]
        for (movimiento in ejemplos) {
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
                Text(text = movimiento["nombre"].toString(), color = Color.White)
                Text(text = movimiento["monto"].toString(), color = Color.White)
            }
            ultimoDia = movimiento["fecha"]
        }
        if (mostrarDialogo && itemSeleccionado != null) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                title = { Text(text = "Detalle del Movimiento") },
                text = {
                    Text(
                        "Nombre: ${itemSeleccionado!!["nombre"]}\n" +
                                "Fecha: ${itemSeleccionado!!["fecha"]}\n" +
                                "Monto: ${itemSeleccionado!!["monto"]}"
                    )
                },
                confirmButton = {
                    Button(onClick = { mostrarDialogo = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }
    }
}

@Composable
fun TotalCuenta(cuenta: List<Map<String, Any>>): Int {
    var total = 0
    for (item in cuenta){
        total += item["monto"].toString().toInt()
    }
    return total
}

@Composable
fun EditarButton(){


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview5() {
    AppTheme {
        Historial("Cuenta 1", onBack = {})
    }
}
