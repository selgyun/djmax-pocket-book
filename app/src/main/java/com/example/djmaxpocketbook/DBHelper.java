package com.example.djmaxpocketbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DBHelper extends SQLiteOpenHelper {

    Context context;

    public static final String TABLE_NAME = "songDB";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_LV = "lv";
    public static final String COLUMN_DIFFICULTY = "difficulty";
    public static final String COLUMN_FLOOR = "floor";
    public static final String COLUMN_BUTTON = "button";
    public static final String COLUMN_DLC = "dlc";
    public static final String COLUMN_ACCURACY = "accuracy";
    public static final String COLUMN_MAXCOMBO = "maxCombo";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE if not exists " + TABLE_NAME + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_TITLE + " text not null, "
                + COLUMN_LV + " integer not null, "
                + COLUMN_DIFFICULTY + " text not null, "
                + COLUMN_FLOOR + " integer not null, "
                + COLUMN_BUTTON + " text not null, "
                + COLUMN_DLC + " text not null, "
                + COLUMN_ACCURACY + " real default 0.00, "
                + COLUMN_MAXCOMBO + " integer default 0);";

        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "DROP TABLE if exists "+TABLE_NAME;

        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

    void updateSong(SQLiteDatabase db, int btnId, float acc, boolean maxCombo){
        if(acc == 100.0f){
            maxCombo = true;
        }
        if (db != null && (0.0f <= acc && acc <= 100.0f)){
            try {
                db.execSQL("UPDATE " + TABLE_NAME + " SET "
                        + COLUMN_ACCURACY + "=" + acc + ", "
                        + COLUMN_MAXCOMBO + "=" + maxCombo
                        + " WHERE _id =" + btnId
                );
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    float searchSong(SQLiteDatabase db, int btnId){
        Cursor cursor = null;
        float retAcc = 0.00f;

        try{
            cursor = db.rawQuery("SELECT * FROM "
                    + TABLE_NAME + " WHERE _id="
                    + btnId
                    ,null);
            cursor.moveToFirst();
            retAcc = cursor.getFloat(7);
            if (cursor.getInt(8) == 1){
                retAcc += 100.0f;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return retAcc;
    }

    void insertSong(SQLiteDatabase db, String title, int lv, String diff,int floor, String btn, String dlc, float accuracy, int mc){
        boolean isNewNode = true;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE title='" +title+ "' and lv='" +lv+ "';", null);
        if (cursor.moveToFirst())
            isNewNode = false;
        cursor.close();
        if (isNewNode) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, title);
            values.put(COLUMN_LV, lv);
            values.put(COLUMN_DIFFICULTY, diff);
            values.put(COLUMN_FLOOR, floor);
            values.put(COLUMN_BUTTON, btn);
            values.put(COLUMN_DLC, dlc);
            values.put(COLUMN_ACCURACY, accuracy);
            values.put(COLUMN_MAXCOMBO, mc);
            db.insert(TABLE_NAME, null, values);
            Toast.makeText(context, "Insert Success", Toast.LENGTH_LONG).show();
        }
    }
}
