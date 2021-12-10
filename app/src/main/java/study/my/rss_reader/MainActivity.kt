package study.my.rss_reader

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {

    var urledit: String = ""
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
        return true
    }
//    Здесь обрабатываются щелчки по элементам панели
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_menu -> true
            else -> super.onOptionsItemSelected(item)
        }
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
//    получение ссылки из диалогового окна
    fun nextStep(editText: EditText) {
        urledit = editText.text.toString()
        val fragment = RSSFragment()
        val bundle = Bundle().apply {
            putString("text", urledit)
        }
        fragment.arguments = bundle
    //        вызываем фрагмент с помощью метода управления фрагментами
        supportFragmentManager.beginTransaction().replace(R.id.fragment_root,fragment).commit()
    }
}