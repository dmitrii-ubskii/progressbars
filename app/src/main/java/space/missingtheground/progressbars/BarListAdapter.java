package space.missingtheground.progressbars;

import java.lang.Float;
import java.lang.Integer;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.MotionEvent.*;

public class BarListAdapter extends RecyclerView.Adapter<BarListAdapter.BarViewHolder> {
    class BarViewHolder extends RecyclerView.ViewHolder {
        class ResponsiveBar {
            Bar bar;

            private final TextView title;
            private final ProgressBar progressBar;
            private final TextView percentText;

            private Float swipeAmount = null;
            private Integer startProgress = null;

            ResponsiveBar(View itemView, Bar bar_) {
                bar = bar_;

                title = itemView.findViewById(R.id.textView);
                progressBar = itemView.findViewById(R.id.bar);
                percentText = itemView.findViewById(R.id.percentText);

                title.setText(bar.title);
                progressBar.setProgress(bar.percentProgress());
                percentText.setText(bar.progress + " / " + bar.total);
            }

            public void adjustProgress(float prevX, float newX) {
                if (swipeAmount == null) {
                    swipeAmount = 0.0f;
                }
                if (startProgress == null) {
                    startProgress = bar.progress;
                }

                float deltaX = newX - prevX;
                float width = itemView.getWidth();

                float deltaProgress = deltaX / width;
                swipeAmount += deltaProgress;
                bar.progress = startProgress + Math.round(swipeAmount * bar.total);
                bar.progress = Math.max(0, Math.min(bar.total, bar.progress));

                progressBar.setProgress(bar.percentProgress());
                percentText.setText(bar.progress + " / " + bar.total);

                prevX = newX;

                boundBar.updateTotal();
            }

            public void doneSwiping() {
                swipeAmount = null;
                startProgress = null;
                viewModel.update(bar);
                if (this != boundBar) {
                    viewModel.update(boundBar.bar);
                }
            }

            void updateTotal() {
                if (childBars.size() != 0) {
                    bar.progress = 0;
                    for (ResponsiveBar child : childBars) {
                        bar.progress += child.bar.progress;
                    }
                    progressBar.setProgress(bar.percentProgress());
                    percentText.setText(bar.progress + " / " + bar.total);
                }
            }
        }

        private ResponsiveBar boundBar;
        private List<ResponsiveBar> childBars;
        private BarListAdapter parent;

        private final LinearLayout childrenContainer;
        private final ImageButton menuButton;

        private BarViewHolder(View itemView) {
            super(itemView);
            childrenContainer = itemView.findViewById(R.id.childrenContainer);
            menuButton = itemView.findViewById(R.id.menuButton);

            menuButton.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(itemView.getContext(), menuButton);
                popup.getMenuInflater().inflate(R.menu.bar_options_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_edit:
                            Intent intent = new Intent(context, EditBarActivity.class);
                            intent.putExtra("barId", boundBar.bar.uid);
                            ((MainActivity)context)
                                .startActivityForResult(intent, MainActivity.EditBarActivityRequest);
                            return true;
                        case R.id.menu_delete:
                            for (ResponsiveBar child : childBars) {
                                viewModel.delete(child.bar);
                            }
                            viewModel.delete(boundBar.bar);
                            return true;
                        default:
                            return false;
                    }
                });

                popup.show();
            });
        }

        public void bind(BarListAdapter adapter, Bar bar) {
            parent = adapter;
            boundBar = new ResponsiveBar(itemView, bar);

            List<Bar> children = childrenMap.get(bar.uid);
            childBars = new ArrayList<>();
            childrenContainer.removeAllViews();
            if (children == null) {
                boundBar.progressBar.setProgressTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent))
                );
                childrenContainer.setVisibility(View.GONE);
            } else {
                boundBar.progressBar.setProgressTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorInactive))
                );
                childrenContainer.setVisibility(View.VISIBLE);
                for (Bar child : children) {
                    View subView = LayoutInflater.from(itemView.getContext())
                        .inflate(R.layout.child_bar, childrenContainer, false);
                    childBars.add(new ResponsiveBar(subView, child));
                    childrenContainer.addView(subView);
                }
            }
        }

        public ResponsiveBar findSwipableBar(float x, float y) {
            if (childBars.size() == 0) {
                View bar = itemView.findViewById(R.id.barLayout);
                int top = itemView.getTop() + bar.getTop();
                int bottom = top + bar.getHeight();
                if (y >= top && y <= bottom) {
                    return boundBar;
                }
            } else {
                int childrenContainerTop = itemView.getTop() + childrenContainer.getTop();
                for (int i = 0; i < childBars.size(); i++) {
                    View bar = childrenContainer.getChildAt(i);
                    int top = childrenContainerTop + bar.getTop();
                    int bottom = top + bar.getHeight();
                    if (y >= top && y <= bottom) {
                        return childBars.get(i);
                    }
                }
            }
            return null;
        }
    }

    private final Context context;
    private final LayoutInflater inflater;
    private List<Bar> bars;
    private Map<Long, List<Bar>> childrenMap;

    private BarViewModel viewModel;

    BarListAdapter(Context context_, BarViewModel vm) {
        context = context_;
        inflater = LayoutInflater.from(context);
        viewModel = vm;
    }

    @Override
    public BarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item, parent, false);
        return new BarViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BarViewHolder holder, int position) {
        if (bars != null) {
            holder.bind(this, bars.get(position));
        }
    }

    void setBars(List<Bar> bars_) {
        bars = new ArrayList<>();
        childrenMap = new HashMap<>();
        for (Bar bar : bars_) {
            if (bar.parent == null) {
                bars.add(bar);
            } else {
                childrenMap.computeIfAbsent(bar.parent, p -> new ArrayList<>()).add(bar);
            }
        }
        notifyDataSetChanged();
    }

    public void swapItems(int fromPos, int toPos) {
        Collections.swap(bars, fromPos, toPos);
        bars.get(fromPos).listPosition = fromPos;
        bars.get(toPos).listPosition = toPos;
    }

    public void onDragStop() {
        viewModel.updateAll(bars);
    }

    @Override
    public int getItemCount() {
        if (bars != null) {
            return bars.size();
        } else {
            return 0;
        }
    }
}

