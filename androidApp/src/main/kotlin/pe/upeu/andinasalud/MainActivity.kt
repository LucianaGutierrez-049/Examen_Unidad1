package pe.upeu.andinasalud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.koin.core.context.GlobalContext
import pe.upeu.andinasalud.di.iniciarKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        if (GlobalContext.getOrNull() == null) iniciarKoin()
        setContent { App() }
    }
}

@Preview
@Composable
fun AppAndroidPreview() { App() }
