package com.hanastudio.columns.activity;

import android.media.AudioManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.startapp.android.publish.adsCommon.StartAppAd;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.SimpleLayoutGameActivity;

import amaztricks.game.manager.ConfigManager;

public class MainActivity extends SimpleLayoutGameActivity {

    public static String PREF_FILE_NAME = "localstore_fruitlines_";
    private ZoomCamera mZoomCamera;

    public static SceneMainMenu mainMenuScene;
    public static SceneTetris playScene;

    public static final int FLASH = 1;
    public static final int MAIN_MENU = 2;
    public static final int PLAY_SCENE = 3;
    public static final int SETTING_SCENE = 4;
    public static int curScene = MAIN_MENU;
    public static float initCenterX, initCenterY;
    static StartAppAd startAppAd;
    InterstitialAd interstitialAd;
    boolean isShowAdd = false;

    @Override
    public EngineOptions onCreateEngineOptions() {
        StartAppAd.init(this, getString(R.string.startapp_app_id), getString(R.string.startapp_dev_id));
        startAppAd = new StartAppAd(this);
        StartAppAd.disableSplash();
        setupAds();
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.mZoomCamera = new ZoomCamera(0, 0, ConfigManager.CAMERA_WIDTH,
                ConfigManager.CAMERA_HEIGHT);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.mZoomCamera = new ZoomCamera(0, 0, ConfigManager.CAMERA_WIDTH,
                ConfigManager.CAMERA_HEIGHT);
        final EngineOptions engineOptions = new EngineOptions(true,
                org.andengine.engine.options.ScreenOrientation.PORTRAIT_FIXED,
                new FillResolutionPolicy(), this.mZoomCamera);
        return engineOptions;
    }


    public void setupAds() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                interstitialAd = new InterstitialAd(MainActivity.this);
                interstitialAd.setAdUnitId(getString(R.string.interstitialAd));
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
    public void onCreateResources() {
        initCenterX = getEngine().getCamera().getCenterX();
        initCenterY = getEngine().getCamera().getCenterY();
        mainMenuScene = new SceneMainMenu(this);
        mainMenuScene.onLoadResources(this);
        MainActivity.playScene = new SceneTetris(this);

    }

    @Override
    public Scene onCreateScene() {

        return mainMenuScene.onLoadScene(this);
    }

    @Override
    protected synchronized void onResume() {
        // TODO Auto-generated method stub
        if (curScene == PLAY_SCENE) {
            if (playScene != null) {
                playScene.onResume();
            }
        }
        startAppAd.onResume();
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        // if (startAppAd != null) {
        startAppAd.onBackPressed();
        // }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        if (curScene == PLAY_SCENE) {
            if (playScene != null) {
                playScene.onPause();
            }
        }
        super.onPause();
        startAppAd.onPause();

    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        if (mainMenuScene != null) {
            mainMenuScene.onStop();
        }
        super.onStop();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (curScene) {
                case MAIN_MENU:
                    mainMenuScene.onKeyDown(keyCode, event);
                    break;
                case PLAY_SCENE:
                    playScene.onKeyDown(keyCode, event);
                    break;
                default:
                    break;
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.main;
    }

    @Override
    protected int getRenderSurfaceViewID() {
        return R.id.xmllayoutRenderSurfaceView;
    }

}
