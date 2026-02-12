package deepankumarpn.github.io.eggchef.presentation.ui.screens.splash

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import deepankumarpn.github.io.eggchef.presentation.ui.screens.main.MainActivity

class SplashScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
