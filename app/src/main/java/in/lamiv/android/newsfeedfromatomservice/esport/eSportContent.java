package in.lamiv.android.newsfeedfromatomservice.esport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class eSportContent {

    /**
     * An array of sample (esport) items.
     */
    public static final List<eSportItem> ITEMS = new ArrayList<eSportItem>();

    /**
     * A map of sample (esport) items, by ID.
     */
    public static final Map<String, eSportItem> ITEM_MAP = new HashMap<String, eSportItem>();

//    private static final int COUNT = 25;
//
//    static {
//        // Add some sample items.
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createDummyItem(i));
//        }
//    }

    private static void addItem(eSportItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

//    private static eSportItem createDummyItem(int position) {
//        return new eSportItem(String.valueOf(position), "Item " + position, makeDetails(position));
//    }

    private static String makeDetails(int position) {

        //{
//        XMLPullParserHandler xmlPullParserHandler = new XMLPullParserHandler();
//        xmlPullParserHandler.fetchIndexFeed("http://feed.esportsreader.com/reader/sports");
        //}

        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A esport item representing a piece of content.
     */
    public static class eSportItem {
        public final String id;
        public final String title;
        public final String href;

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
