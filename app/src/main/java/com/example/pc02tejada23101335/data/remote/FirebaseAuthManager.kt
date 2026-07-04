package com.example.pc02tejada23101335.data.remote

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * Clase encargada solo del login/logout con Firebase Authentication.
 */
object FirebaseAuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Devuelve el usuario actual.
     * Si es null, significa que no ha iniciado sesión.
     */
    fun currentUser() = auth.currentUser

    /**
     * Inicia sesión con correo y contraseña.
     */
    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cierra la sesión actual.
     */
    fun logout() {
        auth.signOut()
    }
}