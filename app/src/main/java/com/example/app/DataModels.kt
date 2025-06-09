import kotlinx.serialization.Serializable

@Serializable
data class TransactionRecord(
    val id: Int,
    val monto: Int,
    val mensaje: String,
    val fecha: String, // Formato: "yyyy-MM-dd" o timestamp
    val total: Int
)

// Obtener las transacciones como map
fun TransactionRecord.toMap(): Map<String, Any> = mapOf(
    "monto" to monto,
    "mensaje" to mensaje,
    "fecha" to fecha,
    "total" to total
)

@Serializable
data class Account(
    val nombre: String,
    val contraseña: String,
    val transacciones: List<TransactionRecord> = emptyList()
)

@Serializable
data class AccountsData(
    val accounts: List<Account> = emptyList()
)





