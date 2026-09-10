package live.iptvbdlive

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ContentAdapter(
    private var items: List<ContentItem>,
    private val onClick: (ContentItem) -> Unit
) : RecyclerView.Adapter<ContentAdapter.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        val poster: ImageView = view.findViewById(R.id.poster)

        val title: TextView = view.findViewById(R.id.title)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_content, parent, false)

        return Holder(view)
    }

    override fun onBindViewHolder(
        holder: Holder,
        position: Int
    ) {

        val item = items[position]

        holder.title.text = item.title

        Glide.with(holder.poster.context)
            .load(item.poster)
            .centerCrop()
            .into(holder.poster)

        // Mobile touch
        holder.itemView.setOnClickListener {
            onClick(item)
        }

        // Android TV remote focus
        holder.itemView.setOnFocusChangeListener { view, focused ->

            view.animate()
                .scaleX(if (focused) 1.04f else 1.0f)
                .scaleY(if (focused) 1.04f else 1.0f)
                .setDuration(120)
                .start()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(newItems: List<ContentItem>) {

        items = newItems

        notifyDataSetChanged()
    }
}
