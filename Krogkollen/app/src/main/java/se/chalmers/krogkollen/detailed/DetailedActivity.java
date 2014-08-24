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
	private TextView			pubTextView, descriptionTextView, openingHoursTextView, lastUpdatedTextView;

    private ProgressDialog		progressDialog;

    private boolean hidden = false;

    @Override
	public void onCreate(Bundle savedInstanceState) {

		// Sets display mode to portrait only.
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detailed);

        final View view = findViewById(R.id.detailed_main_content);

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

                if (!hidden) {
                    Animation animation = AnimationUtils.loadAnimation(DetailedActivity.this, R.anim.slide_out_bottom);
                    animation.setInterpolator(DetailedActivity.this, android.R.anim.accelerate_interpolator);
                    animation.setDuration(600);
                    view.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            view.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    Animation animation1 = AnimationUtils.loadAnimation(DetailedActivity.this, android.R.anim.fade_out);
                    animation1.setDuration(400);
                    final ImageView imageView = (ImageView) findViewById(R.id.overlay_gradient);

                    animation1.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            imageView.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    imageView.startAnimation(animation1);

                    Animation animation2 = AnimationUtils.loadAnimation(DetailedActivity.this, android.R.anim.fade_out);
                    animation2.setDuration(200);
                    final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabbutton);

                    animation2.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            fab.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    fab.startAnimation(animation2);

                    hidden = true;
                } else {
                    Animation animation = AnimationUtils.loadAnimation(DetailedActivity.this, R.anim.slide_in_bottom);
                    animation.setInterpolator(DetailedActivity.this, android.R.anim.decelerate_interpolator);
                    animation.setDuration(600);
                    view.startAnimation(animation);

                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            view.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    Animation animation1 = AnimationUtils.loadAnimation(DetailedActivity.this, android.R.anim.fade_in);
                    animation1.setDuration(200);
                    animation1.setStartOffset(600);
                    final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabbutton);

                    animation1.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            fab.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    fab.startAnimation(animation1);

                    Animation animation2 = AnimationUtils.loadAnimation(DetailedActivity.this, android.R.anim.fade_in);
                    animation2.setDuration(500);
                    final ImageView imageView = (ImageView) findViewById(R.id.overlay_gradient);

                    animation2.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            imageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    imageView.startAnimation(animation2);

                    hidden = false;
                }

                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                System.out.println("onSCROLL");

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
        animation.setInterpolator(this, android.R.anim.decelerate_interpolator);
        view.startAnimation(animation);
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
    public void updateText(String pubName, String description, String openingHours, String lastUpdated) {

    }

    @Override
	public void updateQueueIndicator(int queueTime) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabbutton);
		switch (queueTime) {
			case 1:
                fab.setColor(Color.parseColor("#85ca4c"));
				break;
			case 2:
				fab.setColor(Color.parseColor("#f6f406"));
				break;
			case 3:
				fab.setColor(Color.parseColor("#f61e06"));
				break;
			default:
                fab.setColor(Color.parseColor("#a0a0a0"));
				break;
		}
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