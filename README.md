# Budget Spring Batch Application

## Description

This Spring Batch application processes budget data from flat files. It calculates net savings by deducting expenses from salaries 
and then exports the processed data into another CSV file for staging. The processed information is also stored in a MongoDB database. 
The application is designed to manage and analyze budgeting data efficiently.

## Prerequisites

- Java JDK 17
- Maven (for building the project)
- MongoDB (for data storage)
- An IDE like IntelliJ IDEA or Eclipse (optional, for development)

Features
Reads budget data from CSV files.
Processes the data to calculate net savings.
Writes processed data to a new CSV file.
Stores data in MongoDB for further analysis.
