package mmss.musicco.dataobjects;

/**
 * Created by User on 12.10.2016.
 */

public class Track {
    public String name;
    public String actor;
    public String album;
    public Integer length;
    public Integer size;

    public Track() {

    }

    public Track(String name, String actor, String album, Integer length, Integer size) {
        this.name = name;
        this.actor = actor;
        this.album = album;
        this.length = length;
        this.size = size;
    }
}
