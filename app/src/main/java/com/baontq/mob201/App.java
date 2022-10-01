package com.baontq.mob201;

import android.app.Application;

import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.style.MIUIStyle;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DialogX.init(this);
        DialogX.DEBUGMODE = false;
        DialogX.autoRunOnUIThread = false;
        DialogX.globalTheme = DialogX.THEME.DARK;
//        DialogX.globalStyle = MIUIStyle.style();
    }
}
