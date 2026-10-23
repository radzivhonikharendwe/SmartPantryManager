# Smart Pantry Manager

## 1. App Description

Smart Pantry Manager is an Android application developed for the Mobile App Development 700 module. The application is designed to help users manage ingredients stored in their pantry and reduce food waste.

The app is developed using Java and XML layouts in Android Studio.

## 2. Technologies Used

- **Programming language:** Java
- **User interface:** XML
- **Database:** SQLite
- **Development environment:** Android Studio
- **Platform:** Android

## 3. Database Choice and Rationale

Smart Pantry Manager uses SQLite to store pantry ingredient information locally on the device.

SQLite was selected because it is lightweight, works without requiring a separate database server, and allows the application to store and retrieve data locally. This makes it suitable for a small Android application that manages pantry information.

## 4. Main Features

Smart Pantry Manager provides a user interface for managing pantry ingredients. The application uses SQLite to store ingredient information locally on the Android device.

The available features should be described according to the functions successfully implemented and tested in the submitted version.

## 5. Project Structure

The project contains the following important components:

- `app/src/main/java/` — Java source code.
- `app/src/main/res/layout/` — XML layout files.
- `app/src/main/res/drawable/` — Images and drawable resources.
- `app/src/main/res/values/` — Colours, strings and other resources.
- `app/src/main/AndroidManifest.xml` — Android application configuration.
- `gradle/` and Gradle configuration files — Build configuration.

## 6. Requirements

To open and run this project, you need:

- Android Studio.
- A compatible Android SDK.
- An Android emulator or a physical Android device.
- Java and Android build tools configured through Android Studio.

## 7. Setup and Run Instructions

1. Clone or download this repository from GitHub.
2. Open Android Studio.
3. Select **Open** and choose the `SmartPantryManager` project folder.
4. Allow Gradle to sync and download any required dependencies.
5. Connect an Android device with USB debugging enabled or start an Android emulator.
6. Select the app configuration in Android Studio.
7. Click **Run** to build and launch the application.

## 8. Database Information

The application uses SQLite through Android's database APIs. The database is created and managed by the application's database helper class.

Database records are stored locally on the device. The database contents may not be included in the GitHub repository because the database is created by the application at runtime.

## 9. Version Control

This project is maintained using Git and GitHub. Git is used to track source-code changes throughout development.

GitHub repository: [https://github.com/radzivhonikharendwe/SmartPantryManager](https://github.com/radzivhonikharendwe/SmartPantryManager)

## 10. Author

**Student name:** Kharendwe Radzivhoni\
**Student number:** 402312133\
**Module:** Mobile App Development 700