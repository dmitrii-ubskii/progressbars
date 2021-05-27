package com.ubskii.progressbars;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private BarViewModel viewModel;

    private final int NewWordActivityRequest = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(BarViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final BarListAdapter adapter = new BarListAdapter(this, viewModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getAllBars().observe(this, new Observer<List<Bar>>() {
            @Override
            public void onChanged(@Nullable final List<Bar> bars) {
                adapter.setBars(bars);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewBarActivity.class);
                startActivityForResult(intent, NewWordActivityRequest);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NewWordActivityRequest && resultCode == RESULT_OK) {
            Bar bar = new Bar();
            bar.fractionDone = 0.0f;
            bar.title = data.getStringExtra(NewBarActivity.ExtraReply);
            viewModel.insert(bar);
        } else {
            Toast.makeText(getApplicationContext(), R.string.empty_not_saved, Toast.LENGTH_LONG).show();
        }
    }
}

