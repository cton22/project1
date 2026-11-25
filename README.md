# RoK Monitor

Local-only, read-only monitoring starter for Rise of Kingdoms PC Client.
It captures the selected desktop region, optionally runs Tesseract OCR, stores snapshots in SQLite,
and serves a React dashboard. It does not click, control gameplay, automate farming/combat,
or bypass game protections.

## Requirements
- Windows 11
- JDK 21
- Maven 3.9+
- Node.js 20+
- Tesseract OCR for Windows (optional; screenshot capture works without it)

## 1. Backend (Eclipse)
Import `rok-monitor-backend` into Eclipse:
File > Import > Maven > Existing Maven Projects.
Set Installed JRE to JDK 21: Window > Preferences > Java > Installed JREs.
Run `src/main/java/com/rok/monitor/RokMonitorApplication.java` as Java Application.

Or from PowerShell:
```powershell
cd rok-monitor-backend
mvnw.cmd spring-boot:run
```
If Maven Wrapper is not present, use `mvn spring-boot:run`.

Backend: http://localhost:8080
Health: http://localhost:8080/api/health
API: http://localhost:8080/api

### Tesseract setup
Install Tesseract OCR from:
https://github.com/UB-Mannheim/tesseract/wiki
Default path is `C:\Program Files\Tesseract-OCR\tesseract.exe`.
Set environment variable before starting backend if installed elsewhere:
```powershell
$env:TESSERACT_PATH = "C:\Program Files\Tesseract-OCR\tesseract.exe"
```
OCR is optional. If Tesseract is missing, the API still captures screenshots and reports OCR unavailable.

## 2. Frontend
Open a second PowerShell:
```powershell
cd rok-monitor-frontend
npm install
npm run dev
```
Open http://localhost:5173

## 3. First run
1. Start the official RoK PC client and leave the game visible.
2. Start backend, then frontend.
3. In dashboard, choose a profile label (e.g. Main) and click "Capture & analyze".
4. For an accurate capture, use Windows display scaling/resolution consistently.
5. This starter captures the primary display. It does not switch accounts or inspect background windows.

## API
- `GET /api/health`
- `GET /api/accounts`
- `POST /api/accounts` body: `{"name":"Main"}`
- `GET /api/snapshots?accountId=1&limit=30`
- `POST /api/monitor/capture` body: `{"accountId":1,"runOcr":true}`
- `GET /api/screenshots/{filename}`

## Notes / limitations
- Resource values are not yet parsed into structured numeric fields; OCR text is stored for review.
- Account profiles are labels for organizing observations, not separate game sessions.
- Java AWT Robot captures the primary desktop. It cannot guarantee capture of minimized or protected windows.
- Use only in ways permitted by the game's terms. No gameplay actions are implemented.
