package com.jikexueyuan.releasememory;

import android.app.ActivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityManager mActivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infoList = mActivityManager.getRunningAppProcesses();

        //Get the initial available memory
        long beforeMem = getAvailMemory();

        if (infoList != null) {
            for (int i = 0; i < infoList.size(); ++i) {
                ActivityManager.RunningAppProcessInfo appProcessInfo = infoList.get(i);
                if (appProcessInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    String[] pkgList = appProcessInfo.pkgList;
                    for (int j = 0; j < pkgList.length; ++j) {
                        mActivityManager.killBackgroundProcesses(pkgList[j]);
                    }
                }
            }
        }
        //Get the free memory after killing processes
        long afterMem = getAvailMemory();
        String size = android.text.format.Formatter.formatFileSize(this,afterMem - beforeMem);
        Toast.makeText(this, size , Toast.LENGTH_SHORT).show();
        finish();
    }

    // Get the size of available memories right time
    private long getAvailMemory() {
        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        mActivityManager.getMemoryInfo(mi);
        //Return current available memories
        return mi.availMem;
    }
}
