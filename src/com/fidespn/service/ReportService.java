package com.fidespn.service;

import com.fidespn.model.Match;
import com.fidespn.model.MatchEvent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Exports simple PDF reports without external dependencies.
 * Generates a single-page PDF with basic text content.
 */
public class ReportService {

    public void exportMatchReport(Match match, File file) throws IOException {
        if (match == null) throw new IllegalArgumentException("Match no puede ser null");

        List<String> lines = new ArrayList<>();
        lines.add("Reporte de Partido");
        lines.add(" ");

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy | hh:mm a");
        lines.add("Partido: " + match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName());
        lines.add("Fecha/Hora: " + sdf.format(match.getDate()) + " " + match.getTime());
        lines.add("Estado: " + match.getStatus());
        lines.add("Marcador: " + match.getScoreHome() + " - " + match.getScoreAway());
        lines.add(" ");

        StatisticsService statsService = new StatisticsService();
        StatisticsService.MatchStats stats = statsService.computeMatchStats(match);
        lines.add("Estadísticas");
        lines.add("Eventos: " + stats.totalEvents);
        lines.add("Goles: " + stats.goals);
        lines.add("Tarjetas Amarillas: " + stats.yellowCards);
        lines.add("Tarjetas Rojas: " + stats.redCards);
        lines.add("Sustituciones: " + stats.substitutions);
        lines.add(" ");

        lines.add("Eventos del Partido");
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
        int idx = 1;
        for (MatchEvent ev : match.getEvents()) {
            String line = String.format("%02d. [%s] %s - %s (min %d)", idx++, tf.format(ev.getTimestamp()), ev.getType(), ev.getDescription(), ev.getMinute());
            // Keep ASCII-only fallback to avoid font encoding issues
            lines.add(line.replace("ñ", "n"));
        }

        PdfSimpleWriter.writeSimplePdf(file, lines);
    }

    /**
     * Minimal PDF writer to create a one-page text-based PDF.
     */
    static class PdfSimpleWriter {
        private static final int PAGE_WIDTH = 595;
        private static final int PAGE_HEIGHT = 842;

        static void writeSimplePdf(File file, List<String> lines) throws IOException {
            String contentStream = buildContentStream(lines);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            List<Integer> xref = new ArrayList<>();

            // Helper to write and track offset
            java.util.function.Consumer<String> write = s -> {
                try {
                    baos.write(s.getBytes(StandardCharsets.ISO_8859_1));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };

            // PDF Header
            write.accept("%PDF-1.4\n");

            // 1 0 obj - Catalog
            xref.add(baos.size());
            write.accept("1 0 obj\n");
            write.accept("<< /Type /Catalog /Pages 2 0 R >>\n");
            write.accept("endobj\n");

            // 2 0 obj - Pages
            xref.add(baos.size());
            write.accept("2 0 obj\n");
            write.accept("<< /Type /Pages /Kids [3 0 R] /Count 1 >>\n");
            write.accept("endobj\n");

            // 3 0 obj - Page
            xref.add(baos.size());
            write.accept("3 0 obj\n");
            write.accept("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + PAGE_WIDTH + " " + PAGE_HEIGHT + "] ");
            write.accept("/Resources << /Font << /F1 5 0 R >> >> /Contents 4 0 R >>\n");
            write.accept("endobj\n");

            // 4 0 obj - Contents (stream)
            byte[] csBytes = contentStream.getBytes(StandardCharsets.ISO_8859_1);
            xref.add(baos.size());
            write.accept("4 0 obj\n");
            write.accept("<< /Length " + csBytes.length + " >>\n");
            write.accept("stream\n");
            baos.write(csBytes);
            write.accept("\nendstream\n");
            write.accept("endobj\n");

            // 5 0 obj - Font
            xref.add(baos.size());
            write.accept("5 0 obj\n");
            write.accept("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\n");
            write.accept("endobj\n");

            // xref table
            int xrefStart = baos.size();
            write.accept("xref\n");
            write.accept("0 " + (xref.size() + 1) + "\n");
            write.accept(String.format("%010d %05d f \n", 0, 65535));
            for (Integer off : xref) {
                write.accept(String.format("%010d %05d n \n", off, 0));
            }

            // trailer
            write.accept("trailer\n");
            write.accept("<< /Size " + (xref.size() + 1) + " /Root 1 0 R >>\n");
            write.accept("startxref\n");
            write.accept(String.valueOf(xrefStart) + "\n");
            write.accept("%%EOF");

            try (FileOutputStream fos = new FileOutputStream(file)) {
                baos.writeTo(fos);
            }
        }

        private static String buildContentStream(List<String> lines) {
            StringBuilder sb = new StringBuilder();
            // Begin text object
            sb.append("BT\n");
            sb.append("/F1 12 Tf\n");
            sb.append("14 TL\n"); // leading
            // Move to start (x=50, y=792)
            sb.append("1 0 0 1 50 792 Tm\n");
            for (String raw : lines) {
                String line = escapePdfText(raw);
                sb.append("(").append(line).append(") Tj\nT*\n");
            }
            sb.append("ET\n");
            return sb.toString();
        }

        private static String escapePdfText(String s) {
            if (s == null) return "";
            String out = s.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
            // Basic ASCII fallback to minimize encoding issues
            out = out.replaceAll("[^\\x09\\x0A\\x0D\\x20-\\x7E]", "?");
            return out;
        }
    }
}


