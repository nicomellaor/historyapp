import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionRecord(
    val id: Int = 0,
    val monto: Int = 0,
    val mensaje: String = "",
    val fecha: String = "",
    val total: Int = 0
) : Parcelable

// Obtener las transacciones como map
fun TransactionRecord.toMap(): Map<String, Any> = mapOf(
    "id" to id,
    "monto" to monto,
    "mensaje" to mensaje,
    "fecha" to fecha,
    "total" to total
)

@Parcelize
data class Account(
    val id: String = "",
    val userId: String = "",
    val nombre: String = "",
    val contraseña: String = "",
    var transacciones: List<TransactionRecord> = emptyList()
) : Parcelable

@Parcelize
data class AccountsData(
    val accounts: List<Account> = emptyList()
) : Parcelable