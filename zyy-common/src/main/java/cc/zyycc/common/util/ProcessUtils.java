package cc.zyycc.common.util;

import java.io.*;
import java.util.List;

public class ProcessUtils {
    /**
     * 运行一个命令，并将输出重定向到日志文件
     *
     * @param command 命令参数（如 java ...）
     * @param workingDir 工作目录（用于设置 ProcessBuilder）
     * @param logFile 日志文件（输出日志保存到这个文件）
     * @return 子进程退出码
     */
    public static int runProcess(List<String> command, File workingDir, File logFile) {
        try {
            // 1. 创建 ProcessBuilder创建子进程	准备执行命令
            ProcessBuilder builder = new ProcessBuilder(command);
            //设置工作目录	保证路径相对正确
            builder.directory(workingDir);
            // 2. 启动进程
            Process process = builder.start();

            // 3. 创建日志输出流（追加 false，覆盖）
            File logDir = new File(logFile.getParent());
            if(!logDir.exists()){
                logDir.mkdirs();
            }
            PrintWriter logWriter = new PrintWriter(new FileWriter(logFile, false), true);

            // 4. 异步读取子进程的标准输出和标准错误输出，并写入日志
            Thread stdoutThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);      // 同步打印到控制台
                        logWriter.println(line);       // 写入日志
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            Thread stderrThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.err.println(line);      // 错误流打印到控制台
                        logWriter.println("[ERR] " + line); // 加个标记写入日志
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // 5. 启动读取线程
            stdoutThread.start();
            stderrThread.start();

            // 6. 等待进程结束 + 等待读取线程结束
            int exitCode = process.waitFor();
            stdoutThread.join();
            stderrThread.join();

            // 7. 关闭写入器
            logWriter.close();

            return exitCode;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
