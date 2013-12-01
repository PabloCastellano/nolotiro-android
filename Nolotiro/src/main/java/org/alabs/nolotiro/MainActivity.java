/*
 * Copyright (c) 2013 "Pablo Castellano <pablo@anche.no>"
 * Copyright (c) 2013 "Eugenio Cano-Manuel Mendoza <eugeniocanom@gmail.com>"
 * Nolotiro App [http://nolotiro.org]
 *
 * This file is part of nolotiro-android.
 *
 * nolotiro-android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.alabs.nolotiro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;

import android.support.v7.app.ActionBar.Tab;
import android.widget.Toast;

import org.alabs.nolotiro.db.DbAdapter;
import org.alabs.nolotiro.dialogs.ChangeLocationDialogFragment;
import org.alabs.nolotiro.dialogs.ChooseLocationDialogFragment;
import org.alabs.nolotiro.dialogs.FindLocationDialogFragment;

public class MainActivity extends ActionBarActivity implements ChangeLocationDialogFragment.ChangeLocationDialogListener,
        FindLocationDialogFragment.FindLocationDialogListener, ChooseLocationDialogFragment.ChooseLocationDialogListener {

    private static final String TAG = "MainActivity";
    private static final String GIVES_TAG = "gives";
    private static final String WANTS_TAG = "wants";
    private ActionBar actionBar;
    private Woeid currentWoeid;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int id = sharedPref.getInt("current_woeid", Utils.DEBUG_WOEID);
        Log.i(TAG, "current_woeid: " + id);
        DbAdapter dba = new DbAdapter(this);
        dba.openToRead();
        currentWoeid = dba.getWoeid(id);
        Log.i(TAG, "woeid= " + currentWoeid);
        dba.close();

        // FIXME: Bootstrap
        if (currentWoeid == null) {
            currentWoeid = new Woeid(Utils.DEBUG_WOEID, "Málaga", "Andalucía", "España");
            dba.openToWrite();
            dba.insertWoeid(currentWoeid);
            dba.close();
        }

        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        updateTitle();

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
        Intent intent;
        switch (item.getItemId()) {
            case R.id.create_ad:
                intent = new Intent(this, CreateAdActivity.class);
                intent.putExtra("location", currentWoeid.getName());
                startActivity(intent);
                return true;
            case R.id.action_location:
                showChangeLocationDialog();
                return true;
            case R.id.action_refresh:
                //AdsFragment fragment
                //ActionBar.Tab t = actionBar.getSelectedTab();
                //fragment.refreshAds();
                // FIXME: getSupportFragmentManager() doesn't have findFragmentByTag() method
                //Fragment f = this.getFragmentManager().findFragmentByTag(GIVES_TAG);
                return true;
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //step1
    public void showChangeLocationDialog() {
        ChangeLocationDialogFragment frag = new ChangeLocationDialogFragment();
        frag.show(getSupportFragmentManager(), "ChangeLocationDialogFragment");
    }

    // step2: This is called when user has chosen to add a new location
    public void onChangeDialogNeutralClick(DialogFragment dialog) {
        DialogFragment frag = new FindLocationDialogFragment();
        frag.show(getSupportFragmentManager(), "FindLocationDialog");
    }

    public void onChangeDialogPositiveClick(DialogFragment dialog, Woeid newWoeid) {
        dialog.dismiss();

        changeWoeid(newWoeid);
    }

    // step3: Find woeid
    // TODO: Don't reload dialog, don't close instead
    public void onFindDialogPositiveClick(DialogFragment dialog, String location) {
        boolean isInternet = Utils.isInternetAvailable(this);
        Log.i(TAG, "location: " + location);
        dialog.dismiss();

        if (!isInternet) {
            DialogFragment frag = new FindLocationDialogFragment(location);
            frag.show(getSupportFragmentManager(), "FindLocationDialog");
            Toast.makeText(this, getResources().getString(R.string.error_connecting), Toast.LENGTH_LONG).show();
        } else {
            DialogFragment frag = new ChooseLocationDialogFragment(location);
            frag.show(getSupportFragmentManager(), "ChooseLocationDialog");
        }
    }

    public void onChooseDialogPositiveClick(DialogFragment dialog, Woeid newWoeid) {
        dialog.dismiss();

        DbAdapter dba = new DbAdapter(this);
        dba.openToWrite();
        dba.insertWoeid(currentWoeid);
        dba.close();

        changeWoeid(newWoeid);
    }

    private void changeWoeid(Woeid newWoeid) {
        Toast.makeText(this, newWoeid.toString(), Toast.LENGTH_SHORT).show();
        // Save current woeid
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("current_woeid", newWoeid.getId());
        editor.commit();
        currentWoeid = newWoeid;

        updateTitle();

        // refreshAds
        ListFragment f = (ListFragment) getSupportFragmentManager().findFragmentByTag(GIVES_TAG);
        f.setListAdapter(null);
    }

    private void updateTitle() {
        String title = getResources().getString(R.string.app_name) + " (" + currentWoeid.getName() + ")";
        actionBar.setTitle(title);
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
                Fragment f = getSupportFragmentManager().findFragmentByTag(mTag);
                if(f == null ) {
                    mFragment = Fragment.instantiate(mActivity, mClass.getName());
                    ft.add(Utils.getContentViewCompat(), mFragment, mTag);
                }
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
