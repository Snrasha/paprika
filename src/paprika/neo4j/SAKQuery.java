package paprika.neo4j;

import net.sourceforge.jFuzzyLogic.FunctionBlock;
import java.util.ArrayList;
import org.neo4j.cypher.CypherException;
import net.sourceforge.jFuzzyLogic.FIS;
import java.io.File;
import java.util.HashMap;
import java.io.IOException;
import codesmells.annotations.LM;
import java.util.List;
import java.util.Map;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

public class SAKQuery extends FuzzyQuery {
    protected static double high = 8.5;

    protected static double veryHigh = 13;

    private SAKQuery(QueryEngine queryEngine) {
        super(queryEngine);
        fclFile = "/SwissArmyKnife.fcl";
    }

    public static SAKQuery createSAKQuery(QueryEngine queryEngine) {
        return new SAKQuery(queryEngine);
    }

    public void execute(boolean details) throws IOException, CypherException {
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = ("MATCH (cl:Class) WHERE HAS(cl.is_interface) AND cl.number_of_methods > " + (SAKQuery.veryHigh)) + " RETURN cl.app_key as app_key";
            if (details) {
                query += ",cl.name as full_name";
            }else {
                query += ",count(cl) as SAK";
            }
            result = graphDatabaseService.execute(query);
            queryEngine.resultToCSV(result, "_SAK_NO_FUZZY.csv");
        }
    }

    @LM
    public void executeFuzzy(boolean details) throws IOException, CypherException {
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = ("MATCH (cl:Class) WHERE HAS(cl.is_interface) AND cl.number_of_methods > " + (SAKQuery.high)) + " RETURN cl.app_key as app_key,cl.number_of_methods as number_of_methods";
            if (details) {
                query += ",cl.name as full_name";
            }
            result = graphDatabaseService.execute(query);
            List<String> columns = new ArrayList<>(result.columns());
            columns.add("fuzzy_value");
            int cc;
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
                cc = ((int) (res.get("number_of_methods")));
                if (cc >= (SAKQuery.veryHigh)) {
                    res.put("fuzzy_value", 1);
                }else {
                    fb.setVariable("number_of_methods", cc);
                    fb.evaluate();
                    res.put("fuzzy_value", fb.getVariable("res").getValue());
                }
                fuzzyResult.add(res);
            } 
            queryEngine.resultToCSV(fuzzyResult, columns, "_SAK.csv");
        }
    }
}

