package com.hcd.itetris.listener;

/**
 * 游戏监听器,监听游戏状态变更事件
 * Created by hcd on 14-10-25.
 */
public interface GameListener {

    /**
     * 游戏开始
     */
    public void gameStart();

    /**
     * 游戏结束
     */
    public void gameOver();

    /**
     * 游戏结束
     */
    public void gamePause();

    /**
     * 游戏继续
     */
    public void gameContinue();

    /**
     * 游戏即将结束
     * @return boolean 是否允许游戏结束 true 是 false 不是
     */
    public boolean gameWillStop();
}
