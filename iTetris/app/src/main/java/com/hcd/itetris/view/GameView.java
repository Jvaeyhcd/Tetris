package com.hcd.itetris.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.hcd.itetris.entity.Ground;
import com.hcd.itetris.entity.Shape;
import com.hcd.itetris.global.Config;
import com.hcd.itetris.listener.ConfigListener;
import com.hcd.itetris.listener.GameListener;
import com.hcd.itetris.listener.GameViewListener;

/**
 * Created by hcd on 14-10-25.
 */
public class GameView extends View
        implements GameViewListener,GameListener,ConfigListener{

    /**
     * 地形
     */
    private Ground ground;

    /**
     * 形状
     */
    private Shape shape;

    /**
     *下落形状
     */
    private Shape dropedShape;

    /**
     * 游戏是否正在进行
     */
    private boolean playing;

    /**
     * 游戏是否暂停
     */
    private boolean pause;

    private Canvas canvas;
    private Paint paint;
    private float cellWidth;
    private int col = 12;
    private int row = 20;

    /**
     * 游戏界面
     */
    public GameView(Context context,AttributeSet attrs){
        super(context, attrs);
        if (isInEditMode()){
            return;
        }
        this.paint = new Paint();
        setBackgroundColor(Color.rgb(49, 56, 66));
        playing = true;
        pause = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = this.getRight() - this.getLeft();
        float height = this.getBottom() - this.getTop();
        cellWidth = (width / col) < (height / row) ? (width / col) : (height / row);
        Config.CURRENT.setCellWidth((int)cellWidth);
        cellWidth = Config.CURRENT.getCellWidth();
        this.canvas = canvas;
        if (paint == null) paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paintBackground();
        paintGround(ground);
        paintShape(shape);
        paintDropShape(dropedShape);
        paintBackgroundLine();
        if (!playing || pause)
            paintCovering();
    }

    /**
     * 绘制背景网格
     */
    private void paintBackground(){
        int oldColor = paint.getColor();

        paint.setColor(Color.rgb(40,47,57));
        for (int i = 0; i < col; i++) {
            for (int j = 0; j < row; j++) {
                Rect rect = new Rect((int)(i*cellWidth),(int)(j*cellWidth),(int)((i+1)*cellWidth),(int)((j+1)*cellWidth));
                RectF rectF = new RectF(rect);
                canvas.drawRoundRect(rectF,cellWidth/3,cellWidth/3,paint);
            }
        }
        paint.setColor(oldColor);
    }

    /**
     * 绘制背景网格
     */
    private void paintBackgroundLine() {
        int oldColor = paint.getColor();
        paint.setStrokeWidth(cellWidth/12);
        paint.setColor(Config.CURRENT.getBackgroundColor());
        for (int i = 0; i <= row; i++){
            canvas.drawLine(0,i*cellWidth,col*cellWidth,i*cellWidth,paint);
        }
        for (int i = 0; i <= col; i++){
            canvas.drawLine(i*cellWidth,0,i*cellWidth,cellWidth*row,paint);
        }
        paint.setColor(1);
        paint.setColor(oldColor);
    }

    /**
     * 绘制地形
     * @param ground Ground 地形
     */
    private void paintGround(Ground ground) {
        if (ground == null) return;
        int[][] points = ground.getPoints();
        if (points == null) return;
        int length = points.length;
        for (int i = 0; i < length; i++) {
            if (points[i][1] < 0)continue;
            paint.setColor(Config.CURRENT.getGroundColor(points[i][2]));
            _paintPoint(points[i][0],points[i][1]);
        }
    }

    private void paintGround(Shape shape) {
        if (shape == null) return;
        _paintShape(shape, Color.BLACK);
    }

    private void paintShape(Shape shape){
        if (shape == null) return;
        _paintShape(shape, Config.CURRENT.getShapeColor(shape.getType()));
    }

    private void paintDropShape(Shape shape) {
        if(shape == null) return;
        _paintShape(shape, Color.argb(100,100,100,100));
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
            Rect rect = new Rect((int)(x*cellWidth + 0.5),(int)(y*cellWidth + 0.5),(int)((x+1)*cellWidth + 0.5),(int)((y+1)*cellWidth + 0.5));
            RectF rectF = new RectF(rect);
            canvas.drawRoundRect(rectF,cellWidth/3,cellWidth/3,paint);
            paint.setStrokeWidth(1);
        }
        paint.setColor(oldColor);
    }

    /**
     * 绘制形状
     * @param shape Shape 形状
     */
    private void _paintShape(Shape shape,int color){
        paint.setColor(color);
        int[][] points = shape.getPoints();
        int length = points.length;
        for (int i = 0; i < length; i++) {
            if (points[i][1] < 0) continue;
            _paintPoint(points[i][0],points[i][1]);
        }
    }

    private void paintCovering() {

    }

    @Override
    public void gameStart() {
        playing = true;
        ground = null;
        shape = null;
    }

    @Override
    public void gameOver() {
        playing = false;
        synchronized (this) {
            if (pause) {
                this.postInvalidate();
            } else {
                paintCovering();
            }
        }
    }

    @Override
    public void gamePause() {
        pause = true;
        synchronized (this) {
            paintCovering();
        }
    }

    @Override
    public void gameContinue() {

    }

    @Override
    public boolean gameWillStop() {
        return false;
    }

    /**
     * 创建形状的相应事件
     * @param shape Shape 新创建的形状
     */
    @Override
    public void shapeCreated(Shape shape) {
        this.shape = shape;
        if(ground != null){
            this.dropedShape = ground.dropDownShape(shape);
        }
        postInvalidate();
    }

    /**
     * 形状即将开始移动的事件相应
     * @param shape Shape 发生变化的形状
     */
    @Override
    public void shapeWillMoved(Shape shape) {

    }

    @Override
    public void shapeMoved(Shape shape) {
        this.shape = shape;
        if(ground != null){
            this.dropedShape = ground.dropDownShape(shape);
        }
        postInvalidate();
    }

    @Override
    public void shapeDroped(boolean swift) {

    }

    @Override
    public void groundFilledShape(Ground ground, Shape shape) {
        this.ground = ground;
        this.shape = null;
        postInvalidate();
        //paintGround(shape);
    }

    @Override
    public void groundCreated(Ground ground) {
        this.ground = ground;
        postInvalidate();
    }

    @Override
    public void groundWillDeleteLine(Ground ground, int[] line) {

    }

    @Override
    public void groundDeletedLine(Ground ground) {
        this.ground = ground;
    }

    @Override
    public void groundWillClear(Ground ground) {

    }

    @Override
    public void groundFilledRandom(Ground ground) {

    }

    @Override
    public void groundCleared() {

    }

    @Override
    public void viewConfigChanged() {

    }

    @Override
    public void hotkeyConfigChanged() {

    }

    @Override
    public void levelConfigChanged() {

    }
}
