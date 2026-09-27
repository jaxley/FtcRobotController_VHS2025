package org.firstinspires.ftc.teamcode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("SpellCheckingInspection")
public class InitializeAutonomousFiles {

    // FIXED: Adjusted base folder layouts to target the TeamCode module structure cleanly
    private static final String DROP_DIR = "TeamCode/src/main/java/org/firstinspires/ftc/teamcode/filedrop";
    private static final String DUMP_DIR = "TeamCode/src/main/java/org/firstinspires/ftc/teamcode/filedump";

    public static void main(String[] args) {
        System.out.println("Starting Native Java Multi-File Generation System for Pedro 3...");
        generateOpModes();
    }

    public static void generateOpModes() {
        try {
            String rootPath = new File("").getAbsolutePath();

            // Standardize path structures using environment safe separators
            File dropFolder = new File(rootPath, DROP_DIR.replace("/", File.separator));
            File dumpFolder = new File(rootPath, DUMP_DIR.replace("/", File.separator));

            // CRITICAL TERMINAL MONITOR REGISTRATION:
            System.out.println("=========================================================");
            System.out.println("[TARGET DROP DIRECTORY]: " + dropFolder.getAbsolutePath());
            System.out.println("[TARGET DUMP DIRECTORY]: " + dumpFolder.getAbsolutePath());
            System.out.println("=========================================================");

            if (!dropFolder.exists()) {
                if (dropFolder.mkdirs()) {
                    System.out.println("--> Success: Created filedrop folder. Place your .pp files inside: " + dropFolder.getAbsolutePath());
                } else {
                    System.out.println("--> Error: System was unable to create target drop path structural layers.");
                }
                return;
            }

            if (!dumpFolder.exists() && !dumpFolder.mkdirs()) {
                System.out.println("--> Warning: System was unable to auto-verify creation patterns for filedump module directory maps.");
            }

            File[] files = dropFolder.listFiles();
            if (files == null || files.length == 0) {
                System.out.println("--> Notice: No path configuration data (.pp) discovered inside: " + dropFolder.getAbsolutePath());
                return;
            }

            File[] oldFiles = dumpFolder.listFiles();
            if (oldFiles != null) {
                for (File f : oldFiles) {
                    if (f.getName().endsWith(".java")) {
                        f.delete();
                    }
                }
            }

            int generatedCount = 0;
            for (File file : files) {
                if (file.getName().endsWith(".pp")) {
                    String rawName = file.getName().replace(".pp", "");
                    String cleanJavaName = sanitizeClassName(rawName);

                    System.out.println(" -> Compiling Trajectories For: " + file.getName());
                    compilePath(file, dumpFolder, cleanJavaName, "RED");
                    compilePath(file, dumpFolder, cleanJavaName, "BLUE");
                    generatedCount += 2;
                }
            }
            System.out.println("=========================================================");
            System.out.println("BUILD SUCCESSFUL: Generated " + generatedCount + " autonomous files inside the dump folder!");
            System.out.println("=========================================================");

        } catch (Exception e) {
            System.out.println("Critical Generation Failure: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String sanitizeClassName(String input) {
        String clean = input.replaceAll("[\\s.-_]", "");
        if (!clean.isEmpty() && Character.isDigit(clean.charAt(0))) {
            clean = "Auto" + clean;
        }
        return clean;
    }

    private static void compilePath(File ppFile, File targetDir, String cleanJavaName, String alliance) throws IOException {
        String className = cleanJavaName + "_" + alliance;
        File javaFile = new File(targetDir, className + ".java");

        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(ppFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line.trim());
            }
        }
        String json = contentBuilder.toString();

        double startX = getJsonValue(json, "\"startPoint\"\\s*:\\s*\\{[^}]*\"x\"\\s*:\\s*([\\d.-]+)");
        double startY = getJsonValue(json, "\"startPoint\"\\s*:\\s*\\{[^}]*\"y\"\\s*:\\s*([\\d.-]+)");
        double startH = getJsonValue(json, "\"startPoint\"\\s*:\\s*\\{[^}]*\"headingDeg\"\\s*:\\s*([\\d.-]+)");

        StringBuilder pathSegmentsCode = new StringBuilder();

        double currentX = startX;
        double currentY = startY;
        double currentH = startH;

        Pattern lineBlockPattern = Pattern.compile("\\{\\s*\"id\"\\s*:\\s*\"line-[^\"]+\".*?\"heading\"\\s*:\\s*\\{[^}]*\\}\\s*\\}");
        Matcher lineMatcher = lineBlockPattern.matcher(json);

        boolean firstSegment = true;
        while (lineMatcher.find()) {
            String lineBlock = lineMatcher.group();

            double endX = getJsonValue(lineBlock, "\"endPoint\"\\s*:\\s*\\{[^}]*\"x\"\\s*:\\s*([\\d.-]+)");
            double endY = getJsonValue(lineBlock, "\"endPoint\"\\s*:\\s*\\{[^}]*\"y\"\\s*:\\s*([\\d.-]+)");
            double endDeg = getJsonValue(lineBlock, "\"endDeg\"\\s*:\\s*([\\d.-]+)");

            if (!firstSegment) {
                pathSegmentsCode.append(",\n");
            }

            if (lineBlock.contains("\"controlPoints\"\\s*:\\s*\\[\\s*\\{")) {
                double controlX = getJsonValue(lineBlock, "\"controlPoints\"\\s*:\\s*\\[\\s*\\{[^}]*\"x\"\\s*:\\s*([\\d.-]+)");
                double controlY = getJsonValue(lineBlock, "\"controlPoints\"\\s*:\\s*\\[\\s*\\{[^}]*\"y\"\\s*:\\s*([\\d.-]+)");

                pathSegmentsCode.append("            Paths.curve(p.of(")
                        .append(currentX).append(", ").append(currentY).append(", ").append(currentH).append("), p.of(")
                        .append(controlX).append(", ").append(controlY).append("), p.of(")
                        .append(endX).append(", ").append(endY).append(", ").append(endDeg).append("))");
            } else {
                pathSegmentsCode.append("            Paths.line(p.of(")
                        .append(currentX).append(", ").append(currentY).append(", ").append(currentH).append("), p.of(")
                        .append(endX).append(", ").append(endY).append(", ").append(endDeg).append("))");
            }

            currentX = endX;
            currentY = endY;
            currentH = endDeg;
            firstSegment = false;
        }

        if (firstSegment) {
            pathSegmentsCode.append("            Paths.line(p.of(")
                    .append(startX).append(", ").append(startY).append(", ").append(startH).append("), p.of(")
                    .append(startX).append(", ").append(startY).append(", ").append(startH).append("))");
        }

        String sourceCode = "package org.firstinspires.ftc.teamcode.filedump;\n\n" +
                "import com.qualcomm.robotcore.eventloop.opmode.Autonomous;\n" +
                "import com.pedropathing.follower.Follower;\n" +
                "import com.pedropathing.math.Pose;\n" +
                "import com.pedropathing.paths.Path;\n" +
                "import com.pedropathing.api.PoseFactory;\n" +
                "import com.pedropathing.api.Paths;\n" +
                "import org.firstinspires.ftc.teamcode.AutonomousOpMode;\n" +
                "import org.firstinspires.ftc.teamcode.pedroPathing.Constants;\n" +
                "import org.firstinspires.ftc.teamcode.pedroPathing.Alliance;\n\n" +
                "@Autonomous(name = \"Auto: " + ppFile.getName().replace(".pp", "") + " (" + alliance + ")\", group = \"PedroGenerated\")\n" +
                "public class " + className + " extends AutonomousOpMode {\n\n" +
                "    private PoseFactory p;\n" +
                "    private Follower follower;\n\n" +
                "    public " + className + "() {\n" +
                "        super(Alliance." + alliance + ");\n" +
                "    }\n\n" +
                "    @Override\n" +
                "    public void init() {\n" +
                "        p = PoseFactory.degrees();\n" +
                "        if (Alliance.RED == Alliance." + alliance + ") {\n" +
                "            p = p.mirrorX(70.75);\n" +
                "        }\n\n" +
                "        follower = Constants.createFollower(hardwareMap);\n" +
                "        Pose startPose = p.of(" + startX + ", " + startY + ", " + startH + ");\n" +
                "        this.follower = follower;\n" +
                "        follower.setPose(startPose);\n\n" +
                "        autoPaths = Paths.path(\n" +
                pathSegmentsCode.toString() + "\n" +
                "        );\n" +
                "        super.init();\n" +
                "    }\n" +
                "}\n";

        try (FileWriter writer = new FileWriter(javaFile)) {
            writer.write(sourceCode);
            System.out.println("    -> Wrote file: " + javaFile.getName());
        }
    }

    private static double getJsonValue(String rawJson, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(rawJson);
        if (matcher.find()) {
            try
            {
                return Double.parseDouble(matcher.group(1));
            }
            catch (Exception e)
            {
                return 0.0;
            }
        }
        return 0.0;
    }
}