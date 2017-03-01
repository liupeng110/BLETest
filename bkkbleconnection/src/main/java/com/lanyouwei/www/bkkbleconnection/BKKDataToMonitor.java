package com.lanyouwei.www.bkkbleconnection;
/*
 *                    _ooOoo_
 *                   o8888888o
 *                   88" . "88
 *                   (| -_- |)
 *                   O\  =  /O
 *                ____/`---'\____
 *              .'  \\|     |//  `.
 *             /  \\|||  :  |||//  \
 *            /  _||||| -:- |||||-  \
 *            |   | \\\  -  /// |   |
 *            | \_|  ''\---/''  |   |
 *            \  .-\__  `-`  ___/-. /
 *          ___`. .'  /--.--\  '. .'__
 *       ."" '<  `.___\_<|>_/___.'  >'"".
 *      | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *      \  \ `-.   \_ __\ /__ _/   .-` /  /
 * ======`-.____`-.___\_____/___.-`____.-'======
 *				  	`=---='
 *^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *               佛祖保佑       永无BUG
*/

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.lanyouwei.www.bkkbleconnection.Ble.BluetoothLeService;

/**
 * 作者：lyw on 2016/10/31 17:51
 */

public class BKKDataToMonitor {

    private MyBroadcastReceiver myBroadcastReceiver;
    private BkkDataListener mBkkDataListener;

    public static BKKDataToMonitor getInstance() {
        return new BKKDataToMonitor();
    }
    public void init(Context context,BkkDataListener bkkDataListener){
        setListener(bkkDataListener);
        if(myBroadcastReceiver ==null){
            myBroadcastReceiver = new MyBroadcastReceiver();
        }
        context.registerReceiver(myBroadcastReceiver,makeGattUpdateIntentFilter());
    }



    public void clear(Context context){
        if(myBroadcastReceiver !=null){
            context.unregisterReceiver(myBroadcastReceiver);
            mBkkDataListener=null;
        }

    }

    public void setListener(BkkDataListener bkkDataListener){
        mBkkDataListener = bkkDataListener;
    }
    /**
     * 监听广播Intent
     * @return
     */
    private  IntentFilter makeGattUpdateIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);//连接
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);//断开
        intentFilter.addAction(BluetoothLeService.ACTION_MODE);//模式
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_MOTION);//摇动
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_PRESS);//按键
        return intentFilter;
    }

    class MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
             String action = intent.getAction();
            if(BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)){//连接
                mBkkDataListener.Connected();
            }else if(BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)){
                mBkkDataListener.DisConnected();
            }else if(BluetoothLeService.ACTION_MODE.equals(action)){
                String data = intent.getStringExtra(BluetoothLeService.DEVICE_MODE);
                if(data.equals("1001")){
                    data = "无法控制";
                }
                mBkkDataListener.Mode(data);
            }else if(BluetoothLeService.ACTION_DATA_PRESS.equals(action)){
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                mBkkDataListener.PressData(data);
            }else if(BluetoothLeService.ACTION_DATA_MOTION.equals(action)){
                float floatExtra = intent.getFloatExtra(BluetoothLeService.EXTRA_DATA, 0);
                String stringExtra = intent.getStringExtra(BluetoothLeService.DEVICE_NAME);
                mBkkDataListener.MotionData(stringExtra,floatExtra);
            }else if(action.equals("android.bluetooth.adapter.action.DISCOVERY_FINISHED")){//
                Log.i("blue","蓝牙扫描结束");
//                bkkController.ConnectDeviceName(new String[]{"BKK Cup","BKK Cup"});
//                bkkController.Scan();
            }
            Log.i("blue","蓝牙扫描:"+action);

        }
    }
}
