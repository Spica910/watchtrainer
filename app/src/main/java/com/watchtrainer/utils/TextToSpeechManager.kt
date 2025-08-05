package com.watchtrainer.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class TextToSpeechManager(private val context: Context) {
    
    companion object {
        private const val TAG = "TextToSpeechManager"
        private const val SPEECH_RATE = 1.0f
        private const val PITCH = 1.0f
    }
    
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()
    
    init {
        initializeTTS()
    }
    
    private fun initializeTTS() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.KOREAN)
                isInitialized = result != TextToSpeech.LANG_MISSING_DATA && 
                               result != TextToSpeech.LANG_NOT_SUPPORTED
                
                if (isInitialized) {
                    tts?.setSpeechRate(SPEECH_RATE)
                    tts?.setPitch(PITCH)
                    setupProgressListener()
                    Log.d(TAG, "TTS initialized successfully")
                } else {
                    Log.e(TAG, "Korean language not supported")
                }
            } else {
                Log.e(TAG, "TTS initialization failed")
            }
        }
    }
    
    private fun setupProgressListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
            }
            
            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
            }
            
            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
                Log.e(TAG, "TTS error for utterance: $utteranceId")
            }
        })
    }
    
    fun speak(text: String, urgent: Boolean = false) {
        if (!isInitialized) {
            Log.w(TAG, "TTS not initialized")
            return
        }
        
        val queueMode = if (urgent) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        val params = HashMap<String, String>()
        params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = System.currentTimeMillis().toString()
        
        tts?.speak(text, queueMode, params)
    }
    
    fun speakWorkoutUpdate(steps: Int, heartRate: Int, calories: Int) {
        val message = buildString {
            append("현재 ")
            if (steps > 0) append("${steps}걸음, ")
            if (heartRate > 0) append("심박수 ${heartRate}, ")
            if (calories > 0) append("${calories}칼로리 소모")
        }
        speak(message)
    }
    
    fun speakMotivation(message: String) {
        speak(message, urgent = true)
    }
    
    fun speakWorkoutStart() {
        speak("운동을 시작합니다. 화이팅!", urgent = true)
    }
    
    fun speakWorkoutPause() {
        speak("운동을 일시정지했습니다.", urgent = true)
    }
    
    fun speakWorkoutResume() {
        speak("운동을 다시 시작합니다.", urgent = true)
    }
    
    fun speakWorkoutEnd(duration: String, steps: Int, calories: Int) {
        val message = buildString {
            append("운동을 종료했습니다. ")
            append("${duration} 동안 ")
            if (steps > 0) append("${steps}걸음, ")
            append("${calories}칼로리를 소모했습니다. ")
            append("수고하셨습니다!")
        }
        speak(message, urgent = true)
    }
    
    fun speakGoalAchieved(goalType: String) {
        speak("축하합니다! $goalType 목표를 달성했습니다!", urgent = true)
    }
    
    fun speakWeatherWarning(warning: String) {
        speak(warning, urgent = true)
    }
    
    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }
    
    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}