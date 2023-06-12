package ayds.apolo.songinfo.home.model

import ayds.apolo.songinfo.home.model.entities.Song
import ayds.apolo.songinfo.home.model.repository.SongRepository

interface HomeModel {

    fun searchSong(term: String): Song
}

internal class HomeModelImpl(private val repository: SongRepository) : HomeModel {
    override fun searchSong(term: String): Song = repository.getSongByTerm(term)
}