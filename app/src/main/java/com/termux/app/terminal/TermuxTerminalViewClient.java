package com.termux.app.terminal;

import android.view.KeyEvent;
import android.view.MotionEvent;

import com.termux.app.TermuxActivity;
import com.termux.shared.termux.terminal.TermuxTerminalViewClientBase;
import com.termux.shared.termux.extrakeys.SpecialButton;
import com.termux.terminal.TerminalSession;

import androidx.drawerlayout.widget.DrawerLayout;

public class TermuxTerminalViewClient extends TermuxTerminalViewClientBase {

    private final TermuxTerminalViewClientHost mHost;
    private final TermuxTerminalViewInteractionHelper mInteractionHelper;
    private final TermuxTerminalViewInputController mInputController;

    public TermuxTerminalViewClient(TermuxTerminalViewClientHost host, TermuxTerminalSessionActivityClient termuxTerminalSessionActivityClient) {
        this.mHost = host;
        this.mInteractionHelper = new TermuxTerminalViewInteractionHelper(host, termuxTerminalSessionActivityClient);
        this.mInputController = new TermuxTerminalViewInputController(host, termuxTerminalSessionActivityClient, mInteractionHelper);
    }

    public TermuxTerminalViewClientHost getHost() {
        return mHost;
    }

    public TermuxActivity getActivity() {
        return (TermuxActivity) mHost.getActivity();
    }

    /**
     * Should be called when mActivity.onCreate() is called
     */
    public void onCreate() {
        mInteractionHelper.onCreate();
    }

    /**
     * Should be called when mActivity.onStart() is called
     */
    public void onStart() {
        mInteractionHelper.onStart();
    }

    /**
     * Should be called when mActivity.onResume() is called
     */
    public void onResume() {
        mInteractionHelper.onResume();
    }

    /**
     * Should be called when mActivity.onStop() is called
     */
    public void onStop() {
        mInteractionHelper.onStop();
    }

    /**
     * Should be called when mActivity.reloadProperties() is called
     */
    public void onReloadProperties() {
        mInteractionHelper.onReloadProperties();
    }

    /**
     * Should be called when mActivity.reloadActivityStyling() is called
     */
    public void onReloadActivityStyling() {
        mInteractionHelper.onReloadActivityStyling();
    }

    /**
     * Should be called when {@link com.termux.view.TerminalView#mEmulator} is set
     */
    @Override
    public void onEmulatorSet() {
        mInteractionHelper.onEmulatorSet();
    }



    @Override
    public float onScale(float scale) {
        if (scale < 0.9f || scale > 1.1f) {
            boolean increase = scale > 1.f;
            mInteractionHelper.changeFontSize(increase);
            return 1.0f;
        }
        return scale;
    }



    @Override
    public void onSingleTapUp(MotionEvent e) {
        mInteractionHelper.onSingleTapUp(e);
    }

    @Override
    public boolean shouldBackButtonBeMappedToEscape() {
        return mHost.getProperties().isBackKeyTheEscapeKey();
    }

    @Override
    public boolean shouldEnforceCharBasedInput() {
        return mHost.getProperties().isEnforcingCharBasedInput();
    }

    @Override
    public boolean shouldUseCtrlSpaceWorkaround() {
        return mHost.getProperties().isUsingCtrlSpaceWorkaround();
    }

    @Override
    public boolean isTerminalViewSelected() {
        return mHost.getTerminalToolbarViewPager() == null || mHost.isTerminalViewSelected() || mHost.getTerminalView().hasFocus();
    }



    @Override
    public void copyModeChanged(boolean copyMode) {
        // Disable drawer while copying.
        mHost.getDrawer().setDrawerLockMode(copyMode ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e, TerminalSession currentSession) {
        return mInputController.onKeyDown(keyCode, e, currentSession);
    }



    @Override
    public boolean onKeyUp(int keyCode, KeyEvent e) {
        return mInputController.onKeyUp(keyCode, e);
    }



    @Override
    public boolean readControlKey() {
        return mInputController.readControlKey();
    }

    @Override
    public boolean readAltKey() {
        return mInputController.readAltKey();
    }

    @Override
    public boolean readShiftKey() {
        return mInputController.readShiftKey();
    }

    @Override
    public boolean readFnKey() {
        return mInputController.readFnKey();
    }

    public boolean readExtraKeysSpecialButton(SpecialButton specialButton) {
        return mInteractionHelper.readExtraKeysSpecialButton(specialButton);
    }

    @Override
    public boolean onLongPress(MotionEvent event) {
        return false;
    }



    @Override
    public boolean onCodePoint(final int codePoint, boolean ctrlDown, TerminalSession session) {
        return mInputController.onCodePoint(codePoint, ctrlDown, session);
    }





    public void changeFontSize(boolean increase) {
        mInteractionHelper.changeFontSize(increase);
    }



    /**
     * Called when user requests the soft keyboard to be toggled via "KEYBOARD" toggle button in
     * drawer or extra keys, or with ctrl+alt+k hardware keyboard shortcut.
     */
    public void onToggleSoftKeyboardRequest() {
        mInteractionHelper.onToggleSoftKeyboardRequest();
    }

    public void setSoftKeyboardState(boolean isStartup, boolean isReloadTermuxProperties) {
        mInteractionHelper.setSoftKeyboardState(isStartup, isReloadTermuxProperties);
    }



    public void setTerminalCursorBlinkerState(boolean start) {
        mInteractionHelper.setTerminalCursorBlinkerState(start);
    }



    public void shareSessionTranscript() {
        mInteractionHelper.shareSessionTranscript();
    }

    public void shareSelectedText() {
        mInteractionHelper.shareSelectedText();
    }

    public void showUrlSelection() {
        mInteractionHelper.showUrlSelection();
    }

    public void reportIssueFromTranscript() {
        mInteractionHelper.reportIssueFromTranscript();
    }

    public void doPaste() {
        mInteractionHelper.doPaste();
    }

}
