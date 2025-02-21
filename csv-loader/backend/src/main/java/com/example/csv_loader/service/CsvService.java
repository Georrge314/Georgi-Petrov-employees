package com.example.csv_loader.service;

import com.example.csv_loader.model.EmployeePairs;
import com.example.csv_loader.model.EmployeeWork;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class CsvService {
    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            // Standard international formats
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),        // 2024-02-19
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),        // 19/02/2024
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),        // 02/19/2024
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),        // 2024/02/19
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),        // 19-02-2024
            DateTimeFormatter.ofPattern("MM-dd-yyyy")         // 02-19-2024
    );

    //should be able to handle csv with or without header
    public List<EmployeePairs> processCsv(MultipartFile file) {
        List<EmployeeWork> employeeWorkList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setIgnoreSurroundingSpaces(true).build();
            CSVParser csvParser = new CSVParser(reader, csvFormat);

            for (CSVRecord record : csvParser) {
                long employeeId = Long.parseLong(record.get(0));
                long projectId = Long.parseLong(record.get(1));
                LocalDate startDate = parseDate(record.get(2));
                LocalDate endDate = checkEndDate(record.get(3)) ? LocalDate.now() : parseDate(record.get(3));

                EmployeeWork employeeWork = new EmployeeWork(employeeId, projectId, startDate, endDate);
                employeeWorkList.add(employeeWork);
            }

        } catch (Exception exception) {
            throw new RuntimeException("Error reading CSV file", exception);
        }

        return findEmployeePairs(employeeWorkList);
    }

    private LocalDate parseDate(String dateStr) {
        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new IllegalArgumentException("Invalid date format: " + dateStr);
    }

    private boolean checkEndDate(String dateStr) {
        return dateStr.equalsIgnoreCase("null") || dateStr.trim().isEmpty();
    }

    private List<EmployeePairs> findEmployeePairs(List<EmployeeWork> employeeWorkList) {
        //Group employees by project
        Map<Long, List<EmployeeWork>> projectEmployees = new HashMap<>();
        for (EmployeeWork record : employeeWorkList) {
            projectEmployees.computeIfAbsent(record.getProjectId(), k -> new ArrayList<>()).add(record);
        }

        //Calculate overlaps for all employee pairs, but track by projectId
        Map<String, EmployeePairs> employeeProjectPairs = new HashMap<>();

        for (Map.Entry<Long, List<EmployeeWork>> entry : projectEmployees.entrySet()) {
            long projectId = entry.getKey();
            List<EmployeeWork> employees = entry.getValue();

            for (int i = 0; i < employees.size(); i++) {
                for (int j = i + 1; j < employees.size(); j++) {
                    EmployeeWork e1 = employees.get(i);
                    EmployeeWork e2 = employees.get(j);

                    // Compute overlap
                    long overlapDays = calculateOverlapDays(e1, e2);
                    if (overlapDays > 0) {
                        long emp1 = Math.min(e1.getEmployeeId(), e2.getEmployeeId());
                        long emp2 = Math.max(e1.getEmployeeId(), e2.getEmployeeId());
                        String key = emp1 + "-" + emp2 + "-" + projectId;

                        employeeProjectPairs.put(key, new EmployeePairs(emp1, emp2, projectId, overlapDays));
                    }
                }
            }
        }

        //Find the employee pair with the highest single-project overlap
        EmployeePairs topPair = employeeProjectPairs.values()
                .stream()
                .max(Comparator.comparingLong(EmployeePairs::getDays))
                .orElse(null);

        if (topPair == null) {
            return Collections.emptyList();
        }

        long topEmp1 = topPair.getEmployeeId1();
        long topEmp2 = topPair.getEmployeeId2();

        //Filter only projects where the top two employees worked together
        List<EmployeePairs> result = new ArrayList<>();
        for (EmployeePairs pair : employeeProjectPairs.values()) {
            if ((pair.getEmployeeId1() == topEmp1 && pair.getEmployeeId2() == topEmp2)) {
                result.add(pair);
            }
        }

        //Sort the final result by daysWorkedTogether in descending order
        result.sort((a, b) -> Long.compare(b.getDays(), a.getDays()));

        return result;
    }

    private long calculateOverlapDays(EmployeeWork emp1, EmployeeWork emp2) {
        LocalDate startMax = emp1.getStartDate().isAfter(emp2.getStartDate()) ? emp1.getStartDate() : emp2.getStartDate();
        LocalDate endMin = emp1.getEndDate().isBefore(emp2.getEndDate()) ? emp1.getEndDate() : emp2.getEndDate();

        if (startMax.isAfter(endMin)) {
            return 0; // No overlap
        }

        long totalDays = ChronoUnit.DAYS.between(startMax, endMin) + 1;
        long weekends = countWeekends(startMax, endMin);

        //return only working days
        return totalDays - weekends;
    }

    private long countWeekends(LocalDate start, LocalDate end) {
        long weekends = 0;
        LocalDate current = start;

        while (!current.isAfter(end)) {
            DayOfWeek day = current.getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                weekends++;
            }
            current = current.plusDays(1);
        }

        return weekends;
    }

}
