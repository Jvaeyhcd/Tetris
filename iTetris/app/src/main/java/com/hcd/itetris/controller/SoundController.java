package com.hcd.itetris.controller;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.hcd.itetris.R;
import com.hcd.itetris.entity.Ground;
import com.hcd.itetris.entity.Level;
import com.hcd.itetris.entity.Shape;
import com.hcd.itetris.listener.GameListener;
import com.hcd.itetris.listener.GameViewListener;
import com.hcd.itetris.listener.ScoringListener;

import java.util.HashMap;

/**
 * Created by hcd on 14-11-2.
 * Author:Jvaeyhcd
 */
public class SoundController
        implements GameListener,ScoringListener,GameViewListener {

    /**
     * 声音类型(开始)
     */
    public static final int START = 0;
    /**
     * 声音类型(结束)
     */
    public static final int OVER = 1;
    /**
     * 声音类型(暂停)
     */
    public static final int PAUSE = 2;
    /**
     * 声音类型(继续)
     */
    public static final int CONTINUE = 3;
    /**
     * 声音类型(下落到位)
     */
    public static final int DOWN = 4;
    /**
     * 声音类型(直落到底)
     */
    public static final int SWIFT = 5;
    /**
     * 声音类型(得分)
     */
    public static final int SCORING = 6;
    /**
     * 声音类型(级别变化)
     */
    public static final int LEVEL = 7;
    /**
     * 声音类型(赢啦)
     */
    public static final int WINNING = 8;

    /**
     * 声音文件的文件名
     */
    private static final String[] sounds = {
            "start.wav",
            "over.wav",
            "pause.wav",
            "start.wav", // continue
            "down.wav",
            "swift.wav",
            "scoring.wav",
            "level.wav",
            "winning.wav"
    };

    private Context context;
    private static final SoundPool soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
    private static final HashMap<Integer, Integer> soundPoolMap = new HashMap<Integer, Integer>();

    public SoundController(Context context) {
        this.context = context;
        soundPoolMap.put(START,soundPool.load(this.context, R.raw.start, 1));
        soundPoolMap.put(OVER, soundPool.load(this.context, R.raw.over, 1));
        soundPoolMap.put(PAUSE, soundPool.load(this.context, R.raw.pause, 1));
        soundPoolMap.put(CONTINUE, soundPool.load(this.context, R.raw.start, 1));
        soundPoolMap.put(DOWN, soundPool.load(this.context, R.raw.down, 1));
        soundPoolMap.put(SWIFT, soundPool.load(this.context, R.raw.swift, 1));
        soundPoolMap.put(SCORING, soundPool.load(this.context, R.raw.scoring, 1));
        soundPoolMap.put(LEVEL, soundPool.load(this.context, R.raw.level, 1));
        soundPoolMap.put(WINNING, soundPool.load(this.context, R.raw.winning, 1));
    }

    /**
     * 按指定类型播放声音
     * @param type
     */
    private void play(int type) {
        //if(!Config.CURRENT.isSupportSound()) return;
        if (soundPool == null) return;
        soundPool.play(soundPoolMap.get(type),1,1,0,0,1);
    }

    @Override
    public void gameStart() {
        play(START);
    }

    @Override
    public void gameOver() {
        play(OVER);
    }

    @Override
    public void gamePause() {
        play(PAUSE);
    }

    @Override
    public void gameContinue() {
        play(CONTINUE);
    }

    @Override
    public boolean gameWillStop() {
        return true;
    }

    @Override
    public void scoringInit(int scoring, int speed, Level level) {

    }

    @Override
    public void scoringChanged(int scoring, boolean levelChanged) {
        if(!levelChanged) play(SCORING);
    }

    @Override
    public void speedChanged(int speed) {

    }

    @Override
    public void levelChanged(Level level) {
        play(LEVEL);
    }

    @Override
    public void winning(int scoring, int speed, Level level) {
        play(WINNING);
    }

    @Override
    public void shapeCreated(Shape shape) {

    }

    @Override
    public void shapeWillMoved(Shape shape) {

    }

    @Override
    public void shapeMoved(Shape shape) {

    }

    @Override
    public void shapeDroped(boolean swift) {
        if(swift) {
            play(SWIFT);
        } else {
            play(DOWN);
        }
    }

    @Override
    public void groundCreated(Ground ground) {

    }

    @Override
    public void groundFilledShape(Ground ground, Shape shape) {

    }

    @Override
    public void groundWillDeleteLine(Ground ground, int[] line) {

    }

    @Override
    public void groundDeletedLine(Ground ground) {

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
}
