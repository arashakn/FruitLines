package com.hanastudio.columns.activity;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.BitmapFont;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.HorizontalAlign;

import amaztricks.game.manager.ConfigManager;
import amaztricks.game.manager.SoundManager;
import android.util.Log;

public class BackgroundView {

	// private Scene scene;
	BitmapTextureAtlas playRegionTextureAtlas;
	ITextureRegion playRegionTextureRegion;
	private int fieldBlockWidth = 480;
	private int fieldBlockHeigh = 800;
	// static long score = 0;
	public static Text scoreText;
	private static int scoreDx = 240;
	private static int scoreDy = 125;

	// static int level = 1;

	// static int level = 1;
	public static Text levelText;
	private static int levelDx = 200;
	private static int levelDy = 60;

	public static long SCORE_RATE = 5;

	final long[] SCORE_LV = new long[] { 200, 400, 800, 1600, 3200, 6400, 12800,
			25600 };
	private static ScoreLevel[] scoreLevels = new ScoreLevel[7];

	public BackgroundView() {
		for (int i = 0; i < scoreLevels.length; i++) {
			scoreLevels[i] = new ScoreLevel(SCORE_LV[i]);
		}
	}
	


	public void onloadResource(BaseGameActivity mBaseGame) {
		playRegionTextureAtlas = new BitmapTextureAtlas(
				mBaseGame.getTextureManager(), fieldBlockWidth,
				fieldBlockHeigh, TextureOptions.BILINEAR);
		playRegionTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(playRegionTextureAtlas, mBaseGame.getAssets(),
						"gfx/bg_game.jpg", 0, 0);
		playRegionTextureAtlas.load();

		BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(
				mBaseGame.getTextureManager(), 512, 512,
				TextureOptions.BILINEAR);
		// create font, bg, start
		BitmapFont levelFont = new BitmapFont(mBaseGame.getTextureManager(),
				mBaseGame.getAssets(), "font/recordfont.fnt");
		levelFont.load();

		levelText = new Text(
				0,
				0,
				levelFont,
				String.valueOf(ConfigManager.GAME_STATUS[0]),
				100,
				new org.andengine.entity.text.TextOptions(HorizontalAlign.LEFT),
				mBaseGame.getVertexBufferObjectManager());
		levelText.setPosition(levelDx-levelText.getWidth()/2, levelDy - levelFont.getLineHeight());

		fontTexture = new BitmapTextureAtlas(mBaseGame.getTextureManager(),
				512, 512, TextureOptions.BILINEAR);
		// create font, bg, start

		BitmapFont scoreFont = new BitmapFont(mBaseGame.getTextureManager(),
				mBaseGame.getAssets(), "font/recordfont.fnt");
		scoreFont.load();

		scoreText = new Text(
				0,
				0,
				scoreFont,
				String.valueOf(ConfigManager.GAME_STATUS[1]),
				100,
				new org.andengine.entity.text.TextOptions(HorizontalAlign.LEFT),
				mBaseGame.getVertexBufferObjectManager());
		scoreText.setPosition(scoreDx- scoreText.getWidth() / 2, scoreDy - scoreFont.getLineHeight());

	}

	public void onloadResource() {
		scoreText.setText(String.valueOf(ConfigManager.GAME_STATUS[1]));
		levelText.setText(String.valueOf(ConfigManager.GAME_STATUS[0]));
		scoreText.setPosition(scoreDx - scoreText.getWidth() / 2,
				scoreText.getY());
		levelText.setPosition(levelDx - levelText.getWidth() / 2,
				levelText.getY());

	}

	public void onloadScene(float fieldX, float fieldY, Scene scene,
			VertexBufferObjectManager vbo) {
		// this.scene = scene;
		Sprite sprite = new Sprite(fieldX, fieldY, playRegionTextureRegion, vbo);
		scene.setBackground(new SpriteBackground(sprite));
		scene.attachChild(levelText);
		scene.attachChild(scoreText);

	}

	public static void upLevel(int lev) {
		ConfigManager.GAME_STATUS[0] = lev + 1;
		SoundManager.playLevelUp();
		levelText.setText(String.valueOf(ConfigManager.GAME_STATUS[0]));
		levelText.setPosition(levelDx - levelText.getWidth() / 2,
				levelText.getY());

	}

	public static void reset() {
		for (int i = 0; i < scoreLevels.length; i++) {
			if (scoreLevels[i] != null)
				scoreLevels[i].reset();
		}
	}

	public synchronized static boolean isUplevel() {
		// check up level
		
		boolean isUpLevel = false;
		for (int i = scoreLevels.length - 1; i >= ConfigManager.GAME_STATUS[0] - 1; i--) {
			// duyet cac level chua duoc up len
			if (!scoreLevels[i].isUpLevel) {
				// kiem tra xem co thoa man dieu kien up level khong
				if (scoreLevels[i].scoreLevel <= ConfigManager.GAME_STATUS[1]) {
					upLevel(i + 1);
					scoreLevels[i].isUpLevel = true;
					isUpLevel = true;
					break;
				}
			}
		}
		return isUpLevel;
	}

	public static void upScore(long scr,int dx,int dy,float scale) {
		if(scr>0){
			final long score = scr * Math.min(ConfigManager.GAME_STATUS[0]* SCORE_RATE,30);
			ConfigManager.GAME_STATUS[1] += score;
			SoundManager.playSound(2f, SoundManager.pointSoundID);
			scoreText.setText(String.valueOf(ConfigManager.GAME_STATUS[1]));
			scoreText.setPosition(scoreDx - scoreText.getWidth() / 2,
					scoreText.getY());
			MainActivity.playScene.game.addScoreText(String.valueOf(score), 0, dx, dy,1.2f*scale);	
		}
	
	}

	public static void upPlainScore(long scr) {
		ConfigManager.GAME_STATUS[1] += scr;
		SoundManager.playSound(2f, SoundManager.pointSoundID);
		scoreText.setText(String.valueOf(ConfigManager.GAME_STATUS[1]));
		scoreText.setPosition(scoreDx - scoreText.getWidth() / 2,
				scoreText.getY());
	}

	private class ScoreLevel {
		long scoreLevel;
		boolean isUpLevel = false;

		public ScoreLevel(long scoreLevel) {
			this.scoreLevel = scoreLevel;
		}

		public void reset() {
			isUpLevel = false;
		}

	}
}
