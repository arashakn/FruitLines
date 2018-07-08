package amaztricks.game.manager;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.hanastudio.columns.activity.MainActivity;
import com.hanastudio.columns.activity.R;

import org.android.datastore.LocalStore;

import java.util.ArrayList;

//import amaztricks.googleplay.service.WinFragment;

public class HighScoreManager extends BaseGameActivity {

    LinearLayout content;
    Typeface face;
    static final int MAX_BEST_PLAYER_NUMBER = 10;

    // request codes we use when invoking an external activity
    final int RC_RESOLVE = 5000, RC_UNUSED = 5001;

    // tag for debug logging
    final boolean ENABLE_DEBUG = true;
    final String TAG = "fruitlines";
    // achievements and scores we're pending to push to the cloud
    // (waiting for the user to sign in, for instance)
    AccomplishmentsOutbox mOutbox = new AccomplishmentsOutbox();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
        face = Typeface.createFromAsset(getAssets(), "font/ANTSYPAN.TTF");

        ((TextView) findViewById(R.id.textView1)).setTypeface(face);
        ((TextView) findViewById(R.id.textView2)).setTypeface(face);
        ((TextView) findViewById(R.id.textView3)).setTypeface(face);
        ((TextView) findViewById(R.id.textView4)).setTypeface(face);


        Button button_yes = (Button) findViewById(R.id.button_contine);
        button_yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HighScoreManager.this.finish();
            }
        });
        Button show_leaderboards_button = (Button) findViewById(R.id.show_leaderboards_button);
        show_leaderboards_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isSignedIn()) {
                    onSignInButtonClicked();
                } else {
                    onShowLeaderboardsRequested();
                    // pushAccomplishments();

                }
            }
        });

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        onloadScore();
    }

    public void onloadScore() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                content = (LinearLayout) findViewById(R.id.content);
                content.removeAllViews();
                String[][] highScore = (String[][]) LocalStore.getObject(
                        MainActivity.PREF_FILE_NAME + ConfigManager.SCORE_GAME_ADDRESS,
                        HighScoreManager.this);
                ArrayList<BestPlayer> listBestPlayer = new ArrayList<BestPlayer>();
                if (highScore == null) {
                    for (int i = 0; i < MAX_BEST_PLAYER_NUMBER; i++) {
                        listBestPlayer.add(new BestPlayer("--", "---", "---"));
                    }
                } else {
                    for (int i = 0; i < MAX_BEST_PLAYER_NUMBER; i++) {
                        listBestPlayer.add(new BestPlayer(highScore[i][0],
                                highScore[i][1], highScore[i][2]));
                    }
                }

                for (int i = 0; i < listBestPlayer.size(); i++) {
                    content.addView(getView(i, listBestPlayer.get(i)), i);
                }

            }
        });
    }

    private long getTheHighestScore() {
        long highestScore = 0;
        String[][] highScore = (String[][]) LocalStore.getObject(
                MainActivity.PREF_FILE_NAME + ConfigManager.SCORE_GAME_ADDRESS,
                this);
        if (highScore != null) {
            highestScore = Long.parseLong(highScore[0][1]);
        }
        return highestScore;
    }

    public static boolean checkHighScore(int checkScore, Context context) {

        String[][] highScore = (String[][]) LocalStore.getObject(
                MainActivity.PREF_FILE_NAME + ConfigManager.SCORE_GAME_ADDRESS,
                context);

        if (highScore == null) {
            return true;
        } else {
            for (int i = 0; i < 10; i++) {
                if (highScore[i][1].equals("---")) {
                    return true;
                }
                int savedScore = Integer.parseInt(highScore[i][1]);
                if (checkScore > savedScore) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void addHighScore(String name, int score, int level,
                                    Context context) {

        String[][] highScore = (String[][]) LocalStore.getObject(
                MainActivity.PREF_FILE_NAME + ConfigManager.SCORE_GAME_ADDRESS,
                context);

        if (highScore == null) {
            highScore = new String[MAX_BEST_PLAYER_NUMBER][];
            for (int i = 0; i < MAX_BEST_PLAYER_NUMBER; i++) {
                highScore[i] = new String[3];
                for (int j = 0; j < highScore[i].length; j++) {
                    highScore[i][j] = new String();
                }
            }
            for (int i = 0; i < highScore.length; i++) {
                if (i == 0) {
                    highScore[0][0] = name;
                    highScore[0][1] = String.valueOf(score);
                    highScore[0][2] = String.valueOf(level);

                } else {
                    highScore[i][0] = "---";
                    highScore[i][1] = "---";
                    highScore[i][2] = "---";

                }
            }
        } else {
            int curPosition = MAX_BEST_PLAYER_NUMBER;
            for (int i = 0; i < MAX_BEST_PLAYER_NUMBER; i++) {
                if (highScore[i][1].equals("---")) {
                    curPosition--;
                    continue;
                }
                int savedScore = Integer.parseInt(highScore[i][1]);
                if (score > savedScore) {
                    curPosition--;
                }
            }
            // sap xep lai vi tri diem
            for (int i = MAX_BEST_PLAYER_NUMBER - 1; i > curPosition; i--) {
                highScore[i][0] = highScore[i - 1][0];
                highScore[i][1] = highScore[i - 1][1];
                highScore[i][2] = highScore[i - 1][2];

            }
            // cap nhap vi tri diem moi
            highScore[curPosition][0] = name;
            highScore[curPosition][1] = String.valueOf(score);
            highScore[curPosition][2] = String.valueOf(level);

        }

        LocalStore.putObject(MainActivity.PREF_FILE_NAME
                + ConfigManager.SCORE_GAME_ADDRESS, highScore, context);
    }

    public View getView(int stt, BestPlayer bestPlayer) {

        View convertView = View.inflate(this, R.layout.item_top10, null);

        // Set the check-icon

        TextView numTv = (TextView) convertView.findViewById(R.id.nuTv);
        numTv.setText("" + (stt + 1));
        numTv.setTypeface(face);

        TextView nameTv = (TextView) convertView.findViewById(R.id.nameTv);
        nameTv.setText(bestPlayer.getName());
        nameTv.setTypeface(face);

        TextView scoreTv = (TextView) convertView.findViewById(R.id.scoreTv);
        scoreTv.setText(bestPlayer.getScore());
        scoreTv.setTypeface(face);

        TextView levelTextView = (TextView) convertView
                .findViewById(R.id.levelTv);
        levelTextView.setText(bestPlayer.getLevel());
        levelTextView.setTypeface(face);
        return convertView;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HighScoreManager.this.finish();
    }


    public void onShowLeaderboardsRequested() {
        if (isSignedIn()) {
            pushAccomplishments();
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(), getString(R.string.leaderboard_fruit_lines)),
                    RC_UNUSED);
        } else {
            onSignInButtonClicked();

        }
    }


    public void onSignInButtonClicked() {
        beginUserInitiatedSignIn();
    }


    void pushAccomplishments() {
        if (!isSignedIn()) {
            // can't push to the cloud, so save locally
            return;
        }

        Games.Leaderboards.submitScore(getApiClient(),
                getString(R.string.leaderboard_fruit_lines),
                getTheHighestScore());

    }


    @Override
    public void onSignInFailed() {
        // Sign-in failed, so show sign-in button on main menu

    }

    @Override
    public void onSignInSucceeded() {

    }


    class AccomplishmentsOutbox {

    }
}