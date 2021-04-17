package com.eassychat.utils;

import android.app.Application;

import com.eassychat.R;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.paperdb.Paper;


public class ApplicationClass extends Application {
    private static ApplicationClass instance;

    public ApplicationClass() {
    }
    @Override
    public void onCreate() {
        super.onCreate();
        initApplication();
        set_Calligraphy();
        Paper.init(this);

    }

    /**
     * set font calligraphy
     */
    private void set_Calligraphy() {
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
    }


    private void initApplication() {
        instance = this;
    }

    public static ApplicationClass getInstance() {
        return instance;
    }
}
