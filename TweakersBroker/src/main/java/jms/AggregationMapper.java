package jms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.jms.TextMessage;

/**
 * Singleton class for keeping track of scattered ActiveMQ messages
 * @author Robin
 */
public class AggregationMapper {
    private static AggregationMapper aggregationMapper = null;
    private static Map<String, Set<TextMessage>> aggregations;
    
    private AggregationMapper() {
        aggregations = new HashMap();
    }
    
    public static AggregationMapper getInstance() {
        if (aggregationMapper == null) {
            aggregationMapper = new AggregationMapper();
        }
        return aggregationMapper;
    }
    
    public Map<String, Set<TextMessage>> getAggregationMap() {
        return aggregations;
    }
    
    /**
     * 
     * @param aggregationId The aggregationId for one client's request
     * @param response The response to add to the given aggregationId
     * @return Whether the added response is the first for this aggregationId
     */
    public boolean addNewResponse(String aggregationId, TextMessage response) {
        boolean isFirstResponse = false;
        if (!aggregations.containsKey(aggregationId)) {
            isFirstResponse = true;
            aggregations.put(aggregationId, new HashSet());
        }
        aggregations.get(aggregationId).add(response);
        return isFirstResponse;
    }
    
    public Set<TextMessage> getAggregation(String aggregationId) {
        return aggregations.get(aggregationId);
    }
}
