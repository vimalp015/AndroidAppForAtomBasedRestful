package in.lamiv.android.newsfeedfromatomservice;

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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import in.lamiv.android.newsfeedfromatomservice.esport.DetailFeed;
import in.lamiv.android.newsfeedfromatomservice.esport.GlobalVars;
import in.lamiv.android.newsfeedfromatomservice.esport.XMLPullParserHandler;

/**
 * An activity representing a single eSport detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link eSportListActivity}.
 */
public class eSportDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esport_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

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

        new LoadeSportsAsyncTask().execute();
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
//            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).getText());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, eSportDetailActivity.class);
                    intent.putExtra(eSportDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
                    intent.putExtra(eSportDetailFragment.ARG_ITEM_TITLE, holder.mItem.getText());
                    intent.putExtra(eSportDetailFragment.ARG_ITEM_HREF, holder.mItem.getIconURL());
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
            //            public final TextView mIdView;
            public final TextView mContentView;
            public DetailFeed mItem;

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

    private class LoadeSportsAsyncTask extends AsyncTask<Void,Void,Void> {

        InputStream inputStream;
        List<DetailFeed> items;
        String stringURL = getIntent().getStringExtra(eSportDetailFragment.ARG_ITEM_HREF);
        int responseCode;
        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(stringURL);
                HttpURLConnection connect = (HttpURLConnection) url.openConnection();
                connect.setRequestMethod("GET");
                connect.setReadTimeout(10000);
                connect.setConnectTimeout(15000);
                connect.connect();
                responseCode = connect.getResponseCode();

                inputStream = connect.getInputStream();
                items = new XMLPullParserHandler().parseDetailFeed(inputStream);
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(responseCode != 200) {
                new AlertDialog.Builder(eSportDetailActivity.this)
                        .setTitle("eSport Details")
                        .setMessage("Server not reachable, please try later.")
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create().show();
            }
            else {
                View recyclerView = findViewById(R.id.esport_list);
                assert recyclerView != null;
                ((RecyclerView) recyclerView).setAdapter(new eSportDetailActivity.SimpleItemRecyclerViewAdapter(items));
            }
        }
    }

}
