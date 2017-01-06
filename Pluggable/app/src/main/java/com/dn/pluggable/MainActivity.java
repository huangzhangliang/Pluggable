package com.dn.pluggable;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dn.pluggable.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String CLOUD = "cloud";
    ActivityMainBinding mActivityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        mActivityMainBinding.ivSunny.setOnClickListener(this);
        mActivityMainBinding.ivCloud.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivSunny:
                // 本地
                handleAnim(v);
                break;
            case R.id.ivCloud:
                // 插件
                String fileName = CLOUD + ".apk";
                String filePath = this.getCacheDir() + File.separator + fileName;
                String pakcageName = "com.dn." + CLOUD;
                File file = new File(filePath);
                if (file.exists()){
                    // 插件已经下载
                    Drawable drawable = v.getBackground();
                    if (drawable instanceof  AnimationDrawable){
                        handleAnim(v);
                        return;
                    }

                    // 加载插件
                    AssetManager assetManager = PluginResource.getAssetManager(file,getResources());
                    // 拿到插件的resource
                    Resources resources = PluginResource.getPluginResource(assetManager,getResources());
                    DexClassLoader classLoader = new DexClassLoader(file.getAbsolutePath(),
                            this.getDir(fileName, Context.MODE_PRIVATE).getAbsolutePath(),
                            null,
                            this.getClassLoader()
                    );

                    try {
                        Class<?> loadClass = classLoader.loadClass(pakcageName+".R$drawable");
                        Field field = loadClass.getDeclaredField(CLOUD);
                        int animID = field.getInt(R.anim.class);
                        Drawable animation = resources.getDrawable(animID);
                        v.setBackgroundDrawable(animation);
                        handleAnim(v);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else {
                    // 下载插件
                    try {
                        InputStream is = this.getAssets().open(fileName);
                        OutputStream os = new FileOutputStream(file);
                        int len = 0;
                        byte[] buffer = new byte[1024];
                        while ((len = is.read(buffer)) != -1){
                            os.write(buffer,0,len);
                        }
                        os.close();
                        is.close();
                        // 下载完成
                        Toast.makeText(this,"下载完成",Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

        }
    }

    private void handleAnim(View v) {
        AnimationDrawable drawable = (AnimationDrawable) v.getBackground();
        if (drawable != null){
            if (drawable.isRunning()){
                drawable.stop();
            }else {
                drawable.start();
            }
        }
    }
}
