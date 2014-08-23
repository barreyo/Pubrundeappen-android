package se.chalmers.krogkollen.detailed;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import se.chalmers.krogkollen.R;
import se.chalmers.krogkollen.backend.BackendNotInitializedException;
import se.chalmers.krogkollen.backend.NoBackendAccessException;
import se.chalmers.krogkollen.backend.NotFoundInBackendException;
import se.chalmers.krogkollen.help.HelpActivity;
import se.chalmers.krogkollen.map.MarkerOptionsFactory;
import se.chalmers.krogkollen.pub.IPub;
import se.chalmers.krogkollen.utils.Constants;
/*
 * This file is part of Krogkollen.
 *
 * Krogkollen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Krogkollen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with Krogkollen.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 * This class represent the activity for the detailed view
 */
public class DetailedActivity extends Activity implements IDetailedView {

	/** The presenter connected to the detailed view */
	private IDetailedPresenter	presenter;

	/** A bunch of view elements */
	private TextView			pubTextView, descriptionTextView, openingHoursTextView,
								ageRestrictionTextView, entranceFeeTextView, votesUpTextView, votesDownTextView;
	private ImageView			thumbsUpImage, thumbsDownImage, queueIndicator;
	private MenuItem			favoriteStar;
	private ProgressDialog		progressDialog;

	private GoogleMap map;
	private Marker marker;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// Sets display mode to portrait only.
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detailed);

        int API_LEVEL =  android.os.Build.VERSION.SDK_INT;

        if (API_LEVEL >= 19)
        {
            getWindow().addFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

		presenter = new DetailedPresenter();
		presenter.setView(this);




		try {
			presenter.setPub(getIntent().getStringExtra(Constants.MARKER_PUB_ID));
		} catch (NoBackendAccessException e) {
			this.showErrorMessage(this.getString(R.string.error_no_backend_access));
		} catch (NotFoundInBackendException e) {
			this.showErrorMessage(this.getString(R.string.error_no_backend_item));
		} catch (BackendNotInitializedException e) {
			this.showErrorMessage(this.getString(R.string.error_backend_not_initialized));
		}

		//addListeners();

		/*pubTextView = (TextView) findViewById(R.id.pub_name);
		descriptionTextView = (TextView) findViewById(R.id.description);
		openingHoursTextView = (TextView) findViewById(R.id.opening_hours);
		ageRestrictionTextView = (TextView) findViewById(R.id.age);
		entranceFeeTextView = (TextView) findViewById(R.id.entrance_fee);
		queueIndicator = (ImageView) findViewById(R.id.queueIndicator);
		votesUpTextView = (TextView) findViewById(R.id.thumbsUpTextView);
		votesDownTextView = (TextView) findViewById(R.id.thumbsDownTextView);
		thumbsUpImage = (ImageView) findViewById(R.id.thumbsUpButton);
		thumbsDownImage = (ImageView) findViewById(R.id.thumbsDownButton);*/

		/*map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				return true; // Suppress default behaviour.
			}
		});

		map.getUiSettings().setCompassEnabled(false);
		map.getUiSettings().setZoomControlsEnabled(false); */

		getActionBar().setDisplayUseLogoEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setIcon(R.drawable.transparent_spacer);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detailed, menu);

        /*
		try {
			presenter.updateInfo();
		} catch (NoBackendAccessException e) {
			this.showErrorMessage(this.getString(R.string.error_no_backend_access));
		} catch (NotFoundInBackendException e) {
			this.showErrorMessage(this.getString(R.string.error_no_backend_item));
		} catch (BackendNotInitializedException e) {
			this.showErrorMessage(this.getString(R.string.error_backend_not_initialized));
		} */

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabbutton);
        fab.setColor(Color.parseColor("#75c552"));

        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        animation.setDuration(500);
        animation.setStartOffset(600);
        fab.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
        View view = findViewById(R.id.detailed_main_content);
        view.startAnimation(animation);

		return true;
	}

	@Override
	public void navigate(Class<?> destination) {
		Intent intent = new Intent(this, destination);
		intent.putExtra(Constants.ACTIVITY_FROM, Constants.DETAILED_ACTIVITY_NAME);
		startActivity(intent);
	}

	@Override
	public void navigate(Class<?> destination, Bundle extras) {
		Intent intent = new Intent(this, destination);
		intent.putExtra(Constants.ACTIVITY_FROM, Constants.DETAILED_ACTIVITY_NAME);
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
	public void updateText(String pubName, String description, String openingHours, String age, String price) {
		/*pubTextView.setText(pubName);
		descriptionTextView.setText(description);
		openingHoursTextView.setText(openingHours);
		ageRestrictionTextView.setText(age);
		entranceFeeTextView.setText(price); */
	}

	@Override
	public void updateQueueIndicator(int queueTime) {
	/*	switch (queueTime) {
			case 1:
				queueIndicator.setBackgroundResource(R.drawable.detailed_queue_green);
				break;
			case 2:
				queueIndicator.setBackgroundResource(R.drawable.detailed_queue_yellow);
				break;
			case 3:
				queueIndicator.setBackgroundResource(R.drawable.detailed_queue_red);
				break;
			default:
				queueIndicator.setBackgroundResource(R.drawable.detailed_queue_gray);
				break;
		}*/
	}

    @Override
    public void showVotes(String upVotes, String downVotes) {

    }

    // Adds listeners to all buttons
    /*private void addListeners() {
		findViewById(R.id.thumbsUpLayout).setOnClickListener(presenter);
		findViewById(R.id.thumbsDownLayout).setOnClickListener(presenter);
		findViewById(R.id.navigate).setOnClickListener(presenter);
	}*/

	@Override
	public void addMarker(IPub pub) {
		if (marker == null) {
			marker = map.addMarker(MarkerOptionsFactory.createMarkerOptions(getResources().getDisplayMetrics(), getResources(), pub));
		}
	}

	@Override
	public void removeMarker() {
		if (marker != null) {
			marker.remove();
			marker = null;
		}
	}

	@Override
	public void navigateToLocation(LatLng latLng, int zoom) {
		map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, zoom, 0, 45)));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.refresh_info:
				try {
					presenter.updateInfo();
				} catch (NoBackendAccessException e) {
					this.showErrorMessage(this.getString(R.string.error_no_backend_access));
				} catch (NotFoundInBackendException e) {
					this.showErrorMessage(this.getString(R.string.error_no_backend_item));
				} catch (BackendNotInitializedException e) {
					this.showErrorMessage(this.getString(R.string.error_backend_not_initialized));
				}
				break;
			case R.id.action_help:
				navigate(HelpActivity.class);
				break;
			case android.R.id.home:
				finish();
				return true;
		}
		return true;
	}

	/**
	 * Shows a progress dialog indicating that the info is being updated
	 */
	public void showProgressDialog() {
		progressDialog = ProgressDialog.show(this, "", getString(R.string.dialog_updating_info), false, false);
	}

	/**
	 * Hides the progress dialog
	 */
	public void hideProgressDialog() {
		progressDialog.hide();
	}
}