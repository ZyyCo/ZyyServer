package cc.zyycc.agent.inject;


import cc.zyycc.agent.VarCapture;
import cc.zyycc.agent.inject.scan.IScanVar;

import java.util.*;

public class VariableCapture {

    public MethodParaIndexCapture methodParam;

    public ReturnCapture returnCapture;

    public IScanVar scan;

    private final List<VarCapture> captures = new ArrayList<>();

    public VariableCapture(List<VarCapture> captures) {
        this.captures.addAll(captures);
    }

    public VariableCapture() {
    }

    public VariableCapture(VarCapture captures) {
        this.captures.add(captures);
    }

    public VariableCapture(int injectPoint) {
        if (injectPoint < 2) {
            this.returnCapture = new ReturnCapture(injectPoint);
        }

    }

//    public void setReturnCapture(ReturnCapture returnCapture) {
//        this.returnCapture = returnCapture;
//    }

    public VariableCapture(MethodParaIndexCapture methodParaIndexCapture) {
        this.methodParam = methodParaIndexCapture;
    }

    public void createMethodParameterCapture(int... index) {
        methodParam = new MethodParaIndexCapture(index);
    }

    public void createMethodParameterCapture(String... name) {
        methodParam = new MethodParaIndexCapture(name);
    }


    public void setSuperConstructorCapture(String... globalFields) {
        this.captures.add(new GlobalVarCapture(globalFields));
    }

    public static VariableCapture createConstructorCapture(String... globalFields) {
        return new VariableCapture(new GlobalVarCapture(globalFields));
    }


    public List<VarCapture> getCaptures() {
        return captures;
    }

    public MethodParaIndexCapture getMethodParam() {
        return methodParam;
    }

    public void add(FieldScanCapture fieldScanCapture) {
        captures.add(fieldScanCapture);
    }


    public static class Builder {

        private final List<VarCapture> captures = new ArrayList<>();

        private UsedVarCapture used;

        private LocalVarIndexCapture localVarIndex;

        public Builder() {
        }

        public Builder addCapture(VarCapture capture) {
            captures.add(capture);
            return this;
        }

        public Builder global(String... globalVar) {
            captures.add(new GlobalVarCapture(globalVar));
            return this;
        }

        public Builder used(String name, String descriptor) {
            captures.add(new UsedVarCapture(name, descriptor));
//            this.used = new UsedVarCapture(name, descriptor);
            return this;
        }


        public Builder localVarIndex(int index, String descriptor) {
            captures.add(new LocalVarIndexCapture(index, descriptor));
//            this.localVarIndex = new LocalVarIndexCapture(index);
            return this;
        }


    }


}
