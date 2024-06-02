package com.example.myapplication;

import android.app.Activity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;


public class PermissionUtils {
    public static void checkAndRequestPackageUsageStats(Activity activity, int requestCode) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.PACKAGE_USAGE_STATS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.PACKAGE_USAGE_STATS}, requestCode);
        }
    }

}

