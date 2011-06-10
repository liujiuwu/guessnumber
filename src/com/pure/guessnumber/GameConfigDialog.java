package com.pure.guessnumber;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.pure.guessnumber.util.LogUtil;

public class GameConfigDialog extends Dialog {
	public static final int DEFAULT_RANDOM_NUM_RANGE = 100;
	public static final boolean DEFAULT_IS_CLEAR_GV = true;
	public static final String RANDOM_NUM_RANGE = "random_num_range";
	public static final String IS_CLEAR_GV = "is_clear_gv";
	private String TAG = getClass().getSimpleName();
	private GameActivity gameActivity;
	private GameConfigDialog gameConfigDialog;
	private SharedPreferences gameConfig;
	private RadioGroup rg_config;
	private RadioButton rg_config_rb_0;
	private RadioButton rg_config_rb_1;
	private Button btn_save;
	private Button btn_cancel;
	private CheckBox cb_clear;
	private int random_num_range = DEFAULT_RANDOM_NUM_RANGE;
	private boolean isCleargv = DEFAULT_IS_CLEAR_GV;
	private Resources resources;

	public GameConfigDialog(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		gameActivity = (GameActivity) context;
		gameConfigDialog = this;
	}

	public GameConfigDialog(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		resources = this.getContext().getResources();
		gameConfig = this.getContext().getApplicationContext().getSharedPreferences(GameActivity.GAME_CONFIG, 0);
		this.setContentView(R.layout.game_config_dialog);
		this.setTitle(resources.getString(R.string.game_config_title));

		rg_config = (RadioGroup) findViewById(R.id.rg_config);
		rg_config_rb_0 = (RadioButton) findViewById(R.id.rg_config_rb_0);
		rg_config_rb_1 = (RadioButton) findViewById(R.id.rg_config_rb_1);
		cb_clear = (CheckBox) findViewById(R.id.cb_clear);
		btn_save = (Button) findViewById(R.id.btn_save);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);

		random_num_range = gameConfig.getInt(RANDOM_NUM_RANGE, DEFAULT_RANDOM_NUM_RANGE);
		if (random_num_range == DEFAULT_RANDOM_NUM_RANGE) {
			rg_config_rb_0.setChecked(true);
		} else if (random_num_range == 1000) {
			rg_config_rb_1.setChecked(true);
		}
		
		isCleargv = gameConfig.getBoolean(IS_CLEAR_GV, DEFAULT_IS_CLEAR_GV);
		cb_clear.setChecked(isCleargv);

		rg_config.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				random_num_range = (checkedId == rg_config_rb_0.getId()) ? DEFAULT_RANDOM_NUM_RANGE : 1000;
			}
		});

		cb_clear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isCleargv = isChecked;
			}
		});

		btn_save.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				gameConfig.edit().putInt(RANDOM_NUM_RANGE, random_num_range).putBoolean(IS_CLEAR_GV, isCleargv).commit();
				LogUtil.show(TAG, "" + random_num_range + "|" + gameConfig.getInt(RANDOM_NUM_RANGE, DEFAULT_RANDOM_NUM_RANGE) + "|" + gameConfig.getBoolean(IS_CLEAR_GV,DEFAULT_IS_CLEAR_GV), Log.DEBUG);
				gameActivity.generateNumber();
				gameConfigDialog.cancel();
			}
		});

		btn_cancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				gameConfigDialog.cancel();
			}
		});
	}
}
