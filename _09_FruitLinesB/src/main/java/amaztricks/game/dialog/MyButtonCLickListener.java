package amaztricks.game.dialog;

import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;

import amaztricks.game.manager.SoundManager;

public class MyButtonCLickListener implements OnClickListener{

	@Override
	public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		// TODO Auto-generated method stub
		SoundManager.playSound(1f, SoundManager.touchSoundId);

	}

}
