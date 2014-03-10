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

import java.text.ParseException;
import java.util.Date;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.text.Editable;
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

/**
 * Fragments require a Container Activity, this is the one for the Edit StoryData
 */
public class CreateStoryFragment extends Fragment {

  private final static String LOG_TAG = CreateStoryFragment.class.getCanonicalName();

  private EditText titleET;
  private EditText bodyET;
  private Button videoCaptureButton;
  private EditText imageNameET;
  private Button imageCaptureButton;
  private static TextView storyTimeET;
  private Date date;
  private Button locationButton;

  private TextView imageLocation;
  private TextView videoLocation;
  private TextView audioLocation;

  private Button buttonCreate;
  private Button buttonClear;
  private Button buttonCancel;

  private TextView latitudeValue;
  private TextView longitudeValue;

  private Uri imagePath;
  private Uri fileUri;
  private String audioPath;
  private Location loc;

  private OnOpenWindowInterface mOpener;
  private MoocResolver resolver;

  public static CreateStoryFragment newInstance() {
    CreateStoryFragment f = new CreateStoryFragment();
    return f;
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
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    titleET = (EditText) getView().findViewById(R.id.story_create_value_title);
    bodyET = (EditText) getView().findViewById(R.id.story_create_value_body);
    videoCaptureButton = (Button) getView().findViewById(R.id.story_create_value_video_button);
    imageNameET = (EditText) getView().findViewById(R.id.story_create_value_image_name);
    imageCaptureButton = (Button) getView().findViewById(R.id.story_create_value_image_button);
    storyTimeET = (TextView) getView().findViewById(R.id.story_create_value_story_time);
    locationButton = (Button) getView().findViewById(R.id.story_create_value_location_button);

    imageLocation = (TextView) getView().findViewById(R.id.story_create_value_image_location);
    videoLocation = (TextView) getView().findViewById(R.id.story_create_value_video_location);
    audioLocation = (TextView) getView().findViewById(R.id.story_create_value_audio_location);

    latitudeValue = (TextView) getView().findViewById(R.id.story_create_value_latitude);
    longitudeValue = (TextView) getView().findViewById(R.id.story_create_value_longitude);

    buttonClear = (Button) getView().findViewById(R.id.story_create_button_reset);
    buttonCancel = (Button) getView().findViewById(R.id.story_create_button_cancel);
    buttonCreate = (Button) getView().findViewById(R.id.story_create_button_save);

    buttonClear.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        titleET.setText("");
        bodyET.setText("");
        videoCaptureButton.setText("");
        imageNameET.setText("");
        imageCaptureButton.setText("");
        storyTimeET.setText("0");
        locationButton.setText("0");
      }
    });

    buttonCancel.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (getResources().getBoolean(R.bool.isTablet) == true) {
          mOpener.openViewStoryFragment(0);
        }
        else {
          getActivity().finish(); // same as hitting the Back button
        }
      }
    });
    buttonCreate.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        Editable titleCreateable = titleET.getText();
        Editable bodyCreateable = bodyET.getText();
        Editable imageNameCreateable = imageNameET.getText();
        String storyTimeCreateable = storyTimeET.getText().toString();

        // Try to parse the date into long format
        try {
          date = StoryData.FORMAT.parse(storyTimeCreateable.toString());
        }
        catch (ParseException e1) {
          Log.e(LOG_TAG, "Date was not parsable, reverting to current time");
          date = new Date();
        }

        // TODO The loginId and storyId need to be generated by the system.
        long loginId = 0;
        long storyId = 0;

        String title = String.valueOf(titleCreateable.toString());
        String body = String.valueOf(bodyCreateable.toString());
        String audioLink = audioPath == null ? "" : audioPath;
        String videoLink = fileUri == null ? "" : fileUri.toString();
        String imageName = imageNameCreateable.toString();
        String imageData = imagePathFinal == null ? "" : imagePathFinal.toString();
        double latitude = loc == null ? 0 : loc.getLatitude();
        double longitude = loc == null ? 0 : loc.getLongitude();
        long storyTime = date.getTime();
        Log.i(LOG_TAG, String.valueOf(storyTime));

        // Use -1 for row index because there is no way to know which row the new data will go into
        StoryData newData =
            new StoryData(-1, loginId, storyId, title, body, audioLink, videoLink, imageName, imageData, "", 0,
                storyTime, latitude, longitude);
        Log.d(CreateStoryFragment.class.getCanonicalName(), "imageName" + imageNameET.getText());

        Log.d(CreateStoryFragment.class.getCanonicalName(), "newStoryData:" + newData);

        // Insert it through Resolver to be put into ContentProvider
        try {
          resolver.insert(newData);
        }
        catch (RemoteException e) {
          Log.e(LOG_TAG, "Caught RemoteException => " + e.getMessage());
          e.printStackTrace();
        }
        // Return back to proper state
        if (getResources().getBoolean(R.bool.isTablet) == true) {
          mOpener.openViewStoryFragment(0);
        }
        else {
          getActivity().finish(); // same as hitting the Back button
        }
      }
    });

  }

  Uri imagePathFinal = null;

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d(LOG_TAG, "CreateStoryFragment.onActivityResult called. requestCode: " + requestCode + " resultCode:" + resultCode
        + "data:" + data);
    if (requestCode == CreateStoryActivity.CAMERA_PIC_REQUEST) {
      if (resultCode == CreateStoryActivity.RESULT_OK) {
        // Image captured and saved to fileUri specified in the Intent
        imagePathFinal = imagePath;
        imageLocation.setText(imagePathFinal.toString());
      }
      else if (resultCode == CreateStoryActivity.RESULT_CANCELED) {
        // User cancelled the image capture
      }
      else {
        // Image capture failed. Advise user
      }
    }
    else if (requestCode == CreateStoryActivity.CAMERA_VIDEO_REQUEST) {
      if (resultCode == CreateStoryActivity.RESULT_OK) {
        // Video captured and saved to fileUri specified in the Intent
        fileUri = data.getData();
        videoLocation.setText(fileUri.toString());
      }
      else if (resultCode == CreateStoryActivity.RESULT_CANCELED) {
        // User cancelled the video capture
      }
      else {
        // Image capture failed. Advise user
      }
    }
    else if (requestCode == CreateStoryActivity.MIC_SOUND_REQUEST) {
      if (resultCode == CreateStoryActivity.RESULT_OK) {
        // Audio captured and saved to fileUri specified in the Intent
        audioPath = (String) data.getExtras().get("data");
        audioLocation.setText("file://" + audioPath.toString());
      }
      else if (resultCode == CreateStoryActivity.RESULT_CANCELED) {
        // User cancelled the audio capture
      }
      else {
        // Image capture failed. Advise user
      }

    }
  }

  void setLocation(Location location) {
    Log.d(LOG_TAG, "setLocation = " + location);
    loc = location;
    latitudeValue.setText(String.valueOf(loc.getLatitude()));
    longitudeValue.setText(String.valueOf(loc.getLongitude()));
  }

  static void setStringDate(int year, int monthOfYear, int dayOfMonth) {

    // Increment monthOfYear for Calendar/Date -> Time Format setting
    monthOfYear++;
    String mon = "" + monthOfYear;
    String day = "" + dayOfMonth;

    if (monthOfYear < 10) {
      mon = "0" + monthOfYear;
    }
    if (dayOfMonth < 10) {
      day = "0" + dayOfMonth;
    }

    storyTimeET.setText(year + "-" + mon + "-" + day);

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.story_creation_fragment, container, false);
    container.setBackgroundColor(Color.GRAY);
    return view;
  }

}
