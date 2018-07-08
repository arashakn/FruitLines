package udc.fruitcolumns.logic;

import java.util.List;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

public class EffectEntity {

	private AnimatedSprite effectSprite;
	long timeChangeState;
	int currentState = 0;
	boolean isClearEntity = false;
	boolean isActiveEffect = false;
	public EffectEntity(BaseGameActivity mBaseGame,
			ITextureRegion iTextureRegion, float dx, float dy) {
		effectSprite = new AnimatedSprite(dx, dy, (ITiledTextureRegion) iTextureRegion, mBaseGame.getVertexBufferObjectManager());
		effectSprite.setPosition(effectSprite.getX(), effectSprite.getY()-effectSprite.getHeight()/2);
	}
	public AnimatedSprite getScoreText(){
		return effectSprite;
	}
	
	public void animate(){
		if(!isClearEntity&&!isActiveEffect){
			effectSprite.animate(100,false, new IAnimationListener() {
				
				@Override
				public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
						int pInitialLoopCount) {
					// TODO Auto-generated method stub
					isActiveEffect = true;
				}
				
				@Override
				public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
						int pRemainingLoopCount, int pInitialLoopCount) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
						int pOldFrameIndex, int pNewFrameIndex) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
					// TODO Auto-generated method stub
					isClearEntity = true;
				}
			});
		}

	}

	public void attachScene(Scene scene) {
		scene.attachChild(effectSprite);
	}

	public void detachScene(Scene scene) {
		scene.detachChild(effectSprite);
	}


	

}
