package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import com.example.myapplication.PermissionUtils;
import android.shortcut.ShortcutInfo;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    boolean isBottom = true;
    ViewPager mViewPager;
    int cellHeight;

    int NUMBER_OF_ROWS = 5;
    int NUMBER_OF_COLUMNS = 1;
    int DRAWER_PEEK_HEIGHT = 100;

    // Define the request code for permission request
    private static final int REQUEST_CODE_PACKAGE_USAGE_STATS = 1; // You can use any unique integer

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PACKAGE_USAGE_STATS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, access package usage data
                // (Code to access package usage stats goes here)
            } else {
                // Permission denied, handle gracefully
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                // Provide alternative functionality or explanation (optional)
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Hide the ActionBar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        PermissionUtils.checkAndRequestPackageUsageStats(this, REQUEST_CODE_PACKAGE_USAGE_STATS);

        initializeHome();
        initializeDrawer();
    }
    ViewPagerAdapter mViewPagerAdapter;

    private void initializeHome() {
        ArrayList<PagerObject> pagerAppList = new ArrayList<>();
        ArrayList<AppObject> appList = new ArrayList<>();
        for (int i = 0; i < 20; i++)
            appList.add(new AppObject("", "", getResources().getDrawable(R.drawable.ic_launcher_foreground)));

        pagerAppList.add(new PagerObject(appList));
        pagerAppList.add(new PagerObject(appList));

        cellHeight = (getDisplayContentHeight() - DRAWER_PEEK_HEIGHT) / NUMBER_OF_ROWS;

        mViewPager = findViewById(R.id.viewPager);
        mViewPagerAdapter = new ViewPagerAdapter(this, pagerAppList, cellHeight);
        mViewPager.setAdapter(mViewPagerAdapter);
    }

    List<AppObject> installedAppList = new ArrayList<>();
    GridView mDrawerGridView;
    BottomSheetBehavior mBottomSheetBehavior;

    private void initializeDrawer() {
        View mBottomSheet = findViewById(R.id.bottomSheet);
        mDrawerGridView = findViewById(R.id.drawerGrid);
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);

        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setHideable(false);
            mBottomSheetBehavior.setPeekHeight(DRAWER_PEEK_HEIGHT);

            installedAppList = getInstalledAppList();
            mDrawerGridView.setAdapter(new AppAdapter(getApplicationContext(), installedAppList, cellHeight));

            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (mAppDrag != null)
                        return;
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED && mDrawerGridView.getChildAt(0).getY() != 0)
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    if (newState == BottomSheetBehavior.STATE_DRAGGING && mDrawerGridView.getChildAt(0).getY() != 0)
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            });
        }
    }

    AppObject mAppDrag = null;

    public void itemPress(AppObject app) {
        if (mAppDrag != null) {
            app.setPackageName(mAppDrag.getPackageName());
            app.setName(mAppDrag.getName());
            app.setImage(mAppDrag.getImage());
            mAppDrag = null;
            mViewPagerAdapter.notifyGridChanged();
            return;
        } else {
            Intent launchAppIntent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(app.getPackageName());
            if (launchAppIntent != null)
                getApplicationContext().startActivity(launchAppIntent);
        }
    }

    public void itemLongPress(AppObject app) {
        collapseDrawer();
        mAppDrag = app;
    }

    public void collapseDrawer() {
        mDrawerGridView.setY(0);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private List<AppObject> getInstalledAppList() {
        List<AppObject> list = new ArrayList<>();

        PackageManager packageManager = getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> untreatedAppList = packageManager.queryIntentActivities(intent, 0);

        for (ResolveInfo untreatedapp : untreatedAppList) {
            String appName = untreatedapp.activityInfo.loadLabel(packageManager).toString();
            String appPackageName = untreatedapp.activityInfo.packageName;
            Drawable appImage = untreatedapp.activityInfo.loadIcon(packageManager);

            AppObject app = new AppObject(appPackageName, appName, appImage);
            if (!list.contains(app))
                list.add(app);
        }

        return list;
    }

    private int getDisplayContentHeight() {
        final WindowManager windowManager = getWindowManager();
        final Point size = new Point();
        int screenHeight = 0, actionBarHeight = 0, statusBarHeight = 0;
        if (getActionBar() != null) {
            actionBarHeight = getActionBar().getHeight();
        }
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        int contentTop = (findViewById(android.R.id.content)).getTop();
        windowManager.getDefaultDisplay().getSize(size);
        screenHeight = size.y;
        return screenHeight - contentTop - actionBarHeight - statusBarHeight;
    }
}
