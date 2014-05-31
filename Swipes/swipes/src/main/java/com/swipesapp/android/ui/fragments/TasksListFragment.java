/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swipesapp.android.ui.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.swipesapp.android.Cheeses;
import com.swipesapp.android.R;
import com.swipesapp.android.adapter.TasksListAdapter;
import com.swipesapp.android.ui.listener.ListContentsListener;
import com.swipesapp.android.ui.view.DynamicListView;
import com.swipesapp.android.util.ThemeUtils;
import com.swipesapp.android.values.Sections;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Fragment for the list of tasks.
 */
public class TasksListFragment extends ListFragment {

    /**
     * The fragment argument representing the section number for this fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String LOG_TAG = TasksListFragment.class.getCanonicalName();

    /**
     * Customized list view to display tasks.
     */
    DynamicListView mListView;
    private int mCurrentSection;

    @InjectView(android.R.id.empty)
    ViewStub mViewStub;

    public static TasksListFragment newInstance(int sectionNumber) {
        TasksListFragment fragment = new TasksListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: Remove this and use real data.
        Bundle args = getArguments();
        mCurrentSection = args.getInt(ARG_SECTION_NUMBER, 1);
        ArrayList<String> cheeseList = new ArrayList<String>();
        if (mCurrentSection == Sections.FOCUS.getSectionNumber()) {
            for (int i = 0; i < Cheeses.sCheeseStrings.length; ++i) {
                cheeseList.add(Cheeses.sCheeseStrings[i]);
            }
        }

        View rootView = inflater.inflate(R.layout.fragment_focus_list, container, false);
        ButterKnife.inject(this, rootView);

        TasksListAdapter adapter = new TasksListAdapter(getActivity(), R.layout.swipeable_cell, cheeseList);

        Activity hostActivity = getActivity();
        if (hostActivity instanceof ListContentsListener) {
            adapter.setListContentsListener((ListContentsListener) hostActivity);
        }

        mListView = (DynamicListView) rootView.findViewById(android.R.id.list);

        configureListView(adapter, cheeseList);

        configureEmptyView(mCurrentSection);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    private void configureListView(ListAdapter adapter, ArrayList<String> data) {
        // TODO: Remove and use a different data type with real data.
        mListView.setCheeseList(data);
        mListView.setAdapter(adapter);
        mListView.setSwipeListViewListener(mSwipeListener);
        mListView.setBackgroundColor(ThemeUtils.getCurrentThemeBackgroundColor(getActivity()));
        mListView.setSwipeBackgroundColors(ThemeUtils.getSectionColor(Sections.DONE, getActivity()), ThemeUtils.getSectionColor(Sections.LATER, getActivity()), ThemeUtils.getCurrentThemeBackgroundColor(getActivity()));
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH);
        mListView.setSwipeActionRight(SwipeListView.SWIPE_ACTION_DISMISS);
        mListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
    }

    private void configureEmptyView(int currentSection) {
        switch (currentSection) {
            case 0:
                mViewStub.setLayoutResource(R.layout.tasks_later_empty_view);
                break;
            case 1:
                mViewStub.setLayoutResource(R.layout.tasks_focus_empty_view);
                break;
            case 2:
                mViewStub.setLayoutResource(R.layout.tasks_done_empty_view);
                break;
            default:
                Log.wtf(LOG_TAG, "Shouldn't be here");
        }
    }

    private BaseSwipeListViewListener mSwipeListener = new BaseSwipeListViewListener() {
        @Override
        public void onFinishedSwipeRight(int position) {
            Sections currentSection = Sections.getSectionByNumber(mCurrentSection);
            switch (currentSection) {
                case LATER:
                    // TODO: Move task from Later to Focus.
                    Toast.makeText(getActivity(), "TODO: Move task to Focus.", Toast.LENGTH_SHORT).show();
                    break;
                case FOCUS:
                    // TODO: Move task from Focus to Done.
                    Toast.makeText(getActivity(), "TODO: Move task to Done.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onFinishedSwipeLeft(int position) {
            Sections currentSection = Sections.getSectionByNumber(mCurrentSection);
            switch (currentSection) {
                case LATER:
                    // TODO: Reschedule task.
                    Toast.makeText(getActivity(), "TODO: Reschedule task.", Toast.LENGTH_SHORT).show();
                    break;
                case FOCUS:
                    // TODO: Move task from Focus to Later.
                    Toast.makeText(getActivity(), "TODO: Move task to Later.", Toast.LENGTH_SHORT).show();
                    break;
                case DONE:
                    // TODO: Move task from Done to Focus.
                    Toast.makeText(getActivity(), "TODO: Move task to Focus.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onFinishedLongSwipeRight(int position) {
            Sections currentSection = Sections.getSectionByNumber(mCurrentSection);
            switch (currentSection) {
                case LATER:
                    // TODO: Move task from Later to Done.
                    Toast.makeText(getActivity(), "TODO: Move task to Done.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onFinishedLongSwipeLeft(int position) {
            Sections currentSection = Sections.getSectionByNumber(mCurrentSection);
            switch (currentSection) {
                case DONE:
                    // TODO: Move task from Done to Later.
                    Toast.makeText(getActivity(), "TODO: Move task to Later.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
