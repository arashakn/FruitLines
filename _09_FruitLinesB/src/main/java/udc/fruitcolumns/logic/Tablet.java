package udc.fruitcolumns.logic;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.modifier.IModifier;

import java.util.List;
import java.util.Random;

import amaztricks.game.manager.ConfigManager;

public class Tablet {
    private AnimatedSprite[] tabletSprite = new AnimatedSprite[3];
    private int[][] tabletLocation = new int[3][3];
    private int[] tabletColor = new int[3];
    public static int TABLET_INIT_LOCATION_x0 = 4;
    public static int TABLET_INIT_LOCATION_y0 = 1;
    public static int TABLET_INIT_LOCATION_y1 = 2;
    public static int TABLET_INIT_LOCATION_y2 = 3;

    private static final int MOVE_DOWN_MODULE = 50;
    private int moveDownModuleCounting = 0;
    private int moveSideCount = 0;
    boolean isMovingSide = false;

    public Tablet(BaseGameActivity mBaseGame,
                  List<ITextureRegion> listTextureRegions) {
        Random random = new Random();

        ITextureRegion tabletTextureRegion = listTextureRegions.get(0);
        boolean hasSkill = false;
        for (int i = 0; i < tabletSprite.length; i++) {
            tabletSprite[i] = new AnimatedSprite(0, 0,
                    (ITiledTextureRegion) tabletTextureRegion,
                    mBaseGame.getVertexBufferObjectManager());
            if (ConfigManager.GAME_STATUS[0] < 2) {
                tabletColor[i] = random.nextInt(5);
            } else if (ConfigManager.GAME_STATUS[0] < 3) {
                tabletColor[i] = random.nextInt(6);
            } else if (ConfigManager.GAME_STATUS[0] < 5) {
                int ran = random.nextInt(100);
                if (ran > 95 && !hasSkill) {
                    hasSkill = true;
                    tabletColor[i] = TabletController.BLACK_HOLE_FRAME;
                    tabletSprite[i].animate(new long[]{300, 300, 300},
                            new int[]{7, 8, 9});
                } else {
                    tabletColor[i] = random.nextInt(7);

                }
            } else {
                if (random.nextInt(100) > 95 && !hasSkill) {
                    hasSkill = true;
                    tabletColor[i] = TabletController.BLACK_HOLE_FRAME;
                    tabletSprite[i].animate(new long[]{300, 300, 300},
                            new int[]{7, 8, 9});
                } else if (random.nextInt(100) > 90 && !hasSkill) {
                    hasSkill = true;
                    if (random.nextInt(100) > 50) {
                        tabletColor[i] = TabletController.THUNDER_FRAME_HORIZONTAL;
                        tabletSprite[i].animate(
                                new long[]{300, 300, 300, 300}, new int[]{
                                        10, 11, 12, 13});
                    } else {
                        tabletColor[i] = TabletController.THUNDER_FRAME_VERTICAL;
                        tabletSprite[i].animate(
                                new long[]{300, 300, 300, 300}, new int[]{
                                        14, 15, 16, 17});
                    }

                } else {
                    tabletColor[i] = random.nextInt(7);

                }

            }
            tabletSprite[i].setCurrentTileIndex(tabletColor[i]);
        }
        // tabletColor[0] = 0;
        // tabletColor[1] = 0;
        // tabletColor[2] = new Random().nextInt(2);
        // tabletSprite[0].setCurrentTileIndex(tabletColor[0]);
        // tabletSprite[1].setCurrentTileIndex(tabletColor[1]);
        // tabletSprite[2].setCurrentTileIndex(tabletColor[2]);

        initLocation();
    }

    public void attachScene(Scene scene) {
        // scene.attachChild(tabletSprite[0]);
        // scene.attachChild(tabletSprite[1]);
        initLocation();
    }

    public void detachScene(Scene scene) {
        for (int i = 0; i < tabletSprite.length; i++) {
            scene.detachChild(tabletSprite[i]);
            tabletSprite[i].dispose();
            tabletSprite[i] = null;
            // Runtime.getRuntime().gc();
        }
    }

    public int beginIndex = 0;

    public void rotate() {
        if (isMovingSide) {
            return;
        }
        beginIndex++;
        if (beginIndex >= tabletColor.length) {
            beginIndex = 0;
        }
        int bufferX = tabletLocation[0][0];
        int bufferY = tabletLocation[0][1];
        for (int i = 0; i < tabletSprite.length - 1; i++) {
            tabletLocation[i][0] = tabletLocation[i + 1][0];
            tabletLocation[i][1] = tabletLocation[i + 1][1];

        }
        tabletLocation[tabletSprite.length - 1][0] = bufferX;
        tabletLocation[tabletSprite.length - 1][1] = bufferY;
        updateLocation();
    }

    float timeMove = 0.1f;

    public void moveRight() {
        if (isMovingSide) {
            return;
        }
        isMovingSide = true;
        for (int i = 0; i < tabletSprite.length; i++) {
            final int index = i;
            tabletSprite[i].registerEntityModifier(new MoveXModifier(timeMove,
                    tabletSprite[i].getX(), tabletSprite[i].getX()
                    + TabletView.cellWidth,
                    new IEntityModifierListener() {
                        @Override
                        public void onModifierStarted(
                                IModifier<IEntity> pModifier, IEntity pItem) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onModifierFinished(
                                IModifier<IEntity> pModifier, IEntity pItem) {
                            // TODO Auto-generated method stub
                            tabletLocation[index][0]++;
                            moveSideCount++;
                            if (moveSideCount == tabletLocation.length) {
                                moveSideCount = 0;
                                isMovingSide = false;
                            }
                        }
                    }));
        }
        // updateLocation();

    }

    public void moveLeft() {
        if (isMovingSide) {
            return;
        }
        isMovingSide = true;
        for (int i = 0; i < tabletSprite.length; i++) {
            final int index = i;
            tabletSprite[i].registerEntityModifier(new MoveXModifier(timeMove,
                    tabletSprite[i].getX(), tabletSprite[i].getX()
                    - TabletView.cellWidth,
                    new IEntityModifierListener() {
                        @Override
                        public void onModifierStarted(
                                IModifier<IEntity> pModifier, IEntity pItem) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onModifierFinished(
                                IModifier<IEntity> pModifier, IEntity pItem) {
                            // TODO Auto-generated method stub
                            tabletLocation[index][0]--;
                            moveSideCount++;
                            if (moveSideCount == tabletLocation.length) {
                                moveSideCount = 0;
                                isMovingSide = false;
                            }
                        }
                    }));
        }

        // for(int i=0;i<tabletSprite.length;i++){
        // tabletLocation[i][0]--;
        // }
        // updateLocation();

    }

    public void moveDown() {
        if (isMovingSide) {
            return;
        }
        moveDownModuleCounting++;
        if (moveDownModuleCounting % (50 / MOVE_DOWN_MODULE) == 0) {
            moveDownModuleCounting = 0;
            for (int i = 0; i < tabletSprite.length; i++) {
                tabletLocation[i][1]++;
            }
        }
        updateLocation();
    }

    public AnimatedSprite[] getTabletSprite() {
        return tabletSprite;
    }

    public void setTabletSprite(AnimatedSprite[] tabletSprite) {
        this.tabletSprite = tabletSprite;
    }

    public int[][] getTabletLocation() {
        return tabletLocation;
    }

    public void setTabletLocation(int[][] tabletLocation) {
        this.tabletLocation = tabletLocation;
        updateLocation();

    }

    public void setTabletLocation(Scene scene, float marginX, float marginY) {
        float scaleRate = 0.75f;
        for (int i = 0; i < tabletSprite.length; i++) {
            tabletSprite[i].setScale(scaleRate);
            tabletSprite[i].setPosition(marginX, marginY + i
                    * +TabletView.cellHeight * scaleRate);
            scene.attachChild(tabletSprite[i]);
        }

    }

    private void initLocation() {
        for (int i = 0; i < tabletSprite.length; i++) {
            tabletSprite[i].setScale(1f);
        }
        tabletLocation[0][0] = TABLET_INIT_LOCATION_x0;
        tabletLocation[0][1] = TABLET_INIT_LOCATION_y0;
        tabletLocation[1][0] = TABLET_INIT_LOCATION_x0;
        tabletLocation[1][1] = TABLET_INIT_LOCATION_y1;
        tabletLocation[2][0] = TABLET_INIT_LOCATION_x0;
        tabletLocation[2][1] = TABLET_INIT_LOCATION_y2;
        updateLocation();

    }

    public void updateLocation() {
        for (int i = 0; i < tabletSprite.length; i++) {
            tabletSprite[i].setPosition(TabletView.marginX
                            + tabletLocation[i][0] * TabletView.cellWidth,
                    TabletView.marginY + tabletLocation[i][1]
                            * TabletView.cellHeight + MOVE_DOWN_MODULE
                            * moveDownModuleCounting);

        }
    }

    public int[] getTabletColor() {
        return tabletColor;
    }

    public void setTabletColor(int[] tabletColor) {
        this.tabletColor = tabletColor;
    }

}
