package paprika.neo4j;

import net.sourceforge.jFuzzyLogic.FunctionBlock;
import java.util.ArrayList;
import org.neo4j.graphdb.Transaction;
import org.neo4j.cypher.CypherException;
import net.sourceforge.jFuzzyLogic.FIS;
import java.io.File;
import java.util.HashMap;
import java.io.IOException;
import codesmells.annotations.LM;
import java.util.List;
import java.util.Map;
import org.neo4j.graphdb.Result;

public class HeavyAsyncTaskStepsQuery extends FuzzyQuery {
    protected static double high_cc = 3.5;

    protected static double veryHigh_cc = 5;

    protected static double high_noi = 17;

    protected static double veryHigh_noi = 26;

    private HeavyAsyncTaskStepsQuery(QueryEngine queryEngine) {
        super(queryEngine);
        fclFile = "/HeavySomething.fcl";
    }

    public static HeavyAsyncTaskStepsQuery createHeavyAsyncTaskStepsQuery(QueryEngine queryEngine) {
        return new HeavyAsyncTaskStepsQuery(queryEngine);
    }

    public void execute(boolean details) throws IOException, CypherException {
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = ((("MATCH (c:Class{parent_name:'android.os.AsyncTask'})-[:CLASS_OWNS_METHOD]->(m:Method) WHERE (m.name='onPreExecute' OR m.name='onProgressUpdate' OR m.name='onPostExecute') AND  m.number_of_instructions >" + (HeavyAsyncTaskStepsQuery.veryHigh_noi)) + " AND m.cyclomatic_complexity > ") + (HeavyAsyncTaskStepsQuery.veryHigh_cc)) + " return m.app_key as app_key";
            if (details) {
                query += ",m.full_name as full_name";
            }else {
                query += ",count(m) as HAS";
            }
            result = graphDatabaseService.execute(query);
            queryEngine.resultToCSV(result, "_HAS_NO_FUZZY.csv");
        }
    }

    @LM
    public void executeFuzzy(boolean details) throws IOException, CypherException {
        Result result;
        try (Transaction ignored = graphDatabaseService.beginTx()) {
            String query = ((("MATCH (c:Class{parent_name:'android.os.AsyncTask'})-[:CLASS_OWNS_METHOD]->(m:Method) WHERE (m.name='onPreExecute' OR m.name='onProgressUpdate' OR m.name='onPostExecute') AND  m.number_of_instructions >" + (HeavyAsyncTaskStepsQuery.high_noi)) + " AND m.cyclomatic_complexity > ") + (HeavyAsyncTaskStepsQuery.high_cc)) + " return m.app_key as app_key,m.cyclomatic_complexity as cyclomatic_complexity, m.number_of_instructions as number_of_instructions";
            if (details) {
                query += ",m.full_name as full_name";
            }
            result = graphDatabaseService.execute(query);
            List<String> columns = new ArrayList<>(result.columns());
            columns.add("fuzzy_value");
            int noi;
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
                cc = ((int) (res.get("cyclomatic_complexity")));
                noi = ((int) (res.get("number_of_instructions")));
                if ((cc >= (HeavyAsyncTaskStepsQuery.veryHigh_cc)) && (noi >= (HeavyAsyncTaskStepsQuery.veryHigh_noi))) {
                    res.put("fuzzy_value", 1);
                }else {
                    fb.setVariable("cyclomatic_complexity", cc);
                    fb.setVariable("number_of_instructions", noi);
                    fb.evaluate();
                    res.put("fuzzy_value", fb.getVariable("res").getValue());
                }
                fuzzyResult.add(res);
            } 
            queryEngine.resultToCSV(fuzzyResult, columns, "_HAS.csv");
        }
    }
}

