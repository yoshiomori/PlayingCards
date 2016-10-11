package tcc.ronaldoyoshio.playingcards.application;

import android.app.Application;
import android.util.Log;

import java.io.File;


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
}
