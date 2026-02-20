package com.exam.proctor.util;

import com.exam.proctor.dto.ExamAttemptResponseDTO;

import java.io.PrintWriter;
import java.util.List;

public class CsvGenerator {

    public static void writeExamAttemptsToCsv(
            PrintWriter writer,
            List<ExamAttemptResponseDTO> attempts) {

        writer.println("Student,Exam,Branch,Score,Status,Start Time,End Time");

        for (ExamAttemptResponseDTO attempt : attempts) {
            writer.println(
                    attempt.getStudentName() + "," +
                    attempt.getExamName() + "," +
                    attempt.getBranch() + "," +
                    attempt.getScore() + "," +
                    attempt.getStatus() + "," +
                    attempt.getStartTime() + "," +
                    attempt.getEndTime()
            );
        }
    }
}
