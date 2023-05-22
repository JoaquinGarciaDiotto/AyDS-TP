

interface SongCache {
    fun insertSong(query: String, song: SpotifySong)

    fun getSongByTerm(term: String): SpotifySong?

}

internal class SongCacheImpl : SongCache {

    private val songCache = mutableMapOf<String, SpotifySong>()

    override fun insertSong(query: String, song: SpotifySong) {
        cache[term] = spotifySong
        spotifySong.isCacheStored = true
    }

    override fun getSongByTerm(term: String) = songCache[term]
}