package com.example.pc02tejada23101335.presentation.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.pc02tejada23101335.data.remote.FirebaseAuthManager
import com.example.pc02tejada23101335.presentation.auth.LoginScreen
import com.example.pc02tejada23101335.presentation.converter.CurrencyHomeScreen

/**
 * CurrencyApp es el contenedor principal de la aplicación.
 *
 * Aquí decidimos qué pantalla mostrar:
 *
 * - Si el usuario NO inició sesión, mostramos LoginScreen.
 * - Si el usuario SÍ inició sesión, mostramos CurrencyHomeScreen.
 *
 * Así evitamos meter esta lógica dentro de MainActivity.
 */
@Composable
fun CurrencyApp() {
    var isLoggedIn by remember {
        mutableStateOf(FirebaseAuthManager.currentUser() != null)
    }

    if (isLoggedIn) {
        CurrencyHomeScreen(
            onLogout = {
                FirebaseAuthManager.logout()
                isLoggedIn = false
            }
        )
    } else {
        LoginScreen(
            onLoginSuccess = {
                isLoggedIn = true
            }
        )
    }
}