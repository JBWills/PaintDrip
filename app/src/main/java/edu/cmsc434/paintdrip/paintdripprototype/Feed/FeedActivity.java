package edu.cmsc434.paintdrip.paintdripprototype.Feed;

import edu.cmsc434.paintdrip.paintdripprototype.MapsActivity;
import edu.cmsc434.paintdrip.paintdripprototype.R;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;


public class FeedActivity extends FragmentActivity implements PaintingListFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        Context context = getApplicationContext();

        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setAllCaps(true);
        tabs.setShouldExpand(true);
        tabs.setViewPager(pager);

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
