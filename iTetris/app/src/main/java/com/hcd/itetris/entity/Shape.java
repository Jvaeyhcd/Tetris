package com.hcd.itetris.entity;

import java.util.Random;

/**
 * 方块的形状,由很多点构成,点的坐标x,y为相对于形状左上角的坐标位置
 * Created by hcd on 14-10-25.
 */
public class Shape {

    /**
     * 随机数生成器
     */
    private final static Random random = new Random();
    /**
     * 方块类型
     */
    private final int type;
    /**
     * 由同一形状不同状态的集合
     * 表示同一形状在旋转变形时的点的不同表现
     * [i]:表示该形状多种状态的其中一种
     * [i][0]:表示该状态下形状的宽度和高度
     * [i][1...n]:表示该状态下形状的点的坐标x,y
     * shapes数据类型为int[m][n][2]
     * m=状态数
     * n=点个数+1
     */
    private final int[][][] shapes;

    /**
     * 形状与容器顶端的距离
     */
    private int top;
    /**
     * 形状与容器左侧的距离
     */
    private int left;
    /**
     * 形状的状态
     */
    private int status;

    /**
     * 不提供外部回去实例化方式,通过ShapeFactory.getShape()获取实例
     * @param type int 类型
     * @param shapes int[][][] 表示该形状的集合点
     */
    Shape(int type,int[][][] shapes){
        this.type = type;
        this.shapes = shapes;
    }

    /**
     * 获取形状当前状态
     * @return int 形状状态
     */
    public int getType() {
        return type;
    }

    /**
     * 获取形状离容器上方的距离
     * @return int 形状离容器上方距离
     */
    public int getTop() {
        return top;
    }

    /**
     * 形状当前状态下的所占高度
     * @return int 高度
     */
    public int getHeight() {
        return shapes[status][0][1];
    }

    /**
     * 设置形状离容器上方的距离
     * @param top int 形状离容器上方的距离
     */
    public void setTop(int top) {
        this.top = top;
    }

    /**
     * 获取形状离容器左边距离
     * @return int 形状离容器左边距离
     */
    public int getLeft() {
        return left;
    }

    /**
     * 形状当前状态下的所占宽度
     * @return int 形状当前状态下的所占宽度
     */
    public int getWidth() {

        return shapes[status][0][0];
    }

    /**
     * 设置形状离容器左边距离
     * @param left int 形状离容器左边距离
     */
    public void setLeft(int left) {
        this.left = left;
    }

    /**
     * 获取形状状态
     * @return int 形状状态
     */
    public int getStatus() {
        return status;
    }

    /**
     * 获取形状当前状态下的所有点的绝对位置的集合
     * @return int[][] 点的绝对位置集合
     * int[i][0] 点的x坐标
     * int[i][1] 点的y坐标
     * int[i][2] 点的类型
     */
    public int[][] getPoints() {
        return getPoints(true);
    }

    /**
     * 获取形状当前状态下的所有点的集合
     * @param absoluteLocation bool 是否获取绝对位置坐标
     *                         true 绝对位置,相对于容器的坐标
     *                         false 相对位置,相对于形状左上角的坐标
     * @return int[][] 点的集合
     * int[i][0] 点的x坐标
     * int[i][1] 点的y坐标
     * int[i][2] 点的类型
     */
    public int[][] getPoints(boolean absoluteLocation){
        int[][] shape = shapes[status];
        int length = shape.length;
        int[][] points = new int[length - 1][3];
        // 第一个点表示形状的宽度和高度, 故i从1开始
        for (int i = 1; i < length; i++) {
            if(absoluteLocation) {
                points[i-1][0] = left + shape[i][0];
                points[i-1][1] = top + shape[i][1];
            } else {
                points[i-1][0] = shape[i][0];
                points[i-1][1] = shape[i][1];
            }
            points[i-1][2] = type;
        }
        return points;
    }

    /**
     * 移动到某一位置,以形状的左上角来定位
     * @param x 离容器上方距离
     * @param y 离容器左边距离
     */
    public void moveTo(int x,int y){
        this.left = x;
        this.top = y;
    }

    /**
     * 向下移动
     */
    public void moveDown(){
        top++;
    }

    /**
     * 向左移动
     */
    public void moveLeft(){
        left--;
    }

    /**
     * 向右移动
     */
    public void moveRight(){
        left++;
    }

    /**
     * 形状旋转
     * @param clockwise boolean 是否按照顺时针旋转
     */
    public void rotate(boolean clockwise){
        if (clockwise){
            if (shapes.length > 1){
                status = (status + 1) % shapes.length;
            }
        }
    }
    /**
     * 按顺时针方向旋转
     */
    public void rotate() {
        rotate(true);
    }

    /**
     * 随机旋转
     */
    public void rotateRandom() {
        status = shapes.length > 1 ? random.nextInt(shapes.length) : 0;
    }

    /**
     * 复制形状
     * @return Shape 复制后的新形状
     */
    public Object clone() {
        Shape shape = new Shape(type, shapes);
        shape.top = top;
        shape.left = left;
        shape.status = status;
        return shape;
    }
}
