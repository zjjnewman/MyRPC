package rpc10_nio_threadpool;

import java.util.*;

public class Test {

    public static List<List<Integer>> method(int n){
        Set<List<Integer>> ans = new HashSet<>();
        ArrayList<Integer> combine = new ArrayList<>();
        dfs(n,3, ans, combine);
        return new ArrayList<>(ans);
    }

    // 三角形任意两边之和大于第三边
    public static void dfs(int n, int cnt, Set<List<Integer>> ans, List<Integer> combine){
        if(cnt == 0 && isValid(n,combine)){
            Collections.sort(combine);
            ans.add(new ArrayList<>(combine));
            return;
        }
        if(cnt == 0){
            return;
        }
        for (int i = 1; i < n; i++) {
            combine.add(i);
            dfs(n, cnt - 1, ans, combine);
            combine.remove(combine.size()-1);
        }
    }

    public static boolean isValid( int n ,List<Integer> combine){
        if (combine.size() != 3){
            return false;
        }
        int a = combine.get(0);
        int b = combine.get(1);
        int c = combine.get(2);

        // 三角形规则
        if(a+b+c == n && a+b>c && a+c>b && b+c>a){
            // 直角三角形规则
//            if(a*a+b*b==c*c||a*a==b*b+c*c||a*a+c*c==b*b){
//                return true;
//            }
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(method(12));
    }
}
