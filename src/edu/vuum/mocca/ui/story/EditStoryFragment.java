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

import java.util.Date;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Toast;
import edu.vanderbilt.mooc.R;
import edu.vuum.mocca.orm.MoocResolver;
import edu.vuum.mocca.orm.StoryData;

public class EditStoryFragment extends Fragment implements CustomFragment {

  private final static String LOG_TAG = EditStoryFragment.class.getCanonicalName();

  // Variable for passing around row index
  final static String ROW_IDENTIFIER_TAG = "index";
  private EditText titleText;
  private EditText bodyText;
  private TextView audioLocation, videoLocation, imageLocation;
  private EditText imageNameText;
  private EditText tagsText;
  private TextView storyTime;
  private Date date;
  private EditText latitudeText;
  private EditText longitudeText;

  private Button newAudio, removeAudio, newVideo, removeVideo, newImage, removeImage;

  private Button saveButton;
  private Button resetButton;
  private Button cancelButton;

  // Parent activity
  private OnOpenWindowInterface mOpener;
  // Custom ContentResolver wrapper.
  private MoocResolver resolver;

  private Uri imagePath;

  @Override
  public void setImagePath(Uri imagePath) {
    this.imagePath = imagePath;
  }

  @Override
  public String getAudioFilename() {
    return "";
  }

  @Override
  public String getVideoFilename() {
    return "";
  }

  @Override
  public String getImageFilename() {
    return "";
  }

  // TODO Determine/label pattern.
  private OnClickListener myOnClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      switch (v.getId()) {
      case R.id.story_edit_button_save:
        doSaveButtonClick();
        break;
      case R.id.story_edit_button_reset:
        doResetButtonClick();
        break;
      case R.id.story_edit_button_cancel:
        doCancelButtonClick();
        break;
      default:
        break;
      }
    }
  };

  public static EditStoryFragment newInstance(long index) {
    EditStoryFragment f = new EditStoryFragment();
    // Supply index input as an argument.
    Bundle args = new Bundle();
    args.putLong(ROW_IDENTIFIER_TAG, index);
    f.setArguments(args);
    return f;
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
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    saveButton = (Button) getView().findViewById(R.id.story_edit_button_save);
    resetButton = (Button) getView().findViewById(R.id.story_edit_button_reset);
    cancelButton = (Button) getView().findViewById(R.id.story_edit_button_cancel);

    titleText = (EditText) getView().findViewById(R.id.story_edit_title);
    bodyText = (EditText) getView().findViewById(R.id.story_edit_body);
    audioLocation = (TextView) getView().findViewById(R.id.story_edit_audio_link);
    videoLocation = (TextView) getView().findViewById(R.id.story_edit_video_link);
    imageNameText = (EditText) getView().findViewById(R.id.story_edit_image_title);
    imageLocation = (TextView) getView().findViewById(R.id.story_edit_image_meta_data);
    tagsText = (EditText) getView().findViewById(R.id.story_edit_tags);
    storyTime = (TextView) getView().findViewById(R.id.story_edit_story_time);
    latitudeText = (EditText) getView().findViewById(R.id.story_edit_latitude);
    longitudeText = (EditText) getView().findViewById(R.id.story_edit_longitude);

    newAudio = (Button) getView().findViewById(R.id.story_edit_new_audio);
    removeAudio = (Button) getView().findViewById(R.id.story_edit_remove_audio);
    newVideo = (Button) getView().findViewById(R.id.story_edit_new_video);
    removeVideo = (Button) getView().findViewById(R.id.story_edit_remove_video);
    newImage = (Button) getView().findViewById(R.id.story_edit_new_image);
    removeImage = (Button) getView().findViewById(R.id.story_edit_remove_image);

    newAudio.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {
        Utils.launchSoundIntent(getActivity(), null);
      }

    });
    removeAudio.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {
        audioLocation.setText(""); // TODO: Delete file from OS
        removeAudio.setEnabled(false);
      }

    });
    newVideo.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {
        Utils.launchVideoCameraIntent(getActivity(), null);
      }

    });
    removeVideo.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {
        videoLocation.setText(""); // TODO: Delete file from OS
        removeVideo.setEnabled(false);
      }

    });
    newImage.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {
        Utils.launchCameraIntent(getActivity(), EditStoryFragment.this, null);
      }

    });
    removeImage.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {
        imageLocation.setText(""); // TODO: Delete file from OS
        imageNameText.setEnabled(false);
        removeImage.setEnabled(false);
      }

    });

    saveButton.setOnClickListener(myOnClickListener);
    resetButton.setOnClickListener(myOnClickListener);
    cancelButton.setOnClickListener(myOnClickListener);

    setValuesToDefault();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.i("EditStoryFragment", "Entered onActivityResult in EditStoryFragment. resultCode = " + resultCode
        + ". requestCode = " + requestCode);
    switch (requestCode) {
    case Constants.CAMERA_PIC_REQUEST:
      if (resultCode == CreateStoryActivity.RESULT_OK) {
        // Image captured and saved to fileUri specified in the Intent
        if (data != null) {
          imagePath = data.getData();
        }
        imageLocation.setText(imagePath.toString());
        imageNameText.setEnabled(true);
        removeImage.setEnabled(true);
      }
      break;
    case Constants.MIC_SOUND_REQUEST:
      if (resultCode == EditStoryActivity.RESULT_OK) {
        // Audio captured and saved to fileUri specified in the Intent
        String audioPath = (String) data.getExtras().get("data");
        Log.i("EditStoryFragment", audioPath);
        audioLocation.setText("file://" + audioPath.toString());
        removeAudio.setEnabled(true);
      }
      break;
    case Constants.CAMERA_VIDEO_REQUEST:
      if (resultCode == CreateStoryActivity.RESULT_OK) {
        // Video captured and saved to fileUri specified in the Intent
        videoLocation.setText(data.getData().toString());
        removeVideo.setEnabled(true);
      }
      break;
    default:
      throw new IllegalArgumentException("Invalid request code: " + requestCode);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.story_edit_fragment, container, false);
    container.setBackgroundColor(Color.GRAY);
    return view;
  }

  private void doResetButtonClick() {
    Preferences preferences = new Preferences(getActivity());
    preferences.clearPreferences();
    setValuesToDefault();
  }

  private void doSaveButtonClick() {
    Toast.makeText(getActivity(), "Updated.", Toast.LENGTH_SHORT).show();
    StoryData location = makeStoryDataFromUI();
    if (location == null) {
      return;
    }
    try {
      resolver.updateStoryWithID(location);
    }
    catch (RemoteException e) {
      e.printStackTrace();
      return;
    }
    Preferences preferences = new Preferences(getActivity());
    preferences.clearPreferences();
    if (getResources().getBoolean(R.bool.isTablet)) {
      mOpener.openViewStoryFragment(getUniqueKey());
    }
    else {
      getActivity().finish(); // same as hitting the Back button
    }
  }

  private StoryData makeStoryDataFromUI() {

    Editable titleEditable = titleText.getText();
    Editable bodyEditable = bodyText.getText();
    String audioLinkEditable = (String) audioLocation.getText();
    String videoLinkEditable = (String) videoLocation.getText();
    Editable imageNameEditable = imageNameText.getText();
    String imageMetaDataEditable = (String) imageLocation.getText();
    Editable tagsEditable = tagsText.getText();
    String storyTimeEditable = (String) storyTime.getText();
    Editable latitudeEditable = latitudeText.getText();
    Editable longitudeEditable = longitudeText.getText();

    date = Utils.parseDateTime(storyTimeEditable.toString());
    if (date == null) {
      date = new Date();
    }

    long loginId = 0;
    long storyId = 0;
    long creationTime = 0;

    String title = titleEditable.toString();
    String body = bodyEditable.toString();
    String audioLink = audioLinkEditable.toString();
    String videoLink = videoLinkEditable.toString();
    String imageName = imageNameEditable.toString();
    String imageMetaData = imageMetaDataEditable.toString();
    String tags = tagsEditable.toString();
    long storyTime = date.getTime();
    double latitude = Double.valueOf(latitudeEditable.toString());
    double longitude = Double.valueOf(longitudeEditable.toString());

    return new StoryData(getUniqueKey(), loginId, storyId, title, body, audioLink, videoLink, imageName, imageMetaData,
        tags, creationTime, storyTime, latitude, longitude);

  }

  private void doCancelButtonClick() {
    Preferences preferences = new Preferences(getActivity());
    preferences.clearPreferences();
    if (getResources().getBoolean(R.bool.isTablet)) {
      mOpener.openViewStoryFragment(getUniqueKey());
    }
    else {
      getActivity().finish(); // same as hitting the Back button
    }

  }

  private boolean setValuesToDefault() {

    Preferences prefs = new Preferences(getActivity());

    StoryData storyData;
    try {
      storyData = resolver.getStoryDataViaRowID(getUniqueKey());
    }
    catch (RemoteException e) {
      Log.d(LOG_TAG, e.getMessage());
      e.printStackTrace();
      return false;
    }

    if (storyData == null) {
      return false;
    }

    Log.d(LOG_TAG, "setValuesToDefault: " + storyData);
    titleText.setText(prefs.hasValue(Constants.TITLE_EDIT) ? prefs.getString(Constants.TITLE_EDIT) : storyData.getTitle());
    bodyText.setText(prefs.hasValue(Constants.BODY_EDIT) ? prefs.getString(Constants.BODY_EDIT) : storyData.getBody());
    String audioLink = storyData.getAudioLink();
    if (!prefs.hasValue(Constants.AUDIO_LOCATION_EDIT) && audioLink.isEmpty()) {
      removeAudio.setEnabled(false);
    }
    else {
      audioLink = String.format("file:///%s", audioLink);
    }
    audioLocation.setText(prefs.hasValue(Constants.AUDIO_LOCATION_EDIT) ? prefs.getString(Constants.AUDIO_LOCATION_EDIT)
        : audioLink);
    String videoLink = storyData.getVideoLink();
    removeVideo.setEnabled(!videoLink.isEmpty());
    videoLocation.setText(prefs.hasValue(Constants.VIDEO_LOCATION_EDIT) ? prefs.getString(Constants.VIDEO_LOCATION_EDIT)
        : videoLink);
    String imageLink = storyData.getImageLink();
    imageNameText.setEnabled(!imageLink.isEmpty());
    imageNameText.setText(prefs.hasValue(Constants.IMAGE_NAME_EDIT) ? prefs.getString(Constants.IMAGE_NAME_EDIT) : storyData
        .getImageName());
    removeImage.setEnabled(!imageLink.isEmpty());
    imageLocation.setText(prefs.hasValue(Constants.IMAGE_LOCATION_EDIT) ? prefs.getString(Constants.IMAGE_LOCATION_EDIT)
        : imageLink);
    tagsText.setText(storyData.getTags());
    storyTime.setText(prefs.hasValue(Constants.STORY_TIME_EDIT) ? prefs.getString(Constants.STORY_TIME_EDIT) : Utils
        .formatDateTime(storyData.getStoryTime()));
    String lat = String.valueOf(storyData.getLatitude());
    latitudeText.setText(prefs.hasValue(Constants.LATITUDE_EDIT) ? prefs.getString(Constants.LATITUDE_EDIT) : lat);
    String lng = String.valueOf(storyData.getLongitude());
    longitudeText.setText(prefs.hasValue(Constants.LONGITUDE_EDIT) ? prefs.getString(Constants.LONGITUDE_EDIT) : lng);
    return true;
  }

  long getUniqueKey() {
    return getArguments().getLong(ROW_IDENTIFIER_TAG, 0);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    saveFields();
  }

  private void saveFields() {
    Preferences preferences = new Preferences(getActivity());
    preferences.saveString(Constants.TITLE_EDIT, titleText.getText().toString());
    preferences.saveString(Constants.BODY_EDIT, bodyText.getText().toString());
    preferences.saveString(Constants.STORY_TIME_EDIT, storyTime.getText().toString());
    preferences.saveString(Constants.LATITUDE_EDIT, latitudeText.getText().toString());
    preferences.saveString(Constants.LONGITUDE_EDIT, longitudeText.getText().toString());
    // text.audioName = audioNameText.getText().toString();
    // text.videoName = videoNameText.getText().toString();
    preferences.saveString(Constants.IMAGE_NAME_EDIT, imageNameText.getText().toString());
    preferences.saveString(Constants.AUDIO_LOCATION_EDIT, audioLocation.getText().toString());
    preferences.saveString(Constants.VIDEO_LOCATION_EDIT, videoLocation.getText().toString());
    preferences.saveString(Constants.IMAGE_LOCATION_EDIT, imageLocation.getText().toString());
  }

}
