package tcc.ronaldoyoshio.playingcards.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;

import tcc.ronaldoyoshio.playingcards.service.GamePlayerService;
import tcc.ronaldoyoshio.playingcards.service.GameServerService;


// Referência: https://www.hrupin.com/2011/11/how-to-clear-user-data-in-your-android-application-programmatically
public class PlayingCardsApplication extends Application {
    private static final String TAG = "PlayingCardsApplication";

    private static PlayingCardsApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static PlayingCardsApplication getInstance(){
        return instance;
    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if(appDir.exists()){
            String[] children = appDir.list();
            for(String s : children){
                if(!s.equals("lib")){
                    deleteDir(new File(appDir, s));
                    Log.i(TAG, "File /data/data/APP_PACKAGE/" + s +" DELETED");
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void stopServices() {
        if (isMyServiceRunning(GamePlayerService.class)) {
            Intent intent = new Intent(this, GamePlayerService.class);
            stopService(intent);
            PackageManager pManager = this.getPackageManager();
            pManager.setComponentEnabledSetting(new ComponentName(getApplicationContext(), GamePlayerService.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        }

        if (isMyServiceRunning(GameServerService.class)) {
            Intent intent = new Intent(this, GameServerService.class);
            stopService(intent);
            PackageManager pManager = this.getPackageManager();
            pManager.setComponentEnabledSetting(new ComponentName(getApplicationContext(), GameServerService.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        }
    }
}
