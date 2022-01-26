package study.my.rss_reader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
//Установка нового адаптера(когда создается новый фрагмент)
//В адаптере описывается способ связи между данными и компонентом
class MyRssItemRecyclerViewAdapter(
    private val mValues: List<RssItem>,
    private val context: FragmentActivity?
) : RecyclerView.Adapter<MyRssItemRecyclerViewAdapter.ViewHolder>() {


    private val mOnClickListener: View.OnClickListener = View.OnClickListener {
    }
//    идентификатор макета для отдельного элемента списка
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_r_s_s, parent, false)
        return ViewHolder(view)
    }
//    связываем используемые текстовые метки с данными
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.titleTV?.text = item.title
        holder.linkTV?.text = item.link
        holder.contentTV?.text  = item.description
        holder.pubDateTV?.text = item.pubDate

        val link = getFeaturedImageLink(item.description)

        if(link != null) {
            context?.let {
                holder.featuredImg?.let { it1 ->
                    Glide.with(it)
                            .load(link)
                            .centerCrop()
                            .into(it1)
                }
            }
        }

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }
//    достаем значения item по id
    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val titleTV: TextView? = mView.findViewById(R.id.txtTitle)
        val linkTV: TextView? = mView.findViewById(R.id.txtLink)
        val contentTV: TextView? = mView.findViewById(R.id.txtContent)
        val pubDateTV: TextView? = mView.findViewById(R.id.txtPubdate)
        val featuredImg: ImageView? = mView.findViewById(R.id.featuredImg)
    }


    private fun getFeaturedImageLink(htmlText: String): String? {
        var result: String? = null

        StringBuilder()
        try {
            val doc: Document = Jsoup.parse(htmlText)
            val imgs: Elements = doc.select("img")

            for (img in imgs) {
                val src = img.attr("src")
                result = src
            }

        } catch (e: IOException) {

        }
        return result

    }
}