package amaztricks.game.manager;

import org.andengine.ui.activity.BaseGameActivity;




import com.hanastudio.columns.activity.R;

import game.android.music.AndroidAudio;
import game.android.music.Music;

public class SoundManager {
	private static AndroidAudio androidAudio;
	public static int touchSoundId = 0;
	public static int tickSoundID;
	public static int pointSoundID;
	public static int loseSoundID;
	public static int holeSoundID;
	public static int sparkSoundID;
	public static int bombSoundID;
	public static int lightningID;

	//	public static int startSoundID;
//	public static int levelupSoundID;
	public int uplevlSoundID;

	
	private static Music levelMusic;

	
	public static void init(BaseGameActivity mBaseGameActivity) {
		androidAudio = new AndroidAudio(mBaseGameActivity);
	}
	
	public static void loadMenuSound(BaseGameActivity mBaseGame) {
		touchSoundId = androidAudio.getSoundPool().load(mBaseGame, R.raw.button,
				0);
		tickSoundID = androidAudio.getSoundPool()
				.load(mBaseGame, R.raw.tick, 0);
		pointSoundID = androidAudio.getSoundPool().load(mBaseGame, R.raw.point,
				0);

	}
	public static void loadGameSound(BaseGameActivity mBaseGame) {
		loseSoundID = androidAudio.getSoundPool()
				.load(mBaseGame, R.raw.lose, 0);
		holeSoundID = androidAudio.getSoundPool()
				.load(mBaseGame, R.raw.hole, 0);
		sparkSoundID = androidAudio.getSoundPool()
				.load(mBaseGame, R.raw.spark, 0);
		bombSoundID = androidAudio.getSoundPool()
				.load(mBaseGame, R.raw.bomb, 0);
		lightningID = androidAudio.getSoundPool()
				.load(mBaseGame, R.raw.lightning, 0);		
		levelMusic = SoundManager.loadMusic("uplevel.wav");
		levelMusic.setLooping(false);		
	}
	

	
	public static void playLevelUp(){
		if(ConfigManager.IS_SOUND){
			levelMusic.play();			
		}
	}
	
	public static Music loadMusic(String filename){
		return androidAudio.newMusic(filename);
	}

	public static void playSound(final float volume, final int soundID) {
		if (ConfigManager.IS_SOUND) {
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					androidAudio.getSoundPool().play(soundID, volume, volume,
							0, 0, 0);
				}
			});
			t.start();

		}

	}
	
	public static void release(){
		androidAudio.getSoundPool().release();
	}

}
