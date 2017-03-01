package com.lanyouwei.www.bkkbleconnection.Ble;
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
 * 作者：lyw on 2016/11/1 10:14
 */

public interface BKKController {
    /**
     * 扫描
     */
    void Scan();

    /**
     * 停止扫描
     */
     void StopScan();

    /**
     * 设置模式
     * @param mode
     */
    void setMode(String mode);

    /**
     * 读取模式
     */
   void readMode();

    /**
     * 蓝牙是否连接
     * @return
     */
    boolean isConnect();

    /**
     * 设置连接的硬件名称
     * @param DeviceName
     */
    void ConnectDeviceName(String[] DeviceName);

}
