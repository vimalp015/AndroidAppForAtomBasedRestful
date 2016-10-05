package in.lamiv.android.newsfeedfromatomservice;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import in.lamiv.android.newsfeedfromatomservice.esport.GlobalVars;
import in.lamiv.android.newsfeedfromatomservice.esport.XMLPullParserHandler;
import in.lamiv.android.newsfeedfromatomservice.esport.eSportContent;

public class eSportsDetailsDisplayActivity extends AppCompatActivity {

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_TITLE = "item_title";
    public static final String ARG_SUMMARY = "item_summary";
    public static final String ARG_RIGHTS = "item_rights";
    public static final String ARG_ICON_URL = "item_image_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_sports_details_display);

        //set values to the controls to be displayed for the user
        ((TextView)findViewById(R.id.details_text)).setText(getIntent().getStringExtra(ARG_TITLE));
        ((TextView)findViewById(R.id.details_summary)).setText(getIntent().getStringExtra(ARG_SUMMARY));
        ((TextView)findViewById(R.id.details_rights)).setText(getIntent().getStringExtra(ARG_RIGHTS));

        if(getIntent().getStringExtra(ARG_ICON_URL) != null) {
            getImage();
        }
    }

    /**
     * Method to get image from the URL
     */
    public void getImage() {

        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getIntent().getStringExtra(ARG_ICON_URL), new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    ((ImageView) findViewById(R.id.imageView1))
                            .setImageBitmap(BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

}
