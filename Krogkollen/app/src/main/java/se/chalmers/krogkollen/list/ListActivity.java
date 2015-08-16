package se.chalmers.krogkollen.list;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import se.chalmers.krogkollen.IView;
import se.chalmers.krogkollen.R;
import se.chalmers.krogkollen.adapter.PubListAdapter;
import se.chalmers.krogkollen.detailed.DetailedActivity;
import se.chalmers.krogkollen.help.HelpActivity;
import se.chalmers.krogkollen.pub.IPub;
import se.chalmers.krogkollen.pub.PubUtilities;
import se.chalmers.krogkollen.utils.Constants;

/**
 * Activity for the list view. This shows a list that is sorted by a few default values, that the
 * user can chose between. Such as distance, queue-time or favorites.
 * 
 */
public class ListActivity extends Activity implements IView {

    private List<IPub>              mItems;
    private RefreshableListView     mListView;
    private ArrayAdapter<IPub>      adapter;
    private boolean                 firstRun = true, finishingAnimation = false;
    private ActionBar               actionBar;
    private boolean                 updating = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

        mItems = new ArrayList<IPub>();

        mItems.addAll(PubUtilities.getInstance().getPubList());

        Collections.sort(mItems, new PubQueueComparator());

        adapter = new PubListAdapter(this, R.layout.listview_item, mItems);

        mListView = (RefreshableListView) findViewById(R.id.listview);
        mListView.setAdapter(adapter);

        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(300);
        set.addAnimation(animation);

        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(300);
        set.addAnimation(animation);

        LayoutAnimationController controller =
                new LayoutAnimationController(set, 0.25f);
        mListView.setLayoutAnimation(controller);

        // Callback to refresh the list
        mListView.setOnRefreshListener(new RefreshableListView.OnRefreshListener() {
            @Override
            public void onRefresh(RefreshableListView listView) {
                finishingAnimation = true;
                if (!updating) {
                    updating = true;
                    new RefreshListData().execute();
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.MAP_PRESENTER_KEY, mItems.get(position).getID());
                System.out.println("IDDDDD:  " + mItems.get(position).getID());
                ListActivity.this.navigate(DetailedActivity.class, bundle);
            }
        });

		// Remove the default logo icon and add our list icon.
		actionBar = getActionBar();

		actionBar.setIcon(R.drawable.map_icon);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);

        new RefreshListData().execute();
	}

	// Start the activity in a local method to keep the right context.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list, menu);
		return true;
	}

	@Override
	public void navigate(Class<?> destination) {
		Intent intent = new Intent(this, destination);
		intent.putExtra(Constants.ACTIVITY_FROM, Constants.LIST_ACTIVITY_NAME);
		startActivity(intent);
	}

	@Override
	public void navigate(Class<?> destination, Bundle extras) {
		Intent intent = new Intent(this, destination);
		intent.putExtra(Constants.MARKER_PUB_ID, extras.getString(Constants.MAP_PRESENTER_KEY));
		intent.putExtra(Constants.ACTIVITY_FROM, Constants.LIST_ACTIVITY_NAME);
		startActivity(intent);
	}

	@Override
	public void showErrorMessage(String message) {
		CharSequence text = message;
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(this, text, duration);
		toast.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.search:
				this.onSearchRequested();
				break;
			case R.id.action_help:
				this.navigate(HelpActivity.class);
				break;
			case android.R.id.home:
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("RESUME");
        if (!firstRun) {
            new RefreshListData().execute();
        }
        firstRun = false;
        actionBar.show();
    }

    @Override
    public void onPause() {
        super.onResume();

        actionBar.hide();
    }

    private class RefreshListData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                PubUtilities.getInstance().refreshPubList();
            } catch (Exception e) {
                System.out.println("LOAD ERROR");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            mItems.clear();
            mItems.addAll(PubUtilities.getInstance().getPubList());
            Collections.sort(mItems, new PubQueueComparator());

            adapter.notifyDataSetChanged();
            mListView.invalidateViews();
            if (finishingAnimation) {
                mListView.completeRefreshing();
            } else {
                mListView.invalidateViews();
            }
            mListView.refreshDrawableState();
            finishingAnimation = false;

            updating = false;

            super.onPostExecute(result);
        }
    }

    private class PubQueueComparator implements Comparator<IPub>
    {
        @Override
        public int compare(IPub lhs, IPub rhs) {

            int lhsQueueTime = lhs.getQueueTime();
            int rhsQueueTime = rhs.getQueueTime();

            if (lhsQueueTime == 0) {
                lhsQueueTime = 4;
            }

            if(rhsQueueTime == 0) {
                rhsQueueTime = 4;
            }

            return lhsQueueTime - rhsQueueTime;
        }
    }
}