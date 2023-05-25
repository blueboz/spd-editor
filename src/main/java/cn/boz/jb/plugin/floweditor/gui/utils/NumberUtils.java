package cn.boz.jb.plugin.floweditor.gui.utils;

public class NumberUtils {
    /**
     * 将字符串左侧补0
     *
     * @param nu  int类型数据
     * @param num 想要补0的位数
     * @return
     */
    public static String binaryFormat(int nu, int num) {
        long number = Long.parseLong(Integer.toBinaryString(nu));

        // 0 代表前面补充0
        // num 数据前补num个0
        // d 代表参数为正数型

        return String.format("%0" + (num + 1) + "d", number);
    }
}

