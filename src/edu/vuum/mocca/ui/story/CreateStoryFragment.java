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

import java.util.Calendar;
import java.util.Date;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import edu.vanderbilt.mooc.R;
import edu.vuum.mocca.orm.MoocResolver;
import edu.vuum.mocca.orm.StoryData;

public class CreateStoryFragment extends Fragment implements CustomFragment {

  private static final String LOG_TAG = CreateStoryFragment.class.getCanonicalName();

  private EditText titleText, bodyText;
  private static TextView storyTime;
  private Date date;

  private TextView audioLocation, videoLocation, imageLocation;

  private Button buttonCreate, buttonClear, buttonCancel;

  private TextView latitudeValue, longitudeValue;

  private Uri imagePath, fileUri;

  private String audioPath;
  private Location loc;

  private EditText audioNameText, videoNameText, imageNameText;

  private OnOpenWindowInterface mOpener;
  private MoocResolver resolver;

  @Override
  public void setImagePath(Uri imagePath) {
    this.imagePath = imagePath;
  }

  @Override
  public String getAudioFilename() {
    return audioNameText.getText().toString();
  }

  @Override
  public String getVideoFilename() {
    return videoNameText.getText().toString();
  }

  @Override
  public String getImageFilename() {
    return imageNameText.getText().toString();
  }

  public static CreateStoryFragment newInstance() {
    return new CreateStoryFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mOpener = (OnOpenWindowInterface) activity;
      resolver = new MoocResolver(activity);
    }
    catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnOpenWindowListener");
    }
  }

  @Override
  public void onDetach() {
    mOpener = null;
    resolver = null;
    super.onDetach();
  }

  @Override
  public void onActivityCreated(Bundle inState) {
    super.onActivityCreated(inState);

    titleText = (EditText) getView().findViewById(R.id.story_create_value_title);
    bodyText = (EditText) getView().findViewById(R.id.story_create_value_body);
    storyTime = (TextView) getView().findViewById(R.id.story_create_value_story_time);

    imageLocation = (TextView) getView().findViewById(R.id.story_create_value_image_location);
    videoLocation = (TextView) getView().findViewById(R.id.story_create_value_video_location);
    audioLocation = (TextView) getView().findViewById(R.id.story_create_value_audio_location);

    latitudeValue = (TextView) getView().findViewById(R.id.story_create_value_latitude);
    longitudeValue = (TextView) getView().findViewById(R.id.story_create_value_longitude);

    audioNameText = (EditText) getView().findViewById(R.id.story_audio_file_name);
    videoNameText = (EditText) getView().findViewById(R.id.story_video_file_name);
    imageNameText = (EditText) getView().findViewById(R.id.story_image_file_name);

    buttonClear = (Button) getView().findViewById(R.id.story_create_button_reset);
    buttonCancel = (Button) getView().findViewById(R.id.story_create_button_cancel);
    buttonCreate = (Button) getView().findViewById(R.id.story_create_button_save);

    buttonClear.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        titleText.setText("");
        bodyText.setText("");
        storyTime.setText("Set Time");
        latitudeValue.setText("0");
        longitudeValue.setText("0");
        audioNameText.setText("");
        videoNameText.setText("");
        imageNameText.setText("");
        audioLocation.setText("");
        videoLocation.setText("");
        imageLocation.setText("");
      }
    });

    buttonCancel.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (getResources().getBoolean(R.bool.isTablet)) {
          mOpener.openViewStoryFragment(0);
          return;
        }

        boolean hasText =
            hasText(titleText) || hasText(bodyText) || hasText(audioNameText) || hasText(videoNameText)
                || hasText(imageNameText);
        hasText =
            hasText || !"Set Time".equals(storyTime.toString()) || !"0.0".equals(latitudeValue.toString())
                || !"0.0".equals(longitudeValue.toString());

        if (!hasText) {
          getActivity().finish();
          return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You have unsaved changes. Are you sure you want to cancel?").setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                Preferences preferences = new Preferences(getActivity());
                preferences.clearPreferences();
                getActivity().finish();
              }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
              }
            }).create().show();
      }

      private boolean hasText(EditText text) {
        return !text.getText().toString().isEmpty();
      }
    });
    buttonCreate.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        String title = titleText.getText().toString();
        if (title.isEmpty()) {
          AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
          builder.setMessage("Please enter a title for this story.").setCancelable(false)
              .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
                }
              }).create().show();
          return;
        }
        String storyTimeCreateable = storyTime.getText().toString();

        date = Utils.parseDateTime(storyTimeCreateable.toString());
        if (date == null) {
          date = new Date();
        }

        // TODO The loginId and storyId need to be generated by the system.
        long loginId = 0;
        long storyId = 0;

        String body = bodyText.getText().toString();
        String audioLink = audioPath == null ? "" : audioPath;
        String videoLink = fileUri == null ? "" : fileUri.toString();
        String imageName = imageNameText.getText().toString();
        String imageData = imagePath == null ? "" : imagePath.toString();
        double latitude = loc == null ? 0 : loc.getLatitude();
        double longitude = loc == null ? 0 : loc.getLongitude();
        long storyTime = date.getTime();
        Log.i(LOG_TAG, String.valueOf(storyTime));

        // Use -1 for row index because there is no way to know which row the new data will go into
        StoryData newData =
            new StoryData(-1, loginId, storyId, title, body, audioLink, videoLink, imageName, imageData, "", 0,
                storyTime, latitude, longitude);
        Log.d(LOG_TAG, "newStoryData:" + newData);

        // Insert it through Resolver to be put into ContentProvider
        try {
          resolver.insert(newData);
        }
        catch (RemoteException e) {
          Log.e(LOG_TAG, "Caught RemoteException => " + e.getMessage());
          e.printStackTrace();
        }
        Preferences preferences = new Preferences(getActivity());
        preferences.clearPreferences();
        // Return back to proper state
        if (getResources().getBoolean(R.bool.isTablet) == true) {
          mOpener.openViewStoryFragment(0);
        }
        else {
          getActivity().finish(); // same as hitting the Back button
        }
      }
    });

    Preferences prefs = new Preferences(getActivity());

    titleText.setText(prefs.getString(Constants.TITLE_CREATE));
    bodyText.setText(prefs.getString(Constants.BODY_CREATE));

    String text = prefs.getString(Constants.STORY_TIME_CREATE);
    storyTime.setText(text.isEmpty() ? getResources().getString(R.string.story_create_set_time_text_value) : text);
    text = prefs.getString(Constants.LATITUDE_CREATE);
    latitudeValue.setText(text.isEmpty() ? "0.0" : text);
    text = prefs.getString(Constants.LONGITUDE_CREATE);
    longitudeValue.setText(text.isEmpty() ? "0.0" : text);

    audioNameText.setText(prefs.getString(Constants.AUDIO_NAME_CREATE));
    videoNameText.setText(prefs.getString(Constants.VIDEO_NAME_CREATE));
    imageNameText.setText(prefs.getString(Constants.IMAGE_NAME_CREATE));
    audioLocation.setText(prefs.getString(Constants.AUDIO_LOCATION_CREATE));
    videoLocation.setText(prefs.getString(Constants.VIDEO_LOCATION_CREATE));
    imageLocation.setText(prefs.getString(Constants.IMAGE_LOCATION_CREATE));

  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d(LOG_TAG, "CreateStoryFragment.onActivityResult called. requestCode: " + requestCode + " resultCode: "
        + resultCode + "data: " + data);
    switch (requestCode) {
    case Constants.CAMERA_PIC_REQUEST:
      if (resultCode == CreateStoryActivity.RESULT_OK) {
        // Image captured and saved to fileUri specified in the Intent
        if (data != null) {
          imagePath = data.getData();
        }
        imageLocation.setText(imagePath.toString());
      }
      break;
    case Constants.CAMERA_VIDEO_REQUEST:
      if (resultCode == CreateStoryActivity.RESULT_OK) {
        // Video captured and saved to fileUri specified in the Intent
        fileUri = data.getData();
        videoLocation.setText(fileUri.toString());
      }
      break;
    case Constants.MIC_SOUND_REQUEST:
      if (resultCode == CreateStoryActivity.RESULT_OK) {
        // Audio captured and saved to fileUri specified in the Intent
        audioPath = (String) data.getExtras().get("data");
        audioLocation.setText("file://" + audioPath.toString());
      }
      break;
    default:
      throw new IllegalArgumentException("Invalid request code: " + requestCode);
    }
  }

  void setLocation(Location location) {
    Log.d(LOG_TAG, "setLocation = " + location);
    loc = location;
    latitudeValue.setText(String.format("%.1f", loc.getLatitude()));
    longitudeValue.setText(String.format("%.1f", loc.getLongitude()));
  }

  static void setStringDate(int year, int monthOfYear, int dayOfMonth) {

    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, monthOfYear);
    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    storyTime.setText(Utils.formatDateTime(calendar.getTimeInMillis()));

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.story_creation_fragment, container, false);
    container.setBackgroundColor(Color.GRAY);
    return view;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    saveFields();
  }

  private void saveFields() {
    Preferences preferences = new Preferences(getActivity());
    preferences.saveString(Constants.TITLE_CREATE, titleText.getText().toString());
    preferences.saveString(Constants.BODY_CREATE, bodyText.getText().toString());
    preferences.saveString(Constants.STORY_TIME_CREATE, storyTime.getText().toString());
    preferences.saveString(Constants.LATITUDE_CREATE, latitudeValue.getText().toString());
    preferences.saveString(Constants.LONGITUDE_CREATE, longitudeValue.getText().toString());
    preferences.saveString(Constants.AUDIO_NAME_CREATE, audioNameText.getText().toString());
    preferences.saveString(Constants.VIDEO_NAME_CREATE, videoNameText.getText().toString());
    preferences.saveString(Constants.IMAGE_NAME_CREATE, imageNameText.getText().toString());
    preferences.saveString(Constants.AUDIO_LOCATION_CREATE, audioLocation.getText().toString());
    preferences.saveString(Constants.VIDEO_LOCATION_CREATE, videoLocation.getText().toString());
    preferences.saveString(Constants.IMAGE_LOCATION_CREATE, imageLocation.getText().toString());
  }

}
