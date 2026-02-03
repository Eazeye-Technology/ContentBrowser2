package com.txkj.drawingapp.db;


import android.os.Parcel;
import android.os.Parcelable;

public class NoteItem implements Parcelable {
	private int mId;
	private String mNoteType;
	private String mCreateTime;
	private String mNoteFilePath;
	private String mNoteName;
	private String mExtId1;
	private String mExtId2;
	private String mUpdateTime;
	private String mNoteContent;

	public NoteItem() {

	}

	public NoteItem(Parcel in) {
		mId = in.readInt();
		mNoteType = in.readString();
		mCreateTime = in.readString();
		mNoteFilePath = in.readString();
		mNoteName = in.readString();
		mExtId1 = in.readString();
		mExtId2 = in.readString();
		mUpdateTime = in.readString();
		mNoteContent = in.readString();
	}

	public String getNoteFilePath() {
		return mNoteFilePath;
	}

	public void setNoteFilePath(String noteFilePath) {
		mNoteFilePath = noteFilePath;
	}

	public String getNoteType() {
		return mNoteType;
	}

	public void setNoteType(String noteType) {
		mNoteType = noteType;
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public String getNoteName() {
		return mNoteName;
	}

	public void setNoteName(String noteName) {
		mNoteName = noteName;
	}

	public String getCreateTime() {
		return mCreateTime;
	}

	public void setCreateTime(String createTime) {
		mCreateTime = createTime;
	}

	public String getExtId1() {
		return mExtId1;
	}

	public void setExtId1(String meetingId) {
		this.mExtId1 = meetingId;
	}

	public String getExtId2() {
		return mExtId2;
	}

	public void setExtId2(String agendaId) {
		this.mExtId2 = agendaId;
	}

	public String getUpdateTime() {
		return mUpdateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.mUpdateTime = updateTime;
	}

	public String getNoteContent() {
		return mNoteContent;
	}

	public void setNoteContent(String noteContent) {
		this.mNoteContent = noteContent;
	}

	public static final Creator<NoteItem> CREATOR = new Creator<NoteItem>() {
		public NoteItem createFromParcel(Parcel in) {
			return new NoteItem(in);
		}

		public NoteItem[] newArray(int size) {
			return new NoteItem[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mId);
		dest.writeString(mNoteType);
		dest.writeString(mCreateTime);
		dest.writeString(mNoteFilePath);
		dest.writeString(mNoteName);
		dest.writeString(mExtId1);
		dest.writeString(mExtId2);
		dest.writeString(mUpdateTime);
		dest.writeString(mNoteContent);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
