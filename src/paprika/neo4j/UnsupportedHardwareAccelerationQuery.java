package paprika.neo4j;

import org.neo4j.cypher.CypherException;
import java.io.IOException;
import codesmells.annotations.LM;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

public class UnsupportedHardwareAccelerationQuery extends Query {
    private UnsupportedHardwareAccelerationQuery(QueryEngine queryEngine) {
        super(queryEngine);
    }

    public static UnsupportedHardwareAccelerationQuery createUnsupportedHardwareAccelerationQuery(QueryEngine queryEngine) {
        return new UnsupportedHardwareAccelerationQuery(queryEngine);
    }

    @Override
    @LM
    public void execute(boolean details) throws IOException, CypherException {
        Result result;
        String[] uhas = new String[]{ "drawPicture#android.graphics.Canvas" , "drawVertices#android.graphics.Canvas" , "drawPosText#android.graphics.Canvas" , "drawTextOnPath#android.graphics.Canvas" , "drawPath#android.graphics.Canvas" , "setLinearText#android.graphics.Paint" , "setMaskFilter#android.graphics.Paint" , "setPathEffect#android.graphics.Paint" , "setRasterizer#android.graphics.Paint" , "setSubpixelText#android.graphics.Paint" };
        String query = ("MATCH (m:Method)-[:CALLS]->(e:ExternalMethod) WHERE e.full_name='" + (uhas[0])) + "'";
        for (int i = 1; i < (uhas.length); i++) {
            query += (" OR e.full_name='" + (uhas[i])) + "' ";
        }
        query += "return m.app_key";
        if (details) {
            query += ",m.full_name as full_name";
        }else {
            query += ",count(m) as UHA";
        }
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            result = graphDatabaseService.execute(query);
            queryEngine.resultToCSV(result, "_UHA.csv");
        }
    }
}

