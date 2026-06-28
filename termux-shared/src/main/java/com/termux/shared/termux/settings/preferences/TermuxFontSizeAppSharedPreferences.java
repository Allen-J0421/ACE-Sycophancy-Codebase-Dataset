package com.termux.shared.termux.settings.preferences;

import android.content.Context;
import android.util.TypedValue;

import androidx.annotation.NonNull;

import com.termux.shared.data.DataUtils;
import com.termux.shared.settings.preferences.SharedPreferenceUtils;

abstract class TermuxFontSizeAppSharedPreferences extends TermuxPackageAppSharedPreferences {

    private final int mMinFontSize;
    private final int mMaxFontSize;
    private final int mDefaultFontSize;

    protected TermuxFontSizeAppSharedPreferences(@NonNull Context context,
                                                 @NonNull String preferencesFileBasename) {
        super(context, preferencesFileBasename);

        int[] sizes = getDefaultFontSizes(context);
        mDefaultFontSize = sizes[0];
        mMinFontSize = sizes[1];
        mMaxFontSize = sizes[2];
    }

    public static int[] getDefaultFontSizes(@NonNull Context context) {
        float dipInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
            context.getResources().getDisplayMetrics());

        int[] sizes = new int[3];

        // This is a bit arbitrary and sub-optimal. We want to give a sensible default for minimum
        // font size to prevent invisible text due to zoom by mistake.
        sizes[1] = (int) (4f * dipInPixels);

        // http://www.google.com/design/spec/style/typography.html#typography-line-height
        int defaultFontSize = Math.round(12 * dipInPixels);
        if (defaultFontSize % 2 == 1) defaultFontSize--;

        sizes[0] = defaultFontSize;
        sizes[2] = 256;

        return sizes;
    }

    protected abstract String getFontSizePreferenceKey();

    public int getFontSize() {
        int fontSize = SharedPreferenceUtils.getIntStoredAsString(mSharedPreferences,
            getFontSizePreferenceKey(), mDefaultFontSize);
        return DataUtils.clamp(fontSize, mMinFontSize, mMaxFontSize);
    }

    public void setFontSize(int value) {
        SharedPreferenceUtils.setIntStoredAsString(mSharedPreferences, getFontSizePreferenceKey(),
            value, false);
    }

    public void changeFontSize(boolean increase) {
        int fontSize = getFontSize();
        fontSize += (increase ? 1 : -1) * 2;
        fontSize = Math.max(mMinFontSize, Math.min(fontSize, mMaxFontSize));
        setFontSize(fontSize);
    }

}
