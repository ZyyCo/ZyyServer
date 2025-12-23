package cc.zyycc.agent.inject;

import cc.zyycc.agent.inject.hookResult.*;
import cc.zyycc.agent.inject.method.InjectFieldTarget;
import cc.zyycc.agent.inject.method.InjectMethodTarget;
import cc.zyycc.agent.inject.method.InjectTarget;
import cc.zyycc.agent.inject.method.InjectVisitInsnTarget;
import cc.zyycc.agent.inject.returnType.InjectReturnType;
import cc.zyycc.agent.inject.scan.ScanBase;

public class Injects {
    private String className;
    private String packageName;
    private String resultPackage;


    public Injects(String className) {
        this.className = className;
    }

    public static Injects create(String className) {
        return create(className, "cc/zyycc/common/asm");
    }

    public static Injects create(String className, String resultPackage) {
        Injects injects = new Injects(className);
        injects.packageName = getPackageName(className);
        injects.resultPackage = resultPackage;
        return injects;
    }

    public Injects resetPackageName(String packageName) {
        this.className = className + packageName;
        return this;
    }

    public static String getPackageName(String className) {
        int i = className.lastIndexOf("/") + 1;
        return className.substring(0, i);
    }


    public Injects.InNewFunction inNewFunction(String methodName) {
        return new InNewFunction(packageName, this.className, methodName);
    }

    public Injects.Function function(String methodName, int injectPoint) {
        return new Function(injectPoint, resultPackage, this.className, methodName);
    }

    public Injects.Function function(String methodName) {
        return new Function(2, resultPackage, this.className, methodName);
    }

    public static class InNewFunction {
        private final String className;
        private final String resultPackage;
        private String targetClass;
        private String fieldName, fieldDesc;
        private int[] args;
        private boolean cancelable;

        private GlobalVarCapture globalVarCapture;
        private String methodName;
        private InjectReturnType returnType;

        public InNewFunction(String resultPackage, String className, String methodName) {
            this.resultPackage = resultPackage;
            this.className = className;
            this.methodName = methodName;
        }

        public InNewFunction targetClass(String cls) {
            this.targetClass = cls;
            return this;
        }

        public InNewFunction field(String name, String desc) {
            this.fieldName = name;
            this.fieldDesc = desc;
            return this;
        }

        public InNewFunction args(int... args) {
            this.args = args;
            return this;
        }

        public InNewFunction cancelable() {
            this.cancelable = true;
            return this;
        }


        public InNewFunction captureField(String field) {
            this.globalVarCapture = new GlobalVarCapture(field);
            return this;
        }

        public InNewFunction returnType(String returnName, String returnDesc) {
            this.cancelable = true;
            this.returnType = new InjectReturnType(returnName, returnDesc);
            return this;
        }

        public InjectInNewFunction register() {
            return new InjectInNewFunction(resultPackage, className,
                    methodName,
                    globalVarCapture,
                    returnType);
        }
    }

    public static class Function {
        private final String className;
        private final String methodName;
        private final String resultPackage;
        private VariableCapture varCapture;

        private InjectTarget injectTarget;
        private int locationIndex = -1;
        private ConditionReturn conditionReturn;
        private boolean cancelableHookResult;
        private IInjectResult injectResult;
        private IReturnCapture iReturnCapture;
        private InjectVisitMethodReplaceLocal injectVisit;
        private ScanBase scanBase;

        public Function(int injectPoint, String resultPackage, String className, String methodName) {
            this.resultPackage = resultPackage;
            this.className = className;
            this.methodName = methodName;
            this.varCapture = new VariableCapture(injectPoint);
        }

        public Function args(int... args) {
            varCapture.createMethodParameterCapture(args);
            return this;
        }

        public Function argFieldName(String... name) {
            varCapture.add(new FieldScanCapture(name));
            return this;
        }


        public Function captureField(String field) {
            this.varCapture = new VariableCapture(new GlobalVarCapture(field));
            return this;
        }

        public Function replaceLocal(int astore, ConditionReturn conditionReturn) {
            this.iReturnCapture = new LocalVarCapture(astore);
            this.conditionReturn = conditionReturn;
            return this;
        }

        public Function replaceLocal(String name) {
            this.iReturnCapture = new LocalVarCapture(name);
            this.scanBase = new ScanBase(name);
            return this;
        }

        public Function replaceGlobalVar(String name) {
            this.iReturnCapture = new GlobalVarCapture(name);
            this.scanBase = new ScanBase(name);
            return this;
        }

        public Function targetField(int point, String field, String desc, int index) {
            this.injectTarget = new InjectFieldTarget(field, desc, index);//commandsSource;
            return this;
        }

        public Function targetMethod(int point, String owner, String name, String desc, int index) {
            this.injectTarget = new InjectMethodTarget(point, owner, name, desc, false);
            return this;
        }


        public Function cancelableHookResult() {
            this.cancelableHookResult = true;
            return this;
        }

        public Function customInjectResult(IInjectResult injectResult) {
            this.injectResult = injectResult;
            return this;
        }

        public Function conditionReturn(ConditionReturn returnType) {
            this.conditionReturn = returnType;
            return this;
        }


        public Function targetVisitInsn(int opcodes, int index, int point) {
            this.injectTarget = new InjectVisitInsnTarget(opcodes, index, point);
            return this;
        }


        public Function returnVisitMethod(int args) {
            this.cancelableHookResult = true;
            this.locationIndex = args;
            return this;
        }


        public InjectVisitMethod register() {
            if (iReturnCapture != null && conditionReturn != null && iReturnCapture instanceof LocalVarCapture) {
                return new InjectVisitMethodReplaceLocal(resultPackage, className,
                        methodName,
                        varCapture,
                        injectTarget, iReturnCapture, conditionReturn);

            } else if (iReturnCapture != null && scanBase != null) {
                return new InjectVisitMethodReplaceLocal(resultPackage, className,
                        methodName,
                        varCapture,
                        injectTarget, iReturnCapture, scanBase);
            } else if (injectResult != null) {
                return new CustomMethodHook(resultPackage, className,
                        methodName,
                        varCapture,
                        injectTarget, injectResult);//commandsSource
            } else if (locationIndex >= 0) {
                return new InjectVisitMethodHookResultReplace(resultPackage, className,
                        methodName,
                        varCapture,
                        injectTarget, locationIndex);//commandsSource
            } else if (conditionReturn != null && cancelableHookResult) {
                return new InjectVisitMethodCancel(resultPackage, className,
                        methodName,
                        varCapture,
                        injectTarget, conditionReturn);
            } else if (cancelableHookResult) {
                return new InjectVisitMethodHookResultCancel(resultPackage, className,
                        methodName,
                        varCapture,
                        injectTarget);
            }

            return new InjectVisitMethodHook(resultPackage, className,
                    methodName,
                    varCapture,
                    injectTarget);
        }


    }
}
