package cc.zyycc.agent.transformer.scan;

import cc.zyycc.agent.transformer.TransformerProvider;

import java.security.ProtectionDomain;

public interface IScan {


    void classLoader(String classLoader);


    boolean scan(TransformerProvider provider, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer);


    ScanStrategy moveClass(ScanStrategy className);
}
