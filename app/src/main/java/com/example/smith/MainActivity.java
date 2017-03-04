package com.example.smith;

import java.util.Timer;

import transmission.Complex;
import transmission.transmission_line;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ZoomImageView.OnPointMoveListener {
    private TextView tvVSWR, tvReflectionIn, tvZin, tvReflectionL, tvDistance;
    private EditText etZlReal, etZlImg, etZ0;
    private Button btnCalculate;
    private ZoomImageView zoomImageView;
    private SeekBar sbDistance;

    /**
     * 用于计算的传输线类&smith图类对象
     */
    private transmission_line trans_line = new transmission_line();


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

        tvVSWR = (TextView) this.findViewById(R.id.tvVSWR);
        tvReflectionIn = (TextView) this.findViewById(R.id.tvReflectionIn);
        tvZin = (TextView) this.findViewById(R.id.tvZin);
        tvReflectionL = (TextView) this.findViewById(R.id.tvReflectionL);
        etZlReal = (EditText) this.findViewById(R.id.etZlReal);
        etZlImg = (EditText) this.findViewById(R.id.etZlImg);
        etZ0 = (EditText) this.findViewById(R.id.etZ0);
        tvDistance = (TextView) this.findViewById(R.id.tvDistance);
        btnCalculate = (Button) findViewById(R.id.btnCalculate);
        sbDistance = (SeekBar) this.findViewById(R.id.sbDistance);
        seekBarInit();

        zoomImageView = (ZoomImageView) findViewById(R.id.zivSmithChart);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.smith_chart_xbig);
        Bitmap point = BitmapFactory.decodeResource(getResources(), R.drawable.point);

        zoomImageView.setImageBitmap(bitmap, point);

        etZ0.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                String s1;
                s1 = etZ0.getText().toString().trim();
                if ("".equals(s1)) {
                } else {
                    trans_line.setZ0(Double.parseDouble(s1));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }
        });

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etZlReal.getText())
                        | TextUtils.isEmpty(etZlReal.getText())) {
                    Toast.makeText(getApplicationContext(), "ZL不能为空！ORZ",
                            Toast.LENGTH_SHORT).show();
                } else {
                    trans_line.setZL(
                            Double.parseDouble(etZlReal.getText().toString()),
                            Double.parseDouble(etZlImg.getText().toString()));

                    tvReflectionL.setText(trans_line.ZLZ0CalcReL().toString());

                    Complex Rin = new Complex(trans_line.ReLCalcReIn());
                    zoomImageView.fromSeekBar(Rin.getReal(), Rin.getImage());

                    tvReflectionIn.setText(Rin.toString());
                    tvZin.setText(trans_line.ReInCalcZin().toString());
                    tvVSWR.setText(String.format("%.3f", trans_line.ReLmoCalcVSWR()));
                }
            }
        });
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            super.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onPointMoved() {
        tvReflectionIn.setText(trans_line.abCalcReIn(
                zoomImageView.getPointA(), zoomImageView.getPointB())
                .toString());
        tvZin.setText(trans_line.ReInCalcZin().toString());
        tvReflectionL.setText(trans_line.ReInCalcReL().toString());
        tvVSWR.setText(String.format("%.3f", trans_line.ReLmoCalcVSWR()));
        etZlReal.setText(String.format("%.3f", trans_line.ReLCalcZL()
                .getReal()));
        etZlImg.setText(String.format("%.3f", trans_line.getZL()
                .getImage()));
    }


    private void seekBarInit() {
        sbDistance.setProgress(0);
        OnSeekBarChangeListener osbcl = new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                trans_line.setd((double) sbDistance.getProgress() / seekBar.getMax());
                tvDistance.setText(Double.toString(trans_line.getd()));

                Complex Rin = new Complex(trans_line.ReLCalcReIn());
                zoomImageView.fromSeekBar(Rin.getReal(), Rin.getImage());
                tvReflectionIn.setText(Rin.toString());
                tvZin.setText(trans_line.ReInCalcZin().toString());
                onPointMoved();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
        sbDistance.setOnSeekBarChangeListener(osbcl);
    }
}