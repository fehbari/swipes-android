package com.swipesapp.android.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.swipesapp.android.R;
import com.swipesapp.android.handler.RepeatHandler;
import com.swipesapp.android.handler.SoundHandler;
import com.swipesapp.android.sync.gson.GsonTask;
import com.swipesapp.android.sync.service.TasksService;
import com.swipesapp.android.ui.activity.AddTasksActivity;
import com.swipesapp.android.ui.activity.EditTaskActivity;
import com.swipesapp.android.ui.activity.TasksActivity;
import com.swipesapp.android.util.DateUtils;
import com.swipesapp.android.util.PreferenceUtils;
import com.swipesapp.android.util.ThemeUtils;
import com.swipesapp.android.values.Constants;
import com.swipesapp.android.values.Intents;
import com.swipesapp.android.values.Sections;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Provider for the Swipes Now widget.
 *
 * @author Fernanda Bari
 */
public class NowWidgetProvider extends AppWidgetProvider {

    public static final String WIDTH_KEY = "now_widget_width";
    public static final int SMALL_WIDTH = 180;

    private static TasksService sTasksService;
    private static long sLastToastTime;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Load service.
        sTasksService = TasksService.getInstance();
        if (sTasksService == null) sTasksService = TasksService.newInstance(context);

        // Perform loop for each widget belonging to this provider.
        for (int appWidgetId : appWidgetIds) {
            // Initialize widget layout.
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.now_widget);

            // Setup tasks list.
            setupTasksList(appWidgetId, views, context);

            // Setup toolbar at the bottom.
            setupToolbar(views, context);

            // Update empty view messages.
            updateEmptyView(views, context);

            // Tell AppWidgetManager to update current widget.
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Long taskId = intent.getLongExtra(Constants.EXTRA_TASK_ID, 0);

        // Filter widget actions.
        if (Intents.WIDGET_COMPLETE_TASK.equals(action)) {
            sTasksService = TasksService.getInstance();

            // Complete task.
            GsonTask task = sTasksService.loadTask(taskId);
            task.setLocalCompletionDate(new Date());
            sTasksService.saveTask(task, true);

            // Handle repeat.
            RepeatHandler repeatHandler = new RepeatHandler();
            repeatHandler.handleRepeatedTask(task);

            // Show success message.
            long now = Calendar.getInstance().getTimeInMillis();
            if (now - sLastToastTime > 2000) {
                Toast.makeText(context, context.getString(R.string.now_widget_complete_message), Toast.LENGTH_SHORT).show();
                sLastToastTime = now;
            }

            // Refresh widget and tasks.
            TasksActivity.refreshWidgets(context);
            TasksActivity.setPendingRefresh();

            // Play sound.
            if (sTasksService.countTasksForNow() > 0) {
                SoundHandler.playSound(context, R.raw.complete_task_1);
            } else {
                SoundHandler.playSound(context, R.raw.all_done_today);
            }
        } else if (Intents.WIDGET_OPEN_TASK.equals(action) || Intents.WIDGET_OPEN_SUBTASKS.equals(action)) {
            // Open task intent.
            Intent openIntent = new Intent(context, EditTaskActivity.class);
            openIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            openIntent.putExtra(Constants.EXTRA_TASK_ID, taskId);
            openIntent.putExtra(Constants.EXTRA_SECTION_NUMBER, Sections.FOCUS.getSectionNumber());
            openIntent.putExtra(Constants.EXTRA_SHOW_ACTION_STEPS, Intents.WIDGET_OPEN_SUBTASKS.equals(action));
            openIntent.putExtra(Constants.EXTRA_FROM_WIDGET, true);

            // Show task details.
            context.startActivity(openIntent);
        }

        super.onReceive(context, intent);
    }

    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        // Get view width on Jelly Bean and up.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // Save resized width for later.
            int width = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            PreferenceUtils.saveInt(WIDTH_KEY, width, context);

            // Call widget update.
            TasksActivity.refreshWidgets(context);
        }
    }

    public void onDeleted(Context context, int[] appWidgetIds) {
        // Clear saved width.
        PreferenceUtils.remove(WIDTH_KEY, context);
    }

    private void setupTasksList(int appWidgetId, RemoteViews views, Context context) {
        // Intent to start the widget service.
        Intent intent = new Intent(context, NowWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        // Set the remote adapter to populate data.
        views.setRemoteAdapter(R.id.now_widget_list, intent);

        // Set the empty view.
        views.setEmptyView(R.id.now_widget_list, R.id.now_widget_empty);

        // Widget action intent.
        Intent actionIntent = new Intent(context, NowWidgetProvider.class);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, actionIntent, 0);

        // Attach intent template.
        views.setPendingIntentTemplate(R.id.now_widget_list, actionPendingIntent);

        // Set backgrounds according to theme.
        int background = ThemeUtils.isLightTheme(context) ?
                R.drawable.widget_tasks_background_light : R.drawable.widget_tasks_background_dark;

        views.setInt(R.id.now_widget_list, "setBackgroundResource", background);
        views.setInt(R.id.now_widget_empty, "setBackgroundResource", background);

        // Set text colors.
        int color = ThemeUtils.getSecondaryTextColor(context);
        views.setInt(R.id.now_widget_all_done, "setTextColor", color);
        views.setInt(R.id.now_widget_next_task, "setTextColor", color);
    }

    private void setupToolbar(RemoteViews views, Context context) {
        // Show tasks intent.
        Intent tasksIntent = new Intent(context, TasksActivity.class);
        tasksIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent tasksPendingIntent = PendingIntent.getActivity(context, 0, tasksIntent, 0);

        // Add task intent.
        Intent addIntent = new Intent(context, AddTasksActivity.class);
        addIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        addIntent.setAction(Intents.ADD_TASK);
        addIntent.putExtra(Constants.EXTRA_FROM_WIDGET, true);
        PendingIntent addPendingIntent = PendingIntent.getActivity(context, 0, addIntent, 0);

        // Attach click listeners to buttons.
        views.setOnClickPendingIntent(R.id.now_widget_show_tasks, tasksPendingIntent);
        views.setOnClickPendingIntent(R.id.now_widget_add_task, addPendingIntent);
        views.setOnClickPendingIntent(R.id.now_widget_count_area, tasksPendingIntent);

        // Retrieve tasks count.
        int completedToday = sTasksService.countTasksCompletedToday();
        int tasksToday = sTasksService.countTasksForToday() + completedToday;

        // Display tasks count.
        if (tasksToday > 0) {
            views.setTextViewText(R.id.now_widget_tasks_completed, String.valueOf(completedToday));
            views.setTextViewText(R.id.now_widget_tasks_today, String.valueOf(tasksToday));

            views.setViewVisibility(R.id.now_widget_tasks_progress, View.VISIBLE);
            views.setViewVisibility(R.id.now_widget_empty_count, View.GONE);
        } else {
            views.setViewVisibility(R.id.now_widget_tasks_progress, View.GONE);
            views.setViewVisibility(R.id.now_widget_empty_count, View.VISIBLE);
        }

        // Set text for labels.
        views.setTextViewText(R.id.now_widget_count_label, context.getString(R.string.now_widget_tasks_count_label));
        views.setTextViewText(R.id.now_widget_empty_count, context.getString(R.string.now_widget_empty_count_label));

        // Show progress area.
        views.setViewVisibility(R.id.now_widget_count_area, View.VISIBLE);

        // Load current width.
        int width = PreferenceUtils.readInt(WIDTH_KEY, context);

        // Hide or show views based on width.
        int visibility = width > 0 && width < SMALL_WIDTH ? View.GONE : View.VISIBLE;
        views.setViewVisibility(R.id.now_widget_show_tasks, visibility);
    }

    private void updateEmptyView(RemoteViews views, Context context) {
        // Load next scheduled task.
        List<GsonTask> tasks = sTasksService.loadScheduledTasks();
        GsonTask nextTask = !tasks.isEmpty() ? tasks.get(0) : null;

        if (nextTask != null && nextTask.getLocalSchedule() != null) {
            Date nextSchedule = nextTask.getLocalSchedule();

            // Set text according to the next scheduled task.
            if (DateUtils.isToday(nextSchedule)) {
                views.setTextViewText(R.id.now_widget_all_done, context.getString(R.string.all_done_now));
                String nextDate = DateUtils.getTimeAsString(context, nextSchedule);
                views.setTextViewText(R.id.now_widget_next_task, context.getString(R.string.all_done_now_next, nextDate));
            } else {
                views.setTextViewText(R.id.now_widget_all_done, context.getString(R.string.all_done_today));
                String nextDate = DateUtils.formatToRecent(nextSchedule, context, false);
                views.setTextViewText(R.id.now_widget_next_task, context.getString(R.string.all_done_today_next, nextDate));
            }
        } else {
            // Show default message.
            views.setTextViewText(R.id.now_widget_all_done, context.getString(R.string.all_done_today));
            views.setTextViewText(R.id.now_widget_next_task, context.getString(R.string.all_done_next_empty));
        }
    }

    public static void refreshWidget(Context context) {
        // Load manager and IDs.
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] ids = manager.getAppWidgetIds(new ComponentName(context, NowWidgetProvider.class));

        // Update widget intent.
        Intent intent = new Intent(context, NowWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);

        // Send update broadcast.
        context.sendBroadcast(intent);
        manager.notifyAppWidgetViewDataChanged(ids, R.id.now_widget_list);
    }

}
