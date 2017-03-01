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

/**
 * 作者：lyw on 2016/11/1 10:31
 */

public interface BkkDataListener {
    public static final String KEY_R = "04";
    public static final String LONG_R = "09";
    public static final String KEY_L = "01";
    public static final String LONG_L = "06";
    public static final String KEY_G = "05";
    public static final String LONG_G = "0a";
    public static final String KEY_E = "03";
    public static final String LONG_E = "08";
    public static final String KEY_POWER = "02";
    public static final String LONG_POWER = "07";
    /**
     * 蓝牙连接
     */
    void Connected();//CONNECTED

    /**
     * 蓝牙断开
     */
    void DisConnected();//DISCONNECTED

    /**
     * 蓝牙模式
     * @param mode 如果不能控制模式，返回"无法控制"
     */
    void Mode(String mode);//MODE

    /**
     * 按键数据
     * @param data
     */
    void PressData(String data);//ACTION_DATA_MOTION

    /**
     * 摇动数据
     * @param stringExtra 硬件名称
     * @param floatExtra 摇动数据
     */
    void MotionData(String stringExtra, float floatExtra);//ACTION_DATA_PRESS
}
