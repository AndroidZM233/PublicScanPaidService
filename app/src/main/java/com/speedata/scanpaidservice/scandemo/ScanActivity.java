package com.speedata.scanpaidservice.scandemo;

import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.barcode.HSMDecodeResult;
import com.honeywell.barcode.HSMDecoder;
import com.honeywell.plugins.decode.DecodeResultListener;
import com.speedata.scanpaidservice.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.honeywell.barcode.Symbology.CODE11;
import static com.honeywell.barcode.Symbology.CODE128;
import static com.honeywell.barcode.Symbology.CODE39;
import static com.honeywell.barcode.Symbology.CODE93;
import static com.honeywell.barcode.Symbology.EAN13;
import static com.honeywell.barcode.Symbology.EAN8;
import static com.honeywell.barcode.Symbology.GS1_128;
import static com.honeywell.barcode.Symbology.UPCA;


public class ScanActivity extends AppCompatActivity implements DecodeResultListener {//}, DecodeResultListener {

    private final static String TAG = "MainActivity";
    private HSMDecoder hsmDecoder;
    private TextView mTvShow;
    private TextView mTvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_demo);
        setSettings();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hsmDecoder.removeResultListener(this);
        Camera camera = hsmDecoder.getCamera();
        camera.release();
        HSMDecoder.disposeInstance();
        System.out.println("关闭相机");
    }


    public void setSettings() {
        hsmDecoder = HSMDecoder.getInstance(this);
        hsmDecoder.addResultListener(this);
        //初始为默认前置摄像头扫码
        hsmDecoder.enableSymbology(CODE11);
        hsmDecoder.enableSymbology(EAN13);
        hsmDecoder.enableSymbology(CODE128);
        hsmDecoder.enableSymbology(CODE39);
        hsmDecoder.enableSymbology(CODE93);
        hsmDecoder.enableSymbology(EAN8);
        hsmDecoder.enableAimer(true);
        hsmDecoder.setAimerColor(Color.RED);
        hsmDecoder.enableSound(true);
        hsmDecoder.setOverlayText("ceshi");
        hsmDecoder.setOverlayTextColor(Color.RED);

    }

    private void initEnableDecode() {
        hsmDecoder.enableSymbology(UPCA);
        hsmDecoder.enableSymbology(EAN8);
        hsmDecoder.enableSymbology(EAN13);
        hsmDecoder.enableSymbology(CODE128);
        hsmDecoder.enableSymbology(GS1_128);
    }

    private List<ScanBean> list = new ArrayList<>();

    @Override
    public void onHSMDecodeResult(HSMDecodeResult[] hsmDecodeResults) {
        try {
            for (int i = 0; i < hsmDecodeResults.length; i++) {
                String decodeDate = new String(hsmDecodeResults[i].getBarcodeDataBytes(),
                        "utf8");
                boolean success = true;
                long longValue = hsmDecodeResults[i].getDecodeTime().longValue();
                for (ScanBean scanBean : list) {
                    String code = scanBean.getCode();
                    if (code.equals(decodeDate)) {
                        success = false;
                    }
                }
                if (success) {
                    ScanBean scanBean = new ScanBean();
                    scanBean.setCode(decodeDate);
                    scanBean.setLongValue(longValue);
                    list.add(scanBean);
                    mTvShow.append(decodeDate + "\n");
                    mTvTime.append(longValue + "ms\n");
                }
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initView() {
        mTvShow = findViewById(R.id.tv_show);
        mTvTime = findViewById(R.id.tv_time);
    }
}
