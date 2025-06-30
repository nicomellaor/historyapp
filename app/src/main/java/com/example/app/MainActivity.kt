package com.example.app

import Account
import AccountViewModel
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        // Verificar si el usuario ya está autenticado
        if (auth.currentUser == null) {
            // No hay usuario logueado, ir a AccSelect
            val intent = Intent(this, AccSelect::class.java)
            startActivity(intent)
            finish() // Cerrar MainActivity
            return
        }
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val viewModel = remember { AccountViewModel() }
                PantallaInicio(auth, viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicio (auth: FirebaseAuth, viewModel: AccountViewModel){
    val context = LocalContext.current

    // Conexión con Firebase Firestore
    val cuentas by viewModel.cuentas
    val userId = getCurrentUserUID()

    LaunchedEffect(Unit) {
        if (userId != null) {
            viewModel.observarCuentasPorUsuario(userId)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ColorFondo,
        floatingActionButton = {
            Column (verticalArrangement = Arrangement.spacedBy(16.dp)) {
                AgregarButton(cuentas)
                CerrarSesionButton(context, auth)
            }
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
                cuentas.forEach { cuenta ->
                    Button(
                        onClick = { navigateToLogin(context, cuenta.nombre) },
                        colors = ButtonDefaults.buttonColors(containerColor = ColorBoton)
                    ) {
                        Text(cuenta.nombre, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CerrarSesionButton(context: Context, auth: FirebaseAuth){
    Button(
        onClick = {
            auth.signOut()
            navigateToAccSelect(context)
        },
        modifier = Modifier.size(48.dp),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ColorBoton)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = "Cerrar sesión"
        )
    }
}

@Composable
fun AgregarButton(cuentas: List<Account>) {
    var showDialog by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val id = generarIdCuenta(cuentas)
    val userId = getCurrentUserUID()

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
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = ColorBoton,
                            focusedLabelColor = ColorBoton
                        ),
                    )
                    Spacer(Modifier.height(8.dp))
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
                        if (nombre.isNotBlank() && password.isNotBlank()) {
                            // Lógica para guardar con Firebase FireStore
                            val cuenta = Account(id, userId!!, nombre.trim(), password)
                            agregarCuenta(
                                cuenta = cuenta,
                                onSuccess = {
                                    nombre = ""
                                    password = ""
                                    showDialog = false
                                },
                                onError = { Log.e("Firestore", "Error al crear cuenta", it) }
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

fun agregarCuenta(cuenta: Account, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("cuentas")
        .document(cuenta.id)
        .set(cuenta)
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener { exception ->
            onError(exception)
        }
}

fun generarIdCuenta(cuentas: List<Account>): String {
    val idsExistentes = cuentas.map { it.id }
    var numero = 1
    var nuevoId: String

    do {
        nuevoId = "cuenta$numero"
        numero++
    } while (nuevoId in idsExistentes)

    return nuevoId
}

fun navigateToLogin(context: Context, cuenta: String){
    val intent = Intent(context, LogIn::class.java).apply {
        putExtra("cuenta", cuenta)
    }
    context.startActivity(intent)
}

fun getCurrentUserUID(): String? {
    val auth = FirebaseAuth.getInstance()
    return auth.currentUser?.uid
}