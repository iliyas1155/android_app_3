package pandusland;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "diplomahomebuh.db";

    //TABLES
    //Название таблицы
    private static final String TABLE_EXPENDITURE = "expenditure";
    //Столбцы таблицы
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ADDRESS= "address";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_IMAGEPATH = "img";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_ANON = "anon";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";

    public MyDBHandler(Context context, String name,
                       SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        System.out.println("DATABASEINIT");
    }

    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        System.out.println("DATABASEINIT");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EXPENDITURE_TABLE = "CREATE TABLE " +
                TABLE_EXPENDITURE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_ADDRESS  + " TEXT," + LONGITUDE + " TEXT," + LATITUDE
                + " TEXT," + COLUMN_TYPE + " TEXT,"  + COLUMN_CATEGORY + " TEXT," + COLUMN_IMAGEPATH + " TEXT," + COLUMN_COMMENT + " TEXT,"
                + " TEXT," + COLUMN_ANON + " TEXT,"  + COLUMN_USER_ID + " INTEGER," + COLUMN_USERNAME + " TEXT)";
        db.execSQL(CREATE_EXPENDITURE_TABLE);
        System.out.println("DATABASE CREATED!!!");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENDITURE);
        onCreate(db);
    }

    //Обработчики
    //ADDERS добавить затраты
    public void addRecord(String address, String type, String category, String imagepath,
                               String comment,String anon, String user_id, String username, String longitude, String latitude) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_IMAGEPATH, imagepath);
        values.put(COLUMN_COMMENT, comment);
        values.put(COLUMN_ANON, anon);
        values.put(COLUMN_USER_ID, user_id);
        values.put(COLUMN_USERNAME, username);
        values.put(LONGITUDE, longitude);
        values.put(LATITUDE, latitude);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_EXPENDITURE, null, values);
        db.close();
        System.out.println("ADDED " + address);
    }

    //DELETE
    public void deleteRecord(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXPENDITURE, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
        db.close();
    }

    //GETTERS
    public List<Pandus> getRecords() {
        List<Pandus> pandusList = new ArrayList<>();
        String selectQuery = " SELECT  * FROM " + TABLE_EXPENDITURE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                /*
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_ADDRESS  + " TEXT," +
                LONGITUDE + " TEXT," +
                LATITUDE
                COLUMN_TYPE + " TEXT,"  +
                COLUMN_CATEGORY + " TEXT," +
                COLUMN_IMAGEPATH + " TEXT," +
                COLUMN_COMMENT + " TEXT,"
                + " TEXT," +
                8COLUMN_ANON + " TEXT,"  +
                COLUMN_USER_ID + " INTEGER," +
                10COLUMN_USERNAME + " TEXT)";
        db.execSQL(CREATE_EXPENDITURE_TABLE);
        System.out.println("DATABASE CREATED!!!");
                 */
                Pandus category = new Pandus(Integer.parseInt(cursor.getString(0)),
                        Double.parseDouble(cursor.getString(2)),
                        Double.parseDouble(cursor.getString(3)),
                        cursor.getString(1),
                        cursor.getString(4),
                        cursor.getString(7),
                        "",
                        Integer.parseInt(cursor.getString(9)),
                        cursor.getString(5)
                        );
                category.anon = cursor.getString(8);
                category.uname = cursor.getString(10);
                category.file = cursor.getString(6);
                pandusList.add(category);
            } while (cursor.moveToNext());
        }
        return pandusList;
    }
}