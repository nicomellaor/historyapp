import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Serializable
data class TransactionRecord @RequiresApi(Build.VERSION_CODES.O) constructor(
    val id: Int,
    val monto: Int,
    val mensaje: String,
    @Serializable(with = LocalDateSerializer::class)
    val fecha: LocalDate,
    val total: Int
)

// Obtener las transacciones como map
fun TransactionRecord.toMap(): Map<String, Any> = mapOf(
    "id" to id,
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

@RequiresApi(Build.VERSION_CODES.O)
object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), formatter)
    }
}



