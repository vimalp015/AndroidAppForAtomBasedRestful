package in.lamiv.android.newsfeedfromatomservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import java.lang.ref.WeakReference;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import in.lamiv.android.newsfeedfromatomservice.esport.DetailFeed;
import in.lamiv.android.newsfeedfromatomservice.esport.GlobalVars;
import in.lamiv.android.newsfeedfromatomservice.esport.HttpRequestHandler;
import in.lamiv.android.newsfeedfromatomservice.esport.IndexFeed;
import in.lamiv.android.newsfeedfromatomservice.esport.XMLPullParserHandler;
import in.lamiv.android.newsfeedfromatomservice.esport.ESportContent;

/**
 * An activity representing a single eSport detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ESportListActivity}.
 */
public class ESportDetailActivity extends AppCompatActivity implements HttpRequestHandler.IHttpRequestHandler {

    // Progress Dialog Object
    private ProgressDialog prgDialog;
    //var to hold values passed from eSports list on selection
    private IndexFeed _indexFeed = new IndexFeed();
    private HttpRequestHandler httpRequestHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esport_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        Bundle data = getIntent().getExtras();
        if(data.containsKey(GlobalVars.ARG_INDEX_FEED)) {
            _indexFeed = (IndexFeed) data.getParcelable(GlobalVars.ARG_INDEX_FEED);

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

            this.setTitle(GlobalVars.SOURCES_LIST + ": " + _indexFeed.getTitle());

            if (savedInstanceState == null) {
                // Create the detail fragment and add it to the activity
                // using a fragment transaction. You need the below params to fetch feed with the ID
                //and to display title on the screen
                Bundle arguments = new Bundle();
                arguments.putParcelable(GlobalVars.ARG_INDEX_FEED, _indexFeed);
                ESportDetailFragment fragment = new ESportDetailFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.esport_detail_container, fragment)
                        .commit();
            }

            //verify if the eSport details to be fetched is already in our static object
            if (ESportContent.HASH.containsKey(_indexFeed.getId())
                    && ESportContent.HASH.get(_indexFeed.getId()).details != null) {
                InputStream inputStream = new ByteArrayInputStream(ESportContent.HASH.get(_indexFeed.getId()).details);
                List<DetailFeed> items = new XMLPullParserHandler().parseDetailFeed(inputStream);
                View recyclerView = findViewById(R.id.esport_list);
                assert recyclerView != null;
                ((RecyclerView) recyclerView).setAdapter(new ESportDetailActivity.SimpleItemRecyclerViewAdapter(items));
            } else {
                prgDialog.show();
                httpRequestHandler = new HttpRequestHandler();
                httpRequestHandler.setListener(this);
                httpRequestHandler.invokeWS(new WeakReference<AppCompatActivity>(ESportDetailActivity.this),
                        _indexFeed.getHref());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, ESportListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Load the values fetched from REST service to UI
    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<ESportDetailActivity.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DetailFeed> mValues;

        public SimpleItemRecyclerViewAdapter(List<DetailFeed> items) {
            mValues = items;
        }

        @Override
        public ESportDetailActivity.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.esport_list_content, parent, false);
            return new ESportDetailActivity.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ESportDetailActivity.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).getText());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ESportsDetailsDisplayActivity.class);
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

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        // Hide Progress Dialog
        prgDialog.hide();
        List<DetailFeed> items;
        try {
            InputStream inputStream = new ByteArrayInputStream(responseBody);
            items = new XMLPullParserHandler().parseDetailFeed(inputStream);
            inputStream.close();
            View recyclerView = findViewById(R.id.esport_list);
            assert recyclerView != null;
            ((RecyclerView) recyclerView).setAdapter(new ESportDetailActivity.SimpleItemRecyclerViewAdapter(items));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //add the result to our static object for any further access
        ESportContent.addItem(new ESportContent.eSportItem(_indexFeed.getId(), _indexFeed.getTitle(),
                _indexFeed.getHref(), responseBody));
        httpRequestHandler = null;
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        // Hide Progress Dialog
        prgDialog.hide();
        httpRequestHandler = null;
    }
    
}
