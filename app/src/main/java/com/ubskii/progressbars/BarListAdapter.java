package com.ubskii.progressbars;

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

public class BarListAdapter extends RecyclerView.Adapter<BarListAdapter.BarViewHolder> {
    class BarViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnTouchListener {
        private BarListAdapter parent;

        private final TextView title;
        private final ProgressBar progressBar;
        private final TextView percentText;

        private GestureDetector gestureDetector;

        private class GestureListener extends GestureDetector.SimpleOnGestureListener {
            BarViewHolder parent;

            GestureListener(BarViewHolder holder) {
                super();
                parent = holder;
            }

            @Override
            public boolean onDown(MotionEvent event) {
                parent.toast(event.toString());
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                parent.toast(Float.toString(distanceX) + ", " + Float.toString(distanceY));
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                parent.toast(Float.toString(velocityX) + ", " + Float.toString(velocityY));
                if (velocityX > Math.abs(velocityY) && velocityX > 10) {
                    parent.remove();
                }
                return true;
            }
        }

        private BarViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textView);
            progressBar = itemView.findViewById(R.id.bar);
            percentText = itemView.findViewById(R.id.percentText);
            itemView.setOnLongClickListener(this);

            gestureDetector = new GestureDetector(itemView.getContext(), new GestureListener(this));
        }

        public void toast(String s) {
            Toast.makeText(title.getContext(), title.getText() + " / " + s, Toast.LENGTH_LONG).show();
        }

        @Override
        public boolean onLongClick(View view) {
            // parent.remove(getAdapterPosition());
            return true;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            toast(event.toString());
            this.gestureDetector.onTouchEvent(event);
            return true;
        }

        public void bind(BarListAdapter adapter, Bar bar) {
            parent = adapter;
            title.setText(bar.title);
            progressBar.setProgress((int)(bar.fractionDone * 100));
            percentText.setText(Integer.toString((int)(bar.fractionDone * 100)) + "%");
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

