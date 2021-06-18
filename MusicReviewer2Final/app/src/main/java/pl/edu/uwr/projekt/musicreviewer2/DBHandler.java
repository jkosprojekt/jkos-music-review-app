package pl.edu.uwr.projekt.musicreviewer2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "reviewsDB_JAVA.db";

    public static final String TABLE_REVIEWS = "reviews";

    public static final String COLUMN_ARTIST = "artist";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_GENRE = "genre";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_REVIEW = "review";
    public static final String COLUMN_ACCOUNT = "account";


    public DBHandler(Context context){
        super(context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_REVIEWS_TABLE = "CREATE TABLE " +
                TABLE_REVIEWS +
                "(" +
                COLUMN_ARTIST +
                " TEXT," +
                COLUMN_TITLE +
                " TEXT," +
                COLUMN_YEAR +
                " TEXT," +
                COLUMN_GENRE +
                " TEXT," +
                COLUMN_TYPE +
                " TEXT," +
                COLUMN_SCORE +
                " TEXT," +
                COLUMN_REVIEW +
                " TEXT," +
                COLUMN_ACCOUNT +
                " TEXT" +
                ")";
        db.execSQL(CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEWS);
        onCreate(db);
    }

    public Cursor getReview(){
        String query = "SELECT * FROM " + TABLE_REVIEWS;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query, null);
    }

    public Cursor getAccountsReviews(String account){
        String query = "SELECT * FROM "+
                TABLE_REVIEWS +
                " WHERE " +
                COLUMN_ACCOUNT +
                " = \"" +
                account +
                "\"";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query, null);
    }

    public Cursor getSearchedReviews(String search, int sort){
        String query;
        if(sort==2){
            query = "SELECT * FROM "+
                    TABLE_REVIEWS +
                    " WHERE " +
                    COLUMN_ARTIST +
                    " LIKE \"%" +
                    search +
                    "%\" OR " +
                    COLUMN_TITLE +
                    " LIKE \"%" +
                    search +
                    "%\" ORDER BY " +
                    COLUMN_ARTIST +
                    " ASC";
        }
        else if(sort==3){
            query = "SELECT * FROM "+
                    TABLE_REVIEWS +
                    " WHERE " +
                    COLUMN_ARTIST +
                    " LIKE \"%" +
                    search +
                    "%\" OR " +
                    COLUMN_TITLE +
                    " LIKE \"%" +
                    search +
                    "%\" ORDER BY " +
                    COLUMN_GENRE +
                    " ASC";
        }
        else if(sort==4){
            query = "SELECT * FROM "+
                    TABLE_REVIEWS +
                    " WHERE " +
                    COLUMN_ARTIST +
                    " LIKE \"%" +
                    search +
                    "%\" OR " +
                    COLUMN_TITLE +
                    " LIKE \"%" +
                    search +
                    "%\" ORDER BY " +
                    COLUMN_ACCOUNT +
                    " ASC";
        }
        else if(sort==5){
            query = "SELECT * FROM "+
                    TABLE_REVIEWS +
                    " WHERE " +
                    COLUMN_ARTIST +
                    " LIKE \"%" +
                    search +
                    "%\" OR " +
                    COLUMN_TITLE +
                    " LIKE \"%" +
                    search +
                    "%\" ORDER BY " +
                    COLUMN_SCORE +
                    " ASC";
        }
        else{
            query = "SELECT * FROM "+
                    TABLE_REVIEWS +
                    " WHERE " +
                    COLUMN_ARTIST +
                    " LIKE \"%" +
                    search +
                    "%\" OR " +
                    COLUMN_TITLE +
                    " LIKE \"%" +
                    search +
                    "%\" ORDER BY " +
                    COLUMN_TITLE +
                    " ASC";
        }
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query, null);
    }

    public void addReview(Review review){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ARTIST, review.getArtist());
        values.put(COLUMN_TITLE, review.getTitle());
        values.put(COLUMN_YEAR, review.getYear());
        values.put(COLUMN_GENRE, review.getGenre());
        values.put(COLUMN_SCORE, review.getScore());
        values.put(COLUMN_TYPE, review.getType());
        values.put(COLUMN_REVIEW, review.getReview());
        values.put(COLUMN_ACCOUNT, review.getAccount());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_REVIEWS, null, values);
        db.close();
    }

    public void deleteReview(String title, String account){
        String query = "SELECT * FROM " +
                TABLE_REVIEWS +
                " WHERE " +
                COLUMN_TITLE +
                " = \"" +
                title +
                "\"" +
                " AND " +
                COLUMN_ACCOUNT +
                " = \"" +
                account +
                "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            db.delete(TABLE_REVIEWS, COLUMN_TITLE + "='" + title +"' AND " + COLUMN_ACCOUNT + "='" + account + "';",
                    null);
            cursor.close();
        }
        db.close();
    }

    public void deleteContent(String account){
        String query = "SELECT * FROM " +
                TABLE_REVIEWS +
                " WHERE " +
                COLUMN_ACCOUNT +
                " = \"" +
                account +
                "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            db.delete(TABLE_REVIEWS, COLUMN_ACCOUNT + "='" + account + "';",
                    null);
            cursor.close();
        }
        db.close();
    }

    public void updateReview(String currentTitle, String artist, String title, String year, String genre, String score, String type, String review, String account){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ARTIST, artist);
        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_YEAR, year);
        contentValues.put(COLUMN_GENRE, genre);
        contentValues.put(COLUMN_SCORE, score);
        contentValues.put(COLUMN_TYPE, type);
        contentValues.put(COLUMN_REVIEW, review);
        contentValues.put(COLUMN_ACCOUNT, account);

        db.update(TABLE_REVIEWS,
                contentValues,
                COLUMN_TITLE + "='" + currentTitle +"' AND " + COLUMN_ACCOUNT + "='" + account + "';",
                null);

        db.close();

    }

    public void updateUsername(String oldName, String newName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ACCOUNT, newName);
        db.update(TABLE_REVIEWS,
                contentValues,
                COLUMN_ACCOUNT + "='" + oldName + "';",
                null);
        db.close();
    }
}
