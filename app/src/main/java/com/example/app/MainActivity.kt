package com.example.app

import Account
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.theme.ColorBoton
import com.example.app.ui.theme.ColorFondo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                PantallaInicio()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicio (){
    val context = LocalContext.current
    // Instancia de DataStore
    val accountsPreferences = AccountsPreferences(context)
    val scope = rememberCoroutineScope()

    // val nombres = listOf("Cuenta 1", "Cuenta 2", "Cuenta 3", "Cuenta 4", "Cuenta 5", "Cuenta 6")
    val nombres by accountsPreferences.accountNamesFlow.collectAsState(initial = emptyList())

    Scaffold(
        modifier = Modifier.fillMaxSize().background(ColorFondo),
        contentWindowInsets = WindowInsets(0, 0, 0, 0), // Por espacio en blanco debajo
        floatingActionButton = {
            AgregarButton(accountsPreferences, scope)
        },
        // Logo de la App
        topBar = {
            TopAppBar(
                navigationIcon = {Spacer(modifier = Modifier.size(48.dp))}, // Espacio de botón volver
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorFondo)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(ColorFondo),

            ){
            Column (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Text(text="Tus Cuentas", fontWeight = FontWeight.Medium, fontSize = 20.sp, color = ColorBoton)
                Spacer(modifier = Modifier.height(20.dp))
                nombres.forEach { cuenta ->
                    Button(
                        onClick = { navigateToLogin(context, cuenta) },
                        colors = ButtonDefaults.buttonColors(containerColor = ColorBoton)
                    ) {
                        Text(cuenta)
                    }
                }
            }
        }
    }
}

@Composable
fun AgregarButton(accountsPreferences: AccountsPreferences, scope: CoroutineScope) {
    var showDialog by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
            onDismissRequest = {
                showDialog = false
                nombre=""
                password=""},
            title = { Text("Agregar nueva cuenta") },
            text = {
                Column {
                    TextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre de cuenta") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña/PIN") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nombre.isNotBlank() && password.isNotBlank()) {
                            // Lógica para guardar
                            val cuenta = Account(nombre.trim(), password)
                            scope.launch {
                                try {
                                    accountsPreferences.addAccount(cuenta)
                                    nombre = ""
                                    password = ""
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
                    onClick = {
                        showDialog = false
                        nombre = ""
                        password = ""
                    }
                ) {
                    Text("Cancelar", color = ColorBoton)
                }
            }
        )
    }
}

fun navigateToLogin(context: Context, cuenta: String){
    val intent = Intent(context, LogIn::class.java).apply {
        putExtra("cuenta", cuenta)
    }
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {
        PantallaInicio()
    }
}
