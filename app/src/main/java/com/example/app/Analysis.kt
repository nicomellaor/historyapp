package com.example.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.theme.ColorBoton
import com.example.app.ui.theme.ColorExtra1
import com.example.app.ui.theme.ColorExtra2
import com.example.app.ui.theme.ColorFondo
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Analysis : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cuenta = intent.getStringExtra("cuenta") ?: "Desconocido"
        enableEdgeToEdge()
        setContent {
            AppTheme {
                PantallaAnalisis (cuenta) {
                    finish()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAnalisis(cuenta: String, onBack: () -> Unit) {

    val months = listOf("Marzo", "Abril", "Mayo", "Junio", "Julio")
    // userPreferences
    val fechas = listOf(
        LocalDateTime.of(2024, 1, 1, 0, 0),
        LocalDateTime.of(2024, 2, 1, 0, 0),
        LocalDateTime.of(2024, 3, 1, 0, 0),
        LocalDateTime.of(2024, 4, 1, 0, 0),
        LocalDateTime.of(2024, 5, 1, 0, 0),
        LocalDateTime.of(2024, 6, 1, 0, 0)
    )
    val montos = listOf(100, -50, 200, 150, -30, 300) // Totales

    Scaffold (
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
                    Row (modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End){
                        Image(
                            painter = painterResource(id = R.drawable.circle),
                            contentDescription = "Logo",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorFondo)
                .padding(innerPadding),
            //verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Análisis y Predicción", fontWeight = FontWeight.Medium, fontSize = 20.sp, color = ColorBoton)
            Spacer(modifier = Modifier.height(20.dp))
            GraficoLineas(fechas, montos)
            Spacer(modifier = Modifier.height(20.dp))
            DetalleButton()
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Período", color = ColorExtra1)
            Spacer(modifier = Modifier.height(10.dp))
            MesesButton(months)
            Spacer(modifier = Modifier.height(10.dp))
            MesesButton(months)
        }
    }
}

@Composable
fun MesesButton(months: List<String>) {
    val isDropDownExpanded = remember {
        mutableStateOf(false)
    }

    val itemPosition = remember {
        mutableIntStateOf(0)
    }

    Box {
        Row (
            modifier = Modifier
                .clickable { isDropDownExpanded.value = true }
                .background(color = Color.White)
                .padding(5.dp)
        ) {
            Text(text = months[itemPosition.value], color = ColorExtra2)
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Expandir",
                tint = ColorExtra2
            )
        }
        DropdownMenu(
            expanded = isDropDownExpanded.value,
            onDismissRequest = {
                isDropDownExpanded.value = false
            }
        ) {
            months.forEachIndexed { index, month ->
                DropdownMenuItem(
                    text = { Text(month, color = ColorExtra2) },
                    onClick = {
                        isDropDownExpanded.value = false
                        itemPosition.value = index
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GraficoLineas(fechas: List<LocalDateTime>, montos: List<Int>) {
    AndroidView(
        factory = { context ->
            val linechart = LineChart(context)
            val entries = mutableListOf<Entry>()

            montos.forEachIndexed { index, amount ->
                entries.add(Entry(index.toFloat(), amount.toFloat()))
            }

            val formatter = DateTimeFormatter.ofPattern("MM/yyyy")
            val fechasString = fechas.map { fecha ->
                fecha.format(formatter)
            }

            linechart.xAxis.apply {
                setDrawGridLines(false)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                axisLineColor = Color.White.toArgb()
                valueFormatter = IndexAxisValueFormatter(fechasString)
                axisLineWidth = 2f
            }

            linechart.axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.White.toArgb()
                gridLineWidth = 1f
                setDrawAxisLine(true)
                axisLineColor = Color.White.toArgb()
                axisLineWidth = 2f
                setDrawZeroLine(true)
                zeroLineColor = Color.White.toArgb()
                zeroLineWidth = 1f
            }

            val dataset = LineDataSet(entries, "Totales").apply {
                color = ColorBoton.toArgb()
                setCircleColor(ColorBoton.toArgb())
                lineWidth = 3f
                circleRadius = 4f
                setDrawCircleHole(false)
                valueTextSize = 9f
                setDrawFilled(false)
                mode = LineDataSet.Mode.LINEAR
            }

            linechart.apply { description.isEnabled = false }
            linechart.data = LineData(dataset)

            linechart
        },
        modifier = Modifier.fillMaxWidth().height(300.dp)
    )
}

@Composable
fun DetalleButton() {
    var showDialog by remember { mutableStateOf(false) }

    TextButton(onClick = { showDialog = true }) {
        Text(text = "Análisis al detalle", color = ColorBoton)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Predicción de Montos") },
            text = {
                Column {
                    Text("Aproximación por curva")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
}

fun prediccion() {

}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview4() {
    AppTheme {
        PantallaAnalisis(cuenta = "Cuenta 1", onBack = {})
    }
}
