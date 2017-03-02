package com.example.smith;

import java.util.Timer;
import java.util.TimerTask;

import transmission.Complex;
import transmission.SmithChart;
import transmission.transmission_line;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	private TextView etext_vswr, etext_refin, etext_zin, etext_refl;
	private EditText etex_zlreal, etex_zlimg, etex_z0, etex_d;
	private ZoomImageView zoomImageView;
	private Timer timer = new java.util.Timer(true);
	private SeekBar seekbar;
	private double a, b;

	/**
	 * 用于计算的传输线类&smith图类对象
	 */
	private transmission_line trans_line = new transmission_line();
	private SmithChart Schart = new SmithChart();

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {// 检测到move point后
			    etext_refin.setText(trans_line.abCalcReIn(
						zoomImageView.getPointA(), zoomImageView.getPointB())
						.toString());
				etext_zin.setText(trans_line.ReInCalcZin().toString());
				etext_refl.setText(trans_line.ReInCalcReL().toString());
//				etext_vswr.setText(Double.toString(trans_line.ReLmoCalcVSWR()));
				etext_vswr.setText(String.format("%.3f", trans_line.ReLmoCalcVSWR()));
//				etex_zlreal.setText(Double.toString(trans_line.ReLCalcZL()
//						.getReal()));
//				etex_zlimg.setText(Double.toString(trans_line.getZL()
//						.getImage()));
				etex_zlreal.setText(String.format("%.3f", trans_line.ReLCalcZL()
						.getReal()));
				etex_zlimg.setText(String.format("%.3f", trans_line.getZL()
						.getImage()));
			} else if (msg.what == 1) {// seekbar
				etex_d.setText(Double.toString(trans_line.getd()));

				Complex Rin = new Complex(trans_line.ReLCalcReIn());
				zoomImageView.fromSeekBar(Rin.getReal(), Rin.getImage());
				etext_refin.setText(Rin.toString());
				etext_zin.setText(trans_line.ReInCalcZin().toString());

			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 隐藏ActionBar
		ActionBar actionBar = this.getSupportActionBar();
		actionBar.hide();
		// 隐藏标题栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		etext_vswr = (TextView) this.findViewById(R.id.vswr);
		etext_refin = (TextView) this.findViewById(R.id.reflection_in);
		etext_zin = (TextView) this.findViewById(R.id.zin);
		etext_refl = (TextView) this.findViewById(R.id.reflection_l);
		etex_zlreal = (EditText) this.findViewById(R.id.zlreal);
		etex_zlimg = (EditText) this.findViewById(R.id.zlimg);
		etex_z0 = (EditText) this.findViewById(R.id.z0);
		etex_d = (EditText) this.findViewById(R.id.d);
		seekbar = (SeekBar) this.findViewById(R.id.seekbar);
		sb_init();

		zoomImageView = (ZoomImageView) findViewById(R.id.zoom_image_view);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.abc_smith_xbig);
		Bitmap point = BitmapFactory.decodeResource(getResources(),R.drawable.point);

//		Bitmap bitmap = BitmapFactory.decodeStream(getResources()
//				.openRawResource(R.drawable.abc_smith_xbig));
//		Bitmap point = BitmapFactory.decodeStream(getResources()
//				.openRawResource(R.drawable.point));
		zoomImageView.setImageBitmap(bitmap, point);

		etex_z0.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				String s1;
				s1 = etex_z0.getText().toString().trim();
				if ("".equals(s1)) {
				} else {
					trans_line.setZ0(Double.parseDouble(s1));
				}
			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
		});

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (zoomImageView.getStatus() == zoomImageView.STATUS_MOVE_POINT
						||zoomImageView.getStatus() == zoomImageView.STATUS_INIT ) {
					mHandler.sendEmptyMessage(0);
				}
			}
		};
		timer.schedule(task, 0, 30);// 0ms后每50ms执行一次
	}

	public void clickHandler1(View source) {

		if (TextUtils.isEmpty(etex_zlreal.getText())
				| TextUtils.isEmpty(etex_zlreal.getText())) {
			Toast.makeText(getApplicationContext(), "ZL不能为空！ORZ",
					Toast.LENGTH_SHORT).show();
		} else {
			trans_line.setZL(
					Double.parseDouble(etex_zlreal.getText().toString()),
					Double.parseDouble(etex_zlimg.getText().toString()));
			
			etext_refl.setText(trans_line.ZLZ0CalcReL().toString());
			
			Complex Rin = new Complex(trans_line.ReLCalcReIn());
			zoomImageView.fromSeekBar(Rin.getReal(), Rin.getImage());
						
			etext_refin.setText(Rin.toString());
			etext_zin.setText(trans_line.ReInCalcZin().toString());
//			etext_vswr.setText(Double.toString(trans_line.ReLmoCalcVSWR()));
			etext_vswr.setText(String.format("%.3f", trans_line.ReLmoCalcVSWR()));
		}
	}

	public void sb_init() {
		seekbar.setProgress(0);
		OnSeekBarChangeListener osbcl = new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				trans_line.setd((double) seekbar.getProgress() / seekBar.getMax());
				mHandler.sendEmptyMessage(1);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		};
		seekbar.setOnSeekBarChangeListener(osbcl);
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			super.onConfigurationChanged(newConfig);
		}
	}
}