/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lanyouwei.www.bkkbleconnection.Ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */
public class BluetoothLeService extends Service implements BKKController {
    public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
    public final static UUID UUID_BBK_PRESS_SERVICE = UUID.fromString(SampleGattAttributes.BBK_PRESS_SERVICE);
    public final static UUID UUID_BBK_PRESS_CHARAC1 = UUID.fromString(SampleGattAttributes.BBK_PRESS_CHARAC1);
    public final static UUID UUID_BBK_MOTION_CHARAC1 = UUID.fromString(SampleGattAttributes.BBK_MOTION_CHARAC1);
    public final static UUID UUID_BBK_MOTION_SERVICE = UUID.fromString(SampleGattAttributes.BBK_MOTION_SERVICE);
    public final static UUID UUID_BBK_DEVICE_INFORMATION = UUID.fromString(SampleGattAttributes.BBK_DEVICE_INFORMATION);
    public final static UUID UUID_BBK_FIRMWARE_REVISION = UUID.fromString(SampleGattAttributes.BBK_FIRMWARE_REVISION);
    public final static UUID UUID_BBK_MODE_SERVICE = UUID.fromString(SampleGattAttributes.BBK_MODE_SERVICE);
    public final static UUID UUID_BKK_MODE_CHARAC = UUID.fromString(SampleGattAttributes.BBK_MODE_CHARAC);

    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_MODE = "com.example.bluetooth.le.ACTION_MODE";
    public final static String DEVICE_MODE = "com.example.bluetooth.le.DEVICE_MODE";
    public final static String ACTION_DATA_PRESS = "com.example.bluetooth.le.ACTION_DATA_PRESS";
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
    public final static String DEVICE_NAME = "com.example.bluetooth.le.DEVICE_NAME";
    public final static String ACTION_DATA_MOTION = "com.example.bluetooth.le.ACTION_DATA_MOTION";

    private final static String TAG = BluetoothLeService.class.getSimpleName();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private MyLeScanCallback mLeScanCallback;
    private MyBluetoothGattCallback myBluetoothGattCallback;
    private BluetoothGatt mBluetoothGatt;
    private Handler mHandler;
    private BluetoothGattCharacteristic modeBGC;
    private String deviceName;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "BKK服务绑定");

        return new LocalBinder();
    }

    //解绑
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "BKK服务解除绑定");
        return super.onUnbind(intent);
    }

    //创建
    @Override
    public void onCreate() {
        Log.d(TAG, "BKK服务创建");
        super.onCreate();
        list = new ArrayList<Float>();
        wList = new ArrayList<Integer>();
        initialize();
    }

    //销毁
    @Override
    public void onDestroy() {
        Log.d(TAG, "BKK服务销毁");
        super.onDestroy();
        scanLeDevice(false);

        close();

        myBluetoothGattCallback = null;
        mBluetoothAdapter = null;
        mBluetoothManager = null;
    }

    /**
     * 初始化蓝牙空间
     *
     * @return
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter
        // through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }
        myBluetoothGattCallback = new MyBluetoothGattCallback();
        mHandler = new Handler();
        return true;
    }

    /**
     * 清空
     */
    public void close() {
        if(mBluetoothGatt !=null){
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    @Override
    public void Scan() {
        if(ConnectDeviceName ==null){
            Log.e(TAG,"ConnectDeviceName = null,请在Scan()前设置ConnectDeviceName");
        }else {
            scanLeDevice(true);
        }

    }

    @Override
    public void StopScan() {
        scanLeDevice(false);
    }

    @Override
    public void setMode(String mode) {
        if (modeBGC != null) {
            byte[] bytes = new byte[1];
            if ("mode1".equals(mode)) {
                bytes[0] = 0x02;
            } else if ("mode2".equals(mode)) {
                bytes[0] = 0x00;
            } else if ("mode3".equals(mode)) {
                bytes[0] = 0x01;
            }
            modeBGC.setValue(bytes);
            mBluetoothGatt.writeCharacteristic(modeBGC);
        }
    }

    @Override
    public void readMode() {
        if (modeBGC != null) {
            readCharacteristic(modeBGC);
        }
    }

    @Override
    public boolean isConnect() {
        if (mBluetoothGatt ==null){
            return false;
        }else {
            return true;
        }

    }
    private String[] ConnectDeviceName;
    @Override
    public void ConnectDeviceName(String[] DeviceName){
        ConnectDeviceName = DeviceName;
    }

    /**
     * 启动或者停止扫描
     *
     * @param enable
     */
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            Log.d(TAG, "蓝牙扫描开始");

            if (mLeScanCallback == null) {
                mLeScanCallback = new MyLeScanCallback();
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        } else {
            Log.d(TAG, "蓝牙扫描停止");
            if (mLeScanCallback != null) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mLeScanCallback = null;
            }
        }

    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @return Return true if the connection is initiated successfully. The
     * connection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    private boolean connect(BluetoothDevice device) {
        Log.d(TAG, "L:开始连接");
        mBluetoothGatt = device.connectGatt(this, true, myBluetoothGattCallback);
        return true;
    }

    /**
     * 获取需要的服务
     *
     * @return
     */
    public ArrayList<BluetoothGattCharacteristic> getCharacteristic() {
        if (mBluetoothGatt == null)
            return null;
        ArrayList<BluetoothGattCharacteristic> list = new ArrayList<BluetoothGattCharacteristic>();
        list.add(mBluetoothGatt.getService(UUID_BBK_DEVICE_INFORMATION).getCharacteristic(UUID_BBK_FIRMWARE_REVISION));
        list.add(mBluetoothGatt.getService(UUID_BBK_PRESS_SERVICE).getCharacteristic(UUID_BBK_PRESS_CHARAC1));
        list.add(mBluetoothGatt.getService(UUID_BBK_MOTION_SERVICE).getCharacteristic(UUID_BBK_MOTION_CHARAC1));
        if (mBluetoothGatt.getService(UUID_BBK_MODE_SERVICE) != null) {
            list.add(mBluetoothGatt.getService(UUID_BBK_MODE_SERVICE).getCharacteristic(UUID_BKK_MODE_CHARAC));
        } else {
            list.add(null);
        }
        return list;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read
     * result is reported asynchronously through the
     * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {

            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     *                       <p>
     *                       If true, enable notification. False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }

        mBluetoothGatt.setCharacteristicNotification(characteristic, true);
        Log.d("sad", "L:监听服务按键的");
        BluetoothGattDescriptor descriptor = characteristic
                .getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
    }


    /**
     * 扫描到回调
     */
    private class MyLeScanCallback implements BluetoothAdapter.LeScanCallback {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device == null) {
                return;
            }
            Log.d(TAG, "L:扫描到的信号为=" + rssi + "L:有扫描到=" + device.getName() + ";地址为=" + device.getAddress() + ";所有为=" + device.toString());
            try {
                String deviceName = device.getName();
                for (int i = 0 ;i<ConnectDeviceName.length;i++){
                    Log.d(TAG,"对比="+ConnectDeviceName[i]);
                    if(deviceName.equals(ConnectDeviceName[i])){
                        connect(device);
                        scanLeDevice(false);
                        return;
                    }
                }
            } catch (Exception e) {
            }
        }

    }

    private class MyBluetoothGattCallback extends BluetoothGattCallback {
        //连接状态
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            Log.d(TAG, "L连接代号=" + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mBluetoothGatt.discoverServices();//获取服务
                broadcastUpdate(ACTION_GATT_CONNECTED);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mBluetoothGatt.close();
                mBluetoothGatt = null;
                Log.d(TAG, "L断开连接，重新开始扫描");
                scanLeDevice(true);
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
            }
        }
        //setCharacteristicNotification

        /**
         * 获取服务
         *
         * @param gatt
         * @param status
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                final ArrayList<BluetoothGattCharacteristic> listCh = getCharacteristic();
                readCharacteristic(listCh.get(0));
                // Thread.sleep(1000);
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO 自动生成的方法存根
                        setCharacteristicNotification(listCh.get(1));
                    }

                }, 500);
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO 自动生成的方法存根
                        setCharacteristicNotification(listCh.get(2));
                    }

                }, 1000);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        modeBGC = listCh.get(3);
                        if (modeBGC != null) {
                            readCharacteristic(listCh.get(3));
                        } else {
                            broadcastUpdate(ACTION_MODE, DEVICE_MODE, "1001");
                        }
                    }
                }, 1500);
            }
        }

        /**
         * 读数据
         *
         * @param gatt
         * @param mBluetoothGatt
         * @param status
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic mBluetoothGatt, int status) {
            byte[] values = mBluetoothGatt.getValue();
            if (mBluetoothGatt.getUuid().equals(UUID_BBK_FIRMWARE_REVISION)) {
                deviceName = bytesToHexString(values);
                deviceName = toStringHex(deviceName);
                Log.d(TAG, "L:读到版本号：" + deviceName);
            } else if (mBluetoothGatt.getUuid().equals(UUID_BKK_MODE_CHARAC)) {
                Log.d(TAG, "读到模式为：" + bytesToHexString(values));
                broadcastUpdate(ACTION_MODE, DEVICE_MODE, bytesToHexString(values));
            }
        }

        /**
         * 监听数据
         *
         * @param gatt
         * @param characteristic
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte values[] = characteristic.getValue();
            String data = "";
            for (byte value : values) {
                data = data + value;
            }

            broadcastUpdate(characteristic);
        }
    }

    private void broadcastUpdate(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(String action, String kay, String string) {
        Intent intent = new Intent(action);
        intent.putExtra(kay, string);
        sendBroadcast(intent);
    }

    /**
     * 发送摇动数据广播
     *
     * @param characteristic
     */
    private void broadcastUpdate(BluetoothGattCharacteristic characteristic) {
        byte values[] = characteristic.getValue();
        String valuesData = bytesToHexString(values);
        //	Log.d("aaaa","蓝牙数据为="+valuesData);
        if (UUID_BBK_PRESS_CHARAC1.equals(characteristic.getUuid())) {
            Log.d("aaaa", "蓝牙数据为=" + valuesData);
            broadcastUpdate(ACTION_DATA_PRESS, EXTRA_DATA, valuesData);
        }
        if (UUID_BBK_MOTION_CHARAC1.equals(characteristic.getUuid())) {


            if (deviceName.equals("BKK Cup-1.0")) {// 飞机杯
                float parsedDataCup = parsedDataCup(valuesData);
                if (parsedDataCup != -1.0f) {
                    parsedDataCup = 1 / parsedDataCup;
                    if (parsedDataCup / 2 > 5.0f) {
                        parsedDataCup = 10.0f;
                    }
                    if (parsedDataCup / 2 > 0 && parsedDataCup / 2 < 1) {
                        parsedDataCup = 2.0f;
                    }
                    Log.d(TAG, "需要发送的数据为=" + parsedDataCup / 2);

                    sendMotionData(parsedDataCup / 2, ACTION_DATA_MOTION);
                }

            } else if (deviceName.equals("BKK Watch-1.0") || deviceName.equals("BKK Cup-2.0") || deviceName.equals("BKK Wristband-1.0")) {// 手环
                float parsedDataWristband = parsedDataWristband(valuesData);
                //	Log.d("AAAA","计算后为="+parsedDataWristband);
                if (parsedDataWristband != 0) {
                    Log.d(TAG, "需要发送的数据为=" + parsedDataWristband / 2);
                    sendMotionDataW(parsedDataWristband, ACTION_DATA_MOTION);
                }
            }
        }


    }

    private void sendMotionData(float parsedDataCup, String action) {
        // TODO 自动生成的方法存根
        if (parsedDataCup == 0) {
            return;
        }
        Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA, parsedDataCup);
        intent.putExtra(DEVICE_NAME, deviceName);
        sendBroadcast(intent);
    }

    private void sendMotionDataW(float parsedDataWristband, String actionDataMotion) {
        float num = parsedDataWristband;
        // Log.d(TAG, "L:sendMotionDataW"+num);
        num = (float) ((num * num) * 0.00000527 + 0.00326 * num);
        if (num > 1) {
            num = 1;
        }
        Intent intent = new Intent(actionDataMotion);
        intent.putExtra(EXTRA_DATA, num);
        intent.putExtra(DEVICE_NAME, deviceName);
        sendBroadcast(intent);
    }

    int num = 0;

    private float parsedDataCup(String valuesData) {
        // TODO 自动生成的方法存根
        float frq = -1;
        num++;
        if (isOne(valuesData)) {
            frq = average(num);
            num = 0;
        }
        return frq;
    }

    private boolean isOne(String valuesData) {
        // num++;
        String value = valuesData.substring(3, 4);
        if (value.equals("1")) {
            return true;
        }
        return false;

    }

    // private float sum;
    private List<Float> list;

    private float average(int num) { // 算出最近的三次频率的平均值
        // TODO Auto-generated method stub
        float average = 0;
        float time = (float) num * 0.01f;
        list.add(time);
        float sum = 0;
        if (list.size() <= 3) {
            for (int i = 0; i < list.size(); i++) {
                sum = sum + list.get(i);
            }
            average = sum / list.size();
        } else if (list.size() == 4) {
            list.remove(0);
            for (int i = 0; i < list.size(); i++) {
                sum = sum + list.get(i);
            }
            average = sum / list.size();
        }
        return average;
    }

    private List<Integer> wList;

    private float parsedDataWristband(String valuesData) {
        // TODO 自动生成的方法存根
        String value = valuesData.substring(4, 6);
        Integer valueOf = Integer.valueOf(value, 16);
        float average = 0;

        wList.add(valueOf);

        // System.out.println(valueOf+"--------------");
        if (wList.size() <= 3) {
            for (int i = 0; i < wList.size(); i++) {
                average = average + (float) wList.get(i);
            }
            // System.out.println("----------"+sum);
            average = average / wList.size();
        } else if (wList.size() == 4) {
            wList.remove(0);
            for (int i = 0; i < wList.size(); i++) {

                average = average + (float) wList.get(i);
            }
            average = average / wList.size();
        }
        return average;

    }

    /**
     * 服务器借口
     */
    public class LocalBinder extends Binder {
        public BKKController getService() {
            return BluetoothLeService.this;
        }
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String toStringHex(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "utf-8");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }
}
