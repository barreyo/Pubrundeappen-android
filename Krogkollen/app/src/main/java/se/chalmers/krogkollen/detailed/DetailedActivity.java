package se.chalmers.krogkollen.detailed;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
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

    private FrameLayout.LayoutParams original_params;
    private double start_y;
    private int view_height;

    @Override
	public void onCreate(Bundle savedInstanceState) {

		// Sets display mode to portrait only.
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detailed);

        final View view = findViewById(R.id.detailed_main_content);

        original_params = (FrameLayout.LayoutParams) view.getLayoutParams();

        start_y = view.getY();
        view_height = view.getHeight();

        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                System.out.println("onDOWN");
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                System.out.println("onSCROLL");

                System.out.println(distanceY);

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();

                System.out.println("view.getheight: " + view.getHeight());

                System.out.println("HEIGHT: " + original_params.height);
                System.out.println("ORIGINAL MARGIN: " + original_params.topMargin);
                System.out.println("PARAMS MARGIN: " + params.topMargin);

                params.topMargin -= distanceY;

                view.setLayoutParams(params);

                if (distanceY > 0 ) {
                    System.out.println("UP");
                } else {
                    System.out.println("DOWN");
                }

                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                System.out.println("onFLING");

                return true;
            }
        });

        FrameLayout frameView = (FrameLayout) findViewById(R.id.frame);

        frameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

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

		getActionBar().setDisplayUseLogoEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setIcon(R.drawable.ic_action_back);
		getActionBar().setDisplayHomeAsUpEnabled(false);
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

        /*Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
        ImageView imageView = (ImageView) findViewById(R.id.overlay_gradient);
        imageView.startAnimation(animation1); */

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