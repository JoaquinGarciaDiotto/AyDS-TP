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
internal class SongRepositoryImpl : SongRepository {

    private val spotifyLocalStorage : SpotifyLocalStorage = SpotifySqlDBImpl(SpotifySqlQueriesImpl(), ResultSetToSpotifySongMapperImpl())
    private val spotifyTrackService = SpotifyModule.spotifyTrackService
    private val cache = mutableMapOf<String, SpotifySong>()
    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://en.wikipedia.org/w/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
    private var wikipediaAPI: WikipediaAPI = retrofit.create(WikipediaAPI::class.java)

    override fun getSongByTerm(term: String): SearchResult {
        var spotifySong: SpotifySong?

        spotifySong = getSongFromCache(term)
        if (spotifySong != null)
            return spotifySong

        spotifySong = getSongFromLocalStorage(term)
        if (spotifySong != null) {
            storeSpotifySongInCache(term, spotifySong)
            return spotifySong
        }

        spotifySong = getSongFromTrackService(term)
        if (spotifySong != null) {
            storeSpotifySongInLocalStorage(term, spotifySong)
            return spotifySong
        }

        spotifySong = getSongFromWikipediaAPI(term)
        return spotifySong ?: EmptySong
    }

    private fun getSongFromCache(term: String): SpotifySong? {
        return cache[term]
    }

    private fun getSongFromLocalStorage(term: String): SpotifySong? {
        return spotifyLocalStorage.getSongByTerm(term)
    }

    private fun getSongFromTrackService(term: String): SpotifySong? {
        return spotifyTrackService.getSong(term)
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

    private fun storeSpotifySongInCache(term: String, spotifySong: SpotifySong){
        cache[term] = spotifySong
        spotifySong.isCacheStored = true
    }

    private fun storeSpotifySongInLocalStorage(term: String, spotifySong: SpotifySong){
        spotifyLocalStorage.insertSong(term, spotifySong)
        spotifySong.isLocallyStored = true
    }

    private fun getCallResponseFromWikipediaAPI(term: String): Response<String> {
        return wikipediaAPI.getInfo(term).execute()
    }

}