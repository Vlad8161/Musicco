package mmss.musicco.dataobjects;

/**
 * Created by User on 12.10.2016.
 */

public class Album {
    public String name;
    public String artist;
    public int tracksCount;

    public Album() {

    }

    public Album(String name, String actor, int tracksCount) {
        this.name = name;
        this.artist = actor;
        this.tracksCount = tracksCount;
    }
}
