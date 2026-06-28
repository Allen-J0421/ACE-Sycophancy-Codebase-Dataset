package com.termux.app.terminal;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;

import com.termux.shared.termux.extrakeys.SpecialButton;
import com.termux.terminal.TerminalSession;

final class TermuxTerminalViewInputController {

    private final TermuxTerminalViewClientHost mHost;
    private final TermuxTerminalSessionActivityClient mTermuxTerminalSessionActivityClient;
    private final TermuxTerminalViewInteractionHelper mInteractionHelper;

    private boolean mVirtualControlKeyDown;
    private boolean mVirtualFnKeyDown;

    TermuxTerminalViewInputController(TermuxTerminalViewClientHost host,
        TermuxTerminalSessionActivityClient termuxTerminalSessionActivityClient,
        TermuxTerminalViewInteractionHelper interactionHelper) {
        mHost = host;
        mTermuxTerminalSessionActivityClient = termuxTerminalSessionActivityClient;
        mInteractionHelper = interactionHelper;
    }

    @SuppressLint("RtlHardcoded")
    boolean onKeyDown(int keyCode, KeyEvent e, TerminalSession currentSession) {
        if (handleVirtualKeys(keyCode, e, true)) return true;

        if (keyCode == KeyEvent.KEYCODE_ENTER && !currentSession.isRunning()) {
            mTermuxTerminalSessionActivityClient.removeFinishedSession(currentSession);
            return true;
        } else if (!mHost.getProperties().areHardwareKeyboardShortcutsDisabled() &&
            e.isCtrlPressed() && e.isAltPressed()) {
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
                mInteractionHelper.onToggleSoftKeyboardRequest();
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
                mInteractionHelper.changeFontSize(true);
            } else if (unicodeChar == '-') {
                mInteractionHelper.changeFontSize(false);
            } else if (unicodeChar >= '1' && unicodeChar <= '9') {
                mTermuxTerminalSessionActivityClient.switchToSession(unicodeChar - '1');
            }
            return true;
        }

        return false;
    }

    boolean onKeyUp(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mHost.getTerminalView().mEmulator == null) {
            mHost.finishActivityIfNotFinishing();
            return true;
        }

        return handleVirtualKeys(keyCode, e, false);
    }

    boolean readControlKey() {
        return mInteractionHelper.readExtraKeysSpecialButton(SpecialButton.CTRL) || mVirtualControlKeyDown;
    }

    boolean readAltKey() {
        return mInteractionHelper.readExtraKeysSpecialButton(SpecialButton.ALT);
    }

    boolean readShiftKey() {
        return mInteractionHelper.readExtraKeysSpecialButton(SpecialButton.SHIFT);
    }

    boolean readFnKey() {
        return mInteractionHelper.readExtraKeysSpecialButton(SpecialButton.FN);
    }

    boolean onCodePoint(final int codePoint, boolean ctrlDown, TerminalSession session) {
        return mInteractionHelper.onCodePoint(codePoint, ctrlDown, mVirtualFnKeyDown, session, () -> mVirtualFnKeyDown = false);
    }

    private boolean handleVirtualKeys(int keyCode, KeyEvent event, boolean down) {
        InputDevice inputDevice = event.getDevice();
        if (mHost.getProperties().areVirtualVolumeKeysDisabled()) {
            return false;
        } else if (inputDevice != null && inputDevice.getKeyboardType() == InputDevice.KEYBOARD_TYPE_ALPHABETIC) {
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
}
