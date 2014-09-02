package se.chalmers.krogkollen.detailed;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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

import com.parse.GetDataCallback;
import com.parse.ParseException;

import java.util.Date;
import java.util.TimeZone;

import se.chalmers.krogkollen.R;
import se.chalmers.krogkollen.backend.BackendHandler;
import se.chalmers.krogkollen.backend.BackendNotInitializedException;
import se.chalmers.krogkollen.backend.NoBackendAccessException;
import se.chalmers.krogkollen.backend.NotFoundInBackendException;
import se.chalmers.krogkollen.help.HelpActivity;
import se.chalmers.krogkollen.map.MapActivity;
import se.chalmers.krogkollen.pub.IPub;
import se.chalmers.krogkollen.pub.PubUtilities;
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
	private TextView			pubTextView, descriptionTextView, openingHoursBranchTextView,lastUpdatedTextView;

    private FrameLayout         main;

    private ProgressDialog		progressDialog;

    private boolean hidden = false, isAnimating = false;

    private IPub                pub;

    @Override
	public void onCreate(Bundle savedInstanceState) {

		// Sets display mode to portrait only.
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detailed);

        pubTextView = (TextView) findViewById(R.id.pub_name_text);
        descriptionTextView = (TextView) findViewById(R.id.description_text);
        openingHoursBranchTextView = (TextView) findViewById(R.id.opening_hours_branch_text);
        lastUpdatedTextView = (TextView) findViewById(R.id.last_updated);
        main = (FrameLayout) findViewById(R.id.frame);

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

                if (!hidden && !isAnimating ) {
                    DetailedActivity.this.slideDownanimation(view);
                    hidden = true;
                } else if (!isAnimating) {
                    DetailedActivity.this.slideUpAnimation(view);
                    hidden = false;
                }

                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                System.out.println("onFLING");

                return false;
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

        pub = PubUtilities.getInstance().getPub(getIntent().getStringExtra(Constants.MARKER_PUB_ID));
        this.pubTextView.setText(pub.getName());
        this.descriptionTextView.setText(pub.getDescription());

        Date fixedUpdated = convertTimeZone(pub.getLastUpdated(), TimeZone.getDefault(), TimeZone.getTimeZone("Coordinated Universal Time"));

        String fixedUpdatedString = "";

        if (fixedUpdated.getHours() < 10) {
            fixedUpdatedString += "0" + fixedUpdated.getHours() + ":";
        } else {
            fixedUpdatedString += fixedUpdated.getHours() + ":";
        }

        if (fixedUpdated.getMinutes() < 10) {
            fixedUpdatedString += "0" + fixedUpdated.getMinutes();
        } else {
            fixedUpdatedString += fixedUpdated.getMinutes();
        }

        this.lastUpdatedTextView.setText("Senast uppdaterad " + fixedUpdatedString);

        Date fixedOpening = convertTimeZone(pub.getOpeningTime(), TimeZone.getDefault(), TimeZone.getTimeZone("Coordinated Universal Time"));
        Date fixedClosing = convertTimeZone(pub.getClosingTime(), TimeZone.getDefault(), TimeZone.getTimeZone("Coordinated Universal Time"));

        String fixedOpeningString = "";
        String fixedClosingString = "";

        if (fixedOpening.getHours() < 10) {
            fixedOpeningString +=  "0" + fixedOpening.getHours() + ":";
        } else {
            fixedOpeningString += fixedOpening.getHours() + ":";
        }

        if (fixedOpening.getMinutes() < 10) {
            fixedOpeningString += "0" + fixedOpening.getMinutes();
        } else {
            fixedOpeningString += fixedOpening.getMinutes();
        }

        if (fixedClosing.getHours() < 10) {
            fixedClosingString += "0" + fixedClosing.getHours() + ":";
        } else {
            fixedClosingString += fixedClosing.getHours() + ":";
        }

        if (fixedClosing.getMinutes() < 10) {
            fixedClosingString += "0" + fixedClosing.getMinutes();
        } else {
            fixedClosingString += fixedClosing.getMinutes();
        }

        this.openingHoursBranchTextView.setText(fixedOpeningString + " - " + fixedClosingString);

        if (pub.getBackground() != null) {
            pub.getBackground().getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (data != null){
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Drawable d = new BitmapDrawable(getResources(), bitmap);
                        main.setBackgroundDrawable(d);
                    }
                }
            });
        }

        updateQueueIndicator(pub.getQueueTime());
        new ConcurrentQueueTimeUpdate().execute(pub);
	}

    private java.util.Date convertTimeZone(java.util.Date date, TimeZone fromTZ , TimeZone toTZ)
    {
        long fromTZDst = 0;
        if(fromTZ.inDaylightTime(date))
        {
            fromTZDst = fromTZ.getDSTSavings();
        }

        long fromTZOffset = fromTZ.getRawOffset() + fromTZDst;

        long toTZDst = 0;
        if(toTZ.inDaylightTime(date))
        {
            toTZDst = toTZ.getDSTSavings();
        }
        long toTZOffset = toTZ.getRawOffset() + toTZDst;

        return new java.util.Date(date.getTime() + (toTZOffset - fromTZOffset));
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detailed, menu);

		return true;
	}

	@Override
	public void navigate(Class<?> destination) {
		Intent intent = new Intent(this, destination);
		intent.putExtra(Constants.ACTIVITY_FROM, Constants.DETAILED_ACTIVITY_NAME);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

	@Override
	public void navigate(Class<?> destination, Bundle extras) {
		Intent intent = new Intent(this, destination);
		intent.putExtra(Constants.ACTIVITY_FROM, Constants.DETAILED_ACTIVITY_NAME);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtras(extras);
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
                fab.setColor(Color.parseColor("#a2eb62"));
				break;
			case 2:
				fab.setColor(Color.parseColor("#fffa67"));
				break;
			case 3:
				fab.setColor(Color.parseColor("#eb7862"));
				break;
			default:
                fab.setColor(Color.parseColor("#dfdfdf"));
				break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
            case R.id.show_on_map:
                Bundle bundle = new Bundle();
                bundle.putDouble("SHOW_ON_MAP_LATITUDE", pub.getLatitude());
                bundle.putDouble("SHOW_ON_MAP_LONGITUDE", pub.getLongitude());
                this.navigate(MapActivity.class, bundle);
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

    private void slideUpAnimation(final View view) {
        isAnimating = true;
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
                isAnimating = false;
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
    }

    private void slideDownanimation(final View view) {
        isAnimating = true;
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
                isAnimating = false;
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

    private class ConcurrentQueueTimeUpdate extends AsyncTask<IPub, IPub, Integer> {

        protected Integer doInBackground(IPub... pubs) {

            int queueTime = pubs[0].getQueueTime();

            try {
                queueTime = BackendHandler.getInstance().getQueueTime(pubs[0]);
            } catch (Exception e) {
                System.out.println("Failed to get accurate queue time. Using cache.");
            }
            return queueTime;
        }

        @Override
        protected void onPostExecute(Integer result) {
            DetailedActivity.this.updateQueueIndicator(result.intValue());
            System.out.println(result.intValue());
        }
    }
}