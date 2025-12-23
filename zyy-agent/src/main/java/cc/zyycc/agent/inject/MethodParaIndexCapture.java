package cc.zyycc.agent.inject;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;
import java.util.Map;


public class MethodParaIndexCapture {

    private String[] name;
    private int[] index;

    public MethodParaIndexCapture(int... index) {
        this.index = index;

    }

    public MethodParaIndexCapture(String... name) {
        this.name = name;
    }


    public String load(MethodVisitor mv, Map<Integer, String> methodArgDesc) {
        StringBuilder sb = new StringBuilder();
        if (!methodArgDesc.isEmpty()) {
            for (int i : index) {
                mv.visitVarInsn(Opcodes.ALOAD, i);
                String s = methodArgDesc.get(i);
                if (s == null) {
                    throw new RuntimeException("methodArgDesc is empty");
                }
                sb.append(s);

            }
        }
//        if (name != null) {
//            String[] split = descriptor.substring(descriptor.indexOf("(") + 1, descriptor.indexOf(")")).trim().split(";");
//            for (String captureName : name) {
//                Type[] argumentTypes = Type.getArgumentTypes(descriptor);
//                for (int i = 0; i < argumentTypes.length; i++) {
//                    if (argumentTypes[i].getClassName().equals(captureName)) {
//                        mv.visitVarInsn(Opcodes.ALOAD, i + 1);
//                        sb.append(split[i]).append(";");
//                    }
//                }
//            }
//
//        }

        return sb.toString();
    }
}
