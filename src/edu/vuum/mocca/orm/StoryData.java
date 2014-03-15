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

package edu.vuum.mocca.orm;

import java.text.SimpleDateFormat;
import java.util.Locale;
import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Custom ORM container class, for Story Data.
 * <p>
 * This class is meant as a helper class for those working with the ContentProvider. The use of this class is completely
 * optional.
 * <p>
 * ORM = Object Relational Mapping http://en.wikipedia.org/wiki/Object-relational_mapping
 * <p>
 * This class is a simple one-off POJO class with some simple ORM additions that allow for conversion between the
 * incompatible types of the POJO java classes, the 'ContentValues', and the 'Cursor' classes from the use with
 * ContentProviders.
 * 
 * @author Michael A. Walker
 * 
 */
public class StoryData implements Parcelable {

  private final long keyId;
  private final long loginId;
  private final long storyId;
  private final String title;
  private final String body;
  private final String audioLink;
  private final String videoLink;
  private final String imageName;
  private final String imageLink;
  private final String tags;
  private final long creationTime;
  private final long storyTime;
  private final double latitude;
  private final double longitude;

  /**
   * Constructor WITHOUT _id, this creates a new object for insertion into the ContentProvider
   * 
   * @param loginId
   * @param storyId
   * @param title
   * @param body
   * @param audioLink
   * @param videoLink
   * @param imageName
   * @param imageLink
   * @param tags
   * @param creationTime
   * @param storyTime
   * @param latitude
   * @param longitude
   */
  public StoryData(long loginId, long storyId, String title, String body, String audioLink, String videoLink,
      String imageName, String imageMetaData, String tags, long creationTime, long storyTime, double latitude,
      double longitude) {
    this.keyId = -1;
    this.loginId = loginId;
    this.storyId = storyId;
    this.title = title;
    this.body = body;
    this.audioLink = audioLink;
    this.videoLink = videoLink;
    this.imageName = imageName;
    this.imageLink = imageMetaData;
    this.tags = tags;
    this.creationTime = creationTime;
    this.storyTime = storyTime;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * Constructor WITH _id, this creates a new object for use when pulling already existing object's information from the
   * ContentProvider
   * 
   * @param keyId
   * @param loginId
   * @param storyId
   * @param title
   * @param body
   * @param audioLink
   * @param videoLink
   * @param imageName
   * @param imageLink
   * @param tags
   * @param creationTime
   * @param storyTime
   * @param latitude
   * @param longitude
   */
  public StoryData(long keyId, long loginId, long storyId, String title, String body, String audioLink,
      String videoLink, String imageName, String imageLink, String tags, long creationTime, long storyTime,
      double latitude, double longitude) {
    this.keyId = keyId;
    this.loginId = loginId;
    this.storyId = storyId;
    this.title = title;
    this.body = body;
    this.audioLink = audioLink;
    this.videoLink = videoLink;
    this.imageName = imageName;
    this.imageLink = imageLink;
    this.tags = tags;
    this.creationTime = creationTime;
    this.storyTime = storyTime;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public long getKeyId() {
    return keyId;
  }

  public long getLoginId() {
    return loginId;
  }

  public long getStoryId() {
    return storyId;
  }

  public String getTitle() {
    return title;
  }

  public String getBody() {
    return body;
  }

  public String getAudioLink() {
    return audioLink;
  }

  public String getVideoLink() {
    return videoLink;
  }

  public String getImageName() {
    return imageName;
  }

  public String getImageLink() {
    return imageLink;
  }

  public String getTags() {
    return tags;
  }

  public long getCreationTime() {
    return creationTime;
  }

  public long getStoryTime() {
    return storyTime;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  @Override
  public String toString() {
    return "StoryData=[loginId=[" + loginId + "], storyId=[" + storyId + "], title=[" + title + "], body=[" + body
        + "], audioLink=[" + audioLink + "], videoLink=[" + videoLink + "], imageName=[" + imageName + "], imageLink=["
        + imageLink + "], tags=[" + tags + "], creationTime=[" + creationTime + "], storyTime=[" + storyTime
        + "], latitude=[" + latitude + "], longitude=[" + longitude + "]]";
  }

  /**
   * Helper Method that allows easy conversion of object's data into an appropriate ContentValues
   * 
   * @return contentValues A new ContentValues object
   */
  public ContentValues getCV() {
    return StoryCreator.getCVfromStory(this);
  }

  /**
   * Clone this object into a new StoryData
   */
  public StoryData clone() {
    return new StoryData(loginId, storyId, title, body, audioLink, videoLink, imageName, imageLink, tags, creationTime,
        storyTime, latitude, longitude);
  }

  // these are for parcelable interface
  @Override
  /**
   * Used for writing a copy of this object to a Parcel, do not manually call.
   */
  public int describeContents() {
    return 0;
  }

  @Override
  /**
   * Used for writing a copy of this object to a Parcel, do not manually call.
   */
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(keyId);
    dest.writeLong(loginId);
    dest.writeLong(storyId);
    dest.writeString(title);
    dest.writeString(body);
    dest.writeString(audioLink);
    dest.writeString(videoLink);
    dest.writeString(imageName);
    dest.writeString(imageLink);
    dest.writeString(tags);
    dest.writeLong(creationTime);
    dest.writeLong(storyTime);
    dest.writeDouble(latitude);
    dest.writeDouble(longitude);
  }

  /**
   * Used for writing a copy of this object to a Parcel, do not manually call.
   */
  public static final Parcelable.Creator<StoryData> CREATOR = new Parcelable.Creator<StoryData>() {
    public StoryData createFromParcel(Parcel in) {
      return new StoryData(in);
    }

    public StoryData[] newArray(int size) {
      return new StoryData[size];
    }
  };

  /**
   * Used for writing a copy of this object to a Parcel, do not manually call.
   */
  private StoryData(Parcel in) {
    keyId = in.readLong();
    loginId = in.readLong();
    storyId = in.readLong();
    title = in.readString();
    body = in.readString();
    audioLink = in.readString();
    videoLink = in.readString();
    imageName = in.readString();
    imageLink = in.readString();
    tags = in.readString();
    creationTime = in.readLong();
    storyTime = in.readLong();
    latitude = in.readDouble();
    longitude = in.readDouble();
  }

}