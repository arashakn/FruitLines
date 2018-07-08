package com.hanastudio.columns.activity;

import android.graphics.Typeface;
import android.view.KeyEvent;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.android.datastore.LocalStore;

import java.util.ArrayList;
import java.util.List;

import amaztricks.game.dialog.GameOverDialog;
import amaztricks.game.dialog.MyDialog;
import amaztricks.game.dialog.PauseDialog;
import amaztricks.game.manager.ConfigManager;
import amaztricks.game.manager.SoundManager;
import andengine.game.scenemanager.IScenePattern;
import andengine.game.scenemanager.SceneSwitcher;
import game.android.music.Music;
import udc.fruitcolumns.logic.TabletController;
import udc.fruitcolumns.logic.TabletView;
import udc.xephinh.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SceneTetris implements IScenePattern, IOnSceneTouchListener {
    private final int MOVE_LEFT = 1;
    private final int MOVE_RIGHT = 2;
    private final int ROTATE = 3;
    private final int DROP = 4;
    private final int SWIPE_MIN_DISTANCE = 20;

    private Font mFont;

    // public LightrisGame game;
    // private LightrisGameView gameView;
    TabletController game;
    TabletView gameView;

    List<ITextureRegion> textureRegionManager = new ArrayList<ITextureRegion>();
    private BackgroundView backgroundView;
    private static final float HUD_ALPHA = 0.5f;

    private Scene scene;

    BaseGameActivity mBaseGame;

    // private GestureDetector mSGDA;

    private BitmapTextureAtlas controlAtlas;
    private TiledTextureRegion controlRegion;
    private ButtonSprite controlBtn; //
    private int controlWidth = 120;
    private int controlHeight = 60;
    private int controlDx = 340;
    private int controlDy = 10;

    SettingGame settingButton;

    Music bgMusic;
    MyDialog pauseDialog, gameOverDialog;
    private int movingStatus;
    private int preMovingStatus;
    public SceneTetris(BaseGameActivity mbBaseGameActivity) {
        this.mBaseGame = mbBaseGameActivity;
        settingButton = new SettingGame();
        bgMusic = SoundManager.loadMusic("stage.mp3");
        bgMusic.setLooping(true);
        bgMusic.setVolume(0.5f);
        settingButton = new SettingGame();
        SoundManager.loadGameSound(mBaseGame);

    }

    public void resetExistedFile() {

        ConfigManager.resetConfig();
        isResetGame = true;
        BackgroundView.reset();

    }

    public boolean isNeededResetingGame() {
        isResetGame = ConfigManager.isNeededResetingGame(mBaseGame);
        return isResetGame;
    }

    private void saveGame() {
        ConfigManager.saveGame(mBaseGame);
    }

    public void onResume() {
        bgMusic = SoundManager.loadMusic("stage.mp3");
        bgMusic.setLooping(true);
        bgMusic.setVolume(0.5f);
        isPauseGame = false;
        resumeBgMusic();

    }

    public void onPause() {
        isPauseGame = true;
        bgMusic.dispose();

    }

    public void stopBgMusic() {
        if (bgMusic != null) {
            if (!bgMusic.isStopped()) {
                bgMusic.pause();
            }
        }
    }

    public void resumeBgMusic() {

        if (bgMusic != null) {
            if (ConfigManager.IS_MUSIC) {
                bgMusic.play();
            }
        }

    }

    public void onStop() {
        if (ConfigManager.IS_MUSIC) {

            if (bgMusic != null) {
                bgMusic.stop();

            }
        }

    }

    private void createHUD(final Scene scene) {

        OnClickListener onClickListener = new OnClickListener() {

            @Override
            public void onClick(ButtonSprite pButtonSprite,
                                float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pButtonSprite == controlBtn) {
                    isPauseGame = !isPauseGame;
                    if (isPauseGame) {
                        pauseGame();
                    } else {
                        resumeGame();
                    }
                }

            }
        };

        scene.setTouchAreaBindingOnActionDownEnabled(true);
        scene.setTouchAreaBindingOnActionMoveEnabled(true);
        scene.setOnSceneTouchListener(this);

        controlBtn.setOnClickListener(onClickListener);
        // soundBtn.setOnClickListener(onClickListener);

        // camera.setHUD(tetrisControls);
    }

    // DialogPause mDialogPause;
    public boolean isPauseGame = false;
    boolean isGameOver = false;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (game != null && game.isStopAble()) {
                if (isPauseGame) {
                    // resumeGame();
                    resumeGame();
                } else if (isGameOver) {
                    return true;
                } else {
                    // isPauseGame = true;
                    // initAlertQuit();
                    isPauseGame = true;
                    controlBtn.setCurrentTileIndex(1);
                    initAlertQuit();
                    // pauseGame();
                }
            }

        }

        return true;

    }

    private void initAlertQuit() {

        // TODO Auto-generated method stub
        if (pauseDialog == null) {
            pauseDialog = new PauseDialog(mBaseGame);
            pauseDialog.onloadResource();
            pauseDialog.onloadScene(scene);
        }
        pauseDialog.action(true);

    }

    public void quitGame() {
        mBaseGame.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                ConfigManager.GAME_FIELD = game.getMatrix();
                ConfigManager.GAME_COLOR = game.getColor();
                LocalStore.putObject(MainActivity.PREF_FILE_NAME
                        + ConfigManager.FIELD_TETRIS_ADDRESS, ConfigManager.GAME_FIELD, mBaseGame);
                LocalStore.putObject(MainActivity.PREF_FILE_NAME
                        + ConfigManager.COLOR_TETRIS_ADDRESS, ConfigManager.GAME_COLOR, mBaseGame);
                saveGame();
                finish();
            }
        });

    }

    public void pauseGame() {

        isPauseGame = true;
        controlBtn.setCurrentTileIndex(1);
        initAlertQuit();

    }


    public void resumeGame() {
        if (pauseDialog != null) {
            pauseDialog.action(false);
        }
        isPauseGame = false;
        controlBtn.setCurrentTileIndex(0);
        settingButton.activeSetting(false);
    }

    private void showGameOverDialog() {
        mBaseGame.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                // new Dialog(mBaseGame);
                saveGame();
                if (gameOverDialog == null) {
                    gameOverDialog = new GameOverDialog(mBaseGame);
                    gameOverDialog.onloadResource();
                    gameOverDialog.onloadScene(scene);
                }
                isPauseGame = true;
                gameOverDialog.action(true);
                // finish();
            }
        });
    }

    public void finish() {
        saveGame();
        stopBgMusic();
        // saveGame();
        MainActivity.curScene = MainActivity.MAIN_MENU;
        onUnloadResoures(mBaseGame);
        SceneSwitcher.switchScene(mBaseGame, MainActivity.mainMenuScene, false,
                false);

    }

    private void initNextLevel(Scene scene) {
        game.initLevel();
        isPauseGame = false;
        isGameOver = false;
    }

    public void nextGame() {
        initNextLevel(scene);

    }

    boolean isDoneLoadingResource = false;
    private int nColumn = 9;
    private int nRow = 1;
    private boolean isResetGame;

    @Override
    public void onLoadResources(BaseGameActivity mBaseGame) {
        // TODO Auto-generated method stub
        if (isDoneLoadingResource) {
            return;
        }
        this.mFont = FontFactory.create(mBaseGame.getFontManager(),
                mBaseGame.getTextureManager(), 256, 256,
                Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
        this.mFont.load();

        controlAtlas = new BitmapTextureAtlas(mBaseGame.getTextureManager(),
                controlWidth, controlHeight, TextureOptions.BILINEAR);
        controlRegion = BitmapTextureAtlasTextureRegionFactory
                .createTiledFromAsset(controlAtlas, mBaseGame.getAssets(),
                        "settings/pause.png", 0, 0, 2, 1);
        controlAtlas.load();

        controlBtn = new ButtonSprite(controlDx, controlDy, controlRegion,
                mBaseGame.getVertexBufferObjectManager());

        settingButton.onLoadResources(mBaseGame);

        // soundAtlas = new BitmapTextureAtlas(mBaseGame.getTextureManager(),
        // soundWidth, soundHeight, TextureOptions.BILINEAR);
        // soundRegion = BitmapTextureAtlasTextureRegionFactory
        // .createTiledFromAsset(soundAtlas, mBaseGame.getAssets(),
        // "settings/setting.png", 0, 0, 2, 1);
        // soundAtlas.load();
        //
        // soundBtn = new ButtonSprite(soundDx, soundDy, soundRegion,
        // mBaseGame.getVertexBufferObjectManager());
        isDoneLoadingResource = true;

        BitmapTextureAtlas fieldBlockTextureAtlas = new BitmapTextureAtlas(
                mBaseGame.getTextureManager(), 450, 150, TextureOptions.BILINEAR);
        ITextureRegion fieldBlockRegion = BitmapTextureAtlasTextureRegionFactory
                .createTiledFromAsset(fieldBlockTextureAtlas,
                        mBaseGame.getAssets(), "block/columns.png", 0, 0,
                        7, 3);
        fieldBlockTextureAtlas.load();

        BitmapTextureAtlas sparkAtlas = new BitmapTextureAtlas(
                mBaseGame.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
        ITextureRegion sparkTextureRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(sparkAtlas, mBaseGame.getAssets(),
                        "block/spark.png", 0, 0);
        sparkAtlas.load();

        BitmapTextureAtlas bombEffectAtlas = new BitmapTextureAtlas(
                mBaseGame.getTextureManager(), 60, 60, TextureOptions.BILINEAR);
        ITextureRegion bomEffectRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(bombEffectAtlas, mBaseGame.getAssets(),
                        "block/bomb.png", 0, 0);
        bombEffectAtlas.load();

        BitmapTextureAtlas holeEffectAtlas = new BitmapTextureAtlas(
                mBaseGame.getTextureManager(), 46, 37, TextureOptions.BILINEAR);
        ITextureRegion holeEffectrRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(holeEffectAtlas, mBaseGame.getAssets(),
                        "block/hole.png", 0, 0);
        holeEffectAtlas.load();

        BitmapTextureAtlas levelUpAtlas = new BitmapTextureAtlas(
                mBaseGame.getTextureManager(), 256, 64, TextureOptions.BILINEAR);
        ITextureRegion levelUpRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(levelUpAtlas, mBaseGame.getAssets(),
                        "gfx/levelup.png", 0, 0);
        levelUpAtlas.load();

        BitmapTextureAtlas startAtlas = new BitmapTextureAtlas(
                mBaseGame.getTextureManager(), 60, 60, TextureOptions.BILINEAR);
        ITextureRegion starRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(startAtlas, mBaseGame.getAssets(),
                        "block/star.png", 0, 0);
        startAtlas.load();

        BitmapTextureAtlas thunderAtlas = new BitmapTextureAtlas(
                mBaseGame.getTextureManager(), 50, 50, TextureOptions.BILINEAR);
        ITextureRegion thunderRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(thunderAtlas, mBaseGame.getAssets(),
                        "block/thunder.png", 0, 0);
        thunderAtlas.load();

        // BitmapTextureAtlas chainAtlas = new BitmapTextureAtlas(
        // mBaseGame.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
        // ITextureRegion chainRegion = BitmapTextureAtlasTextureRegionFactory
        // .createFromAsset(chainAtlas, mBaseGame.getAssets(),
        // "block/chain.png", 0, 0);
        // chainAtlas.load();

        textureRegionManager.add(ConfigManager.COLUMNS_INDEX, fieldBlockRegion);
        textureRegionManager.add(ConfigManager.SPARK_INDEX, sparkTextureRegion);
        textureRegionManager.add(ConfigManager.BOMB_INDEX, bomEffectRegion);
        textureRegionManager.add(ConfigManager.CYCLONE_INDEX, holeEffectrRegion);
        textureRegionManager.add(ConfigManager.LEVEL_INDEX, levelUpRegion);
        textureRegionManager.add(ConfigManager.STAR_INDEX, starRegion);
        textureRegionManager.add(ConfigManager.THUNDER_INDEX, thunderRegion);

    }


    @Override
    public Scene onLoadScene(BaseGameActivity mBaseGame) {
        // TODO Auto-generated method stub
        resumeGame();
        // AdManager.loadAd(mBaseGame);


        if (scene != null) {
            resumeBgMusic();
            backgroundView.onloadResource();
            if (isResetGame) {
                isResetGame = false;
                initNextLevel(scene);
            }
            return scene;
        }
        scene = new Scene();


        game = new TabletController(mBaseGame, textureRegionManager);
        this.gameView = new TabletView(game, textureRegionManager);

        game.onLoadScene(scene, gameView);
        backgroundView = new BackgroundView();
        backgroundView.onloadResource(mBaseGame);
        settingButton.onLoadScene(mBaseGame, scene);
        // scene.registerUpdateHandler(new TimerHandler(0.05f, true,
        // timerCallback));

        // scene.attachChild(soundBtn);
        // scene.registerTouchArea(soundBtn);

        scene.attachChild(controlBtn);
        scene.registerTouchArea(controlBtn);
        // scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
        final VertexBufferObjectManager vertexBufferObjectManager = mBaseGame
                .getVertexBufferObjectManager();

        backgroundView.onloadScene(0, 0, scene, vertexBufferObjectManager);

        createHUD(scene);

        scene.registerUpdateHandler(new IUpdateHandler() {

            @Override
            public void reset() {
                // TODO Auto-generated method stub
                // isGameOver = false;

            }

            @Override
            public void onUpdate(float pSecondsElapsed) {
                // TODO Auto-generated method stub
                settingButton.timerCallBack();

                if (isPauseGame) {
                    return;
                }
                if (isGameOver) {
                    return;
                }
                if (movingStatus == MOVE_LEFT) {
                    game.moveLeft();
                } else if (movingStatus == MOVE_RIGHT) {
                    game.moveRight();
                } else if (movingStatus == ROTATE) {
                    game.rotate();
                } else if (movingStatus == DROP) {
                    game.drop();
                }
                movingStatus = 0;
                game.run();

                if (game.isLost()) {
                    stopBgMusic();
                    SoundManager.playSound(5, SoundManager.loseSoundID);
                    isGameOver = true;
                    ConfigManager.finalScore = ConfigManager.GAME_STATUS[ConfigManager.INDEX_GAME_SCORE];
                    ConfigManager.finalLevel = ConfigManager.GAME_STATUS[ConfigManager.INDEX_GAME_LEVEL];
                    ConfigManager.GAME_STATUS = null;
                    ConfigManager.GAME_FIELD = null;
                    showGameOverDialog();
                }

                try {
                    Thread.sleep(60);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        resumeBgMusic();

        return scene;
    }

    @Override
    public void onUnloadResoures(BaseGameActivity mBaseGame) {
        // TODO Auto-generated method stub

    }

    float velocityX = 0;
    float velocityY = 0;
    float downVelocityX = 0;
    float downVelocityY = 0;

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        if (isPauseGame) {
            return true;
        }
        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
            velocityX = pSceneTouchEvent.getX();
            velocityY = pSceneTouchEvent.getY();
            downVelocityX = pSceneTouchEvent.getX();
            downVelocityY = pSceneTouchEvent.getY();

        } else if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_MOVE) {
            if (preMovingStatus != 0) {
                if (preMovingStatus == MOVE_RIGHT
                        && pSceneTouchEvent.getX() - velocityX >= 0) {
                    movingStatus = MOVE_RIGHT;
                    preMovingStatus = movingStatus;
                    velocityX = pSceneTouchEvent.getX();
                    velocityY = pSceneTouchEvent.getY();
                    return true;
                }
                if (preMovingStatus == MOVE_LEFT
                        && pSceneTouchEvent.getX() - velocityX <= 0) {
                    movingStatus = MOVE_LEFT;
                    preMovingStatus = movingStatus;
                    velocityX = pSceneTouchEvent.getX();
                    velocityY = pSceneTouchEvent.getY();
                    return true;
                }

                if (pSceneTouchEvent.getX() - velocityX > SWIPE_MIN_DISTANCE / 2) {
                    movingStatus = MOVE_RIGHT;
                    preMovingStatus = movingStatus;
                    velocityX = pSceneTouchEvent.getX();
                    velocityY = pSceneTouchEvent.getY();
                    return true;
                    // return true;
                } else if (velocityX - pSceneTouchEvent.getX() > SWIPE_MIN_DISTANCE / 2) {
                    movingStatus = MOVE_LEFT;
                    preMovingStatus = movingStatus;
                    velocityX = pSceneTouchEvent.getX();
                    velocityY = pSceneTouchEvent.getY();
                    return true;
                }
            } else if (Math.abs(pSceneTouchEvent.getX() - velocityX) > Math
                    .abs(pSceneTouchEvent.getY() - velocityY)) {
                if (pSceneTouchEvent.getX() - velocityX > SWIPE_MIN_DISTANCE) {
                    movingStatus = MOVE_RIGHT;
                    preMovingStatus = movingStatus;
                    velocityX = pSceneTouchEvent.getX();
                    velocityY = pSceneTouchEvent.getY();
                    return true;
                    // return true;
                } else if (velocityX - pSceneTouchEvent.getX() > SWIPE_MIN_DISTANCE) {
                    movingStatus = MOVE_LEFT;
                    preMovingStatus = movingStatus;
                    velocityX = pSceneTouchEvent.getX();
                    velocityY = pSceneTouchEvent.getY();
                    return true;
                }
                // Vertical
            }
        } else if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
            preMovingStatus = 0;
            if (Math.abs(pSceneTouchEvent.getX() - downVelocityX) < SWIPE_MIN_DISTANCE / 2
                    && Math.abs(pSceneTouchEvent.getY() - downVelocityY) < SWIPE_MIN_DISTANCE / 2) {
                SoundManager.playSound(0.2f, SoundManager.tickSoundID);
                movingStatus = ROTATE;
                return true;
                // return true;
            } else {
                if (pSceneTouchEvent.getY() - velocityY > SWIPE_MIN_DISTANCE) {
                    SoundManager.playSound(0.5f, SoundManager.tickSoundID);
                    movingStatus = DROP;
                    velocityX = pSceneTouchEvent.getX();
                    velocityY = pSceneTouchEvent.getY();
                    return true;
                    // return true;
                }
            }
        }
        return true;

    }


}
