package com.example.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ToDoDBHelper extends SQLiteOpenHelper {
    private static final String databaseName = "todoDatabase";
    SQLiteDatabase todoDatabase;

    public ToDoDBHelper(Context context) {
        super(context, databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table task(id integer primary key autoincrement not null," +
                "name text, category text, isImportant text, creationDate text," +
                "completionDate text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists task");
        onCreate(db);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNewTask(String name, String category, boolean isImportant,
                              LocalDateTime creationDate) {
        ContentValues row = new ContentValues();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM HH:mm");

        row.put("name", name);
        row.put("category", category);
        row.put("isImportant", (isImportant) ? "Important" : "Trivial");
        row.put("creationDate", creationDate.format(formatter));
        row.put("completionDate", "pending");

        todoDatabase = getWritableDatabase();
        todoDatabase.insert("task", null, row);
        todoDatabase.close();
    }

    public Cursor fetchAllTasks() {
        todoDatabase = getReadableDatabase();
        String[] rowDetails = {"name", "category", "isImportant", "creationDate", "completionDate"};
        Cursor cursor = todoDatabase.query("task", rowDetails, null, null,
                null, null, null, null);

        assert cursor != null;

        cursor.moveToFirst();
        todoDatabase.close();
        return cursor;
    }

    public void removeAllTasks() {
        todoDatabase = getWritableDatabase();
        todoDatabase.execSQL("delete from task");
        todoDatabase.close();
    }

    public void removeTask(String taskName) {
        todoDatabase = getWritableDatabase();
        todoDatabase.delete("task", "name=?", new String[]{taskName});
        todoDatabase.close();
    }

    public void editTask(String taskName, String newName) {
        todoDatabase = getWritableDatabase();

        String[] rowDetails = {"name", "category", "isImportant", "creationDate", "completionDate"};
        String[] args = {taskName};
        Cursor cursor = todoDatabase.query("task", rowDetails, "name=?", args,
                null, null, null, null);

        assert cursor != null;
        cursor.moveToFirst();

        ContentValues row = new ContentValues();
        row.put("name", newName);
        row.put("category", cursor.getString(1));
        row.put("isImportant", cursor.getString(2));
        row.put("creationDate", cursor.getString(3));
        row.put("completionDate", cursor.getString(4));

        todoDatabase.update("task", row, "name=?", args);
        todoDatabase.close();
        cursor.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void mark(String taskName, boolean toggle, boolean pending) {
        todoDatabase = getWritableDatabase();

        String[] rowDetails = {"name", "category", "isImportant", "creationDate", "completionDate"};
        String[] args = {taskName};
        Cursor cursor = todoDatabase.query("task", rowDetails, "name=?", args,
                null, null, null, null);

        assert cursor != null;
        cursor.moveToFirst();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM HH:mm");

        ContentValues row = new ContentValues();
        row.put("name", cursor.getString(0));
        row.put("category", cursor.getString(1));
        row.put("isImportant", cursor.getString(2));
        row.put("creationDate", cursor.getString(3));

        if (toggle) {
            if (cursor.getString(4).equals("pending")) {
                row.put("completionDate", LocalDateTime.now().format(formatter));

            } else {
                row.put("completionDate", "pending");
            }
        } else {
            if (pending) {
                if (!cursor.getString(4).equals("pending")) {
                    row.put("completionDate", "pending");
                }
            } else {
                if (cursor.getString(4).equals("pending")) {
                    row.put("completionDate", LocalDateTime.now().format(formatter));
                }
            }
        }

        todoDatabase.update("task", row, "name=?", args);
        todoDatabase.close();
        cursor.close();
    }

    public Cursor getRow(String taskName) {
        todoDatabase = getReadableDatabase();
        String[] rowDetails = {"name", "category", "isImportant", "creationDate", "completionDate"};
        String[] args = {taskName};
        Cursor cursor = todoDatabase.query("task", rowDetails, "name=?", args,
                null, null, null, null);

        assert cursor != null;
        cursor.moveToFirst();
        todoDatabase.close();
        return cursor;
    }
}