package com.custom.glasswidget;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private TextView txtClock, txtDate, txtBattery, txtRam;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtClock = findViewById(R.id.txt_clock);
        txtDate = findViewById(R.id.txt_date);
        txtBattery = findViewById(R.id.txt_battery);
        txtRam = findViewById(R.id.txt_ram);

        updateTask = new Runnable() {
            @Override
            public void run() {
                refreshData();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateTask);
    }

    private void refreshData() {
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE, dd 'Tháng' MM, yyyy", new Locale("vi", "VN"));
        Date now = new Date();
        txtClock.setText(sdfTime.format(now));
        txtDate.setText(sdfDate.format(now));

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        if (batteryStatus != null) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int pct = (int) ((level * 100f) / scale);
            txtBattery.setText(pct + "%");
        }

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (am != null) {
            am.getMemoryInfo(mi);
            double availGb = mi.availMem / 1073741824.0;
            double totalGb = mi.totalMem / 1073741824.0;
            double usedGb = totalGb - availGb;
            txtRam.setText(String.format(Locale.US, "%.1f / %.1f GB", usedGb, totalGb));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTask);
    }
}
