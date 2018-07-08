package udc.fruitcolumns.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.andengine.engine.handler.runnable.RunnableHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.CardinalSplineMoveModifier.CardinalSplineMoveModifierConfig;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.CardinalSplineMoveModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.particle.SpriteParticleSystem;
import org.andengine.entity.particle.emitter.CircleOutlineParticleEmitter;
import org.andengine.entity.particle.initializer.AccelerationParticleInitializer;
import org.andengine.entity.particle.initializer.BlendFunctionParticleInitializer;
import org.andengine.entity.particle.initializer.ColorParticleInitializer;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ColorParticleModifier;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.font.BitmapFont;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.math.MathUtils;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseLinear;

import com.hanastudio.columns.activity.BackgroundView;
import com.hanastudio.columns.activity.SceneTetris;

import amaztricks.game.manager.ConfigManager;
import amaztricks.game.manager.SoundManager;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.util.Log;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * 
 * @author thien
 */
public class TabletController {

	private static final int WALL = 16;
	private static final int EMPTY = 15;
	private static final int CHECK_POINT = 2;

	// private final int SMALL_BOMB_FRAME = 7;
	public static final int THUNDER_FRAME_HORIZONTAL = 10;
	public static final int THUNDER_FRAME_VERTICAL = 14;
	public static final int BLACK_HOLE_FRAME = 7;

	private final int BOMB_SKILL = 1;
	private final int CYCLONE_SKILL = 2;
	private final int SMALL_BOMB_SKILL = 3;
	private final int BLACK_HOLE_SKILL = 4;
	private final int THUNDER_SKILL_HORIZONTAL = 5;
	private final int THUNDER_SKILL_VERTICAL = 6;

	private Vector<Tablet> vectorTablet = new Vector<Tablet>();
	public Vector<RecordText> vectorScoreTexts = new Vector<RecordText>();
	// public Vector<PointStore> vectorPointStore = new Vector<PointStore>();

	// public Vector<EffectEntity> vectorEffectEntity = new
	// Vector<EffectEntity>();

	private int[][] matrix;
	private int[][] color;
	public static final int ROWS = 14;
	public static final int COLS = 10;
	BaseGameActivity mBaseGame;
	List<ITextureRegion> listControllerTextureRegion;

	TabletView tabletView;

	private Tablet currentTablet;
	private boolean requestCheckFallingTablet = false;
	private long timeCheckFallingTablet;
	private long timeCheckCreateFruit;
	private Scene scene;

	private BlendFunctionParticleInitializer<Sprite> bledFuntion = new BlendFunctionParticleInitializer<Sprite>(
			GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
	private AccelerationParticleInitializer<Sprite> acceleration = new AccelerationParticleInitializer<Sprite>(
			-5, 15);

	private RotationParticleInitializer<Sprite> rotation = new RotationParticleInitializer<Sprite>(
			0.0f, 360.0f);

	private ColorParticleInitializer<Sprite> colorParticle1 = new ColorParticleInitializer<Sprite>(
			0.0f, 0.0f, 1.0f);

	private AlphaParticleModifier<Sprite> alphaParticleModifier = new AlphaParticleModifier<Sprite>(
			0f, 1f, 1.0f, 0.0f);

	private ColorParticleModifier<Sprite> colorParticle2 = new ColorParticleModifier<Sprite>(
			0.0F, 1F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, 1F);
	private ScaleParticleModifier<Sprite> scaleParticleModifier = new ScaleParticleModifier<Sprite>(
			0F, 1.0F, 1f, 5);

	private int countSpark = 0;

	private BitmapFont recordFont;
	private Font alertLevelFont;
	private boolean isDoubleScore;

	public TabletController(BaseGameActivity mBaseGame,
			List<ITextureRegion> listTextureRegions) {
		this.listControllerTextureRegion = listTextureRegions;
		this.mBaseGame = mBaseGame;

		recordFont = new BitmapFont(mBaseGame.getTextureManager(),
				mBaseGame.getAssets(), "font/scorefont.fnt");
		recordFont.load();

		BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(
				mBaseGame.getTextureManager(), 512, 512,
				TextureOptions.BILINEAR);

		fontTexture = new BitmapTextureAtlas(mBaseGame.getTextureManager(),
				512, 512, TextureOptions.BILINEAR);
		alertLevelFont = FontFactory.createFromAsset(
				mBaseGame.getFontManager(), fontTexture, mBaseGame.getAssets(),
				"font/Huongque.ttf", 60, true, android.graphics.Color.RED);
		alertLevelFont.load();

	}

	public void onLoadScene(Scene scene, TabletView view) {
		this.tabletView = view;
		this.scene = scene;
		initLevel();

	}

	public void initLevel() {

		for (int i = 0; i < vectorTablet.size(); i++) {
			tabletView.detachTablet(vectorTablet.get(i));
		}
		// for (int i = 0; i < vectorVirus.size(); i++) {
		// vectorVirus.get(i).detachScene(scene);
		// }
		vectorTablet.clear();

		if (ConfigManager.GAME_FIELD != null) {
			matrix = ConfigManager.GAME_FIELD;
			color = ConfigManager.GAME_COLOR;
		} else {
			matrix = new int[ROWS][COLS];
			color = new int[ROWS][COLS];

			for (int i = 0; i < ROWS; i++) {
				for (int j = 0; j < COLS; j++) {
					if (i == 0) {
						matrix[i][j] = EMPTY;

					} else {
						matrix[i][j] = WALL;
					}
					color[i][j] = EMPTY;
				}
			}
			for (int i = 1; i < ROWS - 1; i++) {
				for (int j = 1; j < COLS - 1; j++) {
					matrix[i][j] = EMPTY;
				}
			}

		}
		Tablet nextTablet = new Tablet(mBaseGame, listControllerTextureRegion);
		nextTablet.setTabletLocation(scene, 408, 8);
		vectorTablet.add(nextTablet);
		tabletView.onloadScene(scene, matrix);
		tabletView.updateTabletField();
		nextTablet();
		timeCheckCreateFruit = System.currentTimeMillis();
	}

	private long getTimeFallingDown() {
		return Math.max(1000, 15250 - ConfigManager.GAME_STATUS[0] * 1250);
		// return 1000;
	}

	public void run() {

		if (ConfigManager.GAME_STATUS != null) {
			if (!isMovingDown
					&& !requestCheckFallingTablet
					&& System.currentTimeMillis() - timeCheckCreateFruit >= getTimeFallingDown()) {
				if (!requestCheckFallingTablet && countSpark == 0) {
					timeCheckCreateFruit = System.currentTimeMillis();
					createMoreFruit();
				}
			}
		}
		for (int i = 0; i < vectorScoreTexts.size(); i++) {
			RecordText scoreText = vectorScoreTexts.get(i);
			scoreText.animate();
			if (scoreText.isClearEntity) {
				scoreText.detachScene(scene);
				vectorScoreTexts.removeElementAt(i);
				i--;
			}
		}
		if (isMovingDown) {
			isMovingDown();
		} else {
			fallTablet();
		}

	}

	private void nextTablet() {
		if (vectorTablet.size() == 1) {
			Tablet nextTablet = new Tablet(mBaseGame,
					listControllerTextureRegion);
			nextTablet.setTabletLocation(scene, 408, 8);
			vectorTablet.add(nextTablet);
			currentTablet = (Tablet) vectorTablet.get(0);
			tabletView.attachTablet(currentTablet);
		}
	}

	boolean hasFallingTablet = false;

	private void fallTablet() {
		if (!requestCheckFallingTablet || countSpark > 0) {
			return;
		}
		// if (System.currentTimeMillis() - timeCheckFallingTablet < 120) {
		// return;
		// }
		// timeCheckFallingTablet = System.currentTimeMillis();
		if (!hasFallingTablet) {
			for (int i = ROWS - 2; i >= 1; i--) {
				for (int j = 1; j <= COLS - 2; j++) {
					if (matrix[i + 1][j] == EMPTY && matrix[i][j] != EMPTY) {
						matrix[i + 1][j] = matrix[i][j];
						color[i + 1][j] = color[i][j];
						this.draw(i, j);
						matrix[i][j] = EMPTY;
						color[i][j] = EMPTY;
						hasFallingTablet = true;
					}
				}
			}
			if (!hasFallingTablet) {
				updateTabletField();
				requestCheckFallingTablet = false;
				updateGame();
				timeCheckFallingTablet = System.currentTimeMillis();
				if (!requestCheckFallingTablet && countSpark == 0) {

					nextTablet();
				}
			}
		}
	}

	long countFallTablet = 0;

	private void draw(final int row, final int column) {
		tabletView.getTabletSpriteView()[row][column].setVisible(true);
		tabletView.getTabletSpriteView()[row][column]
				.setCurrentTileIndex(matrix[row][column]);
		tabletView.getTabletSpriteView()[row][column]
				.registerEntityModifier(new MoveYModifier(0.2f, tabletView
						.getTabletSpriteView()[row][column].getY(), tabletView
						.getTabletSpriteView()[row][column].getY()
						+ TabletView.cellHeight, new IEntityModifierListener() {
					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier,
							IEntity pItem) {
						// TODO Auto-generated method stub
						countFallTablet++;
					}

					@Override
					public void onModifierFinished(
							IModifier<IEntity> pModifier, IEntity pItem) {
						countFallTablet--;
						if (countFallTablet == 0) {
							hasFallingTablet = false;
							tabletView.resetPosition();
							updateTabletField();
						}
					}
				}));
	}

	private void finishTurn(Tablet tablet) {

		for (int i = 0; i < tablet.getTabletLocation().length; i++) {

			int j = tablet.getTabletLocation()[i][0];
			int k = tablet.getTabletLocation()[i][1];
			this.matrix[k][j] = tablet.getTabletColor()[i];
			this.color[k][j] = tablet.getTabletColor()[i];
			this.tabletView.drawTablet(k, j, this.matrix[k][j]);
		}

		if (currentTablet != null) {
			tabletView.detachTablet(currentTablet);
			if (!vectorTablet.isEmpty())
				vectorTablet.remove(0);
		}
	}

	private boolean isUpAble(Tablet lastTablet) {
		if (requestCheckFallingTablet) {
			return false;
		}
		boolean isupAble = true;
		return isupAble;
	}

	private boolean isDownAble(Tablet lastTablet) {
		if (requestCheckFallingTablet) {
			return false;
		}
		boolean isDownAble = false;
		int i = lastTablet.beginIndex;
		int j = lastTablet.getTabletLocation()[2 - i][0];
		int k = lastTablet.getTabletLocation()[2 - i][1];
		if (matrix[k + 1][j] == EMPTY) {
			isDownAble = true;
		}

		return isDownAble;
	}

	private boolean isLeftAble(Tablet lastTablet) {
		if (requestCheckFallingTablet) {
			return false;
		}
		boolean isLeftAble = true;
		for (int i = 0; i < lastTablet.getTabletLocation().length; i++) {
			int j = lastTablet.getTabletLocation()[i][0];
			int k = lastTablet.getTabletLocation()[i][1];
			if (matrix[k][j - 1] != EMPTY) {
				isLeftAble = false;
				break;
			}
		}

		return isLeftAble;
	}

	private boolean isRightAble(Tablet lastTablet) {
		if (requestCheckFallingTablet) {
			return false;
		}
		boolean isRightAble = true;
		for (int i = 0; i < lastTablet.getTabletLocation().length; i++) {
			int j = lastTablet.getTabletLocation()[i][0];
			int k = lastTablet.getTabletLocation()[i][1];
			if (matrix[k][j + 1] != EMPTY) {
				isRightAble = false;
				break;
			}
		}

		return isRightAble;
	}

	private void checkScore() {

		long totalScore = getScore();
		updateTabletField();
		if (totalScore > 0) {
			requestCheckFallingTablet = true;
			BackgroundView
					.upScore(totalScore, COLS / 2 - 1, ROWS / 2 + 1, 1f);
			if (BackgroundView.isUplevel()) {
				addLevelText();
			}
		} else {
			if (checkSpecificSkill()) {
				requestCheckFallingTablet = true;
			}
		}
	}

	private void updateGame() {
		checkScore();

	}

	private List<PointStore> getScore(int rowMarginIndex, int columnMarginIndex) {
		List<PointStore> listOfBallInLines = new ArrayList<TabletController.PointStore>();
		int spriteMarginIndex = color[rowMarginIndex][columnMarginIndex];
		List<PointStore> listOfCheckBall = new ArrayList<PointStore>();
		int countBallCheck = 0;
		int checkSkill = 0;
		/*
		 * 
		 */
		// check line by straight-line-right-left
		// check to the left

		for (int i = columnMarginIndex - 1; i >= 1; i--) {
			if (matrix[rowMarginIndex][i] >= BLACK_HOLE_FRAME) {
				break;
			}

			if (color[rowMarginIndex][i] == spriteMarginIndex) {
				countBallCheck++;
				listOfCheckBall.add(new PointStore(new int[] { rowMarginIndex,
						i }));
			} else {
				break;
			}
		}
		// check to the right
		for (int i = columnMarginIndex + 1; i < COLS - 1; i++) {
			if (matrix[rowMarginIndex][i] >= BLACK_HOLE_FRAME) {
				break;
			}
			if (color[rowMarginIndex][i] == spriteMarginIndex) {
				countBallCheck++;
				listOfCheckBall.add(new PointStore(new int[] { rowMarginIndex,
						i }));
			} else {
				break;
			}
		}

		if (countBallCheck >= CHECK_POINT) {
			listOfBallInLines.addAll(listOfCheckBall);
			if (countBallCheck == 4) {
				checkSkill = 2;
			} else if (countBallCheck == 3) {
				checkSkill = 1;
			}
		}
		listOfCheckBall.clear();
		countBallCheck = 0;
		/*
		 * 
		 */
		// check line by straight-line-up-down
		// check to the up

		for (int i = rowMarginIndex - 1; i >= 1; i--) {
			if (matrix[i][columnMarginIndex] >= BLACK_HOLE_FRAME) {
				break;
			}
			if (color[i][columnMarginIndex] == spriteMarginIndex) {
				countBallCheck++;
				listOfCheckBall.add(new PointStore(new int[] { i,
						columnMarginIndex }));
			} else {
				break;
			}
		}
		// check to the down
		for (int i = rowMarginIndex + 1; i < ROWS - 1; i++) {
			if (matrix[i][columnMarginIndex] >= BLACK_HOLE_FRAME) {
				break;
			}
			if (color[i][columnMarginIndex] == spriteMarginIndex) {
				countBallCheck++;
				listOfCheckBall.add(new PointStore(new int[] { i,
						columnMarginIndex }));
			} else {
				break;
			}
		}

		if (countBallCheck >= CHECK_POINT) {
			listOfBallInLines.addAll(listOfCheckBall);
			if (countBallCheck == 4) {
				checkSkill = 2;
			} else if (countBallCheck == 3) {
				checkSkill = 1;
			}
		}
		listOfCheckBall.clear();
		countBallCheck = 0;

		/*
		 * 
		 */
		// check line by cross-line-right-to-left-down

		// Log.e("check", "left-to-right-down");

		int i = rowMarginIndex - 1;
		int j = columnMarginIndex - 1;
		while (i >= 1 & j >= 1) {
			if (matrix[i][j] >= BLACK_HOLE_FRAME) {
				break;
			}
			if (color[i][j] == spriteMarginIndex) {
				countBallCheck++;
				listOfCheckBall.add(new PointStore(new int[] { i, j }));
				i--;
				j--;
			} else {
				break;
			}

		}

		// check to the down
		i = rowMarginIndex + 1;
		j = columnMarginIndex + 1;
		while (i < ROWS - 1 && j < COLS - 1) {
			if (matrix[i][j] >= BLACK_HOLE_FRAME) {
				break;
			}
			if (color[i][j] == spriteMarginIndex) {
				countBallCheck++;
				listOfCheckBall.add(new PointStore(new int[] { i, j }));
				i++;
				j++;
			} else {
				break;
			}

		}
		// Log.e("end check", "left-to-right-down: "+countBallCheck);

		if (countBallCheck >= CHECK_POINT) {
			listOfBallInLines.addAll(listOfCheckBall);
			if (countBallCheck == 4) {
				checkSkill = 2;
			} else if (countBallCheck == 3) {
				checkSkill = 1;
			}
		}
		listOfCheckBall.clear();
		countBallCheck = 0;

		/*
		 * 
		 */
		// check line by cross-line-left-to-right-up
		// Log.e("check", "left-to-right-up ");
		i = rowMarginIndex - 1;
		j = columnMarginIndex + 1;
		while (i >= 1 && j < COLS - 1) {
			if (matrix[i][j] >= BLACK_HOLE_FRAME) {
				break;
			}
			if (color[i][j] == spriteMarginIndex) {
				countBallCheck++;
				listOfCheckBall.add(new PointStore(new int[] { i, j }));
				i--;
				j++;
			} else {
				break;
			}

		}

		// check to the left-to-right-down
		i = rowMarginIndex + 1;
		j = columnMarginIndex - 1;
		while (i < ROWS - 1 && j >= 1) {
			if (matrix[i][j] >= BLACK_HOLE_FRAME) {
				break;
			}
			if (color[i][j] == spriteMarginIndex) {
				countBallCheck++;
				listOfCheckBall.add(new PointStore(new int[] { i, j }));
				i++;
				j--;
			} else {
				break;
			}

		}
		// Log.e("end check", "left-to-right-up: "+columnMarginIndex);

		if (countBallCheck >= CHECK_POINT) {
			// Log.e("get score", "left-to-right-up: "+countBallCheck);
			listOfBallInLines.addAll(listOfCheckBall);
			if (countBallCheck == 4) {
				checkSkill = 2;
			} else if (countBallCheck == 3) {
				checkSkill = 1;
			}
		}
		listOfCheckBall.clear();
		countBallCheck = 0;

		if (!listOfBallInLines.isEmpty()) {
			listOfBallInLines.add(new PointStore(new int[] { rowMarginIndex,
					columnMarginIndex }));
		}

		if (checkSkill > 0) {
			if (checkSkill == 2) {
				createSkill(CYCLONE_SKILL, columnMarginIndex, rowMarginIndex,
						listOfBallInLines);

			} else if (checkSkill == 1) {
				createSkill(BOMB_SKILL, columnMarginIndex, rowMarginIndex, null);

			}
		}

		return listOfBallInLines;
	}

	private boolean isExistedInStore(List<PointStore> pointStores,
			int indexRow, int indexColumn) {
		boolean isExisted = false;
		if (!pointStores.isEmpty()) {
			for (PointStore pointStore : pointStores) {
				if (pointStore.pointStore[0] == indexRow
						&& pointStore.pointStore[1] == indexColumn) {
					isExisted = true;
					break;
				}
			}
		}
		return isExisted;
	}

	private long getScore() {
		long totalScore = 0;
		Vector<PointStore> pointStores = new Vector<TabletController.PointStore>();
		for (int i = ROWS - 2; i > 1; i--) {
			for (int j = 1; j < COLS - 1; j++) {
				if (!isExistedInStore(pointStores, i, j)) {
					List<PointStore> points = getScore(i, j);
					if (!points.isEmpty()) {
						pointStores.addAll(points);
					}
				}
			}
		}
		if (!pointStores.isEmpty()) {
			totalScore = pointStores.size();
			for (PointStore pointStore : pointStores) {
				int rowIndex = pointStore.pointStore[0];
				int columnIndex = pointStore.pointStore[1];
				updateAnimatedField(scene, rowIndex, columnIndex);
				matrix[rowIndex][columnIndex] = EMPTY;
				color[rowIndex][columnIndex] = EMPTY;

			}
		}
		return totalScore;
	}

	private void updateTabletField() {
		if (countSpark == 0)
			tabletView.updateTabletField();
	}

	public synchronized void moveDown() {
		if (isDownAble(currentTablet)) {
			currentTablet.moveDown();
		} else {
			finishTurn(currentTablet);
			updateGame();
			timeCheckFallingTablet = System.currentTimeMillis();
			if (!requestCheckFallingTablet) {
				nextTablet();
			}

		}
	}

	private boolean isMovingDown = false;

	public synchronized void drop() {
		if (requestCheckFallingTablet) {
			return;
		}
		isMovingDown = true;
		// vectorVirus.clear();
	}

	public void isMovingDown() {
		if (isDownAble(currentTablet)) {
			currentTablet.moveDown();
		} else {
			finishTurn(currentTablet);
			updateGame();
			timeCheckFallingTablet = System.currentTimeMillis();
			if (!requestCheckFallingTablet) {
				nextTablet();
			}
			isMovingDown = false;
		}
	}

	public void moveLeft() {

		if (isLeftAble(currentTablet)) {
			currentTablet.moveLeft();
		}
	}

	public void moveRight() {
		if (isRightAble(currentTablet)) {
			currentTablet.moveRight();
		}
	}

	public void rotate() {

		if (isUpAble(currentTablet)) {
			currentTablet.rotate();
		}
	}

	private void updateAnimatedField(final Scene scene, final int indexRow,
			final int indexColumn) {
		if (matrix[indexRow][indexColumn] < EMPTY) {
			countSpark++;
			tabletView.getTabletSpriteView()[indexRow][indexColumn]
					.setCurrentTileIndex(color[indexRow][indexColumn]);
			tabletView.getTabletSpriteView()[indexRow][indexColumn]
					.setVisible(true);
			tabletView.getTabletSpriteView()[indexRow][indexColumn]
					.registerEntityModifier(new SequenceEntityModifier(
							new IEntityModifierListener() {
								@Override
								public void onModifierStarted(
										IModifier<IEntity> pModifier,
										IEntity pItem) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onModifierFinished(
										IModifier<IEntity> pModifier,
										IEntity pItem) {
									countSpark--;
									matrix[indexRow][indexColumn] = EMPTY;
									color[indexRow][indexColumn] = EMPTY;
									tabletView.returnToOriginalState(indexRow,
											indexColumn);
								}
							}, new ParallelEntityModifier(new ScaleModifier(
									0.5f, 1f, 0.2f))));
		}

	}

	private void normalSpark(final Scene scene, float dx, float dy, int index,
			float fromScale, float toScale, float time) {
		countSpark++;
		float circleRadius = 1f;
		final SpriteParticleSystem particleSystem = new SpriteParticleSystem(
				new CircleOutlineParticleEmitter(dx, dy, circleRadius), 10.0F,
				10.0F, 20, listControllerTextureRegion.get(index),
				mBaseGame.getVertexBufferObjectManager());
		particleSystem.addParticleInitializer(bledFuntion);
		particleSystem.addParticleInitializer(acceleration);
		particleSystem.addParticleInitializer(rotation);
		particleSystem.addParticleModifier(new ScaleParticleModifier<Sprite>(
				0F, 1.5F, fromScale, toScale));
		particleSystem.addParticleModifier(alphaParticleModifier);
		particleSystem.addParticleModifier(colorParticle2);

		scene.attachChild(particleSystem);
		ITimerCallback local1 = new ITimerCallback() {
			public void onTimePassed(TimerHandler paramAnonymousTimerHandler) {
				RunnableHandler localRunnableHandler = new RunnableHandler();
				scene.registerUpdateHandler(localRunnableHandler);
				localRunnableHandler.postRunnable(new Runnable() {
					public void run() {
						particleSystem.dispose();
						scene.detachChild(particleSystem);
						countSpark--;
					}
				});
			}
		};
		scene.registerUpdateHandler(new TimerHandler(time, local1));

	}

	private void superSparkEffect(final Scene scene, float dx, float dy,
			int index, float fromScale, float toScale, float time,
			final List<PointStore> vectorPointStore) {
		countSpark++;
		float circleRadius = 10f;
		if (index != ConfigManager.BOMB_INDEX
				&& index != ConfigManager.CYCLONE_INDEX) {
			circleRadius = 1f;
		}
		final SpriteParticleSystem particleSystem = new SpriteParticleSystem(
				new CircleOutlineParticleEmitter(dx, dy, circleRadius), 10.0F,
				10.0F, 20, listControllerTextureRegion.get(index),
				mBaseGame.getVertexBufferObjectManager());
		particleSystem.addParticleInitializer(bledFuntion);
		particleSystem.addParticleInitializer(acceleration);
		particleSystem.addParticleInitializer(rotation);
		particleSystem.addParticleModifier(new ScaleParticleModifier<Sprite>(
				0F, 1.5F, fromScale, toScale));
		particleSystem.addParticleModifier(alphaParticleModifier);
		particleSystem.addParticleModifier(colorParticle2);

		// particleSystem.addParticleModifier(new
		// AlphaParticleModifier<Sprite>(0,
		// 2.0f, 0.0f, 1.0f));
		scene.attachChild(particleSystem);
		ITimerCallback local1 = new ITimerCallback() {
			public void onTimePassed(TimerHandler paramAnonymousTimerHandler) {
				RunnableHandler localRunnableHandler = new RunnableHandler();
				scene.registerUpdateHandler(localRunnableHandler);
				localRunnableHandler.postRunnable(new Runnable() {
					public void run() {
						scene.detachChild(particleSystem);
						countSpark--;
						if (countSpark == 0) {
							PointStore pointStore = vectorPointStore.get(0);
							int effectRowIndex = pointStore.pointStore[0];
							int effectColumnIndex = pointStore.pointStore[1];
							long value = pointStore.pointStore[2];
							if (value > 0) {
								BackgroundView.upScore(value,
										effectColumnIndex, effectRowIndex, 1f);
								if (BackgroundView.isUplevel()) {
									addLevelText();
								}
							}
							vectorPointStore.clear();
						}
					}
				});
			}
		};
		scene.registerUpdateHandler(new TimerHandler(time, local1));

	}

	private void createMoreFruit() {
		for (int i = 0; i < ROWS - 2; i++) {
			for (int j = 1; j < COLS - 1; j++) {
				matrix[i][j] = matrix[i + 1][j];
				color[i][j] = color[i + 1][j];
			}
		}
		Random random = new Random();
		for (int j = 1; j < COLS - 1; j++) {
			if (ConfigManager.GAME_STATUS[0] < 2) {
				color[ROWS - 2][j] = random.nextInt(5);
			} else if (ConfigManager.GAME_STATUS[0] < 3) {
				color[ROWS - 2][j] = random.nextInt(6);
			} else {
				color[ROWS - 2][j] = random.nextInt(7);
			}
			matrix[ROWS - 2][j] = color[ROWS - 2][j];
		}
		updateTabletField();
		updateGame();
	}

	private void superEffect(final Scene scene, float dx, float dy,
			final int index, int scale, final List<PointStore> vectorPointStore) {
		countSpark++;
		final SpriteParticleSystem particleSystem = new SpriteParticleSystem(
				new CircleOutlineParticleEmitter(dx, dy, 10f), 10.0F, 10.0F,
				20, listControllerTextureRegion.get(index),
				mBaseGame.getVertexBufferObjectManager());
		particleSystem.addParticleInitializer(bledFuntion);
		particleSystem.addParticleInitializer(acceleration);
		particleSystem.addParticleInitializer(rotation);
		particleSystem.addParticleModifier(new ScaleParticleModifier<Sprite>(
				0F, 1.5F, 1f, scale));
		particleSystem.addParticleModifier(alphaParticleModifier);
		particleSystem.addParticleModifier(colorParticle2);
		scene.attachChild(particleSystem);
		ITimerCallback local1 = new ITimerCallback() {
			public void onTimePassed(TimerHandler paramAnonymousTimerHandler) {
				RunnableHandler localRunnableHandler = new RunnableHandler();
				scene.registerUpdateHandler(localRunnableHandler);
				localRunnableHandler.postRunnable(new Runnable() {
					public void run() {
						countSpark--;
						particleSystem.dispose();
						scene.detachChild(particleSystem);
						if (index == ConfigManager.CYCLONE_INDEX) {
							for (int i = 0; i < vectorPointStore.size(); i++) {
								PointStore pointStore = vectorPointStore.get(i);
								int effectRowIndex = pointStore.pointStore[0];
								int effectColumnIndex = pointStore.pointStore[1];
								createSkill(SMALL_BOMB_SKILL,
										effectColumnIndex, effectRowIndex, null);
							}
						} else if (vectorPointStore.size() > 1) {
							int value = 0;
							int effectRowIndex = 0;
							int effectColumnIndex = 0;
							SoundManager.playSound(2f,
									SoundManager.sparkSoundID);
							for (int i = 1; i < vectorPointStore.size(); i++) {
								PointStore pointStore = vectorPointStore.get(i);
								effectRowIndex = pointStore.pointStore[0];
								effectColumnIndex = pointStore.pointStore[1];
								float sparkScale = 0f;
								if (index == ConfigManager.BOMB_INDEX) {
									sparkScale = 1f;
									superSparkEffect(scene, TabletView.marginX
											+ effectColumnIndex
											* TabletView.cellWidth,
											TabletView.marginY + effectRowIndex
													* TabletView.cellHeight,
											index, sparkScale, 0.05f, 1.5f,
											vectorPointStore);
								} else if (index == ConfigManager.CYCLONE_INDEX) {
									sparkScale = 1f;
									superSparkEffect(scene, TabletView.marginX
											+ effectColumnIndex
											* TabletView.cellWidth,
											TabletView.marginY + effectRowIndex
													* TabletView.cellHeight,
											index, sparkScale, 0.05f, 1.5f,
											vectorPointStore);
								} else {
									superSparkEffect(scene, TabletView.marginX
											+ effectColumnIndex
											* TabletView.cellWidth + 10,
											TabletView.marginY + effectRowIndex
													* TabletView.cellHeight,
											index, 1f, 0.05f, 1.5f,
											vectorPointStore);
								}
								updateAnimatedField(scene, effectRowIndex,
										effectColumnIndex);
								if (matrix[effectRowIndex][effectColumnIndex] != EMPTY) {
									value++;
								}
								matrix[effectRowIndex][effectColumnIndex] = EMPTY;
								color[effectRowIndex][effectColumnIndex] = EMPTY;
							}
							PointStore pointStore = vectorPointStore.get(0);
							effectRowIndex = pointStore.pointStore[0];
							effectColumnIndex = pointStore.pointStore[1];
							vectorPointStore.clear();
							vectorPointStore
									.add(new PointStore(new int[] {
											effectRowIndex, effectColumnIndex,
											value }));
						}
					}
				});
			}
		};
		scene.registerUpdateHandler(new TimerHandler(1.5F, local1));

	}

	public void addScoreText(String value, int colorIndex, float dx, float dy,
			float scale) {
		RecordText scoreText = new RecordText(mBaseGame, recordFont,
				TabletView.marginX + dx * TabletView.cellWidth,
				TabletView.marginY + dy * TabletView.cellHeight, value);
		scoreText.attachScene(scene);
		scoreText.getScoreText().setScale(scale);
		vectorScoreTexts.add(scoreText);
	}

	private void createSkill(int indexEffect, int indexColumn, int indexRow,
			List<PointStore> exceptionList) {
		final List<PointStore> vectorPointStore = new ArrayList<TabletController.PointStore>();
		if (indexEffect == BOMB_SKILL) {
			superEffect(scene, TabletView.marginX + indexColumn
					* TabletView.cellWidth, TabletView.marginY + indexRow
					* TabletView.cellHeight, ConfigManager.BOMB_INDEX, 5,
					vectorPointStore);
			SoundManager.playSound(2f, SoundManager.bombSoundID);
			vectorPointStore.add(new PointStore(new int[] { indexRow,
					indexColumn }));
			for (int effectRowIndex = Math.max(1, indexRow - 1); effectRowIndex <= Math
					.min(indexRow + 1, ROWS - 2); effectRowIndex++) {
				for (int effectColumnIndex = Math.max(1, indexColumn - 1); effectColumnIndex <= Math
						.min(indexColumn + 1, COLS - 2); effectColumnIndex++) {
					if (matrix[effectRowIndex][effectColumnIndex] < 7
							|| matrix[effectRowIndex][effectColumnIndex] == EMPTY) {
						vectorPointStore.add(new PointStore(new int[] {
								effectRowIndex, effectColumnIndex }));
					}

				}
			}
			// chainSkill(vectorPointStore);

		} else if (indexEffect == CYCLONE_SKILL) {
			SoundManager.playSound(2f, SoundManager.holeSoundID);
			for (int effectRowIndex = 1; effectRowIndex < ROWS - 1; effectRowIndex++) {
				for (int effectColumnIndex = 1; effectColumnIndex < COLS - 1; effectColumnIndex++) {
					if (color[effectRowIndex][effectColumnIndex] == color[indexRow][indexColumn]) {
						boolean isAdd = true;
						for (PointStore pointStore : exceptionList) {
							int exceptRow = pointStore.pointStore[0];
							int exceptColumn = pointStore.pointStore[1];
							if (effectRowIndex == exceptRow
									&& effectColumnIndex == exceptColumn) {
								isAdd = false;
								break;
							}

						}
						if (isAdd) {
							vectorPointStore.add(new PointStore(new int[] {
									effectRowIndex, effectColumnIndex }));
						}

					}

				}

			}
			superEffect(scene, TabletView.marginX + COLS * TabletView.cellWidth
					/ 2 - 25, TabletView.marginY + ROWS * TabletView.cellHeight
					/ 2 - 50, ConfigManager.CYCLONE_INDEX, 20, vectorPointStore);

		} else if (indexEffect == SMALL_BOMB_SKILL) {
			updateAnimatedField(scene, indexRow, indexColumn);
			matrix[indexRow][indexColumn] = EMPTY;
			color[indexRow][indexColumn] = EMPTY;
			long countScore = 0;
			for (int effectRowIndex = Math.max(1, indexRow - 1); effectRowIndex <= Math
					.min(indexRow + 1, ROWS - 2); effectRowIndex++) {
				if (effectRowIndex != indexRow) {
					if (matrix[effectRowIndex][indexColumn] < 7) {
						vectorPointStore.add(new PointStore(new int[] {
								effectRowIndex, indexColumn }));
						countScore++;
					}

				}

			}

			for (int effectColumnIndex = Math.max(1, indexColumn - 1); effectColumnIndex <= Math
					.min(indexColumn + 1, COLS - 2); effectColumnIndex++) {
				if (effectColumnIndex != indexColumn) {
					if (matrix[indexRow][effectColumnIndex] < 7) {
						countScore++;
						vectorPointStore.add(new PointStore(new int[] {
								indexRow, effectColumnIndex }));
					}

				}
			}

			for (PointStore pointStore : vectorPointStore) {
				int effectRowIndex = pointStore.pointStore[0];
				int effectColumnIndex = pointStore.pointStore[1];
				updateAnimatedField(scene, effectRowIndex, effectColumnIndex);
				normalSpark(scene, TabletView.marginX + effectColumnIndex
						* TabletView.cellWidth + 10, TabletView.marginY
						+ effectRowIndex * TabletView.cellHeight,
						ConfigManager.SPARK_INDEX, 5, 10, 0.75f);

			}
			// chainSkill(vectorPointStore);
			if (countScore > 0) {
				BackgroundView.upScore(countScore, indexColumn, indexRow, 1f);
				if (BackgroundView.isUplevel()) {
					addLevelText();
				}

			}
		}

		else if (indexEffect == BLACK_HOLE_SKILL) {
			updateAnimatedField(scene, indexRow, indexColumn);
			matrix[indexRow][indexColumn] = EMPTY;
			color[indexRow][indexColumn] = EMPTY;
			long totalScore = 0;
			if (indexRow < ROWS - 2) {
				int indexColor = color[indexRow + 1][indexColumn];
				for (int i = 1; i < ROWS - 1; i++) {
					for (int j = 1; j < COLS - 1; j++) {
						if (color[i][j] == indexColor) {
							createSparkPath(indexRow, indexColumn, i, j);
							// totalScore++;
							// vectorPointStore.add(new PointStore(new
							// int[]{i,j}));

						}
					}
				}
			}
			// if (totalScore > 0) {
			// BackgroundView.upScore(totalScore,indexColumn, indexRow,1f);
			// if (BackgroundView.isUplevel()) {
			// addLevelText();
			// }
			//
			// }

		} else if (indexEffect == THUNDER_SKILL_HORIZONTAL) {
			updateAnimatedField(scene, indexRow, indexColumn);
			matrix[indexRow][indexColumn] = EMPTY;
			color[indexRow][indexColumn] = EMPTY;
			long totalScore = 0;

			for (int j = 1; j < COLS - 1; j++) {
				if (matrix[indexRow][j] < 7) {
					updateAnimatedField(scene, indexRow, j);
					totalScore++;
					normalSpark(scene, TabletView.marginX + j
							* TabletView.cellWidth, TabletView.marginY
							+ indexRow * TabletView.cellHeight,
							ConfigManager.THUNDER_INDEX, 1, 1, 0.75f);
					vectorPointStore.add(new PointStore(
							new int[] { indexRow, j }));

				}

			}
			// chainSkill(vectorPointStore);

			if (totalScore > 0) {
				BackgroundView.upScore(totalScore, indexColumn, indexRow, 1f);
				if (BackgroundView.isUplevel()) {
					addLevelText();
				}
			}
		} else if (indexEffect == THUNDER_SKILL_VERTICAL) {
			updateAnimatedField(scene, indexRow, indexColumn);
			matrix[indexRow][indexColumn] = EMPTY;
			color[indexRow][indexColumn] = EMPTY;
			long totalScore = 0;

			for (int i = 1; i < ROWS - 1; i++) {
				if (matrix[i][indexColumn] < 7) {
					updateAnimatedField(scene, i, indexColumn);
					totalScore++;
					normalSpark(scene, TabletView.marginX + indexColumn
							* TabletView.cellWidth, TabletView.marginY + i
							* TabletView.cellHeight,
							ConfigManager.THUNDER_INDEX, 1, 1, 0.75f);
					vectorPointStore.add(new PointStore(new int[] { i,
							indexColumn }));
				}

			}
			// chainSkill(vectorPointStore);
			if (totalScore > 0) {
				BackgroundView.upScore(totalScore, indexColumn, indexRow, 1f);
				if (BackgroundView.isUplevel()) {
					addLevelText();
				}
			}
		}
	}

	// private void chainSkill(List<PointStore> influentPointStores){
	// for (int k = 0; k < influentPointStores.size(); k++) {
	// PointStore pointStore = influentPointStores.get(k);
	// int indexRow = pointStore.pointStore[0];
	// int indexColumn = pointStore.pointStore[1];
	// if (matrix[indexRow][indexColumn] == SMALL_BOMB_FRAME) {
	// SoundManager.playSound(2f, SoundManager.bombSoundID);
	// createSkill(SMALL_BOMB_SKILL, indexColumn, indexRow, null);
	// }
	// else if (matrix[indexRow][indexColumn] == BLACK_HOLE_FRAME) {
	// SoundManager.playSound(2f, SoundManager.holeSoundID);
	// createSkill(BLACK_HOLE_SKILL, indexColumn, indexRow, null);
	// }
	// else if (matrix[indexRow][indexColumn] == THUNDER_FRAME_HORIZONTAL) {
	// SoundManager.playSound(2f, SoundManager.lightningID);
	// createSkill(THUNDER_SKILL_HORIZONTAL, indexColumn, indexRow, null);
	// break;
	// }
	// else if (matrix[indexRow][indexColumn] == THUNDER_FRAME_VERTICAL) {
	// SoundManager.playSound(2f, SoundManager.lightningID);
	// createSkill(THUNDER_SKILL_VERTICAL, indexColumn, indexRow, null);
	// break;
	// }
	// }
	// }

	private boolean checkSpecificSkill() {
		boolean isHasSkill = false;
		for (int i = 1; i < ROWS - 1; i++) {
			for (int j = 1; j < COLS - 1; j++) {
				if (matrix[i][j] == BLACK_HOLE_FRAME) {
					SoundManager.playSound(2f, SoundManager.holeSoundID);
					createSkill(BLACK_HOLE_SKILL, j, i, null);
					isHasSkill = true;
					break;
				}
				if (matrix[i][j] == THUNDER_FRAME_HORIZONTAL) {
					SoundManager.playSound(2f, SoundManager.lightningID);
					createSkill(THUNDER_SKILL_HORIZONTAL, j, i, null);
					isHasSkill = true;
					break;
				}
				if (matrix[i][j] == THUNDER_FRAME_VERTICAL) {
					SoundManager.playSound(2f, SoundManager.lightningID);
					createSkill(THUNDER_SKILL_VERTICAL, j, i, null);
					isHasSkill = true;
					break;
				}
			}
		}
		return isHasSkill;
	}

	private void addLevelText() {
		timeCheckCreateFruit = System.currentTimeMillis();
		countSpark++;
		final Sprite sprite = new Sprite(0, 0,
				listControllerTextureRegion.get(ConfigManager.LEVEL_INDEX),
				mBaseGame.getVertexBufferObjectManager());
		sprite.setPosition(ConfigManager.CAMERA_WIDTH / 2 - sprite.getWidth()
				/ 2, ConfigManager.CAMERA_HEIGHT / 2 - sprite.getHeight() / 2);
		sprite.registerEntityModifier(new SequenceEntityModifier(
				new IEntityModifierListener() {

					public void onModifierStarted(IModifier<IEntity> pModifier,
							IEntity pItem) {

					}

					public void onModifierFinished(
							IModifier<IEntity> pModifier, IEntity pItem) {
						sprite.dispose();
						countSpark--;
						scene.detachChild(sprite);
					}
				}, new DelayModifier(1.5f), new ParallelEntityModifier(
						new AlphaModifier(1f, 1f, 0.05f), new ScaleModifier(1f,
								1f, 3f))));
		scene.attachChild(sprite);

	}

	public boolean isLost() {
		boolean isLost = true;
		if (currentTablet != null && currentTablet.isMovingSide) {
			isLost = false;
		} else if (requestCheckFallingTablet || countSpark > 0
				|| !vectorScoreTexts.isEmpty()) {
			isLost = false;
		} else {
			int i = currentTablet.beginIndex;
			int j = currentTablet.getTabletLocation()[2 - i][0];
			int k = currentTablet.getTabletLocation()[2 - i][1];
			if (matrix[k][j] == EMPTY) {
				isLost = false;
			}
			if (!isLost) {
				for (int firstIndex = 1; firstIndex < COLS - 1; firstIndex++) {
					if (matrix[0][firstIndex] != EMPTY) {
						isLost = true;
						break;
					}
				}
			}
		}
		return isLost;
	}

	public boolean isStopAble() {
		boolean isStopAble = true;
		if (isMovingDown
				|| requestCheckFallingTablet
				|| System.currentTimeMillis() - timeCheckCreateFruit >= getTimeFallingDown()) {
			isStopAble = false;
		}
		return isStopAble;

	}

	private void createSparkPath(int sourceRow, int sourceColumn,
			final int desRow, final int desColumn) {
		final float sourceX = TabletView.marginX + sourceColumn
				* TabletView.cellWidth + TabletView.cellWidth / 2;
		final float sourceY = TabletView.marginY + sourceRow
				* TabletView.cellHeight + TabletView.cellHeight / 2;
		final float desX = TabletView.marginX + desColumn
				* TabletView.cellWidth;
		final float desY = TabletView.marginY + desRow * TabletView.cellHeight;
		final Sprite sprite = new Sprite(sourceX, sourceY,
				listControllerTextureRegion.get(ConfigManager.STAR_INDEX),
				mBaseGame.getVertexBufferObjectManager());

		ArrayList<PointF> pMovePaths = GetBezierCurve(sourceX, sourceY, desX,
				desY, (desX - sourceX) / 2, (desY - sourceY) / 2, 0.2f);

		final CardinalSplineMoveModifierConfig catmullRomMoveModifierConfig1 = new CardinalSplineMoveModifierConfig(
				pMovePaths.size(), MathUtils.random(-0.5f, 0.5f));

		for (int i = 0; i < pMovePaths.size(); i++) {
			catmullRomMoveModifierConfig1.setControlPoint(i,
					pMovePaths.get(i).x, pMovePaths.get(i).y);
		}

		sprite.registerEntityModifier(new SequenceEntityModifier(
				new IEntityModifierListener() {

					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier,
							IEntity pItem) {
						// TODO Auto-generated method stub
						countSpark++;
					}

					@Override
					public void onModifierFinished(
							IModifier<IEntity> pModifier, IEntity pItem) {
						// TODO Auto-generated method stub
						scene.detachChild(sprite);
						sprite.dispose();
						countSpark--;
						updateAnimatedField(scene, desRow, desColumn);
						normalSpark(scene, desX + 10, desY,
								ConfigManager.SPARK_INDEX, 5f, 10f, 1.0f);
						BackgroundView.upScore(1, desColumn, desRow, 1f);
						if (BackgroundView.isUplevel()) {
							addLevelText();
						}
					}
				},
				new CardinalSplineMoveModifier(1,
						catmullRomMoveModifierConfig1, EaseLinear.getInstance())));
		scene.attachChild(sprite);

	}

	private ArrayList<PointF> GetBezierCurve(float pX1, float pY1, float pX2,
			float pY2, float pBX, float pBY, float pSkip) {
		ArrayList<PointF> pOutPut = new ArrayList<PointF>();
		for (float t = 0.0f; t <= 1; t += pSkip) {
			float x = (float) ((1 - t) * (1 - t) * pX1 + 2 * (1 - t) * t * pBX + t
					* t * pX2);
			float y = (float) ((1 - t) * (1 - t) * pY1 + 2 * (1 - t) * t * pBY + t
					* t * pY2);
			pOutPut.add(new PointF(x, y));
		}
		return pOutPut;
	}

	public int[][] getMatrix() {
		return matrix;
	}

	public int[][] getColor() {
		return color;
	}

	public BaseGameActivity getmBaseGame() {
		return mBaseGame;
	}

	public void setmBaseGame(BaseGameActivity mBaseGame) {
		this.mBaseGame = mBaseGame;
	}

	public int getnROW() {
		return ROWS;
	}

	public int getnCOL() {
		return COLS;
	}

	private class PointStore {
		int[] pointStore;

		public PointStore(int[] pointStore) {
			this.pointStore = pointStore;
		}
	}

}
