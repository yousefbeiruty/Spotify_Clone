package com.plcoding.spotifycloneyt.exoplayer

import android.content.ComponentName
import android.content.Context
import android.media.browse.MediaBrowser
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.plcoding.spotifycloneyt.others.Constatns.NETWORK_ERROR
import com.plcoding.spotifycloneyt.others.Event
import com.plcoding.spotifycloneyt.others.Resource
import com.plcoding.spotifycloneyt.others.Status

class MusicServiceConnection(context: Context) {

    private val _isConnected = MutableLiveData<Event<Resource<Boolean>>>()
    val isConnected: LiveData<Event<Resource<Boolean>>> = _isConnected

    private val _netWorkError = MutableLiveData<Event<Resource<Boolean>>>()
    val netWorkError: LiveData<Event<Resource<Boolean>>> = _netWorkError


    private val _playBackState = MutableLiveData<PlaybackStateCompat?>()
    val playBackState: LiveData<PlaybackStateCompat?> = _playBackState

    private val _currentlyPlayingSong = MutableLiveData<MediaMetadataCompat?>()
    val currentlyPlayingSong: LiveData<MediaMetadataCompat?> = _currentlyPlayingSong


    lateinit var mediaController: MediaControllerCompat

    private val mediaBrowserConnectionCallBack = MediaBrowserConnectionCallBack(context)

    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(context, MusicService::class.java),
        mediaBrowserConnectionCallBack,
        null
    ).apply { connect() }

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls //here we used the get because the mediaController has not been initialized yet


    fun subscribe(parentId: String, callBack: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callBack)
    }

    fun unsubscribe(parentId: String, callBack: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callBack)
    }

    private inner class MediaBrowserConnectionCallBack(
        private val context: Context
    ) : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallBack())
            }
            _isConnected.postValue(Event(Resource.success(true)))
        }

        override fun onConnectionSuspended() {
            _isConnected.postValue(
                Event(
                    Resource.error(
                        "The connection was suspended", false
                    )
                )
            )
        }

        override fun onConnectionFailed() {
            _isConnected.postValue(
                Event(
                    Resource.error(
                        "Couldn't connect to media browser", false
                    )
                )
            )
        }
    }

    private inner class MediaControllerCallBack : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playBackState.postValue(state)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            _currentlyPlayingSong.postValue(metadata)
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when (event) {
                NETWORK_ERROR -> _netWorkError.postValue(
                    Event(
                        Resource.error(
                            "Couldn't connect to the server. Please check your internet connection.",
                            null
                        )
                    )
                )
            }
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallBack.onConnectionSuspended()
        }
    }
}