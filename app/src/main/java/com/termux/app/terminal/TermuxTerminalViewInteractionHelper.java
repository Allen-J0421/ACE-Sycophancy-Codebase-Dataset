package com.termux.app.terminal;

import android.content.Context;
import android.media.AudioManager;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.termux.R;
import com.termux.app.terminal.io.KeyboardShortcut;
import com.termux.shared.logger.Logger;
import com.termux.shared.termux.extrakeys.SpecialButton;
import com.termux.shared.termux.settings.properties.TermuxPropertyConstants;
import com.termux.shared.termux.data.TermuxUrlUtils;
import com.termux.shared.view.KeyboardUtils;
import com.termux.shared.interact.ShareUtils;
import com.termux.terminal.TerminalSession;
import com.termux.terminal.TerminalEmulator;

import java.util.LinkedHashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class TermuxTerminalViewInteractionHelper {

    private static final String LOG_TAG = "TermuxTerminalViewClient";

    private final TermuxTerminalViewClientHost mHost;
    private final TermuxTerminalSessionActivityClient mTermuxTerminalSessionActivityClient;
    private final TermuxTerminalViewActionHelper mActionHelper;

    private Runnable mShowSoftKeyboardRunnable;
    private boolean mShowSoftKeyboardIgnoreOnce;
    private boolean mShowSoftKeyboardWithDelayOnce;
    private boolean mTerminalCursorBlinkerStateAlreadySet;
    private List<KeyboardShortcut> mSessionShortcuts;

    TermuxTerminalViewInteractionHelper(TermuxTerminalViewClientHost host,
        TermuxTerminalSessionActivityClient termuxTerminalSessionActivityClient) {
        mHost = host;
        mTermuxTerminalSessionActivityClient = termuxTerminalSessionActivityClient;
        mActionHelper = new TermuxTerminalViewActionHelper(host);
    }

    void onCreate() {
        onReloadProperties();
        mHost.getTerminalView().setTextSize(mHost.getPreferences().getFontSize());
        mHost.getTerminalView().setKeepScreenOn(mHost.getPreferences().shouldKeepScreenOn());
    }

    void onStart() {
        boolean isTerminalViewKeyLoggingEnabled = mHost.getPreferences().isTerminalViewKeyLoggingEnabled();
        mHost.getTerminalView().setIsTerminalViewKeyLoggingEnabled(isTerminalViewKeyLoggingEnabled);
        mHost.getTermuxActivityRootView().setIsRootViewLoggingEnabled(isTerminalViewKeyLoggingEnabled);
        com.termux.shared.view.ViewUtils.setIsViewUtilsLoggingEnabled(isTerminalViewKeyLoggingEnabled);
    }

    void onResume() {
        setSoftKeyboardState(true, mHost.isActivityRecreated());
        mTerminalCursorBlinkerStateAlreadySet = false;

        if (mHost.getTerminalView().mEmulator != null) {
            setTerminalCursorBlinkerState(true);
            mTerminalCursorBlinkerStateAlreadySet = true;
        }
    }

    void onStop() {
        setTerminalCursorBlinkerState(false);
    }

    void onReloadProperties() {
        setSessionShortcuts();
    }

    void onReloadActivityStyling() {
        setSoftKeyboardState(false, true);
        setTerminalCursorBlinkerState(true);
    }

    void onEmulatorSet() {
        if (!mTerminalCursorBlinkerStateAlreadySet) {
            setTerminalCursorBlinkerState(true);
            mTerminalCursorBlinkerStateAlreadySet = true;
        }
    }

    void onSingleTapUp(MotionEvent e) {
        TerminalSession currentSession = mHost.getCurrentSession();
        if (currentSession == null) return;

        TerminalEmulator term = currentSession.getEmulator();

        if (mHost.getProperties().shouldOpenTerminalTranscriptURLOnClick()) {
            int[] columnAndRow = mHost.getTerminalView().getColumnAndRow(e, true);
            String wordAtTap = term.getScreen().getWordAtLocation(columnAndRow[0], columnAndRow[1]);
            LinkedHashSet<CharSequence> urlSet = TermuxUrlUtils.extractUrls(wordAtTap);

            if (!urlSet.isEmpty()) {
                String url = (String) urlSet.iterator().next();
                ShareUtils.openUrl(mHost.getActivity(), url);
                return;
            }
        }

        if (!term.isMouseTrackingActive() && !e.isFromSource(InputDevice.SOURCE_MOUSE)) {
            if (!KeyboardUtils.areDisableSoftKeyboardFlagsSet(mHost.getActivity()))
                KeyboardUtils.showSoftKeyboard(mHost.getActivity(), mHost.getTerminalView());
            else
                Logger.logVerbose(LOG_TAG, "Not showing soft keyboard onSingleTapUp since its disabled");
        }
    }

    boolean onCodePoint(final int codePoint, boolean ctrlDown, boolean virtualFnKeyDown, TerminalSession session,
        Runnable clearVirtualFnKeyDownRunnable) {
        if (virtualFnKeyDown) {
            int resultingKeyCode = -1;
            int resultingCodePoint = -1;
            boolean altDown = false;
            int lowerCase = Character.toLowerCase(codePoint);
            switch (lowerCase) {
                case 'w':
                    resultingKeyCode = KeyEvent.KEYCODE_DPAD_UP;
                    break;
                case 'a':
                    resultingKeyCode = KeyEvent.KEYCODE_DPAD_LEFT;
                    break;
                case 's':
                    resultingKeyCode = KeyEvent.KEYCODE_DPAD_DOWN;
                    break;
                case 'd':
                    resultingKeyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
                    break;
                case 'p':
                    resultingKeyCode = KeyEvent.KEYCODE_PAGE_UP;
                    break;
                case 'n':
                    resultingKeyCode = KeyEvent.KEYCODE_PAGE_DOWN;
                    break;
                case 't':
                    resultingKeyCode = KeyEvent.KEYCODE_TAB;
                    break;
                case 'i':
                    resultingKeyCode = KeyEvent.KEYCODE_INSERT;
                    break;
                case 'h':
                    resultingCodePoint = '~';
                    break;
                case 'u':
                    resultingCodePoint = '_';
                    break;
                case 'l':
                    resultingCodePoint = '|';
                    break;
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    resultingKeyCode = (codePoint - '1') + KeyEvent.KEYCODE_F1;
                    break;
                case '0':
                    resultingKeyCode = KeyEvent.KEYCODE_F10;
                    break;
                case 'e':
                    resultingCodePoint = /*Escape*/ 27;
                    break;
                case '.':
                    resultingCodePoint = /*^.*/ 28;
                    break;
                case 'b':
                case 'f':
                case 'x':
                    resultingCodePoint = lowerCase;
                    altDown = true;
                    break;
                case 'v':
                    resultingCodePoint = -1;
                    AudioManager audio = (AudioManager) mHost.getActivity().getSystemService(Context.AUDIO_SERVICE);
                    audio.adjustSuggestedStreamVolume(AudioManager.ADJUST_SAME, AudioManager.USE_DEFAULT_STREAM_TYPE, AudioManager.FLAG_SHOW_UI);
                    break;
                case 'q':
                case 'k':
                    mHost.toggleTerminalToolbar();
                    clearVirtualFnKeyDownRunnable.run();
                    break;
            }

            if (resultingKeyCode != -1) {
                TerminalEmulator term = session.getEmulator();
                session.write(KeyHandler.getCode(resultingKeyCode, 0, term.isCursorKeysApplicationMode(), term.isKeypadApplicationMode()));
            } else if (resultingCodePoint != -1) {
                session.writeCodePoint(altDown, resultingCodePoint);
            }
            return true;
        } else if (ctrlDown) {
            if (codePoint == 106 /* Ctrl+j or \n */ && !session.isRunning()) {
                mTermuxTerminalSessionActivityClient.removeFinishedSession(session);
                return true;
            }

            List<KeyboardShortcut> shortcuts = mSessionShortcuts;
            if (shortcuts != null && !shortcuts.isEmpty()) {
                int codePointLowerCase = Character.toLowerCase(codePoint);
                for (int i = shortcuts.size() - 1; i >= 0; i--) {
                    KeyboardShortcut shortcut = shortcuts.get(i);
                    if (codePointLowerCase == shortcut.codePoint) {
                        switch (shortcut.shortcutAction) {
                            case TermuxPropertyConstants.ACTION_SHORTCUT_CREATE_SESSION:
                                mTermuxTerminalSessionActivityClient.addNewSession(false, null);
                                return true;
                            case TermuxPropertyConstants.ACTION_SHORTCUT_NEXT_SESSION:
                                mTermuxTerminalSessionActivityClient.switchToSession(true);
                                return true;
                            case TermuxPropertyConstants.ACTION_SHORTCUT_PREVIOUS_SESSION:
                                mTermuxTerminalSessionActivityClient.switchToSession(false);
                                return true;
                            case TermuxPropertyConstants.ACTION_SHORTCUT_RENAME_SESSION:
                                mTermuxTerminalSessionActivityClient.renameSession(session);
                                return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    boolean readExtraKeysSpecialButton(SpecialButton specialButton) {
        if (mHost.getExtraKeysView() == null) return false;
        Boolean state = mHost.getExtraKeysView().readSpecialButton(specialButton, true);
        if (state == null) {
            Logger.logError(LOG_TAG,"Failed to read an unregistered " + specialButton + " special button value from extra keys.");
            return false;
        }
        return state;
    }

    void onToggleSoftKeyboardRequest() {
        if (mHost.getProperties().shouldEnableDisableSoftKeyboardOnToggle()) {
            if (!KeyboardUtils.areDisableSoftKeyboardFlagsSet(mHost.getActivity())) {
                Logger.logVerbose(LOG_TAG, "Disabling soft keyboard on toggle");
                mHost.getPreferences().setSoftKeyboardEnabled(false);
                KeyboardUtils.disableSoftKeyboard(mHost.getActivity(), mHost.getTerminalView());
            } else {
                Logger.logVerbose(LOG_TAG, "Enabling soft keyboard on toggle");
                mHost.getPreferences().setSoftKeyboardEnabled(true);
                KeyboardUtils.clearDisableSoftKeyboardFlags(mHost.getActivity());
                if (mShowSoftKeyboardWithDelayOnce) {
                    mShowSoftKeyboardWithDelayOnce = false;
                    mHost.getTerminalView().postDelayed(getShowSoftKeyboardRunnable(), 500);
                    mHost.getTerminalView().requestFocus();
                } else {
                    KeyboardUtils.showSoftKeyboard(mHost.getActivity(), mHost.getTerminalView());
                }
            }
        } else {
            if (!mHost.getPreferences().isSoftKeyboardEnabled()) {
                Logger.logVerbose(LOG_TAG, "Maintaining disabled soft keyboard on toggle");
                KeyboardUtils.disableSoftKeyboard(mHost.getActivity(), mHost.getTerminalView());
            } else {
                Logger.logVerbose(LOG_TAG, "Showing/Hiding soft keyboard on toggle");
                KeyboardUtils.clearDisableSoftKeyboardFlags(mHost.getActivity());
                KeyboardUtils.toggleSoftKeyboard(mHost.getActivity());
            }
        }
    }

    void setSoftKeyboardState(boolean isStartup, boolean isReloadTermuxProperties) {
        boolean noShowKeyboard = false;

        if (KeyboardUtils.shouldSoftKeyboardBeDisabled(mHost.getActivity(),
            mHost.getPreferences().isSoftKeyboardEnabled(),
            mHost.getPreferences().isSoftKeyboardEnabledOnlyIfNoHardware())) {
            Logger.logVerbose(LOG_TAG, "Maintaining disabled soft keyboard");
            KeyboardUtils.disableSoftKeyboard(mHost.getActivity(), mHost.getTerminalView());
            mHost.getTerminalView().requestFocus();
            noShowKeyboard = true;
            if (isStartup && mHost.isOnResumeAfterOnCreate())
                mShowSoftKeyboardWithDelayOnce = true;
        } else {
            KeyboardUtils.setSoftInputModeAdjustResize(mHost.getActivity());
            KeyboardUtils.clearDisableSoftKeyboardFlags(mHost.getActivity());
            if (isStartup && mHost.getProperties().shouldSoftKeyboardBeHiddenOnStartup()) {
                Logger.logVerbose(LOG_TAG, "Hiding soft keyboard on startup");
                KeyboardUtils.setSoftKeyboardAlwaysHiddenFlags(mHost.getActivity());
                KeyboardUtils.hideSoftKeyboard(mHost.getActivity(), mHost.getTerminalView());
                mHost.getTerminalView().requestFocus();
                noShowKeyboard = true;
                mShowSoftKeyboardIgnoreOnce = true;
            }
        }

        mHost.getTerminalView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                boolean textInputViewHasFocus = false;
                final EditText textInputView = mHost.getActivity().findViewById(R.id.terminal_toolbar_text_input);
                if (textInputView != null) textInputViewHasFocus = textInputView.hasFocus();

                if (hasFocus || textInputViewHasFocus) {
                    if (mShowSoftKeyboardIgnoreOnce) {
                        mShowSoftKeyboardIgnoreOnce = false;
                        return;
                    }
                    Logger.logVerbose(LOG_TAG, "Showing soft keyboard on focus change");
                } else {
                    Logger.logVerbose(LOG_TAG, "Hiding soft keyboard on focus change");
                }

                KeyboardUtils.setSoftKeyboardVisibility(getShowSoftKeyboardRunnable(), mHost.getActivity(), mHost.getTerminalView(), hasFocus || textInputViewHasFocus);
            }
        });

        if (!isReloadTermuxProperties && !noShowKeyboard) {
            Logger.logVerbose(LOG_TAG, "Requesting TerminalView focus and showing soft keyboard");
            mHost.getTerminalView().requestFocus();
            mHost.getTerminalView().postDelayed(getShowSoftKeyboardRunnable(), 300);
        }
    }

    void setTerminalCursorBlinkerState(boolean start) {
        if (start) {
            if (mHost.getTerminalView().setTerminalCursorBlinkerRate(mHost.getProperties().getTerminalCursorBlinkRate()))
                mHost.getTerminalView().setTerminalCursorBlinkerState(true, true);
            else
                Logger.logError(LOG_TAG,"Failed to start cursor blinker");
        } else {
            mHost.getTerminalView().setTerminalCursorBlinkerState(false, true);
        }
    }

    private Runnable getShowSoftKeyboardRunnable() {
        if (mShowSoftKeyboardRunnable == null) {
            mShowSoftKeyboardRunnable = () -> KeyboardUtils.showSoftKeyboard(mHost.getActivity(), mHost.getTerminalView());
        }
        return mShowSoftKeyboardRunnable;
    }

    private void setSessionShortcuts() {
        mSessionShortcuts = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : TermuxPropertyConstants.MAP_SESSION_SHORTCUTS.entrySet()) {
            Integer codePoint = (Integer) mHost.getProperties().getInternalPropertyValue(entry.getKey(), true);
            if (codePoint != null)
                mSessionShortcuts.add(new KeyboardShortcut(codePoint, entry.getValue()));
        }
    }

    void changeFontSize(boolean increase) {
        mHost.getPreferences().changeFontSize(increase);
        mHost.getTerminalView().setTextSize(mHost.getPreferences().getFontSize());
    }

    void shareSessionTranscript() {
        mActionHelper.shareSessionTranscript();
    }

    void shareSelectedText() {
        mActionHelper.shareSelectedText();
    }

    void showUrlSelection() {
        mActionHelper.showUrlSelection();
    }

    void reportIssueFromTranscript() {
        mActionHelper.reportIssueFromTranscript();
    }

    void doPaste() {
        mActionHelper.doPaste();
    }
}
