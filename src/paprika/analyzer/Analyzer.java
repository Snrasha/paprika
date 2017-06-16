package paprika.analyzer;

import codesmells.annotations.MIM;
import paprika.entities.PaprikaApp;

public abstract class Analyzer {
    protected static String apk;

    @MIM
    public abstract void init();

    @MIM
    public abstract void runAnalysis();

    @MIM
    public abstract PaprikaApp getPaprikaApp();
}

