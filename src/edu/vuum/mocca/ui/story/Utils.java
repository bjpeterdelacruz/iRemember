package edu.vuum.mocca.ui.story;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import edu.vuum.mocca.ui.story.Constants.MediaType;

public final class Utils {

  private static final String LOG_TAG = Utils.class.getCanonicalName();

  private final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

  private Utils() {
  }

  static String formatDateTime(long dateTime) {
    return FORMAT.format(dateTime);
  }

  static Date parseDateTime(String dateTime) {
    try {
      return FORMAT.parse(dateTime);
    }
    catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
  }

  static Uri getOutputMediaFileUri(MediaType type, String filename) {
    Log.d(LOG_TAG, "Media type: " + type);
    String state = Environment.getExternalStorageState();
    Log.i(LOG_TAG, "External storage state: " + state);

    // TODO Store videos in a separate directory.
    File mediaStorageDir;
    if (state.equals(Environment.MEDIA_MOUNTED)) {
      mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "iRemember");
    }
    else {
      return null;
    }
    // This location works best if you want the images that are created to be shared
    // between applications and persisted after your app has been uninstalled.

    if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
      Log.e(LOG_TAG, "Failed to create directory: " + mediaStorageDir);
      return null;
    }

    String prefix = mediaStorageDir.getPath() + File.separator;
    File mediaFile;
    switch (type) {
    case MEDIA_TYPE_IMAGE:
      mediaFile = new File(prefix + generateFilename("IMG_", filename, ".jpg"));
      break;
    case MEDIA_TYPE_VIDEO:
      mediaFile = new File(prefix + generateFilename("VID_", filename, ".mp4"));
      break;
    case MEDIA_TYPE_AUDIO:
      mediaFile = new File(prefix + generateFilename("AUD_", filename, ".3gp"));
      break;
    default:
      throw new IllegalArgumentException("Unsupported media type: " + type);
    }

    return Uri.fromFile(mediaFile);
  }

  private static String generateFilename(String prefix, String filename, String suffix) {
    if (filename != null && !filename.isEmpty()) {
      return filename.endsWith(suffix) ? filename : filename + suffix;
    }
    return prefix + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + suffix;
  }

  static void launchSoundIntent(Activity activity, String filename) {
    Intent intent = new Intent(activity, SoundRecordActivity.class);
    intent.putExtra(Constants.OUTPUT_FILENAME, getOutputMediaFileUri(MediaType.MEDIA_TYPE_AUDIO, filename));
    activity.startActivityForResult(intent, Constants.MIC_SOUND_REQUEST);
  }

  static void launchCameraIntent(Activity activity, CustomFragment fragment, String filename) {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    Uri imagePath = getOutputMediaFileUri(MediaType.MEDIA_TYPE_IMAGE, filename);
    fragment.setImagePath(imagePath);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath);
    activity.startActivityForResult(intent, Constants.CAMERA_PIC_REQUEST);
  }

  static void launchVideoCameraIntent(Activity activity, String filename) {
    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputMediaFileUri(MediaType.MEDIA_TYPE_VIDEO, filename));
    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
    activity.startActivityForResult(intent, Constants.CAMERA_VIDEO_REQUEST);
  }

}
