package edu.vuum.mocca.ui.story;

public final class Constants {

  static final String EXTRA_OUTPUT = "OUTPUT_FILENAME";
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
