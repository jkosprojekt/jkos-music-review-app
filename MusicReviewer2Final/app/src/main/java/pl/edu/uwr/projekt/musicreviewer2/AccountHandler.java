package pl.edu.uwr.projekt.musicreviewer2;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AccountHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "accountsDB_JAVA.db";

    public static final String TABLE_ACCOUNTS = "accounts";

    public static final String COLUMN_EMAIL = "artist";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    public AccountHandler(Context context){
        super(context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ACCOUNTS_TABLE = "CREATE TABLE " +
                TABLE_ACCOUNTS +
                "(" +
                COLUMN_EMAIL +
                " TEXT," +
                COLUMN_USERNAME +
                " TEXT," +
                COLUMN_PASSWORD +
                " TEXT" +
                ")";
        db.execSQL(CREATE_ACCOUNTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
        onCreate(db);
    }

    public Cursor getAccount(String username){
        String query = "SELECT * FROM " + TABLE_ACCOUNTS + " WHERE " + COLUMN_USERNAME + "='" + username +"' ;";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query, null);
    }

    public Cursor getPassword(String username){
        String query = "SELECT" + COLUMN_PASSWORD + " FROM " + TABLE_ACCOUNTS + " WHERE " + COLUMN_USERNAME + "='" + username +"' ;";
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query, null);
    }

    public void addAccount(Acc account){
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, account.getEmail());
        values.put(COLUMN_USERNAME, account.getUsername());
        values.put(COLUMN_PASSWORD, account.getPassword());
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_ACCOUNTS, null, values);
        db.close();
    }

    public void deleteAccount(String username){
        String query = "SELECT * FROM " +
                TABLE_ACCOUNTS +
                " WHERE " +
                COLUMN_USERNAME +
                " = \"" +
                username +
                "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            db.delete(TABLE_ACCOUNTS, COLUMN_USERNAME + "='" + username +"' ;",
                    null);
            cursor.close();
        }
        db.close();
    }

    public void changeUsername(String currentUsername, String newUsername){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, newUsername);
        db.update(TABLE_ACCOUNTS,
                contentValues,
                COLUMN_USERNAME + "='" + currentUsername + "' ;",
                null);
        db.close();
    }

    public void changePassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PASSWORD, newPassword);
        db.update(TABLE_ACCOUNTS,
                contentValues,
                COLUMN_USERNAME + "='" + username + "' ;",
                null);
        db.close();
    }
}
