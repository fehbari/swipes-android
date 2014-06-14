package com.swipesapp.android.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.swipesapp.android.R;
import com.swipesapp.android.sync.gson.GsonTag;
import com.swipesapp.android.sync.gson.GsonTask;
import com.swipesapp.android.ui.listener.ListContentsListener;
import com.swipesapp.android.ui.view.SwipesTextView;
import com.swipesapp.android.util.DateUtils;
import com.swipesapp.android.util.ThemeUtils;
import com.swipesapp.android.values.Sections;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Adapter for task lists.
 */
public class TasksListAdapter extends ArrayAdapter {

    private List<GsonTask> mData;
    private WeakReference<Context> mContext;
    private int mLayoutResID;
    private Sections mSection;

    private final int INVALID_ID = -1;
    private HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

    private ListContentsListener mListContentsListener;

    private static final String TAG_SEPARATOR = ", ";

    public TasksListAdapter(Context context, int layoutResourceId, List<GsonTask> data, Sections section) {
        super(context, layoutResourceId, data);

        mData = data;
        mContext = new WeakReference<Context>(context);
        mLayoutResID = layoutResourceId;
        mSection = section;

        updateIdMap();
    }

    @Override
    public int getCount() {
        int count = super.getCount();
        // HACK: this is a workaround to notify the activity through the fragment
        if (mListContentsListener != null) {
            if (count != 0) {
                mListContentsListener.onNotEmpty();
            } else {
                mListContentsListener.onEmpty(Sections.FOCUS);
            }
        }
        return count;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TaskHolder holder;
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext.get()).getLayoutInflater();
            row = inflater.inflate(mLayoutResID, parent, false);

            holder = new TaskHolder();

            holder.frontView = (RelativeLayout) row.findViewById(R.id.swipe_front);
            holder.backView = (RelativeLayout) row.findViewById(R.id.swipe_back);
            holder.priorityButton = (CheckBox) row.findViewById(R.id.button_task_priority);
            holder.title = (TextView) row.findViewById(R.id.task_title);
            holder.time = (TextView) row.findViewById(R.id.task_time);
            holder.propertiesContainer = (RelativeLayout) row.findViewById(R.id.task_properties_container);
            holder.propertiesDivider = (TextView) row.findViewById(R.id.task_properties_divider);
            holder.locationIcon = (SwipesTextView) row.findViewById(R.id.task_location_icon);
            holder.notesIcon = (SwipesTextView) row.findViewById(R.id.task_notes_icon);
            holder.repeatIcon = (SwipesTextView) row.findViewById(R.id.task_repeat_icon);
            holder.tagsDivider = (TextView) row.findViewById(R.id.task_tags_divider);
            holder.tags = (TextView) row.findViewById(R.id.task_tags);

            row.setTag(holder);
        } else {
            holder = (TaskHolder) row.getTag();
        }

        customizeView(holder, position);

        return row;
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        String key = mData.get(position).getObjectId();
        return mIdMap.get(key);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void customizeView(TaskHolder holder, int position) {
        // Controls the display of the properties line below the title.
        boolean displayProperties = false;

        // Attributes displayed for all sections.
        String title = mData.get(position).getTitle();
        List<GsonTag> tagList = mData.get(position).getTags();
        String tags = null;
        String notes = mData.get(position).getNotes();
        Date repeatDate = mData.get(position).getRepeatDate();

        // Set task title.
        holder.title.setText(title);

        // Build the formatted tags.
        if (tagList != null && !tagList.isEmpty()) {
            for (GsonTag tag : tagList) {
                if (tags == null) {
                    tags = tag.getTitle();
                } else {
                    tags += TAG_SEPARATOR + tag.getTitle();
                }
            }

            // Display formatted tags with divider.
            holder.tagsDivider.setVisibility(View.VISIBLE);
            holder.tags.setVisibility(View.VISIBLE);
            holder.tags.setText(tags);
            displayProperties = true;
        }

        // Display notes icon.
        if (notes != null && !notes.isEmpty()) {
            holder.propertiesDivider.setVisibility(View.VISIBLE);
            holder.notesIcon.setVisibility(View.VISIBLE);
            displayProperties = true;
        }

        // Display repeat icon.
        if (repeatDate != null) {
            holder.propertiesDivider.setVisibility(View.VISIBLE);
            holder.repeatIcon.setVisibility(View.VISIBLE);
            displayProperties = true;
        }

        // Specific rules for each section.
        customizeViewForSection(holder, position, displayProperties);

        // Display properties line.
        if (displayProperties) {
            holder.propertiesContainer.setVisibility(View.VISIBLE);

            // Change layout weights to properly split cell spacing.
            holder.title.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.5f));
            holder.propertiesContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.5f));

            // Change title alignment.
            holder.title.setGravity(Gravity.BOTTOM);
        }

        // Sets colors for cell, matching the current theme.
        holder.title.setTextColor(ThemeUtils.getCurrentThemeTextColor(getContext()));
        holder.frontView.setBackgroundColor(ThemeUtils.getCurrentThemeBackgroundColor(getContext()));
    }

    private void customizeViewForSection(TaskHolder holder, int position, boolean displayProperties) {
        switch (mSection) {
            case LATER:
                // Set priority button color.
                holder.priorityButton.setBackgroundResource(R.drawable.later_circle_selector);

                // Display scheduled time or location icon (never both).
                Date schedule = mData.get(position).getSchedule();
                String location = mData.get(position).getLocation();

                if (location != null && !location.isEmpty()) {
                    holder.propertiesDivider.setVisibility(View.GONE);
                    holder.locationIcon.setVisibility(View.VISIBLE);
                } else if (schedule != null) {
                    holder.time.setVisibility(View.VISIBLE);
                    holder.time.setText(DateUtils.getTimeAsString(mContext.get(), schedule));
                    holder.time.setTextColor(ThemeUtils.getSectionColor(Sections.LATER, mContext.get()));
                }

                displayProperties = true;
                break;
            case FOCUS:
                // Set priority button color.
                holder.priorityButton.setBackgroundResource(R.drawable.focus_circle_selector);
                break;
            case DONE:
                // Set priority button color.
                holder.priorityButton.setBackgroundResource(R.drawable.done_circle_selector);

                // Display completion time and hide repeat icon.
                Date completionDate = mData.get(position).getCompletionDate();

                if (completionDate != null) {
                    holder.time.setVisibility(View.VISIBLE);
                    holder.time.setText(DateUtils.getTimeAsString(mContext.get(), completionDate));
                    holder.time.setTextColor(ThemeUtils.getSectionColor(Sections.DONE, mContext.get()));
                }

                holder.repeatIcon.setVisibility(View.GONE);
                displayProperties = true;
                break;
        }
    }

    private void updateIdMap() {
        mIdMap.clear();
        for (int i = 0; i < mData.size(); ++i) {
            mIdMap.put(mData.get(i).getObjectId(), i);
        }
    }

    public void setListContentsListener(ListContentsListener listContentsListener) {
        mListContentsListener = listContentsListener;
    }

    public List<GsonTask> getData() {
        return mData;
    }

    public void update(List<GsonTask> data) {
        mData = data;
        updateIdMap();
        notifyDataSetChanged();
    }

    private static class TaskHolder {

        // Containers.
        RelativeLayout frontView;
        RelativeLayout backView;

        // Priority.
        CheckBox priorityButton;

        // Main attributes.
        TextView title;
        TextView time;

        // Properties.
        RelativeLayout propertiesContainer;
        TextView propertiesDivider;
        SwipesTextView locationIcon;
        SwipesTextView notesIcon;
        SwipesTextView repeatIcon;

        // Tags.
        TextView tagsDivider;
        TextView tags;
    }

}