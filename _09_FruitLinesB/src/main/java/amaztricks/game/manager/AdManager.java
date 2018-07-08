package amaztricks.game.manager;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.hanastudio.columns.activity.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import org.andengine.ui.activity.BaseGameActivity;

public class AdManager {

    private static AdView interstitial;

    private static RelativeLayout adViewLayout;

    private static boolean receiverAds;
    private static BaseGameActivity mBaseGameActivity;

    public static void adAdview(final BaseGameActivity mBaseGame, boolean isActiveAdd) {
        mBaseGameActivity = mBaseGame;
        if (isActiveAdd) {
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mBaseGame.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            adViewLayout = new RelativeLayout(mBaseGame);
                            adViewLayout.setLayoutParams(new LayoutParams(
                                    LayoutParams.MATCH_PARENT,
                                    LayoutParams.WRAP_CONTENT));
                            adViewLayout.setBackgroundColor(0xff000000);

                            ((RelativeLayout) mBaseGame
                                    .findViewById(R.id.mainLayout))
                                    .addView(adViewLayout);

                            interstitial = new AdView(mBaseGame);
                            interstitial.setAdSize(AdSize.SMART_BANNER);
                            interstitial
                                    .setAdUnitId("ca-app-pub-2287645634707543/5181326312");
                            // add adview into layout
                            adViewLayout.addView(interstitial);
                            // // Create ad request
                            AdRequest adRequest = new AdRequest.Builder().build();
                            interstitial.loadAd(adRequest);
                            // Set Ad Listener to use the callbacks below
                            interstitial.setAdListener(new AdListener() {
                                public void onAdLoaded() {
                                    receiverAds = true;
                                }

                                public void onAdFailedToLoad(int errorcode) {

                                }

                                public void onAdClosed() {

                                }

                                public void onAdLeftApplication() {
                                }

                            });

                        }
                    });

                }
            });
            t.start();
        }


    }

    // static void loadAd(BaseGameActivity mBaseGame) {
    // mBaseGame.runOnUiThread(new Runnable() {
    //
    // @Override
    // public void run() {
    // // TODO Auto-generated method stub
    // if (!receiverAds && interstitial != null) {
    // interstitial.loadAd(new AdRequest());
    // }
    // }
    // });
    // }

    public static void inactiveAd() {
        mBaseGameActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                adViewLayout.setEnabled(false);
                adViewLayout.setVisibility(View.GONE);
            }
        });

    }

    public static void activeAd() {
        if (AdManager.receiverAds) {
            mBaseGameActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    adViewLayout.setEnabled(true);
                    adViewLayout.setVisibility(View.VISIBLE);
                }
            });

        }

    }

}
