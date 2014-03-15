/*
The iRemember source code (henceforth referred to as "iRemember") is
copyrighted by Mike Walker, Adam Porter, Doug Schmidt, and Jules White
at Vanderbilt University and the University of Maryland, Copyright (c)
2014, all rights reserved.  Since iRemember is open-source, freely
available software, you are free to use, modify, copy, and
distribute--perpetually and irrevocably--the source code and object code
produced from the source, as well as copy and distribute modified
versions of this software. You must, however, include this copyright
statement along with any code built using iRemember that you release. No
copyright statement needs to be provided if you just ship binary
executables of your software products.

You can use iRemember software in commercial and/or binary software
releases and are under no obligation to redistribute any of your source
code that is built using the software. Note, however, that you may not
misappropriate the iRemember code, such as copyrighting it yourself or
claiming authorship of the iRemember software code, in a way that will
prevent the software from being distributed freely using an open-source
development model. You needn't inform anyone that you're using iRemember
software in your software, though we encourage you to let us know so we
can promote your project in our success stories.

iRemember is provided as is with no warranties of any kind, including
the warranties of design, merchantability, and fitness for a particular
purpose, noninfringement, or arising from a course of dealing, usage or
trade practice.  Vanderbilt University and University of Maryland, their
employees, and students shall have no liability with respect to the
infringement of copyrights, trade secrets or any patents by DOC software
or any part thereof.  Moreover, in no event will Vanderbilt University,
University of Maryland, their employees, or students be liable for any
lost revenue or profits or other special, indirect and consequential
damages.

iRemember is provided with no support and without any obligation on the
part of Vanderbilt University and University of Maryland, their
employees, or students to assist in its use, correction, modification,
or enhancement.

The names Vanderbilt University and University of Maryland may not be
used to endorse or promote products or services derived from this source
without express written permission from Vanderbilt University or
University of Maryland. This license grants no permission to call
products or services derived from the iRemember source, nor does it
grant permission for the name Vanderbilt University or
University of Maryland to appear in their names.
 */

package edu.vuum.mocca.ui.story;

import android.app.DialogFragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class CreateStoryActivity extends StoryActivityBase {

  private final static String LOG_TAG = CreateStoryActivity.class.getCanonicalName();

  private CreateStoryFragment fragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState == null) {
      fragment = CreateStoryFragment.newInstance();
      fragment.setArguments(getIntent().getExtras());
      getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();
    }
    else {
      fragment = (CreateStoryFragment) getLastCustomNonConfigurationInstance();
    }
  }

  @Override
  public void onBackPressed() {
    Preferences preferences = new Preferences(this);
    preferences.clearPreferences();
    super.onBackPressed();
  }

  public void addAudioClicked(View aView) {
    Utils.launchSoundIntent(this, fragment.getAudioFilename());
  }

  public void addVideoClicked(View aView) {
    Utils.launchVideoCameraIntent(this, fragment.getVideoFilename());
  }

  public void addPhotoClicked(View aView) {
    Utils.launchCameraIntent(this, fragment, fragment.getImageFilename());
  }

  public void getDateClicked(View aView) {
    DialogFragment newFragment = new DatePickerFragment();
    newFragment.show(getFragmentManager(), "datePicker");
  }

  public void getLocationClicked(View aView) {
    final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

    LocationListener locationListener = new LocationListener() {
      public void onLocationChanged(Location location) {
        Toast.makeText(getApplicationContext(), "New location obtained.", Toast.LENGTH_LONG).show();
        makeUseOfNewLocation(location);
        locationManager.removeUpdates(this);
      }

      public void onStatusChanged(String provider, int status, Bundle extras) {
      }

      public void onProviderEnabled(String provider) {
      }

      public void onProviderDisabled(String provider) {
      }
    };

    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      Toast.makeText(getApplicationContext(), "GPS is not enabled.", Toast.LENGTH_LONG).show();
      return;
    }

    Log.d(LOG_TAG, "locationManager.isProviderEnabled = true/gps");
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    if (location == null) {
      Toast.makeText(getApplicationContext(), "GPS has yet to calculate location.", Toast.LENGTH_LONG).show();
    }
    else {
      makeUseOfNewLocation(location);
    }
  }

  @Override
  public Object onRetainCustomNonConfigurationInstance() {
    return fragment;
  }

  private void makeUseOfNewLocation(Location loc) {
    fragment.setLocation(loc);
  }

}
