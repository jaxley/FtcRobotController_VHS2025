package org.firstinspires.ftc.teamcode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InitializeAutonomousFiles {

    // Public wrapper to make the engine usable from your OpModes and clear IDE "never used" warnings
    public static List<String> parseAndValidateArray(String arrayJson) throws IOException {
        return extractObjects(arrayJson);
    }

    public static void validateFileStructure(String json, String fileName) throws IOException {
        validateJsonObject(json, fileName);
    }

    public static String processClassName(String input) {
        return sanitizeClassName(input);
    }

    public static String loadPathFile(File file) throws IOException {
        return readUtf8(file);
    }

    public static void saveFile(File target, String content) throws IOException {
        writeAtomically(target, content);
    }

    private static List<String> extractObjects(String array) throws IOException {
        List<String> list = new ArrayList<>();
        if (array == null) return list;
        String s = array.trim();
        if (!s.startsWith("[") || !s.endsWith("]")) {
            throw new IOException("Invalid JSON array structure.");
        }
        s = s.substring(1, s.length() - 1).trim();
        if (s.isEmpty()) return list;

        int braceCount = 0;
        int bracketCount = 0;
        boolean inString = false;
        int start = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (inString) {
                if (c == '\\') {
                    i++;
                } else if (c == '"') {
                    inString = false;
                }
            } else {
                if (c == '"') {
                    inString = true;
                } else if (c == '{') {
                    if (braceCount == 0 && bracketCount == 0) start = i;
                    braceCount++;
                } else if (c == '}') {
                    braceCount--;
                    if (braceCount < 0) throw new IOException("Mismatched closing brace at character " + i);
                    if (braceCount == 0 && bracketCount == 0) {
                        list.add(s.substring(start, i + 1));
                    }
                } else if (c == '[') {
                    bracketCount++;
                } else if (c == ']') {
                    bracketCount--;
                    if (bracketCount < 0) throw new IOException("Mismatched closing bracket at character " + i);
                }
            }
        }
        if (braceCount != 0 || bracketCount != 0 || inString) {
            throw new IOException("Unclosed JSON structure within array.");
        }
        return list;
    }

    public static double requiredNumber(String json, String key, String description) throws IOException {
        // FIXED: Removed redundant backslash escaping inside character classes to clean up IDE warnings
        Matcher m = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(-?(?:\\d+(?:\\.\\d*)?|\\.\\d+)(?:[eE][+-]?\\d+)?)").matcher(json);
        if (!m.find()) throw new IOException("Missing numeric value " + description);
        String group = m.group(1);
        if (group == null) throw new IOException("Missing numeric payload matching group for " + description);
        try {
            return Double.parseDouble(group);
        } catch (NumberFormatException e) {
            throw new IOException("Invalid number for " + description, e);
        }
    }

    public static String stringValue(String json, String key) {
        // FIXED: Removed redundant backslash escapes from the JSON key regex matching block
        Matcher m = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"").matcher(json);
        if (m.find()) {
            String val = m.group(1);
            // FIXED: Added defensive null-check guard preventing NullPointerExceptions
            return val != null ? val.replace("\\\"", "\"").replace("\\\\", "\\") : null;
        }
        return null;
    }

    private static void validateJsonObject(String json, String fileName) throws IOException {
        String trimmed = json.trim();
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}"))
            throw new IOException(fileName + " is not a JSON object.");
        findStructureForKey(json, "startPoint", '{', '}');
        findStructureForKey(json, "lines", '[', ']');
        findStructureForKey(json, "sequence", '[', ']');
    }

    private static void findStructureForKey(String json, String key, char openSymbol, char closeSymbol) throws IOException {
        String symbolRegex = openSymbol == '{' ? "\\{" : "\\[";
        Matcher m = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*" + symbolRegex).matcher(json);
        if (!m.find()) {
            throw new IOException("Missing required structural key: " + key);
        }
        int start = m.end() - 1;
        int depthCount = 0;
        boolean inString = false;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (inString) {
                if (c == '\\') i++;
                else if (c == '"') inString = false;
            } else {
                if (c == '"') inString = true;
                else if (c == openSymbol) depthCount++;
                else if (c == closeSymbol) {
                    depthCount--;
                    if (depthCount == 0) return; // Closed cleanly
                }
            }
        }
        throw new IOException("Unclosed JSON structure starting at character " + start);
    }

    private static String sanitizeClassName(String input) {
        String clean = input.replaceAll("[^A-Za-z0-9_$]", "_");
        if (clean.isEmpty() || !Character.isJavaIdentifierStart(clean.charAt(0)))
            clean = "Auto_" + clean;

        // FIXED: Converted enhanced switch down to standard Java 8 statement structures
        switch (clean) {
            case "abstract": case "assert": case "boolean": case "break": case "byte": case "case":
            case "catch": case "char": case "class": case "const": case "continue": case "default":
            case "do": case "double": case "else": case "enum": case "extends": case "final":
            case "finally": case "float": case "for": case "goto": case "if": case "implements":
            case "import": case "instanceof": case "int": case "interface": case "long": case "native":
            case "new": case "package": case "private": case "protected": case "public": case "return":
            case "short": case "static": case "strictfp": case "super": case "switch": case "synchronized":
            case "this": case "throw": case "throws": case "transient": case "try": case "void":
            case "volatile": case "while": case "true": case "false": case "null": case "_":
                return "Auto_" + clean;
            default:
                return clean;
        }
    }

    public static String removeExtension(String name) {
        return name.replaceFirst("(?i)\\.pp$", "");
    }

    public static String number(double value) {
        return Double.toString(value);
    }

    public static String javaStringLiteral(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t");
    }

    // FIXED: Reverted back to Java 8 compatible buffer stream reader ensuring API 24 support
    private static String readUtf8(File file) throws IOException {
        FileInputStream input = new FileInputStream(file);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = input.read(buffer)) != -1) {
                output.write(buffer, 0, count);
            }
        } finally {
            input.close();
        }
        return new String(output.toByteArray(), "UTF-8");
    }

    // FIXED: Atomic file write architecture optimized strictly for old Android API levels
    private static void writeAtomically(File target, String content) throws IOException {
        File parent = target.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Failed to create layout directories for target path: " + target.getAbsolutePath());
        }

        File temp = new File(parent, target.getName() + ".tmp");
        File backup = new File(parent, target.getName() + ".bak");

        FileOutputStream output = new FileOutputStream(temp);
        try {
            output.write(content.getBytes("UTF-8"));
            output.flush();
            output.getFD().sync(); // Hard sync on hardware disk to prevent corruption during robot disconnects
        } finally {
            output.close();
        }

        boolean hadOriginal = target.exists();
        if (backup.exists() && !backup.delete()) {
            temp.delete();
            throw new IOException("Could not remove old backup file: " + backup.getAbsolutePath());
        }
        if (hadOriginal && !target.renameTo(backup)) {
            temp.delete();
            throw new IOException("Could not preserve existing output before replacing it: " + target.getAbsolutePath());
        }
        if (!temp.renameTo(target)) {
            if (hadOriginal) target.createNewFile(); // Attempt structural recovery if execution drops mid-move
            temp.delete();
            throw new IOException("Could not move generated file into place: " + target.getAbsolutePath());
        }
        if (backup.exists()) backup.delete();
    }
}
