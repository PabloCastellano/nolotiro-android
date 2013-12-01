package org.alabs.nolotiro;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ImageView;
import android.widget.Toast;

import org.alabs.nolotiro.db.DbAdapter;

public class AdViewActivity extends ActionBarActivity {

    private static final String TAG = "AdViewActivity";
    private Ad ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_view);
        ad = (Ad) getIntent().getSerializableExtra("ad");

        Log.i(TAG, "Created new activity to show item " + ad.getId());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new AdViewFragment(ad))
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ad_view, menu);

        if (ad.isFavorite()) {
            menu.findItem(R.id.action_bookmark).setIcon(R.drawable.ic_rating_important);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bookmark:
                boolean isFavorite = !ad.isFavorite();
                Log.i(TAG, "Bookmark ad=" + ad.getId() + ": " + isFavorite);
                DbAdapter dba = new DbAdapter(this);
                dba.openToWrite();
                dba.markAdAsFavorite(ad.getId(), isFavorite);
                dba.close();
                ad.setFavorite(isFavorite);
                // toggle icon
                if (ad.isFavorite()) {
                    item.setIcon(R.drawable.ic_rating_important);
                    Toast.makeText(this, getResources().getString(R.id.bookmark_added), Toast.LENGTH_SHORT).show();
                } else {
                    item.setIcon(R.drawable.ic_rating_not_important);
                    Toast.makeText(this, getResources().getString(R.id.bookmark_removed), Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.action_comment:
                Toast.makeText(this, getResources().getString(R.id.not_yet_implemented), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_contact:
                Toast.makeText(this, getResources().getString(R.id.not_yet_implemented), Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class AdViewFragment extends Fragment {

        private Ad ad;

        public AdViewFragment(Ad _ad) {
            ad = _ad;
        }

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            loadItemData();
            ImageView image = (ImageView) this.getActivity().findViewById(R.id.imageImage);
            image.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    // TODO: don't use tag
                    intent.setDataAndType(Uri.parse("file://" + v.getTag()), "image/*");
                    startActivity(intent);
                }
            });
        }

        private void loadItemData() {
            ShowAdTask showTask = new ShowAdTask(this, ad);
            showTask.execute();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_ad_view, container, false);
            return rootView;
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

}
