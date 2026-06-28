package com.termux.app.terminal;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.termux.app.TermuxActivity;
import com.termux.shared.termux.terminal.TermuxTerminalViewClientBase;
import com.termux.shared.termux.extrakeys.SpecialButton;
import com.termux.terminal.TerminalSession;

import androidx.drawerlayout.widget.DrawerLayout;

public class TermuxTerminalViewClient extends TermuxTerminalViewClientBase {

    private final TermuxTerminalViewClientHost mHost;
    private final TermuxTerminalSessionActivityClient mTermuxTerminalSessionActivityClient;
    private final TermuxTerminalViewInteractionHelper mInteractionHelper;

    /** Keeping track of the special keys acting as Ctrl and Fn for the soft keyboard and other hardware keys. */
    private boolean mVirtualControlKeyDown, mVirtualFnKeyDown;

    public TermuxTerminalViewClient(TermuxTerminalViewClientHost host, TermuxTerminalSessionActivityClient termuxTerminalSessionActivityClient) {
        this.mHost = host;
        this.mTermuxTerminalSessionActivityClient = termuxTerminalSessionActivityClient;
        this.mInteractionHelper = new TermuxTerminalViewInteractionHelper(host, termuxTerminalSessionActivityClient);
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



    @SuppressLint("RtlHardcoded")
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e, TerminalSession currentSession) {
        if (handleVirtualKeys(keyCode, e, true)) return true;

        if (keyCode == KeyEvent.KEYCODE_ENTER && !currentSession.isRunning()) {
            mTermuxTerminalSessionActivityClient.removeFinishedSession(currentSession);
            return true;
        } else if (!mHost.getProperties().areHardwareKeyboardShortcutsDisabled() &&
            e.isCtrlPressed() && e.isAltPressed()) {
            // Get the unmodified code point:
            int unicodeChar = e.getUnicodeChar(0);

            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || unicodeChar == 'n'/* next */) {
                mTermuxTerminalSessionActivityClient.switchToSession(true);
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP || unicodeChar == 'p' /* previous */) {
                mTermuxTerminalSessionActivityClient.switchToSession(false);
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                mHost.getDrawer().openDrawer(Gravity.LEFT);
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                mHost.getDrawer().closeDrawers();
            } else if (unicodeChar == 'k'/* keyboard */) {
                onToggleSoftKeyboardRequest();
            } else if (unicodeChar == 'm'/* menu */) {
                mHost.getTerminalView().showContextMenu();
            } else if (unicodeChar == 'r'/* rename */) {
                mTermuxTerminalSessionActivityClient.renameSession(currentSession);
            } else if (unicodeChar == 'c'/* create */) {
                mTermuxTerminalSessionActivityClient.addNewSession(false, null);
            } else if (unicodeChar == 'u' /* urls */) {
                mInteractionHelper.showUrlSelection();
            } else if (unicodeChar == 'v') {
                mInteractionHelper.doPaste();
            } else if (unicodeChar == '+' || e.getUnicodeChar(KeyEvent.META_SHIFT_ON) == '+') {
                // We also check for the shifted char here since shift may be required to produce '+',
                // see https://github.com/termux/termux-api/issues/2
                mInteractionHelper.changeFontSize(true);
            } else if (unicodeChar == '-') {
                mInteractionHelper.changeFontSize(false);
            } else if (unicodeChar >= '1' && unicodeChar <= '9') {
                int index = unicodeChar - '1';
                mTermuxTerminalSessionActivityClient.switchToSession(index);
            }
            return true;
        }

        return false;

    }



    @Override
    public boolean onKeyUp(int keyCode, KeyEvent e) {
        // If emulator is not set, like if bootstrap installation failed and user dismissed the error
        // dialog, then just exit the activity, otherwise they will be stuck in a broken state.
        if (keyCode == KeyEvent.KEYCODE_BACK && mHost.getTerminalView().mEmulator == null) {
            mHost.finishActivityIfNotFinishing();
            return true;
        }

        return handleVirtualKeys(keyCode, e, false);
    }

    /** Handle dedicated volume buttons as virtual keys if applicable. */
    private boolean handleVirtualKeys(int keyCode, KeyEvent event, boolean down) {
        InputDevice inputDevice = event.getDevice();
        if (mHost.getProperties().areVirtualVolumeKeysDisabled()) {
            return false;
        } else if (inputDevice != null && inputDevice.getKeyboardType() == InputDevice.KEYBOARD_TYPE_ALPHABETIC) {
            // Do not steal dedicated buttons from a full external keyboard.
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            mVirtualControlKeyDown = down;
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            mVirtualFnKeyDown = down;
            return true;
        }
        return false;
    }



    @Override
    public boolean readControlKey() {
        return mInteractionHelper.readExtraKeysSpecialButton(SpecialButton.CTRL) || mVirtualControlKeyDown;
    }

    @Override
    public boolean readAltKey() {
        return mInteractionHelper.readExtraKeysSpecialButton(SpecialButton.ALT);
    }

    @Override
    public boolean readShiftKey() {
        return mInteractionHelper.readExtraKeysSpecialButton(SpecialButton.SHIFT);
    }

    @Override
    public boolean readFnKey() {
        return mInteractionHelper.readExtraKeysSpecialButton(SpecialButton.FN);
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
        return mInteractionHelper.onCodePoint(codePoint, ctrlDown, mVirtualFnKeyDown, session, () -> mVirtualFnKeyDown = false);
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
