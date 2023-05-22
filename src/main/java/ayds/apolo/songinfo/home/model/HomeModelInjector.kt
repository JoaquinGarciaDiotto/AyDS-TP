package ayds.apolo.songinfo.home.model

import ayds.apolo.songinfo.home.model.repository.SongRepository
import ayds.apolo.songinfo.home.model.repository.SongRepositoryImpl

object HomeModelInjector {


  private val spotifyLocalStorage = SpotifySqlDBImpl(SpotifySqlQueriesImpl(), ResultSetToSpotifySongMapperImpl())

  private val repository: SongRepository = SongRepositoryImpl(spotifyLocalStorage, SpotifyModule.spotifyTrackService)

  val homeModel: HomeModel = HomeModelImpl(repository)
}