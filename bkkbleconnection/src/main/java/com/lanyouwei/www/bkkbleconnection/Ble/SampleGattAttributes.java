/*
 * Copyright (C) 2013 The Android Open Source Project
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

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    //private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String BBK_PRESS_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String BBK_PRESS_CHARAC1 = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String BBK_MOTION_SERVICE = "0000ffa0-0000-1000-8000-00805f9b34fb";
    public static String BBK_MOTION_CHARAC1 = "0000ffa1-0000-1000-8000-00805f9b34fb";
    public static String BBK_DEVICE_INFORMATION = "0000180a-0000-1000-8000-00805f9b34fb";//获取硬件属性服务
    public static String BBK_FIRMWARE_REVISION = "00002a25-0000-1000-8000-00805f9b34fb";//获取硬件版本号
    public static String BBK_MODE_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";//获取模式服务
    public static String BBK_MODE_CHARAC = "0000fff1-0000-1000-8000-00805f9b34fb";//获取或者写入模式
}
