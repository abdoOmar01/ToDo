package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView tasks = findViewById(R.id.tasks);
        final ArrayAdapter<String> taskNames =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        tasks.setAdapter(taskNames);

        RadioButton important = findViewById(R.id.important);
        RadioButton trivial = findViewById(R.id.trivial);

        Spinner category = findViewById(R.id.categories);

        EditText taskName = findViewById(R.id.task);

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(e -> {
            if (taskName.getText().toString().equals("")) {
                Toast.makeText(this, "Task name can't be empty", Toast.LENGTH_LONG)
                        .show();
            } else if (!(important.isChecked() || trivial.isChecked())) {
                Toast.makeText(this, "Please specify the importance", Toast.LENGTH_LONG)
                        .show();
            } else {
                Task task = new Task();
                task.setName(taskName.getText().toString());
                task.setCategory(category.getSelectedItem().toString());
                task.setImportant(important.isChecked());

                taskNames.add(task.getName());
            }
        });
    }
}