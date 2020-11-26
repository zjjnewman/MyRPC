import java.util.*;


public class T3 {
    /**
     * 代码中的类名、方法名、参数名已经指定，请勿修改，直接返回方法规定的值即可
     *
     * @param cars int整型一维数组 车辆数量
     * @param num int整型 出游总人数
     * @return int整型
     */
    static public int getPayCount (int n) {
        // write code here
        int[] nums = new int[]{1, 2, 5, 10};
        int[]dp = new int[n+1];
        dp[0] = 1;
        for (int j = 0; j < nums.length; j++) {
            for (int i = 1; i <= n; i++) {
                if(i >= nums[j]){
                    dp[i] += dp[i-nums[j]];
                }
            }
        }
        return dp[n];
    }

    public static void main(String[] args) {
        System.out.println(getPayCount(3));
    }
}