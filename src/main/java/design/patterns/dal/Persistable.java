package design.patterns.dal;

import java.util.Map;

public interface Persistable {
	
    String tableName();

    String idColumn();
    
    Map<String, String> columns();

}
