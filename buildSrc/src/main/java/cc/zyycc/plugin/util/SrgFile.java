package cc.zyycc.plugin.util;

public class resolveSrgFile {
    private File resolveSrgFile(Project project, String taskName) {
        Logger logger = project.getLogger();
        if (!project.getTasks().hasTask(taskName)) {
            logger.lifecycle("Task '{}' not found", taskName);
            return null;
        }

        Task task = project.getTasks().getByName(taskName);
        String clsName = task.getClass().getName();
        logger.lifecycle("Task '{}' is instance of {}", taskName, clsName);

        // 1) If it's ExtractMCPData (old FG), use its API (safe check)
        try {
            Class<?> extractCls = Class.forName("net.minecraftforge.gradle.common.tasks.ExtractMCPData");
            if (extractCls.isInstance(task)) {
                // try to call getOutput() or getOutputFile() reflectively to avoid compile-time dependency
                try {
                    Method getOutput = task.getClass().getMethod("getOutput");
                    Object out = getOutput.invoke(task);
                    // Many FG tasks return a RegularFileProperty or Provider<RegularFile>
                    if (out instanceof RegularFileProperty) {
                        RegularFileProperty rfp = (RegularFileProperty) out;
                        File f = rfp.getAsFile().get();
                        logger.lifecycle("Resolved SRG via ExtractMCPData.getOutput(): {}", f);
                        return f;
                    } else if (out instanceof Provider) {
                        Object v = ((Provider<?>) out).get();
                        if (v instanceof File) return (File) v;
                        // sometimes it's a Path or String
                        return new File(String.valueOf(v));
                    }
                } catch (NoSuchMethodException ns) {
                    // fallthrough to other strategies
                }
            }
        } catch (ClassNotFoundException ignored) {
            // old class not on classpath -> not old FG
        } catch (InvocationTargetException|IllegalAccessException e) {
            logger.warn("Failed to invoke ExtractMCPData.getOutput reflectively: {}", e.toString());
        }

        // 2) Try common method names via reflection: getSrg, getOutput, getOutputFile, getSrgFile
        String[] tryMethodNames = {"getSrg", "getOutput", "getOutputFile", "getOutputSrg", "getOutputFileProperty"};
        for (String mname : tryMethodNames) {
            try {
                Method m = task.getClass().getMethod(mname);
                Object out = m.invoke(task);
                if (out == null) continue;
                // handle Provider, RegularFileProperty, File, String, Path
                if (out instanceof RegularFileProperty) {
                    File f = ((RegularFileProperty) out).getAsFile().get();
                    logger.lifecycle("Resolved SRG via {}(): {}", mname, f);
                    return f;
                }
                if (out instanceof Provider) {
                    Object v = ((Provider<?>) out).get();
                    if (v instanceof File) return (File) v;
                    return new File(String.valueOf(v));
                }
                if (out instanceof File) return (File) out;
                if (out instanceof CharSequence) return new File(out.toString());
            } catch (NoSuchMethodException ignored) {
                // skip
            } catch (InvocationTargetException | IllegalAccessException e) {
                logger.warn("Reflection call {}() on {} failed: {}", mname, clsName, e.toString());
            }
        }

        // 3) Fallback: check task outputs for any .srg / .tsrg file
        try {
            Set<File> outputs = task.getOutputs().getFiles().getFiles();
            for (File f : outputs) {
                if (f == null) continue;
                String name = f.getName().toLowerCase();
                if (name.endsWith(".srg") || name.endsWith(".tsrg") || name.contains("srg") || name.contains("tsrg")) {
                    logger.lifecycle("Resolved SRG via task outputs: {}", f.getAbsolutePath());
                    return f;
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to inspect outputs of {}: {}", taskName, e.toString());
        }

        // 4) Last resort: try reading public fields (reflectively) named srg/output/outputFile
        Field[] fields = task.getClass().getDeclaredFields();
        for (Field f : fields) {
            String fname = f.getName().toLowerCase();
            if (fname.contains("srg") || fname.contains("output")) {
                f.setAccessible(true);
                try {
                    Object val = f.get(task);
                    if (val == null) continue;
                    if (val instanceof RegularFileProperty) {
                        File rf = ((RegularFileProperty) val).getAsFile().get();
                        logger.lifecycle("Resolved SRG via field {}: {}", f.getName(), rf);
                        return rf;
                    }
                    if (val instanceof Provider) {
                        Object v = ((Provider<?>) val).get();
                        if (v instanceof File) return (File) v;
                        return new File(String.valueOf(v));
                    }
                    if (val instanceof File) return (File) val;
                    if (val instanceof CharSequence) return new File(val.toString());
                } catch (IllegalAccessException iae) {
                    // ignore
                } catch (Exception ex) {
                    logger.warn("Error reading field {}: {}", f.getName(), ex.toString());
                }
            }
        }

        logger.lifecycle("Could not resolve SRG/TSRG file for task '{}'. Please check the task type and outputs.", taskName);
        return null;
    }
}
