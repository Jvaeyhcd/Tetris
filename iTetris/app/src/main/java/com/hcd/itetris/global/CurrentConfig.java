package com.hcd.itetris.global;

import android.content.Context;
import android.content.SharedPreferences;

import com.hcd.itetris.entity.LevelSetFactory;
import com.hcd.itetris.utils.ApplicationUtils;

import java.util.Map;

/**
 * Created by hcd on 14-11-8.
 * Author:Jvaeyhcd
 */
public class CurrentConfig extends DefaultConfig {
    ApplicationUtils app;
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;
    protected Context context;
    protected String configFileName;
    protected Map<String,?> values;

    public CurrentConfig() {
        super();
        configFileName = "itetris";
        context = ApplicationUtils.getInstance().getApplicationContext();
        sharedPreferences = context.getSharedPreferences(configFileName, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
        load();
    }

    private void load() {
        values = sharedPreferences.getAll();
        boolean val = sharedPreferences.contains("show_grid_line");
        if (!val) setShowGridLine(true);
        val = sharedPreferences.contains("show_preview_grid_line");
        if (!val) setShowPreviewGridLine(true);
        val = sharedPreferences.contains("support_colorful_shape");
        if (!val) setSupportColorfulShape(true);
        val = sharedPreferences.contains("support_colorful_ground");
        if (!val) setSupportColorfulGround(true);
        val = sharedPreferences.contains("support_colorful_preview");
        if (!val) setSupportColorfulPreview(true);
        val = sharedPreferences.contains("support_sound");
        if (!val) setSupportSound(true);

        int num = sharedPreferences.getInt("current_level_set", -1);
        setCurrentLevelSet(num >= 0 ? num : currentLevelSet);
        num = sharedPreferences.getInt("init_level", -1);
        setInitLevel(num >= 0 ? num : initLevel);
    }

    /**
     * 是否显示游戏区的网格
     * @param showGridLine
     */
    public void setShowGridLine(boolean showGridLine) {
        this.showGridLine = showGridLine;
        editor.putBoolean("show_grid_line", showGridLine);
        editor.commit();
    }
    /**
     * 是否显示预览区的网格
     * @param showPreviewGridLine
     */
    public void setShowPreviewGridLine(boolean showPreviewGridLine) {
        this.showPreviewGridLine = showPreviewGridLine;
        editor.putBoolean("show_preview_grid_line", showPreviewGridLine);
        editor.commit();
    }
    /**
     * 是否支持彩色形状
     * @param supportColorfulShape
     */
    public void setSupportColorfulShape(boolean supportColorfulShape) {
        this.supportColorfulShape = supportColorfulShape;
        editor.putBoolean("support_colorful_shape", supportColorfulShape);
        editor.commit();
    }
    /**
     * 是否支持彩色障碍物
     * @param supportColorfulGround
     */
    public void setSupportColorfulGround(boolean supportColorfulGround) {
        this.supportColorfulGround = supportColorfulGround;
        editor.putBoolean("support_colorful_ground", supportColorfulGround);
        editor.commit();
    }
    /**
     * 是否支持彩色预览
     * @param supportColorfulPreview
     */
    public void setSupportColorfulPreview(boolean supportColorfulPreview) {
        this.supportColorfulPreview = supportColorfulPreview;
        editor.putBoolean("support_colorful_preview", supportColorfulPreview);
        editor.commit();
    }
    /**
     * 是否支持声音
     * @param supportSound
     */
    public void setSupportSound(boolean supportSound) {
        this.supportSound = supportSound;
        editor.putBoolean("support_sound", supportSound);
        editor.commit();
    }
    /**
     * 当前的级别集
     * @param currentLevelSet
     */
    public void setCurrentLevelSet(int currentLevelSet) {
        int maxSet = LevelSetFactory.getLevelSetCount();
        if(currentLevelSet >= 0 && currentLevelSet < maxSet) {
            this.currentLevelSet = currentLevelSet;
            int maxLevel = LevelSetFactory.getLevelSet(this.currentLevelSet)
                    .getLevelCount();
            if(initLevel < 0 || initLevel >= maxLevel) {
                setInitLevel(0);
            }
        }
        editor.putInt("current_level_set", currentLevelSet);
        editor.commit();
    }
    /**
     * 初始级别
     * @param initLevel
     */
    public void setInitLevel(int initLevel) {
        int max = LevelSetFactory.getLevelSet(currentLevelSet).getLevelCount();
        if(initLevel >= 0 && initLevel < max) {
            this.initLevel = initLevel;
        }
        editor.putInt("init_level", initLevel);
        editor.commit();
    }
}
