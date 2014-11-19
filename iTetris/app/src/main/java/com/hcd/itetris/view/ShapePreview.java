package com.hcd.itetris.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.hcd.itetris.entity.Shape;
import com.hcd.itetris.global.Config;
import com.hcd.itetris.listener.ConfigListener;
import com.hcd.itetris.listener.PreviewListener;

/**
 * Created by hcd on 14-10-26.
 * Author:Jvaeyhcd
 */
public class ShapePreview extends View implements PreviewListener, ConfigListener {

    /**
     * 形状
     */
    protected Shape shape;

    private Canvas canvas;
    private Paint paint;
    /**
     * 预览方块边长
     */
    private float cellWidth;

    private float offsetX;
    private float offsetY;

    /**
     * 游戏界面
     */
    public ShapePreview(Context context,AttributeSet attrs){
        super(context, attrs);
        this.paint = new Paint();
        if (isInEditMode()){
            return;
        }
        setBackgroundColor(Config.CURRENT.getBackgroundColor());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;

        float width = this.getRight() - this.getLeft();
        int col = Config.CURRENT.getPreviewWidth();
        offsetX = offsetY = width / 10;
        cellWidth = ((width - 2 * offsetX) / col);

        paintBackground();
        paintShape(shape);
        paintBackgroundLine();
    }

    public void paintShape(Shape shape) {
        if (shape == null) return;
        int oldColor = paint.getColor();
        paint.setColor(Config.CURRENT.getShapeColor(shape.getType()));
        int[][] points = shape.getPoints(false);
        int length = points.length;
        for (int i = 0; i < length; i++) {
            if (1 + points[i][0] < 0)
                continue;
            _paintPoint(points[i][0]+1,points[i][1]+1);
        }
        paint.setColor(oldColor);
    }

    /**
     * 根据点坐标绘制点
     * @param x int 点x坐标
     * @param y int 点y坐标
     */
    private void _paintPoint(int x, int y){
        int oldColor = paint.getColor();
        //创建一个和原始图片一样大小的矩形
        if (canvas != null)
        {
            Rect rect = new Rect((int)(x*cellWidth + offsetX + 0.5),(int)(y*cellWidth + offsetY + 0.5),(int)((x+1)*cellWidth+offsetX + 0.5),(int)((y+1)*cellWidth+offsetY + 0.5));
            RectF rectF = new RectF(rect);
            canvas.drawRoundRect(rectF,cellWidth/3,cellWidth/3,paint);
        }
        paint.setColor(oldColor);
    }

    public void paintBackground() {
        int oldColor = paint.getColor();
        int row = Config.CURRENT.getPreviewHeight();
        int col = Config.CURRENT.getPreviewWidth();

        paint.setColor(Color.rgb(40,47,57));
        for (int i = 0; i < col; i++) {
            for (int j = 0; j < row; j++) {
                _paintPoint(i, j);
            }
        }
        paint.setColor(oldColor);
    }


    /**
     * 绘制背景网格
     */
    private void paintBackgroundLine() {
        int oldColor = paint.getColor();
        int row = Config.CURRENT.getPreviewHeight();
        int col = Config.CURRENT.getPreviewWidth();
        paint.setStrokeWidth(cellWidth/12);
        paint.setColor(Config.CURRENT.getBackgroundColor());
        for (int i = 0; i <= row; i++){
            canvas.drawLine(offsetX,i*cellWidth + offsetY,offsetX + col*cellWidth,i*cellWidth + offsetY,paint);
        }
        for (int i = 0; i <= col; i++){
            canvas.drawLine(i*cellWidth + offsetX,offsetY,i*cellWidth + offsetX,cellWidth*row + offsetY,paint);
        }
        paint.setStrokeWidth(1);
        paint.setColor(oldColor);
    }

    @Override
    public void viewConfigChanged() {

        this.postInvalidate();
    }

    @Override
    public void hotkeyConfigChanged() {

    }

    @Override
    public void levelConfigChanged() {

    }

    @Override
    public void shapePreviewCreated(Shape shape) {
        this.shape = shape;
        this.postInvalidate();
    }

    @Override
    public void shapePreviewCleared() {
        this.shape = null;
        this.postInvalidate();
    }
}
