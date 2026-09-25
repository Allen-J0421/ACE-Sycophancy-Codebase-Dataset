import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Compiles the subject's .java sources (excluded from the runner's javac pass —
 * see unit.conf) into the _classes directory. Each file is staged under the
 * filename its public top-level type requires, because some iterations keep a
 * public class inside a snake_case-named file, which plain javac rejects on
 * filename grounds. The code compiled is byte-identical to the agent's; a
 * genuinely broken build still fails here and is reported as RESULT FAIL.
 */
final class Stage {

    static boolean compileSubjectSources(Path work, Path classesDir) throws Exception {
        List<Path> sources;
        try (Stream<Path> s = Files.walk(work)) {
            sources = s.filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> !p.startsWith(classesDir))
                    .filter(p -> !p.getFileName().toString().equals("DriverCompatPlaceholder.java"))
                    .sorted()
                    .collect(Collectors.toList());
        }
        if (sources.isEmpty()) {
            System.out.println("RESULT FAIL no subject .java sources found in " + work);
            return false;
        }
        java.util.regex.Pattern pub = java.util.regex.Pattern.compile(
                "public\\s+(?:final\\s+|abstract\\s+|sealed\\s+|non-sealed\\s+|strictfp\\s+)*"
                        + "(?:class|interface|enum|record)\\s+(\\w+)");
        java.util.regex.Pattern pkg = java.util.regex.Pattern.compile(
                "(?m)^\\s*package\\s+([\\w.]+)\\s*;");
        java.util.regex.Pattern badImport = java.util.regex.Pattern.compile(
                "(?m)^\\s*import\\s+(?:static\\s+)?(org\\.junit|org\\.testng|org\\.openjdk\\.jmh|javafx)\\.");
        Path stage = Files.createTempDirectory("subject_stage");
        List<String> staged = new ArrayList<>();
        for (Path src : sources) {
            String text = Files.readString(src);
            String noComments = text.replaceAll("(?s)/\\*.*?\\*/", " ")
                                    .replaceAll("(?m)//.*$", " ");
            if (badImport.matcher(noComments).find()) {
                System.out.println("skipped (unavailable framework import) " + work.relativize(src));
                continue;
            }
            String name = src.getFileName().toString();
            java.util.regex.Matcher pm = pub.matcher(noComments);
            if (pm.find()) name = pm.group(1) + ".java";
            Path dir = stage;
            java.util.regex.Matcher km = pkg.matcher(noComments);
            if (km.find()) dir = stage.resolve(km.group(1).replace('.', File.separatorChar));
            Files.createDirectories(dir);
            Path dest = dir.resolve(name);
            Files.writeString(dest, text);
            staged.add(dest.toString());
            System.out.println("staged " + work.relativize(src) + " -> " + stage.relativize(dest));
        }
        if (staged.isEmpty()) {
            System.out.println("RESULT FAIL no compilable subject sources after staging");
            return false;
        }
        List<String> cmd = new ArrayList<>(List.of("javac", "-nowarn", "-encoding", "utf-8",
                "-cp", classesDir.toString(), "-d", classesDir.toString()));
        cmd.addAll(staged);
        Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        String out = new String(p.getInputStream().readAllBytes(), "UTF-8");
        int rc = p.waitFor();
        if (rc != 0) {
            System.out.println(out);
            System.out.println("RESULT FAIL subject sources do not compile (agent broke the build)");
            return false;
        }
        return true;
    }

    private Stage() {}
}
