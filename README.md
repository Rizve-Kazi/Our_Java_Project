# Smart Student Survival System

A Java Swing desktop app: login, then a dashboard with assignments, exam countdown,
daily study planner and a marks / CGPA calculator.

## How to run
Needs a JDK (Java 8 or newer).

- Linux / macOS: `./run.sh`
- Windows: double-click `run.bat`
- Manual: `javac -d out $(find src -name "*.java")` then `java -cp out gui.Main`
  (on Windows PowerShell: `javac -d out src\model\*.java src\util\*.java src\service\*.java src\controller\*.java src\gui\*.java`)

## First time
1. Click "New here? Create an account", enter full name, student ID, username and password.
2. Next time just log in. Every user has a separate folder: `data/users/<username>/`.

## Features
- Login / register. Passwords are hashed (PBKDF2 + random salt), never saved as plain text
- Assignment deadline tracker with High / Medium / Low priority
- Exam countdown (red warning within 7 days)
- Daily study planner
- Marks -> grade -> GPA / CGPA calculator (4.00 scale, per-semester GPA)
- Data saved in `.txt` files and loaded again on start

Dates: `yyyy-MM-dd` (2026-12-31). Time: `HH:mm` (18:30). Marks: 0-100.
