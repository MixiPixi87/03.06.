package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.Drawable;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.ActivityInfo;
import java.util.List;
import android.content.ComponentName;
import android.content.pm.ShortcutManager;
import android.os.Build;
import android.shortcut.ShortcutInfo;
import android.shortcut.ShortcutManager;


public class AppAdapter extends BaseAdapter {
    Context context;
    List<AppObject> appList;
    int cellHeight;


    public AppAdapter(Context context, List<AppObject> appList, int cellHeight) {
        this.context = context;
        this.appList = appList;
        this.cellHeight = cellHeight;
    }

    @Override
    public int getCount()
    {
        return appList.size();}

    @Override
    public Object getItem(int position) {
        return appList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
       View view;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.item_app, parent, false);
        }
        else {
            view = convertView;
        }

        LinearLayout mLayout = view.findViewById(R.id.Layout);
        ImageView mImage = view.findViewById(R.id.image);
        TextView mLabel = view.findViewById(R.id.label);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, cellHeight);
        mLayout.setLayoutParams(lp);

        AppObject appObject = appList.get(position);
        mImage.setImageDrawable(appObject.getImage());
        mLabel.setText(appObject.getName());

        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof MainActivity) {
                    ((MainActivity) context).itemPress(appList.get(position));
                }
            }
        });

        mLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (context instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.collapseDrawer();
                    mainActivity.mAppDrag = appList.get(position);

                    // Get information for the shortcut
                    String packageName = appList.get(position).getPackageName();
                    String appName = appList.get(position).getName();
                    Drawable appIcon = appList.get(position).getImage();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        // Code for creating shortcuts (API level 25+)
                        PackageManager packageManager = context.getPackageManager();
                        String launcherActivityName = null;
                        try {
                            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                            if (packageInfo.activities != null) {
                                for (ActivityInfo activityInfo : packageInfo.activities) {
                                    launcherActivityName = activityInfo.name; // Directly access the name
                                    break; // Exit the loop after getting the first activity (assuming it's the launcher)
                                }
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                            // Handle exception if package is not found
                        }

                        Intent launchAppIntent = new Intent(Intent.ACTION_MAIN);
                        launchAppIntent.setComponent(new ComponentName(packageName, launcherActivityName));

                        ShortcutManager shortcutManager = mainActivity.getSystemService(ShortcutManager.class);

                        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(mainActivity, packageName)
                                .setActivity(launchAppIntent)
                                .setIcon(appIcon)
                                .setTitle(appName)
                                .build();

                        shortcutManager.addShortcut(shortcutInfo, null, null);

                    } else {
                        // Handle cases where shortcut creation is not supported (e.g., display a toast message)
                        Toast.makeText(context, "Shortcut creation not supported on this device", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });return view;}}
