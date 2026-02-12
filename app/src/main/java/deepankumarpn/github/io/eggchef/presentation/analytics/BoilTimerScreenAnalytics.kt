package deepankumarpn.github.io.eggchef.presentation.analytics

import android.os.Bundle
import deepankumarpn.github.io.eggchef.domain.repository.AnalyticsRepository
import deepankumarpn.github.io.eggchef.utils.AnalyticsKeys
import javax.inject.Inject

class BoilTimerScreenAnalytics @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {

    fun privacyPolicyClickedTag() {
        analyticsRepository.sendEvent(AnalyticsKeys.EVENT_PRIVACY_POLICY, Bundle().apply {
            putLong(AnalyticsKeys.PARAM_PRIVACY_CLICKED_TIMESTAMP, System.currentTimeMillis())
        })
    }

    fun instructionClickedTag() {
        analyticsRepository.sendEvent(AnalyticsKeys.EVENT_INSTRUCTION, Bundle().apply {
            putLong(AnalyticsKeys.PARAM_INSTRUCTION_CLICKED_TIMESTAMP, System.currentTimeMillis())
        })
    }

    fun startTimerTag(timerCount: Int, boilType: String) {
        analyticsRepository.sendEvent(AnalyticsKeys.EVENT_START_BTN, Bundle().apply {
            putLong(AnalyticsKeys.PARAM_START_TIMER_TIMESTAMP, System.currentTimeMillis())
            putInt(AnalyticsKeys.PARAM_START_TIMER_COUNT, timerCount)
            putString(AnalyticsKeys.PARAM_START_BOIL_TYPE, boilType)
        })
    }

    fun resumeTimerTag(timerCount: Int, boilType: String) {
        analyticsRepository.sendEvent(AnalyticsKeys.EVENT_RESUME_BTN, Bundle().apply {
            putLong(AnalyticsKeys.PARAM_RESUME_TIMER_TIMESTAMP, System.currentTimeMillis())
            putInt(AnalyticsKeys.PARAM_RESUME_TIMER_COUNT, timerCount)
            putString(AnalyticsKeys.PARAM_RESUME_BOIL_TYPE, boilType)
        })
    }

    fun pauseTimerTag(timerCount: Int, boilType: String) {
        analyticsRepository.sendEvent(AnalyticsKeys.EVENT_PAUSE_BTN, Bundle().apply {
            putLong(AnalyticsKeys.PARAM_PAUSE_TIMER_TIMESTAMP, System.currentTimeMillis())
            putInt(AnalyticsKeys.PARAM_PAUSE_TIMER_COUNT, timerCount)
            putString(AnalyticsKeys.PARAM_PAUSE_BOIL_TYPE, boilType)
        })
    }

    fun resetTimerTag(boilType: String) {
        analyticsRepository.sendEvent(AnalyticsKeys.EVENT_RESET_BTN, Bundle().apply {
            putLong(AnalyticsKeys.PARAM_RESET_TIMER_TIMESTAMP, System.currentTimeMillis())
            putInt(AnalyticsKeys.PARAM_RESET_TIMER_COUNT, 0)
            putString(AnalyticsKeys.PARAM_RESET_BOIL_TYPE, boilType)
        })
    }

    fun boilTypeSelectedTag(boilType: String) {
        analyticsRepository.sendEvent(AnalyticsKeys.EVENT_BOIL_TYPE, Bundle().apply {
            putLong(AnalyticsKeys.PARAM_BOIL_TYPE_CLICKED_TIMESTAMP, System.currentTimeMillis())
            putString(AnalyticsKeys.PARAM_BOIL_TYPE, boilType)
        })
    }

    fun addCustomBoilTypeTag(title: String, minutes: Int, description: String) {
        analyticsRepository.sendEvent(AnalyticsKeys.EVENT_ADD_CUSTOM_BOIL_TYPE, Bundle().apply {
            putString(AnalyticsKeys.PARAM_CUSTOM_BOIL_TITLE, title)
            putInt(AnalyticsKeys.PARAM_CUSTOM_BOIL_MINUTES, minutes)
            putString(AnalyticsKeys.PARAM_CUSTOM_BOIL_DESCRIPTION, description)
        })
    }

    fun audioPlayedTag() {
        analyticsRepository.sendEvent(AnalyticsKeys.EVENT_AUDIO_PLAYED, Bundle().apply {
            putLong(AnalyticsKeys.PARAM_AUDIO_PLAYED_TIMESTAMP, System.currentTimeMillis())
        })
    }

    fun notificationPlayedTag(notificationTitle: String, boilType: String) {
        analyticsRepository.sendEvent(AnalyticsKeys.EVENT_NOTIFICATION_PLAYED, Bundle().apply {
            putLong(AnalyticsKeys.PARAM_NOTIFICATION_COMPLETED_TIMESTAMP, System.currentTimeMillis())
            putString(AnalyticsKeys.PARAM_NOTIFICATION_TITLE, notificationTitle)
            putString(AnalyticsKeys.PARAM_NOTIFICATION_DESC, boilType)
        })
    }
}
