package ayds.apolo.songinfo.home.model

import SongCacheImpl
import ayds.apolo.songinfo.home.model.repository.SongRepository
import ayds.apolo.songinfo.home.model.repository.SongRepositoryImpl
import ayds.apolo.songinfo.home.model.repository.external.spotify.SongBrokerImpl
import ayds.apolo.songinfo.home.model.repository.external.spotify.SpotifyModule
import ayds.apolo.songinfo.home.model.repository.external.spotify.WikipediaServiceImpl
import ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb.ResultSetToSpotifySongMapperImpl
import ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb.SpotifySqlDBImpl
import ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb.SpotifySqlQueriesImpl

object HomeModelInjector {

  private val spotifyLocalStorage = SpotifySqlDBImpl(SpotifySqlQueriesImpl(), ResultSetToSpotifySongMapperImpl())

  private val songBroker = SongBrokerImpl(SpotifyModule.spotifyTrackService, WikipediaServiceImpl())

  private val repository: SongRepository = SongRepositoryImpl(spotifyLocalStorage, songBroker, SongCacheImpl())

  val homeModel: HomeModel = HomeModelImpl(repository)
}