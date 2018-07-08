package andengine.game.scenemanager;

import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

public interface IScenePattern {

	void onLoadResources(final BaseGameActivity mBaseGame);
	Scene onLoadScene(final BaseGameActivity mBaseGame);
	void onUnloadResoures(final BaseGameActivity mBaseGame);
	
}
