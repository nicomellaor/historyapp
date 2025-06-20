import android.content.Context
import com.example.app.AccountsPreferences
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthManager private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: AuthManager? = null

        fun getInstance(context: Context): AuthManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val auth = FirebaseAuth.getInstance()
    private val accountsPrefs = AccountsPreferences(context)

    init {
        // Listener para sincronizar estado de Firebase con DataStore
        auth.addAuthStateListener { firebaseAuth ->
            CoroutineScope(Dispatchers.IO).launch {
                val user = firebaseAuth.currentUser
                if (user != null) {
                    accountsPrefs.saveUserSession(user)
                } else {
                    accountsPrefs.clearUserSession()
                }
            }
        }
    }

    suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            if (result.user != null) {
                AuthResult.Success
            } else {
                AuthResult.Error("Error desconocido")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error al iniciar sesión")
        }
    }

    suspend fun signUp(email: String, password: String): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            if (result.user != null) {
                AuthResult.Success
            } else {
                AuthResult.Error("Error desconocido")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error al crear cuenta")
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun isUserLoggedIn() = accountsPrefs.isUserLoggedIn()
    fun getUserData() = accountsPrefs.getUserData()

    sealed class AuthResult {
        object Success : AuthResult()
        data class Error(val message: String) : AuthResult()
    }
}