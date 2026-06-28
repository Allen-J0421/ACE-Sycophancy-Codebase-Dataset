package com.termux.app;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.termux.R;
import com.termux.app.activities.HelpActivity;
import com.termux.app.activities.SettingsActivity;
import com.termux.app.api.file.FileReceiverActivity;
import com.termux.app.terminal.TermuxActivityRootView;
import com.termux.app.terminal.TermuxSessionsListViewController;
import com.termux.app.terminal.TermuxTerminalSessionActivityClient;
import com.termux.app.terminal.TermuxTerminalViewClient;
import com.termux.app.terminal.io.TerminalToolbarViewPager;
import com.termux.app.terminal.io.TermuxTerminalExtraKeys;
import com.termux.shared.activity.ActivityUtils;
import com.termux.shared.activity.media.AppCompatActivityUtils;
import com.termux.shared.android.PermissionUtils;
import com.termux.shared.data.DataUtils;
import com.termux.shared.data.IntentUtils;
import com.termux.shared.logger.Logger;
import com.termux.shared.termux.TermuxConstants;
import com.termux.shared.termux.TermuxConstants.TERMUX_APP.TERMUX_ACTIVITY;
import com.termux.shared.termux.TermuxInstaller;
import com.termux.shared.termux.TermuxUtils;
import com.termux.shared.termux.crash.TermuxCrashUtils;
import com.termux.shared.termux.extrakeys.ExtraKeysView;
import com.termux.shared.termux.interact.TextInputDialogUtils;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;
import com.termux.shared.termux.settings.properties.TermuxAppSharedProperties;
import com.termux.shared.termux.theme.TermuxThemeUtils;
import com.termux.shared.theme.NightMode;
import com.termux.shared.view.ViewUtils;
import com.termux.shared.termux.shell.command.runner.terminal.TermuxSession;
import com.termux.terminal.TerminalSession;
import com.termux.view.TerminalView;

import java.util.Arrays;

final class TermuxActivityController {

    private static final String LOG_TAG = "TermuxActivity";

    private static final int CONTEXT_MENU_SELECT_URL_ID = 0;
    private static final int CONTEXT_MENU_SHARE_TRANSCRIPT_ID = 1;
    private static final int CONTEXT_MENU_SHARE_SELECTED_TEXT = 10;
    private static final int CONTEXT_MENU_AUTOFILL_USERNAME = 11;
    private static final int CONTEXT_MENU_AUTOFILL_PASSWORD = 2;
    private static final int CONTEXT_MENU_RESET_TERMINAL_ID = 3;
    private static final int CONTEXT_MENU_KILL_PROCESS_ID = 4;
    private static final int CONTEXT_MENU_STYLING_ID = 5;
    private static final int CONTEXT_MENU_TOGGLE_KEEP_SCREEN_ON = 6;
    private static final int CONTEXT_MENU_HELP_ID = 7;
    private static final int CONTEXT_MENU_SETTINGS_ID = 8;
    private static final int CONTEXT_MENU_REPORT_ID = 9;

    private static final String ARG_TERMINAL_TOOLBAR_TEXT_INPUT = "terminal_toolbar_text_input";
    private static final String ARG_ACTIVITY_RECREATED = "activity_recreated";

    final TermuxActivity mActivity;
    private final BroadcastReceiver mTermuxActivityBroadcastReceiver = new TermuxActivityBroadcastReceiver();
    private final TermuxActivityUiController mUiController;

    TermuxService mTermuxService;
    TerminalView mTerminalView;
    TermuxTerminalViewClient mTermuxTerminalViewClient;
    TermuxTerminalSessionActivityClient mTermuxTerminalSessionActivityClient;
    TermuxAppSharedPreferences mPreferences;
    TermuxAppSharedProperties mProperties;
    TermuxActivityRootView mTermuxActivityRootView;
    View mTermuxActivityBottomSpaceView;
    ExtraKeysView mExtraKeysView;
    TermuxTerminalExtraKeys mTermuxTerminalExtraKeys;
    TermuxSessionsListViewController mTermuxSessionListViewController;
    Toast mLastToast;
    boolean mIsVisible;
    boolean mIsOnResumeAfterOnCreate = true;
    boolean mIsActivityRecreated;
    boolean mIsInvalidState;
    int mNavBarHeight;
    float mTerminalToolbarDefaultHeight;

    TermuxActivityController(TermuxActivity activity) {
        mActivity = activity;
        mUiController = new TermuxActivityUiController(this);
    }

    void initializeActivityState(@Nullable Bundle savedInstanceState) {
        mIsOnResumeAfterOnCreate = true;

        if (savedInstanceState != null) {
            mIsActivityRecreated = savedInstanceState.getBoolean(ARG_ACTIVITY_RECREATED, false);
        }

        // Delete ReportInfo serialized object files from cache older than 14 days
        com.termux.shared.activities.ReportActivity.deleteReportInfoFilesOlderThanXDays(mActivity, 14, false);

        mProperties = TermuxAppSharedProperties.getProperties();
        reloadProperties();
        setActivityTheme();
    }

    void initializeActivityContent(@Nullable Bundle savedInstanceState) {
        mPreferences = TermuxAppSharedPreferences.build(mActivity, true);
        if (mPreferences == null) {
            mIsInvalidState = true;
            return;
        }

        mUiController.initializeViews(savedInstanceState);
        startAndBindService();

        if (mIsInvalidState) return;

        TermuxUtils.sendTermuxOpenedBroadcast(mActivity);
    }

    private void initializeViews(@Nullable Bundle savedInstanceState) {
        mUiController.initializeViews(savedInstanceState);
    }

    private void startAndBindService() {
        try {
            Intent serviceIntent = new Intent(mActivity, TermuxService.class);
            mActivity.startService(serviceIntent);

            if (!mActivity.bindService(serviceIntent, mActivity, 0))
                throw new RuntimeException("bindService() failed");
        } catch (Exception e) {
            Logger.logStackTraceWithMessage(LOG_TAG, "TermuxActivity failed to start TermuxService", e);
            Logger.showToast(mActivity,
                mActivity.getString(e.getMessage() != null && e.getMessage().contains("app is in background") ?
                    R.string.error_termux_service_start_failed_bg : R.string.error_termux_service_start_failed_general),
                true);
            mIsInvalidState = true;
        }
    }

    void onStart() {
        if (mIsInvalidState) return;

        mIsVisible = true;

        if (mTermuxTerminalSessionActivityClient != null) {
            mTermuxTerminalSessionActivityClient.onStart();
        }
        if (mTermuxTerminalViewClient != null) {
            mTermuxTerminalViewClient.onStart();
        }
        mUiController.onStart();
    }

    void onResume() {
        if (mIsInvalidState) return;

        if (mTermuxTerminalSessionActivityClient != null) {
            mTermuxTerminalSessionActivityClient.onResume();
        }
        if (mTermuxTerminalViewClient != null) {
            mTermuxTerminalViewClient.onResume();
        }

        TermuxCrashUtils.notifyAppCrashFromCrashLogFile(mActivity, LOG_TAG);
        mIsOnResumeAfterOnCreate = false;
    }

    void onStop() {
        if (mIsInvalidState) return;

        mIsVisible = false;

        if (mTermuxTerminalSessionActivityClient != null) {
            mTermuxTerminalSessionActivityClient.onStop();
        }
        if (mTermuxTerminalViewClient != null) {
            mTermuxTerminalViewClient.onStop();
        }

        mUiController.onStop();
    }

    void onDestroy() {
        if (mIsInvalidState) return;

        if (mTermuxService != null) {
            mTermuxService.unsetTermuxTerminalSessionClient();
            mTermuxService = null;
        }

        try {
            mActivity.unbindService(mActivity);
        } catch (Exception e) {
            // ignore
        }
    }

    void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        mUiController.onSaveInstanceState(savedInstanceState);
    }

    void onServiceConnected(ComponentName componentName, IBinder service) {
        mTermuxService = ((TermuxService.LocalBinder) service).service;
        mUiController.setTermuxSessionsListView();
        handleServiceConnectedIntent(mActivity.getIntent());
        mTermuxService.setTermuxTerminalSessionClient(mTermuxTerminalSessionActivityClient);
    }

    void onServiceDisconnected(ComponentName name) {
        finishActivityIfNotFinishing();
    }

    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mUiController.onActivityResult(requestCode, resultCode, data);
    }

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mUiController.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    void onBackPressed() {
        mUiController.onBackPressed();
    }

    void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        mUiController.onCreateContextMenu(menu, v, menuInfo);
    }

    boolean onCreateOptionsMenu(Menu menu) {
        return mUiController.onCreateOptionsMenu(menu);
    }

    boolean onContextItemSelected(MenuItem item) {
        return mUiController.onContextItemSelected(item);
    }

    void onContextMenuClosed(Menu menu) {
        mUiController.onContextMenuClosed(menu);
    }

    void requestStoragePermission(boolean isPermissionCallback) {
        mUiController.requestStoragePermission(isPermissionCallback);
    }

    void reloadActivityStyling(boolean recreateActivity) {
        mUiController.reloadActivityStyling(recreateActivity);
    }

    void reloadProperties() {
        mProperties.loadTermuxPropertiesFromDisk();

        if (mTermuxTerminalViewClient != null) {
            mTermuxTerminalViewClient.onReloadProperties();
        }
    }

    void toggleTerminalToolbar() {
        mUiController.toggleTerminalToolbar();
    }

    void showToast(String text, boolean longDuration) {
        mUiController.showToast(text, longDuration);
    }

    void finishActivityIfNotFinishing() {
        mUiController.finishActivityIfNotFinishing();
    }

    void addTermuxActivityRootViewGlobalLayoutListener() {
        mUiController.addTermuxActivityRootViewGlobalLayoutListener();
    }

    void removeTermuxActivityRootViewGlobalLayoutListener() {
        mUiController.removeTermuxActivityRootViewGlobalLayoutListener();
    }

    void setExtraKeysView(ExtraKeysView extraKeysView) {
        mUiController.setExtraKeysView(extraKeysView);
    }

    @Nullable
    TerminalSession getCurrentSession() {
        return mUiController.getCurrentSession();
    }

    int getNavBarHeight() {
        return mUiController.getNavBarHeight();
    }

    TermuxActivityRootView getTermuxActivityRootView() {
        return mUiController.getTermuxActivityRootView();
    }

    View getTermuxActivityBottomSpaceView() {
        return mUiController.getTermuxActivityBottomSpaceView();
    }

    ExtraKeysView getExtraKeysView() {
        return mUiController.getExtraKeysView();
    }

    TermuxTerminalExtraKeys getTermuxTerminalExtraKeys() {
        return mUiController.getTermuxTerminalExtraKeys();
    }

    DrawerLayout getDrawer() {
        return mUiController.getDrawer();
    }

    ViewPager getTerminalToolbarViewPager() {
        return mUiController.getTerminalToolbarViewPager();
    }

    float getTerminalToolbarDefaultHeight() {
        return mUiController.getTerminalToolbarDefaultHeight();
    }

    boolean isTerminalViewSelected() {
        return mUiController.isTerminalViewSelected();
    }

    boolean isTerminalToolbarTextInputViewSelected() {
        return mUiController.isTerminalToolbarTextInputViewSelected();
    }

    void termuxSessionListNotifyUpdated() {
        mUiController.termuxSessionListNotifyUpdated();
    }

    boolean isVisible() {
        return mIsVisible;
    }

    boolean isOnResumeAfterOnCreate() {
        return mIsOnResumeAfterOnCreate;
    }

    boolean isActivityRecreated() {
        return mIsActivityRecreated;
    }

    TermuxService getTermuxService() {
        return mTermuxService;
    }

    TerminalView getTerminalView() {
        return mTerminalView;
    }

    TermuxTerminalViewClient getTermuxTerminalViewClient() {
        return mTermuxTerminalViewClient;
    }

    TermuxTerminalSessionActivityClient getTermuxTerminalSessionClient() {
        return mTermuxTerminalSessionActivityClient;
    }

    TermuxAppSharedPreferences getPreferences() {
        return mPreferences;
    }

    TermuxAppSharedProperties getProperties() {
        return mProperties;
    }

    private void handleServiceConnectedIntent(@Nullable Intent intent) {
        mActivity.setIntent(null);

        if (mTermuxService.isTermuxSessionsEmpty()) {
            handleEmptyServiceSessionState(intent);
        } else {
            handleExistingServiceSessionState(intent);
        }
    }

    private void handleEmptyServiceSessionState(@Nullable Intent intent) {
        if (!mIsVisible) {
            finishActivityIfNotFinishing();
            return;
        }

        TermuxInstaller.setupBootstrapIfNeeded(mActivity, () -> {
            if (mTermuxService == null) return;
            try {
                boolean launchFailsafe = intent != null && intent.getExtras() != null &&
                    intent.getExtras().getBoolean(TERMUX_ACTIVITY.EXTRA_FAILSAFE_SESSION, false);
                mTermuxTerminalSessionActivityClient.addNewSession(launchFailsafe, null);
            } catch (WindowManager.BadTokenException e) {
                // Activity finished - ignore.
            }
        });
    }

    private void handleExistingServiceSessionState(@Nullable Intent intent) {
        if (!mIsActivityRecreated && intent != null && Intent.ACTION_RUN.equals(intent.getAction())) {
            boolean isFailSafe = intent.getBooleanExtra(TERMUX_ACTIVITY.EXTRA_FAILSAFE_SESSION, false);
            mTermuxTerminalSessionActivityClient.addNewSession(isFailSafe, null);
        } else {
            mTermuxTerminalSessionActivityClient.setCurrentSession(
                mTermuxTerminalSessionActivityClient.getCurrentStoredSessionOrLast());
        }
    }

    private void setActivityTheme() {
        TermuxThemeUtils.setAppNightMode(mProperties.getNightMode());
        AppCompatActivityUtils.setNightMode(mActivity, NightMode.getAppNightMode().getName(), true);
    }

    private void setMargins() {
        RelativeLayout relativeLayout = mActivity.findViewById(R.id.activity_termux_root_relative_layout);
        int marginHorizontal = mProperties.getTerminalMarginHorizontal();
        int marginVertical = mProperties.getTerminalMarginVertical();
        ViewUtils.setLayoutMarginsInDp(relativeLayout, marginHorizontal, marginVertical, marginHorizontal, marginVertical);
    }

    private void setTermuxTerminalViewAndClients() {
        mTermuxTerminalSessionActivityClient = new TermuxTerminalSessionActivityClient(mActivity);
        mTermuxTerminalViewClient = new TermuxTerminalViewClient(mActivity, mTermuxTerminalSessionActivityClient);

        mTerminalView = mActivity.findViewById(R.id.terminal_view);
        mTerminalView.setTerminalViewClient(mTermuxTerminalViewClient);

        if (mTermuxTerminalViewClient != null) {
            mTermuxTerminalViewClient.onCreate();
        }
        if (mTermuxTerminalSessionActivityClient != null) {
            mTermuxTerminalSessionActivityClient.onCreate();
        }
    }

    private void setTermuxSessionsListView() {
        ListView termuxSessionsListView = mActivity.findViewById(R.id.terminal_sessions_list);
        mTermuxSessionListViewController = new TermuxSessionsListViewController(mActivity, mTermuxService.getTermuxSessions());
        termuxSessionsListView.setAdapter(mTermuxSessionListViewController);
        termuxSessionsListView.setOnItemClickListener(mTermuxSessionListViewController);
        termuxSessionsListView.setOnItemLongClickListener(mTermuxSessionListViewController);
    }

    private void setTerminalToolbarView(Bundle savedInstanceState) {
        mTermuxTerminalExtraKeys = new TermuxTerminalExtraKeys(mActivity, mTerminalView,
            mTermuxTerminalViewClient, mTermuxTerminalSessionActivityClient);

        final ViewPager terminalToolbarViewPager = getTerminalToolbarViewPager();
        if (mPreferences.shouldShowTerminalToolbar()) terminalToolbarViewPager.setVisibility(View.VISIBLE);

        ViewGroup.LayoutParams layoutParams = terminalToolbarViewPager.getLayoutParams();
        mTerminalToolbarDefaultHeight = layoutParams.height;

        setTerminalToolbarHeight();

        String savedTextInput = null;
        if (savedInstanceState != null) {
            savedTextInput = savedInstanceState.getString(ARG_TERMINAL_TOOLBAR_TEXT_INPUT);
        }

        terminalToolbarViewPager.setAdapter(new TerminalToolbarViewPager.PageAdapter(mActivity, savedTextInput));
        terminalToolbarViewPager.addOnPageChangeListener(
            new TerminalToolbarViewPager.OnPageChangeListener(mActivity, terminalToolbarViewPager));
    }

    private void setTerminalToolbarHeight() {
        final ViewPager terminalToolbarViewPager = getTerminalToolbarViewPager();
        if (terminalToolbarViewPager == null) return;

        ViewGroup.LayoutParams layoutParams = terminalToolbarViewPager.getLayoutParams();
        layoutParams.height = Math.round(mTerminalToolbarDefaultHeight *
            (mTermuxTerminalExtraKeys.getExtraKeysInfo() == null ? 0 : mTermuxTerminalExtraKeys.getExtraKeysInfo().getMatrix().length) *
            mProperties.getTerminalToolbarHeightScaleFactor());
        terminalToolbarViewPager.setLayoutParams(layoutParams);
    }

    private void setSettingsButtonView() {
        ImageButton settingsButton = mActivity.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(v -> ActivityUtils.startActivity(mActivity, new Intent(mActivity, SettingsActivity.class)));
    }

    private void setNewSessionButtonView() {
        View newSessionButton = mActivity.findViewById(R.id.new_session_button);
        newSessionButton.setOnClickListener(v -> mTermuxTerminalSessionActivityClient.addNewSession(false, null));
        newSessionButton.setOnLongClickListener(v -> {
            TextInputDialogUtils.textInput(mActivity, R.string.title_create_named_session, null,
                R.string.action_create_named_session_confirm, text -> mTermuxTerminalSessionActivityClient.addNewSession(false, text),
                R.string.action_new_session_failsafe, text -> mTermuxTerminalSessionActivityClient.addNewSession(true, text),
                -1, null, null);
            return true;
        });
    }

    private void setToggleKeyboardView() {
        mActivity.findViewById(R.id.toggle_keyboard_button).setOnClickListener(v -> {
            mTermuxTerminalViewClient.onToggleSoftKeyboardRequest();
            getDrawer().closeDrawers();
        });

        mActivity.findViewById(R.id.toggle_keyboard_button).setOnLongClickListener(v -> {
            toggleTerminalToolbar();
            return true;
        });
    }

    private void saveTerminalToolbarTextInput(Bundle savedInstanceState) {
        final EditText textInputView = mActivity.findViewById(R.id.terminal_toolbar_text_input);
        if (textInputView != null) {
            String textInput = textInputView.getText().toString();
            if (!textInput.isEmpty()) savedInstanceState.putString(ARG_TERMINAL_TOOLBAR_TEXT_INPUT, textInput);
        }
    }

    private boolean handleContextMenuSelection(int itemId, @Nullable TerminalSession session) {
        switch (itemId) {
            case CONTEXT_MENU_SELECT_URL_ID:
                mTermuxTerminalViewClient.showUrlSelection();
                return true;
            case CONTEXT_MENU_SHARE_TRANSCRIPT_ID:
                mTermuxTerminalViewClient.shareSessionTranscript();
                return true;
            case CONTEXT_MENU_SHARE_SELECTED_TEXT:
                mTermuxTerminalViewClient.shareSelectedText();
                return true;
            case CONTEXT_MENU_AUTOFILL_USERNAME:
                mTerminalView.requestAutoFillUsername();
                return true;
            case CONTEXT_MENU_AUTOFILL_PASSWORD:
                mTerminalView.requestAutoFillPassword();
                return true;
            case CONTEXT_MENU_RESET_TERMINAL_ID:
                onResetTerminalSession(session);
                return true;
            case CONTEXT_MENU_KILL_PROCESS_ID:
                showKillSessionDialog(session);
                return true;
            case CONTEXT_MENU_STYLING_ID:
                showStylingDialog();
                return true;
            case CONTEXT_MENU_TOGGLE_KEEP_SCREEN_ON:
                toggleKeepScreenOn();
                return true;
            case CONTEXT_MENU_HELP_ID:
                ActivityUtils.startActivity(mActivity, new Intent(mActivity, HelpActivity.class));
                return true;
            case CONTEXT_MENU_SETTINGS_ID:
                ActivityUtils.startActivity(mActivity, new Intent(mActivity, SettingsActivity.class));
                return true;
            case CONTEXT_MENU_REPORT_ID:
                mTermuxTerminalViewClient.reportIssueFromTranscript();
                return true;
            default:
                return false;
        }
    }

    private void showKillSessionDialog(TerminalSession session) {
        if (session == null) return;

        final AlertDialog.Builder b = new AlertDialog.Builder(mActivity);
        b.setIcon(android.R.drawable.ic_dialog_alert);
        b.setMessage(R.string.title_confirm_kill_process);
        b.setPositiveButton(android.R.string.yes, (dialog, id) -> {
            dialog.dismiss();
            session.finishIfRunning();
        });
        b.setNegativeButton(android.R.string.no, null);
        b.show();
    }

    private void onResetTerminalSession(TerminalSession session) {
        if (session != null) {
            session.reset();
            showToast(mActivity.getResources().getString(R.string.msg_terminal_reset), true);

            if (mTermuxTerminalSessionActivityClient != null) {
                mTermuxTerminalSessionActivityClient.onResetTerminalSession();
            }
        }
    }

    private void showStylingDialog() {
        Intent stylingIntent = new Intent();
        stylingIntent.setClassName(TermuxConstants.TERMUX_STYLING_PACKAGE_NAME,
            TermuxConstants.TERMUX_STYLING_APP.TERMUX_STYLING_ACTIVITY_NAME);
        try {
            mActivity.startActivity(stylingIntent);
        } catch (ActivityNotFoundException | IllegalArgumentException e) {
            new AlertDialog.Builder(mActivity).setMessage(mActivity.getString(R.string.error_styling_not_installed))
                .setPositiveButton(R.string.action_styling_install,
                    (dialog, which) -> ActivityUtils.startActivity(mActivity,
                        new Intent(Intent.ACTION_VIEW, Uri.parse(TermuxConstants.TERMUX_STYLING_FDROID_PACKAGE_URL))))
                .setNegativeButton(android.R.string.cancel, null).show();
        }
    }

    private void toggleKeepScreenOn() {
        if (mTerminalView.getKeepScreenOn()) {
            mTerminalView.setKeepScreenOn(false);
            mPreferences.setKeepScreenOn(false);
        } else {
            mTerminalView.setKeepScreenOn(true);
            mPreferences.setKeepScreenOn(true);
        }
    }

    private void registerTermuxActivityBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TERMUX_ACTIVITY.ACTION_NOTIFY_APP_CRASH);
        intentFilter.addAction(TERMUX_ACTIVITY.ACTION_RELOAD_STYLE);
        intentFilter.addAction(TERMUX_ACTIVITY.ACTION_REQUEST_PERMISSIONS);

        mActivity.registerReceiver(mTermuxActivityBroadcastReceiver, intentFilter);
    }

    private void unregisterTermuxActivityBroadcastReceiver() {
        mActivity.unregisterReceiver(mTermuxActivityBroadcastReceiver);
    }

    private void fixTermuxActivityBroadcastReceiverIntent(Intent intent) {
        if (intent == null) return;

        String extraReloadStyle = intent.getStringExtra(TERMUX_ACTIVITY.EXTRA_RELOAD_STYLE);
        if ("storage".equals(extraReloadStyle)) {
            intent.removeExtra(TERMUX_ACTIVITY.EXTRA_RELOAD_STYLE);
            intent.setAction(TERMUX_ACTIVITY.ACTION_REQUEST_PERMISSIONS);
        }
    }

    private class TermuxActivityBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || !mIsVisible) return;

            fixTermuxActivityBroadcastReceiverIntent(intent);

            switch (intent.getAction()) {
                case TERMUX_ACTIVITY.ACTION_NOTIFY_APP_CRASH:
                    Logger.logDebug(LOG_TAG, "Received intent to notify app crash");
                    TermuxCrashUtils.notifyAppCrashFromCrashLogFile(context, LOG_TAG);
                    return;
                case TERMUX_ACTIVITY.ACTION_RELOAD_STYLE:
                    Logger.logDebug(LOG_TAG, "Received intent to reload styling");
                    reloadActivityStyling(intent.getBooleanExtra(TERMUX_ACTIVITY.EXTRA_RECREATE_ACTIVITY, true));
                    return;
                case TERMUX_ACTIVITY.ACTION_REQUEST_PERMISSIONS:
                    Logger.logDebug(LOG_TAG, "Received intent to request storage permissions");
                    requestStoragePermission(false);
                    return;
                default:
            }
        }
    }
}
