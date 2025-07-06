package space.missingtheground.progressbars;

import java.lang.Float;
import java.lang.Integer;
import java.lang.Math;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

        private BarViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textView);
            progressBar = itemView.findViewById(R.id.bar);
            percentText = itemView.findViewById(R.id.percentText);
        }

        public void toast(String s) {
            Toast.makeText(title.getContext(), title.getText() + " / " + s, Toast.LENGTH_LONG).show();
        }

        public void adjustProgress(float prevX, float newX) {
            float deltaX = newX - prevX;
            float width = itemView.getWidth();

            float deltaProgress = (int)(deltaX / width * boundBar.targetTotal);
            boundBar.progress += deltaProgress;
            boundBar.progress = Math.max(0, Math.min(boundBar.targetTotal, boundBar.progress));

            progressBar.setProgress(boundBar.percentProgress());
            percentText.setText(boundBar.progress + " / " + boundBar.targetTotal);

            prevX = newX;

            viewModel.update(boundBar);
        }

        public void bind(BarListAdapter adapter, Bar bar) {
            parent = adapter;
            boundBar = bar;
            title.setText(boundBar.title);
            progressBar.setProgress(boundBar.percentProgress());
            percentText.setText(boundBar.progress + " / " + boundBar.targetTotal);
        }

        public void remove() {
            parent.remove(getAdapterPosition());
        }
    }

    private final LayoutInflater inflater;
    private List<Bar> bars;

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
        bars = bars_;
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
        if (bars != null)
            return bars.size();
        else return 0;
    }
}

