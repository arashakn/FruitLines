package amaztricks.game.dialog;

import com.hanastudio.columns.activity.MainActivity;

import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

import amaztricks.game.manager.ConfigManager;
import amaztricks.game.manager.HighScoreManager;
import game.android.dialog.DialogSaveHighScore;

public class GameOverDialog extends MyDialog {

    public GameOverDialog(BaseGameActivity mBaseGame) {
        super(mBaseGame);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onloadResource() {
        super.onloadResource();
        titleWidth = 256;
        titleHeight = 64;
        BitmapTextureAtlas textureAtlas = new BitmapTextureAtlas(
                getTextureManager(), titleWidth, titleHeight,
                TextureOptions.BILINEAR);
        ITextureRegion textureRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(textureAtlas, getAssets(),
                        "dialog/gameover.png", 0, 0);
        textureAtlas.load();
        title = new Sprite(0, 0, textureRegion, getVertexBufferObjectManager());

        BitmapTextureAtlas[] buttonAtlas = new BitmapTextureAtlas[2];
        TiledTextureRegion[] buttonRegion = new TiledTextureRegion[buttonAtlas.length];
        buttonSpr = new ButtonSprite[buttonAtlas.length];


        MyButtonCLickListener onClickListener = new MyButtonCLickListener() {
            @Override
            public void onClick(ButtonSprite pButtonSprite,
                                float pTouchAreaLocalX, float pTouchAreaLocalY) {
                // TODO Auto-generated method stub
                super.onClick(pButtonSprite, pTouchAreaLocalX, pTouchAreaLocalY);
                if (pButtonSprite == buttonSpr[1]) {
                    // share
                    mBaseGame.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            ShareDialog.createShareOption(mBaseGame);
                        }
                    });

                } else if (pButtonSprite == buttonSpr[0]) {
                    final boolean isHighScore = HighScoreManager
                            .checkHighScore(ConfigManager.finalScore, mBaseGame);
                    if (isHighScore) {
                        showDialogSaveDollar();
                    } else {
                        ConfigManager.GAME_STATUS = null;
                        MainActivity.playScene.finish();
                    }
                    action(false);
                }

            }
        };


        for (int i = 0; i < buttonAtlas.length; i++) {

            int buttonDx = (int) (ConfigManager.CAMERA_WIDTH) / 2;
            int buttonDy = (int) (resultBoardDy + resultBoardHeight
                    - buttonHeight / 4 - 20);

            buttonAtlas[i] = new BitmapTextureAtlas(getTextureManager(),
                    buttonWidth, buttonHeight, TextureOptions.BILINEAR);
            if (i == 1) {
                buttonRegion[i] = BitmapTextureAtlasTextureRegionFactory
                        .createTiledFromAsset(buttonAtlas[i],
                                mBaseGame.getAssets(), "dialog/share.png", 0,
                                0, 1, 2);
                buttonDx += (30);

            } else if (i == 0) {
                buttonRegion[i] = BitmapTextureAtlasTextureRegionFactory
                        .createTiledFromAsset(buttonAtlas[i],
                                mBaseGame.getAssets(), "dialog/next.png", 0, 0,
                                1, 2);
                buttonDx -= (30 + buttonWidth);

            }

            buttonAtlas[i].load();

            buttonSpr[i] = new ButtonSprite(buttonDx, buttonDy,
                    buttonRegion[i], mBaseGame.getVertexBufferObjectManager(),
                    onClickListener);

        }
    }

    @Override
    public void action(boolean setActive) {
        super.action(setActive);
        content.setText("Result: " + ConfigManager.finalScore + "\n\nLevel: "
                + ConfigManager.finalLevel);
        content.setPosition(ConfigManager.CAMERA_WIDTH / 2 - content.getWidth()
                        / 2,
                resultBoardDy + resultBoardHeight / 2 - content.getHeight() / 2
                        - 10);

    }

    public void showDialogSaveDollar() {
        mBaseGame.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                new DialogSaveHighScore(mBaseGame);
            }
        });
    }
}
