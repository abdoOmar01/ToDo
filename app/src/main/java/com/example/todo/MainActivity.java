package com.example.todo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String myPref = "preferences";
    Activity thisActivity = this;

    public boolean getPreferredTheme() {
        SharedPreferences sp = getSharedPreferences(myPref, 0);
        return sp.getBoolean("isDay", true);
    }

    public void setPreferredTheme(boolean t) {
        SharedPreferences.Editor editor = getSharedPreferences(myPref, 0).edit();
        editor.putBoolean("isDay", t);
        editor.apply();
    }

    ListView taskView;
    ArrayAdapter<String> taskAdapter;
    ArrayList<String> taskNames;
    ToDoDBHelper tasksDB;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(getPreferredTheme() ? R.style.Theme_ToDo : R.style.Theme_ToDoDark);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tasksDB = new ToDoDBHelper(getApplicationContext());

        taskAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        taskView = findViewById(R.id.tasks);
        taskView.setAdapter(taskAdapter);

        taskNames = new ArrayList<>();

        Cursor cursor = tasksDB.fetchAllTasks();
        while (!cursor.isAfterLast()) {
            taskAdapter.add(cursor.getString(0));
            taskNames.add(cursor.getString(0));
            cursor.moveToNext();
        }

        RadioButton important = findViewById(R.id.important);
        RadioButton trivial = findViewById(R.id.trivial);

        Spinner category = findViewById(R.id.categories);

        EditText taskName = findViewById(R.id.task);

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(e -> {
            boolean isDuplicate = false;
            for (String t: taskNames) {
                if (t.equals(taskName.getText().toString())) {
                    isDuplicate = true;
                }
            }

            if (taskName.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(this, "Task name can't be empty", Toast.LENGTH_LONG)
                        .show();
            } else if (isDuplicate) {
                Toast.makeText(this, "Task names can't contain duplicates",
                        Toast.LENGTH_LONG).show();
            } else if (!(important.isChecked() || trivial.isChecked())) {
                Toast.makeText(this, "Please specify the importance", Toast.LENGTH_LONG)
                        .show();
            } else {
                tasksDB.createNewTask(taskName.getText().toString(),
                        category.getSelectedItem().toString(), important.isChecked(),
                        LocalDateTime.now());

                taskNames.add(taskName.getText().toString());
                taskAdapter.add(taskName.getText().toString());

                Toast.makeText(this, "Task \"" + taskName.getText().toString() +
                        "\" added", Toast.LENGTH_SHORT).show();

                taskName.setText("");
            }
        });

        taskView.setOnItemClickListener((adapterView, view, i, l) -> {
            Cursor itemCursor = tasksDB.getRow(((TextView)view).getText().toString());
            String status = itemCursor.getString(4);
            Toast.makeText(thisActivity, status, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.theme:
                setPreferredTheme(!getPreferredTheme());
                thisActivity.recreate();
                break;

            case R.id.remove:
                taskAdapter.clear();
                taskNames.clear();
                tasksDB.removeAllTasks();
                break;

            case R.id.mark:
                for (String taskName: taskNames) {
                    tasksDB.markAsCompleted(taskName);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        return false;
    }
}