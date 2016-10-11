package in.lamiv.android.newsfeedfromatomservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//Android Asynchronous Http Client – An asynchronous callback-based Http client
// for Android built on top of Apache’s HttpClient libraries which is used by Pinterest, Instagram etc.,
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import cz.msebera.android.httpclient.Header;

import in.lamiv.android.newsfeedfromatomservice.esport.GlobalVars;
import in.lamiv.android.newsfeedfromatomservice.esport.HttpRequestHandler;
import in.lamiv.android.newsfeedfromatomservice.esport.IndexFeed;
import in.lamiv.android.newsfeedfromatomservice.esport.XMLPullParserHandler;
import in.lamiv.android.newsfeedfromatomservice.esport.eSportContent;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * An activity representing a list of eSports. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link eSportDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class eSportListActivity extends AppCompatActivity implements HttpRequestHandler.IHttpRequestHandler {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    // Progress Dialog Object
    ProgressDialog prgDialog;

    HttpRequestHandler httpRequestHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esport_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        this.setTitle(GlobalVars.ESPORTS_LIST);

        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage(GlobalVars.PLEASE_WAIT_MESSAGE);
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        //invoke feed request only if the required item is missing from our static object
        if (eSportContent.ITEMS == null || eSportContent.ITEMS.size() == 0) {
            prgDialog.show();
            httpRequestHandler = new HttpRequestHandler();
            httpRequestHandler.setListener(this);
            httpRequestHandler.invokeWS(eSportListActivity.this, GlobalVars.ENTRY_URL);
            httpRequestHandler = null;
        } else {
            View recyclerView = findViewById(R.id.esport_list);
            assert recyclerView != null;
            ((RecyclerView) recyclerView).setAdapter(new SimpleItemRecyclerViewAdapter(eSportContent.ITEMS));
            if (findViewById(R.id.esport_detail_container) != null) {
                mTwoPane = true;
            }
        }
    }

    public void refreshOnClick(View view) {
        prgDialog.show();
        httpRequestHandler = new HttpRequestHandler();
        httpRequestHandler.setListener(this);
        httpRequestHandler.invokeWS(eSportListActivity.this, GlobalVars.ENTRY_URL);
        httpRequestHandler = null;
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<eSportContent.eSportItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<eSportContent.eSportItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.esport_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
//            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).title);
            final IndexFeed indexFeed = new IndexFeed();
            indexFeed.setId(holder.mItem.id);
            indexFeed.setTitle(holder.mItem.title);
            indexFeed.setHref(holder.mItem.href);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable(GlobalVars.ARG_INDEX_FEED, indexFeed);
                        eSportDetailFragment fragment = new eSportDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.esport_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, eSportDetailActivity.class);
                        intent.putExtra(GlobalVars.ARG_INDEX_FEED, indexFeed);
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            //            public final TextView mIdView;
            public final TextView mContentView;
            public eSportContent.eSportItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
//                mIdView = (TextView) view.findViewById(R.id.id);
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
        List<eSportContent.eSportItem> items;
        try {
            InputStream inputStream = new ByteArrayInputStream(responseBody);
            items = new XMLPullParserHandler().parseIndexFeed(inputStream);
            eSportContent.addItems(items);
            inputStream.close();
            View recyclerView = findViewById(R.id.esport_list);
            assert recyclerView != null;
            ((RecyclerView) recyclerView).setAdapter(new SimpleItemRecyclerViewAdapter(items));
            if (findViewById(R.id.esport_detail_container) != null) {
                mTwoPane = true;
            }
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
    }
}
