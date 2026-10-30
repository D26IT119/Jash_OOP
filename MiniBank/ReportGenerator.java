import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import model.TransactionType;

public final class ReportGenerator {
    private ReportGenerator() {
    }

    public static String generate(Path logsFolder, Path reportFile) throws IOException {
        long totalDeposits = 0;
        long totalWithdrawals = 0;
        List<String> fileDetails = new ArrayList<>();
        List<Path> logFiles = new ArrayList<>();

        try (DirectoryStream<Path> entries = Files.newDirectoryStream(logsFolder)) {
            for (Path entry : entries) {
                if (Files.isRegularFile(entry)) {
                    logFiles.add(entry);
                }
            }
        }
        logFiles.sort(Comparator.comparing(path -> path.getFileName().toString(), String.CASE_INSENSITIVE_ORDER));

        for (Path logFile : logFiles) {
            BasicFileAttributes attributes = Files.readAttributes(logFile, BasicFileAttributes.class);
            fileDetails.add(String.format(
                    "%s | size: %d bytes | last modified: %s",
                    logFile.getFileName(),
                    attributes.size(),
                    attributes.lastModifiedTime()
            ));

            try (BufferedReader reader = Files.newBufferedReader(logFile, StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmedLine = line.trim();
                    if (trimmedLine.isEmpty()) {
                        continue;
                    }

                    String[] fields = trimmedLine.split(",", -1);
                    if (fields.length != 2) {
                        throw new IOException("Invalid transaction log entry in " + logFile + ": " + line);
                    }

                    String typeValue = fields[0].trim();
                    String amountValue = fields[1].trim();

                    final long amount;
                    try {
                        amount = Long.parseLong(amountValue);
                    } catch (NumberFormatException exception) {
                        throw new IOException("Invalid transaction amount in " + logFile + ": " + line, exception);
                    }
                    if (amount < 0) {
                        throw new IOException("Transaction amounts cannot be negative in " + logFile + ": " + line);
                    }

                    final TransactionType type;
                    try {
                        type = TransactionType.valueOf(typeValue.toUpperCase(Locale.ROOT));
                    } catch (IllegalArgumentException exception) {
                        throw new IOException("Unknown transaction type in " + logFile + ": " + line, exception);
                    }

                    switch (type) {
                        case DEPOSIT -> totalDeposits += amount;
                        case WITHDRAW -> totalWithdrawals += amount;
                        case TRANSFER -> totalWithdrawals += amount;
                        default -> throw new IOException("Unknown transaction type in " + logFile + ": " + line);
                    }
                }
            }
        }

        StringBuilder report = new StringBuilder()
                .append("End-of-Day Transaction Summary").append(System.lineSeparator())
                .append("Total deposits: ").append(totalDeposits).append(System.lineSeparator())
                .append("Total withdrawals: ").append(totalWithdrawals).append(System.lineSeparator())
                .append("Log files:").append(System.lineSeparator());
        for (String details : fileDetails) {
            report.append(details).append(System.lineSeparator());
        }

        Path parent = reportFile.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        String reportText = report.toString();
        Files.writeString(reportFile, reportText, StandardCharsets.UTF_8);
        return reportText;
    }
}
