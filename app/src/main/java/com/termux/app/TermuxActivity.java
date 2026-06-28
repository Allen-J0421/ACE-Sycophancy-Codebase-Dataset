package com.termux.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.termux.app.terminal.TermuxActivityRootView;
import com.termux.app.terminal.TermuxTerminalSessionActivityClient;
import com.termux.app.terminal.TermuxTerminalViewClient;
import com.termux.app.terminal.TermuxTerminalViewClientHost;
import com.termux.app.terminal.io.TermuxTerminalExtraKeys;
import com.termux.shared.activity.ActivityUtils;
import com.termux.shared.logger.Logger;
import com.termux.shared.termux.TermuxConstants.TERMUX_APP.TERMUX_ACTIVITY;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;
import com.termux.shared.termux.settings.properties.TermuxAppSharedProperties;
import com.termux.shared.termux.extrakeys.ExtraKeysView;
import com.termux.terminal.TerminalSession;
import com.termux.view.TerminalView;

public final class TermuxActivity extends AppCompatActivity implements android.content.ServiceConnection, TermuxTerminalViewClientHost {

    private static final String LOG_TAG = "TermuxActivity";

    private final TermuxActivityController mController = new TermuxActivityController(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.logDebug(LOG_TAG, "onCreate");
        mController.initializeActivityState(savedInstanceState);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termux);

        mController.initializeActivityContent(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.logDebug(LOG_TAG, "onStart");
        mController.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.logVerbose(LOG_TAG, "onResume");
        mController.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.logDebug(LOG_TAG, "onStop");
        mController.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.logDebug(LOG_TAG, "onDestroy");
        mController.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        Logger.logVerbose(LOG_TAG, "onSaveInstanceState");
        super.onSaveInstanceState(savedInstanceState);
        mController.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onServiceConnected(android.content.ComponentName componentName, IBinder service) {
        Logger.logDebug(LOG_TAG, "onServiceConnected");
        mController.onServiceConnected(componentName, service);
    }

    @Override
    public void onServiceDisconnected(android.content.ComponentName name) {
        Logger.logDebug(LOG_TAG, "onServiceDisconnected");
        mController.onServiceDisconnected(name);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mController.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mController.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        mController.onBackPressed();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, android.view.View v, ContextMenu.ContextMenuInfo menuInfo) {
        mController.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mController.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (mController.onContextItemSelected(item)) return true;
        return super.onContextItemSelected(item);
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
        mController.onContextMenuClosed(menu);
    }

    public void addTermuxActivityRootViewGlobalLayoutListener() {
        mController.addTermuxActivityRootViewGlobalLayoutListener();
    }

    public void removeTermuxActivityRootViewGlobalLayoutListener() {
        mController.removeTermuxActivityRootViewGlobalLayoutListener();
    }

    public void toggleTerminalToolbar() {
        mController.toggleTerminalToolbar();
    }

    public void finishActivityIfNotFinishing() {
        mController.finishActivityIfNotFinishing();
    }

    public void showToast(String text, boolean longDuration) {
        mController.showToast(text, longDuration);
    }

    public void requestStoragePermission(boolean isPermissionCallback) {
        mController.requestStoragePermission(isPermissionCallback);
    }

    public void reloadActivityStyling(boolean recreateActivity) {
        mController.reloadActivityStyling(recreateActivity);
    }

    public int getNavBarHeight() {
        return mController.getNavBarHeight();
    }

    public TermuxActivityRootView getTermuxActivityRootView() {
        return mController.getTermuxActivityRootView();
    }

    public android.view.View getTermuxActivityBottomSpaceView() {
        return mController.getTermuxActivityBottomSpaceView();
    }

    public ExtraKeysView getExtraKeysView() {
        return mController.getExtraKeysView();
    }

    public TermuxTerminalExtraKeys getTermuxTerminalExtraKeys() {
        return mController.getTermuxTerminalExtraKeys();
    }

    public void setExtraKeysView(ExtraKeysView extraKeysView) {
        mController.setExtraKeysView(extraKeysView);
    }

    public DrawerLayout getDrawer() {
        return mController.getDrawer();
    }

    public ViewPager getTerminalToolbarViewPager() {
        return mController.getTerminalToolbarViewPager();
    }

    public float getTerminalToolbarDefaultHeight() {
        return mController.getTerminalToolbarDefaultHeight();
    }

    public boolean isTerminalViewSelected() {
        return mController.isTerminalViewSelected();
    }

    public boolean isTerminalToolbarTextInputViewSelected() {
        return mController.isTerminalToolbarTextInputViewSelected();
    }

    public void termuxSessionListNotifyUpdated() {
        mController.termuxSessionListNotifyUpdated();
    }

    public boolean isVisible() {
        return mController.isVisible();
    }

    public boolean isOnResumeAfterOnCreate() {
        return mController.isOnResumeAfterOnCreate();
    }

    public boolean isActivityRecreated() {
        return mController.isActivityRecreated();
    }

    public TermuxService getTermuxService() {
        return mController.getTermuxService();
    }

    public TerminalView getTerminalView() {
        return mController.getTerminalView();
    }

    public TermuxTerminalViewClient getTermuxTerminalViewClient() {
        return mController.getTermuxTerminalViewClient();
    }

    public TermuxTerminalSessionActivityClient getTermuxTerminalSessionClient() {
        return mController.getTermuxTerminalSessionClient();
    }

    @Nullable
    public TerminalSession getCurrentSession() {
        return mController.getCurrentSession();
    }

    public TermuxAppSharedPreferences getPreferences() {
        return mController.getPreferences();
    }

    public TermuxAppSharedProperties getProperties() {
        return mController.getProperties();
    }

    public static void updateTermuxActivityStyling(Context context, boolean recreateActivity) {
        Intent stylingIntent = new Intent(TERMUX_ACTIVITY.ACTION_RELOAD_STYLE);
        stylingIntent.putExtra(TERMUX_ACTIVITY.EXTRA_RECREATE_ACTIVITY, recreateActivity);
        context.sendBroadcast(stylingIntent);
    }

    public static void startTermuxActivity(@NonNull final Context context) {
        ActivityUtils.startActivity(context, newInstance(context));
    }

    public static Intent newInstance(@NonNull final Context context) {
        Intent intent = new Intent(context, TermuxActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    public android.app.Activity getActivity() {
        return this;
    }
}
