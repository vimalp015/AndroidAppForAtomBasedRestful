package in.lamiv.android.newsfeedfromatomservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import in.lamiv.android.newsfeedfromatomservice.esport.DetailFeed;
import in.lamiv.android.newsfeedfromatomservice.esport.GlobalVars;
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

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(eSportDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(eSportDetailFragment.ARG_ITEM_ID));
            arguments.putString(eSportDetailFragment.ARG_ITEM_TITLE,
                    getIntent().getStringExtra(eSportDetailFragment.ARG_ITEM_TITLE));
            arguments.putString(eSportDetailFragment.ARG_ITEM_HREF,
                    getIntent().getStringExtra(eSportDetailFragment.ARG_ITEM_HREF));
            eSportDetailFragment fragment = new eSportDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.esport_detail_container, fragment)
                    .commit();
        }

        invokeWS();
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
                    intent.putExtra(eSportsDetailsDisplayActivity.ARG_ITEM_ID, holder.mItem.getId());
                    intent.putExtra(eSportsDetailsDisplayActivity.ARG_TITLE, holder.mItem.getText());
                    intent.putExtra(eSportsDetailsDisplayActivity.ARG_SUMMARY, holder.mItem.getSummary());
                    intent.putExtra(eSportsDetailsDisplayActivity.ARG_RIGHTS, holder.mItem.getRights());
                    intent.putExtra(eSportsDetailsDisplayActivity.ARG_ICON_URL, holder.mItem.getIconURL());
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
        String stringURL = getIntent().getStringExtra(eSportDetailFragment.ARG_ITEM_HREF);

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
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Hide Progress Dialog
                prgDialog.hide();
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
