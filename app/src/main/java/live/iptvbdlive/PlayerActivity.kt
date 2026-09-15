package live.iptvbdlive

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

class PlayerActivity : Activity() {

    private var player: ExoPlayer? = null
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

        player =
            ExoPlayer.Builder(this).build().also {

                playerView.player = it

                it.setMediaItem(
                    MediaItem.fromUri(url)
                )

                it.prepare()

                it.playWhenReady = true
            }

        fullscreenButton.setOnClickListener {

            toggleFullscreen(
                playerView,
                title,
                fullscreenButton
            )
        }
    }

    private fun toggleFullscreen(
        playerView: PlayerView,
        title: TextView,
        button: TextView
    ) {

        isFullscreen = !isFullscreen

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

            // Crop/zoom নয়, পুরো video fit থাকবে
            playerView.resizeMode =
                AspectRatioFrameLayout.RESIZE_MODE_FIT

            title.visibility =
                View.GONE

            button.text = "⛶"

        } else {

            // Normal orientation
            requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

            // System bars দেখাবে
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_VISIBLE

            // Normal অবস্থাতেও video fit
            playerView.resizeMode =
                AspectRatioFrameLayout.RESIZE_MODE_FIT

            title.visibility =
                View.VISIBLE

            button.text = "⛶"
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

        // Fullscreen orientation change-এর সময়
        // Activity recreate না হওয়ায় player এখানে
        // অকারণে release হবে না।
        if (isChangingConfigurations) {
            return
        }

        player?.release()

        player = null
    }
}
