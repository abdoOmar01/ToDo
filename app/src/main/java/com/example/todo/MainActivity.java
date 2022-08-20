package com.example.todo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView taskView = findViewById(R.id.tasks);
        final ArrayAdapter<String> taskNames =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        taskView.setAdapter(taskNames);

        ArrayList<Task> tasks = new ArrayList<>();

        RadioButton important = findViewById(R.id.important);
        RadioButton trivial = findViewById(R.id.trivial);

        Spinner category = findViewById(R.id.categories);

        EditText taskName = findViewById(R.id.task);

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(e -> {
            boolean isDuplicate = false;
            for (Task t: tasks) {
                if (t.getName().equals(taskName.getText().toString())) {
                    isDuplicate = true;
                }
            }

            if (taskName.getText().toString().equals("")) {
                Toast.makeText(this, "Task name can't be empty", Toast.LENGTH_LONG)
                        .show();
            } else if (isDuplicate) {
                Toast.makeText(this, "Task names can't contain duplicates",
                        Toast.LENGTH_LONG).show();
            } else if (!(important.isChecked() || trivial.isChecked())) {
                Toast.makeText(this, "Please specify the importance", Toast.LENGTH_LONG)
                        .show();
            } else {
                Task task = new Task();
                task.setName(taskName.getText().toString());
                task.setCategory(category.getSelectedItem().toString());
                task.setImportant(important.isChecked());
                task.setCreationDate(LocalDateTime.now());

                tasks.add(task);
                taskNames.add(task.getName());

                Toast.makeText(this, "Task \"" + taskName.getText().toString() +
                        "\" added", Toast.LENGTH_LONG).show();
            }
        });

        taskView.setOnItemClickListener((adapterView, view, i, l) -> {
            Task thisTask = null;
            for (Task t: tasks) {
                if (t.getName().equals(((TextView)view).getText().toString())) {
                    thisTask = t;
                }
            }

            assert thisTask != null;

            // Should switch to another intent displaying task details
            // Remove after implementation
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM HH:mm");
            Toast.makeText(getApplicationContext(),
                    "Category: " + thisTask.getCategory() +
                            "\n" + (thisTask.getImportant() ? "Important" : "Trivial")
                            + "\nCreated on: " + thisTask.getCreationDate().format(formatter),
                    Toast.LENGTH_LONG).show();
        });

        // Context menu for each task, showing: edit, delete and mark as completed/pending
    }

    // Options menu for switching Dark/Night theme
    // after Database!!!!!!!
}