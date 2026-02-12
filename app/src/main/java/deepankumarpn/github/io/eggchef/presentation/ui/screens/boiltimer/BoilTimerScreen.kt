package deepankumarpn.github.io.eggchef.presentation.ui.screens.boiltimer

import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import deepankumarpn.github.io.eggchef.BuildConfig
import deepankumarpn.github.io.eggchef.R
import deepankumarpn.github.io.eggchef.utils.Constants
import deepankumarpn.github.io.eggchef.domain.model.BoilItem
import deepankumarpn.github.io.eggchef.presentation.ui.theme.EggChefTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BoilTimerScreen(
    viewModel: BoilTimerViewModel = hiltViewModel(),
    onNavigateToIdentifier: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is BoilTimerContract.Effect.NavigateToIdentifier -> onNavigateToIdentifier()
            }
        }
    }

    BoilTimerScreenContent(
        state = state,
        onEvent = viewModel::setEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoilTimerScreenContent(
    state: BoilTimerContract.State,
    onEvent: (BoilTimerContract.Event) -> Unit
) {
    val yellowColor = colorResource(id = R.color.yellow)

    if (state.showPrivacyDialog) {
        PrivacyDialog(
            onDismiss = { onEvent(BoilTimerContract.Event.DismissPrivacyDialog) }
        )
    }
    if (state.showInstructionDialog) {
        InstructionDialog(
            onDismiss = { onEvent(BoilTimerContract.Event.DismissInstructionDialog) }
        )
    }
    if (state.showAddBoilDialog) {
        AddCustomBoilDialog(
            onDismiss = { onEvent(BoilTimerContract.Event.DismissAddBoilDialog) },
            onAdd = { title, minutes, description ->
                onEvent(BoilTimerContract.Event.AddCustomBoilType(title, minutes, description))
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            if (state.isSpinnerEnabled) {
                FloatingActionButton(
                    onClick = { onEvent(BoilTimerContract.Event.ShowAddBoilDialog) },
                    containerColor = yellowColor,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.content_desc_add_custom_boil)
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top row: Privacy | Info Message | Instructions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onEvent(BoilTimerContract.Event.ShowPrivacyDialog) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.lock_24px),
                        contentDescription = stringResource(R.string.privacy_policy)
                    )
                }
                Text(
                    text = state.infoMessage,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
                IconButton(
                    onClick = { onEvent(BoilTimerContract.Event.ShowInstructionDialog) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.menu_book_24px),
                        contentDescription = stringResource(R.string.click_to_view_instructions)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Timer display
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.showMinus) {
                    Text(
                        text = stringResource(R.string.minus),
                        fontSize = 40.sp
                    )
                }
                Text(
                    text = state.timerText,
                    fontSize = 40.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Boil type dropdown
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { if (state.isSpinnerEnabled) expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = state.boilTypes.getOrNull(state.selectedPosition)?.title ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.boiling_type)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    enabled = state.isSpinnerEnabled,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = yellowColor,
                        focusedLabelColor = yellowColor
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    state.boilTypes.forEachIndexed { index, item ->
                        val isCustom = item.isCustom
                        DropdownMenuItem(
                            text = { Text(item.title) },
                            onClick = {
                                onEvent(
                                    BoilTimerContract.Event.BoilTypeSelected(index)
                                )
                                expanded = false
                            },
                            trailingIcon = if (isCustom) {
                                {
                                    IconButton(
                                        onClick = {
                                            onEvent(
                                                BoilTimerContract.Event.DeleteCustomBoilType(index)
                                            )
                                            expanded = false
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = stringResource(R.string.content_desc_delete, item.title),
                                            tint = colorResource(id = R.color.yellow)
                                        )
                                    }
                                }
                            } else null
                        )
                    }
                }
            }

            // Description text
            Text(
                text = state.description,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // Buttons: Reset | Start | Pause
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { onEvent(BoilTimerContract.Event.ResetClicked) },
                    enabled = state.isResetEnabled,
                    modifier = Modifier.width(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = yellowColor,
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(R.string.reset))
                }
                Button(
                    onClick = { onEvent(BoilTimerContract.Event.StartClicked) },
                    enabled = state.isStartEnabled,
                    modifier = Modifier.width(110.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = yellowColor,
                        contentColor = Color.White
                    )
                ) {
                    Text(state.startButtonText)
                }
                Button(
                    onClick = { onEvent(BoilTimerContract.Event.PauseClicked) },
                    enabled = state.isPauseEnabled,
                    modifier = Modifier.width(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = yellowColor,
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(R.string.pause))
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

            if (BuildConfig.DEBUG) {
                IconButton(
                    onClick = { onEvent(BoilTimerContract.Event.NavigateToIdentifier) },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.data_info_alert_24),
                        contentDescription = stringResource(R.string.content_desc_identifier),
                        tint = yellowColor
                    )
                }
            }
        }
    }
}

@Composable
private fun AddCustomBoilDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, minutes: Int, description: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var minutesText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) }
    var minutesError by remember { mutableStateOf(false) }

    val yellowColor = colorResource(id = R.color.yellow)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.add_custom_boil_type),
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it.replaceFirstChar { char -> char.uppercase() }
                        titleError = false
                    },
                    label = { Text(stringResource(R.string.title_label)) },
                    placeholder = { Text(stringResource(R.string.title_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    isError = titleError,
                    supportingText = if (titleError) {
                        { Text(stringResource(R.string.title_required_error)) }
                    } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = yellowColor,
                        focusedLabelColor = yellowColor
                    )
                )

                OutlinedTextField(
                    value = minutesText,
                    onValueChange = {
                        minutesText = it.filter { char -> char.isDigit() }
                        minutesError = false
                    },
                    label = { Text(stringResource(R.string.minutes_label)) },
                    placeholder = { Text(stringResource(R.string.minutes_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = minutesError,
                    supportingText = if (minutesError) {
                        { Text(stringResource(R.string.minutes_valid_error)) }
                    } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = yellowColor,
                        focusedLabelColor = yellowColor
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description_label)) },
                    placeholder = { Text(stringResource(R.string.description_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = yellowColor,
                        focusedLabelColor = yellowColor
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val minutes = minutesText.toIntOrNull()
                    titleError = title.isBlank()
                    minutesError = minutes == null || minutes <= 0
                    if (!titleError && !minutesError) {
                        onAdd(title.trim(), minutes!!, description.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = yellowColor,
                    contentColor = Color.White
                )
            ) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun PrivacyDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.privacy_policy),
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(24.dp)
                )

                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(
                                    view: WebView,
                                    request: WebResourceRequest
                                ): Boolean {
                                    view.loadUrl(request.url.toString())
                                    return true
                                }
                            }
                            loadUrl(Constants.PRIVACY_POLICY_URL)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.close))
                    }
                }
            }
        }
    }
}

@Composable
private fun InstructionDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.instructions),
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontSize = 18.sp
            )
        },
        text = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InstructionItem(R.string.water_level)
                InstructionItem(R.string.boil_first)
                InstructionItem(R.string.lower_heat)
                InstructionItem(R.string.start_timer)
                InstructionItem(R.string.cool_10)
                InstructionItem(R.string.peel_from)
                InstructionItem(R.string.strong_hard)
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.close)) }
        }
    )
}

@Composable
private fun InstructionItem(resId: Int) {
    Text(
        text = stringResource(resId),
        fontStyle = FontStyle.Italic,
        fontSize = 14.sp,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun BoilTimerScreenPreview() {
    val sampleBoilItems = listOf(
        BoilItem("Soft-Boiled", 300, "Runny yolk, firm white."),
        BoilItem("Medium-Boiled", 420, "Jammy yolk, firm white."),
        BoilItem("Hard-Boiled", 600, "Firm yolk and white.")
    )
    val sampleState = BoilTimerContract.State(
        boilTypes = sampleBoilItems,
        selectedPosition = 1,
        description = sampleBoilItems[1].description,
        infoMessage = "Select boil type and start timer."
    )
    EggChefTheme {
        BoilTimerScreenContent(
            state = sampleState,
            onEvent = {}
        )
    }
}
