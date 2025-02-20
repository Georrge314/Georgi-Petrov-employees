package com.example.csv_loader.controller;

import com.example.csv_loader.model.EmployeePairs;
import com.example.csv_loader.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/csv")
@CrossOrigin(origins = "http://localhost:4200")
public class CsvController {
    @Autowired
    private CsvService csvService;

    @PostMapping("/process")
    public ResponseEntity<List<EmployeePairs>> processCsvFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        List<EmployeePairs> result = csvService.processCsv(file);

        return ResponseEntity.ok(result);
    }
}
