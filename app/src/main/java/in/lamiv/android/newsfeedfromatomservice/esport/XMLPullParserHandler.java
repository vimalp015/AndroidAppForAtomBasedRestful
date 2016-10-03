package in.lamiv.android.newsfeedfromatomservice.esport;

/**
 * Created by vimal on 10/3/2016.
 */

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class XMLPullParserHandler {

    private IndexFeed eSport;
    private String urlString;

    public List<eSportContent.eSportItem> parseIndexFeed(InputStream is) {
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;

        String text = null;

        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            eSport = new IndexFeed();

            parser = factory.newPullParser();
            parser.setInput(is, null);
            int eventType = parser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch(eventType) {
                    case XmlPullParser.START_TAG:
                        if(tagname.equalsIgnoreCase("collection")){
                            eSport.setHref(parser.getAttributeValue(null, "href"));
                        }
                        break;
                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if(tagname.equalsIgnoreCase("collection")) {
                            eSportContent.ITEMS.add(new eSportContent.eSportItem
                                    (eSport.getId(),eSport.getTitle(),eSport.getHref()));
                        } else if (tagname.equalsIgnoreCase("id")) {
                            eSport.setId(text);
                        } else if (tagname.equalsIgnoreCase("title")) {
                            eSport.setTitle(text);
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eSportContent.ITEMS;
    }

    public void fetchIndexFeed(String _urlString) {
        urlString = _urlString;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connect = (HttpURLConnection) url.openConnection();
                    int responseCode = connect.getResponseCode();
                    connect.setReadTimeout(10000);
                    connect.setConnectTimeout(15000);
                    //connect.setRequestMethod("GET");
                    //connect.setDoInput(true);
                    connect.connect();

                    InputStream inputStream = connect.getInputStream();
                    parseIndexFeed(inputStream);
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
