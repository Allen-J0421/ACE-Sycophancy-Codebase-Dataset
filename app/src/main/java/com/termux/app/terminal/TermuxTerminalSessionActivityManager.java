package com.termux.app.terminal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.termux.R;
import com.termux.app.TermuxActivity;
import com.termux.app.TermuxService;
import com.termux.shared.logger.Logger;
import com.termux.shared.termux.TermuxConstants;
import com.termux.shared.termux.shell.command.runner.terminal.TermuxSession;
import com.termux.shared.termux.interact.TextInputDialogUtils;
import com.termux.terminal.TerminalSession;
import com.termux.terminal.TerminalColors;
import com.termux.terminal.TextStyle;

/** Session lifecycle and service interaction manager for {@link TermuxTerminalSessionActivityClient}. */
final class TermuxTerminalSessionActivityManager {

    private static final int MAX_SESSIONS = 8;
    private static final String LOG_TAG = "TermuxTerminalSessionActivityClient";

    private final TermuxActivity mActivity;

    TermuxTerminalSessionActivityManager(TermuxActivity activity) {
        mActivity = activity;
    }

    void onStart() {
        if (mActivity.getTermuxService() != null) {
            setCurrentSession(getCurrentStoredSessionOrLast());
            termuxSessionListNotifyUpdated();
        }

        mActivity.getTerminalView().onScreenUpdated();
    }

    void onStop() {
        setCurrentStoredSession();
    }

    void onTitleChanged(@NonNull TerminalSession updatedSession) {
        if (!mActivity.isVisible()) return;

        if (updatedSession != mActivity.getCurrentSession()) {
            mActivity.showToast(toToastTitle(updatedSession), true);
        }

        termuxSessionListNotifyUpdated();
    }

    void onSessionFinished(@NonNull TerminalSession finishedSession) {
        TermuxService service = mActivity.getTermuxService();
        if (service == null || service.wantsToStop()) {
            mActivity.finishActivityIfNotFinishing();
            return;
        }

        int index = service.getIndexOfSession(finishedSession);
        boolean isPluginExecutionCommandWithPendingResult = false;
        TermuxSession termuxSession = service.getTermuxSession(index);
        if (termuxSession != null) {
            isPluginExecutionCommandWithPendingResult = termuxSession.getExecutionCommand().isPluginExecutionCommandWithPendingResult();
            if (isPluginExecutionCommandWithPendingResult) {
                Logger.logVerbose(LOG_TAG, "The \"" + finishedSession.mSessionName + "\" session will be force finished automatically since result in pending.");
            }
        }

        if (mActivity.isVisible() && finishedSession != mActivity.getCurrentSession() && index >= 0) {
            mActivity.showToast(toToastTitle(finishedSession) + " - exited", true);
        }

        if (mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LEANBACK)) {
            if (service.getTermuxSessionsSize() > 1 || isPluginExecutionCommandWithPendingResult) {
                removeFinishedSession(finishedSession);
            }
        } else if (finishedSession.getExitStatus() == 0 || finishedSession.getExitStatus() == 130 || isPluginExecutionCommandWithPendingResult) {
            removeFinishedSession(finishedSession);
        }
    }

    void setTerminalShellPid(@NonNull TerminalSession terminalSession, int pid) {
        TermuxService service = mActivity.getTermuxService();
        if (service == null) return;

        TermuxSession termuxSession = service.getTermuxSessionForTerminalSession(terminalSession);
        if (termuxSession != null) {
            termuxSession.getExecutionCommand().mPid = pid;
        }
    }

    void setCurrentSession(TerminalSession session) {
        if (session == null) return;

        if (mActivity.getTerminalView().attachSession(session)) {
            notifyOfSessionChange();
        }

        checkAndScrollToSession(session);
        updateBackgroundColor();
    }

    void switchToSession(boolean forward) {
        TermuxService service = mActivity.getTermuxService();
        if (service == null) return;

        TerminalSession currentTerminalSession = mActivity.getCurrentSession();
        int index = service.getIndexOfSession(currentTerminalSession);
        int size = service.getTermuxSessionsSize();
        if (forward) {
            if (++index >= size) index = 0;
        } else {
            if (--index < 0) index = size - 1;
        }

        TermuxSession termuxSession = service.getTermuxSession(index);
        if (termuxSession != null) {
            setCurrentSession(termuxSession.getTerminalSession());
        }
    }

    void switchToSession(int index) {
        TermuxService service = mActivity.getTermuxService();
        if (service == null) return;

        TermuxSession termuxSession = service.getTermuxSession(index);
        if (termuxSession != null) {
            setCurrentSession(termuxSession.getTerminalSession());
        }
    }

    @SuppressLint("InflateParams")
    void renameSession(final TerminalSession sessionToRename) {
        if (sessionToRename == null) return;

        TextInputDialogUtils.textInput(mActivity, R.string.title_rename_session, sessionToRename.mSessionName,
            R.string.action_rename_session_confirm, text -> {
                renameSession(sessionToRename, text);
                termuxSessionListNotifyUpdated();
            }, -1, null, -1, null, null);
    }

    void addNewSession(boolean isFailSafe, String sessionName) {
        TermuxService service = mActivity.getTermuxService();
        if (service == null) return;

        if (service.getTermuxSessionsSize() >= MAX_SESSIONS) {
            new AlertDialog.Builder(mActivity).setTitle(R.string.title_max_terminals_reached).setMessage(R.string.msg_max_terminals_reached)
                .setPositiveButton(android.R.string.ok, null).show();
            return;
        }

        TerminalSession currentSession = mActivity.getCurrentSession();
        String workingDirectory = currentSession == null ? mActivity.getProperties().getDefaultWorkingDirectory() : currentSession.getCwd();
        TermuxSession newTermuxSession = service.createTermuxSession(null, null, null, workingDirectory, isFailSafe, sessionName);
        if (newTermuxSession == null) return;

        setCurrentSession(newTermuxSession.getTerminalSession());
        mActivity.getDrawer().closeDrawers();
    }

    void setCurrentStoredSession() {
        TerminalSession currentSession = mActivity.getCurrentSession();
        if (currentSession != null) {
            mActivity.getPreferences().setCurrentSession(currentSession.mHandle);
        } else {
            mActivity.getPreferences().setCurrentSession(null);
        }
    }

    TerminalSession getCurrentStoredSessionOrLast() {
        TerminalSession stored = getCurrentStoredSession();
        if (stored != null) {
            return stored;
        }

        TermuxService service = mActivity.getTermuxService();
        if (service == null) return null;

        TermuxSession termuxSession = service.getLastTermuxSession();
        return termuxSession != null ? termuxSession.getTerminalSession() : null;
    }

    void removeFinishedSession(TerminalSession finishedSession) {
        TermuxService service = mActivity.getTermuxService();
        if (service == null) return;

        int index = service.removeTermuxSession(finishedSession);
        int size = service.getTermuxSessionsSize();
        if (size == 0) {
            mActivity.finishActivityIfNotFinishing();
            return;
        }

        if (index >= size) {
            index = size - 1;
        }

        TermuxSession termuxSession = service.getTermuxSession(index);
        if (termuxSession != null) {
            setCurrentSession(termuxSession.getTerminalSession());
        }
    }

    void termuxSessionListNotifyUpdated() {
        mActivity.termuxSessionListNotifyUpdated();
    }

    void checkAndScrollToSession(TerminalSession session) {
        if (!mActivity.isVisible()) return;
        TermuxService service = mActivity.getTermuxService();
        if (service == null) return;

        final int indexOfSession = service.getIndexOfSession(session);
        if (indexOfSession < 0) return;
        final ListView termuxSessionsListView = mActivity.findViewById(R.id.terminal_sessions_list);
        if (termuxSessionsListView == null) return;

        termuxSessionsListView.setItemChecked(indexOfSession, true);
        termuxSessionsListView.postDelayed(() -> termuxSessionsListView.smoothScrollToPosition(indexOfSession), 1000);
    }

    String toToastTitle(TerminalSession session) {
        TermuxService service = mActivity.getTermuxService();
        if (service == null) return null;

        final int indexOfSession = service.getIndexOfSession(session);
        if (indexOfSession < 0) return null;

        StringBuilder toastTitle = new StringBuilder("[" + (indexOfSession + 1) + "]");
        if (!android.text.TextUtils.isEmpty(session.mSessionName)) {
            toastTitle.append(" ").append(session.mSessionName);
        }

        String title = session.getTitle();
        if (!android.text.TextUtils.isEmpty(title)) {
            toastTitle.append(session.mSessionName == null ? " " : "\n");
            toastTitle.append(title);
        }
        return toastTitle.toString();
    }

    void updateBackgroundColor() {
        if (!mActivity.isVisible()) return;
        TerminalSession session = mActivity.getCurrentSession();
        if (session != null && session.getEmulator() != null) {
            mActivity.getWindow().getDecorView().setBackgroundColor(session.getEmulator().mColors.mCurrentColors[TextStyle.COLOR_INDEX_BACKGROUND]);
        }
    }

    void checkForFontAndColors() {
        try {
            java.io.File colorsFile = TermuxConstants.TERMUX_COLOR_PROPERTIES_FILE;
            java.io.File fontFile = TermuxConstants.TERMUX_FONT_FILE;

            java.util.Properties props = new java.util.Properties();
            if (colorsFile.isFile()) {
                try (java.io.InputStream in = new java.io.FileInputStream(colorsFile)) {
                    props.load(in);
                }
            }

            TerminalColors.COLOR_SCHEME.updateWith(props);
            TerminalSession session = mActivity.getCurrentSession();
            if (session != null && session.getEmulator() != null) {
                session.getEmulator().mColors.reset();
            }
            updateBackgroundColor();

            android.graphics.Typeface newTypeface = (fontFile.exists() && fontFile.length() > 0) ? android.graphics.Typeface.createFromFile(fontFile) : android.graphics.Typeface.MONOSPACE;
            mActivity.getTerminalView().setTypeface(newTypeface);
        } catch (Exception e) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Error in checkForFontAndColors()", e);
        }
    }

    private TerminalSession getCurrentStoredSession() {
        String sessionHandle = mActivity.getPreferences().getCurrentSession();
        if (sessionHandle == null) return null;

        TermuxService service = mActivity.getTermuxService();
        if (service == null) return null;

        return service.getTerminalSessionForHandle(sessionHandle);
    }

    private void notifyOfSessionChange() {
        if (!mActivity.isVisible()) return;

        if (!mActivity.getProperties().areTerminalSessionChangeToastsDisabled()) {
            TerminalSession session = mActivity.getCurrentSession();
            mActivity.showToast(toToastTitle(session), false);
        }
    }

    private void renameSession(TerminalSession sessionToRename, String text) {
        if (sessionToRename == null) return;
        sessionToRename.mSessionName = text;
        TermuxService service = mActivity.getTermuxService();
        if (service != null) {
            TermuxSession termuxSession = service.getTermuxSessionForTerminalSession(sessionToRename);
            if (termuxSession != null) {
                termuxSession.getExecutionCommand().shellName = text;
            }
        }
    }
}
