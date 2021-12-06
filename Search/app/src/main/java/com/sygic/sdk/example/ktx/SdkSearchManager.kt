package com.sygic.sdk.example.ktx

import com.sygic.sdk.context.CoreInitCallback
import com.sygic.sdk.context.CoreInitException
import com.sygic.sdk.search.SearchManager
import com.sygic.sdk.search.SearchManagerProvider
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SdkSearchManager {
    private suspend fun get(): SearchManager {
        return suspendCoroutine {
            SearchManagerProvider.getInstance(object : CoreInitCallback<SearchManager> {
                override fun onError(error: CoreInitException) {
                    it.resumeWithException(Throwable("Unable to get SearchManager"))
                }

                override fun onInstance(instance: SearchManager) {
                    it.resume(instance)
                }
            })
        }
    }

    suspend fun closeSession(session: SdkSearchSession) = get().closeSession(session.session)
    suspend fun newOnlineSession(): SdkSearchSession = SdkSearchSession(get().newOnlineSession())
}