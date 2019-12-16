package com.mei.chaji.instruction;

/**
 * 茶机参数
 * 上位机发给下位机的通信指令
 */

public class CmcConstant {
    /**
     * 改变工作状态模式指令
     *
     * @instructions 指令
     * @param1 参数
     * @param2 参数2
     */

    public static final int change_work_ins = 0x01;
    //前置参数
    public static final int sell_parm1 = 0x00;
    //后置参数正常售卖模式
    public static final int normal_sell_parm2 = 0x01;
    //只售杯模式，此模式下下位机只接收卖杯指令
    public static final int change_only_parm2 = 0x02;
    //待机模式，机器停止加热，关闭指示灯
    public static final int change_standby_parm2 = 0x03;
    //清洁模式，机器打开出杯隔离门
    public static final int change_clear_parm2 = 0x04;
    //加杯模式，打开机器顶部电子锁
    public static final int change_add_parm2 = 0x05;


    /**
     * 工作指令
     *
     * @instructions 指令
     * @param1 参数
     * @param2 参数2
     */
    public static final int normal_work_ins = 0x02;
    //前置参数0x01
    //（0x02）注：0x01表示一货道，0x02表示二货道
    public static final int normal_cargo1_parm1 = 0x01;
    public static final int normal_cargo2_parm1 = 0x02;
    //后置参数一货道（二货道）只卖杯不加水
    public static final int normal_onlycup_parm2 = 0x01;
    //一货道（二货道）出杯加热水
    public static final int normal_hot_parm2 = 0x02;
    //一货道（二货道）出杯加冷水
    public static final int normal_cold_parm2 = 0x03;
    //续热水  注：续水操作对参数 Byte5 不敏感（不论几货道）
    public static final int refill_hot_parm2 = 0x04;
    //续冷水  注：续水操作对参数 Byte5 不敏感（不论几货道）
    public static final int refill_cold_parm2 = 0x05;
    //一货道（二货道）出杯加温水
    public static final int normal_warm_parm2 = 0x06;
    //续温水  注：续水操作对参数 Byte5 不敏感（不论几货道）
    public static final int refill_warm_parm2 = 0x07;


    /**
     * 系统复位指令
     *
     * @instructions 指令
     * @param1 参数
     * @param2 参数2
     * 上位机通过此指令复位下位机
     */
    public static final int system_reset_ins = 0x04;
    public static final int system_reset_param1 = 0x04;
    public static final int system_reset_param2 = 0x04;

    /**
     * 更改丢杯等待时间
     *
     * @instructions 指令
     * @param1 参数
     * @param2 参数2
     * 0xXX 为等待时间，范围为 10~125（0x0a~0x7D）,单位为 S；
     */
    public static final int lost_cup_ins = 0x05;
    public static final int lost_cup_param1 = 0;
    //0xXX
    public static final int lost_cup_param2 = 0x0a;


    /**
     * 修复指令
     *
     * @instructions 指令
     * @param1 参数
     * @param2 参数2
     */
    public static final int repair_ins = 0x06;
    //一货道（二货道）双爪
    public static final int repair_gar_param1 = 0x01;
    public static final int repair_gar_param2 = 0x02;
    //开一货道爪
    public static final int repair_open_param1 = 0x01;
    //关一货道爪
    public static final int repair_close_param1 = 0x02;
    //开二货道爪
    public static final int repair_open_param2 = 0x01;
    //关二货道爪
    public static final int repair_close_param2 = 0x02;


    /**
     * 握手指令
     *
     * @instructions 指令
     * @param1 参数
     * @param2 参数2
     * 下位机收到一条握手指令后向上位机连续回复 3 次握手指令
     */
    public static final int handshake_ins = 0x07;
    public static final int handshake_param1 = 0;
    public static final int handshake_param2 = 0;

    /**
     * 进水模式修改
     *
     * @instructions 指令
     * @param1 参数
     * @param2 参数2
     */

    public static final int inlet_mode_ins = 0x08;
    //桶装水0x01
    public static final int barreled_water_param1 = 0x01;
    //直饮水0x02
    public static final int drinking_water_param1 = 0x02;
    public static final int inlet_mode_param2 = 0;


}
