package space.missingtheground.progressbars;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EditBarActivity extends AppCompatActivity {
    private EditText titleField, progressField, totalField;
    private LinearLayout childList;
    private Button addChildButton;
    private FloatingActionButton saveButton;

    private boolean isEditMode = false;
    private long barId = -1;
    private BarDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editbar);

        db = BarDatabase.getDatabase(getApplicationContext());

        titleField = findViewById(R.id.edit_title);
        progressField = findViewById(R.id.edit_progress);
        totalField = findViewById(R.id.edit_total);
        childList = findViewById(R.id.child_list);
        addChildButton = findViewById(R.id.button_add_child);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("barId")) {
            barId = intent.getLongExtra("barId", -1);
            isEditMode = true;
            loadBarForEditing(barId);
        }

        addChildButton.setOnClickListener(v -> this.addChildRow());

        final FloatingActionButton saveButton = findViewById(R.id.button_save);
        saveButton.setOnClickListener(v -> saveBar());
    }

    private void loadBarForEditing(long id) {
        BarDatabase.databaseWriteExecutor.execute(() -> {
            Bar bar = db.barDao().getById(id);
            if (bar != null) {
                runOnUiThread(() -> {
                    titleField.setText(bar.title);
                    progressField.setText(String.valueOf(bar.progress));
                    totalField.setText(String.valueOf(bar.total));
                    // TODO children
                });
            }
        });
    }

    private final TextWatcher progressWatcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateMainBarProgress();
        }
        @Override public void afterTextChanged(Editable s) {}
    };

    private void addChildRow() {
        View row = LayoutInflater.from(this).inflate(R.layout.child_input_row, childList, false);

        EditText childProgress = row.findViewById(R.id.edit_child_progress);
        EditText childTotal = row.findViewById(R.id.edit_child_total);
        ImageButton deleteButton = row.findViewById(R.id.button_delete_child);

        childProgress.addTextChangedListener(progressWatcher);
        childTotal.addTextChangedListener(progressWatcher);

        deleteButton.setOnClickListener(v -> {
            childList.removeView(row);
            updateMainBarProgress();
        });

        childList.addView(row);
        updateMainBarProgress();
    }

    private void updateMainBarProgress() {
        int count = childList.getChildCount();
        if (count == 0) {
            progressField.setEnabled(true);
            totalField.setEnabled(true);
            return;
        }

        int totalProgress = 0;
        int totalTarget = 0;

        for (int i = 0; i < count; i++) {
            View row = childList.getChildAt(i);
            EditText progress = row.findViewById(R.id.edit_child_progress);
            EditText target = row.findViewById(R.id.edit_child_total);

            totalProgress += safeParseInt(progress.getText().toString());
            totalTarget += safeParseInt(target.getText().toString());
        }

        progressField.setText(String.valueOf(totalProgress));
        totalField.setText(String.valueOf(totalTarget));
        progressField.setEnabled(false);
        totalField.setEnabled(false);
    }

    private int safeParseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void saveBar() {
        String title = titleField.getText().toString().trim();
        int progress = safeParseInt(progressField.getText().toString());
        int total = safeParseInt(totalField.getText().toString());

        ArrayList<Bundle> children = new ArrayList<>();
        for (int i = 0; i < childList.getChildCount(); i++) {
            View child = childList.getChildAt(i);
            String childTitle =
                ((EditText) child.findViewById(R.id.edit_child_title)).getText().toString();
            int childProgress = Integer.parseInt(
                ((EditText) child.findViewById(R.id.edit_child_progress)).getText().toString()
            );
            int childTotal = Integer.parseInt(
                ((EditText) child.findViewById(R.id.edit_child_total)).getText().toString()
            );
            Bundle childBar = new Bundle();
            childBar.putString("title", childTitle);
            childBar.putInt("progress", childProgress);
            childBar.putInt("total", childTotal);
            children.add(childBar);
        }

        Bundle bar = new Bundle();
        bar.putLong("uid", barId);
        bar.putString("title", title);
        bar.putInt("progress", progress);
        bar.putInt("total", total);
        bar.putParcelableArrayList("children", children);

        Intent intent = new Intent();
        intent.putExtra("barData", bar);
        setResult(RESULT_OK, intent);
        finish();
    }
}

