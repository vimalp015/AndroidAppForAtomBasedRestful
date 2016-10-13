package in.lamiv.android.newsfeedfromatomservice;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.lamiv.android.newsfeedfromatomservice.esport.GlobalVars;
import in.lamiv.android.newsfeedfromatomservice.esport.IndexFeed;

/**
 * A fragment representing a single eSport detail screen.
 * This fragment is either contained in a {@link ESportListActivity}
 * in two-pane mode (on tablets) or a {@link ESportDetailActivity}
 * on handsets.
 */
public class ESportDetailFragment extends Fragment {

    public ESportDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(GlobalVars.ARG_INDEX_FEED)) {
            IndexFeed indexFeed = (IndexFeed)getArguments().getParcelable(GlobalVars.ARG_INDEX_FEED);
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(GlobalVars.SOURCES_LIST +": " + indexFeed.getTitle());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.esport_detail, container, false);
    }
}
