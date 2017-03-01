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

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.lanyouwei.www.bkkbleconnection.Ble.BluetoothLeService;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * 作者：lyw on 2016/10/31 09:59
 */

public class BKK {
    private final static String TAG = BluetoothLeService.class.getSimpleName();
    private static Activity mContext;
    private static final int REQUEST_ENABLE_BT = 1;
    private static BkkOperation mBkkOperation;
    private static BKKDataToMonitor instance;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0;

    /**
     * 初始化
     * @return 初始化成功
     */
    public static boolean init(Activity context){
        mContext = context;
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "BLE不可用", Toast.LENGTH_SHORT).show();
           return false;
        }
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(context, "不支持蓝牙", Toast.LENGTH_SHORT).show();
            return false;
        }
        try{
            mBluetoothAdapter.enable();//打开蓝牙
        }catch(Exception e){e.printStackTrace();}

//        if (!mBluetoothAdapter.isEnabled()) {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                context.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//                return true;
//        }//

//        initPermission(context);
        return true;
    }

    private static void initPermission(Activity context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            //判断是否需要 向用户解释，为什么要申请该权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(context, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static Intent service;

    /**
     * 开启蓝牙服务
     */
    public static void start(){
        if(service == null){
            service = new Intent(mContext, BluetoothLeService.class);
            mContext.startService(service);
        }
    }

    /**
     * 关闭蓝牙服务
     */
    public static void stop(){

        if(service!=null){
            mContext.stopService(service);
            service = null;
        }
    }

    /**
     * 获取蓝牙转态监听
     * @param bkkOperation
     * @return
     */
    public static BkkOperation operationStart(BkkOperation bkkOperation){
        if(service ==null){
            start();
        }
        if(mBkkOperation == null){
            mBkkOperation = bkkOperation;
            mContext.bindService(service, bkkOperation, BIND_AUTO_CREATE);
            return bkkOperation;
        }
      return null;
    }

    /**
     * 释放蓝牙
     * @param b 是否关闭蓝牙服务
     */
    public static void operationStop(boolean b){
        if(mBkkOperation!=null){
            mContext.unbindService(mBkkOperation);
            mBkkOperation = null;
            if(b){
                stop();
            }
        }
    }

    /**
     * 获取数据监听
     * @param bkkDataListener
     */
    public static void setBKKDataToMonitor(BkkDataListener bkkDataListener){
        instance = BKKDataToMonitor.getInstance();
        instance.init(mContext,bkkDataListener);


    }

    /**
     * 清空数据监听
     */
    public static void cancelBKKDataToMonitor(){
        if (instance!=null){
            instance.clear(mContext);
            instance = null;
        }
    }
}
