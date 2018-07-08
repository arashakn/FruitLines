package udc.fruitcolumns.logic;

import java.util.List;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.BitmapFont;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.HorizontalAlign;



public class RecordText {

	// private boolean isDown = true;
	// private boolean isRight = true;
	// private boolean isLeft = true;
	// private boolean isUp = true;

	private Text scoreText;
	private List<ITextureRegion> listTextureRegions;
	private BaseGameActivity mBaseGame;
	long timeChangeState;
	int currentState = 0;
	boolean isClearEntity = false;
	private boolean isMoving = true;

//	public RecordText(BaseGameActivity mBaseGame, Font font, float dx,
//			float dy, String value) {
//		scoreText = new Text(dx, dy, font, value, 100,
//				new org.andengine.entity.text.TextOptions(
//						HorizontalAlign.CENTER),
//				mBaseGame.getVertexBufferObjectManager());
//		scoreText.setPosition(dx - scoreText.getWidth() / 2,
//				dy - scoreText.getHeight());
//	}
	
	public RecordText(BaseGameActivity mBaseGame, BitmapFont font, float dx,
			float dy, String value) {
		scoreText = new Text(dx, dy, font, value, 100,
				new org.andengine.entity.text.TextOptions(
						HorizontalAlign.LEFT),
				mBaseGame.getVertexBufferObjectManager());
		scoreText.setPosition(dx+30 - scoreText.getWidth() / 2,
				dy - scoreText.getHeight());
	}

	public Text getScoreText() {
		return scoreText;
	}

	public void animate() {
		if (!isClearEntity) {
			if (System.currentTimeMillis() - timeChangeState > 100) {
				if (isMoving) {
					timeChangeState = System.currentTimeMillis();
					currentState++;
					scoreText.setPosition(scoreText.getX(),
							scoreText.getY() - 20);
					if (currentState >= 8) {
						isClearEntity = true;
					}
				} else {
					timeChangeState = System.currentTimeMillis();
					currentState++;
					if (currentState > 10) {
						if (currentState % 2 == 0) {
							scoreText.setVisible(false);
						} else {
							scoreText.setVisible(true);
						}
					}
					if (currentState > 17) {
						isClearEntity = true;
					}
				}
			}
		}

	}

	public void attachScene(Scene scene) {
		scene.attachChild(scoreText);
	}

	public void detachScene(Scene scene) {
		scene.detachChild(scoreText);
		scoreText.dispose();
		scoreText = null;
	}

	public boolean isMoving() {
		return isMoving;
	}

	public void setMoving(boolean isMoving) {
		this.isMoving = isMoving;
	}

}
