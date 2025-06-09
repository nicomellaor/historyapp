package com.example.app

import Account
import AccountsData
import TransactionRecord
import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "accounts_prefs")

class AccountsPreferences(private val context: Context) {
    companion object {
        private val ACCOUNTS_KEY = stringPreferencesKey("accounts_data")
        private val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true }
    }
    // Obtener todas las cuentas
    val accountsFlow: Flow<List<Account>> = context.dataStore.data.map { preferences ->
        val jsonString = preferences[ACCOUNTS_KEY] ?: ""
        if (jsonString.isEmpty()) {
            emptyList()
        } else {
            try {
                val accountsData = json.decodeFromString<AccountsData>(jsonString)
                accountsData.accounts
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    // Obtener lista de nombres de cuentas
    val accountNamesFlow: Flow<List<String>> = accountsFlow.map { accounts ->
        accounts.map { it.nombre }
    }

    // Obtener contraseña de cuenta específica
    fun getAccountPasswordFlow(accountName: String): Flow<String?> = accountsFlow.map { accounts ->
        accounts.find { it.nombre == accountName }?.contraseña
    }

    // Obtener todas las transacciones de una cuenta
    fun getAccountTransactionsFlow(accountName: String): Flow<List<TransactionRecord>> =
        accountsFlow.map { accounts ->
            accounts.find { it.nombre == accountName }?.transacciones ?: emptyList()
        }

    // Obtener transacción específica
    fun getTransactionFlow(accountName: String, transactionId: Int): Flow<TransactionRecord?> =
        accountsFlow.map { accounts ->
            accounts.find { it.nombre == accountName }?.transacciones?.find { it.id == transactionId }
        }

    // Generar Id para Transacción
    suspend fun generateId(accountName: String): Int {
        val transacciones = getAccountTransactionsFlow(accountName)
            .first()               // Obtiene el primer valor emitido
        return transacciones.size + 1
    }

    // Agregar nueva cuenta
    suspend fun addAccount(account: Account) {
        context.dataStore.edit { preferences ->
            val currentData = getCurrentAccountsData(preferences)
            val updatedAccounts = currentData.accounts + account
            val updatedData = currentData.copy(accounts = updatedAccounts)
            preferences[ACCOUNTS_KEY] = json.encodeToString(updatedData)
        }
    }

    // Actualizar cuenta
    suspend fun updateAccount(updatedAccount: Account) {
        context.dataStore.edit { preferences ->
            val currentData = getCurrentAccountsData(preferences)
            val updatedAccounts = currentData.accounts.map { account ->
                if (account.nombre == updatedAccount.nombre) updatedAccount else account
            }
            val updatedData = currentData.copy(accounts = updatedAccounts)
            preferences[ACCOUNTS_KEY] = json.encodeToString(updatedData)
        }
    }

    // Eliminar cuenta
    suspend fun deleteAccount(accountName: String) {
        context.dataStore.edit { preferences ->
            val currentData = getCurrentAccountsData(preferences)
            val updatedAccounts = currentData.accounts.filter { it.nombre != accountName }
            val updatedData = currentData.copy(accounts = updatedAccounts)
            preferences[ACCOUNTS_KEY] = json.encodeToString(updatedData)
        }
    }

    // Agregar transacción a cuenta específica
    suspend fun addTransaction(accountName: String, transaction: TransactionRecord) {
        context.dataStore.edit { preferences ->
            val currentData = getCurrentAccountsData(preferences)
            val updatedAccounts = currentData.accounts.map { account ->
                if (account.nombre == accountName) {
                    account.copy(transacciones = account.transacciones + transaction)
                } else {
                    account
                }
            }
            val updatedData = currentData.copy(accounts = updatedAccounts)
            preferences[ACCOUNTS_KEY] = json.encodeToString(updatedData)
        }
    }

    // Actualizar transacción
    suspend fun updateTransaction(accountName: String, updatedTransaction: TransactionRecord) {
        context.dataStore.edit { preferences ->
            val currentData = getCurrentAccountsData(preferences)
            val updatedAccounts = currentData.accounts.map { account ->
                if (account.nombre == accountName) {
                    val updatedTransactions = account.transacciones.map { transaction ->
                        if (transaction.id == updatedTransaction.id) {
                            updatedTransaction
                        } else {
                            transaction
                        }
                    }
                    account.copy(transacciones = updatedTransactions)
                } else {
                    account
                }
            }
            val updatedData = currentData.copy(accounts = updatedAccounts)
            preferences[ACCOUNTS_KEY] = json.encodeToString(updatedData)
        }
    }

    // Eliminar transacción específica
    suspend fun deleteTransaction(accountName: String, transactionId: Int) {
        context.dataStore.edit { preferences ->
            val currentData = getCurrentAccountsData(preferences)
            val updatedAccounts = currentData.accounts.map { account ->
                if (account.nombre == accountName) {
                    val filteredTransactions = account.transacciones.filter {
                        it.id != transactionId
                    }
                    account.copy(transacciones = filteredTransactions)
                } else {
                    account
                }
            }
            val updatedData = currentData.copy(accounts = updatedAccounts)
            preferences[ACCOUNTS_KEY] = json.encodeToString(updatedData)
        }
    }

    // Limpiar todos los datos
    suspend fun clearAllData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private fun getCurrentAccountsData(preferences: Preferences): AccountsData {
        val jsonString = preferences[ACCOUNTS_KEY] ?: ""
        return if (jsonString.isEmpty()) {
            AccountsData()
        } else {
            try {
                json.decodeFromString<AccountsData>(jsonString)
            } catch (e: Exception) {
                AccountsData()
            }
        }
    }
}