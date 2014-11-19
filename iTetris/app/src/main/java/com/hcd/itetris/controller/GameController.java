package com.hcd.itetris.controller;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.hcd.itetris.entity.Ground;
import com.hcd.itetris.entity.Level;
import com.hcd.itetris.entity.Shape;
import com.hcd.itetris.entity.ShapeFactory;
import com.hcd.itetris.global.Config;
import com.hcd.itetris.global.Utilities;
import com.hcd.itetris.listener.ConfigListener;
import com.hcd.itetris.listener.GameListener;
import com.hcd.itetris.listener.GameViewListener;
import com.hcd.itetris.listener.PreviewListener;
import com.hcd.itetris.listener.ScoringListener;

/**
 * 游戏控制器
 * 控制游戏的开始,结束,暂停,继续
 * 控制游戏中形状的移动,变形,自动下落,障碍物的满行,计分,级别变更等
 * 在游戏状态变化时向注册的监听器发出游戏事件
 * 在形状,障碍物发生变化时向注册的监听器发出游戏显示事件
 * Created by hcd on 14-10-25.
 * Author:Jvaeyhcd
 */
public class GameController
        implements ConfigListener, ScoringListener, View.OnTouchListener {

    /**
     * 游戏监听器
     */
    private GameListener[] gameListeners;

    /**
     * 游戏显示监听器
     */
    private GameViewListener[] gameViewListeners;

    /**
     * 预览监听器
     */
    private PreviewListener[] previewListeners;

    /**
     * 计分管理器
     */
    private ScoringController scorer;

    /**
     * 地形
     */
    private Ground ground;

    /**
     * 形状
     */
    private Shape shape;

    /**
     * 形状创建的时间<br>
     * 用于屏蔽形状刚创建时的操作事件<br>
     * 有时用户对一个形状持续执行某一操作, 直至该形状触底变成障碍物, <br>
     * 而用户可能仍未停止操作, 导致该操作直接作用于下一形状, <br>
     * 但这一操作对于下一形状可能并不合适<br>
     * 以形状创建的时间加上一个配置的偏移时间来屏蔽形状刚创建时的部分操作事件<br>
     * 以减少这种情况<br>
     */
    private long shapeCreateTime;

    /**
     * 下一次的形状, 用于提供预览
     */
    private Shape nextShape;

    /**
     * 形状自动下落的控制器
     */
    private Thread shapeDropDriver;

    /**
     * 游戏是否正在运行
     */
    private boolean playing;

    /**
     * 游戏是否已暂停
     */
    private boolean pause;

    public GameController(Context context) {
        // 将监听器初始化为0长度数组, 减少后续事件广播时的空值判断
        gameListeners = new GameListener[0];
        gameViewListeners = new GameViewListener[0];
        previewListeners = new PreviewListener[0];

        // 计分器
        scorer = new ScoringController();
        // 计分器增加监听器
        scorer.addScoringListener(this);

        // 增加声音监听器
        SoundController sound = new SoundController(context);
        addGameListener(sound); // 游戏状态变化触发的声音
        addGameViewListener(sound);
        scorer.addScoringListener(sound); // 计分变化触发的声音
    }

    /**
     * 游戏是否是暂停状态
     * @return 是否是暂停状态
     */
    public boolean isPause() {
        return pause;
    }

    /**
     * 游戏是否正在运行
     * @return 是否在运行
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * 形状向下移动的处理<br>
     * 首先判断形状向下移动后是否与地形发生碰撞, 如果不发生碰撞则执行移动操作<br>
     * 当形状不能再向下移动时, 将该形状变成障碍物<br>
     * 并进行后续处理:<br>
     * 1. 判断是否存在满行<br>
     * 2. 删除满行<br>
     *    删除满行前发出游戏显示事件, 以便通知显示组件显示一些效果<br>
     * 3. 计分<br>
     *    计分有可能导致级别变化<br>
     * 4. 创建新形状<br>
     * @param swift 是否直落到底
     */
    private synchronized void shapeDrop(boolean swift) {
        // 判断形状向下移动后是否与地形发生碰撞
        // 复制形状, 令该形状下移, 判断下移后是否与地形发生碰撞
        Shape cloneShape = (Shape)shape.clone();
        cloneShape.moveDown();
        if(!ground.collisional(cloneShape)) {
            // 未与地形发生碰撞, 向下移动
            for (GameViewListener gameViewListener1 : gameViewListeners) gameViewListener1.shapeWillMoved(shape);
            shape.moveDown();
            for (GameViewListener gameViewListener : gameViewListeners) gameViewListener.shapeMoved(shape);
        } else {
            // 形状变成障碍物
            ground.fill(shape);

            for (GameViewListener gameViewListener : gameViewListeners) gameViewListener.shapeDroped(swift);
            for (GameViewListener gameViewListener1 : gameViewListeners)
                gameViewListener1.groundFilledShape(ground, shape);

            // 销毁当前的形状
            shape = null;
            ((ShapeDropDriver)shapeDropDriver).kill();
            shapeDropDriver = null;

            // 检查满行
            int[] fullLine = ground.checkFullLine();
            if(fullLine.length > 0) {
                for (GameViewListener gameViewListener : gameViewListeners)
                    gameViewListener.groundWillDeleteLine(ground, fullLine);
                // 删除满行
                ground.deleteLine(fullLine);
                for (GameViewListener gameViewListener : gameViewListeners) gameViewListener.groundDeletedLine(ground);
                // 计分
                scorer.score(fullLine.length);
            }
            // 创建新形状
            shapeCreate();
        }
    }

    /**
     * 创建新形状<br>
     * 将预览形状置为当前形状, 再创建一个新的预览形状<br>
     * 没有位置创建新形状的时候, 判定为游戏结束<br>
     */
    private void shapeCreate() {
        if(!playing)
            return;

        // 初始位置
        int x = (ground.getWidth() - nextShape.getWidth()) / 2;
        int y = 1 - nextShape.getHeight();
        nextShape.moveTo(x, y);

        // 没有位置创建新形状了, 判定为游戏结束
        if(ground.collisional(nextShape)) {
            playing = false;
            shape = null;
            for (GameListener gameListener : gameListeners) gameListener.gameOver();
        } else {
            // 将预览形状置为当前形状
            shape = nextShape;
            shapeCreateTime = System.currentTimeMillis();
            for (GameViewListener gameViewListener : gameViewListeners) gameViewListener.shapeCreated(shape);
            shapeDropDriver = new ShapeDropDriver();
            shapeDropDriver.start();
            nextShape = null;
            for (PreviewListener previewListener : previewListeners) previewListener.shapePreviewCleared();

            // 创建一个新的预览形状
            int complexity = scorer.getCurrentLevel().getComplexity();
            nextShape = ShapeFactory.getRandomShape(complexity);
            for (PreviewListener previewListener : previewListeners) previewListener.shapePreviewCreated(nextShape);
        }
    }

    /**
     * 创建新游戏
     */
    public void gameCreate() {
        synchronized(this) {
            if(playing) return;
            playing = true;
        }
        if(pause) {
            pause = false;
            for (GameListener gameListener : gameListeners) gameListener.gameContinue();
        }

        for (GameListener gameListener : gameListeners) gameListener.gameStart();

        //设置游戏等级
//        Config.CURRENT.setCurrentLevelSet(2);
//        Config.CURRENT.setInitLevel(3);

        // 初始化游戏环境
        if(ground == null) {
            int width = Config.CURRENT.getGroundWidth();
            int height = Config.CURRENT.getGroundHeight();
            ground = new Ground(width, height);
        } else {
            ground.clear();
            for (GameViewListener gameViewListener : gameViewListeners) gameViewListener.groundCleared();
            for (PreviewListener previewListener : previewListeners) previewListener.shapePreviewCleared();
        }
        for (GameViewListener gameViewListener : gameViewListeners)
            gameViewListener.groundCreated(ground);

        // 初始化计分器
        scorer.init();
        if(playing) {
            // 创建预览形状
            int complexity = scorer.getCurrentLevel().getComplexity();
            nextShape = ShapeFactory.getRandomShape(complexity);
            for (PreviewListener previewListener : previewListeners) previewListener.shapePreviewCreated(nextShape);
            // 创建新形状
            shapeCreate();
        }
    }

    /**
     * 停止当前游戏
     */
    public void gameStop() {
        if(!playing) return;

        // 停止游戏确认
        boolean confirm = true;
        for (GameListener gameListener : gameListeners) {
            if (!gameListener.gameWillStop()) {
                confirm = false;
            }
        }
        // 可以停止游戏
        if(confirm) {
            playing = false;
            shape = null;
            if(this.shapeDropDriver != null) {
                ((ShapeDropDriver)this.shapeDropDriver).kill();
                this.shapeDropDriver = null;
            }
            for (GameListener gameListener : gameListeners) gameListener.gameOver();
        }
    }

    /**
     * 暂停游戏
     */
    public void gamePause() {
        if(!playing || pause) return;

        pause = true;
        for (GameListener gameListener : gameListeners) gameListener.gamePause();
    }

    /**
     * 继续游戏
     */
    public void gameContinue() {
        if(!playing || !pause) return;

        pause = false;
        for (GameListener gameListener : gameListeners) gameListener.gameContinue();
    }

    /**
     * 有关级别的配置项改变时的处理
     */
    public void levelConfigChanged() {
        if(!playing) return;

        // 停止当前游戏
        playing = false;
        shape = null;
        if(this.shapeDropDriver != null) {
            ((ShapeDropDriver)this.shapeDropDriver).kill();
            this.shapeDropDriver = null;
        }

        for (GameListener gameListener : gameListeners) gameListener.gameOver();

        new Thread() {
            public void run() {
                gameCreate();
            }
        }.start();
    }

    public void hotkeyConfigChanged() {}
    public void viewConfigChanged() {}

    /**
     * 级别改变时的处理<br>
     * 1. 将预览形状清空<br>
     * 2. 将障碍物清空<br>
     *    清空前发出游戏显示事件, 以便通知显示组件显示一些效果<br>
     * 3. 根据新的级别要求创建一个新的预览形状<br>
     * 4. 如果新的级别要求填充一些随机障碍物, 填充之<br>
     *    填充后发出游戏显示事件, 以便通知显示组件显示一些效果<br>
     */
    public void levelChanged(Level level) {

        // 1. 将预览形状清空<br>
        nextShape = null;
        for (PreviewListener previewListener : previewListeners) previewListener.shapePreviewCleared();

        // 2. 将障碍物清空
        //    清空前发出游戏显示事件, 以便通知显示组件显示一些效果
        for (GameViewListener gameViewListener : gameViewListeners) gameViewListener.groundWillClear(ground);
        if(!playing) return;
        ground.clear();
        for (GameViewListener gameViewListener : gameViewListeners) gameViewListener.groundCleared();

        // 3. 根据新的级别要求创建一个新的预览形状
        nextShape = ShapeFactory.getRandomShape(
                scorer.getCurrentLevel().getComplexity());
        for (PreviewListener previewListener : previewListeners) previewListener.shapePreviewCreated(nextShape);

        // 4. 如果新的级别要求填充一些随机障碍物, 填充之
        if(level.getFraiseLine() > 0) {
            ground.randomFill(scorer.getCurrentLevel().getFraiseLine(),
                    scorer.getCurrentLevel().getFraiseFillRate());
            // 填充后发出游戏显示事件, 以便通知显示组件显示一些效果
            for (GameViewListener gameViewListener : gameViewListeners) gameViewListener.groundFilledRandom(ground);
        }

    }

    /**
     * 计分初始化时的处理<br>
     * 如果初始级别要求填充一些随机障碍物, 填充之<br>
     */
    public void scoringInit(int scoring, int speed, Level level) {
        // 如果新的级别要求填充一些随机障碍物, 填充之
        if(level.getFraiseLine() > 0) {
            ground.randomFill(scorer.getCurrentLevel().getFraiseLine(),
                    scorer.getCurrentLevel().getFraiseFillRate());
            // 填充后发出游戏显示事件, 以便通知显示组件显示一些效果
            for(int i = 0; i < gameViewListeners.length; i++)
                gameViewListeners[i].groundFilledRandom(ground);
        }
    }
    public void shapeDroped(boolean swift) {}
    public void scoringChanged(int scoring, boolean levelChanged) {}
    public void speedChanged(int speed) {}

    /**
     * 超过最高级别, 游戏结束
     */
    public void winning(int scoring, int speed, Level level) {
        playing = false;
        shape = null;
        for(int i = 0; i < gameListeners.length; i++)
            gameListeners[i].gameOver();
    }

    /**
     * 增加游戏监听器
     * @param listener GameListener 游戏监听器
     */
    public void addGameListener(GameListener listener) {
        gameListeners = (GameListener[]) Utilities.arrayAddItem(
                gameListeners, listener);
    }

    /**
     * 移除游戏监听器
     * @param listener GameListener 游戏监听器
     */
    public void removeGameListener(GameListener listener) {
        gameListeners = (GameListener[])Utilities.arrayRemoveItem(
                gameListeners, listener);
    }

    /**
     * 增加游戏显示监听器
     * @param listener GameViewListener 游戏显示监听器
     */
    public void addGameViewListener(GameViewListener listener) {
        gameViewListeners = (GameViewListener[])Utilities.arrayAddItem(
                gameViewListeners, listener);
    }

    /**
     * 移除游戏显示监听器
     * @param listener GameViewListener 游戏显示监听器
     */
    public void removeGameViewListener(GameViewListener listener) {
        gameViewListeners = (GameViewListener[])Utilities.arrayRemoveItem(
                gameViewListeners, listener);
    }

    /**
     * 增加预览监听器
     * @param listener PreviewListener 预览监听器
     */
    public void addPreviewListener(PreviewListener listener) {
        previewListeners = (PreviewListener[])Utilities.arrayAddItem(
                previewListeners, listener);
    }

    /**
     * 移除预览监听器
     * @param listener PreviewListener 预览监听器
     */
    public void removePreviewListener(PreviewListener listener) {
        previewListeners = (PreviewListener[])Utilities.arrayRemoveItem(
                previewListeners, listener);
    }

    /**
     * 增加计分监听器
     * @param listener ScoringListener 计分监听器
     */
    public void addScoringListener(ScoringListener listener) {
        scorer.addScoringListener(listener);
    }

    /**
     * 增加计分监听器
     * @param listener ScoringListener 计分监听器
     * @param first boolean 是否增加至首位
     */
    public void addScoringListener(ScoringListener listener, boolean first) {
        scorer.addScoringListener(listener, first);
    }

    /**
     * 移除计分监听器
     * @param listener ScoringListener 计分监听器
     */
    public void removeScoringListener(ScoringListener listener) {
        scorer.removeScoringListener(listener);
    }

    private float x1,y1,x2,y2=0;
    private boolean isTouchMove;
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        // 形状仍未被创建或按键的时间早于创建时间, 抛弃之
        if(shape == null || System.currentTimeMillis() <= shapeCreateTime)
            return false;
        // 如果游戏未开始或已暂停, 抛弃之
        if(!playing || pause)
            return false;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x1 = event.getX();
            y1 = event.getY();
            isTouchMove = false;
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            x2 = event.getX();
            y2 = event.getY();
            int cellWidth = Config.CURRENT.getCellWidth();
            if(y1 - y2 > cellWidth && y1 - y2 < 2*cellWidth) {
                Log.d("滑动方向","向上滑");
            }
            if(y2 - y1 > cellWidth && y2 - y1 < 2 * cellWidth) {
                Log.d("滑动方向","向下滑");
                shapeMoveDown();
            }
            if(x1 - x2 > cellWidth && x1 - x2 < 2 * cellWidth) {
                // 形状向左移动的滑动处理
               shapeMoveLeft();
               Log.d("滑动方向","向左滑");
            }
            if(x2 - x1 > cellWidth && x2 - x1 < 2 * cellWidth) {
                // 形状向右移动的滑动处理
                shapeMoveRight();
                Log.d("滑动方向","向右滑");
            }
            else if (x2 - x1 < 1 && y2 - y1 < 1 && !isTouchMove){
                // 形状变形的按键处理
                shapeRotate();
            }
            isTouchMove = false;
        }

        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            isTouchMove = true;
            x2 = event.getX();
            y2 = event.getY();

            int cellWidth = Config.CURRENT.getCellWidth();
            if(y1 - y2 > cellWidth) {
                Log.d("滑动方向","向上滑");
            }
            if(y2 - y1 > cellWidth) {
                Log.d("滑动方向","向下滑");
                //shapeGoDwon();
            }
            if(x1 - x2 > cellWidth) {
                // 形状向左移动的滑动处理
                int offset = (int)((x1 - x2) / (float)cellWidth);

                for (int i = 0; i < offset; i++) {
                    shapeMoveLeft();
                }

                Log.d("滑动方向","向左滑");
            }
            if(x2 - x1 > cellWidth) {
                // 形状向右移动的滑动处理
                int offset = (int)((x2 - x1) / (float)cellWidth);

                for (int i = 0; i < offset; i++) {
                    shapeMoveRight();
                }

                Log.d("滑动方向","向右滑");
            }
            x1 = x2;
            y1 = y2;
        }

        return true;
    }

    public void shapeMoveLeft() {
        Shape shape = (Shape)this.shape.clone();
        shape.moveLeft();
        if(!ground.collisional(shape)) {
            for (GameViewListener gameViewListener : gameViewListeners)
                gameViewListener.shapeWillMoved(this.shape);
            this.shape.moveLeft();
            for (GameViewListener gameViewListener : gameViewListeners)
                gameViewListener.shapeMoved(this.shape);
        }
    }

    public void shapeMoveRight() {
        Shape shape = (Shape)this.shape.clone();
        shape.moveRight();
        if(!ground.collisional(shape)) {
            for (GameViewListener gameViewListener1 : gameViewListeners)
                gameViewListener1.shapeWillMoved(this.shape);
            this.shape.moveRight();
            for (GameViewListener gameViewListener : gameViewListeners)
                gameViewListener.shapeMoved(this.shape);
        }
    }

    public void shapeMoveDown() {
        new Thread() {
            public void run() {
                shapeDrop(false);
            }
        }.start();
    }

    public void shapeGoDwon() {
        if (isPlaying()) {
            ((ShapeDropDriver)this.shapeDropDriver).kill();
            shapeDropDriver = new ShapeDropDriver(true);
            shapeDropDriver.start();
        }
    }

    public void shapeRotate() {
        Shape shape = (Shape)this.shape.clone();
        shape.rotate();
        if(!ground.collisional(shape)) {
            for (GameViewListener gameViewListener : gameViewListeners)
                gameViewListener.shapeWillMoved(this.shape);
            this.shape.rotate();
            for (GameViewListener gameViewListener : gameViewListeners)
                gameViewListener.shapeMoved(this.shape);
        }
    }

    public void moveDown() {

    }

    /**
     * 形状自动下落驱动器
     * @author zhaohuihua
     */
    private class ShapeDropDriver extends Thread {

        /**
         * 该驱动器是否运行的标志<br>
         * 如为false, 则结束运行<br>
         */
        private boolean run;

        /**
         * 是否直落到底的标志<br>
         * 如为false, 每次下落前间歇一定周期<br>
         * 如为true, 无间歇直落到底<br>
         */
        private boolean swift;

        /**
         * 形状自动下落驱动器
         */
        public ShapeDropDriver() {
            this.run = true;
        }

        /**
         * 形状自动下落驱动器
         * @param swift boolean 是否直落到底
         */
        public ShapeDropDriver(boolean swift) {
            this.run = true;
            this.swift = swift;
            this.setDaemon(true);
        }

        /**
         * 销毁驱动器
         */
        public void kill() {
            run = false;
        }

        /**
         * 休眠一定周期后形状自动向下移动一格
         */
        public void run() {
            while(playing && run) {
                try {
                    // 如果不是直落到底, 休眠一个周期
                    if(!swift)
                        Thread.sleep(scorer.getCurrentSpeed());
                } catch (InterruptedException e) {
                }
                if(playing && !pause && shape != null) {
                    // 向下移动
                    shapeDrop(swift);
                }
            }
        }
    }

}
