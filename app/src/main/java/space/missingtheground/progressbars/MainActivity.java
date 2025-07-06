package space.missingtheground.progressbars;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.view.MotionEvent.*;

public class MainActivity extends AppCompatActivity {
    private BarViewModel viewModel;

    private final int NewWordActivityRequest = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(BarViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            Float prevX = null;
            BarListAdapter.BarViewHolder activeHolder = null;

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());

                switch (e.getAction()) {
                    case ACTION_DOWN:
                        if (child != null) {
                            RecyclerView.ViewHolder holder = rv.getChildViewHolder(child);
                            if (holder instanceof BarListAdapter.BarViewHolder) {
                                View bar = child.findViewById(R.id.barLayout);

                                float y = e.getY();

                                int top = child.getTop() + bar.getTop();
                                int bottom = top + bar.getHeight();

                                if (y >= top && y <= bottom) {
                                    prevX = e.getX();
                                    activeHolder = (BarListAdapter.BarViewHolder) holder;
                                }
                            }
                        }
                        break;
                    case ACTION_MOVE:
                        if (activeHolder != null && prevX != null) {
                            activeHolder.adjustProgress(prevX, e.getX());
                            prevX = e.getX();
                        }
                        break;
                    case ACTION_UP: case ACTION_CANCEL:
                        if (activeHolder != null) {
                            activeHolder.doneSwiping();
                        }
                        prevX = null;
                        activeHolder = null;
                        break;
                }

                return false; // allow other touch events (e.g., vertical scrolling) to continue
            }

            @Override public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

            @Override public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });

        final BarListAdapter adapter = new BarListAdapter(this, viewModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
            new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

                @Override
                public boolean onMove(RecyclerView recyclerView,
                    RecyclerView.ViewHolder viewHolder,
                    RecyclerView.ViewHolder target) {
                    int fromPos = viewHolder.getAdapterPosition();
                    int toPos = target.getAdapterPosition();

                    // Swap data in your adapter
                    adapter.swapItems(fromPos, toPos);

                    // Notify adapter of item moved
                    adapter.notifyItemMoved(fromPos, toPos);
                    return true;
                }

                @Override
                public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                    super.onSelectedChanged(viewHolder, actionState);

                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder != null) {
                        viewHolder.itemView.setScaleX(1.05f);
                        viewHolder.itemView.setScaleY(1.05f);
                    }
                }

                @Override
                public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                    super.clearView(recyclerView, viewHolder);
                    viewHolder.itemView.setScaleX(1f);
                    viewHolder.itemView.setScaleY(1f);
                    adapter.onDragStop();
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {}

                @Override
                public boolean isLongPressDragEnabled() {
                    return true;
                }
            }
        );
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.requestDisallowInterceptTouchEvent(true);
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
            bar.progress = 0;
            bar.targetTotal = 100;
            bar.title = data.getStringExtra(NewBarActivity.ExtraReply);
            viewModel.insert(bar);
        } else {
            Toast.makeText(getApplicationContext(), R.string.empty_not_saved, Toast.LENGTH_LONG).show();
        }
    }
}

