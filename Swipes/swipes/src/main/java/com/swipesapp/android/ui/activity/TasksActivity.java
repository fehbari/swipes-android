package com.swipesapp.android.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.negusoft.holoaccent.activity.AccentActivity;
import com.swipesapp.android.R;
import com.swipesapp.android.sync.gson.GsonTag;
import com.swipesapp.android.sync.gson.GsonTask;
import com.swipesapp.android.sync.service.TasksService;
import com.swipesapp.android.ui.adapter.SectionsPagerAdapter;
import com.swipesapp.android.ui.listener.ListContentsListener;
import com.swipesapp.android.ui.view.BlurBuilder;
import com.swipesapp.android.ui.view.FactorSpeedScroller;
import com.swipesapp.android.ui.view.FlowLayout;
import com.swipesapp.android.ui.view.NoSwipeViewPager;
import com.swipesapp.android.ui.view.SwipesButton;
import com.swipesapp.android.util.Constants;
import com.swipesapp.android.util.ThemeUtils;
import com.swipesapp.android.values.Actions;
import com.swipesapp.android.values.RepeatOptions;
import com.swipesapp.android.values.Sections;
import com.swipesapp.android.values.Themes;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class TasksActivity extends AccentActivity implements ListContentsListener {

    @InjectView(R.id.pager)
    NoSwipeViewPager mViewPager;

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip mTabs;

    @InjectView(R.id.tasks_activity_container)
    ViewGroup mActivityMainLayout;

    @InjectView(R.id.button_add_task)
    SwipesButton mButtonAddTask;

    @InjectView(R.id.blur_background)
    ImageView mBlurBackground;

    @InjectView(R.id.add_task_container)
    RelativeLayout mAddTaskContainer;

    @InjectView(R.id.edit_text_add_task_content)
    EditText mEditTextAddNewTask;

    @InjectView(R.id.button_confirm_add_task)
    SwipesButton mButtonConfirmAddTask;

    @InjectView(R.id.button_add_task_priority)
    CheckBox mButtonAddTaskPriority;

    @InjectView(R.id.edit_tasks_bar)
    LinearLayout mEditTasksBar;

    @InjectView(R.id.button_edit_task)
    SwipesButton mButtonEditTask;

    @InjectView(R.id.button_assign_tags)
    SwipesButton mButtonAssignTags;

    @InjectView(R.id.button_delete_tasks)
    SwipesButton mButtonDeleteTasks;

    @InjectView(R.id.button_share_tasks)
    SwipesButton mButtonShareTasks;

    @InjectView(R.id.add_task_tag_container)
    FlowLayout mAddTaskTagContainer;

    @InjectView(R.id.button_add_task_tag)
    CheckBox mButtonAddTaskTag;

    private static final String LOG_TAG = TasksActivity.class.getSimpleName();

    private WeakReference<Context> mContext;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private Sections mCurrentSection;

    private static Typeface sTypeface;

    // TODO: Populate list of selected tags.
    private List<GsonTag> mSelectedTags;

    // Used by animator to store tags container position.
    private float mTagsTranslationY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeUtils.getCurrentThemeResource(this));
        setContentView(R.layout.activity_tasks);
        ButterKnife.inject(this);
        mContext = new WeakReference<Context>(this);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager(), this);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabs.setViewPager(mViewPager);

        if (sTypeface == null) {
            sTypeface = Typeface.createFromAsset(getAssets(), Constants.FONT_NAME);
        }
        mTabs.setTypeface(sTypeface, 0);

        int dimension = getResources().getDimensionPixelSize(R.dimen.action_bar_icon_size);
        mTabs.setTextSize(dimension);
        mTabs.setIndicatorColor(ThemeUtils.getCurrentThemeTextColor(this));
        mTabs.setTextColor(ThemeUtils.getCurrentThemeTextColor(this));
        mTabs.setDividerColor(ThemeUtils.getCurrentThemeDividerColor(this));
        mTabs.setTabBackground(ThemeUtils.getCurrentThemeTabBackground(this));
        mTabs.setOnPageChangeListener(mSimpleOnPageChangeListener);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mSelectedTags = new ArrayList<GsonTag>();

        mTagsTranslationY = mAddTaskTagContainer.getTranslationY();

        // Default to second item, index starts at zero
        mViewPager.setCurrentItem(Sections.FOCUS.getSectionNumber());
        mCurrentSection = Sections.FOCUS;

        // HACK: Flip add task confirm button, so the arrow points to the right.
        mButtonConfirmAddTask.setScaleX(-mButtonConfirmAddTask.getScaleX());

        // Define a custom duration to the page scroller, providing a more natural feel.
        customizeScroller();

        // Customize tag button to match current theme.
        customizeTagButton();

        // Set button selectors.
        mButtonAddTask.setSelector(R.string.round_add, R.string.round_add_full);

        // TODO: Remove this when tagging is working. The container is hidden for the first beta.
        mAddTaskTagContainer.setEnabled(false);
        mAddTaskTagContainer.setAlpha(0f);
    }

    @Override
    protected void onDestroy() {
        ButterKnife.reset(this);
        super.onDestroy();
    }

    private ViewPager.SimpleOnPageChangeListener mSimpleOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            int[] textColors = {
                    ThemeUtils.getSectionColor(Sections.LATER, mContext.get()),
                    ThemeUtils.getSectionColor(Sections.FOCUS, mContext.get()),
                    ThemeUtils.getSectionColor(Sections.DONE, mContext.get()),
                    ThemeUtils.getCurrentThemeTextColor(mContext.get())
            };
            mTabs.setIndicatorColor(textColors[position]);
            mTabs.setTextColor(ThemeUtils.getCurrentThemeTextColor(mContext.get()));
            View v = mTabs.getTabView(position);
            if (v instanceof TextView) {
                TextView tabTextView = (TextView) v;
                tabTextView.setTextColor(textColors[position]);
            }

            if (position == Sections.SETTINGS.getSectionNumber()) {
                mButtonAddTask.setVisibility(View.GONE);
                clearEmptyBackground();
            } else {
                mButtonAddTask.setVisibility(View.VISIBLE);
            }

            mCurrentSection = Sections.getSectionByNumber(position);

            // Notify listeners that current tab has changed.
            TasksService.getInstance(mContext.get()).sendBroadcast(Actions.TAB_CHANGED);

            hideEditBar();
        }
    };

    /**
     * Returns the current section being displayed.
     *
     * @return Current section.
     */
    public Sections getCurrentSection() {
        return mCurrentSection;
    }

    private void customizeScroller() {
        try {
            // HACK: Use reflection to access the scroller and customize it.
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(mViewPager, new FactorSpeedScroller(this));
        } catch (Exception e) {
            Log.e(LOG_TAG, "Something went wrong accessing field \"mScroller\" inside ViewPager class", e);
        }
    }

    private void customizeTagButton() {
        int background = ThemeUtils.getCurrentTheme(this) == Themes.LIGHT ? R.drawable.tag_selector_light : R.drawable.tag_selector_dark;
        int color = ThemeUtils.getCurrentTheme(this) == Themes.LIGHT ? R.color.text_color_selector_light : R.color.text_color_selector_dark;
        mButtonAddTaskTag.setBackgroundResource(background);
        mButtonAddTaskTag.setTextColor(getResources().getColorStateList(color));
    }

    private void clearEmptyBackground() {
        // Reset background and divider color.
        mActivityMainLayout.setBackgroundColor(ThemeUtils.getCurrentThemeBackgroundColor(this));
        mTabs.setDividerColor(ThemeUtils.getCurrentThemeDividerColor(this));

        // Reset tab colors.
        mTabs.setTextColor(ThemeUtils.getCurrentThemeTextColor(this));
        mTabs.setDividerColor(ThemeUtils.getCurrentThemeDividerColor(this));

        // Reset buttons and text colors.
        mButtonAddTask.setTextColor(ThemeUtils.getCurrentThemeTextColor(this));
        mButtonConfirmAddTask.setTextColor(ThemeUtils.getCurrentThemeTextColor(this));
        mEditTextAddNewTask.setTextColor(ThemeUtils.getCurrentThemeTextColor(this));
        mEditTextAddNewTask.setBackgroundResource(ThemeUtils.getCurrentThemeEditTextBackground(this));
    }

    private void setEmptyBackground() {
        // Change background.
        mActivityMainLayout.setBackgroundResource(R.drawable.default_background);

        // Load white color.
        int white = getResources().getColor(R.color.white);

        // Change tab colors, otherwise they look misplaced against the image background.
        mTabs.setTextColor(white);
        mTabs.setDividerColor(getResources().getColor(R.color.empty_divider));

        // Change buttons and text colors to improve visibility.
        mButtonAddTask.setTextColor(white);
        mButtonConfirmAddTask.setTextColor(white);
        mEditTextAddNewTask.setTextColor(white);
        mEditTextAddNewTask.setBackgroundResource(R.drawable.dark_theme_edit_text_background);
    }

    /**
     * Shows the task edit bar.
     *
     * @param isBatchOperation True when multiple tasks are selected.
     */
    public void showEditBar(boolean isBatchOperation) {
        // Animate views only when necessary.
        if (mEditTasksBar.getVisibility() == View.GONE) {
            Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
            slideDown.setAnimationListener(mShowEditBarListener);
            mButtonAddTask.startAnimation(slideDown);

            Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            mEditTasksBar.startAnimation(slideUp);
        }

        // The edit button shouldn't be used for multiple tasks at once.
        if (isBatchOperation) {
            // Disable button and make it transparent instead of invisible, to preserve layout positions.
            mButtonEditTask.setEnabled(false);
            mButtonEditTask.setTextColor(0);
        } else {
            // Enable button and apply color.
            mButtonEditTask.setEnabled(true);
            mButtonEditTask.setTextColor(ThemeUtils.getCurrentThemeTextColor(this));
        }
    }

    public void hideEditBar() {
        // Animate views only when necessary.
        if (mEditTasksBar.isShown()) {
            Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
            slideDown.setAnimationListener(mHideEditBarListener);
            mEditTasksBar.startAnimation(slideDown);

            Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            mButtonAddTask.startAnimation(slideUp);
        }
    }

    Animation.AnimationListener mShowEditBarListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            mEditTasksBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mButtonAddTask.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    Animation.AnimationListener mHideEditBarListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            mButtonAddTask.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mEditTasksBar.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    @OnClick(R.id.button_confirm_add_task)
    protected void addTask() {
        Date currentDate = new Date();
        String title = mEditTextAddNewTask.getText().toString();
        Integer priority = mButtonAddTaskPriority.isChecked() ? 1 : 0;
        // TODO: What should the temp ID be?
        String tempId = title + currentDate.getTime();

        if (!title.isEmpty()) {
            GsonTask task = new GsonTask(null, tempId, null, currentDate, currentDate, false, title, null, 0, priority, null, currentDate, null, null, RepeatOptions.NEVER.getValue(), null, null, mSelectedTags, 0);
            TasksService.getInstance(this).saveTask(task);
        }

        endAddTaskWorkflow();
    }

    @OnClick(R.id.button_add_task)
    protected void startAddTaskWorkflow() {
        // Go to main fragment if needed.
        if (mCurrentSection != Sections.FOCUS) {
            mViewPager.setCurrentItem(Sections.FOCUS.getSectionNumber());
        }

        // Blur background.
        Bitmap blurBitmap = BlurBuilder.blur(mActivityMainLayout);
        mBlurBackground.setImageBitmap(blurBitmap);

        // Fade in the blur background.
        mBlurBackground.setAlpha(0f);
        mBlurBackground.setVisibility(View.VISIBLE);
        mBlurBackground.animate().alpha(1f).setDuration(Constants.ANIMATION_DURATION).setListener(mBlurFadeInListener);

        // Show and hide keyboard automatically.
        mEditTextAddNewTask.setOnFocusChangeListener(mFocusListener);

        // Display edit text.
        mEditTextAddNewTask.setVisibility(View.VISIBLE);
        mEditTextAddNewTask.setFocusable(true);
        mEditTextAddNewTask.setFocusableInTouchMode(true);
        mEditTextAddNewTask.requestFocus();
        mEditTextAddNewTask.bringToFront();

        // Display add task area.
        mAddTaskContainer.setVisibility(View.VISIBLE);

        // Display tags area.
        animateTags(false);
    }

    @OnClick(R.id.blur_background)
    protected void endAddTaskWorkflow() {
        // Remove focus and hide text view.
        mEditTextAddNewTask.clearFocus();
        mEditTextAddNewTask.setVisibility(View.GONE);

        // Reset fields.
        mEditTextAddNewTask.setText("");
        mButtonAddTaskPriority.setChecked(false);

        // Hide add task area.
        mAddTaskContainer.setVisibility(View.GONE);

        // Hide tags area.
        animateTags(true);

        // Show the main layout.
        mActivityMainLayout.setAlpha(1f);
        mActivityMainLayout.setVisibility(View.VISIBLE);

        // Fade out the blur background.
        mBlurBackground.animate().alpha(0f).setDuration(Constants.ANIMATION_DURATION).setListener(mBlurFadeOutListener);
    }

    private void animateTags(boolean isHiding) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        float fromY = isHiding ? mAddTaskTagContainer.getY() : -displaymetrics.heightPixels;
        float toY = isHiding ? -displaymetrics.heightPixels : mTagsTranslationY;

        ObjectAnimator animator = ObjectAnimator.ofFloat(mAddTaskTagContainer, "translationY", fromY, toY);
        animator.setDuration(Constants.ANIMATION_DURATION).start();
    }

    @OnClick(R.id.button_edit_task)
    protected void editTask() {
        // Send a broadcast to edit the currently selected task. The fragment should handle it.
        TasksService.getInstance(this).sendBroadcast(Actions.EDIT_TASK);
        // Close edit bar.
        hideEditBar();
    }

    @OnClick(R.id.button_assign_tags)
    protected void assignTags() {
        // Send a broadcast to assign tags to the selected tasks. The fragment should handle it.
        TasksService.getInstance(this).sendBroadcast(Actions.ASSIGN_TAGS);

        // TODO: Remove this when tagging is working.
        Toast.makeText(this, "Tags coming soon", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.button_delete_tasks)
    protected void deleteTasks() {
        // Send a broadcast to delete tasks. The fragment should handle it, since it contains the list.
        TasksService.getInstance(this).sendBroadcast(Actions.DELETE_TASKS);
    }

    @OnClick(R.id.button_share_tasks)
    protected void shareTasks() {
        // Send a broadcast to share selected tasks. The fragment should handle it.
        TasksService.getInstance(this).sendBroadcast(Actions.SHARE_TASKS);

        // TODO: Remove this when sharing is working.
        Toast.makeText(this, "Sharing coming soon", Toast.LENGTH_SHORT).show();
    }

    private AnimatorListenerAdapter mBlurFadeInListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            // Hide the main layout.
            mActivityMainLayout.setVisibility(View.GONE);
        }
    };

    private AnimatorListenerAdapter mBlurFadeOutListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            // Hide the blur background.
            mBlurBackground.setVisibility(View.GONE);
        }
    };

    private View.OnFocusChangeListener mFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (hasFocus) {
                imm.showSoftInput(mEditTextAddNewTask, InputMethodManager.SHOW_IMPLICIT);
            } else {
                imm.hideSoftInputFromWindow(mEditTextAddNewTask.getWindowToken(), 0);
            }
        }
    };

    // HACK: This is a workaround to change the background entirely.
    @Override
    public void onEmpty(Sections section) {
        if (section == mCurrentSection) {
            if (section == Sections.FOCUS) {
                setEmptyBackground();
            } else {
                clearEmptyBackground();
            }
        }
    }

    // HACK: This is a workaround to change the background entirely.
    @Override
    public void onNotEmpty(Sections section) {
        if (section == mCurrentSection) {
            clearEmptyBackground();
        }
    }

    public void shareOnFacebook(View v) {
        // TODO: Call sharing flow.
        Toast.makeText(this, "Facebook share coming soon", Toast.LENGTH_SHORT).show();
    }

    public void shareOnTwitter(View v) {
        // TODO: Call sharing flow.
        Toast.makeText(this, "Twitter share coming soon", Toast.LENGTH_SHORT).show();
    }

}
