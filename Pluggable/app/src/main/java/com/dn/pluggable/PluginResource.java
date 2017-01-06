package com.dn.pluggable;

import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Created by leon on 17/1/3.
 */

public class PluginResource extends Resources {

    /**
     * Create a new Resources object on top of an existing set of assets in an
     * AssetManager.
     *
     * @param assets  Previously created AssetManager.
     * @param metrics Current display metrics to consider when
     *                selecting/computing resource values.
     * @param config  Desired device configuration to consider when
     */
    public PluginResource(AssetManager assets, DisplayMetrics metrics, Configuration config) {
        super(assets, metrics, config);
    }

    // 偷梁换柱
    public static AssetManager getAssetManager(File file,Resources resources){
        try {
            Class<?> c = Class.forName("android.content.res.AssetManager");
            Method[] methods = c.getDeclaredMethods();
            for (Method method : methods){
                if (method.getName().equals("addAssetPath")){
                    AssetManager assetManager = AssetManager.class.newInstance();
                    method.invoke(assetManager,file.getAbsolutePath());
                    return assetManager;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static PluginResource getPluginResource(AssetManager manager,Resources resources){
        Log.v("PluginResource",resources.getConfiguration().toString());
        return new PluginResource(manager,resources.getDisplayMetrics(),resources.getConfiguration());
    }

}
