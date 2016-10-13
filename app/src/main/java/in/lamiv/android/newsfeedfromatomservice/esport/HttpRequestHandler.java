package in.lamiv.android.newsfeedfromatomservice.esport;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.lang.ref.WeakReference;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

/**
 * Created by vimal on 10/11/2016.
 * This class is used to invoke web service and provides two delegate methods
 * for passing onSuccess and onFailure callbacks
 */

public class HttpRequestHandler {
    IHttpRequestHandler iHttpRequestHandler;

    //Delegate for callback methods
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

    public HttpRequestHandler() {

    }

    /**
     * Method that performs RESTful webservice invocations
     * Pass WeakReference to avoid any memory leak
     */
    public void invokeWS(final WeakReference<AppCompatActivity> activity, String url) {
        String stringURL = url;
        //AppCompatActivity _activity = activity;

        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(stringURL, new AsyncHttpResponseHandler() {
            List<DetailFeed> items;

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(getListener() != null)
                    getListener().onSuccess(statusCode, headers, responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //show an alert to the user about the request failure
                if(getListener() != null && activity.get() != null) {
                    new AlertDialog.Builder(activity.get())
                            .setTitle(GlobalVars.ALERT_TITLE)
                            .setMessage(GlobalVars.ALERT_MESSAGE_SERVER_CON_ISSUE)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create().show();
                    getListener().onFailure(statusCode, headers, responseBody, error);
                }
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
                if(getListener() != null)
                    getListener().onSuccess(statusCode, headers, responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(getListener() != null)
                    getListener().onFailure(statusCode,headers,responseBody,error);
            }
        });
    }
}
