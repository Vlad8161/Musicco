package mmss.musicco.dataobjects;

/**
 * Created by User on 12.10.2016.
 */

public class Track {
    public String name;
    public String artist;
    public String album;
    public Integer length;
    public Long size;
    public String url;

    public Track() {

    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Track)) {
            return false;
        }

        Track t = (Track) obj;
        return url != null && t.url != null && t.url.equals(url);
    }
}
