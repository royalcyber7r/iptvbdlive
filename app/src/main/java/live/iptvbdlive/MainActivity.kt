package live.iptvbdlive

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : Activity() {

    private lateinit var adapter: ContentAdapter
    private var allItems: List<ContentItem> = emptyList()

    private var selectedCategory = "All"

    /*
     * এখানে YOUR_GITHUB_USERNAME-এর জায়গায়
     * আপনার GitHub username লিখবেন।
     *
     * উদাহরণ:
     * https://raw.githubusercontent.com/royalcyber7r/iptvbdlive/main/content.json
     */
    private val contentUrl =
        "https://raw.githubusercontent.com/royalcyber7r/iptvbdlive/main/content.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setupGrid()
        setupCategories()
        setupSearch()
        setupBottomNavigation()

        loadContent()
    }

    private fun setupGrid() {

        val grid = findViewById<RecyclerView>(R.id.contentGrid)

        val columns = if (isAndroidTV()) 5 else 3

        grid.layoutManager =
            GridLayoutManager(this, columns)

        adapter = ContentAdapter(emptyList()) { item ->

            val intent =
                Intent(this, PlayerActivity::class.java)

            intent.putExtra("title", item.title)
            intent.putExtra("url", item.videoUrl)

            startActivity(intent)
        }

        grid.adapter = adapter
    }

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

            button.gravity =
                android.view.Gravity.CENTER

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

    private fun setupSearch() {

        val searchBox =
            findViewById<EditText>(R.id.searchBox)

        searchBox.addTextChangedListener(
            object : android.text.TextWatcher {

                override fun afterTextChanged(
                    text: android.text.Editable?
                ) {
                    filter(text?.toString().orEmpty())
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

    private fun selectCategory(
        category: String
    ) {

        selectedCategory = category

        val searchBox =
            findViewById<EditText>(R.id.searchBox)

        filter(searchBox.text.toString())
    }

    private fun filter(query: String) {

        val result =
            allItems.filter { item ->

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

    private fun loadContent() {

        thread {

            try {

                val connection =
                    URL(contentUrl)
                        .openConnection() as HttpURLConnection

                connection.requestMethod = "GET"

                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                connection.connect()

                if (connection.responseCode != 200) {
                    throw Exception(
                        "HTTP ${connection.responseCode}"
                    )
                }

                val json =
                    connection.inputStream
                        .bufferedReader()
                        .use { it.readText() }

                connection.disconnect()

                val jsonArray =
                    JSONArray(json)

                val items =
                    mutableListOf<ContentItem>()

                for (i in 0 until jsonArray.length()) {

                    val obj =
                        jsonArray.getJSONObject(i)

                    val title =
                        obj.optString("title")

                    val category =
                        obj.optString("category")

                    val poster =
                        obj.optString("poster")

                    val videoUrl =
                        obj.optString("videoUrl")

                    if (
                        title.isNotBlank() &&
                        videoUrl.isNotBlank()
                    ) {

                        items.add(
                            ContentItem(
                                title = title,
                                category = category,
                                poster = poster,
                                videoUrl = videoUrl
                            )
                        )
                    }
                }

                runOnUiThread {

                    allItems = items

                    filter(
                        findViewById<EditText>(
                            R.id.searchBox
                        ).text.toString()
                    )
                }

            } catch (e: Exception) {

                runOnUiThread {

                    Toast.makeText(
                        this,
                        "Content load failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun isAndroidTV(): Boolean {

        return packageManager.hasSystemFeature(
            PackageManager.FEATURE_LEANBACK
        )
    }
}
