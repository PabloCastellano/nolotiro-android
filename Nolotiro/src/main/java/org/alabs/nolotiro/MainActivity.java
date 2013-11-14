package org.alabs.nolotiro;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.app.ActionBar.Tab;

public class MainActivity extends ActionBarActivity {

    private static final String GIVES_TAG = "gives";
    private static final String WANTS_TAG = "wants";
    private ActionBar actionBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Tab tab = actionBar.newTab()
                .setText(R.string.gives)
                .setTabListener(new TabListener<AdsFragment>(this, GIVES_TAG, AdsFragment.class));
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setText(R.string.wants)
                .setTabListener(new TabListener<AdsFragment>(this, WANTS_TAG, AdsFragment.class));
        actionBar.addTab(tab);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                //AdsFragment fragment
                //ActionBar.Tab t = actionBar.getSelectedTab();
                //fragment.refreshAds();
                // FIXME: getSupportFragmentManager() doesn't have findFragmentByTag() method
                //Fragment f = this.getFragmentManager().findFragmentByTag(GIVES_TAG);
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private Fragment mFragment;
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;

        public TabListener(Activity activity, String tag, Class<T> clz) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            if (mFragment == null) {
                mFragment = Fragment.instantiate(mActivity, mClass.getName());
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                ft.attach(mFragment);
            }
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                ft.detach(mFragment);
                Log.i("ActionBar", tab.getText() + " deseleccionada.");
            }
        }

        public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {

        }

        public Fragment getFragment() {
            return mFragment;
        }

    }

}
