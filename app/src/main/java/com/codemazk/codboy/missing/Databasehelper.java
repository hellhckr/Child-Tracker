package com.codemazk.codboy.missing;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Abhinay on 2/08/2017.
 * Contact for more info 9995028577
 * Abhinaymu@gmail.com
 * All COPYRIGHT RESERVED 2016
 */


public class Databasehelper extends SQLiteOpenHelper {
    public static final String DATABASE = "Biodata.db";
    public static final String UREGISTRATION_Table = "userregistration";

    public static final String _ID = "_id";
    public static final String NAME_field = "name";
    public static final String PHONE_field = "phone";
    public static final String ADDRESS_field = "address";
    public static final String EMAIL_field = "email";

    public static final String CHILD_Table = "child";


    public static final String CLNAME_field = "last_name";

    public static final String CDESC_field = "desc";
    public static final String CUID_field = "uid";
    public static final String CIDMARK_field = "mark";
    public static final String CIMAGE_field = "image";



    private static final int DATABASE_VERSION = 1;

    SQLiteDatabase mDB;

    public Databasehelper(Context context) {
        super(context, DATABASE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_Register_Table = "CREATE TABLE " + UREGISTRATION_Table + "("
                + _ID + " INTEGER PRIMARY KEY," + NAME_field + " TEXT," + PHONE_field + " TEXT," + ADDRESS_field + " TEXT," + EMAIL_field + " TEXT)";


        sqLiteDatabase.execSQL(CREATE_Register_Table);

        String CREATE_CHILD_Table = "CREATE TABLE " + CHILD_Table + "("
                + _ID + " INTEGER PRIMARY KEY," + NAME_field + " TEXT," + CLNAME_field + " TEXT," + CDESC_field + " TEXT," + PHONE_field + " TEXT,"
                + ADDRESS_field + " TEXT," + EMAIL_field + " TEXT," + CUID_field + " TEXT," + CIDMARK_field + " TEXT," + CIMAGE_field + " BLOB)";


        sqLiteDatabase.execSQL(CREATE_CHILD_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UREGISTRATION_Table);
        onCreate(sqLiteDatabase);

    }

    public long insertdat(String name,String phone,String addres,String email){
        getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(PHONE_field,phone);
        cv.put(NAME_field,name);
        cv.put(ADDRESS_field,addres);
        cv.put(EMAIL_field,email);
        return mDB.insert(UREGISTRATION_Table, null,cv);

    }

    // Insert the image to the Sqlite DB
    public void insertImage(byte[] imageBytes) {
        ContentValues cv = new ContentValues();
        cv.put(CIMAGE_field, imageBytes);
        mDB.insert(CHILD_Table, null, cv);
    }

    // Get the image from SQLite DB
    // We will just get the last image we just saved for convenience...
  /*  public byte[] retreiveImageFromDB() {
        Cursor cur = mDB.query(true, CHILD_Table, new String[]{CIMAGE_field,},
                null, null, null, null,
                CUID_field + " DESC", "1");
        if (cur.moveToFirst()) {
            byte[] blob = cur.getBlob(cur.getColumnIndex(CIMAGE_field));
            cur.close();
            return blob;
        }
        cur.close();
        return null;
    }
}*/

}
