package live.iptvbdlive

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
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
            toggleFullscreen(fullscreenButton)
        }
    }

    private fun toggleFullscreen(
        button: TextView
    ) {

        isFullscreen = !isFullscreen

        if (isFullscreen) {

            requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

            button.text = "⛶"

        } else {

            requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_VISIBLE

            button.text = "⛶"
        }
    }

    override fun onBackPressed() {

        if (isFullscreen) {

            val button =
                findViewById<TextView>(
                    R.id.fullscreenButton
                )

            toggleFullscreen(button)

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
