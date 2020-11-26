import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class test {
    static public String minWindow(String s, String t) {
        if(s.length() <  t.length()){
            return "";
        }
        Map<Character, Integer> mapDate = new HashMap<>();
        Map<Character, Integer> map = new HashMap<>();
        for(int i = 0; i < t.length(); i++){
            map.put(t.charAt(i), 0);
            mapDate.put(t.charAt(i), mapDate.getOrDefault(t.charAt(i), 0)+1);
        }
        int cnt = 0;
        int start=0, end=0;
        List<String> list = new ArrayList<>();
        boolean isFirst = true;
        String ret = "";
        while(start <= end || end < s.length()){
            if(cnt < mapDate.size() && end < s.length()){
                char ec = s.charAt(end);
                if(map.containsKey(ec)){
                    if(map.get(ec) + 1 == mapDate.get(ec)){
                        map.put(ec, map.getOrDefault(ec, 0)+1);
                        cnt++;
                    }else if(map.get(ec) + 1 > mapDate.get(ec) || map.get(ec) + 1 < mapDate.get(ec)){
                        map.put(ec, map.getOrDefault(ec, 0)+1);
                    }
                }
                end++;
            }else if(cnt == mapDate.size()){
//                list.add(s.substring(start, end));
                if(isFirst){
                    ret = s.substring(start, end);
                    isFirst = false;
                }else {
                    String str = s.substring(start, end);
                    if(str.length() < ret.length()){
                        ret = str;
                    }
                }

                while(cnt == mapDate.size()){
                    char sc = s.charAt(start);
                    start++;
                    if(map.containsKey(sc)){
                        int tmp = map.get(sc);
                        if(tmp-1 < mapDate.get(sc)){
                            map.put(sc, tmp-1);
                            cnt--;
                        }else if(tmp-1 >= mapDate.get(sc)){
                            map.put(sc, tmp-1);
//                            list.add(s.substring(start, end));
                            if(isFirst){
                                ret = s.substring(start, end);
                                isFirst = false;
                            }else {
                                String str = s.substring(start, end);
                                if(str.length() < ret.length()){
                                    ret = str;
                                }
                            }
                        }
                    }else{
//                        list.add(s.substring(start, end));
                        if(isFirst){
                            ret = s.substring(start, end);
                            isFirst = false;
                        }else {
                            String str = s.substring(start, end);
                            if(str.length() < ret.length()){
                                ret = str;
                            }
                        }
                    }
                }
                if(end == s.length()){
                    break;
                }
            }else {
                break;
            }
        }
        return ret;
    }

    static public String makeGood(String s) {
        if(s.length()==0 || s.length()==1)return s;
        char[] cs = s.toCharArray();
        int cons = 'A' - 'a';
        for(int i = 0; i < s.length() - 1;i++){
            if(cs[i]+cons == cs[i+1]||cs[i] - cons == cs[i+1]){
                cs[i]='\0';
                cs[i+1]='\0';
            }
        }
        return new String(cs);
    }
    public static void main(String[] args) {
        System.out.println(minWindow("cabwefgewcwaefgcf", "cae"));
        String s = "leEeetcode";
        char[] cs = s.toCharArray();
        StringBuilder sb = new StringBuilder(s);
        List l = null;
        l.stream().distinct().collect(Collectors.toList()) ;
        cs[2]='\0';

        cs[3]='\0';
        System.out.println(makeGood(s));
    }
}
