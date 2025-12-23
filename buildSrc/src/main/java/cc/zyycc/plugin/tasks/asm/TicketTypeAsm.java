package cc.zyycc.plugin.tasks.asm;

import org.objectweb.asm.*;

public class TicketTypeAsm implements ClassEnhancer{
    @Override
    public String targetClassName() {
        return "net.minecraft.world.server.TicketType.class";
    }

    @Override
    public ClassVisitor createVisitor(ClassWriter cw) {
        return new ClassVisitor(Opcodes.ASM5, cw) {
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                // 添加一个 public static final TicketType 字段
                FieldVisitor fieldVisitor = cv.visitField(
                        Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,  // 访问权限：public static final
                        "PLUGIN",  // 字段名
                        "Lnet/minecraft/world/server/TicketType;",  // 字段类型
                        null,  // 泛型签名（这里为空）
                        null  // 字段初始值
                );
                fieldVisitor.visitEnd();
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                if ("<clinit>".equals(name)) {
                    // 你可以修改构造函数或其他方法的字节码
                    mv = new MethodVisitor(Opcodes.ASM5, mv) {
                        @Override
                        public void visitCode() {
                            super.visitCode();
                            // 在静态初始化块中插入对 PLUGIN 字段的赋值
                            mv.visitTypeInsn(Opcodes.NEW, "net/minecraft/world/server/TicketType");  // 创建 TicketType 类实例
                            mv.visitInsn(Opcodes.DUP);  // 复制对象

                            // 推送参数到栈上
                            mv.visitLdcInsn("plugin");  // 第一个参数："plugin"
                            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "net/minecraft/world/server/TicketType", "create",
                                    "(Ljava/lang/String;Lcom/mojang/serialization/Lambda;)Lnet/minecraft/world/server/TicketType;",
                                    false);  // 调用 TicketType.create 方法

                            // 为 PLUGIN 字段赋值
                            mv.visitFieldInsn(Opcodes.PUTSTATIC, "net/minecraft/world/server/TicketType",
                                    "PLUGIN", "Lnet/minecraft/world/server/TicketType;");  // 将生成的实例赋值给 PLUGIN

                            super.visitCode();
                        }
                    };
                }
                return mv;
            };
        };
    }


}
