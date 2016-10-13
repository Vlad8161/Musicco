package mmss.musicco;

import android.app.Application;

/**
 * Created by User on 13.10.2016.
 */

public class App extends Application {

    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this.getApplicationContext()))
                .build();
    }

    public static AppComponent getApp() {
        return appComponent;
    }
}
