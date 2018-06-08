package unifar.unifar.readlight2;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.ads.MobileAds;

/**
 * Created by 三悟 on 2017/10/17.
 */

public class MyApplication extends Application {
    public static int adCount ;
    private static MyApplication context;
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, "ca-app-pub-6418178360564076~2300294432");
        adCount = 0;
        context = this;
    }
    public static MyApplication getInstance() {
        return context;
    }
}
