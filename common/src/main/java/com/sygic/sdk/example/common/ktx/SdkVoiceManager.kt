package com.sygic.sdk.example.common.ktx

import com.sygic.sdk.context.CoreInitCallback
import com.sygic.sdk.context.CoreInitException
import com.sygic.sdk.voice.VoiceEntry
import com.sygic.sdk.voice.VoiceManager
import com.sygic.sdk.voice.VoiceManagerProvider
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SdkVoiceManager {
    private suspend fun get(): VoiceManager {
        return suspendCoroutine {
            VoiceManagerProvider.getInstance(object : CoreInitCallback<VoiceManager> {
                override fun onError(error: CoreInitException) {
                    it.resumeWithException(Throwable("Unable to get VoiceManager"))
                }

                override fun onInstance(instance: VoiceManager) {
                    it.resume(instance)
                }
            })
        }
    }

    suspend fun getInstalledVoices(): List<VoiceEntry> {
        val voiceManager = get()
        return suspendCoroutine {
            voiceManager.getInstalledVoices { voices, _ ->
                it.resume(voices)
            }
        }
    }

    suspend fun getDefaultTts(): String {
        val voiceManager = get()
        return suspendCoroutine {
            voiceManager.getDefaultTtsLocale { locale ->
                it.resume(locale)
            }
        }
    }

    suspend fun getVoice(): VoiceEntry {
        return get().voice
    }

    suspend fun setVoice(voice: VoiceEntry) {
        get().voice = voice
    }
}