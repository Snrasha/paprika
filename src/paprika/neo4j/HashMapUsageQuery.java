package paprika.neo4j;

import org.neo4j.cypher.CypherException;
import java.io.IOException;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

public class HashMapUsageQuery extends Query {
    private HashMapUsageQuery(QueryEngine queryEngine) {
        super(queryEngine);
    }

    public static HashMapUsageQuery createHashMapUsageQuery(QueryEngine queryEngine) {
        return new HashMapUsageQuery(queryEngine);
    }

    @Override
    public void execute(boolean details) throws IOException, CypherException {
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = "MATCH (m:Method)-[:CALLS]->(e:ExternalMethod{full_name:'<init>#java.util.HashMap'}) return m.app_key";
            if (details) {
                query += ",m.full_name as full_name";
            }else {
                query += ", count(m) as HMU";
            }
            result = graphDatabaseService.execute(query);
            queryEngine.resultToCSV(result, "_HMU.csv");
        }
    }
}

