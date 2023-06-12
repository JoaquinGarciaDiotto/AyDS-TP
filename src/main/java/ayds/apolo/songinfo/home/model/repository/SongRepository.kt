package ayds.apolo.songinfo.home.model.repository

import SongCache
import ayds.apolo.songinfo.home.model.entities.EmptySong
import ayds.apolo.songinfo.home.model.entities.Song
import ayds.apolo.songinfo.home.model.entities.SpotifySong
import ayds.apolo.songinfo.home.model.repository.external.spotify.SongBroker
import ayds.apolo.songinfo.home.model.repository.local.spotify.SpotifyLocalStorage


interface SongRepository {

    fun getSongByTerm(term: String): Song

}
internal class SongRepositoryImpl(
    private val spotifyLocalStorage: SpotifyLocalStorage,
    private val songBroker: SongBroker,
    private val cache: SongCache
) : SongRepository {


    //Clase para wikipedia y un broker
    override fun getSongByTerm(term: String): Song {
        var spotifySong: SpotifySong?

        spotifySong = cache.getSongByTerm(term)
        if (spotifySong == null){
            spotifySong = spotifyLocalStorage.getSongByTerm(term)
            if (spotifySong == null) {
                spotifySong = songBroker.getSong(term)
                if (spotifySong != null) {
                    storeSpotifySongInLocalStorage(term, spotifySong)
                    cache.insertSong(term, spotifySong)
                }
            } else
                cache.insertSong(term, spotifySong)
        }

        return spotifySong ?: EmptySong
    }

    private fun storeSpotifySongInLocalStorage(term: String, spotifySong: SpotifySong){
        spotifyLocalStorage.insertSong(term, spotifySong)
        spotifySong.isLocallyStored = true
    }

}