package com.example.app

import AccountViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import java.time.format.DateTimeFormatter

class Analysis : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cuenta = intent.getStringExtra("cuenta") ?: "Desconocido"
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val viewModel = remember { AccountViewModel() }
                PantallaAnalisis (cuenta, viewModel) {
                    finish()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAnalisis(nombreCuenta: String, viewModel: AccountViewModel, onBack: () -> Unit) {
    // Instancia de Firebase Firestore
    val cuenta by viewModel.cuenta
    val userId = getCurrentUserUID()

    LaunchedEffect(Unit) {
        if (userId != null) {
            viewModel.observarCuenta(userId, nombreCuenta)
        }
    }
    val transacciones = cuenta.transacciones

    Scaffold (
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
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Análisis y Predicción", fontWeight = FontWeight.Medium, fontSize = 20.sp, color = ColorBoton)
            Spacer(modifier = Modifier.height(20.dp))
            when {
                transacciones.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay transacciones para mostrar",
                            color = Color.White
                        )
                    }
                }
                else -> {
                    val fechas = transacciones.map { it.fecha }

                    var fechaInicioIndex by remember { mutableIntStateOf(0) }
                    var fechaFinIndex by remember { mutableIntStateOf(fechas.size - 1) }
                    val (fechasFiltradas, totalesFiltrados) = remember(fechaInicioIndex, fechaFinIndex, transacciones) {
                        if (transacciones.isEmpty()) {
                            Pair(emptyList(), emptyList())
                        } else {
                            val inicio = fechaInicioIndex.coerceAtMost(fechaFinIndex)
                            val fin = (fechaFinIndex + 1).coerceAtMost(transacciones.size)
                            val filtered = transacciones.subList(inicio, fin)
                            Pair(
                                filtered.map { it.fecha },
                                filtered.map { it.total }
                            )
                        }
                    }

                    Text(text = "Período", color = ColorExtra1)
                    Spacer(modifier = Modifier.height(10.dp))
                    MesesButton(fechas=fechas, selectedIndex = fechaInicioIndex, onDateSelected = { index, _ -> fechaInicioIndex = index }, label = "Inicio")
                    Spacer(modifier = Modifier.height(10.dp))
                    MesesButton(fechas=fechas, selectedIndex = fechaFinIndex, onDateSelected = { index, _ -> fechaFinIndex = index }, label = "Fin")
                    Spacer(modifier = Modifier.height(20.dp))
                    DetalleButton()
                    Spacer(modifier = Modifier.height(20.dp))
                    GraficoLineas(fechasFiltradas, totalesFiltrados)
                }
            }
        }
    }
}

@Composable
fun MesesButton(
    fechas: List<String>,
    selectedIndex: Int = 0,
    onDateSelected: (Int, String) -> Unit,
    label: String = "Mes"
) {
    val isDropDownExpanded = remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .clickable { isDropDownExpanded.value = true }
                .background(color = Color.White)
                .padding(5.dp)
        ) {
            Text(
                text = if (fechas.isNotEmpty()) fechas[selectedIndex] else label,
                color = ColorExtra2
            )
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
            fechas.forEachIndexed { index, dateStr ->
                DropdownMenuItem(
                    text = { Text(dateStr, color = ColorExtra2) },
                    onClick = {
                        isDropDownExpanded.value = false
                        onDateSelected(index, fechas[index])
                    }
                )
            }
        }
    }
}

@Composable
fun GraficoLineas(fechas: List<String>, montos: List<Int>) {
    AndroidView(
        factory = { context ->
            val linechart = LineChart(context)

            linechart.xAxis.apply {
                setDrawGridLines(false)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                axisLineColor = Color.White.toArgb()
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

            linechart.apply { description.isEnabled = false }
        },
        update = { lineChart ->
            val entries = mutableListOf<Entry>()

            montos.forEachIndexed { index, amount ->
                entries.add(Entry(index.toFloat(), amount.toFloat()))
            }

            val formatter = DateTimeFormatter.ofPattern("MM/yyyy")
            val fechasString = fechas.map { fecha ->
                fecha.format(formatter)
            }

            lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(fechasString)

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

            lineChart.data = LineData(dataset)

            lineChart.invalidate()
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
                    Text("Aproximación por curva estará disponible próximamente")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("Cerrar", color = ColorBoton)
                }
            }
        )
    }
}

