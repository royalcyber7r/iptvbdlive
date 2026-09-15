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

        // ভিডিও কখনো অযথা crop/zoom করবে না
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

            // Landscape
            requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            // পুরো screen থেকে system bar সরানো
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE

            // Fullscreen-এ video fit থাকবে
            playerView.resizeMode =
                AspectRatioFrameLayout.RESIZE_MODE_FIT

            // Fullscreen-এ title লুকানো
            title.visibility = View.GONE

            button.text = "⛶"

        } else {

            // আগের orientation
            requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

            // System bar আবার দেখানো
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_VISIBLE

            // Normal অবস্থাতেও video fit
            playerView.resizeMode =
                AspectRatioFrameLayout.RESIZE_MODE_FIT

            // Title আবার দেখানো
            title.visibility = View.VISIBLE

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

        player?.release()

        player = null
    }
}
