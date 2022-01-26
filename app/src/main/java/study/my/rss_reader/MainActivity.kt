package study.my.rss_reader

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    private var urledit: String = ""
    private lateinit var menu: Menu
    private var item1: String = ""
    private var item2: String = ""
    private var item3: String = ""
    private var item4: String = ""
    private var item5: String = ""
    //    старт активити
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

    }

//     данный метод отвечает за появление меню у активности
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        if (menu != null) {
            this.menu = menu
        }
        return true
    }
//    Здесь обрабатываются щелчки по элементам панели
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item1, R.id.item2, R.id.item3, R.id.item4, R.id.item5 -> {
                val fragment = RSSFragment()
                val bundle = Bundle().apply {
                    putString("text", item.title.toString())
                }
                fragment.arguments = bundle
                //        вызываем фрагмент с помощью метода управления фрагментами
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_root,fragment)
                    .commit()
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

//    создание диалогового окна
    fun createDialog(view: android.view.View) {
        val dialog = layoutInflater.inflate(R.layout.dialog, null, false)
        val editText = dialog.findViewById<EditText>(R.id.enter)
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Добавить RSS")
        alertDialogBuilder.setView(dialog)
        alertDialogBuilder.setPositiveButton("Добавить") { dialog, i ->
            nextStep(editText)
        }
        alertDialogBuilder.setNegativeButton("Отмена") { dialog, i ->
            dialog.cancel()
        }
        alertDialogBuilder.show()
    }
//    получение ссылки из диалогового окна и добавление item в menu после перезапуска приложения
    fun nextStep(editText: EditText) {
        urledit = editText.text.toString()

        if (item1 == "")  {
            item1 = urledit
            menu.findItem(R.id.item1).title = urledit
        } else if (item2 == "") {
            item2 = urledit
            menu.findItem(R.id.item2).title = urledit
        } else if (item3 == ("")) {
            item3 = urledit
            menu.findItem(R.id.item3).title = urledit
        } else if (item4 == ("")) {
            item4 = urledit
            menu.findItem(R.id.item4).title = urledit
        } else if (item5 == ("")) {
            item5 = urledit
            menu.findItem(R.id.item5).title = urledit
        }
    }


}