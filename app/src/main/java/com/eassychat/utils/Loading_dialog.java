package com.eassychat.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;

import com.eassychat.R;


public class Loading_dialog {
    private Dialog dialog;
    private Context context;

    public Loading_dialog(Context context) {
        this.context = context;
    }

    public void showDialog(){
        dialog  = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_dialog);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.alpha(12)));
        dialog.setCancelable(false);
        dialog.show();
    }

    public void hideDialog(){
        if (dialog!=null)
        dialog.dismiss();
    }
}
