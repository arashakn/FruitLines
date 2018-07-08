package com.hanastudio.columns.activity;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

import amaztricks.game.manager.ConfigManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class SettingGame {

	// setting Btn
	private TiledTextureRegion settingRegion;
	ButtonSprite settingBtn; //
	private int settingWidth = 120;
	private int settingHeight = 60;
	private int settingDx = 340;
	private int settingDy = 70;

	boolean isSetting = false;
	private ITextureRegion bgSettingRegion;
	Sprite bgSettingSpr;
	private int settingBgMaxWidth = 128;
	private int setttingBgMinWidth = 5;
	private int setttingBgHeight = 64;
	private int curWidth = 5;
	// music Btn
	private TiledTextureRegion musicRegion;
	ButtonSprite musicBtn; //
	private int musicWidth = 92;
	private int musicHeight = 46;
	private int musicDx = settingDx - settingBgMaxWidth + 10;
	private int musicDy = settingDy + (settingHeight - musicHeight) / 2;
	// sound Btn
	private TiledTextureRegion soundRegion;
	ButtonSprite soundBtn; //
	private int soundWidth = 92;
	private int soundHeight = 46;
	private int soundDx = musicDx + 60;
	private int soundDy = musicDy;

	public void onLoadResources(BaseGameActivity mBaseGame) {

		BitmapTextureAtlas bgSettingAtlas = new BitmapTextureAtlas(
				mBaseGame.getTextureManager(), 256, 128,
				TextureOptions.BILINEAR);
		bgSettingRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(bgSettingAtlas, mBaseGame.getAssets(),
						"settings/settingbg.png", 0, 0);
		bgSettingAtlas.load();

		BitmapTextureAtlas musicAtlas = new BitmapTextureAtlas(
				mBaseGame.getTextureManager(), musicWidth, musicHeight,
				TextureOptions.BILINEAR);
		musicRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(musicAtlas, mBaseGame.getAssets(),
						"settings/music.png", 0, 0, 2, 1);
		musicAtlas.load();

		BitmapTextureAtlas soundAtlas = new BitmapTextureAtlas(
				mBaseGame.getTextureManager(), soundWidth, soundHeight,
				TextureOptions.BILINEAR);
		soundRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(soundAtlas, mBaseGame.getAssets(),
						"settings/sound.png", 0, 0, 2, 1);
		soundAtlas.load();

		BitmapTextureAtlas settingAlas = new BitmapTextureAtlas(
				mBaseGame.getTextureManager(), settingWidth, settingHeight,
				TextureOptions.BILINEAR);
		settingRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(settingAlas, mBaseGame.getAssets(),
						"settings/setting.png", 0, 0, 2, 1);
		settingAlas.load();

	}

	public Scene onLoadScene(final BaseGameActivity mBaseGame, Scene scene) {

		ButtonSprite.OnClickListener onClickListener = new ButtonSprite.OnClickListener() {

			@Override
			public void onClick(ButtonSprite button, float arg1, float arg2) {
				// TODO Auto-generated method stub
				// SceneMainMenu.playClick(0.3f);

				if (button == settingBtn) {
					if (isSetting) {
						isSetting = false;
					} else {
						isSetting = true;
						MainActivity.playScene.pauseGame();
						bgSettingSpr.setVisible(true);
						button.setCurrentTileIndex(1);
					}
				} else if (button == musicBtn) {
					if (ConfigManager.IS_MUSIC) {
						ConfigManager.IS_MUSIC = false;
						if (MainActivity.playScene.bgMusic.isPlaying()) {
							MainActivity.playScene.bgMusic.stop();
						}
						button.setCurrentTileIndex(1);
					} else {
						ConfigManager.IS_MUSIC = true;
						MainActivity.playScene.bgMusic.play();
						button.setCurrentTileIndex(0);

					}
				} else if (button == soundBtn) {
					if (ConfigManager.IS_SOUND) {
						ConfigManager.IS_SOUND = false;
						button.setCurrentTileIndex(1);
					} else {
						ConfigManager.IS_SOUND = true;
						button.setCurrentTileIndex(0);
					}
				}

			}
		};

		float rectDx = settingDx - settingBgMaxWidth;
		float rectDy = settingDy + (settingHeight - setttingBgHeight) / 2;
		bgSettingSpr = new Sprite(rectDx, rectDy, bgSettingRegion,
				mBaseGame.getVertexBufferObjectManager());
		bgSettingSpr.setVisible(false);
		bgSettingSpr.setWidth(settingBgMaxWidth);
		scene.attachChild(bgSettingSpr);

		settingBtn = new ButtonSprite(settingDx, settingDy, settingRegion,
				mBaseGame.getVertexBufferObjectManager());
		settingBtn.setOnClickListener(onClickListener);

		scene.attachChild(settingBtn);
		scene.registerTouchArea(settingBtn);

		musicBtn = new ButtonSprite(musicDx, musicDy, musicRegion,
				mBaseGame.getVertexBufferObjectManager());
		musicBtn.setOnClickListener(onClickListener);
		musicBtn.setVisible(false);
		scene.attachChild(musicBtn);
		scene.registerTouchArea(musicBtn);

		soundBtn = new ButtonSprite(soundDx, soundDy, soundRegion,
				mBaseGame.getVertexBufferObjectManager());
		soundBtn.setOnClickListener(onClickListener);
		soundBtn.setVisible(false);
		scene.attachChild(soundBtn);
		scene.registerTouchArea(soundBtn);

		return scene;
	}

	// to use in synchronize thread
	public void timerCallBack() {
		if (!ConfigManager.IS_MUSIC) {
			musicBtn.setCurrentTileIndex(1);
		}
		if (!ConfigManager.IS_SOUND) {
			soundBtn.setCurrentTileIndex(1);
		}

		// TODO Auto-generated method stub
		if (bgSettingSpr.isVisible()) {
			if (isSetting) {
				if (!musicBtn.isVisible()) {
					showSettingButton(true);
				}

			} else {
				bgSettingSpr.setVisible(false);
			}

		} else {
			bgSettingSpr.setVisible(false);
			settingBtn.setCurrentTileIndex(0);

			if (musicBtn.isVisible()) {
				showSettingButton(false);
			}
		}

	}

	ITimerCallback timerCallback = new ITimerCallback() {
		@Override
		public void onTimePassed(TimerHandler timerHandler) {
			timerCallBack();

		}

	};

	public void activeSetting(boolean status) {
		isSetting = status;
	}

	private void showSettingButton(boolean isShow) {
		musicBtn.setVisible(isShow);
		soundBtn.setVisible(isShow);
	}

}
