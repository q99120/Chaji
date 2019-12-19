package com.mei.chaji.instruction;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.mei.chaji.core.bean.msg.InsMessage;
import com.mei.chaji.utils.ByteUtil;
import com.mei.chaji.utils.HexUtils;
import com.mei.chaji.utils.LogsFileUtil;
import com.vondear.rxtool.view.RxToast;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

/**
 * 实现了所有和下位机通信的函数
 *
 * @author Administrator
 */
public class MainCommunicate {
    //帧头1字节和2字节
    private static final byte FRAME_HEAD1 = 0x55;
    private static final byte FRAME_HEAD2 = (byte) 0xAA;
    private static byte SERIAL_NUMBER = 0;

    //指令,不固定
    private static final byte INSTRUCTIONS4 = 0x00;

    //参数
    //大参数，不固定
    private static final byte PARAMETER5 = (byte) 0x00;
    private static final byte PARAMETER6 = (byte) 0x00;
    private static final byte PARAMETER7 = (byte) 0x00;
    //小参数.不固定
    private static final byte PARAMETER8 = (byte) 0x00;
    //帧尾9-12
    private static final byte FRAME_FOOT9 = (byte) 0xCC;
    private static final byte FRAME_FOOT10 = (byte) 0x33;
    private static final byte FRAME_FOOT11 = (byte) 0xC3;
    private static final byte FRAME_FOOT12 = 0x3C;

    //串口属性
    private final String TAG = "MainCommunicate";
    private String path = "/dev/ttyS4";
    private int baudrate = 115200;
    public static boolean serialPortStatus = false; //是否打开串口标志
    public String data_;
    public boolean threadStatus; //线程状态，为了安全终止线程

    int SERIAL_NUMBER_FLAG = 0;

    public SerialPort serialPort = null;
    public InputStream inputStream = null;
    public OutputStream outputStream = null;

    TextView textView1, textView2;

    private static MainCommunicate instance = null;

    public static MainCommunicate getInstance() {

        if (instance == null) {
            instance = new MainCommunicate();


        }
        return instance;
    }


    /**
     * 打开串口
     *
     * @return serialPort串口对象
     */
    public boolean openSerialPort() {
        try {
            serialPort = new SerialPort(new File(path), baudrate, 0, 8, 1);
            serialPortStatus = true;
            threadStatus = false; //线程状态
            //定义一个读取串口信息的线程，用于获取串口发送给android的信息
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            new Thread(new ReadThread()).start();
        } catch (IOException e) {
            serialPortStatus = false;
            Log.e(TAG, "SerialPort: 打开串口异常：" + e.toString());
            RxToast.normal("串口打开异常");
            return false;
        }
//        RxToast.normal("串口打开成功");
        Log.e(TAG, "SerialPort: 打开串口");
        return true;
    }


    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        try {
            inputStream.close();
            outputStream.close();

            serialPortStatus = false;
            this.threadStatus = true; //线程状态
            //终止线程
            ReadThread.interrupted();
            serialPort.close();
        } catch (IOException e) {
            Log.e(TAG, "SerialPort: 关闭串口异常：" + e.toString());
            return;
        }
        Log.e(TAG, "SerialPort: 关闭串口成功");
    }


    /**
     * 发送串口指令（字符串）
     * <p>
     * 改变工作状态指令,正常售卖模式
     *
     * @ins change_work_ins
     * @param1 sell_param1
     * @param2 change_sell_parm2
     */
    public void change_normal() {
        byte ins = CmcConstant.change_work_ins;
        byte param1 = CmcConstant.sell_parm1;
        byte param2 = CmcConstant.normal_sell_parm2;
        sendFrame(ins, param1, param2);
        RxToast.normal("进入正常模式");
    }

    /**
     * 发送串口指令（字符串）
     * <p>
     * 只售杯模式，此模式下下位机只接收卖杯指令
     *
     * @ins change_work_ins
     * @param1 sell_param1
     * @param2 change_sell_parm2
     */
    public void change_only() {
        byte ins = CmcConstant.change_work_ins;
        byte param1 = CmcConstant.sell_parm1;
        byte param2 = CmcConstant.change_only_parm2;
        sendFrame(ins, param1, param2);
    }

    /**
     * 发送串口指令（字符串）
     * <p>
     * 待机模式
     *
     * @ins change_work_ins
     * @param1 sell_param1
     * @param2 change_sell_parm2
     */
    public void change_standby() {
        byte ins = CmcConstant.change_work_ins;
        byte param1 = CmcConstant.sell_parm1;
        byte param2 = CmcConstant.change_standby_parm2;
        sendFrame(ins, param1, param2);
    }

    /**
     * 发送串口指令（字符串）
     * <p>
     * 清洁模式
     *
     * @ins change_work_ins
     * @param1 sell_param1
     * @param2 change_sell_parm2
     */
    public void change_clear() {
        byte ins = CmcConstant.change_work_ins;
        byte param1 = CmcConstant.sell_parm1;
        byte param2 = CmcConstant.change_clear_parm2;
        sendFrame(ins, param1, param2);
    }

    /**
     * 发送串口指令（字符串）
     * <p>
     * 加杯模式
     *
     * @ins change_work_ins
     * @param1 sell_param1
     * @param2 change_sell_parm2
     */
    public void change_add() {
        byte ins = CmcConstant.change_work_ins;
        byte param1 = CmcConstant.sell_parm1;
        byte param2 = CmcConstant.change_add_parm2;
        sendFrame(ins, param1, param2);
        RxToast.normal("进入加杯模式");
    }

    /**
     * 工作模式
     * <p>
     * 一货道（二货道）只卖杯不加水
     *
     * @ins change_work_ins
     * @param1 normal_cargo1_parm1, normal_cargo2_parm1 一货道和二货道
     * @param2 _parm2
     */
    public void normal_onlycup(int crago_type) {
        byte ins = CmcConstant.normal_work_ins;
        byte param1 = 0;
        if (crago_type == 1) {
            param1 = CmcConstant.normal_cargo1_parm1;
        } else if (crago_type == 2) {
            param1 = CmcConstant.normal_cargo2_parm1;
        }
        byte param2 = CmcConstant.normal_onlycup_parm2;
        sendFrame(ins, param1, param2);
    }

    public void normal_water(int crago_type) {
        LogsFileUtil.getInstance().addLog("制茶指令",crago_type+"货道发了一个出茶指令");
        byte ins = CmcConstant.normal_work_ins;
        byte param1 = 0;
        byte param2 = 0;
        if (crago_type == 1) {
            param1 = CmcConstant.normal_cargo1_parm1;
        } else if (crago_type == 2) {
            param1 = CmcConstant.normal_cargo2_parm1;
        }
//        if (water_type == 1) {
        param2 = CmcConstant.normal_hot_parm2;
//        } else if (water_type == 2) {
//            param2 = CmcConstant.normal_warm_parm2;
//        } else if (water_type == 3) {
//            param2 = CmcConstant.normal_cold_parm2;
//        }
        Log.e(TAG, "normal_hot: " + "货道" + param1 + "热水" + param2);
        sendFrame(ins, param1, param2);
    }

    public void test_water(int crago_type, int water_type) {
        byte ins = CmcConstant.normal_work_ins;
        byte param1 = 0;
        byte param2 = 0;
        if (crago_type == 1) {
            param1 = CmcConstant.normal_cargo1_parm1;
        } else if (crago_type == 2) {
            param1 = CmcConstant.normal_cargo2_parm1;
        }
        if (water_type == 1) {
        param2 = CmcConstant.normal_hot_parm2;
        } else if (water_type == 2) {
            param2 = CmcConstant.normal_warm_parm2;
        } else if (water_type == 3) {
            param2 = CmcConstant.normal_cold_parm2;
        }
        Log.e(TAG, "normal_hot: " + "货道" + param1 + "热水" + param2);
        sendFrame(ins, param1, param2);
    }


    /**
     * 工作模式
     * <p>
     * 一货道（二货道）出杯加热水
     *
     * @ins change_work_ins
     * @param1 normal_cargo1_parm1, normal_cargo2_parm1 一货道和二货道
     * @param2 _parm2
     */
    public void normal_hot(int crago_type) {
        byte ins = CmcConstant.normal_work_ins;
        byte param1 = 0;
        if (crago_type == 1) {
            param1 = CmcConstant.normal_cargo1_parm1;
        } else if (crago_type == 2) {
            param1 = CmcConstant.normal_cargo2_parm1;
        }
        byte param2 = CmcConstant.normal_hot_parm2;
        Log.e(TAG, "normal_hot: " + "货道" + param1 + "热水" + param2);
        sendFrame(ins, param1, param2);
    }

    /**
     * 工作模式
     * <p>
     * 一货道（二货道）出杯加冷水
     *
     * @ins change_work_ins
     * @param1 normal_cargo1_parm1, normal_cargo2_parm1 一货道和二货道
     * @param2 _parm2
     */
    public void normal_cold(int crago_type) {
        byte ins = CmcConstant.normal_work_ins;
        byte param1 = 0;
        if (crago_type == 1) {
            param1 = CmcConstant.normal_cargo1_parm1;
        } else if (crago_type == 2) {
            param1 = CmcConstant.normal_cargo2_parm1;
        }
        byte param2 = CmcConstant.normal_cold_parm2;
        sendFrame(ins, param1, param2);
    }


    /**
     * 工作模式
     * <p>
     * 续水 续水操作对参数 Byte5 不敏感（不论几货道）
     *
     * @ins change_work_ins
     * @param1 normal_cargo1_parm1, normal_cargo2_parm1 一货道和二货道
     * @param2 _parm2
     */
    public void normal_refill() {
        byte ins = CmcConstant.normal_work_ins;
        byte param1 = CmcConstant.normal_cargo1_parm1;
        byte param2 = CmcConstant.refill_hot_parm2;
        sendFrame(ins, param1, param2);
    }


    /**
     * 工作模式
     * <p>
     * 续热水 续水操作对参数 Byte5 不敏感（不论几货道）
     *
     * @ins change_work_ins
     * @param1 normal_cargo1_parm1, normal_cargo2_parm1 一货道和二货道
     * @param2 _parm2
     */
    public void refill_hot(int crago_type) {
        byte ins = CmcConstant.normal_work_ins;
        byte param1 = 0;
        if (crago_type == 1) {
            param1 = CmcConstant.normal_cargo1_parm1;
        } else if (crago_type == 2) {
            param1 = CmcConstant.normal_cargo2_parm1;
        }
        byte param2 = CmcConstant.refill_hot_parm2;
        sendFrame(ins, param1, param2);
    }

    /**
     * 工作模式
     * <p>
     * 续冷水 续水操作对参数 Byte5 不敏感（不论几货道）
     *
     * @ins change_work_ins
     * @param1 normal_cargo1_parm1, normal_cargo2_parm1 一货道和二货道
     * @param2 _parm2
     */
    public void refill_cold(int crago_type) {
        byte ins = CmcConstant.normal_work_ins;
        byte param1 = 0;
        if (crago_type == 1) {
            param1 = CmcConstant.normal_cargo1_parm1;
        } else if (crago_type == 2) {
            param1 = CmcConstant.normal_cargo2_parm1;
        }
        byte param2 = CmcConstant.refill_cold_parm2;
        sendFrame(ins, param1, param2);
    }

    /**
     * 工作模式
     * <p>
     * 一货道（二货道）出杯加温水
     *
     * @ins change_work_ins
     * @param1 normal_cargo1_parm1, normal_cargo2_parm1 一货道和二货道
     * @param2 _parm2
     */
    public void normal_warm(int crago_type) {
        byte ins = CmcConstant.normal_work_ins;
        byte param1 = 0;
        if (crago_type == 1) {
            param1 = CmcConstant.normal_cargo1_parm1;
        } else if (crago_type == 2) {
            param1 = CmcConstant.normal_cargo2_parm1;
        }
        byte param2 = CmcConstant.normal_warm_parm2;
        sendFrame(ins, param1, param2);
    }

    /**
     * 工作模式
     * <p>
     * 续温水  注：续水操作对参数 Byte5 不敏感（不论几货道）
     *
     * @ins change_work_ins
     * @param1 normal_cargo1_parm1, normal_cargo2_parm1 一货道和二货道
     * @param2 _parm2
     */
    public void refill_warm(int crago_type) {
        byte ins = CmcConstant.normal_work_ins;
        byte param1 = 0;
        if (crago_type == 1) {
            param1 = CmcConstant.normal_cargo1_parm1;
        } else if (crago_type == 2) {
            param1 = CmcConstant.normal_cargo2_parm1;
        }
        byte param2 = CmcConstant.refill_warm_parm2;
        sendFrame(ins, param1, param2);
    }


    /**
     * 系统复位
     * 上位机通过此指令复位下位机
     *
     * @ins system_reset_ins
     * @param1 system_reset_param1
     * @param2 system_reset_param2
     */
    public void system_reset() {
        byte ins = CmcConstant.system_reset_ins;
        byte param1 = CmcConstant.system_reset_param1;
        byte param2 = CmcConstant.system_reset_param2;
        sendFrame(ins, param1, param2);
    }


    /**
     * 更改丢杯等待时间
     * 范围为 10~125（0x0a~0x7D）
     *
     * @ins lost_cup_ins
     * @param1 lost_cup_param1
     * @param2 lost_cup_param2
     */
    public void lost_cup(int time) {
        byte ins = CmcConstant.lost_cup_ins;
        byte param1 = CmcConstant.lost_cup_param1;
        //十进制转16进制，16进制转byte
        byte param2 = (byte) Integer.parseInt(Integer.toHexString(time), 16);
        sendFrame(ins, param1, param2);
    }


    /**
     * 修复指令
     *
     * @ins lost_cup_ins
     * 只开(关)货道1的爪
     * @param1
     * @param2
     */
    public void repair_hand1(int type) {
        byte ins = CmcConstant.repair_ins;
        byte param1 = CmcConstant.repair_gar_param1;
        byte param2 = 0;
        if (type == 1) {
            //开
            param2 = CmcConstant.repair_open_param1;
        } else {
            //关
            param2 = CmcConstant.repair_close_param1;
        }
        sendFrame(ins, param1, param2);
    }

    /**
     * 修复指令
     *
     * @ins lost_cup_ins
     * 只开(关)货道2的爪
     * @param1
     * @param2
     */
    public void repair_hand2(int type) {
        byte ins = CmcConstant.repair_ins;
        byte param1 = CmcConstant.repair_gar_param2;
        byte param2 = 0;
        if (type == 1) {
            //开
            param2 = CmcConstant.repair_open_param2;
        } else {
            //关
            param2 = CmcConstant.repair_close_param2;
        }
        sendFrame(ins, param1, param2);
    }


    /**
     * 握手指令
     *
     * @ins handshake_ins
     * @param1 handshake_param1
     * @param2 handshake_param2
     * 下位机收到一条握手指令后向上位机连续回复 3 次握手指令
     */
    public void handshake() {
        byte ins = CmcConstant.handshake_ins;
        byte param1 = CmcConstant.handshake_param1;
        byte param2 = CmcConstant.handshake_param2;
        sendFrame(ins, param1, param2);
    }

    /**
     * 进水模式修改
     *
     * @ins inlet_mode_ins
     * @param1 barreled_water_param1--桶装水 0
     * @param1 drinking_water_param1--直饮水 1
     * @param2 inlet_mode_param2
     */
    public void inlet_mode(int water_type) {
        byte ins = CmcConstant.inlet_mode_ins;
        byte param1 = 0;
        if (water_type == 0) {
            param1 = CmcConstant.barreled_water_param1;
        } else if (water_type == 1) {
            param1 = CmcConstant.drinking_water_param1;
        }
        byte param2 = CmcConstant.inlet_mode_param2;
        sendFrame(ins, param1, param2);
    }


    /**
     * 单开一个线程来读取串口返回数据
     */
    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (serialPortStatus) {
                try {
                    if (inputStream == null) {
                        return;
                    }
                    Thread.sleep(1000);
                    byte[] buffer = new byte[1024];
                    int size = inputStream.read(buffer, 0, buffer.length);
//                    Log.e(TAG, "个数"+size );
                    if (size >0) {
                        onDataReceived(buffer, size);
                    }else {
                        //如果没有数据超过5秒就关闭串口和流并重新打开
                        if (serialPortStatus) {
                            serialPortStatus = false;
                            handler.sendEmptyMessageDelayed(1, 5000);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            while (serialPortStatus) {
//                try {
//                    byte[] buffer = new byte[1024];
//                    if (inputStream == null) {
//                        return;
//                    }
//                    Thread.sleep(1000);
//                    int size = inputStream.read(buffer,0,buffer.length);
//                    Log.e(TAG, "串口数量: "+size );
//                    if (size > 0 && serialPortStatus) {
//                        Log.e(TAG, "run: " + ByteUtil.byteToStr(buffer, 12));
//                    }
////                        if (inputStream.available() > 0) {
////                            //当接收到数据时，sleep 500毫秒（sleep时间自己把握）
////                            Thread.sleep(1000);
////                            if (size > 0) {
////                                onDataReceived(buffer, size);
////                            }
////                        }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        public void handleMessage(Message  message){
            switch (message.what){
                case 1:
                    closeSerialPort();
                    //3秒后再打开线程
                    handler.sendEmptyMessageDelayed(2,3000);
                    break;
                case 2:
                    openSerialPort();
                    break;
            }
        }
    };

    /**
     * 得到最后的指令
     *
     * @param buffer
     * @param size
     */
    private void onDataReceived(byte[] buffer, int size) {
//        String dd = HexUtils.bytes2HexStrings(buffer);
        String dd = ByteUtil.byteToStr(buffer, size);

        if (dd.substring(0, 2).equals("55")) {
            String err_code = dd;
            String warning1 = dd.substring(10, 12);
            String warning2 = dd.substring(12, 14);
            String warning3 = dd.substring(14, 16);
            String bin_warn1 = ByteUtil.toBinarStr(warning1);
            String bin_warn2 = ByteUtil.toBinarStr(warning2);
            String bin_warn3 = ByteUtil.toBinarStr(warning3);
            EventBus.getDefault().post(new InsMessage("ins_success", bin_warn1, bin_warn2, bin_warn3, err_code));
        }

    }


    private static String asciiToHex(String asciiStr) {
        char[] chars = asciiStr.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char ch : chars) {
            hex.append(Integer.toHexString((int) ch));
        }
        return hex.toString();
    }


    public String convertStringToHex(String str) {

        char[] chars = str.toCharArray();

        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }

        return hex.toString();
    }


    /**
     * 发送整帧数据
     */
    private void sendFrame(byte ins, byte param1, byte param2) {
//        outputStream = serialPort.getOutputStream();
        byte[] buf = new byte[12];
        buf[0] = FRAME_HEAD1;
        buf[1] = FRAME_HEAD2;
        if (SERIAL_NUMBER == 254) {
            buf[2] = 0;
        } else {
            buf[2] = SERIAL_NUMBER++;
        }
        buf[3] = ins;
        buf[4] = param1;
        buf[5] = 0;
        buf[6] = 0;
        buf[7] = param2;
        buf[8] = FRAME_FOOT9;
        buf[9] = FRAME_FOOT10;
        buf[10] = FRAME_FOOT11;
        buf[11] = FRAME_FOOT12;
        String hex = HexUtils.bytes2HexString(buf);
        Log.e(TAG, "sendFrame: " + hex);
        try {
            outputStream.write(buf);
            //刷新
            outputStream.flush();
//            new ReadThread().start(); //开始线程监控是否有数据要接收
//            RxToast.normal("sendSerialPort: 数据成功");
        } catch (IOException e) {
            Log.e(TAG, "sendSerialPort: 串口数据发送失败：" + e.toString());
        }

    }
}
