package in.lamiv.android.newsfeedfromatomservice;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import in.lamiv.android.newsfeedfromatomservice.esport.DetailFeed;
import in.lamiv.android.newsfeedfromatomservice.esport.GlobalVars;
import in.lamiv.android.newsfeedfromatomservice.esport.Helpers;
import in.lamiv.android.newsfeedfromatomservice.esport.HttpRequestHandler;

public class eSportsDetailsDisplayActivity extends AppCompatActivity implements HttpRequestHandler.IHttpRequestHandler{

    private DetailFeed _detailFeed = new DetailFeed();
    HttpRequestHandler httpRequestHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_sports_details_display);

        Bundle data = getIntent().getExtras();
        if(data.containsKey(GlobalVars.ARG_DETAILS_FEED)) {
            _detailFeed = (DetailFeed) data.getParcelable(GlobalVars.ARG_DETAILS_FEED);
        }

        //set the title of the screen to the eSport title selected from the list
        setTitle(GlobalVars.ENTRIES + ": " + _detailFeed.getText());

        //set values to the controls to be displayed for the user
        ((TextView)findViewById(R.id.details_text)).setText(_detailFeed.getText());
        ((TextView)findViewById(R.id.details_summary)).setText(_detailFeed.getSummary());
        ((TextView)findViewById(R.id.details_rights)).setText(_detailFeed.getRights());
        ((TextView)findViewById(R.id.details_updated)).setText(Helpers.ParseDateToStringForDisplay
                (_detailFeed.getUpdated()));

        if(_detailFeed.getIconURL() != null) {
            httpRequestHandler = new HttpRequestHandler();
            httpRequestHandler.setListener(this);
            httpRequestHandler.getImageFromURL(_detailFeed.getIconURL());
        }
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        try {
            ((ImageView) findViewById(R.id.imageViewIcon))
                    .setImageBitmap(BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
    }

}
