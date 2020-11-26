import java.util.ArrayList;
import java.util.List;

public class T2 {

    public int minimumTotal(List<List<Integer>> triangle) {
        if(triangle==null || triangle.size() == 0){
            return -1;
        }

        int[] dp = new int[triangle.size()+1];
        dp[0] = 0;
        dp[1] = triangle.get(0).get(0);
        int j = 0;
        for (int i = 1; i < triangle.size(); i++) {
            int j1=Integer.MAX_VALUE, j2=Integer.MAX_VALUE, j3 = Integer.MAX_VALUE;

            if(j-1 >= 0){
                j1 = triangle.get(i).get(j-1);
            }

            j2 = triangle.get(i).get(j);

            if(j+1<triangle.get(i).size()){
                j3 = triangle.get(i).get(j+1);
            }

            dp[i] = dp[i-1] + Math.min(Math.min(j1, j2), j3);

        }
        return dp[triangle.size()];
    }

    public static void main(String[] args) {

    }
}
