package paprika.neo4j;

import org.neo4j.cypher.CypherException;
import java.io.IOException;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

public class InvalidateWithoutRectQuery extends Query {
    private InvalidateWithoutRectQuery(QueryEngine queryEngine) {
        super(queryEngine);
    }

    public static InvalidateWithoutRectQuery createInvalidateWithoutRectQuery(QueryEngine queryEngine) {
        return new InvalidateWithoutRectQuery(queryEngine);
    }

    @Override
    public void execute(boolean details) throws IOException, CypherException {
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = "MATCH (:Class{parent_name:'android.view.View'})-[:CLASS_OWNS_METHOD]->(n:Method{name:'onDraw'})-[:CALLS]->(e:ExternalMethod{name:'invalidate'}) WHERE NOT e-[:METHOD_OWNS_ARGUMENT]->(:ExternalArgument) return n.app_key";
            if (details) {
                query += ",n.full_name as full_name";
            }else {
                query += ",count(n) as IWR";
            }
            result = graphDatabaseService.execute(query);
            queryEngine.resultToCSV(result, "_IWR.csv");
        }
    }
}

