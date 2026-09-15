package live.iptvbdlive

data class ContentItem(
    val title: String,
    val category: String,
    val poster: String,
    val videoUrl: String,
    val type: String = "m3u8",
    val premium: Boolean = false
)
