package com.termux.app.terminal;

import android.app.AlertDialog;
import android.os.Environment;
import android.widget.ListView;

import com.termux.R;
import com.termux.app.models.UserAction;
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
import com.termux.shared.termux.TermuxUtils;
import com.termux.shared.termux.data.TermuxUrlUtils;
import com.termux.terminal.TerminalSession;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;

final class TermuxTerminalViewActionHelper {

    private final TermuxTerminalViewClientHost mHost;

    TermuxTerminalViewActionHelper(TermuxTerminalViewClientHost host) {
        mHost = host;
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
}
