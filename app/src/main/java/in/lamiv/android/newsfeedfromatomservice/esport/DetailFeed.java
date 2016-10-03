package in.lamiv.android.newsfeedfromatomservice.esport;

import java.util.Date;

/**
 * Created by vimal on 10/3/2016.
 */

public class DetailFeed {

    private String id;
    private String text;
    private Date upated;
    private String summary;
    private String link;
    private String related;
    private String iconURL;

    private String rights;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getUpated() {
        return upated;
    }

    public void setUpated(Date upated) {
        this.upated = upated;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getRelated() {
        return related;
    }

    public void setRelated(String related) {
        this.related = related;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    @Override
    public String toString() {
        return "Detail feed item: { Id: " + id + ", Text: " + text +", Summary: " + summary + "}";
    }
}

