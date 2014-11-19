package com.hcd.itetris.global;

import android.content.Context;
import android.content.SharedPreferences;

import com.hcd.itetris.entity.LevelSetFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hcd on 14-10-25.
 * Author:Jvaeyhcd
 */
public class CurrentConfig2 extends DefaultConfig {
    protected String configFileName;
    protected Properties properties;

    protected SharedPreferences sharedPreferences;
    protected Context context;

    public CurrentConfig2() {
        super();
        configFileName = getConfigFileName();
        properties = new Properties();
        load();
    }
    /**
     * 是否显示游戏区的网格
     * @param showGridLine
     */
    public void setShowGridLine(boolean showGridLine) {
        this.showGridLine = showGridLine;
        properties.setProperty("show_grid_line",
                String.valueOf(this.showGridLine));
    }
    /**
     * 是否显示预览区的网格
     * @param showPreviewGridLine
     */
    public void setShowPreviewGridLine(boolean showPreviewGridLine) {
        this.showPreviewGridLine = showPreviewGridLine;
        properties.setProperty("show_preview_grid_line",
                String.valueOf(this.showPreviewGridLine));
    }
    /**
     * 是否支持彩色形状
     * @param supportColorfulShape
     */
    public void setSupportColorfulShape(boolean supportColorfulShape) {
        this.supportColorfulShape = supportColorfulShape;
        properties.setProperty("support_colorful_shape",
                String.valueOf(this.supportColorfulShape));
    }
    /**
     * 是否支持彩色障碍物
     * @param supportColorfulGround
     */
    public void setSupportColorfulGround(boolean supportColorfulGround) {
        this.supportColorfulGround = supportColorfulGround;
        properties.setProperty("support_colorful_ground",
                String.valueOf(this.supportColorfulGround));
    }
    /**
     * 是否支持彩色预览
     * @param supportColorfulPreview
     */
    public void setSupportColorfulPreview(
            boolean supportColorfulPreview) {
        this.supportColorfulPreview = supportColorfulPreview;
        properties.setProperty("support_colorful_preview",
                String.valueOf(this.supportColorfulPreview));
    }
    /**
     * 是否支持声音
     * @param supportSound
     */
    public void setSupportSound(boolean supportSound) {
        this.supportSound = supportSound;
        properties.setProperty("support_sound",
                String.valueOf(this.supportSound));
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
        properties.setProperty("current_level_set",
                String.valueOf(this.currentLevelSet));
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
        properties.setProperty("init_level", String.valueOf(this.initLevel));
    }

    /**
     * 加载配置文件
     */
    private void load() {
        try {
            // 加载外部配置文件
            properties.load(configFileName);
        } catch (Exception e) { // 不存在外部配置文件或加载外部文件出错
            if(!(e instanceof FileNotFoundException)) {
                e.printStackTrace();
            }
            // 加载内部默认配置文件
            URL url = this.getClass().getResource(
                    "/com/hcd/itetris/resource/properties/config.res");
            if(url != null) {
                try {
                    properties.load(url.openStream());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            return;
        }

        // 逐一读取配置文件内容
        // 如果配置内容不规范, 会自动修改为规范内容, 由setter修改
        // 显示设置
        String val = properties.getProperty("show_grid_line");
        if(val != null) setShowGridLine("true".equalsIgnoreCase(val));
        val = properties.getProperty("show_preview_grid_line");
        if(val != null) setShowPreviewGridLine("true".equalsIgnoreCase(val));
        val = properties.getProperty("support_colorful_shape");
        if(val != null) setSupportColorfulShape("true".equalsIgnoreCase(val));
        val = properties.getProperty("support_colorful_ground");
        if(val != null) setSupportColorfulGround("true".equalsIgnoreCase(val));
        val = properties.getProperty("support_colorful_preview");
        if(val != null) setSupportColorfulPreview("true".equalsIgnoreCase(val));
        // 声音设置
        val = properties.getProperty("support_sound");
        if(val != null) setSupportSound("true".equalsIgnoreCase(val));
        // 关卡选择
        int num = parseInt(properties.getProperty("current_level_set"));
        setCurrentLevelSet(num >= 0 ? num : currentLevelSet);
        num = parseInt(properties.getProperty("init_level"));
        setInitLevel(num >= 0 ? num : initLevel);
    }
    /**
     * 解析字符串为数字<br>
     * 如果解析失败, 返回-1, 以便为其分配默认值
     * @param value
     * @return
     */
    private int parseInt(String value) {
        if(value == null) return -1;
        try {
            return Integer.parseInt(value);
        } catch(NumberFormatException e) {
            return -1;
        }
    }
    private static final Pattern PTN_KEY_CODE = Pattern.compile(
            "0x(\\d+)\\s*(?:\\(.+\\))?");
    /**
     * 解析字符串为按键的键码<br>
     * 如果解析失败, 返回-1, 以便为其分配默认值
     * @param value
     * @return
     */
    private int parseKeyCode(String value) {
        if(value == null) return -1;
        try {
            Matcher matcher = PTN_KEY_CODE.matcher(value);
            if(matcher.matches()) {
                return Integer.parseInt(matcher.group(1), 16);
            }
        } catch(Exception e) {
        }
        return -1;
    }

    public void save() {
        try {
            properties.store(configFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static final String FILE_NAME = "teties";
    private static final String FILE_EXT = ".cfg";
    private static final Pattern PTN_JAR = Pattern.compile(
            "jar:file:.*[/\\\\](.+)\\.jar![/\\\\].*\\.class",
            Pattern.CASE_INSENSITIVE);
    private String getConfigFileName () {
        String classPath = this.getClass().getName().replace('.', '/');
        // 获取与自身JAR包名称相同的配置文件名
        URL url = this.getClass().getResource("/" + classPath + ".class");
        if(url != null) {
            Matcher matcher = PTN_JAR.matcher(url.toString());
            if(matcher.matches()) {
                String fileName = matcher.group(1);
                if(fileName != null) {
                    return fileName + FILE_EXT;
                }
            }
        }
        return FILE_NAME + FILE_EXT; // 获取失败, 返回默认文件名
    }
}
