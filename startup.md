# 🚀 WorkSphere — How to Start the Project

Follow these steps in order. You need **2 terminals**: one for the backend and one for the frontend.

---

## 🍎 MAC

### Step 1 — Open Terminal

Open the Terminal application.

### Step 2 — Go to the backend folder

If the project is inside your Downloads folder, copy-paste:

```bash
cd ~/Downloads/worksphere_project/backend
```

> If your project is somewhere else, change the path accordingly.

### Step 3 — Check Java

```bash
java -version
```

Java 17 or newer is required.

### Step 4 — Check Maven

```bash
mvn -version
```

You should see an Apache Maven version.

### Step 5 — Build the backend

```bash
mvn clean install
```

Wait until you see:

```text
BUILD SUCCESS
```

### Step 6 — Start the backend

```bash
mvn spring-boot:run
```

Wait until you see:

```text
Tomcat started on port 8080
```

and:

```text
Started WorkSphereApplication
```

✅ **Backend is now running.**

### Step 7 — Keep Terminal 1 open

**Do NOT close this terminal.**

The backend must keep running.

---

### Step 8 — Open a second Terminal

Open a new Terminal window/tab.

### Step 9 — Go to the frontend folder

```bash
cd ~/Downloads/worksphere_project/frontend
```

### Step 10 — Start the frontend

```bash
python3 -m http.server 5500
```

You should see:

```text
Serving HTTP on 0.0.0.0 port 5500
```

✅ **Frontend is now running.**

### Step 11 — Keep Terminal 2 open

**Do NOT close this terminal.**

---

### Step 12 — Open the website

Open your browser and visit:

```text
http://localhost:5500
```

🎉 WorkSphere should now be running.

---

## 🪟 WINDOWS

### Step 1 — Open Command Prompt

Press:

```text
Windows + R
```

Type:

```text
cmd
```

Press **Enter**.

### Step 2 — Go to the backend folder

If the project is inside Downloads:

```cmd
cd %USERPROFILE%\Downloads\worksphere_project\backend
```

### Step 3 — Check Java

```cmd
java -version
```

Java 17 or newer is required.

### Step 4 — Check Maven

```cmd
mvn -version
```

You should see an Apache Maven version.

### Step 5 — Build the backend

```cmd
mvn clean install
```

Wait until you see:

```text
BUILD SUCCESS
```

### Step 6 — Start the backend

```cmd
mvn spring-boot:run
```

Wait until you see:

```text
Tomcat started on port 8080
```

and:

```text
Started WorkSphereApplication
```

✅ **Backend is now running.**

### Step 7 — Keep Command Prompt 1 open

**Do NOT close this window.**

---

### Step 8 — Open a second Command Prompt

Open another Command Prompt window.

### Step 9 — Go to the frontend folder

```cmd
cd %USERPROFILE%\Downloads\worksphere_project\frontend
```

### Step 10 — Start the frontend

```cmd
python -m http.server 5500
```

If `python` does not work, try:

```cmd
py -m http.server 5500
```

You should see:

```text
Serving HTTP on 0.0.0.0 port 5500
```

✅ **Frontend is now running.**

### Step 11 — Keep Command Prompt 2 open

**Do NOT close this window.**

---

### Step 12 — Open the website

Open Chrome, Edge, or Firefox and visit:

```text
http://localhost:5500
```

🎉 WorkSphere should now be running.

---

# 🔐 Demo Login Accounts

Use these accounts for testing:

| Role     | Email                      | Password       |
| -------- | -------------------------- | -------------- |
| Admin    | `admin@worksphere.com`     | `Admin@123`    |
| Worker   | `worker1@worksphere.com`   | `Worker@123`   |
| Worker   | `worker2@worksphere.com`   | `Worker@123`   |
| Worker   | `worker3@worksphere.com`   | `Worker@123`   |
| Employer | `employer1@worksphere.com` | `Employer@123` |

---

# 🛑 How to Stop WorkSphere

When you are finished:

Go to the **backend terminal** and press:

```text
Ctrl + C
```

Then go to the **frontend terminal** and press:

```text
Ctrl + C
```

Both servers will stop.

---

# 🔄 How to Start It Again Later

You do **not** need to run the installation again every time.

### Terminal 1 — Backend

Mac:

```bash
cd ~/Downloads/worksphere_project/backend
mvn spring-boot:run
```

Windows:

```cmd
cd %USERPROFILE%\Downloads\worksphere_project\backend
mvn spring-boot:run
```

### Terminal 2 — Frontend

Mac:

```bash
cd ~/Downloads/worksphere_project/frontend
python3 -m http.server 5500
```

Windows:

```cmd
cd %USERPROFILE%\Downloads\worksphere_project\frontend
python -m http.server 5500
```

Then open:

```text
http://localhost:5500
```

---

# ⚠️ If Something Goes Wrong

Do not randomly change project files.

Copy the error from the terminal and send it to the team/developer.

Useful commands to check your setup:

```bash
java -version
mvn -version
python3 --version
```

Windows:

```cmd
java -version
mvn -version
python --version
```

The backend must run on:

```text
http://localhost:8080
```

The frontend must run on:

```text
http://localhost:5500
```
