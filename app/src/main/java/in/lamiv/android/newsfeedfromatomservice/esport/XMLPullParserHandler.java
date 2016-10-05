package in.lamiv.android.newsfeedfromatomservice.esport;

/**
 * Created by vimal on 10/3/2016.
 * This will contain all the parser methods required to parse the feeds
 */

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;


public class XMLPullParserHandler {

    private IndexFeed eSport;
    private DetailFeed detailFeed;

    //method to parse initial atom feed with list of all eSports and link to it
    public List<eSportContent.eSportItem> parseIndexFeed(InputStream is) {
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
        eSportContent.ITEMS = new ArrayList<eSportContent.eSportItem>();

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
                                    (eSport.getId(),eSport.getTitle(),eSport.getHref(), null));
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

    //method to parse atom feed on selected eSports detail
    public List<DetailFeed> parseDetailFeed(InputStream is) {
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
        List<DetailFeed> detailFeedsList = new ArrayList<DetailFeed>();
        String text = null;

        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            detailFeed = new DetailFeed();

            parser = factory.newPullParser();
            parser.setInput(is, null);
            int eventType = parser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch(eventType) {
                    case XmlPullParser.START_TAG:
                        if(tagname.equalsIgnoreCase("entry")){
                            detailFeed = new DetailFeed();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if(tagname.equalsIgnoreCase("entry")) {
                            detailFeedsList.add(detailFeed);
                        } else if (tagname.equalsIgnoreCase("id")) {
                            detailFeed.setId(text);
                        } else if (tagname.equalsIgnoreCase("title")) {
                            detailFeed.setText(text);
                        } else if (tagname.equalsIgnoreCase("summary")) {
                            detailFeed.setSummary(text);
                        } else if (tagname.equalsIgnoreCase("link")) {
                            if(parser.getAttributeValue(null, "rel").equalsIgnoreCase("via"))
                                detailFeed.setLink(parser.getAttributeValue(null, "href"));
                            else if(parser.getAttributeValue(null, "rel").equalsIgnoreCase("related"))
                                detailFeed.setRelated(parser.getAttributeValue(null, "href"));
                            else if(parser.getAttributeValue(null, "rel").equalsIgnoreCase("icon"))
                                detailFeed.setIconURL(parser.getAttributeValue(null, "href"));
                        } else if (tagname.equalsIgnoreCase("rights")) {
                            detailFeed.setRights(text);
                        } else if (tagname.equalsIgnoreCase("updated")) {
                            detailFeed.setUpdated(Helpers.ParseDateFromFeed(text));
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
        return detailFeedsList;
    }
}
