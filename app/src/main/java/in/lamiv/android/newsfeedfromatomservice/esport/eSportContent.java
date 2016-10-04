package in.lamiv.android.newsfeedfromatomservice.esport;

import java.util.ArrayList;
import java.util.List;

public class eSportContent {

    public static List<eSportItem> ITEMS = new ArrayList<eSportItem>();

    /**
     * A esport item representing a piece of content.
     */
    public static class eSportItem {
        public String id;
        public String title;
        public String href;

        public eSportItem(String id, String title, String href) {
            this.id = id;
            this.title = title;
            this.href = href;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
