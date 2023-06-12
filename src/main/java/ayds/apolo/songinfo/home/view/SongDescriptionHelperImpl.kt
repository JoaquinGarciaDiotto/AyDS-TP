package ayds.apolo.songinfo.home.view

import ayds.apolo.songinfo.home.model.entities.EmptySong
import ayds.apolo.songinfo.home.model.entities.Song
import ayds.apolo.songinfo.home.model.entities.SpotifySong

interface SongDescriptionHelper {
  fun getSongDescriptionText(song: Song = EmptySong): String
}

internal class SongDescriptionHelperImpl : SongDescriptionHelper {
  override fun getSongDescriptionText(song: Song): String {
    return when (song) {
      is SpotifySong -> "<font face=\"arial\"> " +
          "${
            "Song: ${song.songName} " +
                    when {
                        song.isCacheStored -> "[*][*]"
                        song.isLocallyStored -> "[*]"
                        else -> ""
                    }
          } <br>" +
          "Artist: ${song.artistName} <br>" +
          "Album: ${song.albumName} <br>" +
          "Year: ${song.year} " +
          "</font>"
      else -> "Song not found"
    }
  }
}