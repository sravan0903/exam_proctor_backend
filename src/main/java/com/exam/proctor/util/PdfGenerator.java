package com.exam.proctor.util;

import com.exam.proctor.dto.ExamAttemptResponseDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.OutputStream;
import java.util.List;

public class PdfGenerator {

    public static void writeExamAttemptsToPdf(
            OutputStream outputStream,
            List<ExamAttemptResponseDTO> attempts) throws Exception {

        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);

        document.open();
        document.add(new Paragraph("Exam Results Report"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);

        table.addCell("Student");
        table.addCell("Exam");
        table.addCell("Branch");
        table.addCell("Score");
        table.addCell("Status");
        table.addCell("Start Time");

        for (ExamAttemptResponseDTO attempt : attempts) {
            table.addCell(attempt.getStudentName());
            table.addCell(attempt.getExamName());
            table.addCell(attempt.getBranch());
            table.addCell(String.valueOf(attempt.getScore()));
            table.addCell(attempt.getStatus().toString());
            table.addCell(
                attempt.getStartTime() != null
                    ? attempt.getStartTime().toString()
                    : "-"
            );
        }

        document.add(table);
        document.close();
    }
}
