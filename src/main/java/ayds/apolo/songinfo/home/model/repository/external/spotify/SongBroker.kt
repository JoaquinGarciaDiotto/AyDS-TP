package ayds.apolo.songinfo.home.model.repository.external.spotify

import ayds.apolo.songinfo.home.model.entities.SpotifySong

interface SongBroker {

    fun getSong(term: String): SpotifySong?

}
internal class SongBrokerImpl(
    private val spotifyService: SpotifyTrackService,
    private val wikipediaService: WikipediaService
): SongBroker {

    override fun getSong(term: String): SpotifySong? {
        var spotifySong = spotifyService.getSong(term)
        if(spotifySong==null)
            spotifySong = wikipediaService.getSong(term)
        return spotifySong
    }

}