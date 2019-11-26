package wg.openmob.mobileimsdk.server.processor;

import java.util.LinkedHashMap;
import java.util.Map;

public class test {
	
	 static int maxSize = 100;
	 public static void main(String[] args) {
	        Map<String, Integer> map = new LinkedHashMap<String, Integer>(){
	            private static final long    serialVersionUID    = 1L;
	            @Override
	            protected boolean removeEldestEntry(java.util.Map.Entry<String, Integer> pEldest) {
	                return size() > maxSize;
	            }
	        };
	        
	        for (int i = 0; i < 15; i++) {
	            map.put("1111", i);
	            map.put("1111", i);
	            map.put("222", i);
	            map.put("222", i);
	        }
	        System.out.println(map.size());
	        System.out.println(map);
	  }
	

}
