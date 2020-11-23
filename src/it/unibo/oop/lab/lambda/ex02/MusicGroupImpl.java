package it.unibo.oop.lab.lambda.ex02;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<String> orderedSongNames() {
        return this.songs.stream().map(song -> song.getSongName())
                .sorted((songName1, songName2) -> songName1.compareTo(songName2));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<String> albumNames() {
        return this.albums.entrySet().stream().map(album -> album.getKey());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<String> albumInYear(final int year) {
        return this.albums.entrySet().stream().filter(albumEntry -> albumEntry.getValue() == year)
                .map(item -> item.getKey());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countSongs(final String albumName) {
        Objects.requireNonNull(albumName);
        // Made it really verbose with multiple filter
        return (int) this.songWithAlbumName().map(song -> song.getAlbumName().get())
                .filter(songAlbumName -> songAlbumName.equals(albumName)).count();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countSongsInNoAlbum() {
        return (int) this.songs.stream().filter(song -> song.getAlbumName().isEmpty()).count();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return this.songWithAlbumName().filter(song -> song.getAlbumName().get().equals(albumName))
                .mapToDouble(song -> song.getDuration()).average();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> longestSong() {
        return this.songs.stream().max((song1, song2) -> Double.compare(song1.getDuration(), song2.getDuration()))
                .map(song -> song.getSongName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> longestAlbum() {
        return this.albums.entrySet().stream()
                .max((albumEntry1, albumEntry2) -> albumEntry1.getValue().compareTo(albumEntry2.getValue()))
                .map(albumEntry -> albumEntry.getKey());
    }

    /**
     * Find all songs that have an album name.
     * 
     * @return all the songs that have an album name
     */
    private Stream<Song> songWithAlbumName() {
        return this.songs.stream().filter(song -> song.getAlbumName().isPresent());
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return this.songName;
        }

        public Optional<String> getAlbumName() {
            return this.albumName;
        }

        public double getDuration() {
            return this.duration;
        }

        @Override
        public int hashCode() {
            if (this.hash == 0) {
                this.hash = this.songName.hashCode() ^ this.albumName.hashCode() ^ Double.hashCode(this.duration);
            }
            return this.hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return this.albumName.equals(other.albumName) && this.songName.equals(other.songName)
                        && this.duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + this.songName + ", albumName=" + this.albumName + ", duration=" + this.duration
                    + "]";
        }

    }

}
