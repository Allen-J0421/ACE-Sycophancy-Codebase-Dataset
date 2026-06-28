package com.termux.app.terminal;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.termux.app.TermuxService;
import com.termux.app.terminal.io.TermuxTerminalExtraKeys;
import com.termux.shared.termux.extrakeys.ExtraKeysView;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;
import com.termux.shared.termux.settings.properties.TermuxAppSharedProperties;
import com.termux.terminal.TerminalSession;
import com.termux.view.TerminalView;

public interface TermuxTerminalViewClientHost {

    Activity getActivity();

    TerminalView getTerminalView();

    TermuxAppSharedPreferences getPreferences();

    TermuxAppSharedProperties getProperties();

    TermuxActivityRootView getTermuxActivityRootView();

    @Nullable
    ExtraKeysView getExtraKeysView();

    TermuxTerminalExtraKeys getTermuxTerminalExtraKeys();

    DrawerLayout getDrawer();

    ViewPager getTerminalToolbarViewPager();

    boolean isTerminalViewSelected();

    boolean isTerminalToolbarTextInputViewSelected();

    boolean isVisible();

    boolean isOnResumeAfterOnCreate();

    boolean isActivityRecreated();

    void setExtraKeysView(ExtraKeysView extraKeysView);

    void toggleTerminalToolbar();

    void finishActivityIfNotFinishing();

    void showToast(String text, boolean longDuration);

    @Nullable
    TerminalSession getCurrentSession();

    @Nullable
    TermuxService getTermuxService();

    TermuxTerminalSessionActivityClient getTermuxTerminalSessionClient();
}
