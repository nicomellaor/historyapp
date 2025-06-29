package com.example.app

import Account
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.theme.ColorBoton
import com.example.app.ui.theme.ColorFondo
import com.google.firebase.firestore.FirebaseFirestore

class LogIn : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        val cuenta = intent.getParcelableExtra<Account>("cuenta")
        enableEdgeToEdge()
        setContent {
            AppTheme {
                if (cuenta != null) {
                    PantallaLogin(cuenta) {
                        finish()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLogin(cuenta: Account, onBack: () -> Unit) {
    val context = LocalContext.current
    var password by rememberSaveable { mutableStateOf("") }

    /* Instancia de DataStore
    val accountsPreferences = AccountsPreferences(context)
    val scope = rememberCoroutineScope()
    val accountPassword by accountsPreferences.getAccountPasswordFlow(cuenta).collectAsState(initial = null) */

    // Instancia de Firebase Firestore
    val accountPassword = cuenta.contraseña

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        containerColor = ColorFondo,
        // Logo de la App
        topBar = {
            TopAppBar(
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ColorFondo, // Azul
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = cuenta.nombre, fontWeight = FontWeight.Medium, fontSize = 20.sp, color = ColorBoton)
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Contraseña/PIN", color = Color.White)
            TextField(
                value = password,
                onValueChange = { password = it },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = ColorFondo,
                    focusedContainerColor = ColorFondo,
                    focusedIndicatorColor = ColorBoton,
                    focusedLabelColor = ColorBoton
                ),
            )
            Spacer(modifier = Modifier.height(60.dp))
            Button(
                onClick = {
                    if (password == accountPassword) {
                        navigateToHomePage(context, cuenta)
                        password = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ColorBoton)
            ){
                Text("Ingresar")
            }
            Spacer(modifier = Modifier.height(200.dp))
            PasswordButton(cuenta)
        }
    }
}

@Composable
fun PasswordButton(cuenta: Account) {
    var showDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }

    //val transacciones by accountsPreferences.getAccountTransactionsFlow(cuenta).collectAsState(initial = emptyList())
    val nombre = cuenta.nombre

    TextButton(onClick = { showDialog = true }) {
        Text(text = "Olvidé mi contraseña", color = Color.White)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Restablecer contraseña") },
            text = {
                Column {
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña/PIN") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = ColorBoton,
                            focusedLabelColor = ColorBoton
                        ),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Lógica para guardar
                        if (nombre.isNotBlank() && password.isNotBlank()) {
                            /*val cuentaObject = Account(nombre, password, transacciones)
                            scope.launch {
                                try {
                                    accountsPreferences.updateAccount(cuentaObject)
                                    password = ""
                                    showDialog = false
                                } catch (e: Exception) {
                                    println("Exception message: ${e.message}")
                                }
                            }*/
                            cambiarContrasena(
                                cuenta = cuenta,
                                password = password,
                                onSuccess = {
                                    password = ""
                                    showDialog = false
                                },
                                onError = { Log.e("Firestore", "Error al cambiar contraseña", it) }
                            )
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

fun cambiarContrasena(cuenta: Account, password: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("cuentas")
        .document(cuenta.id)
        .update("contraseña", password)
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener { exception ->
            onError(exception)
        }
}

fun navigateToHomePage(context: Context, cuenta: Account) {
    val intent = Intent(context, Home::class.java).apply {
        putExtra("cuenta", cuenta)
    }
    context.startActivity(intent)
}