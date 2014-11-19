package com.hcd.itetris.listener;

import com.hcd.itetris.entity.Shape;

/**
 * 游戏预览监听器,监听游戏预览变更事件
 * Created by hcd on 14-10-25.
 */
public interface PreviewListener {
    /**
     * 创建形状预览了
     * @param shape Shape 形状预览
     */
    public void shapePreviewCreated(Shape shape);

    /**
     * 形状预览清除了
     */
    public void shapePreviewCleared();
}
