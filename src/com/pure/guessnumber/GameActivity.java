package com.pure.guessnumber;

import net.youmi.android.AdManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pure.guessnumber.util.LogUtil;
import com.pure.guessnumber.util.Utils;

public class GameActivity extends Activity {
	private String TAG = getClass().getSimpleName();
	public static final String GAME_CONFIG = "game_config";
	private static final int MENU_CONFIG = Menu.FIRST;
	private static final int MENU_HELP = MENU_CONFIG + 1;
	private static final int MENU_ABOUT = MENU_HELP + 1;
	private static final int MENU_EXIT = MENU_ABOUT + 1;
	private SharedPreferences gameConfig;
	private long guessNumber;
	private int minGv = 1;
	private int maxGv = 100;
	private int guessCount = 0;
	private int gameCount = 0;
	private int gc = 0;
	private boolean isGuess;
	private boolean isCleargv;

	private EditText et_guess_value;
	private Button btn_guess;
	private TextView tv_guess_num_range_text;
	private TextView tv_guess_result;
	private TextView tv_guess_num_stat;
	private Resources resources;

	static {
		AdManager.init("da57fd4008d5f880", "95689602aebd7cea", 32, false, 1.0);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.setLogOff(true);
		setContentView(R.layout.game);
		resources = getResources();
		gameConfig = getApplicationContext().getSharedPreferences(GameActivity.GAME_CONFIG, 0);
		et_guess_value = (EditText) findViewById(R.id.et_guess_value);
		et_guess_value.setFilters(new InputFilter[] { new InputFilter.LengthFilter(3) });
		btn_guess = (Button) findViewById(R.id.btn_guess);
		tv_guess_num_range_text = (TextView) findViewById(R.id.tv_guess_num_range_text);
		tv_guess_num_stat = (TextView) findViewById(R.id.tv_guess_num_stat);
		tv_guess_result = (TextView) findViewById(R.id.tv_guess_result);
		btn_guess.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (isGuess) {
					generateNumber();
				} else {
					String msg = "";
					int gv = (et_guess_value.getText() == null || et_guess_value.getText().toString() == null || "".equals(et_guess_value.getText().toString())) ? 0 : Integer.valueOf(et_guess_value.getText().toString());
					if (gv <= minGv || gv >= maxGv) {
						msg = resources.getString(R.string.game_msg_guess_error, minGv, maxGv);
					} else {
						guessCount++;
						if (gv != guessNumber) {
							if (gv < guessNumber) {
								minGv = gv;
							} else {
								maxGv = gv;
							}
							gc = (maxGv - minGv - 1);
							msg = resources.getString(R.string.game_msg_guess_fail);
						} else {
							isGuess = true;
							gameCount++;
							msg = resources.getString(R.string.game_msg_guess_success, guessCount, guessNumber);
							btn_guess.setText(resources.getString(R.string.btn_goon));
							InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(et_guess_value.getWindowToken(), 0);
						}
					}
					tv_guess_result.setText(msg);
					setGuessNumRangeText();
					if (isCleargv) {
						et_guess_value.setText("");
					}
				}
			}
		});

		generateNumber();
	}

	public void setGuessNumRangeText() {
		tv_guess_num_range_text.setText(resources.getString(R.string.game_msg_guess_num_range, minGv, maxGv));
		if (guessCount > 0) {
			if (isGuess) {
				tv_guess_num_stat.setText("");
			} else {
				tv_guess_num_stat.setText(resources.getString(R.string.game_msg_guess_stat, guessCount, gc));
			}
		}
	}

	public void generateNumber() {
		isGuess = false;
		isCleargv = gameConfig.getBoolean(GameConfigDialog.IS_CLEAR_GV, GameConfigDialog.DEFAULT_IS_CLEAR_GV);
		minGv = 1;
		maxGv = gameConfig.getInt(GameConfigDialog.RANDOM_NUM_RANGE, GameConfigDialog.DEFAULT_RANDOM_NUM_RANGE);
		guessNumber = Utils.getRandomByRange(minGv + 1, maxGv - 1);
		guessCount = 0;
		gc = 0;
		et_guess_value.setText("");
		tv_guess_result.setText("");
		tv_guess_num_stat.setText("");
		btn_guess.setText(resources.getString(R.string.btn_ok));
		setGuessNumRangeText();
		LogUtil.show(TAG, "->generateNumber:" + guessNumber, Log.DEBUG);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this).setTitle(resources.getString(R.string.app_name)).setMessage(resources.getString(R.string.exit_msg)).setNegativeButton(resources.getString(R.string.btn_goon), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (isGuess) {
						generateNumber();
					}
				}
			}).setPositiveButton(resources.getString(R.string.btn_exit), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					finish();
				}
			}).setNeutralButton(resources.getString(R.string.btn_begin), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					generateNumber();
				}
			}).show();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_CONFIG, 0, resources.getString(R.string.menu_item_config)).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, MENU_HELP, 0, resources.getString(R.string.menu_item_help)).setIcon(android.R.drawable.ic_menu_help);
		menu.add(0, MENU_ABOUT, 0, resources.getString(R.string.menu_item_about)).setIcon(android.R.drawable.ic_menu_info_details);
		menu.add(0, MENU_EXIT, 0, resources.getString(R.string.menu_item_exit)).setIcon(android.R.drawable.ic_lock_power_off);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case MENU_CONFIG:
			GameConfigDialog gameConfigDialog = new GameConfigDialog(this);
			gameConfigDialog.show();
			return true;
		case MENU_HELP:
			showDialog(MENU_HELP);
			return true;
		case MENU_ABOUT:
			showDialog(MENU_ABOUT);
			return true;
		case MENU_EXIT:
			finish();
			return true;
		}
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog result = null;
		Builder builder;

		switch (id) {
		case MENU_HELP:
			builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.app_name) + getString(R.string.menu_item_help));
			builder.setMessage(getString(R.string.app_help));
			builder.setPositiveButton(getString(R.string.btn_ok), null);
			result = builder.create();
			break;
		case MENU_ABOUT:
			builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.app_name) + " " + getString(R.string.app_version));
			builder.setMessage(getString(R.string.app_about));
			builder.setPositiveButton(getString(R.string.btn_ok), null);
			result = builder.create();
			break;
		default:
			result = super.onCreateDialog(id);
			break;
		}
		return result;
	}

}
