package danilovl.gallery

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import danilovl.gallery.ui.GalleryApp
import danilovl.gallery.ui.theme.GalleryTheme
import danilovl.gallery.util.LocaleHelper

class MainActivity : ComponentActivity() {

    private val viewModel: GalleryViewModel by viewModels()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GalleryTheme {
                GalleryApp(
                    viewModel = viewModel,
                    onLanguageSelected = { code ->
                        LocaleHelper.saveLanguage(this, code)
                        recreate()
                    }
                )
            }
        }
    }
}
