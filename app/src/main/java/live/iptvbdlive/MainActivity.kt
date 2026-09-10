package live.iptvbdlive

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : Activity() {

    private lateinit var adapter: ContentAdapter
    private lateinit var allItems: List<ContentItem>

    private var selectedCategory = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Sample content
        allItems = demoContent()

        setupGrid()
        setupCategories()
        setupSearch()
        setupBottomNavigation()
    }

    // --------------------------------------------------
    // Content Grid
    // --------------------------------------------------

    private fun setupGrid() {

        val grid = findViewById<RecyclerView>(R.id.contentGrid)

        val columns = if (isAndroidTV()) {
            5
        } else {
            3
        }

        grid.layoutManager = GridLayoutManager(this, columns)

        adapter = ContentAdapter(allItems) { item ->

            val intent = Intent(this, PlayerActivity::class.java)

            intent.putExtra("title", item.title)
            intent.putExtra("url", item.videoUrl)

            startActivity(intent)
        }

        grid.adapter = adapter
    }

    // --------------------------------------------------
    // Categories
    // --------------------------------------------------

    private fun setupCategories() {

        val categoryBar =
            findViewById<LinearLayout>(R.id.categoryBar)

        val categories = listOf(
            "All",
            "Live TV",
            "Movies",
            "Drama",
            "Web Series"
        )

        categories.forEach { category ->

            val button = TextView(this)

            button.text = category
            button.textSize = 14f
            button.setTextColor(
                getColor(R.color.text_primary)
            )

            button.gravity = android.view.Gravity.CENTER

            button.setPadding(
                28,
                0,
                28,
                0
            )

            button.isFocusable = true
            button.isClickable = true

            button.setOnClickListener {

                selectCategory(category)
            }

            categoryBar.addView(
                button,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            )
        }
    }

    // --------------------------------------------------
    // Search
    // --------------------------------------------------

    private fun setupSearch() {

        val searchBox =
            findViewById<EditText>(R.id.searchBox)

        searchBox.addTextChangedListener(
            object : TextWatcher {

                override fun afterTextChanged(
                    text: Editable?
                ) {

                    filter(
                        text?.toString().orEmpty()
                    )
                }

                override fun beforeTextChanged(
                    text: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    text: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }
            }
        )
    }

    // --------------------------------------------------
    // Bottom Navigation
    // --------------------------------------------------

    private fun setupBottomNavigation() {

        findViewById<TextView>(
            R.id.homeButton
        ).setOnClickListener {

            selectCategory("All")
        }

        findViewById<TextView>(
            R.id.liveButton
        ).setOnClickListener {

            selectCategory("Live TV")
        }

        findViewById<TextView>(
            R.id.movieButton
        ).setOnClickListener {

            selectCategory("Movies")
        }

        findViewById<TextView>(
            R.id.moreButton
        ).setOnClickListener {

            selectCategory("Drama")
        }
    }

    // --------------------------------------------------
    // Category Filter
    // --------------------------------------------------

    private fun selectCategory(category: String) {

        selectedCategory = category

        val searchBox =
            findViewById<EditText>(R.id.searchBox)

        filter(
            searchBox.text.toString()
        )
    }

    // --------------------------------------------------
    // Search + Category Filter
    // --------------------------------------------------

    private fun filter(query: String) {

        val result = allItems.filter { item ->

            val categoryMatch =
                selectedCategory == "All" ||
                        item.category == selectedCategory

            val searchMatch =
                query.isBlank() ||
                        item.title.contains(
                            query,
                            ignoreCase = true
                        )

            categoryMatch && searchMatch
        }

        adapter.submitList(result)
    }

    // --------------------------------------------------
    // Android TV Detection
    // --------------------------------------------------

    private fun isAndroidTV(): Boolean {

        return packageManager.hasSystemFeature(
            PackageManager.FEATURE_LEANBACK
        )
    }

    // --------------------------------------------------
    // Sample Content
    // --------------------------------------------------

    private fun demoContent(): List<ContentItem> {

        return listOf(

            ContentItem(
                title = "Sample Live TV",
                category = "Live TV",
                poster = "https://dummyimage.com/600x900/222/fff&text=LIVE+TV",
                videoUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),

            ContentItem(
                title = "Sample Movie",
                category = "Movies",
                poster = "https://dummyimage.com/600x900/222/fff&text=MOVIE",
                videoUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),

            ContentItem(
                title = "Sample Drama",
                category = "Drama",
                poster = "https://dummyimage.com/600x900/222/fff&text=DRAMA",
                videoUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),

            ContentItem(
                title = "Sample Web Series",
                category = "Web Series",
                poster = "https://dummyimage.com/600x900/222/fff&text=WEB+SERIES",
                videoUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            )
        )
    }
}
