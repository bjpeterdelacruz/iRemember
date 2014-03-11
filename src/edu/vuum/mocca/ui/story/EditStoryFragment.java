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
import android.graphics.Color;
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

public class EditStoryFragment extends Fragment {

  private final static String LOG_TAG = EditStoryFragment.class.getCanonicalName();
  // Variable for passing around row index
  final static String ROW_IDENTIFIER_TAG = "index";
  private EditText titleET;
  private EditText bodyET;
  private TextView audioLinkET;
  private TextView videoLinkET;
  private EditText imageTitleET;
  private TextView imageMetaDataET;
  private EditText tagsET;
  private TextView storyTimeET;
  private Date date;
  private EditText latitudeET;
  private EditText longitudeET;

  private Button saveButton;
  private Button resetButton;
  private Button cancelButton;

  // Parent activity
  private OnOpenWindowInterface mOpener;
  // Custom ContentResolver wrapper.
  private MoocResolver resolver;

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

    titleET = (EditText) getView().findViewById(R.id.story_edit_title);
    bodyET = (EditText) getView().findViewById(R.id.story_edit_body);
    audioLinkET = (TextView) getView().findViewById(R.id.story_edit_audio_link);
    videoLinkET = (TextView) getView().findViewById(R.id.story_edit_video_link);
    imageTitleET = (EditText) getView().findViewById(R.id.story_edit_image_title);
    imageMetaDataET = (TextView) getView().findViewById(R.id.story_edit_image_meta_data);
    tagsET = (EditText) getView().findViewById(R.id.story_edit_tags);
    storyTimeET = (TextView) getView().findViewById(R.id.story_edit_story_time);
    latitudeET = (EditText) getView().findViewById(R.id.story_edit_latitude);
    longitudeET = (EditText) getView().findViewById(R.id.story_edit_longitude);

    saveButton.setOnClickListener(myOnClickListener);
    resetButton.setOnClickListener(myOnClickListener);
    cancelButton.setOnClickListener(myOnClickListener);

    setValuesToDefault();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.story_edit_fragment, container, false);
    container.setBackgroundColor(Color.GRAY);
    return view;
  }

  private void doResetButtonClick() {
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
    if (getResources().getBoolean(R.bool.isTablet) == true) {
      mOpener.openViewStoryFragment(getUniqueKey());
    }
    else {
      getActivity().finish(); // same as hitting the Back button
    }
  }

  private StoryData makeStoryDataFromUI() {

    Editable titleEditable = titleET.getText();
    Editable bodyEditable = bodyET.getText();
    String audioLinkEditable = (String) audioLinkET.getText();
    String videoLinkEditable = (String) videoLinkET.getText();
    Editable imageNameEditable = imageTitleET.getText();
    String imageMetaDataEditable = (String) imageMetaDataET.getText();
    Editable tagsEditable = tagsET.getText();
    String storyTimeEditable = (String) storyTimeET.getText();
    Editable latitudeEditable = latitudeET.getText();
    Editable longitudeEditable = longitudeET.getText();

    try {
      date = StoryData.FORMAT.parse(storyTimeEditable.toString());
    }
    catch (ParseException e1) {
      Log.e("CreateStoryFragment", "Date was not parsable, reverting to current time");
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
    if (getResources().getBoolean(R.bool.isTablet) == true) {
      mOpener.openViewStoryFragment(getUniqueKey());
    }
    else {
      getActivity().finish(); // same as hitting the Back button
    }

  }

  private boolean setValuesToDefault() {

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
    titleET.setText(storyData.getTitle());
    bodyET.setText(storyData.getBody());
    audioLinkET.setText("file:///" + String.valueOf(storyData.getAudioLink()).toString());
    videoLinkET.setText(storyData.getVideoLink());
    imageTitleET.setText(storyData.getImageName());
    imageMetaDataET.setText(storyData.getImageLink());
    tagsET.setText(storyData.getTags());
    storyTimeET.setText(StoryData.FORMAT.format(storyData.getStoryTime()));
    latitudeET.setText(String.valueOf(storyData.getLatitude()));
    longitudeET.setText(String.valueOf(storyData.getLongitude()));
    return true;
  }

  long getUniqueKey() {
    return getArguments().getLong(ROW_IDENTIFIER_TAG, 0);
  }

}
