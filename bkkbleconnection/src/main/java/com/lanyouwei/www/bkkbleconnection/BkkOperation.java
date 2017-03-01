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

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.lanyouwei.www.bkkbleconnection.Ble.BKKController;
import com.lanyouwei.www.bkkbleconnection.Ble.BluetoothLeService;

/**
 * 作者：lyw on 2016/10/31 11:17
 */

public abstract class BkkOperation implements ServiceConnection {
    private final static String TAG = BluetoothLeService.class.getSimpleName();
   // private boolean isOffBLE = false;

    //连接
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        BKKController bluetoothLeService= ((BluetoothLeService.LocalBinder) service).getService();
        onController(bluetoothLeService);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }

    public abstract void onController(BKKController BKKController);

}
