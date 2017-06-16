package paprika.analyzer;

import paprika.metrics.ClassComplexity;
import codesmells.annotations.CC;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.util.Chain;
import java.io.File;
import paprika.entities.PaprikaVariable;
import paprika.metrics.NumberOfAttributes;
import paprika.metrics.NumberOfInstructions;
import paprika.metrics.IsARGB8888;
import paprika.metrics.LackofCohesionInMethods;
import paprika.entities.PaprikaApp;
import paprika.metrics.NumberOfMethods;
import paprika.metrics.IsFinal;
import paprika.metrics.IsSetter;
import java.util.HashMap;
import java.util.List;
import paprika.metrics.NumberOfServices;
import paprika.metrics.IsAbstract;
import paprika.metrics.IsStatic;
import Options.output_format_grimple;
import paprika.metrics.NPathComplexity;
import soot.grimp.internal.GAssignStmt;
import soot.grimp.GrimpBody;
import paprika.metrics.IsInterface;
import java.util.logging.Logger;
import java.util.LinkedList;
import paprika.metrics.NumberOfViews;
import paprika.entities.PaprikaArgument;
import paprika.metrics.NumberOfAsyncTasks;
import paprika.metrics.NumberOfClasses;
import paprika.metrics.NumberOfContentProviders;
import paprika.metrics.NumberOfInnerClasses;
import codesmells.annotations.LM;
import java.util.Collections;
import paprika.metrics.CouplingBetweenObjects;
import paprika.metrics.CyclomaticComplexity;
import paprika.metrics.IsSynchronized;
import paprika.metrics.NumberOfChildren;
import paprika.entities.PaprikaClass;
import soot.options.Options;
import paprika.metrics.NumberOfCallers;
import paprika.metrics.DepthOfInheritance;
import paprika.metrics.IsActivity;
import paprika.metrics.NumberOfActivities;
import java.io.OutputStream;
import codesmells.annotations.LIC;
import paprika.metrics.IsInnerClass;
import paprika.metrics.IsOverride;
import java.util.Iterator;
import paprika.metrics.NumberOfAbstractClasses;
import paprika.metrics.NumberOfDirectCalls;
import paprika.entities.PaprikaExternalMethod;
import soot.grimp.internal.GRValueBox;
import soot.jimple.toolkits.callgraph.Edge;
import paprika.entities.PaprikaExternalClass;
import paprika.entities.PaprikaMethod;
import paprika.metrics.NumberOfArgb8888;
import paprika.metrics.NumberOfVariables;
import paprika.metrics.NumberOfImplementedInterfaces;
import java.io.PrintStream;
import paprika.metrics.NumberOfInterfaces;
import paprika.metrics.NumberOfDeclaredLocals;
import soot.jimple.StaticFieldRef;
import paprika.metrics.NumberOfParameters;
import paprika.entities.PaprikaExternalArgument;
import soot.jimple.FieldRef;
import paprika.metrics.IsInit;
import paprika.entities.PaprikaModifiers;
import Options.src_prec_apk;
import java.util.Map;
import paprika.metrics.NumberOfBroadcastReceivers;

@CC
public class SootAnalyzer extends Analyzer {
    private static final Logger LOGGER = Logger.getLogger(SootAnalyzer.class.getName());

    private static String androidJAR;

    private PaprikaApp paprikaApp;

    private Map<SootClass, PaprikaClass> classMap;

    private Map<SootClass, PaprikaExternalClass> externalClassMap;

    private Map<SootMethod, PaprikaExternalMethod> externalMethodMap;

    private Map<SootMethod, PaprikaMethod> methodMap;

    int argb8888Count = 0;

    int activityCount = 0;

    int innerCount = 0;

    int varCount = 0;

    int asyncCount = 0;

    int serviceCount = 0;

    int viewCount = 0;

    int interfaceCount = 0;

    int abstractCount = 0;

    int broadcastReceiverCount = 0;

    int contentProviderCount = 0;

    private String rClass;

    private String buildConfigClass;

    private String pack;

    private boolean mainPackageOnly = false;

    public SootAnalyzer(String apk, String androidJAR, String name, String key, String pack, String date, int size, String dev, String cat, String price, double rating, String nbDownload, String versionCode, String versionName, String sdkVersion, String targetSdkVersion, boolean mainPackageOnly) {
        Analyzer.apk = apk;
        this.androidJAR = androidJAR;
        this.pack = pack;
        this.paprikaApp = PaprikaApp.createPaprikaApp(name, key, pack, date, size, dev, cat, price, rating, nbDownload, versionCode, versionName, sdkVersion, targetSdkVersion);
        this.rClass = pack.concat(".R");
        this.buildConfigClass = pack.concat(".BuildConfig");
        this.classMap = new HashMap();
        this.externalClassMap = new HashMap();
        this.externalMethodMap = new HashMap();
        this.methodMap = new HashMap();
        this.mainPackageOnly = mainPackageOnly;
    }

    @Override
    @LM
    public void init() {
        PrintStream originalStream = System.out;
        System.setOut(new PrintStream(new OutputStream() {
            public void write(int b) {
            }
        }));
        G.reset();
        Options.v().set_verbose(false);
        Options.v().set_android_jars(SootAnalyzer.androidJAR);
        Options.v().set_src_prec(src_prec_apk);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_process_dir(Collections.singletonList(Analyzer.apk));
        Options.v().set_whole_program(true);
        Options.v().set_output_format(output_format_grimple);
        Options.v().set_output_dir((((System.getProperty("user.home")) + (File.separator)) + "/These/decompiler/out"));
        PhaseOptions.v().setPhaseOption("gop", "enabled:true");
        System.setOut(originalStream);
        List<String> excludeList = new LinkedList<String>();
        excludeList.add("java.");
        excludeList.add("sun.misc.");
        excludeList.add("android.");
        excludeList.add("org.apache.");
        excludeList.add("soot.");
        excludeList.add("javax.servlet.");
        Options.v().set_exclude(excludeList);
        Scene.v().loadNecessaryClasses();
    }

    @Override
    public void runAnalysis() {
        collectClassesMetrics();
        collectAppMetrics();
        PackManager.v().getPack("gop").add(new Transform("gop.myInstrumenter", new BodyTransformer() {
            @Override
            protected void internalTransform(final Body body, String phaseName, @SuppressWarnings(value = "rawtypes")
            Map options) {
                collectMethodsMetrics(body.getMethod());
            }
        }));
        PackManager.v().runPacks();
        computeMetrics();
        collectCallGraphMetrics();
    }

    private void collectCallGraphMetrics() {
        for (Map.Entry<SootMethod, PaprikaMethod> entry : methodMap.entrySet()) {
            collectMethodMetricsFromCallGraph(entry.getValue(), entry.getKey());
        }
    }

    @Override
    public PaprikaApp getPaprikaApp() {
        return paprikaApp;
    }

    public void collectAppMetrics() {
        NumberOfClasses.createNumberOfClasses(this.paprikaApp, classMap.size());
        NumberOfActivities.createNumberOfActivities(this.paprikaApp, activityCount);
        NumberOfServices.createNumberOfServices(this.paprikaApp, serviceCount);
        NumberOfInnerClasses.createNumberOfInnerClasses(this.paprikaApp, innerCount);
        NumberOfAsyncTasks.createNumberOfAsyncTasks(this.paprikaApp, asyncCount);
        NumberOfViews.createNumberOfViews(this.paprikaApp, viewCount);
        NumberOfVariables.createNumberOfVariables(this.paprikaApp, varCount);
        NumberOfInterfaces.createNumberOfInterfaces(this.paprikaApp, interfaceCount);
        NumberOfAbstractClasses.createNumberOfAbstractClasses(this.paprikaApp, abstractCount);
        NumberOfBroadcastReceivers.createNumberOfBroadcastReceivers(this.paprikaApp, broadcastReceiverCount);
        NumberOfContentProviders.createNumberOfContentProviders(this.paprikaApp, contentProviderCount);
    }

    @LM
    public void collectClassesMetrics() {
        Chain<SootClass> sootClasses = Scene.v().getApplicationClasses();
        for (SootClass sootClass : sootClasses) {
            String rsubClassStart = (rClass) + "$";
            String name = sootClass.getName();
            String packs = pack.concat(".");
            if (((name.equals(rClass)) || (name.startsWith(rsubClassStart))) || (name.equals(buildConfigClass))) {
            }else
                if ((!(mainPackageOnly)) || (name.startsWith(packs))) {
                    collectClassMetrics(sootClass);
                }
            
        }
        for (SootClass sootClass : sootClasses) {
            if (sootClass.hasSuperclass()) {
                SootClass superClass = sootClass.getSuperclass();
                PaprikaClass paprikaClass = classMap.get(superClass);
                if (paprikaClass != null)
                    classMap.get(superClass).addChildren();
                
            }
        }
    }

    public void computeMetrics() {
        computeInheritance();
        computeInterface();
        for (PaprikaClass paprikaClass : paprikaApp.getPaprikaClasses()) {
            ClassComplexity.createClassComplexity(paprikaClass);
            NumberOfChildren.createNumberOfChildren(paprikaClass);
            CouplingBetweenObjects.createCouplingBetweenObjects(paprikaClass);
            LackofCohesionInMethods.createLackofCohesionInMethods(paprikaClass);
            NPathComplexity.createNPathComplexity(paprikaClass);
        }
        NumberOfMethods.createNumberOfMethods(paprikaApp, methodMap.size());
    }

    @LM
    public void collectMethodsMetrics(SootMethod sootMethod) {
        SootClass sootClass = sootMethod.getDeclaringClass();
        PaprikaClass paprikaClass = classMap.get(sootClass);
        if (paprikaClass == null) {
            sootClass.setLibraryClass();
            return ;
        }
        PaprikaModifiers modifiers = PaprikaModifiers.PRIVATE;
        if (sootMethod.isPublic()) {
            modifiers = PaprikaModifiers.PUBLIC;
        }else
            if (sootMethod.isProtected()) {
                modifiers = PaprikaModifiers.PROTECTED;
            }
        
        PaprikaMethod paprikaMethod = PaprikaMethod.createPaprikaMethod(sootMethod.getName(), modifiers, sootMethod.getReturnType().toString(), paprikaClass);
        methodMap.put(sootMethod, paprikaMethod);
        if (sootMethod.isStatic()) {
            IsStatic.createIsStatic(paprikaMethod, true);
        }
        if (sootMethod.isFinal()) {
            IsFinal.createIsFinal(paprikaMethod, true);
        }
        if (sootMethod.isSynchronized()) {
            IsSynchronized.createIsSynchronized(paprikaMethod, true);
        }
        if (sootMethod.isAbstract()) {
            IsAbstract.createIsAbstract(paprikaMethod, true);
        }
        NumberOfParameters.createNumberOfParameters(paprikaMethod, sootMethod.getParameterCount());
        if (sootMethod.hasActiveBody()) {
            int i = 0;
            for (Type type : sootMethod.getParameterTypes()) {
                i++;
                PaprikaArgument.createPaprikaArgument(type.toString(), i, paprikaMethod);
            }
            GrimpBody activeBody = ((GrimpBody) (sootMethod.getActiveBody()));
            int nbOfLines = ((activeBody.getUnits().size()) - (sootMethod.getParameterCount())) - 1;
            NumberOfDeclaredLocals.createNumberOfDeclaredLocals(paprikaMethod, activeBody.getLocals().size());
            NumberOfInstructions.createNumberOfInstructions(paprikaMethod, nbOfLines);
            int nbOfBranches = 1;
            PaprikaVariable paprikaVariable = null;
            for (Unit sootUnit : activeBody.getUnits()) {
                List<ValueBox> boxes = sootUnit.getUseAndDefBoxes();
                for (ValueBox valueBox : boxes) {
                    Value value = valueBox.getValue();
                    if (value instanceof FieldRef) {
                        SootFieldRef field = ((FieldRef) (value)).getFieldRef();
                        if ((field.declaringClass()) == sootClass) {
                            paprikaVariable = paprikaClass.findVariable(field.name());
                            if (paprikaVariable != null) {
                                paprikaMethod.useVariable(paprikaVariable);
                            }
                        }
                    }
                }
                if (sootUnit.branches()) {
                    if (sootUnit.fallsThrough())
                        nbOfBranches++;
                    else
                        if (sootUnit instanceof soot.grimp.internal.GLookupSwitchStmt)
                            nbOfBranches += ((soot.grimp.internal.GLookupSwitchStmt) (sootUnit)).getLookupValues().size();
                        
                    
                }
            }
            CyclomaticComplexity.createCyclomaticComplexity(paprikaMethod, nbOfBranches);
            if (isInit(sootMethod)) {
                IsInit.createIsInit(paprikaMethod, true);
            }else {
                if (isOverride(sootMethod)) {
                    IsOverride.createIsOverride(paprikaMethod, true);
                }
                if (((nbOfBranches == 1) && ((paprikaMethod.getUsedVariables().size()) == 1)) && ((sootMethod.getExceptions().size()) == 0)) {
                    paprikaVariable = paprikaMethod.getUsedVariables().iterator().next();
                    int parameterCount = sootMethod.getParameterCount();
                    int unitSize = sootMethod.getActiveBody().getUnits().size();
                    String returnType = paprikaMethod.getReturnType();
                    if (((parameterCount == 1) && (unitSize <= 4)) && (returnType.equals("void"))) {
                        IsSetter.createIsSetter(paprikaMethod, true);
                    }else
                        if (((parameterCount == 0) && (unitSize <= 3)) && (returnType.equals(paprikaVariable.getType()))) {
                            paprika.metrics.IsGetter.createIsGetter(paprikaMethod, true);
                        }
                    
                }
            }
        }else {
        }
    }

    private boolean isInit(SootMethod sootMethod) {
        String name = sootMethod.getName();
        return (name.equals("<init>")) || (name.equals("<clinit>"));
    }

    @LM
    private void collectMethodMetricsFromCallGraph(PaprikaMethod paprikaMethod, SootMethod sootMethod) {
        CallGraph callGraph = Scene.v().getCallGraph();
        int edgeOutCount = 0;
        int edgeIntoCount = 0;
        Iterator<Edge> edgeOutIterator = callGraph.edgesOutOf(sootMethod);
        Iterator<Edge> edgeIntoIterator = callGraph.edgesInto(sootMethod);
        callGraph = null;
        PaprikaClass currentClass = paprikaMethod.getPaprikaClass();
        while (edgeOutIterator.hasNext()) {
            Edge e = edgeOutIterator.next();
            SootMethod target = e.tgt();
            PaprikaMethod targetMethod = methodMap.get(target);
            if (targetMethod == null) {
                PaprikaExternalMethod externalTgtMethod = externalMethodMap.get(target);
                if (externalTgtMethod == null) {
                    PaprikaExternalClass paprikaExternalClass = externalClassMap.get(target.getDeclaringClass());
                    if (paprikaExternalClass == null) {
                        paprikaExternalClass = PaprikaExternalClass.createPaprikaExternalClass(target.getDeclaringClass().getName(), paprikaApp);
                        externalClassMap.put(target.getDeclaringClass(), paprikaExternalClass);
                    }
                    externalTgtMethod = PaprikaExternalMethod.createPaprikaExternalMethod(target.getName(), target.getReturnType().toString(), paprikaExternalClass);
                    int i = 0;
                    for (Type type : target.getParameterTypes()) {
                        i++;
                        PaprikaExternalArgument paprikaExternalArgument = PaprikaExternalArgument.createPaprikaExternalArgument(type.toString(), i, externalTgtMethod);
                        if ((paprikaExternalArgument.getName()) == "android.graphics.Bitmap$Config") {
                            for (Unit unitChain : ((SootMethod) (e.getSrc())).getActiveBody().getUnits()) {
                                try {
                                    String nameOfStaticFieldRef = ((StaticFieldRef) (((GRValueBox) (((GAssignStmt) (unitChain)).getRightOpBox())).getValue())).getFieldRef().name();
                                    if (nameOfStaticFieldRef.equals("ARGB_8888")) {
                                        (argb8888Count)++;
                                        IsARGB8888.createIsARGB8888(paprikaExternalArgument, true);
                                    }
                                } catch (Exception new_exception) {
                                }
                            }
                        }
                    }
                    externalMethodMap.put(target, externalTgtMethod);
                }
                paprikaMethod.callMethod(externalTgtMethod);
            }
            if (targetMethod != null) {
                paprikaMethod.callMethod(targetMethod);
            }
            PaprikaClass targetClass = classMap.get(target.getDeclaringClass());
            if (((e.isVirtual()) || (e.isSpecial())) || (e.isStatic()))
                edgeOutCount++;
            
            if ((targetClass != null) && (targetClass != currentClass))
                currentClass.coupledTo(targetClass);
            
        } 
        while (edgeIntoIterator.hasNext()) {
            Edge e = edgeIntoIterator.next();
            if (e.isExplicit())
                edgeIntoCount++;
            
        } 
        NumberOfDirectCalls.createNumberOfDirectCalls(paprikaMethod, edgeOutCount);
        NumberOfCallers.createNumberOfCallers(paprikaMethod, edgeIntoCount);
        NumberOfArgb8888.createNumberOfArgb8888(paprikaApp, argb8888Count);
    }

    @LM
    public void collectClassMetrics(SootClass sootClass) {
        PaprikaModifiers modifier = PaprikaModifiers.PRIVATE;
        if (sootClass.isPublic()) {
            modifier = PaprikaModifiers.PUBLIC;
        }else
            if (sootClass.isProtected()) {
                modifier = PaprikaModifiers.PROTECTED;
            }
        
        PaprikaClass paprikaClass = PaprikaClass.createPaprikaClass(sootClass.getName(), this.paprikaApp, modifier);
        if (sootClass.isFinal()) {
            IsFinal.createIsFinal(paprikaClass, true);
        }
        if (sootClass.isInnerClass()) {
            (innerCount)++;
            IsInnerClass.createIsInnerClass(paprikaClass, true);
            if (isInnerClassStatic(sootClass)) {
                IsStatic.createIsStatic(paprikaClass, true);
            }
        }
        if (isActivity(sootClass)) {
            (activityCount)++;
            IsActivity.createIsActivity(paprikaClass, true);
        }else
            if (isService(sootClass)) {
                (serviceCount)++;
                paprika.metrics.IsService.createIsService(paprikaClass, true);
            }else
                if (isView(sootClass)) {
                    (viewCount)++;
                    paprika.metrics.IsView.createIsView(paprikaClass, true);
                }else
                    if (isAsyncTask(sootClass)) {
                        (asyncCount)++;
                        paprika.metrics.IsAsyncTask.createIsAsyncTask(paprikaClass, true);
                    }else
                        if (isBroadcastReceiver(sootClass)) {
                            (broadcastReceiverCount)++;
                            paprika.metrics.IsBroadcastReceiver.createIsBroadcastReceiver(paprikaClass, true);
                        }else
                            if (isContentProvider(sootClass)) {
                                (contentProviderCount)++;
                                paprika.metrics.IsContentProvider.createIsContentProvider(paprikaClass, true);
                            }else
                                if (isApplication(sootClass)) {
                                    paprika.metrics.IsApplication.createIsApplication(paprikaClass, true);
                                }
                            
                        
                    
                
            
        
        if (sootClass.isAbstract()) {
            (abstractCount)++;
            IsAbstract.createIsAbstract(paprikaClass, true);
        }
        if (sootClass.isInterface()) {
            (interfaceCount)++;
            IsInterface.createIsInterface(paprikaClass, true);
        }
        for (SootField sootField : sootClass.getFields()) {
            modifier = PaprikaModifiers.PRIVATE;
            if (sootField.isPublic()) {
                modifier = PaprikaModifiers.PUBLIC;
            }else
                if (sootField.isProtected()) {
                    modifier = PaprikaModifiers.PROTECTED;
                }
            
            PaprikaVariable paprikaVariable = PaprikaVariable.createPaprikaVariable(sootField.getName(), sootField.getType().toString(), modifier, paprikaClass);
            (varCount)++;
            if (sootField.isStatic()) {
                IsStatic.createIsStatic(paprikaVariable, true);
            }
            if (sootField.isFinal()) {
                IsFinal.createIsFinal(paprikaVariable, true);
            }
        }
        if (sootClass.hasSuperclass()) {
            paprikaClass.setParentName(sootClass.getSuperclass().getName());
        }
        this.classMap.put(sootClass, paprikaClass);
        NumberOfMethods.createNumberOfMethods(paprikaClass, sootClass.getMethodCount());
        DepthOfInheritance.createDepthOfInheritance(paprikaClass, getDepthOfInheritance(sootClass));
        NumberOfImplementedInterfaces.createNumberOfImplementedInterfaces(paprikaClass, sootClass.getInterfaceCount());
        NumberOfAttributes.createNumberOfAttributes(paprikaClass, sootClass.getFieldCount());
    }

    private boolean isInnerClassStatic(SootClass innerClass) {
        for (SootField sootField : innerClass.getFields()) {
            if (sootField.getName().equals("this$0")) {
                return false;
            }
        }
        return true;
    }

    public int getDepthOfInheritance(SootClass sootClass) {
        int doi = 0;
        do {
            doi++;
            sootClass = sootClass.getSuperclass();
        } while (sootClass.hasSuperclass() );
        return doi;
    }

    public void computeInheritance() {
        for (Map.Entry entry : classMap.entrySet()) {
            SootClass sClass = ((SootClass) (entry.getKey()));
            PaprikaClass pClass = ((PaprikaClass) (entry.getValue()));
            SootClass sParent = sClass.getSuperclass();
            PaprikaClass pParent = classMap.get(sParent);
            if (pParent != null) {
                pClass.setParent(pParent);
            }
        }
    }

    public void computeInterface() {
        for (Map.Entry entry : classMap.entrySet()) {
            SootClass sClass = ((SootClass) (entry.getKey()));
            PaprikaClass pClass = ((PaprikaClass) (entry.getValue()));
            for (SootClass SInterface : sClass.getInterfaces()) {
                PaprikaClass pInterface = classMap.get(SInterface);
                if (pInterface != null) {
                    pClass.implement(pInterface);
                }
            }
        }
    }

    private boolean isActivity(SootClass sootClass) {
        return isSubClass(sootClass, "android.app.Activity");
    }

    private boolean isService(SootClass sootClass) {
        return isSubClass(sootClass, "android.app.Service");
    }

    private boolean isAsyncTask(SootClass sootClass) {
        return isSubClass(sootClass, "android.os.AsyncTask");
    }

    private boolean isView(SootClass sootClass) {
        return isSubClass(sootClass, "android.view.View");
    }

    private boolean isBroadcastReceiver(SootClass sootClass) {
        return isSubClass(sootClass, "android.content.BroadcastReceiver");
    }

    private boolean isContentProvider(SootClass sootClass) {
        return isSubClass(sootClass, "android.content.ContentProvider");
    }

    private boolean isApplication(SootClass sootClass) {
        return isSubClass(sootClass, "android.app.Application");
    }

    private boolean isSubClass(SootClass sootClass, String className) {
        do {
            if (sootClass.getName().equals(className))
                return true;
            
            sootClass = sootClass.getSuperclass();
        } while (sootClass.hasSuperclass() );
        return false;
    }

    @LM
    private boolean isOverride(SootMethod sootMethod) {
        SootClass sootClass = sootMethod.getDeclaringClass();
        for (SootClass inter : sootClass.getInterfaces()) {
            if (classContainsMethod(inter, sootMethod))
                return true;
            
            while (inter.hasSuperclass()) {
                inter = inter.getSuperclass();
                if (classContainsMethod(inter, sootMethod))
                    return true;
                
            } 
        }
        while (sootClass.hasSuperclass()) {
            sootClass = sootClass.getSuperclass();
            if (classContainsMethod(sootClass, sootMethod))
                return true;
            
        } 
        return false;
    }

    private boolean classContainsMethod(SootClass sootClass, SootMethod sootMethod) {
        if ((sootClass.getMethodUnsafe(sootMethod.getName(), sootMethod.getParameterTypes(), sootMethod.getReturnType())) != null)
            return true;
        else
            return false;
        
    }
}

