package com.eassychat;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class BaseActivity extends AppCompatActivity {
    private Vibrator v;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public void initViberator() {
    v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
    }


    public void viberate(){
        if (v == null) initViberator();
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(30);
        }
    }

    public void toast(Context context, String data ){
        Toast.makeText(context, ""+data, Toast.LENGTH_SHORT).show();

    }
}
