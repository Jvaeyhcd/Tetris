package com.hcd.itetris;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.hcd.itetris.controller.GameController;
import com.hcd.itetris.entity.Level;
import com.hcd.itetris.entity.LevelSetFactory;
import com.hcd.itetris.global.Config;
import com.hcd.itetris.global.CurrentConfig;
import com.hcd.itetris.listener.GameListener;
import com.hcd.itetris.listener.ScoringListener;
import com.hcd.itetris.view.GameView;
import com.hcd.itetris.view.ShapePreview;


public class TetrisActivity extends Activity
    implements GameListener, ScoringListener{

    private ShapePreview shapePreview;
    private GameView gameView;
    private EditText checkPointsEditText;
    private EditText levelEditText;
    private EditText scoreEditText;
    private EditText speedEditText;
    private Button btnLeft;
    private Button btnRight;
    private Button btnDown;
    private Button btnGoDown;
    private Button btnRotate;
    private Button btnStart;
    private Button btnPause;

    /** 游戏控制器 */
    private GameController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tetris);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）

        gameView = (GameView)findViewById(R.id.gameView);
        gameView.setX((float)(width*0));
        gameView.setY((float)(height*0));
        int groundWidth = Config.CURRENT.getGroundWidth();
        int groundHeight = Config.CURRENT.getGroundHeight();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(width*0.8),(int)(width*0.8*((float)groundHeight/(float)groundWidth)));
        gameView.setLayoutParams(params);

        shapePreview = (ShapePreview)findViewById(R.id.shapePreview);
        shapePreview.setX((float)0);
        shapePreview.setY((float)0);
        LinearLayout.LayoutParams p_shapePreview = new LinearLayout.LayoutParams((int)(width*0.2),(int)(width*0.2));
        shapePreview.setLayoutParams(p_shapePreview);

        btnLeft = (Button)findViewById(R.id.btnLeft);
        btnLeft.setOnClickListener(myOnClickListener);
        btnLeft.setEnabled(false);
        btnRight = (Button)findViewById(R.id.btnRight);
        btnRight.setOnClickListener(myOnClickListener);
        btnRight.setEnabled(false);
        btnDown = (Button)findViewById(R.id.btnDown);
        btnDown.setOnClickListener(myOnClickListener);
        btnDown.setEnabled(false);
        btnGoDown = (Button)findViewById(R.id.btnGoDown);
        btnGoDown.setOnClickListener(myOnClickListener);
        btnGoDown.setEnabled(false);
        btnRotate = (Button)findViewById(R.id.btnRotate);
        btnRotate.setOnClickListener(myOnClickListener);
        btnRotate.setEnabled(false);
        btnStart = (Button)findViewById(R.id.btnStart);
        btnStart.setOnClickListener(myOnClickListener);
        btnStart.setEnabled(true);
        btnPause = (Button)findViewById(R.id.btnPause);
        btnPause.setOnClickListener(myOnClickListener);
        btnPause.setEnabled(false);

        controller = new GameController(this);
        controller.addScoringListener(this, true);
        controller.addGameListener(this);
        controller.addGameListener(gameView);
        controller.addGameViewListener(gameView);
        controller.addPreviewListener(shapePreview);
        gameView.setOnTouchListener(controller);

        checkPointsEditText = (EditText)findViewById(R.id.checkPointsEditText);
        levelEditText = (EditText)findViewById(R.id.levelEditText);
        scoreEditText = (EditText)findViewById(R.id.scoreEditText);
        speedEditText = (EditText)findViewById(R.id.speedEditText);
    }

    //手动增加代码开始
    private View.OnClickListener myOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            //增加自己的代码......
            Button btn = (Button) v;
            if (btn == btnLeft) {
                controller.shapeMoveLeft();
            } else if (btn == btnRight) {
                controller.shapeMoveRight();
            } else if (btn == btnDown) {
                controller.shapeMoveDown();
            } else if (btn == btnGoDown) {
                controller.shapeGoDwon();
            } else if (btn == btnRotate) {
                controller.shapeRotate();
            } else if (btn == btnStart) {
                if (controller.isPlaying()) {
                    controller.gameStop();
                } else {
                    controller.gameCreate();
                }
            } else if (btn == btnPause) {
                if(controller.isPause()) {
                    controller.gameContinue();
                } else {
                    controller.gamePause();
                }
            }
        }
    };

    @Override
    public void gameStart() {
        new Thread(new Runnable() {
            public void run() {
                btnDown.post(new Runnable() {
                    @Override
                    public void run() {
                        btnDown.setEnabled(true);
                    }
                });
                btnRotate.post(new Runnable() {
                    @Override
                    public void run() {
                        btnRotate.setEnabled(true);
                    }
                });
                btnGoDown.post(new Runnable() {
                    @Override
                    public void run() {
                        btnGoDown.setEnabled(true);
                    }
                });
                btnRight.post(new Runnable() {
                    @Override
                    public void run() {
                        btnRight.setEnabled(true);
                    }
                });
                btnLeft.post(new Runnable() {
                    @Override
                    public void run() {
                        btnLeft.setEnabled(true);
                    }
                });
                btnPause.post(new Runnable() {
                    @Override
                    public void run() {
                        btnPause.setEnabled(true);
                        btnPause.setText("暂停");
                    }
                });
                btnStart.post(new Runnable() {
                    @Override
                    public void run() {
                        btnStart.setEnabled(true);
                        btnStart.setText("结束");
                    }
                });
            }
        }).start();
    }

    @Override
    public void gameOver() {
        new Thread(new Runnable() {
            public void run() {
                btnDown.post(new Runnable() {
                    @Override
                    public void run() {
                        btnDown.setEnabled(false);
                    }
                });
                btnRotate.post(new Runnable() {
                    @Override
                    public void run() {
                        btnRotate.setEnabled(false);
                    }
                });
                btnGoDown.post(new Runnable() {
                    @Override
                    public void run() {
                        btnGoDown.setEnabled(false);
                    }
                });
                btnRight.post(new Runnable() {
                    @Override
                    public void run() {
                        btnRight.setEnabled(false);
                    }
                });
                btnLeft.post(new Runnable() {
                    @Override
                    public void run() {
                        btnLeft.setEnabled(false);
                    }
                });
                btnPause.post(new Runnable() {
                    @Override
                    public void run() {
                        btnPause.setEnabled(false);
                    }
                });
                btnStart.post(new Runnable() {
                    @Override
                    public void run() {
                        btnStart.setEnabled(true);
                        btnStart.setText("开始");
                    }
                });
            }
        }).start();
    }

    @Override
    public void gamePause() {
        new Thread(new Runnable() {
            public void run() {
                btnDown.post(new Runnable() {
                    @Override
                    public void run() {
                        btnDown.setEnabled(false);
                    }
                });
                btnRotate.post(new Runnable() {
                    @Override
                    public void run() {
                        btnRotate.setEnabled(false);
                    }
                });
                btnGoDown.post(new Runnable() {
                    @Override
                    public void run() {
                        btnGoDown.setEnabled(false);
                    }
                });
                btnRight.post(new Runnable() {
                    @Override
                    public void run() {
                        btnRight.setEnabled(false);
                    }
                });
                btnLeft.post(new Runnable() {
                    @Override
                    public void run() {
                        btnLeft.setEnabled(false);
                    }
                });
                btnPause.post(new Runnable() {
                    @Override
                    public void run() {
                        btnPause.setEnabled(true);
                        btnPause.setText("继续");
                    }
                });
            }
        }).start();
    }

    @Override
    public void gameContinue() {
        new Thread(new Runnable() {
            public void run() {
                btnDown.post(new Runnable() {
                    @Override
                    public void run() {
                        btnDown.setEnabled(true);
                    }
                });
                btnRotate.post(new Runnable() {
                    @Override
                    public void run() {
                        btnRotate.setEnabled(true);
                    }
                });
                btnGoDown.post(new Runnable() {
                    @Override
                    public void run() {
                        btnGoDown.setEnabled(true);
                    }
                });
                btnRight.post(new Runnable() {
                    @Override
                    public void run() {
                        btnRight.setEnabled(true);
                    }
                });
                btnLeft.post(new Runnable() {
                    @Override
                    public void run() {
                        btnLeft.setEnabled(true);
                    }
                });
                btnPause.post(new Runnable() {
                    @Override
                    public void run() {
                        btnPause.setText("暂停");
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean gameWillStop() {
        return false;
    }

    @Override
    public void scoringInit(final int scoring, final int speed, final Level level) {
        checkPointsEditText.setText(LevelSetFactory.getLevelSet(Config.CURRENT.getCurrentLevelSet()).getName());
        levelEditText.setText("" + (level.getId() + 1));
        scoreEditText.setText("" + scoring);
        speedEditText.setText("" + speed + "毫秒/格");
    }

    @Override
    public void scoringChanged(final int scoring, boolean levelChanged) {
        new Thread(new Runnable() {
            public void run() {
                final int score = scoring;
                scoreEditText.post(new Runnable() {
                    @Override
                    public void run() {
                        if (score >= 0)
                            scoreEditText.setText("" + score);
                    }
                });
            }
        }).start();
    }

    @Override
    public void speedChanged(final int speed) {
        new Thread(new Runnable() {
            public void run() {
                final int sp = speed;
                scoreEditText.post(new Runnable() {
                    @Override
                    public void run() {
                        if (sp >= 0)
                            speedEditText.setText("" + sp + "毫秒/格");
                    }
                });
            }
        }).start();

    }

    @Override
    public void levelChanged(final Level level) {
        new Thread(new Runnable() {
            public void run() {
                final Level lv = level;
                scoreEditText.post(new Runnable() {
                    @Override
                    public void run() {
                        if (lv != null)
                            levelEditText.setText("" + (lv.getId() + 1));
                    }
                });
            }
        }).start();
    }

    @Override
    public void winning(int scoring, int speed, Level level) {

    }

}
