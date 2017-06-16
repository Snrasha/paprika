package paprika.neo4j;

import org.neo4j.cypher.CypherException;
import java.io.IOException;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

public class LICQuery extends Query {
    private LICQuery(QueryEngine queryEngine) {
        super(queryEngine);
    }

    public static LICQuery createLICQuery(QueryEngine queryEngine) {
        return new LICQuery(queryEngine);
    }

    @Override
    public void execute(boolean details) throws IOException, CypherException {
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = "MATCH (cl:Class) WHERE HAS(cl.is_inner_class) AND NOT HAS(cl.is_static) RETURN cl.app_key as app_key";
            if (details) {
                query += ",cl.name as full_name";
            }else {
                query += ",count(cl) as LIC";
            }
            Result result = graphDatabaseService.execute(query);
            queryEngine.resultToCSV(result, "_LIC.csv");
        }
    }
}

