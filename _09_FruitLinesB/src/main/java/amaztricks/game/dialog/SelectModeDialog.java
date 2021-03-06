package amaztricks.game.dialog;

import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

import com.hanastudio.columns.activity.MainActivity;
import com.hanastudio.columns.activity.R;
import com.hanastudio.columns.activity.SceneMainMenu;

import amaztricks.game.manager.ConfigManager;
import amaztricks.game.manager.HighScoreManager;
import andengine.game.scenemanager.SceneSwitcher;
import android.app.Activity;

public class SelectModeDialog extends MyDialog {

	public SelectModeDialog(BaseGameActivity mBaseGame) {
		super(mBaseGame);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onloadResource() {
		super.onloadResource();
		titleWidth = 256;
		titleHeight = 64;
		BitmapTextureAtlas textureAtlas = new BitmapTextureAtlas(
				getTextureManager(), titleWidth, titleHeight,
				TextureOptions.BILINEAR);
		ITextureRegion textureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(textureAtlas, getAssets(), "dialog/pause.png",
						0, 0);
		textureAtlas.load();
		title = new Sprite(0, 0, textureRegion, getVertexBufferObjectManager());

		BitmapTextureAtlas[] buttonAtlas = new BitmapTextureAtlas[2];
		TiledTextureRegion[] buttonRegion = new TiledTextureRegion[buttonAtlas.length];
		buttonSpr = new ButtonSprite[buttonAtlas.length];
		MyButtonCLickListener onClickListener = new MyButtonCLickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// TODO Auto-generated method stub
				super.onClick(pButtonSprite, pTouchAreaLocalX, pTouchAreaLocalY);
				if (pButtonSprite == buttonSpr[1]) {
					// reset
					SceneMainMenu.isSelectMode = false;
					MainActivity.curScene = MainActivity.PLAY_SCENE;
					MainActivity.playScene.resetExistedFile();
					SceneSwitcher.switchScene(mBaseGame,
							MainActivity.playScene, false);

				} else if (pButtonSprite == buttonSpr[0]) {
					// continue
					MainActivity.curScene = MainActivity.PLAY_SCENE;
					SceneSwitcher.switchScene(mBaseGame,
							MainActivity.playScene, false, true);
					SceneMainMenu.isSelectMode = false;
				}
				action(false);
			}
		};

		for (int i = 0; i < buttonAtlas.length; i++) {
			int buttonDx = (int) (ConfigManager.CAMERA_WIDTH) / 2;
			int buttonDy = (int) (resultBoardDy + resultBoardHeight / 2 - buttonHeight / 4);

			buttonAtlas[i] = new BitmapTextureAtlas(getTextureManager(),
					buttonWidth, buttonHeight, TextureOptions.BILINEAR);
			if (i == 1) {
				buttonRegion[i] = BitmapTextureAtlasTextureRegionFactory
						.createTiledFromAsset(buttonAtlas[i],
								mBaseGame.getAssets(), "dialog/reset.png", 0,
								0, 1, 2);
				buttonDx += (30);

			} else if (i == 0) {
				buttonRegion[i] = BitmapTextureAtlasTextureRegionFactory
						.createTiledFromAsset(buttonAtlas[i],
								mBaseGame.getAssets(), "dialog/continue.png",
								0, 0, 1, 2);
				buttonDx -= (30 + buttonWidth);

			}
			buttonAtlas[i].load();

			buttonSpr[i] = new ButtonSprite(buttonDx, buttonDy,
					buttonRegion[i], mBaseGame.getVertexBufferObjectManager(),
					onClickListener);

		}
	}

}
