package danilovl.gallery.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
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
import danilovl.gallery.ui.screens.MonthScreen
import danilovl.gallery.ui.screens.TimelineScreen
import danilovl.gallery.util.LocaleHelper
import java.util.Locale

private enum class Tab(@get:StringRes val titleRes: Int, val icon: ImageVector) {
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

    val pagerState = rememberPagerState { Tab.entries.size }
    val scope = rememberCoroutineScope()

    LaunchedEffect(hasPermission) {
        if (hasPermission) viewModel.loadIfNeeded()
    }

    val photos by viewModel.photos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = { LanguageMenu(onLanguageSelected) }
            )
        },
        bottomBar = {
            if (hasPermission) {
                NavigationBar {
                    Tab.entries.forEach { tab ->
                        val title = stringResource(tab.titleRes)
                        val selected = pagerState.currentPage == tab.ordinal
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(tab.ordinal)
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = title) },
                            label = { Text(title) }
                        )
                    }
                }
            }
        }
    ) { padding ->
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
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val tab = Tab.entries[page]
                stateHolder.SaveableStateProvider(tab) {
                    when (tab) {
                        Tab.DAY -> DayScreen(photos)
                        Tab.MONTH -> MonthScreen(photos)
                        Tab.TIMELINE -> TimelineScreen(photos)
                    }
                }
            }

            if (isLoading && photos.isEmpty()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun LanguageMenu(onLanguageSelected: (String) -> Unit) {
    val expanded = remember { mutableStateOf(false) }
    val current = Locale.getDefault().language.uppercase()

    Box(modifier = Modifier.padding(end = 24.dp)) {
        OutlinedButton(
            onClick = { expanded.value = true },
            modifier = Modifier.height(32.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
        ) {
            Text(
                text = current,
                style = MaterialTheme.typography.labelMedium
            )
            Icon(
                imageVector = GalleryIcons.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .padding(start = 2.dp)
            )
        }
        DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
            LocaleHelper.languages.forEach { language ->
                DropdownMenuItem(
                    text = { Text(language.label) },
                    onClick = {
                        expanded.value = false
                        onLanguageSelected(language.code)
                    }
                )
            }
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
