package in.lamiv.android.newsfeedfromatomservice.esport;
/**
 * Created by vimal on 10/3/2016.
 * This class will be used for storing the results we fetched from server
 * It will save us from making multiple requests while storing the data until
 * it is garbage collected
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ESportContent {

    public static List<eSportItem> ITEMS = new ArrayList<eSportItem>();
    public static HashMap<String, eSportItem> HASH = new HashMap<>();

    public static void addItems(List<eSportItem> items) {
        HASH = new HashMap<>();
        for (eSportItem item:items) {
            HASH.put(item.id, item);
        }
    }

    public static void addItem(eSportItem item){
        if(HASH.containsKey(item.id)) {
            //HASH.replace works only with API 24 and above
            HASH.remove(item.id);
            HASH.put(item.id, item);
        }
    }

    /**
     * A esport item representing a piece of content.
     */
    public static class eSportItem {
        public String id;
        public String title;
        public String href;
        public byte[] details; //details of the eSport that we fetch in the second screen of the app

        public eSportItem(String id, String title, String href, byte[] details) {
            this.id = id;
            this.title = title;
            this.href = href;
            this.details = details;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
