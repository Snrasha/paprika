package paprika.neo4j;

import java.io.IOException;
import java.util.ArrayList;
import org.neo4j.graphdb.Transaction;
import org.neo4j.cypher.CypherException;
import net.sourceforge.jFuzzyLogic.FIS;
import java.io.File;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import java.util.HashMap;
import codesmells.annotations.LM;
import java.util.List;
import java.util.Map;
import org.neo4j.graphdb.Result;

public class CCQuery extends FuzzyQuery {
    protected static double high = 28;

    protected static double veryHigh = 43;

    private CCQuery(QueryEngine queryEngine) {
        super(queryEngine);
        fclFile = "/ComplexClass.fcl";
    }

    public static CCQuery createCCQuery(QueryEngine queryEngine) {
        return new CCQuery(queryEngine);
    }

    public void execute(boolean details) throws IOException, CypherException {
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = ("MATCH (cl:Class) WHERE cl.class_complexity > " + (CCQuery.veryHigh)) + " RETURN cl.app_key as app_key";
            if (details) {
                query += ",cl.name as full_name";
            }else {
                query += ",count(cl) as CC";
            }
            result = graphDatabaseService.execute(query);
            queryEngine.resultToCSV(result, "_CC_NO_FUZZY.csv");
        }
    }

    @LM
    public void executeFuzzy(boolean details) throws IOException, CypherException {
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = ("MATCH (cl:Class) WHERE cl.class_complexity > " + (CCQuery.high)) + " RETURN cl.app_key as app_key, cl.class_complexity as class_complexity";
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
                cc = ((int) (res.get("class_complexity")));
                if (cc >= (CCQuery.veryHigh)) {
                    res.put("fuzzy_value", 1);
                }else {
                    fb.setVariable("class_complexity", cc);
                    fb.evaluate();
                    res.put("fuzzy_value", fb.getVariable("res").getValue());
                }
                fuzzyResult.add(res);
            } 
            queryEngine.resultToCSV(fuzzyResult, columns, "_CC.csv");
        }
    }
}

