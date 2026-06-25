package danilovl.gallery.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat
import danilovl.gallery.GalleryViewModel
import danilovl.gallery.R
import danilovl.gallery.ui.icons.GalleryIcons
import danilovl.gallery.ui.screens.DayScreen
import danilovl.gallery.ui.screens.OnThisDayScreen
import danilovl.gallery.ui.screens.MonthScreen
import danilovl.gallery.ui.screens.TimelineScreen
import danilovl.gallery.util.LocaleHelper
import java.util.Locale

private enum class Tab(@get:StringRes val titleRes: Int, val icon: ImageVector) {
    LANGUAGE(R.string.tab_language, GalleryIcons.Language),
    ON_THIS_DAY(R.string.tab_on_this_day, GalleryIcons.Calendar),
    DAY(R.string.tab_day, GalleryIcons.Day),
    MONTH(R.string.tab_month, GalleryIcons.Month),
    TIMELINE(R.string.tab_timeline, GalleryIcons.Timeline)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryApp(
    viewModel: GalleryViewModel,
    onLanguageSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    val pagerState = rememberPagerState(initialPage = Tab.ON_THIS_DAY.ordinal) { Tab.entries.size }
    val scope = rememberCoroutineScope()

    LaunchedEffect(hasPermission) {
        if (hasPermission) viewModel.loadIfNeeded()
    }

    val photos by viewModel.photos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
        ) {
            if (!hasPermission) {
                PermissionScreen { launcher.launch(permission) }
                return@Box
            }

            val stateHolder = rememberSaveableStateHolder()
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 1
            ) { page ->
                val tab = Tab.entries[page]
                stateHolder.SaveableStateProvider(tab) {
                    when (tab) {
                        Tab.LANGUAGE -> LanguageScreen(onLanguageSelected)
                        Tab.ON_THIS_DAY -> OnThisDayScreen(photos)
                        Tab.DAY -> DayScreen(photos)
                        Tab.MONTH -> MonthScreen(photos)
                        Tab.TIMELINE -> TimelineScreen(photos)
                    }
                }
            }

            if (isLoading && photos.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            PagerIndicator(
                count = Tab.entries.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun PagerIndicator(
    count: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), CircleShape)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count) { iteration ->
            val color = if (currentPage == iteration) {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            }
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color, CircleShape)
            )
        }
    }
}

@Composable
private fun LanguageScreen(onLanguageSelected: (String) -> Unit) {
    val current = LocaleHelper.savedLanguage(LocalContext.current)
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(LocaleHelper.languages) { language ->
            val isSelected = language.code == current || (current.isEmpty() && language.code == "en")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLanguageSelected(language.code) }
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = language.label,
                    style = MaterialTheme.typography.bodyMedium
                )
                RadioButton(
                    selected = isSelected,
                    onClick = { onLanguageSelected(language.code) },
                    modifier = Modifier.size(36.dp),
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.onSurface,
                        unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
            }
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
        }
    }
}

@Composable
private fun PermissionScreen(onRequest: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.permission_rationale),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRequest) { Text(stringResource(R.string.permission_grant)) }
    }
}
