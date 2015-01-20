package edu.cmsc434.paintdrip.paintdripprototype.Feed;

import edu.cmsc434.paintdrip.paintdripprototype.MapsActivity;
import edu.cmsc434.paintdrip.paintdripprototype.ParseManager;
import edu.cmsc434.paintdrip.paintdripprototype.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

public class FeedActivity extends FragmentActivity implements
        PaintingListFragment.OnFragmentInteractionListener, FeedItemListener {

    private PagerSlidingTabStrip mPageTabs;
    private MyPagerAdapter mPageAdapter;
    private PagerSlidingTabStrip mTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // intialize Parse
        ParseObject.registerSubclass(Painting.class);
        Parse.initialize(this, "0JR6ZeP19ikuzEW4gGtQrNu8B1m5jukmjNZFwigF", "pX0frOadB8eHXmZignS2p7WOgTfOlKSHPHfWAjlN");

        if (ParseUser.getCurrentUser() == null) {
            // show login screen
            ParseLoginBuilder builder = new ParseLoginBuilder(FeedActivity.this);
            startActivityForResult(builder.build(), 0);
        }

        setContentView(R.layout.activity_feed);

        // Initialize the ViewPager and set an adapter
        mPageAdapter = new MyPagerAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(mPageAdapter);
        pager.setCurrentItem(0);

        // bind tabs to the ViewPager
        mPageTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mPageTabs.setAllCaps(true);
        mPageTabs.setShouldExpand(true);
        mPageTabs.setViewPager(pager);
        mPageTabs.setIndicatorColorResource(R.color.transparent_blue);
        Resources r = getResources();
        mPageTabs.setIndicatorHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.5f, r.getDisplayMetrics()));
        mPageTabs.setUnderlineHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, r.getDisplayMetrics()));
        mPageTabs.setDividerColorResource(R.color.white);
        mPageTabs.setTextSize((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13.5f, r.getDisplayMetrics()));
        mPageTabs.setOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateTabs(position);
                mPageAdapter.setCurrentPage(position);
            }
        });


        // set the actionbar title font
        int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
        if (actionBarTitleView != null){
            Typeface font = Typeface.createFromAsset(getAssets(), "fonts/GrandHotel-Regular.otf");
            actionBarTitleView.setTypeface(font);
        }

        updateTabs(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void updateTabs(int position) {
        LinearLayout tabsLayout = ((LinearLayout)mPageTabs.getChildAt(0));
        for(int i = 0; i < tabsLayout.getChildCount(); i++) {
            TextView tabText = (TextView) tabsLayout.getChildAt(i);
            if (i == position) {
                tabText.setTextAppearance(getApplicationContext(), R.style.SelectedTabBarText);
            } else {
                tabText.setTextAppearance(getApplicationContext(), R.style.DeselectedTabBarText);
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        mPageAdapter.updateFeed();
        mPageAdapter.scrollToTop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_paint) {
            startActivity(new Intent(this, MapsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    public void updatePaintings() {
        Log.i("JB", "Updating tabs");
        mPageAdapter.updateFeed();
    }

    public void menuClicked(final Painting painting) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedActivity.this);

        final String[] items = getApplicationContext().getResources()
                .getStringArray(R.array.dialog_your_drawing);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // TODO add share functionality
                } else if (which == 1) {
                    // TODO add edit functionality
                } else if (which == 2) {
                    // TODO add confirmation dialog
                    new ParseManager(getBaseContext()).deletePainting(painting, FeedActivity.this);
                }
            }
        }).show();
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = new String[3];

        int mCurrentPage = -1;

        PaintingListFragment[] mFragments;
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            Log.i("JB", "Creating PagerAdapter");

            TITLES[0] = "FRIENDS";
            TITLES[1] = "GLOBAL";
            TITLES[2] = "ME";

            mCurrentPage = 0;

            mFragments = new PaintingListFragment[3];
            mFragments[0] = PaintingListFragment.newInstance(PaintingListFragment.FRIENDS_FRAGMENT);
            mFragments[1] = PaintingListFragment.newInstance(PaintingListFragment.GLOBAL_FRAGMENT);
            mFragments[2] = PaintingListFragment.newInstance(PaintingListFragment.ME_FRAGMENT);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        public void updateFeed() {
            for (int i = 0; i < mFragments.length; i++) {
                mFragments[i].refresh();
            }
        }

        public void scrollToTop() {
            mFragments[mCurrentPage].scrollToTop();
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            Log.i("JB", "Getting fragment");
            return mFragments[position];
        }

        public void setCurrentPage(int position) {
            mCurrentPage = position;
        }
    }
}
