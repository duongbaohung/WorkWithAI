package com.example.workwithai.Repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.workwithai.Models.QuestionModel;

import java.util.ArrayList;
import java.util.List;

public class QuestionRepository extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WorkWithAI.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_HISTORY = "question_history";

    public QuestionRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_HISTORY + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "subject TEXT, " +
                "difficulty TEXT, " +
                "question_text TEXT, " +
                "image_uri TEXT, " +
                "concepts TEXT, " +
                "steps TEXT, " +
                "final_answer TEXT, " +
                "is_bookmarked INTEGER DEFAULT 0, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    public long saveQuestion(QuestionModel q) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("subject", q.getSubject());
        values.put("difficulty", q.getDifficulty());
        values.put("question_text", q.getQuestionText());
        values.put("image_uri", q.getImageUri());
        values.put("concepts", q.getConcepts());
        values.put("steps", q.getSteps());
        values.put("final_answer", q.getFinalAnswer());
        return db.insert(TABLE_HISTORY, null, values);
    }

    public List<QuestionModel> getAllHistory(String searchQuery, String subjectFilter) {
        List<QuestionModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_HISTORY + " WHERE 1=1";
        if (searchQuery != null && !searchQuery.isEmpty()) {
            query += " AND question_text LIKE '%" + searchQuery + "%'";
        }
        if (subjectFilter != null && !subjectFilter.equals("All")) {
            query += " AND subject = '" + subjectFilter + "'";
        }
        query += " ORDER BY id DESC";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                QuestionModel q = new QuestionModel();
                q.setId(cursor.getInt(0));
                q.setSubject(cursor.getString(1));
                q.setDifficulty(cursor.getString(2));
                q.setQuestionText(cursor.getString(3));
                q.setImageUri(cursor.getString(4));
                q.setConcepts(cursor.getString(5));
                q.setSteps(cursor.getString(6));
                q.setFinalAnswer(cursor.getString(7));
                q.setIsBookmarked(cursor.getInt(8));
                list.add(q);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void toggleBookmark(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_bookmarked", status);
        db.update(TABLE_HISTORY, values, "id = ?", new String[]{String.valueOf(id)});
    }
}