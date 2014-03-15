package edu.vuum.mocca.ui.story;

public final class Constants {

  static final String TITLE_CREATE = "title_create";
  static final String BODY_CREATE = "body_create";
  static final String STORY_TIME_CREATE = "storyTime_create";
  static final String LATITUDE_CREATE = "lat_create";
  static final String LONGITUDE_CREATE = "lng_create";
  static final String AUDIO_NAME_CREATE = "audioName_create";
  static final String VIDEO_NAME_CREATE = "videoName_create";
  static final String IMAGE_NAME_CREATE = "imageName_create";
  static final String AUDIO_LOCATION_CREATE = "audioLocation_create";
  static final String VIDEO_LOCATION_CREATE = "videoLocation_create";
  static final String IMAGE_LOCATION_CREATE = "imageLocation_create";

  static final String TITLE_EDIT = "title_edit";
  static final String BODY_EDIT = "body_edit";
  static final String STORY_TIME_EDIT = "storyTime_edit";
  static final String LATITUDE_EDIT = "lat_edit";
  static final String LONGITUDE_EDIT = "lng_edit";
  static final String AUDIO_NAME_EDIT = "audioName_edit";
  static final String VIDEO_NAME_EDIT = "videoName_edit";
  static final String IMAGE_NAME_EDIT = "imageName_edit";
  static final String AUDIO_LOCATION_EDIT = "audioLocation_edit";
  static final String VIDEO_LOCATION_EDIT = "videoLocation_edit";
  static final String IMAGE_LOCATION_EDIT = "imageLocation_edit";

  static final String OUTPUT_FILENAME = "outputFilename";
  static final String REQUEST_CODE = "requestCode";

  static final int CAMERA_PIC_REQUEST = 1;
  static final int CAMERA_VIDEO_REQUEST = 2;
  static final int MIC_SOUND_REQUEST = 3;

  private Constants() {
  }

  enum MediaType {
    MEDIA_TYPE_IMAGE, MEDIA_TYPE_VIDEO, MEDIA_TYPE_AUDIO;
  }

}
