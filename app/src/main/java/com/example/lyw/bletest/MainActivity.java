package com.example.lyw.bletest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.lyw.bletest.utlis.LogUtils;
import com.lanyouwei.www.bkkbleconnection.BKK;
import com.lanyouwei.www.bkkbleconnection.BkkDataListener;
import com.lanyouwei.www.bkkbleconnection.BkkOperation;
import com.lanyouwei.www.bkkbleconnection.Ble.BKKController;

import java.util.TimerTask;

public class MainActivity extends Activity implements View.OnClickListener {

    private Intent service;

    private TextView tvMode;
    private static final int REQUEST_ENABLE_BT = 1;
    private TextView textView;
    private Button button_power;
    private Button button_E;
    private Button button_G;
    private Button button_L;
    private Button button_R;
    private ToggleButton togg;
    private BKKController bkkController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(this, "BLE不可用", Toast.LENGTH_SHORT).show();
//             finish();
//        }
//
//       // Log.d(TAG, "L:是否有陀螺仪" + getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE));
//        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        BluetoothAdapter  mBluetoothAdapter = bluetoothManager.getAdapter();
//        if (mBluetoothAdapter == null) {
//            Toast.makeText(this, "不支持蓝牙", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//        if (!mBluetoothAdapter.isEnabled()) {
//            if (!mBluetoothAdapter.isEnabled()) {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            }
//        }
        boolean init = BKK.init(this);
        LogUtils.d("初始化蓝牙="+init);
       if(!init){
           finish();
       }
        BKK.operationStart(new BkkOperation() {
            @Override
            public void onController(BKKController bKKController) {
                bkkController = bKKController;
                java.util.Timer timer = new java.util.Timer(true);
                TimerTask task = new TimerTask() {
                    public void run() {
                         bkkController.ConnectDeviceName(new String[]{"BKK Cup","BKK Cup"});
                         bkkController.Scan();
                    }
                };
                timer.schedule(task, 2000);//,init中直接打开 2秒后,执行一次
//                bkkController.ConnectDeviceName(new String[]{"BKK Cup","BKK Cup"});
//                bkkController.Scan();
            }
        });
        tvMode = (TextView) findViewById(R.id.tv_mode);
        Button tbMode = (Button) findViewById(R.id.bt_mode);
        Button tbMode1 = (Button) findViewById(R.id.bt_mode1);
        Button tbMode2 = (Button) findViewById(R.id.bt_mode2);
        Button tbMode3 = (Button) findViewById(R.id.bt_mode3);
        tbMode.setOnClickListener(this);
        tbMode1.setOnClickListener(this);
        tbMode2.setOnClickListener(this);
        tbMode3.setOnClickListener(this);

//        service = new Intent(this, BluetoothLeService.class);
//        startService(service);
//    bindService(service, mServiceConnection, BIND_AUTO_CREATE);

        textView = (TextView) findViewById(R.id.frq);
        button_power = (Button) findViewById(R.id.button_power);
        button_E = (Button) findViewById(R.id.button_E);
        button_G = (Button) findViewById(R.id.button_G);
        button_L = (Button) findViewById(R.id.button_L);
        button_R = (Button) findViewById(R.id.button_R);
        togg =  (ToggleButton) findViewById(R.id.button_refresh);
        button_power.setBackgroundColor(getResources().getColor(R.color.lv));
        button_E.setBackgroundColor(getResources().getColor(R.color.lv));
        button_G.setBackgroundColor(getResources().getColor(R.color.lv));
        button_L.setBackgroundColor(getResources().getColor(R.color.lv));
        button_R.setBackgroundColor(getResources().getColor(R.color.lv));
    }

    @Override
    protected void onResume() {
        super.onResume();

     //   registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        BKK.setBKKDataToMonitor(new BkkDataListener() {
            @Override
            public void Connected() {//连接成功
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"连接成功,请用BKK cup 进行控制!",Toast.LENGTH_LONG).show();
                    }
                });
                updateTogg(true);
            }

            @Override
            public void DisConnected() {//取消连接
                updateTogg(false);
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this,"蓝牙已断开!",Toast.LENGTH_LONG).show();
                        textView.setText("取消连接");
                    }
                });
            }

            @Override
            public void Mode(String mode) {
                tvMode.setText(mode);
            }

            @Override
            public void PressData(final String data) {
                changeColor(data);

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                recoverColor(data);
                            }
                        });
                    }
                });
                thread.start();
            }

            @Override
            public void MotionData(final String stringExtra, final float floatExtra) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        textView.setText(stringExtra + "\n:" + floatExtra + "\n");
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        BKK.cancelBKKDataToMonitor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d("程序销毁");
        BKK.operationStop(true);
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_mode:
                tvMode.setText("");
            //
                //    mBluetoothLeService.readMode();
                bkkController.readMode();
                break;
            case R.id.bt_mode1:
              //  mBluetoothLeService.WriteModeDescriptor("mode1");
                bkkController.setMode("mode1");
                break;
            case R.id.bt_mode2:
                bkkController.setMode("mode2");
                break;
            case R.id.bt_mode3:
                bkkController.setMode("mode3");
                break;
        }
    }
    private void updateTogg(final boolean isConect) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                togg.setChecked(isConect);

            }
        });
    }
    /**
     * 蓝牙设备控制手机,改变按钮颜色
     *
     * @param data
     */
    private void changeColor(String data) {
        // TODO Auto-generated method stub
        if (data.equals("04")) {
            button_R.setBackgroundColor(getResources().getColor(R.color.hong));
            // button_R.setBackgroundColor(getResources().getColor(R.color.lv));
        }
        if (data.equals("09")) {
            button_R.setBackgroundColor(getResources().getColor(R.color.lan));
            // button_R.setBackgroundColor(getResources().getColor(R.color.lv));
        }
        if (data.equals("01")) {
            button_L.setBackgroundColor(getResources().getColor(R.color.hong));
            // button_L.setBackgroundColor(getResources().getColor(R.color.lv));
        }
        if (data.equals("06")) {
            button_L.setBackgroundColor(getResources().getColor(R.color.lan));
            // button_L.setBackgroundColor(getResources().getColor(R.color.lv));
        }
        if (data.equals("05")) {
            button_G.setBackgroundColor(getResources().getColor(R.color.hong));
            // button_G.setBackgroundColor(getResources().getColor(R.color.lv));
        }
        if (data.equals("0a")) {
            button_G.setBackgroundColor(getResources().getColor(R.color.lan));
            // button_G.setBackgroundColor(getResources().getColor(R.color.lv));
        }
        if (data.equals("03")) {
            button_E.setBackgroundColor(getResources().getColor(R.color.hong));
            // button_E.setBackgroundColor(getResources().getColor(R.color.lv));
        }
        if (data.equals("08")) {
            button_E.setBackgroundColor(getResources().getColor(R.color.lan));
            // button_E.setBackgroundColor(getResources().getColor(R.color.lv));
        }
        if (data.equals("02")) {
            button_power.setBackgroundColor(getResources().getColor(R.color.hong));
            // button_power.setBackgroundColor(getResources().getColor(R.color.lv));
        }
        if (data.equals("07")) {
            button_power.setBackgroundColor(getResources().getColor(R.color.lan));

        }
    }

    private void recoverColor(String data) {
        if (data.equals("04")) {

            button_R.setBackgroundColor(getResources().getColor(R.color.lv));
            // button_R.setBackgroundColor(getResources().getColor(R.color.lv));
        }
        if (data.equals("09")) {
            button_R.setBackgroundColor(getResources().getColor(R.color.lv));
            // button_R.setBackgroundColor(getResources().getColor(R.color.lv));
        }
        if (data.equals("01")) {
            button_L.setBackgroundColor(getResources().getColor(R.color.lv));
            // button_L.setBackgroundColor(getResources().getColor(R.color.lv));
        }
        if (data.equals("06")) {
            button_L.setBackgroundColor(getResources().getColor(R.color.lv));
            // button_L.setBackgroundColor(getResources().getColor(R.color.lv));
        }
        if (data.equals("05")) {
            button_G.setBackgroundColor(getResources().getColor(R.color.lv));
            // button_G.setBackgroundColor(getResources().getColor(R.color.lv));
        }
        if (data.equals("0a")) {
            button_G.setBackgroundColor(getResources().getColor(R.color.lv));
            // button_G.setBackgroundColor(getResources().getColor(R.color.lv));
        }
        if (data.equals("03")) {
            button_E.setBackgroundColor(getResources().getColor(R.color.lv));
            // button_E.setBackgroundColor(getResources().getColor(R.color.lv));
        }
        if (data.equals("08")) {
            button_E.setBackgroundColor(getResources().getColor(R.color.lv));
            // button_E.setBackgroundColor(getResources().getColor(R.color.lv));
        }
        if (data.equals("02")) {
            button_power.setBackgroundColor(getResources().getColor(R.color.lv));
            // button_power.setBackgroundColor(getResources().getColor(R.color.lv));
        }
        if (data.equals("07")) {

            button_power.setBackgroundColor(getResources().getColor(R.color.lv));

        }
    }

}
