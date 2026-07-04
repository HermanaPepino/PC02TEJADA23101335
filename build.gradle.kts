plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // Necesario para que Android Studio lea google-services.json.
    id("com.google.gms.google-services") version "4.5.0" apply false
    alias(libs.plugins.kotlin.android) apply false
}