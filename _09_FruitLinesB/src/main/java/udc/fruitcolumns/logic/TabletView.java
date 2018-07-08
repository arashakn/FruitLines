package udc.fruitcolumns.logic;

import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.batch.DynamicSpriteBatch;
import org.andengine.entity.sprite.batch.SpriteBatch;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.modifier.IModifier;

import android.util.Log;

public class TabletView {
	private int[][] matrix;
	private BaseGameActivity mBaseGame;
	private AnimatedSprite[][] tabletSpriteView;
	private List<ITextureRegion> listTextureRegions;
	private Scene scene;
	private DynamicSpriteBatch itemSpriteBatch;
	public static float cellWidth = 50;
	public static float cellHeight = 50;
	public static float marginX = 40 - cellWidth;
	public static float marginY = 180 - cellHeight;

	public TabletView(TabletController tabletController,
			List<ITextureRegion> listTextureRegions) {
		this.mBaseGame = tabletController.getmBaseGame();
		this.listTextureRegions = listTextureRegions;
	}

	public void onloadScene(final Scene scene, final int[][] matrix) {
		this.matrix = matrix;
		if (this.scene != null) {
			return;
		}
		this.scene = scene;
		tabletSpriteView = new AnimatedSprite[matrix.length][matrix[0].length];
		ITextureRegion tabletTextureRegion = listTextureRegions.get(0);
		for (int i = 0; i < TabletController.ROWS - 1; i++) {
			for (int j = 1; j < TabletController.COLS - 1; j++) {
				tabletSpriteView[i][j] = new AnimatedSprite(marginX + j
						* cellWidth, marginY + i * cellHeight,
						(ITiledTextureRegion) tabletTextureRegion,
						mBaseGame.getVertexBufferObjectManager());
				tabletSpriteView[i][j].setVisible(false);
				scene.attachChild(tabletSpriteView[i][j]);

			}
		}
		// itemSpriteBatch = new DynamicSpriteBatch(0, 0,
		// tabletTextureRegion.getTexture(), (TabletController.ROWS - 2)
		// * (TabletController.COLS - 2),
		// mBaseGame.getVertexBufferObjectManager()) {
		// @Override
		// protected boolean onUpdateSpriteBatch() {
		// // TODO Auto-generated method stub
		// for (int i = 1; i < TabletController.ROWS - 1; i++) {
		// for (int j = 1; j < TabletController.COLS - 1; j++) {
		// draw(tabletSpriteView[i][j]);
		// }
		// }
		// return true;
		// }
		// };
		// scene.attachChild(itemSpriteBatch);

	}

	public void attachTablet(Tablet tablet) {

		tablet.attachScene(scene);

	}

	public void detachTablet(Tablet tablet) {
		tablet.detachScene(scene);

	}

	public void drawTablet(int i, int j, int orientation) {
		if (tabletSpriteView[i][j] != null) {
			if (orientation < 15) {
				tabletSpriteView[i][j].setCurrentTileIndex(orientation);
				tabletSpriteView[i][j].setVisible(true);
			} else {
				tabletSpriteView[i][j].setVisible(false);
			}
		}
	}

	public void returnToOriginalState(int i, int j) {
		tabletSpriteView[i][j].setVisible(false);
		tabletSpriteView[i][j].setAlpha(1f);
		tabletSpriteView[i][j].setScale(1f);
	}

	public void resetPosition() {
		for (int i = 0; i < TabletController.ROWS - 1; i++) {
			for (int j = 1; j < TabletController.COLS - 1; j++) {
				tabletSpriteView[i][j].setPosition(marginX + j * cellWidth,
						marginY + i * cellHeight);
			}
		}

	}

	public AnimatedSprite[][] getTabletSpriteView() {
		return tabletSpriteView;
	}

	public void setTabletSpriteView(AnimatedSprite[][] tabletSpriteView) {
		this.tabletSpriteView = tabletSpriteView;
	}

	public void updateTabletField() {
		for (int i = 0; i < TabletController.ROWS - 1; i++) {
			for (int j = 1; j < TabletController.COLS - 1; j++) {
				drawTablet(i, j, matrix[i][j]);
			}
		}
	}

}
