package study.my.rss_reader

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class RSSFragment : Fragment() {


//    присваиваем переменной адаптер
    var adapter: MyRssItemRecyclerViewAdapter? = null
//    присваиваем переменной список из RssItem
    var rssItems = ArrayList<RssItem>()
//    присваиваем RecyclerView
    var listV: RecyclerView ?= null
//    Создания компонентов внутри фрагмента
    override fun onCreateView(
//    inflater создание иерархии RSSFragment(управление фрагментами)
//    LayoutInflater вертикальная ориентация макета
    inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_r_s_s_list, container, false)

        listV = view.findViewById(R.id.listV)
        return view
    }
//    Вызывается, когда отработает метод активности onCreate(),
//а значит фрагмент может обратиться к компонентам активности
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var text = arguments?.getString("text")
        adapter = MyRssItemRecyclerViewAdapter(rssItems, activity)
        listV?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        listV?.adapter = adapter
//    получение ссылки url
        val url = URL(text)
        RssFeedFetcher(this).execute(url)
    }
//    обращение к адаптеру и добавление статей
    fun updateRV(rssItemsL: List<RssItem>) {
        if (rssItemsL.isNotEmpty()) {
            rssItems.addAll(rssItemsL)
            adapter?.notifyDataSetChanged()
        }
    }
//    получаем поток c сервера при переходе по данной ссылке RSS
    class RssFeedFetcher(val context: RSSFragment) : AsyncTask<URL, Void, List<RssItem>>() {
//    референт - объект, на который будет ссылаться новая ссылка
        val reference = WeakReference(context)
        private var stream: InputStream? = null
//    выполняет вычисление в фоновом потоке
        override fun doInBackground(vararg params: URL?): List<RssItem>? {
            val connect = params[0]?.openConnection() as HttpsURLConnection
            connect.readTimeout = 8000
            connect.connectTimeout = 8000
            connect.requestMethod = "GET"
            connect.connect()

            val responseCode: Int = connect.responseCode;
            var rssItems: List<RssItem>? = null
            if (responseCode == 200) {
                stream = connect.inputStream

//                передаем поток классу парсера
                try {
                    val parser = RssParser()
                    rssItems = parser.parse(stream!!)

                } catch (e: IOException) {
                    e.printStackTrace()
                }


            }

            return rssItems

        }
//    возвращение вычисления в фоновом потоке
        override fun onPostExecute(result: List<RssItem>?) {
            super.onPostExecute(result)
            if (result != null && result.isNotEmpty()) {
                reference.get()?.updateRV(result)
            }

        }

    }


}