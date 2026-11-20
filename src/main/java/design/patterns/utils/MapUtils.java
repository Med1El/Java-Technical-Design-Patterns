package design.patterns.utils;

import java.util.Map;
import java.util.Objects;

public class MapUtils {
	
	public static <K, V> K getKeyByValue(Map<K, V> map, V targetValue) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (Objects.equals(targetValue, entry.getValue())) {
                return entry.getKey(); // Returns the first match
            }
        }
		throw new IllegalArgumentException("Value not found in map: " + targetValue);
	}

}
