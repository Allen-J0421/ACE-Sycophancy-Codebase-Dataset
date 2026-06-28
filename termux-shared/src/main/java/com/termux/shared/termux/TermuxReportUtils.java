package com.termux.shared.termux;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.termux.shared.file.FileUtils;
import com.termux.shared.models.ReportInfo;

public final class TermuxReportUtils {

    private TermuxReportUtils() {}

    @NonNull
    public static ReportInfo newReportInfo(@NonNull String userAction, @NonNull String sender,
                                           @NonNull String title) {
        ReportInfo reportInfo = new ReportInfo(userAction, sender, title);
        setDefaultReportSaveFile(reportInfo, userAction);
        return reportInfo;
    }

    public static void setDefaultReportSaveFile(@NonNull ReportInfo reportInfo,
                                                @NonNull String reportSaveFileLabel) {
        reportInfo.setReportSaveFileLabelAndPath(reportSaveFileLabel,
            getDefaultReportSaveFilePath(reportSaveFileLabel));
    }

    @NonNull
    public static String getDefaultReportSaveFilePath(@NonNull String reportSaveFileLabel) {
        return Environment.getExternalStorageDirectory() + "/" +
            FileUtils.sanitizeFileName(TermuxConstants.TERMUX_APP_NAME + "-" +
                reportSaveFileLabel + ".log", true, true);
    }

    public static void setReportIssueMarkdownSuffix(@NonNull Context context,
                                                    @NonNull ReportInfo reportInfo) {
        reportInfo.setReportStringSuffix("\n\n" + TermuxUtils.getReportIssueMarkdownString(context));
    }

}
