package com.example.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.google.firebase.auth.FirebaseAuth

class AccCreate : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                PantallaCrearUsuario {
                    finish()
                }
            }
        }
    }
}

private lateinit var auth: FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearUsuario(onBack: () -> Unit) {
    val context = LocalContext.current
    var password by rememberSaveable { mutableStateOf("") }
    var password2 by rememberSaveable { mutableStateOf("") }
    var user by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    auth = FirebaseAuth.getInstance()

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
    ){
            innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Cree su cuenta", fontWeight = FontWeight.Medium, fontSize = 20.sp, color = ColorBoton)
            Spacer(modifier = Modifier.height(100.dp))
            Text(text = "Ingrese correo y confirme contraseña", fontWeight = FontWeight.Medium, fontSize = 15.sp, color = Color.White)
            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                value = user,
                onValueChange = { user = it },
                label = { Text("Correo", color = ColorBoton) },
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = ColorFondo, focusedLabelColor = ColorBoton)
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", color = ColorBoton) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = ColorFondo, focusedLabelColor = ColorBoton)
            )
            TextField(
                value = password2,
                onValueChange = { password2 = it },
                label = { Text("Repetir Contraseña", color = ColorBoton) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = ColorFondo, focusedLabelColor = ColorBoton)
            )
            error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(60.dp))
            Button(
                onClick = {
                    if(password == password2){ createAcc(context,user,password,auth) }
                    else{
                        error = "Las contraseñas deben ser iguales para continuar"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ColorBoton)
            ){
                Text("Crear")
            }
        }
    }
}

fun navigateToAccSelect(context: Context){
    val intent = Intent(context, AccSelect::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
    (context as Activity).finish()
}

fun createAcc(context: Context, user: String, pass: String, auth: FirebaseAuth){
    auth.createUserWithEmailAndPassword(user, pass)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                navigateToAccSelect(context)
            }
        }
}
