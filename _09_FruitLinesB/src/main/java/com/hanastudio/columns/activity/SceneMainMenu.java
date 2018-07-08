package com.hanastudio.columns.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Handler;
import android.view.KeyEvent;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

import amaztricks.game.dialog.SelectModeDialog;
import amaztricks.game.dialog.ShareDialog;
import amaztricks.game.manager.ConfigManager;
import amaztricks.game.manager.HighScoreManager;
import amaztricks.game.manager.SoundManager;
import andengine.game.scenemanager.IScenePattern;
import andengine.game.scenemanager.SceneSwitcher;

public class SceneMainMenu implements IScenePattern {

    private BitmapTextureAtlas bgAtlas;
    ITextureRegion bgRegion;
    private int bgWidth = 480;
    private int bgHeight = 800;

    private TiledTextureRegion[] buttonRegions = new TiledTextureRegion[3];
    private ButtonSprite[] btnSprite = new ButtonSprite[buttonRegions.length];

    private int[] buttonWidth = new int[]{250, 250, 250};
    private int[] buttonHeight = new int[]{176, 176, 176};
    private int[] buttonDx = new int[]{240 - 125, 240 - 125, 240 - 125};
    private int[] buttonDy = new int[]{350, 350 + 88 + 58,
            350 + (88 + 58) * 2};
    private Scene scene;

    public static boolean doneLoadResource;

    // private Rectangle helpBg;
    //
    // private ITextureRegion helpRegion;
    // private Sprite helpSpr;
    // private int helpWidth = 700;
    // private int helpHeight = 450;
    // private boolean isHelp = false;

    private SpriteBackground bgSpriteBackground;

    private BaseGameActivity mBaseGame;
    private Intent highscoreIntent;

    // SettingButton settingButton;
    // Settings settings;
    InterstitialAd interstitialAd;

    public SceneMainMenu(BaseGameActivity mBaseGameActivity) {
        this.mBaseGame = mBaseGameActivity;
        highscoreIntent = new Intent(mBaseGame,
                HighScoreManager.class);
        SoundManager.init(mBaseGameActivity);
        SoundManager.loadMenuSound(mBaseGameActivity);
        SharedPreferences sharedPreferences = mBaseGame.getSharedPreferences(
                MainActivity.PREF_FILE_NAME, Context.MODE_PRIVATE);
        ConfigManager.IS_MUSIC = sharedPreferences.getBoolean("is_music", true);
        ConfigManager.IS_SOUND = sharedPreferences.getBoolean("is_sound", true);
        ConfigManager.IS_VIETNAMESE = sharedPreferences.getBoolean(
                "is_vietnamese", true);
        ConfigManager.CONTROL = sharedPreferences.getInt("control", 0);
        setupAds();

    }

    boolean isShowAdd = false;

    public void setupAds() {
        mBaseGame.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                interstitialAd = new InterstitialAd(mBaseGame);
                interstitialAd.setAdUnitId(mBaseGame.getString(R.string.interstitialAd));
                AdRequest.Builder builder = new AdRequest.Builder();
                AdRequest ad = builder.build();
                interstitialAd.loadAd(ad);
                interstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        if (!isShowAdd) {
                            interstitialAd.show();
                            isShowAdd = true;
                        }
                    }

                    @Override
                    public void onAdClosed() {
                        // Proceed to the next level.
                        AdRequest.Builder builder = new AdRequest.Builder();
                        AdRequest ad = builder.build();
                        interstitialAd.loadAd(ad);
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        super.onAdFailedToLoad(errorCode);
                    }
                });
            }
        });

    }

    @Override
    public void onLoadResources(BaseGameActivity mBaseGame) {

        bgAtlas = new BitmapTextureAtlas(mBaseGame.getTextureManager(),
                bgWidth, bgHeight, TextureOptions.BILINEAR);
        bgRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                bgAtlas, mBaseGame.getAssets(), "gfx/bg_main_menu.jpg", 0, 0);
        bgAtlas.load();

        // resource of button
        /*
         * 0 = relax 1 = time 2 = setting 3 = high score 4 = contact
		 */
        BitmapTextureAtlas[] buttonAtlas = new BitmapTextureAtlas[buttonRegions.length];
        for (int i = 0; i < buttonAtlas.length; i++) {
            // if (i == 0) {
            buttonAtlas[i] = new BitmapTextureAtlas(
                    mBaseGame.getTextureManager(), buttonWidth[i],
                    buttonHeight[i], TextureOptions.BILINEAR);

            buttonRegions[i] = BitmapTextureAtlasTextureRegionFactory
                    .createTiledFromAsset(buttonAtlas[i],
                            mBaseGame.getAssets(), "gfx/menu_btn" + i + ".png",
                            0, 0, 1, 2);
            buttonAtlas[i].load();
        }
        doneLoadResource = true;
    }

    public void showInterstitialAd(final Runnable then) {
        mBaseGame.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (interstitialAd.isLoaded()) {
                    if (then != null) {
                        interstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                mBaseGame.runOnUpdateThread(then);
                                AdRequest.Builder builder = new AdRequest.Builder();
                                AdRequest ad = builder.build();
                                interstitialAd.loadAd(ad);
                            }
                        });
                    }
                    interstitialAd.show();
                }
            }
        });
    }

    @Override
    public Scene onLoadScene(final BaseGameActivity mBaseGame) {

        if (!isShowAdd) {

            showInterstitialAd(new Runnable() {
                @Override
                public void run() {
                    isShowAdd = true;
                }
            });
        }


        playBgMusic();

        if (scene != null) {
            mBaseGame
                    .getEngine()
                    .getCamera()
                    .setCenter(MainActivity.initCenterX,
                            MainActivity.initCenterY);
            for (int i = 0; i < buttonRegions.length; i++) {
                scene.registerTouchArea(btnSprite[i]);

            }
            return scene;
        }
        scene = new Scene();

        // add background for main menu
        Sprite sprite = new Sprite(0, 0, bgRegion,
                mBaseGame.getVertexBufferObjectManager());
        bgSpriteBackground = new SpriteBackground(sprite);
        scene.setBackground(bgSpriteBackground);
        // add button into scene
        for (int i = 0; i < buttonRegions.length; i++) {
            final int indexTouch = i;

            // using button sprite to express animation of button easily
            btnSprite[i] = new ButtonSprite(buttonDx[i], buttonDy[i],
                    buttonRegions[i], mBaseGame.getVertexBufferObjectManager(),
                    new OnClickListener() {
                        @Override
                        public void onClick(ButtonSprite arg0, float arg1,
                                            float arg2) {
                            if (isSelectMode) {
                                return;
                            }
                            SoundManager.playSound(1f,
                                    SoundManager.touchSoundId);
                            saveSetting();
                            switch (indexTouch) {
                                case 0:

                                    if (MainActivity.playScene
                                            .isNeededResetingGame()) {
                                        SceneSwitcher.switchScene(mBaseGame,
                                                MainActivity.playScene, false);
                                        MainActivity.curScene = MainActivity.PLAY_SCENE;

                                    } else {
                                        continueOrNewgame();

                                    }
                                    isShowAdd = false;

                                    break;
                                case 1:
                                    initHighScore();
                                    isShowAdd = false;
                                    break;
                                case 2:
                                    initMoreGame();
                                    isShowAdd = false;
                                    break;
                                default:
                                    break;
                            }
                        }

                    });
            scene.attachChild(btnSprite[i]);

        }
        selectMode = new SelectModeDialog(mBaseGame);
        selectMode.onloadResource();
        selectMode.onloadScene(scene);
        selectMode.action(false);
        for (int i = 0; i < buttonRegions.length; i++) {
            scene.registerTouchArea(btnSprite[i]);

        }


        scene.setTouchAreaBindingOnActionDownEnabled(true);
        scene.setOnSceneTouchListenerBindingOnActionDownEnabled(true);

        return scene;
    }

    @Override
    public void onUnloadResoures(BaseGameActivity mBaseGame) {
        // TODO Auto-generated method stub
        doneLoadResource = false;
        scene = null;
    }

    public static boolean isSelectMode = false;
    private SelectModeDialog selectMode;

    public void resumeGame() {

        // TODO Auto-generated method stub
        new Handler().post(new Runnable() {
            public void run() {
                isSelectMode = false;
                selectMode.action(false);
                for (int i = 0; i < buttonRegions.length; i++) {
                    scene.registerTouchArea(btnSprite[i]);

                }
            }
        });

    }

    public void selectGameMode() {
        mBaseGame.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                isSelectMode = true;
                selectMode.action(true);
                for (int i = 0; i < buttonRegions.length; i++) {
                    scene.unregisterTouchArea(btnSprite[i]);

                }
            }
        });

    }

    // private void closeHelp() {
    // scene.unregisterTouchArea(helpSpr);
    // scene.detachChild(helpSpr);
    // isHelp = false;
    // }

    ITimerCallback timerCallback = new ITimerCallback() {
        @Override
        public void onTimePassed(TimerHandler timerHandler) {
            // TODO Auto-generated method stub

            // settingButton.timerCallback.onTimePassed(timerHandler);

        }

    };

    AlertDialog alert = null;

    private void initHighScore() {

        mBaseGame.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mBaseGame.startActivity(highscoreIntent);
            }
        });

    }

    private void initReview() {
        mBaseGame.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mBaseGame.getString(R.string.app_list_link)));
                mBaseGame.startActivity(intent);
                // Toast.makeText(mBaseGame, "Can not perform this action now",
                // 1000).show();

            }
        });
    }

    private void initMoreGame() {
        mBaseGame.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mBaseGame
                        .getString(R.string.app_list_link)));
                mBaseGame.startActivity(intent);
                // Toast.makeText(mBaseGame, "Can not perform this action now",
                // 1000).show();

            }
        });
    }


    private void continueOrNewgame() {

        selectGameMode();

    }

    private void initAlertQuit() {

        String message = mBaseGame.getString(R.string.welcome);
        String quit = mBaseGame.getString(R.string.quitBtn);
        String share = mBaseGame.getString(R.string.shareBtn);
        String rate = mBaseGame.getString(R.string.rateBtn);

        final String finalShare = share;

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(mBaseGame);
        alt_bld.setCancelable(true)
                .setMessage(message)
                .setNegativeButton(quit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        saveSetting();
                        SoundManager.release();
                        ((MainActivity) mBaseGame).onBackPressed();

                    }
                })
                .setNeutralButton(share, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        ShareDialog.createShareOption(mBaseGame);
                    }
                })
                .setPositiveButton(rate, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        initReview();
                    }
                });
        AlertDialog alert = alt_bld.create();
        // Title for AlertDialog
        alert.show();

    }

    public void onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // if (isHelp) {
            // closeHelp();
            // } else
            if (isSelectMode) {
                resumeGame();
            } else {
                initAlertQuit();
            }
        }
    }

    public static void playSlideSound(float volume) {
        // if (GameConfig.IS_SOUND) {
        // SceneMainMenu.androidAudio.getSoundPool().play(
        // SceneMainMenu.slideSoundID, volume, volume, 0, 0, 0);
        //
        // }
    }

    public static void stopBgMusic() {
        // if(bgMusic!=null){
        // if(!bgMusic.isStopped()){
        // bgMusic.stop();
        // }
        // }
    }

    public static void playBgMusic() {
        // if(bgMusic!=null){
        // if(GameConfig.IS_MUSIC){
        // if(!bgMusic.isPlaying()){
        // bgMusic.play();
        // }
        // }
        // }
    }

    public void onStop() {

        // settingButton.isSetting = false;
        saveSetting();
    }

    private void saveSetting() {
        Editor editor = mBaseGame.getSharedPreferences(
                MainActivity.PREF_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean("is_music", ConfigManager.IS_MUSIC);
        editor.putBoolean("is_sound", ConfigManager.IS_SOUND);
        editor.putBoolean("is_vietnamese", ConfigManager.IS_VIETNAMESE);
        editor.putInt("control", ConfigManager.CONTROL);

        editor.commit();

    }

}
