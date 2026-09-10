package live.iptvbdlive

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class PlayerActivity : Activity() {

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_player)

        val playerView = findViewById<PlayerView>(R.id.playerView)

        findViewById<TextView>(R.id.playerTitle).text =
            intent.getStringExtra("title").orEmpty()

        val url = intent.getStringExtra("url").orEmpty()

        player = ExoPlayer.Builder(this).build().also {

            playerView.player = it

            it.setMediaItem(
                MediaItem.fromUri(url)
            )

            it.prepare()

            it.playWhenReady = true
        }
    }

    override fun onStop() {
        super.onStop()

        player?.release()

        player = null
    }
}
