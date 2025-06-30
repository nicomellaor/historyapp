import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class AccountViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private var accountListener: ListenerRegistration? = null

    private val _cuenta = mutableStateOf<Account>(Account())
    val cuenta: MutableState<Account> = _cuenta

    private val _cuentas = mutableStateOf<List<Account>>(emptyList())
    val cuentas: State<List<Account>> = _cuentas

    private var allAccountsListener: ListenerRegistration? = null

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun observarCuenta(userId: String, nombre: String) {
        _isLoading.value = true
        _error.value = null

        // Limpiar listener anterior para evitar memory leaks
        accountListener?.remove()

        // Crear nuevo listener en tiempo real
        accountListener = db.collection("cuentas")
            .whereEqualTo("userId", userId)
            .whereEqualTo("nombre", nombre)
            .addSnapshotListener { snapshots, error ->
                _isLoading.value = false

                // Si hay error de conexión
                if (error != null) {
                    _error.value = "Error de conexión: ${error.message}"
                    return@addSnapshotListener
                }

                // Si encontramos documentos
                if (snapshots != null && !snapshots.isEmpty) {
                    val document = snapshots.documents.first()
                    try {
                        // Convertir documento a objeto Account
                        val cuentaObtenida = document.toObject(Account::class.java)
                        _cuenta.value = cuentaObtenida!!
                        _error.value = null
                    } catch (e: Exception) {
                        _error.value = "Error al procesar datos: ${e.message}"
                    }
                } else {
                    _error.value = "No se encontró la cuenta"
                }
            }
    }

    fun observarCuentasPorUsuario(userId: String) {
        _isLoading.value = true

        allAccountsListener?.remove()

        allAccountsListener = db.collection("cuentas")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshots, error ->
                _isLoading.value = false

                if (error != null) {
                    _error.value = "Error: ${error.message}"
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val cuentas = snapshots.documents.mapNotNull { document ->
                        document.toObject(Account::class.java)
                    }
                    _cuentas.value = cuentas
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        accountListener?.remove()
    }
}