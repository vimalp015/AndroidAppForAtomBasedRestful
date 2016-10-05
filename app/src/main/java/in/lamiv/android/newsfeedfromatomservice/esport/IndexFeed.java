package in.lamiv.android.newsfeedfromatomservice.esport;

/**
 * Created by vimal on 10/3/2016.
 * This class will be used for de-serializing list of eSports we receive with initial entry
 */

public class IndexFeed {

    private String id;
    private String title;
    private String href;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String toString() {
        return "Index feed item: { Id: " + id + ", Title: " + title +", Link: " + href + "}";
    }
}