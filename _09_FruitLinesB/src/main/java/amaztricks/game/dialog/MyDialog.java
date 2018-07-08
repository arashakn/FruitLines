package amaztricks.game.dialog;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.BitmapFont;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;

import amaztricks.game.manager.ConfigManager;
import android.content.res.AssetManager;

public class MyDialog {
	private Rectangle shadowRectangle;
	private BitmapTextureAtlas resultBoardAtlas;
	private ITextureRegion resultBoardRegion;
	private Sprite resultBoard;
	protected int resultBoardWidth = 350;
	protected int resultBoardHeight = 250;
	protected int resultBoardDx = (int) (ConfigManager.CAMERA_WIDTH - resultBoardWidth) / 2;
	protected int resultBoardDy = (int) (ConfigManager.CAMERA_HEIGHT - resultBoardHeight) / 2+60;

	protected ButtonSprite[] buttonSpr;
	protected int buttonWidth = 200;
	protected int buttonHeight = 190;

	protected BaseGameActivity mBaseGame;
	protected Sprite title;
	protected int titleWidth;
	protected int titleHeight;
	protected Text content;

	public MyDialog(BaseGameActivity mBaseGame) {
		this.mBaseGame = mBaseGame;
		shadowRectangle = new Rectangle(0, 0, ConfigManager.CAMERA_WIDTH,
				ConfigManager.CAMERA_HEIGHT,
				mBaseGame.getVertexBufferObjectManager());
		shadowRectangle.setAlpha(0.5f);
	}

	public void onloadResource() {
		resultBoardAtlas = new BitmapTextureAtlas(getTextureManager(),
				resultBoardWidth, resultBoardHeight, TextureOptions.BILINEAR);
		resultBoardRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(resultBoardAtlas, getAssets(),
						"dialog/bg_dialog.png", 0, 0);
		resultBoardAtlas.load();
		resultBoard = new Sprite(resultBoardDx, resultBoardDy,
				resultBoardRegion, getVertexBufferObjectManager());

		BitmapFont dialogFont = new BitmapFont(mBaseGame.getTextureManager(),
				mBaseGame.getAssets(), "font/bestfont.txt");
		dialogFont.load();

		content = new Text(0, 0, dialogFont, "", 200,
				mBaseGame.getVertexBufferObjectManager());

	}

	public void onloadScene() {

		mBaseGame.getEngine().getScene().attachChild(resultBoard);
		mBaseGame.getEngine().getScene().attachChild(title);
		mBaseGame.getEngine().getScene().attachChild(content);
		title.setPosition(
				resultBoardDx + resultBoardWidth / 2 - title.getWidth() / 2,
				resultBoardDy - title.getHeight() / 2);
		content.setPosition(resultBoardDx, resultBoardDy + 20);
		for (int i = 0; i < buttonSpr.length; i++) {
			mBaseGame.getEngine().getScene().attachChild(buttonSpr[i]);
		}

	}

	public void onloadScene(Scene scene) {

		title.setPosition(
				resultBoardDx + resultBoardWidth / 2 - title.getWidth() / 2,
				resultBoardDy - title.getHeight() / 2);
		content.setPosition(resultBoardDx, resultBoardDy + 20);
		// for (int i = 0; i < buttonSpr.length; i++) {
		// scene.attachChild(buttonSpr[i]);
		// }

	}

	protected VertexBufferObjectManager getVertexBufferObjectManager() {
		// TODO Auto-generated method stub
		return mBaseGame.getVertexBufferObjectManager();
	}

	protected AssetManager getAssets() {
		// TODO Auto-generated method stub
		return mBaseGame.getAssets();
	}

	protected TextureManager getTextureManager() {
		// TODO Auto-generated method stub
		return mBaseGame.getTextureManager();
	}

	public void action(boolean setActive) {
		if (mBaseGame.getEngine().getScene() != null) {

			Scene scene = mBaseGame.getEngine().getScene();
			if (setActive) {
				scene.attachChild(shadowRectangle);
				scene.attachChild(resultBoard);
				scene.attachChild(title);
				scene.attachChild(content);
			} else {
				scene.detachChild(shadowRectangle);
				scene.detachChild(resultBoard);
				scene.detachChild(title);
				scene.detachChild(content);
			}

			for (int i = 0; i < buttonSpr.length; i++) {
				if (setActive) {
					scene.attachChild(buttonSpr[i]);
					mBaseGame.getEngine().getScene()
							.registerTouchArea(buttonSpr[i]);

				} else {
					scene.detachChild(buttonSpr[i]);
					mBaseGame.getEngine().getScene()
							.unregisterTouchArea(buttonSpr[i]);

				}
			}

		}
	}

}
