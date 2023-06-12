package ayds.apolo.songinfo.home.model.repository.external.spotify

import ayds.apolo.songinfo.home.model.entities.SpotifySong
import ayds.apolo.songinfo.home.model.repository.WikipediaAPI
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException

interface WikipediaService {

    fun getSong(term: String): SpotifySong?

}
internal class WikipediaServiceImpl: WikipediaService {

    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://en.wikipedia.org/w/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
    private var wikipediaAPI: WikipediaAPI = retrofit.create(WikipediaAPI::class.java)

    override fun getSong(term: String): SpotifySong? {
        val callResponse: Response<String>
        try {
            callResponse = wikipediaAPI.getInfo(term).execute()
            val gson = Gson()
            val jobj: JsonObject = gson.fromJson(callResponse.body(), JsonObject::class.java)
            val query = jobj["query"].asJsonObject
            val snippetObj = query["search"].asJsonArray.firstOrNull()
            if (snippetObj != null) {
                val snippet = snippetObj.asJsonObject["snippet"]
                return SpotifySong("", snippet.asString, " - ", " - ", " - ", "", "")
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return null
    }
}