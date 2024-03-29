package com.inso.example.Hybrid;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/1/2
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class HybridPackage implements ReactPackage {
    HybridModule mHybridModule ;

    public HybridModule getHybridModule() {
        return mHybridModule;
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        mHybridModule=new HybridModule(reactContext);
        modules.add(mHybridModule);
        return modules;
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }
}
