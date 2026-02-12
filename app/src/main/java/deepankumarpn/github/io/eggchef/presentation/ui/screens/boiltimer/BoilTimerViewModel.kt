package deepankumarpn.github.io.eggchef.presentation.ui.screens.boiltimer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.CountDownTimer
import androidx.core.app.NotificationCompat
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import deepankumarpn.github.io.eggchef.R
import deepankumarpn.github.io.eggchef.base.BaseViewModel
import deepankumarpn.github.io.eggchef.data.local.CustomBoilLocalDataSource
import deepankumarpn.github.io.eggchef.domain.model.BoilItem
import deepankumarpn.github.io.eggchef.presentation.analytics.BoilTimerScreenAnalytics
import deepankumarpn.github.io.eggchef.presentation.ui.screens.main.MainActivity
import deepankumarpn.github.io.eggchef.utils.Constants
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoilTimerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val analytics: BoilTimerScreenAnalytics,
    private val customBoilLocalDataSource: CustomBoilLocalDataSource
) : BaseViewModel<BoilTimerContract.Event, BoilTimerContract.State, BoilTimerContract.Effect>() {

    private var countDownTimer: CountDownTimer? = null
    private var selectedTimeSeconds: Int = 0
    private var counterStarted = false
    private var counterStop = false
    private var counterResumed = false
    private var counterCompleted = false
    private var selectedListPos = 0
    private var mediaPlayer: MediaPlayer? = null
    private val boilItems = mutableListOf<BoilItem>()

    override fun createInitialState(): BoilTimerContract.State = BoilTimerContract.State()

    init {
        loadBoilData()
    }

    private fun loadBoilData() {
        boilItems.clear()
        boilItems.addAll(BoilItem.defaultBoilItems(context))
        // Load custom boil types from DataStore
        viewModelScope.launch {
            val customItems = customBoilLocalDataSource.getCustomBoilItems()
                .map { it.copy(isCustom = true) }
            boilItems.addAll(customItems)

            if (boilItems.isNotEmpty()) {
                val sortedList = boilItems.toList().sortedBy { it.seconds }
                val firstItem = sortedList[0]
                selectedListPos = boilItems.indexOf(firstItem)
                selectedTimeSeconds = firstItem.seconds
                setState {
                    copy(
                        boilTypes = sortedList,
                        description = firstItem.description,
                        selectedPosition = 0
                    )
                }
            }
        }
    }

    override fun handleEvent(event: BoilTimerContract.Event) {
        when (event) {
            is BoilTimerContract.Event.StartClicked -> onStartClicked()
            is BoilTimerContract.Event.PauseClicked -> onPauseClicked()
            is BoilTimerContract.Event.ResetClicked -> onResetClicked()
            is BoilTimerContract.Event.BoilTypeSelected -> onBoilTypeSelected(event.position)
            is BoilTimerContract.Event.ShowPrivacyDialog -> {
                setState { copy(showPrivacyDialog = true) }
                analytics.privacyPolicyClickedTag()
            }
            is BoilTimerContract.Event.DismissPrivacyDialog -> setState { copy(showPrivacyDialog = false) }
            is BoilTimerContract.Event.ShowInstructionDialog -> {
                setState { copy(showInstructionDialog = true) }
                analytics.instructionClickedTag()
            }
            is BoilTimerContract.Event.DismissInstructionDialog -> setState { copy(showInstructionDialog = false) }
            is BoilTimerContract.Event.NavigateToIdentifier -> {
                setEffect { BoilTimerContract.Effect.NavigateToIdentifier }
            }
            is BoilTimerContract.Event.ShowAddBoilDialog -> {
                setState { copy(showAddBoilDialog = true) }
            }
            is BoilTimerContract.Event.DismissAddBoilDialog -> {
                setState { copy(showAddBoilDialog = false) }
            }
            is BoilTimerContract.Event.AddCustomBoilType -> {
                onAddCustomBoilType(event.title, event.minutes, event.description)
            }
            is BoilTimerContract.Event.DeleteCustomBoilType -> {
                onDeleteCustomBoilType(event.position)
            }
        }
    }

    private fun onAddCustomBoilType(title: String, minutes: Int, description: String) {
        val seconds = minutes * 60
        val displayTitle = context.getString(R.string.custom_boil_title_format, title, minutes)
        val newItem = BoilItem(
            title = displayTitle,
            seconds = seconds,
            description = description,
            isCustom = true
        )
        viewModelScope.launch {
            customBoilLocalDataSource.saveCustomBoilItem(newItem)
        }
        boilItems.add(newItem)
        selectedListPos = boilItems.size - 1
        selectedTimeSeconds = newItem.seconds
        val sortedList = boilItems.toList().sortedBy { it.seconds }
        val newDisplayPosition = sortedList.indexOf(newItem).coerceAtLeast(0)
        setState {
            copy(
                showAddBoilDialog = false,
                boilTypes = sortedList,
                selectedPosition = newDisplayPosition,
                description = newItem.description
            )
        }
        analytics.addCustomBoilTypeTag(title, minutes, description)
    }

    private fun onDeleteCustomBoilType(position: Int) {
        val displayList = uiState.value.boilTypes
        if (position !in displayList.indices) return
        val itemToDelete = displayList[position]
        if (!itemToDelete.isCustom) return
        val actualIndex = boilItems.indexOf(itemToDelete)
        if (actualIndex !in boilItems.indices) return
        val customIndex = boilItems.take(actualIndex + 1).count { it.isCustom } - 1
        viewModelScope.launch {
            customBoilLocalDataSource.deleteCustomBoilItem(customIndex)
        }
        boilItems.removeAt(actualIndex)
        // If deleted item was selected, fall back to first item
        if (selectedListPos == actualIndex) {
            selectedListPos = 0
        } else if (selectedListPos > actualIndex) {
            selectedListPos -= 1
        }
        if (selectedListPos >= boilItems.size) selectedListPos = 0
        val currentItem = boilItems.getOrNull(selectedListPos)
        if (currentItem != null) {
            selectedTimeSeconds = currentItem.seconds
        }
        val newDisplayList = boilItems.toList().sortedBy { it.seconds }
        val newDisplayPosition = if (currentItem != null) {
            newDisplayList.indexOf(currentItem).coerceAtLeast(0)
        } else 0
        setState {
            copy(
                boilTypes = newDisplayList,
                selectedPosition = newDisplayPosition,
                description = currentItem?.description ?: ""
            )
        }
    }

    private fun onStartClicked() {
        if (!counterStarted) {
            counterStarted = true
            setState {
                copy(
                    isPauseEnabled = true,
                    isResetEnabled = true,
                    isStartEnabled = false,
                    isSpinnerEnabled = false,
                    description = boilItems[selectedListPos].description
                )
            }
            startCountdown()
            analytics.startTimerTag(selectedTimeSeconds, boilItems[selectedListPos].title)
        } else if (counterStop) {
            resetTimer()
            stopAudio()
        } else if (counterStarted && counterResumed) {
            counterResumed = false
            if (!counterCompleted) startCountdown() else startIndefiniteCountdown()
            setState {
                copy(
                    isPauseEnabled = true,
                    isStartEnabled = false,
                    isSpinnerEnabled = false,
                    startButtonText = context.getString(R.string.start)
                )
            }
            analytics.resumeTimerTag(selectedTimeSeconds, boilItems[selectedListPos].title)
        }
    }

    private fun onPauseClicked() {
        if (!counterResumed && counterStarted) {
            countDownTimer?.cancel()
            counterResumed = true
            setState {
                copy(
                    isPauseEnabled = false,
                    isStartEnabled = true,
                    isSpinnerEnabled = false,
                    startButtonText = context.getString(R.string.resume)
                )
            }
            analytics.pauseTimerTag(selectedTimeSeconds, boilItems[selectedListPos].title)
        }
    }

    private fun onResetClicked() {
        resetTimer()
    }

    private fun onBoilTypeSelected(position: Int) {
        val displayList = uiState.value.boilTypes
        if (position in displayList.indices) {
            val selectedItem = displayList[position]
            selectedListPos = boilItems.indexOf(selectedItem).coerceAtLeast(0)
            selectedTimeSeconds = selectedItem.seconds
            setState {
                copy(
                    selectedPosition = position,
                    description = selectedItem.description
                )
            }
            analytics.boilTypeSelectedTag(selectedItem.title)
        }
    }

    private fun startCountdown() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer((selectedTimeSeconds * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val sec = millisUntilFinished / 1000 % 60
                setState {
                    copy(timerText = String.format("%02d:%02d", minutes, sec))
                }
                selectedTimeSeconds--
            }

            override fun onFinish() {
                counterStop = true
                counterCompleted = true
                selectedTimeSeconds = 1
                playAudio()
                setState {
                    copy(
                        showMinus = true,
                        description = context.getString(R.string.boiling_is_completed),
                        isResetEnabled = false,
                        isStartEnabled = true,
                        startButtonText = context.getString(R.string.stop),
                        isPauseEnabled = false,
                        timerText = context.getString(R.string.time_00_00)
                    )
                }
                startIndefiniteCountdown()
            }
        }
        countDownTimer?.start()
    }

    private fun startIndefiniteCountdown() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val sec = selectedTimeSeconds % 60
                val minutes = selectedTimeSeconds / 60
                val timerValue = String.format("%02d:%02d", minutes, sec)
                setState {
                    copy(timerText = timerValue)
                }
                showNotification(timerValue)
                selectedTimeSeconds++
            }

            override fun onFinish() {}
        }
        countDownTimer?.start()
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        if (boilItems.isNotEmpty()) {
            selectedTimeSeconds = boilItems[selectedListPos].seconds
        }
        counterCompleted = false
        counterStarted = false
        counterStop = false
        counterResumed = false
        stopAudio()
        deleteNotification()
        setState {
            copy(
                isStartEnabled = true,
                isPauseEnabled = false,
                isResetEnabled = false,
                showMinus = false,
                timerText = context.getString(R.string.time_00_00),
                startButtonText = context.getString(R.string.start),
                isSpinnerEnabled = true,
                description = if (boilItems.isNotEmpty()) boilItems[selectedListPos].description else ""
            )
        }
        analytics.resetTimerTag(if (boilItems.isNotEmpty()) boilItems[selectedListPos].title else "")
    }

    private fun playAudio() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, R.raw.audio_boiling_completed)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
        analytics.audioPlayedTag()
    }

    private fun stopAudio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun showNotification(timerValue: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID,
                Constants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val itemTitle = boilItems[selectedListPos].title
        val contentText = if (counterStop) {
            context.getString(R.string.overcooking_detected, itemTitle, timerValue)
        } else {
            itemTitle
        }

        val builder = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.notify_egg_icon)
            .setContentTitle(context.getString(R.string.boiling_is_completed))
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSilent(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))

        notificationManager.notify(Constants.NOTIFICATION_ID, builder.build())

        analytics.notificationPlayedTag(context.getString(R.string.boiling_is_completed), itemTitle)
    }

    private fun deleteNotification() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(Constants.CHANNEL_ID)
        }
    }

    override fun onCleared() {
        countDownTimer?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onCleared()
    }
}
