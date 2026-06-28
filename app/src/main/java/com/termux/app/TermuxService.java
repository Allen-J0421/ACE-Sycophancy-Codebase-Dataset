package com.termux.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.shared.shell.command.ExecutionCommand;
import com.termux.shared.shell.command.runner.app.AppShell;
import com.termux.shared.termux.shell.command.runner.terminal.TermuxSession;
import com.termux.shared.termux.terminal.TermuxTerminalSessionClientBase;
import com.termux.app.terminal.TermuxTerminalSessionActivityClient;
import com.termux.terminal.TerminalSession;

import java.util.List;

/**
 * Service wrapper for the session and notification manager.
 */
public final class TermuxService extends Service {

    /** This service is only bound from inside the same process and never uses IPC. */
    class LocalBinder extends Binder {
        public final TermuxService service = TermuxService.this;
    }

    private final IBinder mBinder = new LocalBinder();
    private final TermuxServiceSessionManager mSessionManager = new TermuxServiceSessionManager(this);

    @Override
    public void onCreate() {
        super.onCreate();
        mSessionManager.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return mSessionManager.onStartCommand(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSessionManager.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mSessionManager.onUnbind();
        return false;
    }

    @Nullable
    public synchronized TermuxTerminalSessionClientBase getTermuxTerminalSessionClient() {
        return mSessionManager.getTermuxTerminalSessionClient();
    }

    public synchronized void setTermuxTerminalSessionClient(TermuxTerminalSessionActivityClient termuxTerminalSessionActivityClient) {
        mSessionManager.setTermuxTerminalSessionClient(termuxTerminalSessionActivityClient);
    }

    public synchronized void unsetTermuxTerminalSessionClient() {
        mSessionManager.unsetTermuxTerminalSessionClient();
    }

    @Nullable
    public synchronized AppShell createTermuxTask(String executablePath, String[] arguments, String stdin, String workingDirectory) {
        return mSessionManager.createTermuxTask(executablePath, arguments, stdin, workingDirectory);
    }

    @Nullable
    public synchronized AppShell createTermuxTask(ExecutionCommand executionCommand) {
        return mSessionManager.createTermuxTask(executionCommand);
    }

    @Nullable
    public synchronized TermuxSession createTermuxSession(String executablePath, String[] arguments, String stdin,
                                                           String workingDirectory, boolean isFailSafe, String sessionName) {
        return mSessionManager.createTermuxSession(executablePath, arguments, stdin, workingDirectory, isFailSafe, sessionName);
    }

    @Nullable
    public synchronized TermuxSession createTermuxSession(ExecutionCommand executionCommand) {
        return mSessionManager.createTermuxSession(executionCommand);
    }

    public synchronized int removeTermuxSession(TerminalSession sessionToRemove) {
        return mSessionManager.removeTermuxSession(sessionToRemove);
    }

    public synchronized boolean isTermuxSessionsEmpty() {
        return mSessionManager.isTermuxSessionsEmpty();
    }

    public synchronized int getTermuxSessionsSize() {
        return mSessionManager.getTermuxSessionsSize();
    }

    public synchronized List<TermuxSession> getTermuxSessions() {
        return mSessionManager.getTermuxSessions();
    }

    @Nullable
    public synchronized TermuxSession getTermuxSession(int index) {
        return mSessionManager.getTermuxSession(index);
    }

    @Nullable
    public synchronized TermuxSession getTermuxSessionForTerminalSession(TerminalSession terminalSession) {
        return mSessionManager.getTermuxSessionForTerminalSession(terminalSession);
    }

    @Nullable
    public synchronized TermuxSession getLastTermuxSession() {
        return mSessionManager.getLastTermuxSession();
    }

    public synchronized int getIndexOfSession(TerminalSession terminalSession) {
        return mSessionManager.getIndexOfSession(terminalSession);
    }

    @Nullable
    public synchronized TerminalSession getTerminalSessionForHandle(String sessionHandle) {
        return mSessionManager.getTerminalSessionForHandle(sessionHandle);
    }

    @Nullable
    public synchronized AppShell getTermuxTaskForShellName(String name) {
        return mSessionManager.getTermuxTaskForShellName(name);
    }

    @Nullable
    public synchronized TermuxSession getTermuxSessionForShellName(String name) {
        return mSessionManager.getTermuxSessionForShellName(name);
    }

    public boolean wantsToStop() {
        return mSessionManager.wantsToStop();
    }
}
