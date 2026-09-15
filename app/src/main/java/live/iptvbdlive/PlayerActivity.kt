package live.iptvbdlive

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

class PlayerActivity : Activity() {

    private var player: ExoPlayer? = null
    private var webView: WebView? = null
    private var isFullscreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_player)

        val playerView =
            findViewById<PlayerView>(R.id.playerView)

        val title =
            findViewById<TextView>(R.id.playerTitle)

        val fullscreenButton =
            findViewById<TextView>(R.id.fullscreenButton)

        // Video aspect ratio ঠিক রাখবে
        playerView.resizeMode =
            AspectRatioFrameLayout.RESIZE_MODE_FIT

        title.text =
            intent.getStringExtra("title").orEmpty()

        val url =
            intent.getStringExtra("url").orEmpty()

        val type =
            intent.getStringExtra("type")
                .orEmpty()
                .lowercase()

        // Premium তথ্য বর্তমানে শুধু সংরক্ষণ করা হচ্ছে।
        // এখন কোনো Premium content block করা হচ্ছে না।
        val premium =
            intent.getBooleanExtra(
                "premium",
                false
            )

        when (type) {

            "youtube" -> {

                // YouTube-এর জন্য ExoPlayer ব্যবহার করা হবে না।
                playerView.visibility =
                    View.GONE

                openYouTube(
                    url,
                    playerView
                )
            }

            "bilibili" -> {

                // Bilibili-এর জন্য WebView
                playerView.visibility =
                    View.GONE

                openWebPage(
                    url,
                    playerView
                )
            }

            else -> {

                // m3u8 / mp4
                playerView.visibility =
                    View.VISIBLE

                if (url.isNotBlank()) {

                    player =
                        ExoPlayer.Builder(this)
                            .build()
                            .also {

                                playerView.player = it

                                it.setMediaItem(
                                    MediaItem.fromUri(url)
                                )

                                it.prepare()

                                it.playWhenReady = true
                            }
                }
            }
        }

        fullscreenButton.setOnClickListener {

            toggleFullscreen(
                playerView,
                title,
                fullscreenButton
            )
        }
    }

    /**
     * YouTube URL থেকে Video ID বের করে
     * YouTube Embed URL তৈরি করে।
     */
    private fun openYouTube(
        url: String,
        playerView: PlayerView
    ) {

        val videoId =
            extractYouTubeVideoId(url)

        if (videoId.isBlank()) {

            playerView.visibility =
                View.VISIBLE

            return
        }

        val embedUrl =
            "https://www.youtube.com/embed/$videoId" +
                    "?autoplay=1" +
                    "&playsinline=1"

        openWebView(
            embedUrl,
            playerView
        )
    }

    /**
     * Bilibili বা অন্য Web URL-এর জন্য WebView।
     */
    private fun openWebPage(
        url: String,
        playerView: PlayerView
    ) {

        if (url.isBlank()) {
            return
        }

        openWebView(
            url,
            playerView
        )
    }

    /**
     * Activity layout-এর ভিতরে WebView তৈরি করে।
     *
     * activity_player.xml পরিবর্তন করার দরকার নেই।
     */
    private fun openWebView(
        url: String,
        playerView: PlayerView
    ) {

        val root =
            findViewById<FrameLayout>(
                android.R.id.content
            )

        val newWebView =
            WebView(this)

        webView =
            newWebView

        newWebView.setBackgroundColor(
            Color.BLACK
        )

        newWebView.settings.apply {

            javaScriptEnabled = true

            domStorageEnabled = true

            mediaPlaybackRequiresUserGesture =
                false

            loadWithOverviewMode = true

            useWideViewPort = true

            builtInZoomControls = false

            displayZoomControls = false

            cacheMode =
                WebSettings.LOAD_DEFAULT
        }

        newWebView.webViewClient =
            WebViewClient()

        newWebView.webChromeClient =
            WebChromeClient()

        newWebView.isFocusable = true
        newWebView.isFocusableInTouchMode = true

        root.addView(
            newWebView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        newWebView.loadUrl(url)
    }

    /**
     * YouTube URL থেকে Video ID বের করে।
     *
     * Support:
     * https://www.youtube.com/watch?v=VIDEO_ID
     * https://www.youtube.com/live/VIDEO_ID
     * https://youtu.be/VIDEO_ID
     */
    private fun extractYouTubeVideoId(
        url: String
    ): String {

        return try {

            val uri =
                android.net.Uri.parse(url)

            val host =
                uri.host
                    .orEmpty()
                    .lowercase()

            when {

                host.contains("youtu.be") -> {

                    uri.pathSegments
                        .firstOrNull()
                        .orEmpty()
                }

                uri.pathSegments
                    .contains("live") -> {

                    val index =
                        uri.pathSegments
                            .indexOf("live")

                    if (
                        index >= 0 &&
                        index + 1 <
                        uri.pathSegments.size
                    ) {

                        uri.pathSegments[
                            index + 1
                        ]

                    } else {
                        ""
                    }
                }

                host.contains("youtube.com") -> {

                    uri.getQueryParameter(
                        "v"
                    ).orEmpty()
                }

                else -> {
                    ""
                }
            }

        } catch (
            _: Exception
        ) {

            ""
        }
    }

    private fun toggleFullscreen(
        playerView: PlayerView,
        title: TextView,
        button: TextView
    ) {

        isFullscreen =
            !isFullscreen

        if (isFullscreen) {

            // Landscape fullscreen
            requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            // System navigation/status bar hide
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE

            // Crop/zoom নয়
            playerView.resizeMode =
                AspectRatioFrameLayout.RESIZE_MODE_FIT

            title.visibility =
                View.GONE

            button.text =
                "⛶"

        } else {

            // Normal orientation
            requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

            // System bars দেখাবে
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_VISIBLE

            playerView.resizeMode =
                AspectRatioFrameLayout.RESIZE_MODE_FIT

            title.visibility =
                View.VISIBLE

            button.text =
                "⛶"
        }
    }

    override fun onBackPressed() {

        if (isFullscreen) {

            val playerView =
                findViewById<PlayerView>(
                    R.id.playerView
                )

            val title =
                findViewById<TextView>(
                    R.id.playerTitle
                )

            val button =
                findViewById<TextView>(
                    R.id.fullscreenButton
                )

            toggleFullscreen(
                playerView,
                title,
                button
            )

        } else {

            super.onBackPressed()
        }
    }

    override fun onStop() {

        super.onStop()

        // Orientation change-এর সময়
        // player অকারণে release হবে না।
        if (isChangingConfigurations) {
            return
        }

        player?.release()

        player = null
    }

    override fun onDestroy() {

        webView?.apply {

            stopLoading()

            loadUrl("about:blank")

            clearHistory()

            removeAllViews()

            destroy()
        }

        webView = null

        super.onDestroy()
    }
}
