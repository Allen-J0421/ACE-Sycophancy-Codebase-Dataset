package com.termux.app;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.PowerManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.R;
import com.termux.app.terminal.TermuxTerminalSessionActivityClient;
import com.termux.app.terminal.TermuxTerminalSessionServiceClient;
import com.termux.shared.android.PermissionUtils;
import com.termux.shared.data.DataUtils;
import com.termux.shared.data.IntentUtils;
import com.termux.shared.errors.Errno;
import com.termux.shared.logger.Logger;
import com.termux.shared.net.uri.UriUtils;
import com.termux.shared.notification.NotificationUtils;
import com.termux.shared.shell.ShellUtils;
import com.termux.shared.shell.command.ExecutionCommand;
import com.termux.shared.shell.command.ExecutionCommand.Runner;
import com.termux.shared.shell.command.ExecutionCommand.ShellCreateMode;
import com.termux.shared.shell.command.runner.app.AppShell;
import com.termux.shared.termux.TermuxConstants;
import com.termux.shared.termux.TermuxConstants.TERMUX_APP.TERMUX_ACTIVITY;
import com.termux.shared.termux.TermuxConstants.TERMUX_APP.TERMUX_SERVICE;
import com.termux.shared.termux.plugins.TermuxPluginUtils;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;
import com.termux.shared.termux.settings.properties.TermuxAppSharedProperties;
import com.termux.shared.termux.shell.TermuxShellManager;
import com.termux.shared.termux.shell.TermuxShellUtils;
import com.termux.shared.termux.shell.command.environment.TermuxShellEnvironment;
import com.termux.shared.termux.shell.command.runner.terminal.TermuxSession;
import com.termux.shared.termux.terminal.TermuxTerminalSessionClientBase;
import com.termux.terminal.TerminalSession;
import com.termux.terminal.TerminalSessionClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

final class TermuxServiceSessionManager implements AppShell.AppShellClient, TermuxSession.TermuxSessionClient {

    private final TermuxService mService;
    private final Handler mHandler = new Handler();
    private final TermuxTerminalSessionServiceClient mTermuxTerminalSessionServiceClient;

    private TermuxTerminalSessionActivityClient mTermuxTerminalSessionActivityClient;
    private TermuxAppSharedProperties mProperties;
    private TermuxShellManager mShellManager;
    private PowerManager.WakeLock mWakeLock;
    private WifiManager.WifiLock mWifiLock;
    private boolean mWantsToStop;

    TermuxServiceSessionManager(@NonNull TermuxService service) {
        mService = service;
        mTermuxTerminalSessionServiceClient = new TermuxTerminalSessionServiceClient(service);
    }

    void onCreate() {
        Logger.logVerbose(LOG_TAG, "onCreate");

        mProperties = TermuxAppSharedProperties.getProperties();
        mShellManager = TermuxShellManager.getShellManager();

        runStartForeground();

        com.termux.app.event.SystemEventReceiver.registerPackageUpdateEvents(mService);
    }

    void onDestroy() {
        Logger.logVerbose(LOG_TAG, "onDestroy");

        TermuxShellUtils.clearTermuxTMPDIR(true);

        actionReleaseWakeLock(false);
        if (!mWantsToStop) {
            killAllTermuxExecutionCommands();
        }

        TermuxShellManager.onAppExit(mService);

        com.termux.app.event.SystemEventReceiver.unregisterPackageUpdateEvents(mService);

        runStopForeground();
    }

    int onStartCommand(@Nullable Intent intent) {
        Logger.logDebug(LOG_TAG, "onStartCommand");

        runStartForeground();
        handleStartCommandIntent(intent);
        return android.app.Service.START_NOT_STICKY;
    }

    private void handleStartCommandIntent(@Nullable Intent intent) {
        if (intent == null) return;

        Logger.logVerboseExtended(LOG_TAG, "Intent Received:\n" + IntentUtils.getIntentString(intent));
        String action = intent.getAction();
        if (action == null) return;

        handleStartCommandAction(action, intent);
    }

    private void handleStartCommandAction(@NonNull String action, @NonNull Intent intent) {
        switch (action) {
            case TERMUX_SERVICE.ACTION_STOP_SERVICE:
                Logger.logDebug(LOG_TAG, "ACTION_STOP_SERVICE intent received");
                actionStopService();
                break;
            case TERMUX_SERVICE.ACTION_WAKE_LOCK:
                Logger.logDebug(LOG_TAG, "ACTION_WAKE_LOCK intent received");
                actionAcquireWakeLock();
                break;
            case TERMUX_SERVICE.ACTION_WAKE_UNLOCK:
                Logger.logDebug(LOG_TAG, "ACTION_WAKE_UNLOCK intent received");
                actionReleaseWakeLock(true);
                break;
            case TERMUX_SERVICE.ACTION_SERVICE_EXECUTE:
                Logger.logDebug(LOG_TAG, "ACTION_SERVICE_EXECUTE intent received");
                actionServiceExecute(intent);
                break;
            default:
                Logger.logError(LOG_TAG, "Invalid action: \"" + action + "\"");
                break;
        }
    }

    void onUnbind() {
        Logger.logVerbose(LOG_TAG, "onUnbind");

        if (mTermuxTerminalSessionActivityClient != null) {
            unsetTermuxTerminalSessionClient();
        }
    }

    void actionStopService() {
        mWantsToStop = true;
        killAllTermuxExecutionCommands();
        requestStopService();
    }

    void actionAcquireWakeLock() {
        if (mWakeLock != null) {
            Logger.logDebug(LOG_TAG, "Ignoring acquiring WakeLocks since they are already held");
            return;
        }

        Logger.logDebug(LOG_TAG, "Acquiring WakeLocks");

        PowerManager pm = (PowerManager) mService.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TermuxConstants.TERMUX_APP_NAME.toLowerCase() + ":service-wakelock");
        mWakeLock.acquire();

        WifiManager wm = (WifiManager) mService.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, TermuxConstants.TERMUX_APP_NAME.toLowerCase());
        mWifiLock.acquire();

        if (!PermissionUtils.checkIfBatteryOptimizationsDisabled(mService)) {
            PermissionUtils.requestDisableBatteryOptimizations(mService);
        }

        updateNotification();

        Logger.logDebug(LOG_TAG, "WakeLocks acquired successfully");
    }

    void actionReleaseWakeLock(boolean updateNotification) {
        if (mWakeLock == null && mWifiLock == null) {
            Logger.logDebug(LOG_TAG, "Ignoring releasing WakeLocks since none are already held");
            return;
        }

        Logger.logDebug(LOG_TAG, "Releasing WakeLocks");

        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }

        if (mWifiLock != null) {
            mWifiLock.release();
            mWifiLock = null;
        }

        if (updateNotification) {
            updateNotification();
        }

        Logger.logDebug(LOG_TAG, "WakeLocks released successfully");
    }

    void actionServiceExecute(@Nullable Intent intent) {
        if (intent == null) {
            Logger.logError(LOG_TAG, "Ignoring null intent to actionServiceExecute");
            return;
        }

        ExecutionCommand executionCommand = createExecutionCommandFromIntent(intent);
        if (executionCommand == null) {
            return;
        }

        if (executionCommand.shellCreateMode == null) {
            executionCommand.shellCreateMode = ShellCreateMode.ALWAYS.getMode();
        }

        mShellManager.mPendingPluginExecutionCommands.add(executionCommand);

        if (Runner.APP_SHELL.equalsRunner(executionCommand.runner)) {
            executeTermuxTaskCommand(executionCommand);
        } else if (Runner.TERMINAL_SESSION.equalsRunner(executionCommand.runner)) {
            executeTermuxSessionCommand(executionCommand);
        } else {
            String errmsg = mService.getString(R.string.error_termux_service_unsupported_execution_command_runner, executionCommand.runner);
            executionCommand.setStateFailed(Errno.ERRNO_FAILED.getCode(), errmsg);
            TermuxPluginUtils.processPluginExecutionCommandError(mService, LOG_TAG, executionCommand, false);
        }
    }

    @Nullable
    ExecutionCommand createExecutionCommandFromIntent(@NonNull Intent intent) {
        ExecutionCommand executionCommand = new ExecutionCommand(TermuxShellManager.getNextShellId());

        executionCommand.executableUri = intent.getData();
        executionCommand.isPluginExecutionCommand = true;
        executionCommand.runner = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_RUNNER,
            (intent.getBooleanExtra(TERMUX_SERVICE.EXTRA_BACKGROUND, false) ? Runner.APP_SHELL.getName() : Runner.TERMINAL_SESSION.getName()));
        if (Runner.runnerOf(executionCommand.runner) == null) {
            String errmsg = mService.getString(R.string.error_termux_service_invalid_execution_command_runner, executionCommand.runner);
            executionCommand.setStateFailed(Errno.ERRNO_FAILED.getCode(), errmsg);
            TermuxPluginUtils.processPluginExecutionCommandError(mService, LOG_TAG, executionCommand, false);
            return null;
        }

        if (executionCommand.executableUri != null) {
            Logger.logVerbose(LOG_TAG, "uri: \"" + executionCommand.executableUri + "\", path: \"" + executionCommand.executableUri.getPath() + "\", fragment: \"" + executionCommand.executableUri.getFragment() + "\"");

            executionCommand.executable = UriUtils.getUriFilePathWithFragment(executionCommand.executableUri);
            executionCommand.arguments = IntentUtils.getStringArrayExtraIfSet(intent, TERMUX_SERVICE.EXTRA_ARGUMENTS, null);
            if (Runner.APP_SHELL.equalsRunner(executionCommand.runner)) {
                executionCommand.stdin = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_STDIN, null);
            }
            executionCommand.backgroundCustomLogLevel = IntentUtils.getIntegerExtraIfSet(intent, TERMUX_SERVICE.EXTRA_BACKGROUND_CUSTOM_LOG_LEVEL, null);
        }

        executionCommand.workingDirectory = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_WORKDIR, null);
        executionCommand.isFailsafe = intent.getBooleanExtra(TERMUX_ACTIVITY.EXTRA_FAILSAFE_SESSION, false);
        executionCommand.sessionAction = intent.getStringExtra(TERMUX_SERVICE.EXTRA_SESSION_ACTION);
        executionCommand.shellName = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_SHELL_NAME, null);
        executionCommand.shellCreateMode = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_SHELL_CREATE_MODE, null);
        executionCommand.commandLabel = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_COMMAND_LABEL, "Execution Intent Command");
        executionCommand.commandDescription = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_COMMAND_DESCRIPTION, null);
        executionCommand.commandHelp = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_COMMAND_HELP, null);
        executionCommand.pluginAPIHelp = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_PLUGIN_API_HELP, null);
        executionCommand.resultConfig.resultPendingIntent = intent.getParcelableExtra(TERMUX_SERVICE.EXTRA_PENDING_INTENT);
        executionCommand.resultConfig.resultDirectoryPath = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_RESULT_DIRECTORY, null);
        if (executionCommand.resultConfig.resultDirectoryPath != null) {
            executionCommand.resultConfig.resultSingleFile = intent.getBooleanExtra(TERMUX_SERVICE.EXTRA_RESULT_SINGLE_FILE, false);
            executionCommand.resultConfig.resultFileBasename = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_RESULT_FILE_BASENAME, null);
            executionCommand.resultConfig.resultFileOutputFormat = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_RESULT_FILE_OUTPUT_FORMAT, null);
            executionCommand.resultConfig.resultFileErrorFormat = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_RESULT_FILE_ERROR_FORMAT, null);
            executionCommand.resultConfig.resultFilesSuffix = IntentUtils.getStringExtraIfSet(intent, TERMUX_SERVICE.EXTRA_RESULT_FILES_SUFFIX, null);
        }

        return executionCommand;
    }

    @Nullable
    AppShell createTermuxTask(String executablePath, String[] arguments, String stdin, String workingDirectory) {
        return createTermuxTask(new ExecutionCommand(TermuxShellManager.getNextShellId(), executablePath,
            arguments, stdin, workingDirectory, Runner.APP_SHELL.getName(), false));
    }

    @Nullable
    synchronized AppShell createTermuxTask(ExecutionCommand executionCommand) {
        if (executionCommand == null) return null;

        Logger.logDebug(LOG_TAG, "Creating \"" + executionCommand.getCommandIdAndLabelLogString() + "\" TermuxTask");

        if (!Runner.APP_SHELL.equalsRunner(executionCommand.runner)) {
            Logger.logDebug(LOG_TAG, "Ignoring wrong runner \"" + executionCommand.runner + "\" command passed to createTermuxTask()");
            return null;
        }

        executionCommand.setShellCommandShellEnvironment = true;

        if (Logger.getLogLevel() >= Logger.LOG_LEVEL_VERBOSE) {
            Logger.logVerboseExtended(LOG_TAG, executionCommand.toString());
        }

        AppShell newTermuxTask = AppShell.execute(mService, executionCommand, this,
            new TermuxShellEnvironment(), null, false);
        if (newTermuxTask == null) {
            Logger.logError(LOG_TAG, "Failed to execute new TermuxTask command for:\n" + executionCommand.getCommandIdAndLabelLogString());
            if (executionCommand.isPluginExecutionCommand) {
                TermuxPluginUtils.processPluginExecutionCommandError(mService, LOG_TAG, executionCommand, false);
            } else {
                Logger.logError(LOG_TAG, "Set log level to debug or higher to see error in logs");
                Logger.logErrorPrivateExtended(LOG_TAG, executionCommand.toString());
            }
            return null;
        }

        mShellManager.mTermuxTasks.add(newTermuxTask);
        if (executionCommand.isPluginExecutionCommand) {
            mShellManager.mPendingPluginExecutionCommands.remove(executionCommand);
        }

        updateNotification();
        return newTermuxTask;
    }

    @Override
    public void onAppShellExited(final AppShell termuxTask) {
        mHandler.post(() -> {
            if (termuxTask != null) {
                ExecutionCommand executionCommand = termuxTask.getExecutionCommand();
                Logger.logVerbose(LOG_TAG, "The onTermuxTaskExited() callback called for \"" + executionCommand.getCommandIdAndLabelLogString() + "\" TermuxTask command");

                if (executionCommand != null && executionCommand.isPluginExecutionCommand) {
                    TermuxPluginUtils.processPluginExecutionCommandResult(mService, LOG_TAG, executionCommand);
                }

                mShellManager.mTermuxTasks.remove(termuxTask);
            }

            updateNotification();
        });
    }

    @Nullable
    TermuxSession createTermuxSession(String executablePath, String[] arguments, String stdin,
                                      String workingDirectory, boolean isFailSafe, String sessionName) {
        ExecutionCommand executionCommand = new ExecutionCommand(TermuxShellManager.getNextShellId(),
            executablePath, arguments, stdin, workingDirectory, Runner.TERMINAL_SESSION.getName(), isFailSafe);
        executionCommand.shellName = sessionName;
        return createTermuxSession(executionCommand);
    }

    @Nullable
    synchronized TermuxSession createTermuxSession(ExecutionCommand executionCommand) {
        if (executionCommand == null) return null;

        Logger.logDebug(LOG_TAG, "Creating \"" + executionCommand.getCommandIdAndLabelLogString() + "\" TermuxSession");

        if (!Runner.TERMINAL_SESSION.equalsRunner(executionCommand.runner)) {
            Logger.logDebug(LOG_TAG, "Ignoring wrong runner \"" + executionCommand.runner + "\" command passed to createTermuxSession()");
            return null;
        }

        executionCommand.setShellCommandShellEnvironment = true;
        executionCommand.terminalTranscriptRows = mProperties.getTerminalTranscriptRows();

        if (Logger.getLogLevel() >= Logger.LOG_LEVEL_VERBOSE) {
            Logger.logVerboseExtended(LOG_TAG, executionCommand.toString());
        }

        TermuxSession newTermuxSession = TermuxSession.execute(mService, executionCommand,
            getTermuxTerminalSessionClient(), this, new TermuxShellEnvironment(), null,
            executionCommand.isPluginExecutionCommand);
        if (newTermuxSession == null) {
            Logger.logError(LOG_TAG, "Failed to execute new TermuxSession command for:\n" + executionCommand.getCommandIdAndLabelLogString());
            if (executionCommand.isPluginExecutionCommand) {
                TermuxPluginUtils.processPluginExecutionCommandError(mService, LOG_TAG, executionCommand, false);
            } else {
                Logger.logError(LOG_TAG, "Set log level to debug or higher to see error in logs");
                Logger.logErrorPrivateExtended(LOG_TAG, executionCommand.toString());
            }
            return null;
        }

        mShellManager.mTermuxSessions.add(newTermuxSession);
        if (executionCommand.isPluginExecutionCommand) {
            mShellManager.mPendingPluginExecutionCommands.remove(executionCommand);
        }

        if (mTermuxTerminalSessionActivityClient != null) {
            mTermuxTerminalSessionActivityClient.termuxSessionListNotifyUpdated();
        }

        updateNotification();
        TermuxActivity.updateTermuxActivityStyling(mService, false);
        return newTermuxSession;
    }

    @Override
    public void onTermuxSessionExited(final TermuxSession termuxSession) {
        if (termuxSession != null) {
            ExecutionCommand executionCommand = termuxSession.getExecutionCommand();
            Logger.logVerbose(LOG_TAG, "The onTermuxSessionExited() callback called for \"" + executionCommand.getCommandIdAndLabelLogString() + "\" TermuxSession command");

            if (executionCommand != null && executionCommand.isPluginExecutionCommand) {
                TermuxPluginUtils.processPluginExecutionCommandResult(mService, LOG_TAG, executionCommand);
            }

            mShellManager.mTermuxSessions.remove(termuxSession);

            if (mTermuxTerminalSessionActivityClient != null) {
                mTermuxTerminalSessionActivityClient.termuxSessionListNotifyUpdated();
            }
        }

        updateNotification();
    }

    synchronized int removeTermuxSession(TerminalSession sessionToRemove) {
        int index = getIndexOfSession(sessionToRemove);
        if (index >= 0) {
            mShellManager.mTermuxSessions.get(index).finish();
        }
        return index;
    }

    private synchronized void killAllTermuxExecutionCommands() {
        boolean processResult;

        Logger.logDebug(LOG_TAG, "Killing TermuxSessions=" + mShellManager.mTermuxSessions.size() +
            ", TermuxTasks=" + mShellManager.mTermuxTasks.size() +
            ", PendingPluginExecutionCommands=" + mShellManager.mPendingPluginExecutionCommands.size());

        List<TermuxSession> termuxSessions = new ArrayList<>(mShellManager.mTermuxSessions);
        List<AppShell> termuxTasks = new ArrayList<>(mShellManager.mTermuxTasks);
        List<ExecutionCommand> pendingPluginExecutionCommands = new ArrayList<>(mShellManager.mPendingPluginExecutionCommands);

        for (int i = 0; i < termuxSessions.size(); i++) {
            ExecutionCommand executionCommand = termuxSessions.get(i).getExecutionCommand();
            processResult = mWantsToStop || executionCommand.isPluginExecutionCommandWithPendingResult();
            termuxSessions.get(i).killIfExecuting(mService, processResult);
            if (!processResult) {
                mShellManager.mTermuxSessions.remove(termuxSessions.get(i));
            }
        }

        for (int i = 0; i < termuxTasks.size(); i++) {
            ExecutionCommand executionCommand = termuxTasks.get(i).getExecutionCommand();
            if (executionCommand.isPluginExecutionCommandWithPendingResult()) {
                termuxTasks.get(i).killIfExecuting(mService, true);
            } else {
                mShellManager.mTermuxTasks.remove(termuxTasks.get(i));
            }
        }

        for (int i = 0; i < pendingPluginExecutionCommands.size(); i++) {
            ExecutionCommand executionCommand = pendingPluginExecutionCommands.get(i);
            if (!executionCommand.shouldNotProcessResults() && executionCommand.isPluginExecutionCommandWithPendingResult()) {
                if (executionCommand.setStateFailed(Errno.ERRNO_CANCELLED.getCode(),
                    mService.getString(com.termux.shared.R.string.error_execution_cancelled))) {
                    TermuxPluginUtils.processPluginExecutionCommandResult(mService, LOG_TAG, executionCommand);
                }
            }
        }
    }

    private ShellCreateMode processShellCreateMode(@NonNull ExecutionCommand executionCommand) {
        if (ShellCreateMode.ALWAYS.equalsMode(executionCommand.shellCreateMode)) {
            return ShellCreateMode.ALWAYS;
        } else if (ShellCreateMode.NO_SHELL_WITH_NAME.equalsMode(executionCommand.shellCreateMode)) {
            if (DataUtils.isNullOrEmpty(executionCommand.shellName)) {
                TermuxPluginUtils.setAndProcessPluginExecutionCommandError(mService, LOG_TAG, executionCommand, false,
                    mService.getString(R.string.error_termux_service_execution_command_shell_name_unset, executionCommand.shellCreateMode));
                return null;
            }
            return ShellCreateMode.NO_SHELL_WITH_NAME;
        } else {
            TermuxPluginUtils.setAndProcessPluginExecutionCommandError(mService, LOG_TAG, executionCommand, false,
                mService.getString(R.string.error_termux_service_unsupported_execution_command_shell_create_mode, executionCommand.shellCreateMode));
            return null;
        }
    }

    private <T> T createOrReuseShellCommand(@NonNull ExecutionCommand executionCommand,
                                            @NonNull Function<String, T> shellLookup,
                                            @NonNull Function<ExecutionCommand, T> creator,
                                            @NonNull String shellTypeLabel) {
        if (executionCommand.shellName == null && executionCommand.executable != null) {
            executionCommand.shellName = ShellUtils.getExecutableBasename(executionCommand.executable);
        }

        ShellCreateMode shellCreateMode = processShellCreateMode(executionCommand);
        if (shellCreateMode == null) {
            return null;
        }

        T shell = null;
        if (ShellCreateMode.NO_SHELL_WITH_NAME.equals(shellCreateMode)) {
            shell = shellLookup.apply(executionCommand.shellName);
            if (shell != null) {
                Logger.logVerbose(LOG_TAG, "Existing " + shellTypeLabel + " with \"" + executionCommand.shellName + "\" shell name found for shell create mode \"" + shellCreateMode.getMode() + "\"");
            } else {
                Logger.logVerbose(LOG_TAG, "No existing " + shellTypeLabel + " with \"" + executionCommand.shellName + "\" shell name found for shell create mode \"" + shellCreateMode.getMode() + "\"");
            }
        }

        if (shell == null) {
            shell = creator.apply(executionCommand);
        }

        return shell;
    }

    private void executeTermuxTaskCommand(ExecutionCommand executionCommand) {
        if (executionCommand == null) return;

        Logger.logDebug(LOG_TAG, "Executing background \"" + executionCommand.getCommandIdAndLabelLogString() + "\" TermuxTask command");
        createOrReuseShellCommand(executionCommand, shellName -> getTermuxTaskForShellName(shellName), this::createTermuxTask, "TermuxTask");
    }

    private void executeTermuxSessionCommand(ExecutionCommand executionCommand) {
        if (executionCommand == null) return;

        Logger.logDebug(LOG_TAG, "Executing foreground \"" + executionCommand.getCommandIdAndLabelLogString() + "\" TermuxSession command");
        TermuxSession newTermuxSession = createOrReuseShellCommand(executionCommand, shellName -> getTermuxSessionForShellName(shellName), this::createTermuxSession, "TermuxSession");
        if (newTermuxSession == null) return;

        handleSessionAction(DataUtils.getIntFromString(executionCommand.sessionAction,
            TERMUX_SERVICE.VALUE_EXTRA_SESSION_ACTION_SWITCH_TO_NEW_SESSION_AND_OPEN_ACTIVITY),
            newTermuxSession.getTerminalSession());
    }

    private void handleSessionAction(int sessionAction, TerminalSession newTerminalSession) {
        Logger.logDebug(LOG_TAG, "Processing sessionAction \"" + sessionAction + "\" for session \"" + newTerminalSession.mSessionName + "\"");

        switch (sessionAction) {
            case TERMUX_SERVICE.VALUE_EXTRA_SESSION_ACTION_SWITCH_TO_NEW_SESSION_AND_OPEN_ACTIVITY:
                setCurrentStoredTerminalSession(newTerminalSession);
                if (mTermuxTerminalSessionActivityClient != null) {
                    mTermuxTerminalSessionActivityClient.setCurrentSession(newTerminalSession);
                }
                startTermuxActivity();
                break;
            case TERMUX_SERVICE.VALUE_EXTRA_SESSION_ACTION_KEEP_CURRENT_SESSION_AND_OPEN_ACTIVITY:
                if (getTermuxSessionsSize() == 1) {
                    setCurrentStoredTerminalSession(newTerminalSession);
                }
                startTermuxActivity();
                break;
            case TERMUX_SERVICE.VALUE_EXTRA_SESSION_ACTION_SWITCH_TO_NEW_SESSION_AND_DONT_OPEN_ACTIVITY:
                setCurrentStoredTerminalSession(newTerminalSession);
                if (mTermuxTerminalSessionActivityClient != null) {
                    mTermuxTerminalSessionActivityClient.setCurrentSession(newTerminalSession);
                }
                break;
            case TERMUX_SERVICE.VALUE_EXTRA_SESSION_ACTION_KEEP_CURRENT_SESSION_AND_DONT_OPEN_ACTIVITY:
                if (getTermuxSessionsSize() == 1) {
                    setCurrentStoredTerminalSession(newTerminalSession);
                }
                break;
            default:
                Logger.logError(LOG_TAG, "Invalid sessionAction: \"" + sessionAction + "\". Force using default sessionAction.");
                handleSessionAction(TERMUX_SERVICE.VALUE_EXTRA_SESSION_ACTION_SWITCH_TO_NEW_SESSION_AND_OPEN_ACTIVITY, newTerminalSession);
                break;
        }
    }

    private void startTermuxActivity() {
        if (PermissionUtils.validateDisplayOverOtherAppsPermissionForPostAndroid10(mService, true)) {
            TermuxActivity.startTermuxActivity(mService);
        } else {
            TermuxAppSharedPreferences preferences = TermuxAppSharedPreferences.build(mService);
            if (preferences == null) return;
            if (preferences.arePluginErrorNotificationsEnabled(false)) {
                Logger.showToast(mService, mService.getString(R.string.error_display_over_other_apps_permission_not_granted_to_start_terminal), true);
            }
        }
    }

    private void requestStopService() {
        Logger.logDebug(LOG_TAG, "Requesting to stop service");
        runStopForeground();
        mService.stopSelf();
    }

    private void runStartForeground() {
        setupNotificationChannel();
        mService.startForeground(TermuxConstants.TERMUX_APP_NOTIFICATION_ID, buildNotification());
    }

    private void runStopForeground() {
        mService.stopForeground(true);
    }

    private Notification buildNotification() {
        Resources res = mService.getResources();
        Intent notificationIntent = TermuxActivity.newInstance(mService);
        PendingIntent contentIntent = PendingIntent.getActivity(mService, 0, notificationIntent, 0);

        int sessionCount = getTermuxSessionsSize();
        int taskCount = mShellManager.mTermuxTasks.size();
        String notificationText = sessionCount + " session" + (sessionCount == 1 ? "" : "s");
        if (taskCount > 0) {
            notificationText += ", " + taskCount + " task" + (taskCount == 1 ? "" : "s");
        }

        final boolean wakeLockHeld = mWakeLock != null;
        if (wakeLockHeld) {
            notificationText += " (wake lock held)";
        }

        int priority = (wakeLockHeld) ? Notification.PRIORITY_HIGH : Notification.PRIORITY_LOW;
        Notification.Builder builder = NotificationUtils.geNotificationBuilder(mService,
            TermuxConstants.TERMUX_APP_NOTIFICATION_CHANNEL_ID, priority,
            TermuxConstants.TERMUX_APP_NAME, notificationText, null,
            contentIntent, null, NotificationUtils.NOTIFICATION_MODE_SILENT);
        if (builder == null) return null;

        builder.setShowWhen(false);
        builder.setSmallIcon(R.drawable.ic_service_notification);
        builder.setColor(0xFF607D8B);
        builder.setOngoing(true);

        Intent exitIntent = new Intent(mService, TermuxService.class).setAction(TERMUX_SERVICE.ACTION_STOP_SERVICE);
        builder.addAction(android.R.drawable.ic_delete, res.getString(R.string.notification_action_exit), PendingIntent.getService(mService, 0, exitIntent, 0));

        String newWakeAction = wakeLockHeld ? TERMUX_SERVICE.ACTION_WAKE_UNLOCK : TERMUX_SERVICE.ACTION_WAKE_LOCK;
        Intent toggleWakeLockIntent = new Intent(mService, TermuxService.class).setAction(newWakeAction);
        String actionTitle = res.getString(wakeLockHeld ? R.string.notification_action_wake_unlock : R.string.notification_action_wake_lock);
        int actionIcon = wakeLockHeld ? android.R.drawable.ic_lock_idle_lock : android.R.drawable.ic_lock_lock;
        builder.addAction(actionIcon, actionTitle, PendingIntent.getService(mService, 0, toggleWakeLockIntent, 0));

        return builder.build();
    }

    private void setupNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) return;

        NotificationUtils.setupNotificationChannel(mService, TermuxConstants.TERMUX_APP_NOTIFICATION_CHANNEL_ID,
            TermuxConstants.TERMUX_APP_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
    }

    private synchronized void updateNotification() {
        if (mWakeLock == null && mShellManager.mTermuxSessions.isEmpty() && mShellManager.mTermuxTasks.isEmpty()) {
            requestStopService();
        } else {
            ((NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
                TermuxConstants.TERMUX_APP_NOTIFICATION_ID, buildNotification());
        }
    }

    private void setCurrentStoredTerminalSession(TerminalSession terminalSession) {
        if (terminalSession == null) return;
        TermuxAppSharedPreferences preferences = TermuxAppSharedPreferences.build(mService);
        if (preferences == null) return;
        preferences.setCurrentSession(terminalSession.mHandle);
    }

    synchronized boolean isTermuxSessionsEmpty() {
        return mShellManager.mTermuxSessions.isEmpty();
    }

    synchronized int getTermuxSessionsSize() {
        return mShellManager.mTermuxSessions.size();
    }

    synchronized List<TermuxSession> getTermuxSessions() {
        return mShellManager.mTermuxSessions;
    }

    @Nullable
    synchronized TermuxSession getTermuxSession(int index) {
        if (index >= 0 && index < mShellManager.mTermuxSessions.size()) {
            return mShellManager.mTermuxSessions.get(index);
        }
        return null;
    }

    @Nullable
    synchronized TermuxSession getTermuxSessionForTerminalSession(TerminalSession terminalSession) {
        if (terminalSession == null) return null;

        for (int i = 0; i < mShellManager.mTermuxSessions.size(); i++) {
            if (mShellManager.mTermuxSessions.get(i).getTerminalSession().equals(terminalSession)) {
                return mShellManager.mTermuxSessions.get(i);
            }
        }
        return null;
    }

    @Nullable
    synchronized TermuxSession getLastTermuxSession() {
        return mShellManager.mTermuxSessions.isEmpty() ? null : mShellManager.mTermuxSessions.get(mShellManager.mTermuxSessions.size() - 1);
    }

    synchronized int getIndexOfSession(TerminalSession terminalSession) {
        if (terminalSession == null) return -1;

        for (int i = 0; i < mShellManager.mTermuxSessions.size(); i++) {
            if (mShellManager.mTermuxSessions.get(i).getTerminalSession().equals(terminalSession)) {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    synchronized TerminalSession getTerminalSessionForHandle(String sessionHandle) {
        for (int i = 0, len = mShellManager.mTermuxSessions.size(); i < len; i++) {
            TerminalSession terminalSession = mShellManager.mTermuxSessions.get(i).getTerminalSession();
            if (terminalSession.mHandle.equals(sessionHandle)) {
                return terminalSession;
            }
        }
        return null;
    }

    @Nullable
    synchronized AppShell getTermuxTaskForShellName(String name) {
        if (DataUtils.isNullOrEmpty(name)) return null;
        for (int i = 0, len = mShellManager.mTermuxTasks.size(); i < len; i++) {
            AppShell appShell = mShellManager.mTermuxTasks.get(i);
            String shellName = appShell.getExecutionCommand().shellName;
            if (shellName != null && shellName.equals(name)) {
                return appShell;
            }
        }
        return null;
    }

    @Nullable
    synchronized TermuxSession getTermuxSessionForShellName(String name) {
        if (DataUtils.isNullOrEmpty(name)) return null;
        for (int i = 0, len = mShellManager.mTermuxSessions.size(); i < len; i++) {
            TermuxSession termuxSession = mShellManager.mTermuxSessions.get(i);
            String shellName = termuxSession.getExecutionCommand().shellName;
            if (shellName != null && shellName.equals(name)) {
                return termuxSession;
            }
        }
        return null;
    }

    synchronized TermuxTerminalSessionClientBase getTermuxTerminalSessionClient() {
        if (mTermuxTerminalSessionActivityClient != null) {
            return mTermuxTerminalSessionActivityClient;
        }
        return mTermuxTerminalSessionServiceClient;
    }

    synchronized void setTermuxTerminalSessionClient(TermuxTerminalSessionActivityClient termuxTerminalSessionActivityClient) {
        mTermuxTerminalSessionActivityClient = termuxTerminalSessionActivityClient;

        for (int i = 0; i < mShellManager.mTermuxSessions.size(); i++) {
            mShellManager.mTermuxSessions.get(i).getTerminalSession().updateTerminalSessionClient(mTermuxTerminalSessionActivityClient);
        }
    }

    synchronized void unsetTermuxTerminalSessionClient() {
        for (int i = 0; i < mShellManager.mTermuxSessions.size(); i++) {
            mShellManager.mTermuxSessions.get(i).getTerminalSession().updateTerminalSessionClient(mTermuxTerminalSessionServiceClient);
        }
        mTermuxTerminalSessionActivityClient = null;
    }

    boolean wantsToStop() {
        return mWantsToStop;
    }

    private static final String LOG_TAG = "TermuxService";
}
