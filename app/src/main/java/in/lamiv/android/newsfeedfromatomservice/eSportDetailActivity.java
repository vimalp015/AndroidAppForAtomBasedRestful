package in.lamiv.android.newsfeedfromatomservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

//Android Asynchronous Http Client – An asynchronous callback-based Http client
// for Android built on top of Apache’s HttpClient libraries which is used by Pinterest, Instagram etc.,
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import cz.msebera.android.httpclient.Header;

import in.lamiv.android.newsfeedfromatomservice.esport.DetailFeed;
import in.lamiv.android.newsfeedfromatomservice.esport.GlobalVars;
import in.lamiv.android.newsfeedfromatomservice.esport.IndexFeed;
import in.lamiv.android.newsfeedfromatomservice.esport.XMLPullParserHandler;
import in.lamiv.android.newsfeedfromatomservice.esport.eSportContent;

/**
 * An activity representing a single eSport detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link eSportListActivity}.
 */
public class eSportDetailActivity extends AppCompatActivity {

    // Progress Dialog Object
    ProgressDialog prgDialog;
    //var to hold values passed from eSports list on selection
    IndexFeed _indexFeed = new IndexFeed();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esport_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage(GlobalVars.PLEASE_WAIT_MESSAGE);
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        Bundle data = getIntent().getExtras();
        if(data.containsKey(GlobalVars.ARG_INDEX_FEED)) {
            _indexFeed = (IndexFeed) data.getParcelable(GlobalVars.ARG_INDEX_FEED);
        }

        this.setTitle(GlobalVars.SOURCES_LIST +": " + _indexFeed.getTitle());

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction. You need the below params to fetch feed with the ID
            //and to display title on the screen
            Bundle arguments = new Bundle();
            arguments.putParcelable(GlobalVars.ARG_INDEX_FEED, _indexFeed);
            eSportDetailFragment fragment = new eSportDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.esport_detail_container, fragment)
                    .commit();
        }

        //verify if the eSport details to be fetched is already in our static object
        if(eSportContent.HASH.containsKey(_indexFeed.getId())
                && eSportContent.HASH.get(_indexFeed.getId()).details != null) {
            InputStream inputStream = new ByteArrayInputStream(eSportContent.HASH.get(_indexFeed.getId()).details);
            List<DetailFeed> items = new XMLPullParserHandler().parseDetailFeed(inputStream);
            View recyclerView = findViewById(R.id.esport_list);
            assert recyclerView != null;
            ((RecyclerView) recyclerView).setAdapter(new eSportDetailActivity.SimpleItemRecyclerViewAdapter(items));
        }
        else {
            invokeWS();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, eSportListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<eSportDetailActivity.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DetailFeed> mValues;

        public SimpleItemRecyclerViewAdapter(List<DetailFeed> items) {
            mValues = items;
        }

        @Override
        public eSportDetailActivity.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.esport_list_content, parent, false);
            return new eSportDetailActivity.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final eSportDetailActivity.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).getText());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, eSportsDetailsDisplayActivity.class);
                    intent.putExtra(GlobalVars.ARG_DETAILS_FEED, holder.mItem);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mContentView;
            public DetailFeed mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    /**
     * Method that performs RESTful webservice invocations
     */
    public void invokeWS() {
        // Show Progress Dialog
        prgDialog.show();
        String stringURL = _indexFeed.getHref();

        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(stringURL, new AsyncHttpResponseHandler() {
            List<DetailFeed> items;

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    InputStream inputStream = new ByteArrayInputStream(responseBody);
                    items = new XMLPullParserHandler().parseDetailFeed(inputStream);
                    inputStream.close();
                    View recyclerView = findViewById(R.id.esport_list);
                    assert recyclerView != null;
                    ((RecyclerView) recyclerView).setAdapter(new eSportDetailActivity.SimpleItemRecyclerViewAdapter(items));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Hide Progress Dialog
                prgDialog.hide();
                //add the result to our static object for any further access
                eSportContent.addItem(new eSportContent.eSportItem(_indexFeed.getId(), _indexFeed.getTitle(),
                        _indexFeed.getHref(), responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Hide Progress Dialog
                prgDialog.hide();
                //show an alert to the user about the request failure
                new AlertDialog.Builder(eSportDetailActivity.this)
                        .setTitle(GlobalVars.ALERT_TITLE)
                        .setMessage(GlobalVars.ALERT_MESSAGE_SERVER_CON_ISSUE)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create().show();
            }
        });
    }
    
}
