package com.sygic.sdk.example.common.ktx

import com.sygic.sdk.context.CoreInitCallback
import com.sygic.sdk.context.CoreInitException
import com.sygic.sdk.search.*
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

    suspend fun createOfflineMapSearch(): OfflineMapSearch? {
        val manager = get()
        return suspendCoroutine {
            val listener = object : CreateSearchCallback<OfflineMapSearch> {
                override fun onError(error: CreateSearchCallback.Error) {
                    it.resume(null)
                }

                override fun onSuccess(search: OfflineMapSearch) {
                    it.resume(search)
                }
            }
            manager.createOfflineMapSearch(listener)
        }
    }

    suspend fun createOnlineMapSearch(): OnlineMapSearch? {
        val manager = get()
        return suspendCoroutine {
            val listener = object : CreateSearchCallback<OnlineMapSearch> {
                override fun onError(error: CreateSearchCallback.Error) {
                    it.resume(null)
                }

                override fun onSuccess(search: OnlineMapSearch) {
                    it.resume(search)
                }
            }
            manager.createOnlineMapSearch(listener)
        }
    }

    suspend fun createCoordinateSearch(): CoordinateSearch? {
        val manager = get()
        return suspendCoroutine {
            val listener = object : CreateSearchCallback<CoordinateSearch> {
                override fun onError(error: CreateSearchCallback.Error) {
                    it.resume(null)
                }

                override fun onSuccess(search: CoordinateSearch) {
                    it.resume(search)
                }
            }
            manager.createCoordinateSearch(listener)
        }
    }

    suspend fun createCustomPlacesSearch(): CustomPlacesSearch? {
        val manager = get()
        return suspendCoroutine {
            val listener = object : CreateSearchCallback<CustomPlacesSearch> {
                override fun onError(error: CreateSearchCallback.Error) {
                    it.resume(null)
                }

                override fun onSuccess(search: CustomPlacesSearch) {
                    it.resume(search)
                }
            }
            manager.createCustomPlacesSearch(listener)
        }
    }

    suspend fun createCompositeSearch(
        type: SearchManager.CompositeSearchType,
        searches: List<Search>
    ): CompositeSearch? {
        val manager = get()
        return suspendCoroutine {
            val listener = object : CreateSearchCallback<CompositeSearch> {
                override fun onError(error: CreateSearchCallback.Error) {
                    it.resume(null)
                }

                override fun onSuccess(search: CompositeSearch) {
                    it.resume(search)
                }
            }
            manager.createCompositeSearch(type, searches, listener)
        }
    }
}
