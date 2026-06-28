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

    private final TermuxActivity mActivity;
    private final BroadcastReceiver mTermuxActivityBroadcastReceiver = new TermuxActivityBroadcastReceiver();

    private TermuxService mTermuxService;
    private TerminalView mTerminalView;
    private TermuxTerminalViewClient mTermuxTerminalViewClient;
    private TermuxTerminalSessionActivityClient mTermuxTerminalSessionActivityClient;
    private TermuxAppSharedPreferences mPreferences;
    private TermuxAppSharedProperties mProperties;
    private TermuxActivityRootView mTermuxActivityRootView;
    private View mTermuxActivityBottomSpaceView;
    private ExtraKeysView mExtraKeysView;
    private TermuxTerminalExtraKeys mTermuxTerminalExtraKeys;
    private TermuxSessionsListViewController mTermuxSessionListViewController;
    private Toast mLastToast;
    private boolean mIsVisible;
    private boolean mIsOnResumeAfterOnCreate = true;
    private boolean mIsActivityRecreated;
    private boolean mIsInvalidState;
    private int mNavBarHeight;
    private float mTerminalToolbarDefaultHeight;

    TermuxActivityController(TermuxActivity activity) {
        mActivity = activity;
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

        initializeViews(savedInstanceState);
        startAndBindService();

        if (mIsInvalidState) return;

        TermuxUtils.sendTermuxOpenedBroadcast(mActivity);
    }

    private void initializeViews(@Nullable Bundle savedInstanceState) {
        setMargins();

        mTermuxActivityRootView = mActivity.findViewById(R.id.activity_termux_root_view);
        mTermuxActivityRootView.setActivity(mActivity);
        mTermuxActivityBottomSpaceView = mActivity.findViewById(R.id.activity_termux_bottom_space_view);
        mTermuxActivityRootView.setOnApplyWindowInsetsListener(new TermuxActivityRootView.WindowInsetsListener());

        View content = mActivity.findViewById(android.R.id.content);
        content.setOnApplyWindowInsetsListener((v, insets) -> {
            mNavBarHeight = insets.getSystemWindowInsetBottom();
            return insets;
        });

        if (mProperties.isUsingFullScreen()) {
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setTermuxTerminalViewAndClients();
        setTerminalToolbarView(savedInstanceState);
        setSettingsButtonView();
        setNewSessionButtonView();
        setToggleKeyboardView();

        mActivity.registerForContextMenu(mTerminalView);
        FileReceiverActivity.updateFileReceiverActivityComponentsState(mActivity);
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
        if (mPreferences.isTerminalMarginAdjustmentEnabled()) {
            addTermuxActivityRootViewGlobalLayoutListener();
        }

        registerTermuxActivityBroadcastReceiver();
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

        removeTermuxActivityRootViewGlobalLayoutListener();
        unregisterTermuxActivityBroadcastReceiver();
        getDrawer().closeDrawers();
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
        saveTerminalToolbarTextInput(savedInstanceState);
        savedInstanceState.putBoolean(ARG_ACTIVITY_RECREATED, true);
    }

    void onServiceConnected(ComponentName componentName, IBinder service) {
        mTermuxService = ((TermuxService.LocalBinder) service).service;
        setTermuxSessionsListView();
        handleServiceConnectedIntent(mActivity.getIntent());
        mTermuxService.setTermuxTerminalSessionClient(mTermuxTerminalSessionActivityClient);
    }

    void onServiceDisconnected(ComponentName name) {
        finishActivityIfNotFinishing();
    }

    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Logger.logVerbose(LOG_TAG, "onActivityResult: requestCode: " + requestCode + ", resultCode: " + resultCode +
            ", data: " + IntentUtils.getIntentString(data));
        if (requestCode == PermissionUtils.REQUEST_GRANT_STORAGE_PERMISSION) {
            requestStoragePermission(true);
        }
    }

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Logger.logVerbose(LOG_TAG, "onRequestPermissionsResult: requestCode: " + requestCode + ", permissions: " +
            Arrays.toString(permissions) + ", grantResults: " + Arrays.toString(grantResults));
        if (requestCode == PermissionUtils.REQUEST_GRANT_STORAGE_PERMISSION) {
            requestStoragePermission(true);
        }
    }

    void onBackPressed() {
        if (getDrawer().isDrawerOpen(Gravity.LEFT)) {
            getDrawer().closeDrawers();
        } else {
            finishActivityIfNotFinishing();
        }
    }

    void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        TerminalSession currentSession = getCurrentSession();
        if (currentSession == null) return;

        boolean autoFillEnabled = mTerminalView.isAutoFillEnabled();

        menu.add(Menu.NONE, CONTEXT_MENU_SELECT_URL_ID, Menu.NONE, R.string.action_select_url);
        menu.add(Menu.NONE, CONTEXT_MENU_SHARE_TRANSCRIPT_ID, Menu.NONE, R.string.action_share_transcript);
        if (!DataUtils.isNullOrEmpty(mTerminalView.getStoredSelectedText())) {
            menu.add(Menu.NONE, CONTEXT_MENU_SHARE_SELECTED_TEXT, Menu.NONE, R.string.action_share_selected_text);
        }
        if (autoFillEnabled) {
            menu.add(Menu.NONE, CONTEXT_MENU_AUTOFILL_USERNAME, Menu.NONE, R.string.action_autofill_username);
            menu.add(Menu.NONE, CONTEXT_MENU_AUTOFILL_PASSWORD, Menu.NONE, R.string.action_autofill_password);
        }
        menu.add(Menu.NONE, CONTEXT_MENU_RESET_TERMINAL_ID, Menu.NONE, R.string.action_reset_terminal);
        menu.add(Menu.NONE, CONTEXT_MENU_KILL_PROCESS_ID, Menu.NONE,
            mActivity.getResources().getString(R.string.action_kill_process, getCurrentSession().getPid())).setEnabled(currentSession.isRunning());
        menu.add(Menu.NONE, CONTEXT_MENU_STYLING_ID, Menu.NONE, R.string.action_style_terminal);
        menu.add(Menu.NONE, CONTEXT_MENU_TOGGLE_KEEP_SCREEN_ON, Menu.NONE, R.string.action_toggle_keep_screen_on)
            .setCheckable(true).setChecked(mPreferences.shouldKeepScreenOn());
        menu.add(Menu.NONE, CONTEXT_MENU_HELP_ID, Menu.NONE, R.string.action_open_help);
        menu.add(Menu.NONE, CONTEXT_MENU_SETTINGS_ID, Menu.NONE, R.string.action_open_settings);
        menu.add(Menu.NONE, CONTEXT_MENU_REPORT_ID, Menu.NONE, R.string.action_report_issue);
    }

    boolean onCreateOptionsMenu(Menu menu) {
        mTerminalView.showContextMenu();
        return false;
    }

    boolean onContextItemSelected(MenuItem item) {
        TerminalSession session = getCurrentSession();
        if (handleContextMenuSelection(item.getItemId(), session)) return true;
        return false;
    }

    void onContextMenuClosed(Menu menu) {
        mTerminalView.onContextMenuClosed(menu);
    }

    void requestStoragePermission(boolean isPermissionCallback) {
        new Thread() {
            @Override
            public void run() {
                int requestCode = isPermissionCallback ? -1 : PermissionUtils.REQUEST_GRANT_STORAGE_PERMISSION;

                if (PermissionUtils.checkAndRequestLegacyOrManageExternalStoragePermission(
                    mActivity, requestCode, !isPermissionCallback)) {
                    if (isPermissionCallback) {
                        Logger.logInfoAndShowToast(mActivity, LOG_TAG,
                            mActivity.getString(com.termux.shared.R.string.msg_storage_permission_granted_on_request));
                    }

                    TermuxInstaller.setupStorageSymlinks(mActivity);
                } else if (isPermissionCallback) {
                    Logger.logInfoAndShowToast(mActivity, LOG_TAG,
                        mActivity.getString(com.termux.shared.R.string.msg_storage_permission_not_granted_on_request));
                }
            }
        }.start();
    }

    void reloadActivityStyling(boolean recreateActivity) {
        if (mProperties != null) {
            reloadProperties();

            if (mExtraKeysView != null) {
                mExtraKeysView.setButtonTextAllCaps(mProperties.shouldExtraKeysTextBeAllCaps());
                mExtraKeysView.reload(mTermuxTerminalExtraKeys.getExtraKeysInfo(), mTerminalToolbarDefaultHeight);
            }

            TermuxThemeUtils.setAppNightMode(mProperties.getNightMode());
        }

        setMargins();
        setTerminalToolbarHeight();

        FileReceiverActivity.updateFileReceiverActivityComponentsState(mActivity);

        if (mTermuxTerminalSessionActivityClient != null) {
            mTermuxTerminalSessionActivityClient.onReloadActivityStyling();
        }
        if (mTermuxTerminalViewClient != null) {
            mTermuxTerminalViewClient.onReloadActivityStyling();
        }

        if (recreateActivity) {
            Logger.logDebug(LOG_TAG, "Recreating activity");
            mActivity.recreate();
        }
    }

    void reloadProperties() {
        mProperties.loadTermuxPropertiesFromDisk();

        if (mTermuxTerminalViewClient != null) {
            mTermuxTerminalViewClient.onReloadProperties();
        }
    }

    void toggleTerminalToolbar() {
        final ViewPager terminalToolbarViewPager = getTerminalToolbarViewPager();
        if (terminalToolbarViewPager == null) return;

        final boolean showNow = mPreferences.toogleShowTerminalToolbar();
        Logger.showToast(mActivity, (showNow ? mActivity.getString(R.string.msg_enabling_terminal_toolbar) :
            mActivity.getString(R.string.msg_disabling_terminal_toolbar)), true);
        terminalToolbarViewPager.setVisibility(showNow ? View.VISIBLE : View.GONE);
        if (showNow && isTerminalToolbarTextInputViewSelected()) {
            mActivity.findViewById(R.id.terminal_toolbar_text_input).requestFocus();
        }
    }

    void showToast(String text, boolean longDuration) {
        if (text == null || text.isEmpty()) return;
        if (mLastToast != null) mLastToast.cancel();
        mLastToast = Toast.makeText(mActivity, text, longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        mLastToast.setGravity(Gravity.TOP, 0, 0);
        mLastToast.show();
    }

    void finishActivityIfNotFinishing() {
        if (!mActivity.isFinishing()) {
            mActivity.finish();
        }
    }

    void addTermuxActivityRootViewGlobalLayoutListener() {
        getTermuxActivityRootView().getViewTreeObserver().addOnGlobalLayoutListener(getTermuxActivityRootView());
    }

    void removeTermuxActivityRootViewGlobalLayoutListener() {
        if (getTermuxActivityRootView() != null) {
            getTermuxActivityRootView().getViewTreeObserver().removeOnGlobalLayoutListener(getTermuxActivityRootView());
        }
    }

    void setExtraKeysView(ExtraKeysView extraKeysView) {
        mExtraKeysView = extraKeysView;
    }

    @Nullable
    TerminalSession getCurrentSession() {
        if (mTerminalView != null) {
            return mTerminalView.getCurrentSession();
        }
        return null;
    }

    int getNavBarHeight() {
        return mNavBarHeight;
    }

    TermuxActivityRootView getTermuxActivityRootView() {
        return mTermuxActivityRootView;
    }

    View getTermuxActivityBottomSpaceView() {
        return mTermuxActivityBottomSpaceView;
    }

    ExtraKeysView getExtraKeysView() {
        return mExtraKeysView;
    }

    TermuxTerminalExtraKeys getTermuxTerminalExtraKeys() {
        return mTermuxTerminalExtraKeys;
    }

    DrawerLayout getDrawer() {
        return (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
    }

    ViewPager getTerminalToolbarViewPager() {
        return (ViewPager) mActivity.findViewById(R.id.terminal_toolbar_view_pager);
    }

    float getTerminalToolbarDefaultHeight() {
        return mTerminalToolbarDefaultHeight;
    }

    boolean isTerminalViewSelected() {
        return getTerminalToolbarViewPager().getCurrentItem() == 0;
    }

    boolean isTerminalToolbarTextInputViewSelected() {
        return getTerminalToolbarViewPager().getCurrentItem() == 1;
    }

    void termuxSessionListNotifyUpdated() {
        if (mTermuxSessionListViewController != null) {
            mTermuxSessionListViewController.notifyDataSetChanged();
        }
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
