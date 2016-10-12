package mmss.musicco.dataobjects;

/**
 * Created by User on 12.10.2016.
 */

public class Actor {
    public String name;
    public int tracksCount;
    public int albumsCount;

    public Actor() {

    }

    public Actor(String name, int tracksCount, int albumsCount) {
        this.name = name;
        this.tracksCount = tracksCount;
        this.albumsCount = albumsCount;
    }
}
