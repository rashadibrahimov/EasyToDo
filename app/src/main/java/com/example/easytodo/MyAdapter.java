package com.example.easytodo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public MyAdapter(Context Context, Cursor Cursor) {
        mContext = Context;
        mCursor = Cursor;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView firstLetterTexView;
        public TextView dateTimeTextView;

        public MyViewHolder(View v) {
            super(v);
            titleTextView = v.findViewById(R.id.titleTextView);
            firstLetterTexView = v.findViewById(R.id.firstLetterTextView);
            dateTimeTextView = v.findViewById(R.id.dateTimeTextView);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.todo_item, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UpdateTodoActivity.class);
                intent.putExtra("itemTitle", (String) view.getTag(R.string.titleTextView));
                intent.putExtra("itemID", (Long) view.getTag(R.string.id));
                intent.putExtra("itemPosition", (Integer) view.getTag(R.string.position));
                if((view.getTag(R.string.date) != null) && (view.getTag(R.string.time) != null)) {
                    intent.putExtra("date", (String) view.getTag(R.string.date));
                    intent.putExtra("time", (String) view.getTag(R.string.time));
                    intent.putExtra("year", (int) view.getTag(R.string.year));
                    intent.putExtra("month", (int) view.getTag(R.string.month));
                    intent.putExtra("day", (int) view.getTag(R.string.day));
                    intent.putExtra("hour", (int) view.getTag(R.string.hour));
                    intent.putExtra("minute", (int) view.getTag(R.string.minute));
                }
                ((Activity) mContext).startActivityForResult(intent, 2);
            }
        });

        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (!mCursor.moveToPosition(position)) {
            return;
        }

        String title = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TITLE));
        String titleText = title.substring(0, 1).toUpperCase() + title.substring(1);
        String firstLetter = String.valueOf(titleText.charAt(0));
        long id = mCursor.getLong(mCursor.getColumnIndex(DatabaseHelper.ID));
        String date = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.DATE));
        String time = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.TIME));
        String color = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COLOR));
        int year = mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.YEAR));
        int month = mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.MONTH));
        int day = mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.DAY));
        int hour = mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.HOUR));
        int minute = mCursor.getInt(mCursor.getColumnIndex(DatabaseHelper.MINUTE));

        if (date == null && time == null) {
            holder.dateTimeTextView.setVisibility(View.GONE);
            holder.titleTextView.setGravity(Gravity.LEFT|Gravity.CENTER);
            holder.titleTextView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        else {
            holder.dateTimeTextView.setVisibility(View.VISIBLE);
            holder.titleTextView.setGravity(Gravity.LEFT);
            holder.titleTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        holder.titleTextView.setText(titleText);
        holder.firstLetterTexView.setText(firstLetter);
        holder.dateTimeTextView.setText(new StringBuilder().append(date).append(" ").append(time));
        holder.itemView.setTag(R.string.id, id);
        holder.itemView.setTag(R.string.titleTextView, title);
        holder.itemView.setTag(R.string.date, date);
        holder.itemView.setTag(R.string.time, time);
        holder.itemView.setTag(R.string.position, position);
        holder.itemView.setTag(R.string.year, year);
        holder.itemView.setTag(R.string.month, month);
        holder.itemView.setTag(R.string.day, day);
        holder.itemView.setTag(R.string.hour, hour);
        holder.itemView.setTag(R.string.minute, minute);

        ((GradientDrawable) holder.firstLetterTexView.getBackground()).setColor(Integer.parseInt(color));
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
}