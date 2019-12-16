package com.mei.chaji.component;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;


public class ActivityController {
    private static ActivityController activityController;

    public synchronized static ActivityController getInstance() {
        if (activityController == null) {
            activityController = new ActivityController();
        }
        return activityController;
    }

    private Set<Activity> allActivities;

    public void addActivity(Activity act) {
        if (allActivities == null) {
            allActivities = new HashSet<>();
        }
        allActivities.add(act);
    }

    public void removeActivity(Activity act) {
        if (allActivities != null) {
            allActivities.remove(act);
        }
    }

    /**
     * 退出app
     */
    public void exitApp(Context context) {
        if (null != allActivities) {
                for (Activity act : allActivities) {
                    if (act != null) {
                        act.finish();
                    }
                }
        }
        for (Activity activity:allActivities){
            if (activity.isFinishing()){
                ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
                Log.e("exitApp", "exitApp: "+context.getPackageName() );
                manager.killBackgroundProcesses(context.getPackageName());
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }else {
                activity.finish();
            }
        }
    }
}
