package com.txkj.drawingapp.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.provider.BaseColumns;
import android.util.Log;

import com.sys.speech.db.CustomPathDatabaseContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SDNotesDatabase extends SQLiteOpenHelper {
    private static final String TAG = "SDNDb";
    private final static boolean USE_WAL = true;

	private Context mContext;

	public static final String DATABASE_NAME = "notes.db";
	//if modify this version, please sync to contentbrowser2 and pngnote2
	private static final int DATABASE_VERSION = 3; //don't modify this version

	public static abstract class NoteDatabaseItem implements BaseColumns {
		public static final String TABLE_NAME = "notes";

		public static final String COLUMN_NAME_NOTE_NAME = "noteName";
		public static final String COLUMN_NAME_NOTE_FILE_PATH = "noteFilePath";
		public static final String COLUMN_NAME_NOTE_TYPE = "noteType";
		public static final String COLUMN_NAME_CREATE_TIME = "createTime";
		public static final String COLUMN_NAME_UPDATE_TIME = "updateTime";
		public static final String COLUMN_NAME_NOTE_CONTENT = "noteContent";
		public static final String COLUMN_NAME_EXT_ID1 = "extId1";
		public static final String COLUMN_NAME_EXT_ID2 = "extId2";
        public static final String COLUMN_NAME_NOTE_META = "noteMeta";

        public static final String COLUMN_NAME_EXT_CONTENT1 = "extContent1";
        public static final String COLUMN_NAME_EXT_CONTENT2 = "extContent2";
        public static final String COLUMN_NAME_EXT_CONTENT3 = "extContent3";
        public static final String COLUMN_NAME_EXT_CONTENT4 = "extContent4";
        public static final String COLUMN_NAME_EXT_CONTENT5 = "extContent5";
        public static final String COLUMN_NAME_EXT_CONTENT6 = "extContent6";
        public static final String COLUMN_NAME_EXT_CONTENT7 = "extContent7";
        public static final String COLUMN_NAME_EXT_CONTENT8 = "extContent8";
        public static final String COLUMN_NAME_EXT_CONTENT9 = "extContent9";
        public static final String COLUMN_NAME_EXT_CONTENT10 = "extContent10";
	}

	public interface OnDatabaseChangedListener {
		void onDatabaseEntryUpdated();
	}

	private OnDatabaseChangedListener mOnDatabaseChangedListener;

	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES =
			"CREATE TABLE " + NoteDatabaseItem.TABLE_NAME + " (" +
					NoteDatabaseItem._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
					NoteDatabaseItem.COLUMN_NAME_NOTE_NAME + TEXT_TYPE + COMMA_SEP +
					NoteDatabaseItem.COLUMN_NAME_NOTE_FILE_PATH + TEXT_TYPE + COMMA_SEP +
					NoteDatabaseItem.COLUMN_NAME_NOTE_TYPE + TEXT_TYPE + COMMA_SEP +
					NoteDatabaseItem.COLUMN_NAME_CREATE_TIME + TEXT_TYPE + COMMA_SEP +
					NoteDatabaseItem.COLUMN_NAME_UPDATE_TIME + TEXT_TYPE + COMMA_SEP +
					NoteDatabaseItem.COLUMN_NAME_NOTE_CONTENT + TEXT_TYPE + COMMA_SEP +
					NoteDatabaseItem.COLUMN_NAME_EXT_ID1 + TEXT_TYPE + COMMA_SEP +
					NoteDatabaseItem.COLUMN_NAME_EXT_ID2 + TEXT_TYPE + COMMA_SEP +
                    NoteDatabaseItem.COLUMN_NAME_NOTE_META + TEXT_TYPE + COMMA_SEP +

                    NoteDatabaseItem.COLUMN_NAME_EXT_CONTENT1 + TEXT_TYPE + COMMA_SEP +
                    NoteDatabaseItem.COLUMN_NAME_EXT_CONTENT2 + TEXT_TYPE + COMMA_SEP +
                    NoteDatabaseItem.COLUMN_NAME_EXT_CONTENT3 + TEXT_TYPE + COMMA_SEP +
                    NoteDatabaseItem.COLUMN_NAME_EXT_CONTENT4 + TEXT_TYPE + COMMA_SEP +
                    NoteDatabaseItem.COLUMN_NAME_EXT_CONTENT5 + TEXT_TYPE + COMMA_SEP +
                    NoteDatabaseItem.COLUMN_NAME_EXT_CONTENT6 + TEXT_TYPE + COMMA_SEP +
                    NoteDatabaseItem.COLUMN_NAME_EXT_CONTENT7 + TEXT_TYPE + COMMA_SEP +
                    NoteDatabaseItem.COLUMN_NAME_EXT_CONTENT8 + TEXT_TYPE + COMMA_SEP +
                    NoteDatabaseItem.COLUMN_NAME_EXT_CONTENT9 + TEXT_TYPE + COMMA_SEP +
                    NoteDatabaseItem.COLUMN_NAME_EXT_CONTENT10 + TEXT_TYPE +
                    ")";

	@SuppressWarnings("unused")
	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + NoteDatabaseItem.TABLE_NAME;

	public SDNotesDatabase(Context context, String dirPath) {
		super(new CustomPathDatabaseContext(context, dirPath), DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
    }

    public long addNote(NoteItem item) {
        return addNote(item.getNoteName(), item.getNoteFilePath(), item.getNoteType(), item.getExtId1(), item.getExtId2(), item.getUpdateTime(), item.getNoteContent());
    }

	public long addNote(String noteName, String noteFilePath, String noteType, String noteExtId1, String noteExtId2, String updateTime, String noteContent) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            if (USE_WAL) db.enableWriteAheadLogging();
            ContentValues values = new ContentValues();
            values.put(NoteDatabaseItem.COLUMN_NAME_NOTE_NAME, noteName);
            values.put(NoteDatabaseItem.COLUMN_NAME_NOTE_FILE_PATH, noteFilePath);
            values.put(NoteDatabaseItem.COLUMN_NAME_NOTE_TYPE, noteType);
            values.put(NoteDatabaseItem.COLUMN_NAME_CREATE_TIME, System.currentTimeMillis());
            values.put(NoteDatabaseItem.COLUMN_NAME_UPDATE_TIME, updateTime);
            values.put(NoteDatabaseItem.COLUMN_NAME_NOTE_CONTENT, noteContent);
            values.put(NoteDatabaseItem.COLUMN_NAME_EXT_ID1, noteExtId1);
            values.put(NoteDatabaseItem.COLUMN_NAME_EXT_ID2, noteExtId2);

            long rowId = db.insert(NoteDatabaseItem.TABLE_NAME, null, values);

            Log.e(TAG, "==============addRecording noteExtId1 = " + noteExtId1 + ", noteExtId2 = " + noteExtId2);
            db.close();
            if (mOnDatabaseChangedListener != null)
                mOnDatabaseChangedListener.onDatabaseEntryUpdated();

            return rowId;
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        return -1;
    }

    public void saveNote(List<NoteItem> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        if (USE_WAL) db.enableWriteAheadLogging();db.enableWriteAheadLogging();
        try{
            db.beginTransaction();
            //insert huge data
            //get pre-compiled SQLiteStatement object
            //SQLiteStatement statement = db.compileStatement("insert into tablename(..) value (?,?)")
            for (NoteItem row : items) {
                ContentValues values = new ContentValues();
                values.put(NoteDatabaseItem.COLUMN_NAME_NOTE_NAME, row.getNoteName());
                values.put(NoteDatabaseItem.COLUMN_NAME_NOTE_FILE_PATH, row.getNoteFilePath());
                values.put(NoteDatabaseItem.COLUMN_NAME_NOTE_TYPE, row.getNoteType());
                values.put(NoteDatabaseItem.COLUMN_NAME_CREATE_TIME, System.currentTimeMillis());
                values.put(NoteDatabaseItem.COLUMN_NAME_UPDATE_TIME, row.getUpdateTime());
                values.put(NoteDatabaseItem.COLUMN_NAME_NOTE_CONTENT, row.getNoteContent());
                values.put(NoteDatabaseItem.COLUMN_NAME_EXT_ID1, row.getExtId1());
                values.put(NoteDatabaseItem.COLUMN_NAME_EXT_ID2, row.getExtId2());

                db.insert(NoteDatabaseItem.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally{
            db.endTransaction();
            db.close();
        }

        if (mOnDatabaseChangedListener != null)
            mOnDatabaseChangedListener.onDatabaseEntryUpdated();
    }

    @SuppressLint("Range")
    public List<NoteItem> getAllItems() {
        Map<String, NoteItem> nameResult = new HashMap<>();
        List<NoteItem> result = new ArrayList<>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            if (USE_WAL) db.enableWriteAheadLogging();
            String[] projection = {
                    NoteDatabaseItem._ID,
                    NoteDatabaseItem.COLUMN_NAME_NOTE_NAME,
                    NoteDatabaseItem.COLUMN_NAME_NOTE_FILE_PATH,
                    NoteDatabaseItem.COLUMN_NAME_NOTE_TYPE,
                    NoteDatabaseItem.COLUMN_NAME_CREATE_TIME,
                    NoteDatabaseItem.COLUMN_NAME_UPDATE_TIME,
                    NoteDatabaseItem.COLUMN_NAME_NOTE_CONTENT,
                    NoteDatabaseItem.COLUMN_NAME_EXT_ID1,
                    NoteDatabaseItem.COLUMN_NAME_EXT_ID2
            };

            String orderBy = NoteDatabaseItem._ID + " ASC";
            Cursor c = db.query(NoteDatabaseItem.TABLE_NAME, projection,
                    null, //"_id=?",//NoteDatabaseItem.COLUMN_NAME_EXT_ID1 + " = ? and _id=? ",
                    null, //new String[]{String.valueOf(position)},
                    null, null, orderBy);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { //API 28, Android 9.0
                //Exception occur here: c.moveToNext()
                //android.database.sqlite.SQLiteBlobTooBigException: Row too big to fit into CursorWindow requiredPos

                //https://cloud.tencent.com/developer/ask/sof/115327221
                //https://stackoverflow.com/questions/51959944
                //https://android.googlesource.com/platform/cts/+/master/tests/tests/database/src/android/database/sqlite/cts/SQLiteCursorTest.java
                CursorWindow cw = null;
                cw = new CursorWindow("test", 1024L * 1024L * 100L/*5000*/);
                AbstractWindowedCursor ac = (AbstractWindowedCursor) c;
                ac.setWindow(cw);
            }

            if (c.moveToPosition(0)) {
                do {
                    NoteItem item = new NoteItem();
                    item.setId(c.getInt(c.getColumnIndex(NoteDatabaseItem._ID)));
                    item.setNoteType(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_NOTE_TYPE)));
                    item.setNoteFilePath(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_NOTE_FILE_PATH)));
                    item.setNoteName(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_NOTE_NAME)));
                    item.setCreateTime(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_CREATE_TIME)));
                    item.setUpdateTime(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_UPDATE_TIME)));
                    item.setNoteContent(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_NOTE_CONTENT)));
                    item.setExtId1(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_EXT_ID1)));
                    item.setExtId2(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_EXT_ID2)));

                    if (item.getNoteFilePath() != null) {
                        if (nameResult.get(item.getNoteFilePath()) == null) {
                            nameResult.put(item.getNoteFilePath(), item);
                        } else {
                            NoteItem item2 = nameResult.get(item.getNoteFilePath());
                            if (item2 != null) {
                                long updateTime1 = 0L;
                                long updateTime2 = 0L;
                                try {
                                    updateTime1 = Long.parseLong(item.getUpdateTime());
                                } catch (Throwable eee) {
                                    eee.printStackTrace();
                                }
                                try {
                                    updateTime2 = Long.parseLong(item2.getUpdateTime());
                                } catch (Throwable eee) {
                                    eee.printStackTrace();
                                }
                                if (updateTime2 > updateTime1) {
                                    nameResult.put(item2.getNoteFilePath(), item2);
                                }
                            }
                        }
                    }
                } while (c.moveToNext());
                //Exception occur here: c.moveToNext()
                //android.database.sqlite.SQLiteBlobTooBigException: Row too big to fit into CursorWindow requiredPos
            }
            c.close();
            db.close();
        } catch (Throwable eee) {
            eee.printStackTrace();
        } finally {
        }

        for (String noteFilePath : nameResult.keySet()) {
            result.add(nameResult.get(noteFilePath));
        }
        Collections.sort(result, new Comparator<NoteItem>() {
            @Override
            public int compare(NoteItem item1, NoteItem item2) {
                long updateTime1 = 0L;
                long updateTime2 = 0L;
                try {
                    updateTime1 = Long.parseLong(item1.getUpdateTime());
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }
                try {
                    updateTime2 = Long.parseLong(item2.getUpdateTime());
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }
                return -Long.valueOf(updateTime1).compareTo(Long.valueOf(updateTime2));
            }
        });

        return result;
    }

    @SuppressLint("Range")
    public NoteItem getItemAtNew(int position) {
        try {
            SQLiteDatabase db = getReadableDatabase();
            if (USE_WAL) db.enableWriteAheadLogging();
            String[] projection = {
                    NoteDatabaseItem._ID,
                    NoteDatabaseItem.COLUMN_NAME_NOTE_NAME,
                    NoteDatabaseItem.COLUMN_NAME_NOTE_FILE_PATH,
                    NoteDatabaseItem.COLUMN_NAME_NOTE_TYPE,
                    NoteDatabaseItem.COLUMN_NAME_CREATE_TIME,
                    NoteDatabaseItem.COLUMN_NAME_UPDATE_TIME,
                    NoteDatabaseItem.COLUMN_NAME_NOTE_CONTENT,
                    NoteDatabaseItem.COLUMN_NAME_EXT_ID1,
                    NoteDatabaseItem.COLUMN_NAME_EXT_ID2
            };

            String orderBy = NoteDatabaseItem._ID + " ASC";
            if (false) {
                //too slow
                Cursor c = db.query(NoteDatabaseItem.TABLE_NAME, projection,
                        null, //NoteDatabaseItem.COLUMN_NAME_EXT_ID1 + " = ?",
                        new String[]{},
                        null, null, orderBy);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { //API 28, Android 9.0
                    //Exception occur here: c.moveToNext()
                    //android.database.sqlite.SQLiteBlobTooBigException: Row too big to fit into CursorWindow requiredPos

                    //https://cloud.tencent.com/developer/ask/sof/115327221
                    //https://stackoverflow.com/questions/51959944
                    //https://android.googlesource.com/platform/cts/+/master/tests/tests/database/src/android/database/sqlite/cts/SQLiteCursorTest.java
                    CursorWindow cw = null;
                    cw = new CursorWindow("test", 1024L * 1024L * 100L/*5000*/);
                    AbstractWindowedCursor ac = (AbstractWindowedCursor) c;
                    ac.setWindow(cw);
                }

                if (c.moveToPosition(position)) {
                    NoteItem item = new NoteItem();
                    item.setId(c.getInt(c.getColumnIndex(NoteDatabaseItem._ID)));
                    item.setNoteType(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_NOTE_TYPE)));
                    item.setNoteFilePath(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_NOTE_FILE_PATH)));
                    item.setNoteName(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_NOTE_NAME)));
                    item.setCreateTime(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_CREATE_TIME)));
                    item.setUpdateTime(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_UPDATE_TIME)));
                    item.setNoteContent(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_NOTE_CONTENT)));
                    item.setExtId1(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_EXT_ID1)));
                    item.setExtId2(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_EXT_ID2)));
                    c.close();
                    return item;
                }
            } else {
                Cursor c = db.query(NoteDatabaseItem.TABLE_NAME, projection,
                        "_id=?",//NoteDatabaseItem.COLUMN_NAME_EXT_ID1 + " = ? and _id=? ",
                        new String[]{String.valueOf(position)},
                        null, null, orderBy);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { //API 28, Android 9.0
                    //Exception occur here: c.moveToNext()
                    //android.database.sqlite.SQLiteBlobTooBigException: Row too big to fit into CursorWindow requiredPos

                    //https://cloud.tencent.com/developer/ask/sof/115327221
                    //https://stackoverflow.com/questions/51959944
                    //https://android.googlesource.com/platform/cts/+/master/tests/tests/database/src/android/database/sqlite/cts/SQLiteCursorTest.java
                    CursorWindow cw = null;
                    cw = new CursorWindow("test", 1024L * 1024L * 100L/*5000*/);
                    AbstractWindowedCursor ac = (AbstractWindowedCursor) c;
                    ac.setWindow(cw);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { //API 28, Android 9.0
                    //Exception occur here: c.moveToNext()
                    //android.database.sqlite.SQLiteBlobTooBigException: Row too big to fit into CursorWindow requiredPos

                    //https://cloud.tencent.com/developer/ask/sof/115327221
                    //https://stackoverflow.com/questions/51959944
                    //https://android.googlesource.com/platform/cts/+/master/tests/tests/database/src/android/database/sqlite/cts/SQLiteCursorTest.java
                    CursorWindow cw = null;
                    cw = new CursorWindow("test", 1024L * 1024L * 100L/*5000*/);
                    AbstractWindowedCursor ac = (AbstractWindowedCursor) c;
                    ac.setWindow(cw);
                }

                if (c.moveToPosition(0)) {
                    NoteItem item = new NoteItem();
                    item.setId(c.getInt(c.getColumnIndex(NoteDatabaseItem._ID)));
                    item.setNoteType(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_NOTE_TYPE)));
                    item.setNoteFilePath(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_NOTE_FILE_PATH)));
                    item.setNoteName(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_NOTE_NAME)));
                    item.setCreateTime(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_CREATE_TIME)));
                    item.setUpdateTime(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_UPDATE_TIME)));
                    item.setNoteContent(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_NOTE_CONTENT)));
                    item.setExtId1(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_EXT_ID1)));
                    item.setExtId2(c.getString(c.getColumnIndex(NoteDatabaseItem.COLUMN_NAME_EXT_ID2)));
                    c.close();
                    return item;
                }
            }
            db.close();
        } catch (Throwable eee) {
            eee.printStackTrace();
        } finally {
        }

        return null;
	}

	public void removeItemWithId(int id) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            if (USE_WAL) db.enableWriteAheadLogging();db.enableWriteAheadLogging();
            String[] whereArgs = {String.valueOf(id)};
            db.delete(NoteDatabaseItem.TABLE_NAME,
                    "_id=?",
                    whereArgs);
            db.close();
            if (mOnDatabaseChangedListener != null)
                mOnDatabaseChangedListener.onDatabaseEntryUpdated();
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
	}

	public int getCountNew() {
        try {
            SQLiteDatabase db = getReadableDatabase();
            if (USE_WAL) db.enableWriteAheadLogging();
            String[] projection = {NoteDatabaseItem._ID};
            Cursor c = db.query(NoteDatabaseItem.TABLE_NAME, projection,
                    null, //NoteDatabaseItem.COLUMN_NAME_EXT_ID1 + " = ?",
                    new String[]{},
                    null, null, null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { //API 28, Android 9.0
                //Exception occur here: c.moveToNext()
                //android.database.sqlite.SQLiteBlobTooBigException: Row too big to fit into CursorWindow requiredPos

                //https://cloud.tencent.com/developer/ask/sof/115327221
                //https://stackoverflow.com/questions/51959944
                //https://android.googlesource.com/platform/cts/+/master/tests/tests/database/src/android/database/sqlite/cts/SQLiteCursorTest.java
                CursorWindow cw = null;
                cw = new CursorWindow("test", 1024L * 1024L * 100L/*5000*/);
                AbstractWindowedCursor ac = (AbstractWindowedCursor) c;
                ac.setWindow(cw);
            }

            int count = 0;
            if (false) {
                count = c.getCount();
            } else {
                if (c.moveToFirst()) {
                    do {
//                            RecordingItem item = new RecordingItem();
//                            item.setId(c.getInt(c.getColumnIndex(RecordingDatabaseItem._ID)));
//                            item.setLength(c.getInt(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_RECORDING_LENGTH)));
//                            item.setFilePath(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_RECORDING_FILE_PATH)));
//                            item.setName(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_RECORDING_NAME)));
//                            item.setTime(c.getLong(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_TIME_ADDED)));
//                            item.setRecType(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_REC_TYPE)));
//                            item.setRecContent(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_REC_CONTENT)));
//                            item.setMeetingId(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_MEETING_ID)));
//                            item.setAgendaId(c.getString(c.getColumnIndex(RecordingDatabaseItem.COLUMN_NAME_AGENDA_ID)));
//                            memdb.add(item);
                        count++;
                    } while (c.moveToNext());
                }
            }
            c.close();
            db.close();
            return count;
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        return 0;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_CREATE_ENTRIES);
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// onUpgrade(db, oldVersion, newVersion);
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + NoteDatabaseItem.TABLE_NAME + " ADD COLUMN " +
                    NoteDatabaseItem.COLUMN_NAME_EXT_CONTENT1 + TEXT_TYPE + "");
            db.execSQL("ALTER TABLE " + NoteDatabaseItem.TABLE_NAME + " ADD COLUMN " +
                    NoteDatabaseItem.COLUMN_NAME_EXT_CONTENT2 + TEXT_TYPE + "");
            db.execSQL("ALTER TABLE " + NoteDatabaseItem.TABLE_NAME + " ADD COLUMN " +
                    NoteDatabaseItem.COLUMN_NAME_EXT_CONTENT3 + TEXT_TYPE + "");
            db.execSQL("ALTER TABLE " + NoteDatabaseItem.TABLE_NAME + " ADD COLUMN " +
                    NoteDatabaseItem.COLUMN_NAME_EXT_CONTENT4 + TEXT_TYPE + "");
            db.execSQL("ALTER TABLE " + NoteDatabaseItem.TABLE_NAME + " ADD COLUMN " +
                    NoteDatabaseItem.COLUMN_NAME_EXT_CONTENT5 + TEXT_TYPE + "");
            db.execSQL("ALTER TABLE " + NoteDatabaseItem.TABLE_NAME + " ADD COLUMN " +
                    NoteDatabaseItem.COLUMN_NAME_EXT_CONTENT6 + TEXT_TYPE + "");
            db.execSQL("ALTER TABLE " + NoteDatabaseItem.TABLE_NAME + " ADD COLUMN " +
                    NoteDatabaseItem.COLUMN_NAME_EXT_CONTENT7 + TEXT_TYPE + "");
            db.execSQL("ALTER TABLE " + NoteDatabaseItem.TABLE_NAME + " ADD COLUMN " +
                    NoteDatabaseItem.COLUMN_NAME_EXT_CONTENT8 + TEXT_TYPE + "");
            db.execSQL("ALTER TABLE " + NoteDatabaseItem.TABLE_NAME + " ADD COLUMN " +
                    NoteDatabaseItem.COLUMN_NAME_EXT_CONTENT9 + TEXT_TYPE + "");
            db.execSQL("ALTER TABLE " + NoteDatabaseItem.TABLE_NAME + " ADD COLUMN " +
                    NoteDatabaseItem.COLUMN_NAME_EXT_CONTENT10 + TEXT_TYPE + "");
        }
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + NoteDatabaseItem.TABLE_NAME + " ADD COLUMN " +
                    NoteDatabaseItem.COLUMN_NAME_NOTE_META + TEXT_TYPE + "");
        }
	}

	public Context getContext() {
		return mContext;
	}

//	public class RecordingComparator implements Comparator<RecordingItem> {
//		public int compare(RecordingItem item1, RecordingItem item2) {
//			Long o1 = item1.getTime();
//			Long o2 = item2.getTime();
//			return o2.compareTo(o1);
//		}
//	}

//	public void renameItem(RecordingItem item, String recordingName) {
//		SQLiteDatabase db = getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(RecordingDatabaseItem.COLUMN_NAME_RECORDING_NAME, recordingName);
//		db.update(RecordingDatabaseItem.TABLE_NAME, values,
//				RecordingDatabaseItem._ID + "=" + item.getId(), null);
//
//		if (mOnDatabaseChangedListener != null)
//			mOnDatabaseChangedListener.onDatabaseEntryUpdated();
//	}

	public void setOnDatabaseChangedListener(OnDatabaseChangedListener listener) {
		mOnDatabaseChangedListener = listener;
	}

    public void updateItem(long id, NoteItem item) {
        updateItem(id, item.getNoteName(), item.getNoteFilePath(), item.getNoteType(), item.getExtId1(), item.getExtId2(), item.getUpdateTime(), item.getNoteContent());
    }

    public void updateItem(long id, String noteName, String noteFilePath, String noteType, String noteExtId1, String noteExtId2, String updateTime, String noteContent) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            if (USE_WAL) db.enableWriteAheadLogging();db.enableWriteAheadLogging();

            ContentValues values = new ContentValues();
            values.put(NoteDatabaseItem.COLUMN_NAME_NOTE_NAME, noteName);
            values.put(NoteDatabaseItem.COLUMN_NAME_NOTE_FILE_PATH, noteFilePath);
            values.put(NoteDatabaseItem.COLUMN_NAME_NOTE_TYPE, noteType);
            values.put(NoteDatabaseItem.COLUMN_NAME_CREATE_TIME, System.currentTimeMillis());
            values.put(NoteDatabaseItem.COLUMN_NAME_UPDATE_TIME, updateTime);
            values.put(NoteDatabaseItem.COLUMN_NAME_NOTE_CONTENT, noteContent);
            values.put(NoteDatabaseItem.COLUMN_NAME_EXT_ID1, noteExtId1);
            values.put(NoteDatabaseItem.COLUMN_NAME_EXT_ID2, noteExtId2);
            db.update(NoteDatabaseItem.TABLE_NAME, values,
                    NoteDatabaseItem._ID + "=" + id, null);
            db.close();

            if (mOnDatabaseChangedListener != null)
                mOnDatabaseChangedListener.onDatabaseEntryUpdated();
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
	}


    public void updateItemMeta(long id, String noteMeta) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            if (USE_WAL) db.enableWriteAheadLogging();db.enableWriteAheadLogging();

            ContentValues values = new ContentValues();
            values.put(NoteDatabaseItem.COLUMN_NAME_NOTE_META, noteMeta);
            db.update(NoteDatabaseItem.TABLE_NAME, values,
                    NoteDatabaseItem._ID + "=" + id, null);
            db.close();

            if (mOnDatabaseChangedListener != null)
                mOnDatabaseChangedListener.onDatabaseEntryUpdated();
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
    }
}
