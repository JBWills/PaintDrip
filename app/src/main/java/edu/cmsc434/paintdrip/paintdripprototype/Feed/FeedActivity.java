package edu.cmsc434.paintdrip.paintdripprototype.Feed;

import edu.cmsc434.paintdrip.paintdripprototype.MapsActivity;
import edu.cmsc434.paintdrip.paintdripprototype.Paint.*;
import edu.cmsc434.paintdrip.paintdripprototype.ParseManager;
import edu.cmsc434.paintdrip.paintdripprototype.R;
import edu.cmsc434.paintdrip.paintdripprototype.Share.ShareActivity;

import android.content.Context;
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
        PaintingListFragment.OnFragmentInteractionListener {

    private PagerSlidingTabStrip mPageTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // intialize Parse
        ParseObject.registerSubclass(Painting.class);
        Parse.initialize(this, "0JR6ZeP19ikuzEW4gGtQrNu8B1m5jukmjNZFwigF", "pX0frOadB8eHXmZignS2p7WOgTfOlKSHPHfWAjlN");

        // show login screen
        ParseLoginBuilder builder = new ParseLoginBuilder(FeedActivity.this);
        startActivityForResult(builder.build(), 0);
        setContentView(R.layout.activity_feed);

        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.setCurrentItem(0);

        // Bind the tabs to the ViewPager
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
            }
        });

        updateTabs(0);

        int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/GrandHotel-Regular.otf");
        if(actionBarTitleView != null){
            actionBarTitleView.setTypeface(font);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0) {
            if(ParseUser.getCurrentUser() != null) {
                Context context = getApplicationContext();
                ParseManager parseManager = new ParseManager(context);
                parseManager.uploadDummyImages();
            }
        }
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

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = new String[3];

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            Log.i("JB", "Creating PagerAdapter");

            TITLES[0] = getApplicationContext().getString(R.string.tab_title_friends);
            TITLES[1] = getApplicationContext().getString(R.string.tab_title_global);
            TITLES[2] = getApplicationContext().getString(R.string.tab_title_me);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            Log.i("JB", "Getting fragment");
            return PaintingListFragment.newInstance(position);
        }

    }
}
