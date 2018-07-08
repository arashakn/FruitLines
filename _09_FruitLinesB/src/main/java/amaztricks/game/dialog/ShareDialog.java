package amaztricks.game.dialog;

import android.app.Activity;
import android.content.Intent;

import com.hanastudio.columns.activity.R;

import amaztricks.game.manager.ConfigManager;

public class ShareDialog {

    public static void createShareOption(final Activity mBaseGame) {
        String title = "Share";
        final String listTitle = title;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        String subject = mBaseGame.getResources().getString(
                R.string.app_name);
        String messageShare = " Link: "
                + mBaseGame.getString(R.string.web_link)
                + "\n My high score: "
                + ConfigManager.finalScore;
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, messageShare);
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                subject);
        mBaseGame.startActivity(Intent.createChooser(shareIntent,
                listTitle));

    }

}
