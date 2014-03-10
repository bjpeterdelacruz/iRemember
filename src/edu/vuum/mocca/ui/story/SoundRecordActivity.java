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

import java.io.File;
import java.io.IOException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * This activity will capture audio.
 * 
 * @author Scott
 * @author BJ Peter DeLaCruz
 * @see <a href=
 * 'http://developer.android.com/guide/topics/media/audio-capture.html'>Website</a>
 */
public class SoundRecordActivity extends Activity {

  private static final String LOG_TAG = SoundRecordActivity.class.getName();
  static final String EXTRA_OUTPUT = "OUTPUT_FILENAME";
  private static String mFileName = null;

  private boolean recorded = false;

  private RecordButton mRecordButton;
  private MediaRecorder mRecorder;

  private PlayButton mPlayButton;
  private MediaPlayer mPlayer;

  private void onRecord(boolean start) {
    if (start) {
      try {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.prepare();
        mRecorder.start();
      }
      catch (IOException e) {
        Log.e(LOG_TAG, e.getMessage());
      }
    }
    else {
      mRecorder.stop();
      mRecorder.release();
      mRecorder = null;
      recorded = true;
    }
  }

  private void onPlay(boolean start) {
    if (start) {
      try {
        mPlayer = new MediaPlayer();
        mPlayer.setDataSource(mFileName);
        mPlayer.prepare();
        mPlayer.start();
      }
      catch (IOException e) {
        Log.e(LOG_TAG, e.getMessage());
      }
    }
    else {
      mPlayer.release();
      mPlayer = null;
    }
  }

  private class RecordButton extends Button {
    private boolean mStartRecording = true;

    private OnClickListener clicker = new OnClickListener() {
      public void onClick(View v) {
        onRecord(mStartRecording);
        if (mStartRecording) {
          setText("Stop recording");
        }
        else {
          Intent data = new Intent();
          data.putExtra("data", mFileName);
          setResult(RESULT_OK, data);
          setText("Start recording");
        }
        mStartRecording = !mStartRecording;
      }
    };

    public RecordButton(Context ctx) {
      super(ctx);
      setText("Start recording");
      setOnClickListener(clicker);
    }
  }

  private class PlayButton extends Button {
    private boolean mStartPlaying = true;

    private OnClickListener clicker = new OnClickListener() {
      public void onClick(View v) {
        onPlay(mStartPlaying);
        if (mStartPlaying) {
          setText("Stop playing");
        }
        else {
          setText("Start playing");
        }
        mStartPlaying = !mStartPlaying;
      }
    };

    public PlayButton(Context ctx) {
      super(ctx);
      setText("Start playing");
      setOnClickListener(clicker);
    }
  }

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    LinearLayout ll = new LinearLayout(this);
    mRecordButton = new RecordButton(this);
    ll.addView(mRecordButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT, 0));
    mPlayButton = new PlayButton(this);
    ll.addView(mPlayButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT, 0));
    setContentView(ll);

    Intent caller = getIntent();
    mFileName = new File(((Uri) caller.getExtras().get(EXTRA_OUTPUT)).getPath()).getAbsolutePath();

    Log.i(LOG_TAG, "Filename: " + mFileName);
  }

  @Override
  public void onPause() {
    super.onPause();
    if (mRecorder != null) {
      mRecorder.release();
      mRecorder = null;
    }

    if (mPlayer != null) {
      mPlayer.release();
      mPlayer = null;
    }
  }

  @Override
  public void onBackPressed() {
    if (recorded == false) {
      setResult(RESULT_CANCELED, null);
    }
    super.onBackPressed();
  }
}
