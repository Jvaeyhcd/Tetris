package com.hcd.itetris.global;

import android.graphics.Color;

/**
 * Created by hcd on 14-10-25.
 * Author:Jvaeyhcd
 */
public class DefaultConfig {

    /**
     * 默认颜色
     */
    protected int defaultColor = Color.GRAY; // 灰色,gray,0x808080;

    /**
     * 边框颜色
     */
    protected int borderColor = Color.rgb(0,0,58);//new Color(0x000088);

    /**
     * 背景颜色
     */
    protected int backgroundColor = Color.rgb(49,56,66);

    /**
     * 网格颜色
     */
    protected int gridLineColor = Color.rgb(204,204,204);//new Color(0xCCCCCC);

    /**
     * 游戏结束的蒙色
     */
    protected int coveringOverColor = Color.rgb(255,153,153);//new Color(255, 153, 153, 128);

    /**
     * 游戏暂停的蒙色
     */
    protected int coveringPauseColor = Color.rgb(153,153,255);//new Color(153, 153, 255, 128);

    /**
     * 闪烁颜色
     */
    protected int winkColor = Color.WHITE;

    /**
     * 闪烁的暂停时间
     */
    protected int winkPauseTime = 100;

    /**
     * 地形的宽度(单位:格)
     */
    protected int groundWidth = 12;

    /**
     * 地形的高度(单位:格)
     */
    protected int groundHeight = 20;

    /**
     * 单元格的宽度
     */
    protected int cellWidth = 20;

    /**
     * 单元格的高度
     */
    protected int cellHeight = 20;

    /**
     * 预览的宽度(单位:格)
     */
    protected int previewWidth = 5;

    /**
     * 预览的高度(单位:格)
     */
    protected int previewHeight = 5;

    /**
     * 预览的单元格的宽度
     */
    protected int previewCellWidth = 14;

    /**
     * 预览的单元格的高度
     */
    protected int previewCellHeight = 14;

    /**
     * 是否显示游戏区的网格
     */
    protected boolean showGridLine = true;

    /**
     * 是否显示预览区的网格
     */
    protected boolean showPreviewGridLine = true;

    /**
     * 形状是否支持彩色
     */
    protected boolean supportColorfulShape = true;

    /**
     * 障碍物是否支持彩色
     */
    protected boolean supportColorfulGround = true;

    /**
     * 预览是否支持彩色
     */
    protected boolean supportColorfulPreview = true;

    /**
     * 是否支持声音
     */
    protected boolean supportSound = false;

    /**
     * 当前的级别集
     */
    protected int currentLevelSet = 0;

    /**
     * 初始级别
     */
    protected int initLevel = 0;

    DefaultConfig() {

    }


    /**
     * 默认颜色
     * @return
     */
    public int getDefaultColor() {
        return defaultColor;
    }
    /**
     * 边框颜色
     * @return
     */
    public int getBorderColor() {
        return borderColor;
    }
    /**
     * 背景颜色
     * @return
     */
    public int getBackgroundColor() {
        return backgroundColor;
    }
    /**
     * 网格颜色
     * @return
     */
    public int getGridLineColor() {
        return gridLineColor;
    }
    /**
     * 游戏结束的蒙色
     * @return
     */
    public int getCoveringOverColor() {
        return coveringOverColor;
    }
    /**
     * 游戏暂停的蒙色
     * @return
     */
    public int getCoveringPauseColor() {
        return coveringPauseColor;
    }
    /**
     * 闪烁颜色
     * @return
     */
    public int getWinkColor() {
        return winkColor;
    }
    /**
     * 闪烁的暂停时间
     * @return
     */
    public int getWinkPauseTime() {
        return winkPauseTime;
    }
    /**
     * 地形的宽度(单位:格)
     * @return
     */
    public int getGroundWidth() {
        return groundWidth;
    }
    /**
     * 地形的高度(单位:格)
     * @return
     */
    public int getGroundHeight() {
        return groundHeight;
    }
    /**
     * 单元格的宽度
     * @return
     */
    public int getCellWidth() {
        return cellWidth;
    }

    /**
     * 设置单元格宽度
     * @param cellWidth int 单元格宽度
     */
    public void setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
    }

    /**
     * 单元格的高度
     * @return 单元格高度
     */
    public int getCellHeight() {
        return cellHeight;
    }

    /**
     * 设置单元格高度
     * @param cellHeight int 单元格高度
     */
    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
    }

    /**
     * 预览的宽度(单位:格)
     * @return
     */
    public int getPreviewWidth() {
        return previewWidth;
    }
    /**
     * 预览的高度(单位:格)
     * @return
     */
    public int getPreviewHeight() {
        return previewHeight;
    }
    /**
     * 预览的单元格的宽度
     * @return
     */
    public int getPreviewCellWidth() {
        return previewCellWidth;
    }
    /**
     * 预览的单元格的高度
     * @return
     */
    public int getPreviewCellHeight() {
        return previewCellHeight;
    }
    /**
     * 是否显示游戏区的网格
     * @return
     */
    public boolean isShowGridLine() {
        return showGridLine;
    }
    /**
     * 是否显示预览区的网格
     * @return
     */
    public boolean isShowPreviewGridLine() {
        return showPreviewGridLine;
    }
    /**
     * 是否支持彩色形状
     * @return
     */
    public boolean isSupportColorfulShape() {
        return supportColorfulShape;
    }
    /**
     * 是否支持彩色障碍物
     * @return
     */
    public boolean isSupportColorfulGround() {
        return supportColorfulGround;
    }
    /**
     * 是否支持彩色预览
     * @return
     */
    public boolean isSupportColorfulPreview() {
        return supportColorfulPreview;
    }
    /**
     * 是否支持声音
     * @return
     */
    public boolean isSupportSound() {
        return supportSound;
    }
    /**
     * 当前的级别集
     * @return
     */
    public int getCurrentLevelSet() {
        return currentLevelSet;
    }
    /**
     * 初始级别
     * @return
     */
    public int getInitLevel() {
        return initLevel;
    }

    /**
     * 形状的所有颜色
     */
    public static final int[] COLORS = new int[] {
            Color.rgb(51,204,51), // 巧克力,chocolate,0xD2691E
            Color.rgb(102,102,204),    // 栗色,maroon,0x800000
            Color.rgb(153,153,153),  // 橄榄绿,olive,0x808000
            Color.rgb(51,120,0),    // 深绿,darkgreen,0x008000
            Color.rgb(51,120,153),  // 蓝绿,teal,0x008080
            Color.rgb(0,102,153),    // 海蓝,navy,0x000080
            Color.rgb(204,204,0),  // 紫色,purple,0x800080
            Color.rgb(153,0,102), // 钢青,steelblue,0x4682B4
            Color.rgb(255,204,0), // 黄绿,yellowgreen,0x9ACD32
            Color.rgb(210,203,51),// 深海绿,darkseagreen0x8FBC8B
            Color.rgb(51,51,153),  // 深红,crimson,0xDC143C
            Color.rgb(0,100,151), // 中紫,mediumorchid,0xBA55D3
            Color.rgb(255,102,102), // 暗灰蓝,slateblue,0x6A5ACD
            Color.rgb(201,50,151), // 金麒麟色,goldenrod,0xDAA520
            Color.rgb(204,153,204),   // 桔红,orangered,0xFF4500
            Color.rgb(250,128,114),// 肉色,salmon,0xFA8072
//		new Color(0x0000FF), // 蓝色,blue,0x0000FF(无立体感)
//		new Color(0xFF00FF), // 紫红,fuchsia,0xFF00FF(无立体感)
//		new Color(0xFFA500), // 橙色,orange,0xFFA500(与金麒麟色太接近)
//		new Color(0xFFD700), // 金色,gold,0xFFD700(太浅)
//		new Color(0x00BFFF), // 深天蓝,deepskyblue,0x00BFFF(太浅)
    };

    /**
     * 根据类型获取形状的颜色
     * @param type int 类型
     * @return int
     */
    public int getShapeColor(int type) {
        if(this.supportColorfulShape && type >= 0 && type < COLORS.length)
            return COLORS[type];
        else
            return defaultColor;
    }

    public int getDropedShapeColor(){
        return Color.rgb(1,1,0);
    }

    /**
     * 根据类型获取障碍物的颜色
     * @param type int 类型
     * @return int
     */
    public int getGroundColor(int type) {
        if(this.supportColorfulGround && type >= 0 && type < COLORS.length)
            return COLORS[type];
        else
            return defaultColor;
    }

    /**
     * 根据类型获取预览的颜色
     * @param type int 类型
     * @return int
     */
    public int getPreviewColor(int type) {
        if(this.supportColorfulPreview && type >= 0 && type < COLORS.length)
            return COLORS[type];
        else
            return defaultColor;
    }
}
