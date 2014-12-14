package edu.cmsc434.paintdrip.paintdripprototype.Feed;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import edu.cmsc434.paintdrip.paintdripprototype.R;

/**
 * A fragment representing a list of FeedItems.
 */
public class PaintingListFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final int FRIENDS_FRAGMENT = 0;
    private static final int GLOBAL_FRAGMENT = 1;
    private static final int ME_FRAGMENT = 2;

    private static final String ARG_ID = "id";

    private int feedID;

    private OnFragmentInteractionListener mListener;
    private AbsListView mListView;
    private PaintingListAdapter mAdapter;

    public static PaintingListFragment newInstance(int feedID) {
        PaintingListFragment fragment = new PaintingListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, feedID);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PaintingListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            feedID = getArguments().getInt(ARG_ID);
        }

        FeedItemDummy f = new FeedItemDummy(getActivity().getApplicationContext());

        mAdapter = new PaintingListAdapter(getActivity().getApplicationContext(), feedID);

        /*
        if (feedID == FRIENDS_FRAGMENT) {
            //mAdapter.setList(f.getGlobalOrFeedItems(30));
        } else if (feedID == GLOBAL_FRAGMENT) {
           // mAdapter.setList(f.getGlobalOrFeedItems(30));
        } else if (feedID == ME_FRAGMENT) {
            // ParseManager getListOfPaintingsForUser(
            //mAdapter.setList(f.getMeItems(30));
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feeditem, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
         mListener = (OnFragmentInteractionListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(mAdapter.getItemId(position) + "");
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }


}
