package study.my.rss_reader
//элементы recyclerview
class RssItem {
    var title = ""
    var link = ""
    var description = ""
    var pubDate = ""

    override fun toString(): String {
        return "RssItem(title='$title', link='$link', description='$description', pubDate='$pubDate')"
    }
}