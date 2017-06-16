package paprika.neo4j;

import org.neo4j.cypher.CypherException;
import java.io.IOException;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

public class TrackingHardwareIdQuery extends Query {
    private TrackingHardwareIdQuery(QueryEngine queryEngine) {
        super(queryEngine);
    }

    public static TrackingHardwareIdQuery createTrackingHardwareIdQuery(QueryEngine queryEngine) {
        return new TrackingHardwareIdQuery(queryEngine);
    }

    @Override
    public void execute(boolean details) throws IOException, CypherException {
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = "MATCH (m1:Method)-[:CALLS]->(:ExternalMethod { full_name:'getDeviceId#android.telephony.TelephonyManager'}) RETURN m1.app_key as app_key";
            if (details) {
                query += ",m1.full_name as full_name";
            }else {
                query += ",count(m1) as THI";
            }
            Result result = graphDatabaseService.execute(query);
            queryEngine.resultToCSV(result, "_THI.csv");
        }
    }
}

