package mmss.musicco.dataobjects;

/**
 * Created by User on 12.10.2016.
 */

public class Album {
    public String name;
    public String actor;
    public String tracksCount;

    public Album() {

    }

    public Album(String name, String actor, String tracksCount) {
        this.name = name;
        this.actor = actor;
        this.tracksCount = tracksCount;
    }
}
