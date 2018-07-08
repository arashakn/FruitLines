package game.android.dialog;

import com.hanastudio.columns.activity.MainActivity;
import com.hanastudio.columns.activity.R;

import amaztricks.game.dialog.ShareDialog;
import amaztricks.game.manager.ConfigManager;
import amaztricks.game.manager.HighScoreManager;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class DialogSaveHighScore {
	// Activity activity;
	EditText editText_name;    
	public DialogSaveHighScore(final Context context) {
		Activity activity = (Activity) context;

		final RelativeLayout parentLayout = (RelativeLayout) activity
				.findViewById(R.id.mainLayout);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = inflater.inflate(R.layout.dialog_savescore, null);

		final Animation viewAnimation = AnimationUtils.loadAnimation(context,
				R.anim.slide_in_top_dialog);
		view.startAnimation(viewAnimation);

		final RelativeLayout transparentLayout = new RelativeLayout(context);
		transparentLayout.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		transparentLayout.setBackgroundColor(0x44000000);
		transparentLayout.addView(view);
		
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view
				.getLayoutParams();
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);

		parentLayout.addView(transparentLayout);

		editText_name = (EditText) view.findViewById(R.id.editText_name);
		
		Button button_share = (Button) view.findViewById(R.id.button_share);
		button_share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ShareDialog.createShareOption((Activity) context);
			}
		});
		Button button_yes = (Button) view.findViewById(R.id.button_contine);
		button_yes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				final Animation viewAnimation = AnimationUtils.loadAnimation(
						context, R.anim.slide_out_bottom_dialog);
				viewAnimation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationRepeat(Animation arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationEnd(Animation arg0) {
						// TODO Auto-generated method stub
						String name = editText_name.getText().toString();
						if (name.length() == 0) {
							name = "Player";
						}

						HighScoreManager.addHighScore(name,
								ConfigManager.finalScore,ConfigManager.finalLevel,context);
						parentLayout.removeView(transparentLayout);
				        Toast.makeText(context, "Save successfully.", Toast.LENGTH_SHORT).show();
						MainActivity.playScene.finish();

					}
				});
				view.startAnimation(viewAnimation);


			}
		});

	}

}