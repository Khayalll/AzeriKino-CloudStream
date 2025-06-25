import com.lagradost.cloudstream3.*
    import com.lagradost.cloudstream3.utils.*

    class AzeriKinoPlugin : MainAPI() {
        override var mainUrl = "https://www.azerikino.com"
        override var name = "AzeriKino"
        override val hasMainPage = true
        override val supportedTypes = setOf(TvType.Movie)

        override suspend fun search(query: String): List<SearchResponse> {
            val doc = app.get("\$mainUrl/?s=\${query.replace(" ", "+")}").document
            return doc.select(".post-title a").map {
                val title = it.text()
                val href = it.attr("href")
                MovieSearchResponse(title, href, name, TvType.Movie)
            }
        }

        override suspend fun load(url: String): LoadResponse {
            val doc = app.get(url).document
            val title = doc.selectFirst("h1")?.text() ?: return ErrorLoadResponse
            val iframe = doc.select("iframe").firstOrNull { it.hasAttr("src") }
            val src = iframe?.attr("src")?.trim() ?: return ErrorLoadResponse

            val sources = mutableListOf<ExtractorLink>()
            loadExtractor(src, name, sources)
            return MovieLoadResponse(title, url, name, TvType.Movie, sources = sources)
        }
    }