package in.lamiv.android.newsfeedfromatomservice.esport;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vimal on 10/3/2016.
 * This class will be used for de-serializing list of eSports we receive with initial entry
 */

public class IndexFeed implements Parcelable{

    public IndexFeed() {

    }

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

    //implementation for parcelable
    //{

    public IndexFeed(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        this.id = data[0];
        this.title = data[1];
        this.href = data[2];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.id,
                this.title,
                this.href});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public IndexFeed createFromParcel(Parcel in) {
            return new IndexFeed(in);
        }

        public IndexFeed[] newArray(int size) {
            return new IndexFeed[size];
        }
    };

    //}
}