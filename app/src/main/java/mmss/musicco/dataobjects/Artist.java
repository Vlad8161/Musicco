package mmss.musicco.dataobjects;

/**
 * Created by User on 12.10.2016.
 */

public class Artist {
    public String name;
    public int tracksCount;

    public Artist() {

    }

    public Artist(String name, int tracksCount) {
        this.name = name;
        this.tracksCount = tracksCount;
    }
}
