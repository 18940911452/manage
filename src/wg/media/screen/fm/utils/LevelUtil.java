package wg.media.screen.fm.utils;

import java.util.WeakHashMap;

//用户level存储工具类
public class LevelUtil {

    private static WeakHashMap<Integer,Integer> map = new WeakHashMap<>();

    public static void setLevel(Integer userId,Integer level){
        map.put(userId,level);
    }

    public static Integer getLevel(Integer userId){
        return map.get(userId);
    }

    public static void removeLevel(Integer userId){map.remove(userId);}

    private LevelUtil(){}
}
