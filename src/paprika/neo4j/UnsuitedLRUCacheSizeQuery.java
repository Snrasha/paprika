package paprika.neo4j;

import org.neo4j.cypher.CypherException;
import java.io.IOException;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

public class UnsuitedLRUCacheSizeQuery extends Query {
    private UnsuitedLRUCacheSizeQuery(QueryEngine queryEngine) {
        super(queryEngine);
    }

    public static UnsuitedLRUCacheSizeQuery createUnsuitedLRUCacheSizeQuery(QueryEngine queryEngine) {
        return new UnsuitedLRUCacheSizeQuery(queryEngine);
    }

    @Override
    public void execute(boolean details) throws IOException, CypherException {
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = "Match (m:Method)-[:CALLS]->(e:ExternalMethod {full_name:'<init>#android.util.LruCache'}) WHERE NOT (m)-[:CALLS]->(:ExternalMethod {full_name:'getMemoryClass#android.app.ActivityManager'}) return m.app_key as app_key";
            if (details) {
                query += ",m.full_name as full_name";
            }else {
                query += ",count(m) as UCS";
            }
            Result result = graphDatabaseService.execute(query);
            queryEngine.resultToCSV(result, "_UCS.csv");
        }
    }
}

