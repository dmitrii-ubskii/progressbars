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
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.MotionEvent.*;

public class BarListAdapter extends RecyclerView.Adapter<BarListAdapter.BarViewHolder> {
    class BarViewHolder extends RecyclerView.ViewHolder {
        private BarListAdapter parent;

        private Bar boundBar;

        private final TextView title;
        private final ProgressBar progressBar;
        private final TextView percentText;
        private final LinearLayout childrenContainer;

        private BarViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textView);
            progressBar = itemView.findViewById(R.id.bar);
            percentText = itemView.findViewById(R.id.percentText);
            childrenContainer = itemView.findViewById(R.id.childrenContainer);
        }

        private Float swipeAmount = null;
        private Integer startProgress = null;

        public void adjustProgress(float prevX, float newX) {
            if (swipeAmount == null) {
                swipeAmount = 0.0f;
            }
            if (startProgress == null) {
                startProgress = boundBar.progress;
            }

            float deltaX = newX - prevX;
            float width = itemView.getWidth();

            float deltaProgress = deltaX / width;
            swipeAmount += deltaProgress;
            boundBar.progress = startProgress + (int)(swipeAmount * boundBar.targetTotal);
            boundBar.progress = Math.max(0, Math.min(boundBar.targetTotal, boundBar.progress));

            progressBar.setProgress(boundBar.percentProgress());
            percentText.setText(boundBar.progress + " / " + boundBar.targetTotal);

            prevX = newX;
        }

        public void doneSwiping() {
            swipeAmount = null;
            startProgress = null;
            viewModel.update(boundBar);
        }

        public void bind(BarListAdapter adapter, Bar bar) {
            parent = adapter;
            boundBar = bar;
            title.setText(boundBar.title);
            progressBar.setProgress(boundBar.percentProgress());
            percentText.setText(boundBar.progress + " / " + boundBar.targetTotal);

            List<Bar> children = childrenMap.get(bar.uid);
            childrenContainer.removeAllViews();
            if (children == null) {
                childrenContainer.setVisibility(View.GONE);
            } else {
                childrenContainer.setVisibility(View.VISIBLE);
                for (Bar child : children) {
                    View subView = LayoutInflater.from(itemView.getContext())
                        .inflate(R.layout.child_bar, childrenContainer, false);
                     ((TextView)subView.findViewById(R.id.textView)).setText(child.title);
                     ((ProgressBar)subView.findViewById(R.id.bar))
                        .setProgress(child.percentProgress());
                     ((TextView)subView.findViewById(R.id.percentText))
                        .setText(child.progress + " / " + child.targetTotal);
                    childrenContainer.addView(subView);
                }
            }
        }

        public void remove() {
            parent.remove(getAdapterPosition());
        }
    }

    private final LayoutInflater inflater;
    private List<Bar> bars;
    private Map<Long, List<Bar>> childrenMap;

    private BarViewModel viewModel;

    BarListAdapter(Context context, BarViewModel vm) {
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

    void remove(int position) {
        viewModel.delete(bars.get(position));
        notifyDataSetChanged();
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

