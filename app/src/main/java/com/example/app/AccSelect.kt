package com.example.app

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

class AccSelect : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                PantallaUsuario()
            }
        }
    }
}

private lateinit var auth: FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaUsuario() {
    val context = LocalContext.current
    var password by rememberSaveable { mutableStateOf("") }
    var user by rememberSaveable { mutableStateOf("") }
    auth = FirebaseAuth.getInstance()

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        containerColor = ColorFondo,
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
            Text(text = "Bienvenido a HistoryApp", fontWeight = FontWeight.Medium, fontSize = 20.sp, color = ColorBoton)
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "Ingrese su Correo y Contraseña", fontWeight = FontWeight.Medium, fontSize = 15.sp, color = Color.White)
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
            Spacer(modifier = Modifier.height(60.dp))
            Button(
                onClick = { enterAcc(context,user,password,auth) },
                colors = ButtonDefaults.buttonColors(containerColor = ColorBoton)
            ){
                Text("Ingresar")
            }
            Spacer(modifier = Modifier.height(200.dp))
            TextButton(onClick={ navigateToCreate(context) }) {
                Text(text= "Crear Cuenta", color=Color.White)
            }
        }
    }
}

fun navigateToCreate(context: Context){
    val intent = Intent(context, AccCreate::class.java)
    context.startActivity(intent)
}

fun enterAcc(context: Context, user: String, pass: String, auth: FirebaseAuth){
    auth.signInWithEmailAndPassword(user, pass)
        .addOnCompleteListener{ result ->
            if (result.isSuccessful){
                navigatetoMain(context)
            }
        }
}
