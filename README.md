# Employee Pair Analysis

## Overview
This project identifies **pairs of employees** who worked together on common projects for the **longest period of time**.  
It processes CSV files containing employee work history and displays the results in a structured table.

## Technologies Used
- **Frontend**: Angular (`@angular/core` ^18.2.0)
- **Backend**: Java 17, Spring Boot 3.3.8

## Features
✅ Upload CSV via file input or drag-and-drop  
✅ File history to track previously processed files  
✅ Identifies employee pairs who worked together the longest  
✅ Displays all common projects where the pair worked together  
✅ Calculates the number of days they worked per project  
✅ Presents results in a structured table, sorted by days worked (DESC)  

## Backend Setup (Spring Boot)
Ensure Java 17 and Maven are installed, then run:
- cd backend
- mvn spring-boot:run
- The backend will start at http://localhost:8080.

## Frontend Setup (Angular)
Ensure Node.js and Angular CLI are installed, then run:

- cd frontend
- npm install
- ng serve
- The frontend will be available at http://localhost:4200.

## 📂 CSV File Format
The application accepts CSV files without headers.

## Expected Columns:
143	12	2013-11-01	2014-01-05  
218	10	2012-05-16	NULL  
143	10	2009-01-01	2011-04-27  

The End Date can be NULL, meaning the employee is still working on the project.  
Multiple date formats are supported, including:  
yyyy-MM-dd  
MM/dd/yyyy  
dd/MM/yyyy  
MMM dd, yyyy  
dd-MMM-yyyy  