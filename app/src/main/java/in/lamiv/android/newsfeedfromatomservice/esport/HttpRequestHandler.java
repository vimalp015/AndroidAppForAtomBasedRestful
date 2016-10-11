package in.lamiv.android.newsfeedfromatomservice.esport;

import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import in.lamiv.android.newsfeedfromatomservice.R;

/**
 * Created by vimal on 10/11/2016.
 */

public class HttpRequestHandler {
    IHttpRequestHandler iHttpRequestHandler;

    public interface IHttpRequestHandler {
        void onSuccess(int statusCode, Header[] headers, byte[] responseBody);
        void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error);
    }

    public void setListener(IHttpRequestHandler iHttpRequestHandler) {
        this.iHttpRequestHandler = iHttpRequestHandler;
    }

    public IHttpRequestHandler getListener() {
        return iHttpRequestHandler;

    }

    /**
     * Method that performs RESTful webservice invocations
     */
    public void invokeWS(final AppCompatActivity activity, String url) {
        String stringURL = url;
        final AppCompatActivity _activity = activity;

        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(stringURL, new AsyncHttpResponseHandler() {
            List<DetailFeed> items;

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                getListener().onSuccess(statusCode, headers, responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //show an alert to the user about the request failure
                new AlertDialog.Builder(_activity)
                        .setTitle(GlobalVars.ALERT_TITLE)
                        .setMessage(GlobalVars.ALERT_MESSAGE_SERVER_CON_ISSUE)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create().show();
                getListener().onFailure(statusCode,headers,responseBody,error);
            }
        });
    }

    public void getImageFromURL(String url) {
        final String _url = url;

        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        HttpEntity entity = null;
        client.get(_url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                getListener().onSuccess(statusCode, headers, responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                getListener().onFailure(statusCode,headers,responseBody,error);
            }
        });
    }
}
