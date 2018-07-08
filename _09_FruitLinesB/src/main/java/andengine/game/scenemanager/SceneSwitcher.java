package andengine.game.scenemanager;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.ui.activity.SimpleBaseGameActivity;

public class SceneSwitcher {

//	public static boolean isFinish1;
//	public static boolean isFinish2;
//	public static boolean isFinish3;

	
	/**
	 * switch to next scene
	 * clear current scene
	 * @param pEngine
	 * @param pNextScene
	 * 
	 * @return
	 */
	public final static boolean switchScene(final Engine pEngine, final Scene pNextScene) {
		if (pEngine.getScene() != pNextScene) {
			pEngine.getScene().postRunnable(new Runnable() {

				public void run() {
					SceneSwitcher.clearCurrentScene(pEngine);
					pEngine.setScene(pNextScene);
				}

			});
			return true;
		} else {
			return false;
		}
	}
	
	public final static boolean switchScene(final SimpleBaseGameActivity pBaseGameActivity, final Engine pEngine, final Scene pNextScene) {
		if (pEngine.getScene() != pNextScene) {
			pEngine.getScene().postRunnable(new Runnable() {

				public void run() {
					SceneSwitcher.clearCurrentScene(pEngine);
					pEngine.setScene(pNextScene);
				}

			});
			return true;
		} else {
			return false;
		}
	}
	/**
	 * switch to next scene
	 * clear current scene or not
	 * @param pBaseGameActivity
	 * @param pNextScene
	 * @param isClearCurrentScene
	 */

	public final static void switchScene(final BaseGameActivity pBaseGameActivity, final IScenePattern pNextScene,final boolean isClearCurrentScene) {
		final Engine mEngine = pBaseGameActivity.getEngine();
		mEngine.getScene().postRunnable(new Runnable() {

			public void run() {
				if(isClearCurrentScene)
					SceneSwitcher.clearCurrentScene(mEngine);
				pNextScene.onLoadResources(pBaseGameActivity);
				mEngine.setScene(pNextScene.onLoadScene(pBaseGameActivity));
			}

		});
	}
	
	/**
	 * switch to next scene
	 * clear current scene or not
	 * load resource of next scene or not
	 * @param pBaseGameActivity
	 * @param pNextScene
	 * @param isClearCurrentScene
	 * @param isLoadNextScene
	 */
	public final static void switchScene(final BaseGameActivity pBaseGameActivity, final IScenePattern pNextScene,final boolean isClearCurrentScene,final boolean isLoadNextScene) {
		final Engine mEngine = pBaseGameActivity.getEngine();
		mEngine.getScene().postRunnable(new Runnable() {

			public void run() {
				if(isClearCurrentScene)
					clearCurrentScene(mEngine);
				if(isLoadNextScene)
					pNextScene.onLoadResources(pBaseGameActivity);
				mEngine.setScene(pNextScene.onLoadScene(pBaseGameActivity));
			}

		});
	}
	

	
	public final static void switchScene(final BaseGameActivity pBaseGameActivity, final IScenePattern pNextScene, final Scene pLoadingScene, final IScenePattern pLoadingScenePattern, final boolean isUnloadLoadingScene) {
		final Engine mEngine = pBaseGameActivity.getEngine();
	//	SceneSwitcher.clearCurrentScene(mEngine);
		
		final Scene mScene = mEngine.getScene();

		mScene.postRunnable(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				clearCurrentScene(mScene);
			}
		});
		
		mEngine.setScene(pLoadingScene);
		
		final IAsyncCallback callback = new IAsyncCallback() {
			 
			Scene mNextScene = null;
			public void workToDo() {
				pNextScene.onLoadResources(pBaseGameActivity);
				if (isUnloadLoadingScene) {
					SceneSwitcher.clearCurrentScene(mEngine);
					pLoadingScenePattern.onUnloadResoures(pBaseGameActivity);
				}
				mNextScene = pNextScene.onLoadScene(pBaseGameActivity);
			}
 
			public void onComplete() {
				mEngine.setScene(mNextScene);
			}
		};
		
		pBaseGameActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				final AsyncTaskLoader mAsyncTaskLoader = new AsyncTaskLoader();
				mAsyncTaskLoader.execute(callback);
//				isFinish3 = true;
			}
		});
//		mEngine.runOnUpdateThread(new Runnable() {
//			public void run() {
//				final AsyncTaskLoader mAsyncTaskLoader = new AsyncTaskLoader();
//				mAsyncTaskLoader.execute(callback);
//			}
//		});
		
//		while (!((BatZoi)pBaseGameActivity).isLoading) {
//			((BatZoi)pBaseGameActivity).isLoading = isFinish1 && isFinish2 && isFinish3;
//		};
		
		
		
//		((BatZoi)pBaseGameActivity).isLoading = false;
	}
	
	public final static void switchScene(final BaseGameActivity pBaseGameActivity, final IScenePattern pNextScene, final IScenePattern pLoadingScene, final boolean isUnloadLoadingScene) {
		final Engine mEngine = pBaseGameActivity.getEngine();
		final Scene mScene = mEngine.getScene();
		
		mScene.postRunnable(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				clearCurrentScene(mScene);
			}
		});
		
		pLoadingScene.onLoadResources(pBaseGameActivity);
		mEngine.setScene(pLoadingScene.onLoadScene(pBaseGameActivity));
		IAsyncCallback callback = new IAsyncCallback() {
			 
			public void workToDo() {
				pNextScene.onLoadResources(pBaseGameActivity);
			}
 
			public void onComplete() {
				if (isUnloadLoadingScene) {
					SceneSwitcher.clearCurrentScene(mEngine);
					pLoadingScene.onUnloadResoures(pBaseGameActivity);
				}
				mEngine.setScene(pNextScene.onLoadScene(pBaseGameActivity));
			}
		};
		(new AsyncTaskLoader()).execute(callback);
	}
	
	private final static void clearCurrentScene(final Engine pEngine) {
		Scene scene = pEngine.getScene(); 
		scene.reset();
		scene.clearUpdateHandlers();
		scene.clearTouchAreas();
		scene.clearEntityModifiers();
		scene.detachChildren();
		scene.dispose();
	}
	
	private final static void clearCurrentScene(final Scene pScene) {
		if (pScene != null) {
			pScene.reset();
			pScene.clearUpdateHandlers();
			pScene.clearTouchAreas();
			pScene.clearEntityModifiers();
			pScene.detachChildren();
			pScene.dispose();
		}
	}
}
