package paprika.neo4j;

import net.sourceforge.jFuzzyLogic.FIS;
import java.util.ArrayList;
import org.neo4j.cypher.CypherException;
import java.io.File;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import java.util.HashMap;
import java.io.IOException;
import codesmells.annotations.LM;
import org.neo4j.graphdb.Transaction;
import java.util.List;
import java.util.Map;
import org.neo4j.graphdb.Result;

public class LMQuery extends FuzzyQuery {
    protected static double high = 17;

    protected static double veryHigh = 26;

    private LMQuery(QueryEngine queryEngine) {
        super(queryEngine);
        fclFile = "/LongMethod.fcl";
    }

    public static LMQuery createLMQuery(QueryEngine queryEngine) {
        return new LMQuery(queryEngine);
    }

    public void execute(boolean details) throws IOException, CypherException {
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = ("MATCH (m:Method) WHERE m.number_of_instructions >" + (LMQuery.veryHigh)) + " RETURN m.app_key as app_key";
            if (details) {
                query += ",m.full_name as full_name";
            }else {
                query += ",count(m) as LM";
            }
            result = graphDatabaseService.execute(query);
            queryEngine.resultToCSV(result, "_LM_NO_FUZZY.csv");
        }
    }

    @LM
    public void executeFuzzy(boolean details) throws IOException, CypherException {
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = ("MATCH (m:Method) WHERE m.number_of_instructions >" + (LMQuery.high)) + " RETURN m.app_key as app_key,m.number_of_instructions as number_of_instructions";
            if (details) {
                query += ",m.full_name as full_name";
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
                cc = ((int) (res.get("number_of_instructions")));
                if (cc >= (LMQuery.veryHigh)) {
                    res.put("fuzzy_value", 1);
                }else {
                    fb.setVariable("number_of_instructions", cc);
                    fb.evaluate();
                    res.put("fuzzy_value", fb.getVariable("res").getValue());
                }
                fuzzyResult.add(res);
            } 
            queryEngine.resultToCSV(fuzzyResult, columns, "_LM.csv");
        }
    }
}

