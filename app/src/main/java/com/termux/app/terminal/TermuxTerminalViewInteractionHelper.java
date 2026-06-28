package com.termux.app.terminal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.media.AudioManager;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.termux.R;
import com.termux.app.models.UserAction;
import com.termux.app.terminal.io.KeyboardShortcut;
import com.termux.shared.activities.ReportActivity;
import com.termux.shared.android.AndroidUtils;
import com.termux.shared.data.DataUtils;
import com.termux.shared.file.FileUtils;
import com.termux.shared.interact.MessageDialogUtils;
import com.termux.shared.interact.ShareUtils;
import com.termux.shared.logger.Logger;
import com.termux.shared.markdown.MarkdownUtils;
import com.termux.shared.shell.ShellUtils;
import com.termux.shared.termux.TermuxBootstrap;
import com.termux.shared.termux.TermuxConstants;
import com.termux.shared.termux.extrakeys.SpecialButton;
import com.termux.shared.termux.settings.properties.TermuxPropertyConstants;
import com.termux.shared.termux.TermuxUtils;
import com.termux.shared.termux.data.TermuxUrlUtils;
import com.termux.shared.view.KeyboardUtils;
import com.termux.terminal.KeyHandler;
import com.termux.terminal.TerminalEmulator;
import com.termux.terminal.TerminalSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

final class TermuxTerminalViewInteractionHelper {

    private static final String LOG_TAG = "TermuxTerminalViewClient";

    private final TermuxTerminalViewClientHost mHost;
    private final TermuxTerminalSessionActivityClient mTermuxTerminalSessionActivityClient;

    private Runnable mShowSoftKeyboardRunnable;
    private boolean mShowSoftKeyboardIgnoreOnce;
    private boolean mShowSoftKeyboardWithDelayOnce;
    private boolean mTerminalCursorBlinkerStateAlreadySet;
    private List<KeyboardShortcut> mSessionShortcuts;

    TermuxTerminalViewInteractionHelper(TermuxTerminalViewClientHost host,
        TermuxTerminalSessionActivityClient termuxTerminalSessionActivityClient) {
        mHost = host;
        mTermuxTerminalSessionActivityClient = termuxTerminalSessionActivityClient;
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

    void changeFontSize(boolean increase) {
        mHost.getPreferences().changeFontSize(increase);
        mHost.getTerminalView().setTextSize(mHost.getPreferences().getFontSize());
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

    void shareSessionTranscript() {
        TerminalSession session = mHost.getCurrentSession();
        if (session == null) return;

        String transcriptText = ShellUtils.getTerminalSessionTranscriptText(session, false, true);
        if (transcriptText == null) return;

        transcriptText = DataUtils.getTruncatedCommandOutput(transcriptText, DataUtils.TRANSACTION_SIZE_LIMIT_IN_BYTES, false, true, false).trim();
        ShareUtils.shareText(mHost.getActivity(), mHost.getActivity().getString(R.string.title_share_transcript),
            transcriptText, mHost.getActivity().getString(R.string.title_share_transcript_with));
    }

    void shareSelectedText() {
        String selectedText = mHost.getTerminalView().getStoredSelectedText();
        if (DataUtils.isNullOrEmpty(selectedText)) return;
        ShareUtils.shareText(mHost.getActivity(), mHost.getActivity().getString(R.string.title_share_selected_text),
            selectedText, mHost.getActivity().getString(R.string.title_share_selected_text_with));
    }

    void showUrlSelection() {
        TerminalSession session = mHost.getCurrentSession();
        if (session == null) return;

        String text = ShellUtils.getTerminalSessionTranscriptText(session, true, true);
        LinkedHashSet<CharSequence> urlSet = TermuxUrlUtils.extractUrls(text);
        if (urlSet.isEmpty()) {
            new AlertDialog.Builder(mHost.getActivity()).setMessage(R.string.title_select_url_none_found).show();
            return;
        }

        final CharSequence[] urls = urlSet.toArray(new CharSequence[0]);
        Collections.reverse(Arrays.asList(urls));

        final AlertDialog dialog = new AlertDialog.Builder(mHost.getActivity()).setItems(urls, (di, which) -> {
            String url = (String) urls[which];
            ShareUtils.copyTextToClipboard(mHost.getActivity(), url, mHost.getActivity().getString(R.string.msg_select_url_copied_to_clipboard));
        }).setTitle(R.string.title_select_url_dialog).create();

        dialog.setOnShowListener(di -> {
            ListView lv = dialog.getListView();
            lv.setOnItemLongClickListener((parent, view, position, id) -> {
                dialog.dismiss();
                String url = (String) urls[position];
                ShareUtils.openUrl(mHost.getActivity(), url);
                return true;
            });
        });

        dialog.show();
    }

    void reportIssueFromTranscript() {
        TerminalSession session = mHost.getCurrentSession();
        if (session == null) return;

        final String transcriptText = ShellUtils.getTerminalSessionTranscriptText(session, false, true);
        if (transcriptText == null) return;

        MessageDialogUtils.showMessage(mHost.getActivity(), TermuxConstants.TERMUX_APP_NAME + " Report Issue",
            mHost.getActivity().getString(R.string.msg_add_termux_debug_info),
            mHost.getActivity().getString(com.termux.shared.R.string.action_yes), (dialog, which) -> reportIssueFromTranscript(transcriptText, true),
            mHost.getActivity().getString(com.termux.shared.R.string.action_no), (dialog, which) -> reportIssueFromTranscript(transcriptText, false),
            null);
    }

    void doPaste() {
        TerminalSession session = mHost.getCurrentSession();
        if (session == null || !session.isRunning()) return;

        String text = ShareUtils.getTextStringFromClipboardIfSet(mHost.getActivity(), true);
        if (text != null)
            session.getEmulator().paste(text);
    }

    private void reportIssueFromTranscript(String transcriptText, boolean addTermuxDebugInfo) {
        Logger.showToast(mHost.getActivity(), mHost.getActivity().getString(R.string.msg_generating_report), true);

        new Thread() {
            @Override
            public void run() {
                StringBuilder reportString = new StringBuilder();
                String title = TermuxConstants.TERMUX_APP_NAME + " Report Issue";

                reportString.append("## Transcript\n");
                reportString.append("\n").append(MarkdownUtils.getMarkdownCodeForString(transcriptText, true));
                reportString.append("\n##\n");

                if (addTermuxDebugInfo) {
                    reportString.append("\n\n").append(TermuxUtils.getAppInfoMarkdownString(mHost.getActivity(), TermuxUtils.AppInfoMode.TERMUX_AND_PLUGIN_PACKAGES));
                } else {
                    reportString.append("\n\n").append(TermuxUtils.getAppInfoMarkdownString(mHost.getActivity(), TermuxUtils.AppInfoMode.TERMUX_PACKAGE));
                }

                reportString.append("\n\n").append(AndroidUtils.getDeviceInfoMarkdownString(mHost.getActivity(), true));

                if (TermuxBootstrap.isAppPackageManagerAPT()) {
                    String termuxAptInfo = TermuxUtils.geAPTInfoMarkdownString(mHost.getActivity());
                    if (termuxAptInfo != null)
                        reportString.append("\n\n").append(termuxAptInfo);
                }

                if (addTermuxDebugInfo) {
                    String termuxDebugInfo = TermuxUtils.getTermuxDebugMarkdownString(mHost.getActivity());
                    if (termuxDebugInfo != null)
                        reportString.append("\n\n").append(termuxDebugInfo);
                }

                String userActionName = UserAction.REPORT_ISSUE_FROM_TRANSCRIPT.getName();
                com.termux.shared.models.ReportInfo reportInfo = new com.termux.shared.models.ReportInfo(userActionName,
                    TermuxConstants.TERMUX_APP.TERMUX_ACTIVITY_NAME, title);
                reportInfo.setReportString(reportString.toString());
                reportInfo.setReportStringSuffix("\n\n" + TermuxUtils.getReportIssueMarkdownString(mHost.getActivity()));
                reportInfo.setReportSaveFileLabelAndPath(userActionName,
                    Environment.getExternalStorageDirectory() + "/" +
                        FileUtils.sanitizeFileName(TermuxConstants.TERMUX_APP_NAME + "-" + userActionName + ".log", true, true));

                ReportActivity.startReportActivity(mHost.getActivity(), reportInfo);
            }
        }.start();
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
}
