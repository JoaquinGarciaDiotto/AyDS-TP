package ayds.apolo.songinfo.home.model.repository

import ayds.apolo.songinfo.home.model.entities.EmptySong
import ayds.apolo.songinfo.home.model.entities.SearchResult
import ayds.apolo.songinfo.home.model.entities.SpotifySong
import ayds.apolo.songinfo.home.model.repository.external.spotify.SpotifyModule
import ayds.apolo.songinfo.home.model.repository.local.spotify.SpotifyLocalStorage
import ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb.ResultSetToSpotifySongMapperImpl
import ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb.SpotifySqlDBImpl
import ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb.SpotifySqlQueriesImpl
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException

interface SongRepository {

    fun getSongByTerm(term: String): SearchResult

}
internal class SongRepositoryImpl(
    private val spotifyLocalStorage: SpotifyLocalStorage,
    private val spotifyTrackService: SpotifyTrackService,
    private val cache: SongCache
) : SongRepository {

    //Pasarlos al inyector
    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://en.wikipedia.org/w/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
    private var wikipediaAPI: WikipediaAPI = retrofit.create(WikipediaAPI::class.java)
    //Clase para wikipedia y un broker
    override fun getSongByTerm(term: String): SearchResult {
        var spotifySong: SpotifySong?

        spotifySong = cache.getSongByTerm(term)
        if (spotifySong == null){
            spotifySong = spotifyLocalStorage.getSongByTerm(term)
            if (spotifySong == null) {
                spotifySong = spotifyTrackService.getSong(term)
                if (spotifySong == null)
                    spotifySong = getSongFromWikipediaAPI(term)
                 else
                     storeSpotifySongInLocalStorage(term, spotifySong)
            else
                storeSpotifySongInCache(term, spotifySong)
            }
        }

        return spotifySong ?: EmptySong
    }

    private fun getSongFromWikipediaAPI(term: String): SpotifySong? {
        val callResponse: Response<String>
        try {
            callResponse = getCallResponseFromWikipediaAPI(term)
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

    private fun storeSpotifySongInLocalStorage(term: String, spotifySong: SpotifySong){
        spotifyLocalStorage.insertSong(term, spotifySong)
        spotifySong.isLocallyStored = true
    }

    private fun getCallResponseFromWikipediaAPI(term: String): Response<String> {
        return wikipediaAPI.getInfo(term).execute()
    }

}