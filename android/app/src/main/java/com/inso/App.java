package com.inso;

import android.app.Application;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.inso.example.Hybrid.HybridPackage;
import com.inuker.bluetooth.library.BluetoothContext;

import java.util.Arrays;
import java.util.List;

public class App extends Application implements ReactApplication {
    private static App instance;
    public static Application getInstance() {
        return instance;
    }
    public static HybridPackage hybridPackage =  new HybridPackage();
    private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
            return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    hybridPackage
            );
        }

        @Override
        protected String getJSMainModuleName() {
            return "index";
        }
    };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        BluetoothContext.set(this);
        SoLoader.init(this, /* native exopackage */ false);

    }
}
