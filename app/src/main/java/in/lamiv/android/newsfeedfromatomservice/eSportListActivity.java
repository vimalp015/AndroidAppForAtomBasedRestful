package in.lamiv.android.newsfeedfromatomservice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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


import in.lamiv.android.newsfeedfromatomservice.esport.GlobalVars;
import in.lamiv.android.newsfeedfromatomservice.esport.XMLPullParserHandler;
import in.lamiv.android.newsfeedfromatomservice.esport.eSportContent;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * An activity representing a list of eSports. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link eSportDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class eSportListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esport_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if(eSportContent.ITEMS == null || eSportContent.ITEMS.size() == 0) {
            new LoadeSportsAsyncTask().execute();
        }
        else {
            View recyclerView = findViewById(R.id.esport_list);
            assert recyclerView != null;
            ((RecyclerView) recyclerView).setAdapter(new SimpleItemRecyclerViewAdapter(eSportContent.ITEMS));
            if (findViewById(R.id.esport_detail_container) != null) {
                mTwoPane = true;
            }
        }
    }

    public void refreshOnClick(View view) {
        new LoadeSportsAsyncTask().execute();
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

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(eSportDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        arguments.putString(eSportDetailFragment.ARG_ITEM_TITLE, holder.mItem.title);
                        arguments.putString(eSportDetailFragment.ARG_ITEM_HREF, holder.mItem.href);
                        eSportDetailFragment fragment = new eSportDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.esport_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, eSportDetailActivity.class);
                        intent.putExtra(eSportDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        intent.putExtra(eSportDetailFragment.ARG_ITEM_TITLE, holder.mItem.title);
                        intent.putExtra(eSportDetailFragment.ARG_ITEM_HREF, holder.mItem.href);
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

    private class LoadeSportsAsyncTask extends AsyncTask<Void,Void,Void>{

        InputStream inputStream;
        List<eSportContent.eSportItem> items;
        int responseCode;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(GlobalVars.ENTRY_URL);
                HttpURLConnection connect = (HttpURLConnection) url.openConnection();
                connect.setReadTimeout(GlobalVars.HTTP_READ_TIMEOUT);
                connect.setConnectTimeout(GlobalVars.HTTP_CONNECT_TIMEOUT);
                connect.connect();
                responseCode = connect.getResponseCode();

                inputStream = connect.getInputStream();
                items = new XMLPullParserHandler().parseIndexFeed(inputStream);
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(responseCode != 200) {
                new AlertDialog.Builder(eSportListActivity.this)
                        .setTitle(GlobalVars.ALERT_TITLE)
                        .setMessage(GlobalVars.ALERT_MESSAGE_SERVER_CON_ISSUE)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create().show();
            }
            else {
                View recyclerView = findViewById(R.id.esport_list);
                assert recyclerView != null;
                ((RecyclerView) recyclerView).setAdapter(new SimpleItemRecyclerViewAdapter(items));
                if (findViewById(R.id.esport_detail_container) != null) {
                    mTwoPane = true;
                }
            }
        }
    }
}
