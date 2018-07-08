package amaztricks.game.manager;

import org.android.datastore.LocalStore;

import com.hanastudio.columns.activity.BackgroundView;
import com.hanastudio.columns.activity.MainActivity;

import android.content.Context;

public class ConfigManager {

	public static int[] GAME_STATUS; // 0 = score, 1 = level
	public static int[][] TETRIS_FIELDS;
	public static final int CAMERA_WIDTH = 480;
	public static final int CAMERA_HEIGHT = 800;
	public static boolean IS_MUSIC = true;
	public static boolean IS_SOUND = true;
	public static boolean IS_VIETNAMESE = true;
	public static boolean IS_TUTORIAL = true;
	// public static boolean IS_TOUCH = true;
	public static int CONTROL = 0;

	public static final int COLUMNS_INDEX = 0;
	public static final int SPARK_INDEX = 1;
	public static final int BOMB_INDEX = 2;
	public static final int CYCLONE_INDEX = 3;
	public static final int LEVEL_INDEX = 4;
	public static final int STAR_INDEX = 5;
	public static final int THUNDER_INDEX = 6;


	// 0 = index, 1= skill state
	public static String SAVE_GAME_ADDRESS = "save_game";
	public static String FIELD_TETRIS_ADDRESS = "save_fields";
	public static String COLOR_TETRIS_ADDRESS = "save_colors";
	public static String SCORE_GAME_ADDRESS = "save_score";
	public static String FIELD_TETRIS_SPEED = "save_speed";

	public static int[][] GAME_FIELD;
	public static int[][] GAME_COLOR;

	public final static int INDEX_GAME_LEVEL = 0;
	public final static int INDEX_GAME_SCORE = 1;
	public final static int INDEX_GAME_RECORD = 2;
	public final static int INDEX_GAME_TIME = 3;

	public static int finalScore = 0;
	public static int finalLevel = 1;

	public static void resetConfig() {
		GAME_STATUS = new int[2];
		GAME_STATUS[INDEX_GAME_LEVEL] = 1;
		GAME_STATUS[INDEX_GAME_SCORE] = 0;
		GAME_FIELD = null;
		GAME_COLOR = null;
		finalScore = 0;
		finalLevel = 1;
		BackgroundView.reset();
		
	}

	public static boolean isNeededResetingGame(Context mBaseGame) {
		boolean isResetGame = false;
		GAME_STATUS = (int[]) LocalStore.getObject(MainActivity.PREF_FILE_NAME
				+ SAVE_GAME_ADDRESS, mBaseGame);
		if (GAME_STATUS == null) {
			resetConfig();
			isResetGame = true;
		} else {
			GAME_FIELD = (int[][]) LocalStore.getObject(
					MainActivity.PREF_FILE_NAME + FIELD_TETRIS_ADDRESS,
					mBaseGame);
			GAME_COLOR = (int[][]) LocalStore.getObject(
					MainActivity.PREF_FILE_NAME + COLOR_TETRIS_ADDRESS,
					mBaseGame);
			finalScore = GAME_STATUS[INDEX_GAME_SCORE];
			finalLevel = GAME_STATUS[INDEX_GAME_LEVEL];

		}
		return isResetGame;
		
		
	}

	public static void saveGame(Context mBaseGame) {
		LocalStore.putObject(MainActivity.PREF_FILE_NAME + SAVE_GAME_ADDRESS,
				GAME_STATUS, mBaseGame);
	}

}
