package in.lamiv.android.newsfeedfromatomservice;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.lamiv.android.newsfeedfromatomservice.esport.eSportContent;

/**
 * A fragment representing a single eSport detail screen.
 * This fragment is either contained in a {@link eSportListActivity}
 * in two-pane mode (on tablets) or a {@link eSportDetailActivity}
 * on handsets.
 */
public class eSportDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_TITLE = "item_title";
    public static final String ARG_ITEM_HREF = "item_href";

    /**
     * The esport content this fragment is presenting.
     */
    private eSportContent.eSportItem mItem;

    public eSportDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = eSportContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(getArguments().get(ARG_ITEM_TITLE).toString());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.esport_detail, container, false);

        // Show the esport content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.esport_detail)).setText(mItem.href);
        }

        return rootView;
    }
}
