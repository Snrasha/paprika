package paprika.neo4j;

import net.sourceforge.jFuzzyLogic.FIS;
import java.util.ArrayList;
import org.neo4j.cypher.CypherException;
import java.io.File;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import java.util.HashMap;
import java.io.IOException;
import codesmells.annotations.LM;
import java.util.List;
import org.neo4j.graphdb.Transaction;
import java.util.Map;
import org.neo4j.graphdb.Result;

public class BLOBQuery extends FuzzyQuery {
    protected static double high_lcom = 25;

    protected static double veryHigh_lcom = 40;

    protected static double high_noa = 8.5;

    protected static double veryHigh_noa = 13;

    protected static double high_nom = 14.5;

    protected static double veryHigh_nom = 22;

    private BLOBQuery(QueryEngine queryEngine) {
        super(queryEngine);
        fclFile = "/Blob.fcl";
    }

    public static BLOBQuery createBLOBQuery(QueryEngine queryEngine) {
        return new BLOBQuery(queryEngine);
    }

    public void execute(boolean details) throws IOException, CypherException {
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = ((((("MATCH (cl:Class) WHERE cl.lack_of_cohesion_in_methods >" + (BLOBQuery.veryHigh_lcom)) + " AND cl.number_of_methods > ") + (BLOBQuery.veryHigh_nom)) + " AND cl.number_of_attributes > ") + (BLOBQuery.veryHigh_noa)) + " RETURN cl.app_key as app_key";
            if (details) {
                query += ",cl.name as full_name";
            }else {
                query += ",count(cl) as BLOB";
            }
            result = graphDatabaseService.execute(query);
            queryEngine.resultToCSV(result, "_BLOB_NO_FUZZY.csv");
        }
    }

    @LM
    public void executeFuzzy(boolean details) throws IOException, CypherException {
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = ((((("MATCH (cl:Class) WHERE cl.lack_of_cohesion_in_methods >" + (BLOBQuery.high_lcom)) + " AND cl.number_of_methods > ") + (BLOBQuery.high_nom)) + " AND cl.number_of_attributes > ") + (BLOBQuery.high_noa)) + " RETURN cl.app_key as app_key,cl.lack_of_cohesion_in_methods as lack_of_cohesion_in_methods,cl.number_of_methods as number_of_methods, cl.number_of_attributes as number_of_attributes";
            if (details) {
                query += ",cl.name as full_name";
            }
            result = graphDatabaseService.execute(query);
            List<String> columns = new ArrayList<>(result.columns());
            columns.add("fuzzy_value");
            int lcom;
            int noa;
            int nom;
            List<Map> fuzzyResult = new ArrayList<>();
            File fcf = new File(fclFile);
            FIS fis;
            if ((fcf.exists()) && (!(fcf.isDirectory()))) {
                fis = FIS.load(fclFile, false);
            }else {
                fis = FIS.load(getClass().getResourceAsStream(fclFile), false);
            }
            FunctionBlock fb = fis.getFunctionBlock(null);
            while (result.hasNext()) {
                HashMap res = new HashMap(result.next());
                lcom = ((int) (res.get("lack_of_cohesion_in_methods")));
                noa = ((int) (res.get("number_of_attributes")));
                nom = ((int) (res.get("number_of_methods")));
                if (((lcom >= (BLOBQuery.veryHigh_lcom)) && (noa >= (BLOBQuery.veryHigh_noa))) && (nom >= (BLOBQuery.veryHigh_nom))) {
                    res.put("fuzzy_value", 1);
                }else {
                    fb.setVariable("lack_of_cohesion_in_methods", lcom);
                    fb.setVariable("number_of_attributes", noa);
                    fb.setVariable("number_of_methods", nom);
                    fb.evaluate();
                    res.put("fuzzy_value", fb.getVariable("res").getValue());
                }
                fuzzyResult.add(res);
            } 
            queryEngine.resultToCSV(fuzzyResult, columns, "_BLOB.csv");
        }
    }
}

