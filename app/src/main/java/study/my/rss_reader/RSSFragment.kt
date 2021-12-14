package study.my.rss_reader

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import study.my.rss_reader.CustomTabHelper.Companion.STABLE_PACKAGE
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.provider.Settings.Global.putString
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RSSFragment : Fragment() {
    //    присваиваем переменной адаптер
    var adapter: MyRssItemRecyclerViewAdapter? = null
    //    присваиваем переменной список из RssItem
    var rssItems = ArrayList<RssItem>()
    //    присваиваем RecyclerView
    lateinit var recyclerView: RecyclerView
    //    Создания компонентов внутри фрагмента
    //    inflater создание иерархии RSSFragment(управление фрагментами)
    //    LayoutInflater вертикальная ориентация макета
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_r_s_s_list, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        return view
    }
//    реализация gson для работы SharedPreferences(чтение в режиме offline)
    inline fun <reified T> SharedPreferences.addItemToList(spListKey: String, item: T) {
        val savedList = getList<T>(spListKey).toMutableList()
        savedList.add(item)
        val listJson = Gson().toJson(savedList)
        edit { putString(spListKey, listJson) }
    }

    inline fun <reified T> SharedPreferences.removeItemFromList(spListKey: String, item: T) {
        val savedList = getList<T>(spListKey).toMutableList()
        savedList.remove(item)
        val listJson = Gson().toJson(savedList)
        edit {
            putString(spListKey, listJson)
        }
    }

    fun <T> SharedPreferences.putList(spListKey: String, list: List<T>) {
        val listJson = Gson().toJson(list)
        edit {
            putString(spListKey, listJson)
        }
    }

    inline fun <reified T> SharedPreferences.getList(spListKey: String): List<T> {
        val listJson = getString(spListKey, "")
        if (!listJson.isNullOrBlank()) {
            val type = object : TypeToken<List<T>>() {}.type
            return Gson().fromJson(listJson, type)
        }
        return listOf()
    }



    //    Вызывается, когда отработает метод активности onCreate(),
    //а значит фрагмент может обратиться к компонентам активности
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val text = arguments?.getString("text")
        adapter = MyRssItemRecyclerViewAdapter(rssItems, activity)
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter = adapter
    //    получение ссылки url
        val url = URL(text)
        RssFeedFetcher(this).execute(url)
        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(recyclerView,
            object : RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    val builder = CustomTabsIntent.Builder()
                    val anotherCustomTab = CustomTabsIntent.Builder().build()
                    val requestCode = 100
                    val intent = anotherCustomTab.intent
                    intent.setData(Uri.parse(rssItems[position].link))
                    val pendingIntent = PendingIntent.getActivity(context,
                        requestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT)
                    builder.addMenuItem("Sample item", pendingIntent)
                    builder.setShowTitle(true)
                    activity?.let { it1 -> builder.setStartAnimations(it1.applicationContext,
                        android.R.anim.fade_in, android.R.anim.fade_out) }
                    activity?.let { it1 -> builder.setExitAnimations(it1.applicationContext,
                        android.R.anim.fade_in, android.R.anim.fade_out) }
                    val customTabsIntent = builder.build()
                        customTabsIntent.intent.setPackage(STABLE_PACKAGE)
                        customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity?.let { it1 -> customTabsIntent.launchUrl(it1.applicationContext,
                            Uri.parse(rssItems[position].link)) }
                }

                override fun onLongItemClick(child: View, position: Int) {
                    Toast.makeText(activity, "$position item clicked!", Toast.LENGTH_LONG).show()
                }
            })
        )

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

            val responseCode: Int = connect.responseCode
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
