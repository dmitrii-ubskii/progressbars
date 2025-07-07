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

    static final int EditBarActivityRequest = 1;
    private BarListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(BarViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        recyclerView.addOnItemTouchListener(new TouchListener());

        adapter = new BarListAdapter(this, viewModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchCallback());
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
                Intent intent = new Intent(MainActivity.this, EditBarActivity.class);
                startActivityForResult(intent, EditBarActivityRequest);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EditBarActivityRequest && resultCode == RESULT_OK) {
            Bundle barData = data.getBundleExtra("barData");
            if (barData == null) {
                return;
            }
            Bar mainBar = unbundle(barData);
            mainBar.listPosition = adapter.getItemCount();
            List<Bundle> children = barData.getParcelableArrayList("children");
            if (mainBar.uid == 0) {
                viewModel.insert(mainBar, () -> insertBarChildren(mainBar.uid, children));
            } else {
                viewModel.update(mainBar, () -> insertBarChildren(mainBar.uid, children));
            }
        }
    }

    Bar unbundle(Bundle bundle) {
        Bar bar = new Bar();
        bar.uid = bundle.getLong("uid", 0);
        bar.title = bundle.getString("title", "Untitled");
        bar.progress = bundle.getInt("progress", 0);
        bar.total = bundle.getInt("total", 100);
        return bar;
    }

    void insertBarChildren(long parentId, List<Bundle> children) {
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                Bar child = unbundle(children.get(i));
                child.parent = parentId;
                child.listPosition = i;
                if (child.uid == 0) {
                    viewModel.insert(child);
                } else {
                    viewModel.update(child);
                }
            }
        }
    }

    class TouchListener implements RecyclerView.OnItemTouchListener {
        Float prevX = null;
        BarListAdapter.BarViewHolder.ResponsiveBar activeBar = null;

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());

            switch (e.getAction()) {
                case ACTION_DOWN:
                if (child != null) {
                    RecyclerView.ViewHolder holder = rv.getChildViewHolder(child);
                    if (holder instanceof BarListAdapter.BarViewHolder) {
                        activeBar = ((BarListAdapter.BarViewHolder)holder)
                        .findSwipableBar(e.getX(), e.getY());
                        if (activeBar != null) {
                            prevX = e.getX();
                        }
                    }
                }
                break;
                case ACTION_MOVE:
                if (activeBar != null && prevX != null) {
                    activeBar.adjustProgress(prevX, e.getX());
                    prevX = e.getX();
                }
                break;
                case ACTION_UP: case ACTION_CANCEL:
                if (activeBar != null) {
                    activeBar.doneSwiping();
                }
                prevX = null;
                activeBar = null;
                break;
            }

            return false; // allow other touch events (e.g., vertical scrolling) to continue
        }

        @Override public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

        @Override public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
    }

    class TouchCallback extends ItemTouchHelper.SimpleCallback {
        TouchCallback() {
            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        }

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
}

