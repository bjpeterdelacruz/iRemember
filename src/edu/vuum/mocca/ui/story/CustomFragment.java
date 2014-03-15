package edu.vuum.mocca.ui.story;

import android.net.Uri;

public interface CustomFragment {

  public void setImagePath(Uri imagePath);

  public String getAudioFilename();

  public String getVideoFilename();

  public String getImageFilename();

}
