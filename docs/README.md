# James Property Holdings — Rental Management System

A desktop **Property Rental Management System** for **James Property Holdings**: a Java Swing application backed by MySQL that manages residential rental properties (Houses, Apartments, Townhouses), the tenants who rent them, the leases that bind them, the payments those leases generate, and exportable reports across all of the above.

> **Default login:** `admin` / `admin`

---

## Table of contents

1. [Features](#1-features)
2. [Tech stack](#2-tech-stack)
3. [Architecture](#3-architecture)
4. [Project structure](#4-project-structure)
5. [Database schema](#5-database-schema)
6. [Prerequisites](#6-prerequisites)
7. [Step-by-step setup guide](#7-step-by-step-setup-guide)
8. [Running the project](#8-running-the-project)
9. [Using the application](#9-using-the-application)
10. [Report exports — how they work](#10-report-exports--how-they-work)
11. [Troubleshooting](#11-troubleshooting)
12. [Project conventions](#12-project-conventions)
13. [Known limitations](#13-known-limitations)

---

## 1. Features

| Module | Capabilities |
|---|---|
| **Login** | Single admin account, SHA-256 password hash check against `admin` table |
| **Properties** | CRUD for **Houses**, **Apartments**, and **Townhouses** with type-specific fields (plot size, unit number, floor level, elevator, backyard) — sub-tabbed UI |
| **Tenants** | CRUD with name, phone, email, status (`Active` / `Blacklisted`) |
| **Leases** | CRUD with start/end/due dates, rent, security deposit, grace period, late penalty rate, active flag |
| **Payments** | CRUD with receipt number, amount, date, partial-payment flag, status, lease linkage |
| **Reports** | Five built-in views: All Tenants · All Properties · Active Leases · All Payments · System Summary |
| **Exports** | Any displayed report can be exported to **CSV**, **Excel** (`.xls`), or **PDF** |
| **Logout** | Visible button (top-right), `File → Logout` menu, and `Ctrl+L` shortcut — all confirm before returning to login screen |

---

## 2. Tech stack

- **Language / runtime:** Java 22+ (project declares Java 25 in `pom.xml` but compiles cleanly with Java 22; the code uses no Java 25-specific features)
- **GUI:** Swing (`javax.swing.*`) with the Nimbus look-and-feel
- **Database:** MySQL 8.x via the JDBC driver `com.mysql:mysql-connector-j:9.2.0`
- **Build:** Both Maven (`pom.xml`) and NetBeans Ant (`build.xml`, `nbproject/`) project files are checked in. The instructions below use **plain `javac` + `java`** so you don't need Maven installed.
- **Dependencies:** **One** runtime jar — `mysql-connector-j`. PDF/CSV/Excel export code is hand-written, no third-party libraries.

---

## 3. Architecture

Four-layer separation under the `prgproject` package:

```
┌─────────────────────────────────────────────────────────────────────┐
│  prgproject.ui      Swing forms / panels (presentation)             │
│  prgproject.services   business rules, validation, orchestration   │
│  prgproject.DAO     JDBC data access (PreparedStatement)            │
│  prgproject.model   POJO domain entities                            │
│  prgproject.Reports report data + CSV/Excel/PDF exporters           │
│  prgproject.utils   DBConnection · Hasher                           │
└─────────────────────────────────────────────────────────────────────┘
```

Data flow at runtime:

```
LoginFrom ──► AdminDao.authenticate() ──► MySQL
   │
   └──► DashboardForm  (5 tabs)
          │
          ├─ PropertyPanel  ─► HouseService / ApartmentService / TownHouseService ─► *Dao ─► MySQL
          ├─ TenantPanel    ─► TenantService                                      ─► TenantDao
          ├─ LeasePanel     ─► LeaseService                                       ─► LeaseDao
          ├─ PaymentPanel   ─► PaymentService                                     ─► PaymentDAO
          └─ ReportPanel    ─► all services + Reports.* exporters
```

UI panels never touch SQL directly. Services validate inputs, enforce rules (e.g. *property must be available before creating a lease*), and orchestrate multi-DAO operations. DAOs perform a single round-trip per call using `try-with-resources` and prepared statements.

---

## 4. Project structure

```
Rental-Management-System/
├── README.md                       ← (this file)
├── PROJECT_OVERVIEW.md             ← deep-dive on the codebase
├── pom.xml                         ← Maven build descriptor
├── build.xml + nbproject/          ← NetBeans Ant build
├── james_property_sql.sql          ← original SQL schema (legacy, see §5)
├── schema_dao_aligned.sql          ← canonical schema + seed data
├── mysql-connector-j-9.2.0.jar     ← JDBC driver (downloaded once)
├── src/prgproject/
│   ├── DAO/
│   │   ├── AdminDao.java           ← login auth
│   │   ├── PropertyDao.java        ← base property CRUD
│   │   ├── HouseDao.java
│   │   ├── ApartmentDao.java
│   │   ├── TownHouseDao.java
│   │   ├── TenantDao.java
│   │   ├── LeaseDao.java
│   │   └── PaymentDAO.java
│   ├── model/                      ← domain POJOs
│   │   ├── Property.java           ← base class
│   │   ├── House.java · Apartment.java · TownHouse.java
│   │   ├── Tenant.java · Lease.java · Payment.java
│   ├── services/                   ← business layer
│   │   ├── HouseService.java · ApartmentService.java · TownHouseService.java
│   │   ├── TenantService.java · LeaseService.java · PaymentService.java
│   ├── ui/                         ← Swing forms
│   │   ├── LoginFrom.java          ← entry point (main method)
│   │   ├── DashboardForm.java      ← 5-tab main window + logout button
│   │   ├── PropertyPanel.java · TenantPanel.java
│   │   ├── LeasePanel.java · PaymentPanel.java · ReportPanel.java
│   ├── Reports/                    ← report data + exporters
│   │   ├── ReportData.java         ← title + columns + rows + summary
│   │   ├── ReportExporter.java     ← Strategy interface
│   │   ├── CSVExporter.java
│   │   ├── ExcelExporter.java
│   │   ├── PDFExporter.java        ← real PDF, no libraries
│   │   └── (legacy) Report.java · TenantReport.java · LeaseReport.java …
│   └── utils/
│       ├── DBConnection.java       ← JDBC URL + credentials
│       └── Hasher.java             ← SHA-256
└── target/                         ← Maven output (generated, gitignored)
```

---

## 5. Database schema

The application uses MySQL database `property_management` with the following tables (this is what [schema_dao_aligned.sql](schema_dao_aligned.sql) creates):

| Table | Primary key | Purpose |
|---|---|---|
| `admin` | `username` | Login credentials (SHA-256 hashed) |
| `tenants` | `credential` | Tenant records |
| `property` | `propertyID` | Base property metadata |
| `houses` | `propertyID` | House-specific fields (plot_size) |
| `apartments` | `propertyID` | Apartment-specific fields (unit_no, floor_level, elevator, backyard) |
| `townhouses` | `propertyID` | Townhouse-specific fields (unit_no, backyard) |
| `leases` | `leaseID` | Tenant↔property contracts (rent, dates, penalty rate) |
| `payments` | `receipt` | Payment receipts linked to a lease |

The repo also contains a legacy [james_property_sql.sql](james_property_sql.sql) (table names like `PropertyTable`, `HouseTable`, …) which **does not match** the DAO queries. Use [schema_dao_aligned.sql](schema_dao_aligned.sql) — that's the source of truth.

---

## 6. Prerequisites

| Tool | Version | Why |
|---|---|---|
| **JDK** | 22 or later | Compile + run the application |
| **MySQL Server** | 8.x | Backend database |
| **PowerShell** | Windows 10/11 default | Setup commands |

Verify your JDK:

```powershell
java -version    # should print 22 or higher
javac -version   # should print 22 or higher
```

---

## 7. Step-by-step setup guide

> All commands assume the repo is cloned to `C:\Users\<you>\Desktop\Rental-Management-System` and run from **PowerShell** (not cmd).

### 7.1 — Install MySQL

If you don't already have MySQL Server installed:

```powershell
winget install Oracle.MySQL
```

Accept the license. The installer drops binaries at `C:\Program Files\MySQL\MySQL Server 8.4\bin\`.

If the installer doesn't register a Windows service or initialize a data directory (common with a binary-only install), continue with **§7.2**.

If your installer ran the full configurator and started a service, skip to **§7.4**.

### 7.2 — Initialize the data directory (only if §7.1 didn't)

```powershell
$DD  = "$env:USERPROFILE\mysql-data"
$BIN = "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe"
& $BIN --initialize-insecure --datadir=$DD
```

This creates `~/mysql-data` with a fresh MySQL system tablespace and a passwordless `root@localhost` account. Takes ~5 seconds.

### 7.3 — Start `mysqld`

```powershell
$DD  = "$env:USERPROFILE\mysql-data"
$BIN = "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe"
Start-Process -FilePath $BIN -ArgumentList "--datadir=$DD","--port=3306" -WindowStyle Hidden
```

Verify it's listening:

```powershell
Test-NetConnection localhost -Port 3306 -InformationLevel Quiet
# expected: True
```

> ⚠️ This `mysqld` runs as a foreground process, **not as a Windows service**. It will exit when you reboot or sign out. To make it persistent, run `mysqld --install` from an **elevated** PowerShell with the same `--datadir` argument, or use the official MySQL Configurator.

### 7.4 — Set the root password to `toor`

The application's [DBConnection.java](src/prgproject/utils/DBConnection.java) hard-codes `root` / `toor` as the database credentials. Set the password to match:

```powershell
$CLIENT = "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe"
& $CLIENT -u root --skip-password -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'toor'; FLUSH PRIVILEGES;"
```

If your install already has a different root password, edit the `PASS` constant in [src/prgproject/utils/DBConnection.java](src/prgproject/utils/DBConnection.java) instead.

### 7.5 — Load the schema and seed data

From the repo root:

```powershell
$CLIENT = "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe"
Get-Content schema_dao_aligned.sql -Raw | & $CLIENT -u root -ptoor
```

This drops any pre-existing tables, creates the 8 tables (incl. `admin`), and inserts realistic Namibian-flavoured seed data: 5 tenants, 6 properties, 3 active leases, 5 payments, and the `admin / admin` login.

Verify:

```powershell
& $CLIENT -u root -ptoor -e "USE property_management; SHOW TABLES; SELECT COUNT(*) FROM tenants;"
```

You should see 8 tables and tenant count = 5.

### 7.6 — Download the JDBC driver

If `mysql-connector-j-9.2.0.jar` isn't already in the repo root:

```powershell
Invoke-WebRequest "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.2.0/mysql-connector-j-9.2.0.jar" `
  -OutFile "mysql-connector-j-9.2.0.jar"
```

---

## 8. Running the project

### 8.1 — Compile

From the repo root:

```powershell
if (Test-Path out) { Remove-Item -Recurse -Force out }
New-Item -ItemType Directory -Path out | Out-Null
javac -d out (Get-ChildItem -Recurse src -Filter *.java | % FullName)
```

A clean build produces ~50 `.class` files under `out/`.

### 8.2 — Launch

```powershell
java -cp "out;mysql-connector-j-9.2.0.jar" prgproject.ui.LoginFrom
```

The login window appears. Enter:

| | |
|---|---|
| Username | `admin` |
| Password | `admin` |

Press Enter or click **Login**. The dashboard opens with five tabs.

### 8.3 — One-liner alias (optional)

Save this in a `run.ps1` at the repo root for quick re-launches:

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java | % FullName)
java -cp "out;mysql-connector-j-9.2.0.jar" prgproject.ui.LoginFrom
```

Then just run `.\run.ps1`.

---

## 9. Using the application

### Properties tab
Three sub-tabs: **Houses · Apartments · Townhouses**. Each has a table at the top, a CRUD form below, and Add / Update / Delete / Clear / Refresh buttons. Selecting a row populates the form for editing.

### Tenants tab
Single CRUD view for tenant records (credential, name, phone, email, status).

### Leases tab
CRUD for lease contracts. The lease ID is user-supplied; tenant/property links are placeholder fields in the current form (see *Known limitations*).

### Payments tab
Records receipts against leases. The system tracks partial vs full payments and updates status accordingly.

### Reports tab
Five generator buttons in the toolbar: **All Tenants · All Properties · Active Leases · All Payments · Summary**. Click one and the table populates. The "Summary" view shows counts for each entity.

### Logout
- **Logout button** — top-right of the dashboard header (red)
- **File → Logout** menu
- **Ctrl+L** keyboard shortcut

All three confirm before disposing the dashboard and returning you to the login screen.

---

## 10. Report exports — how they work

After generating any report (clicking one of the five report buttons), use the export panel on the right to save it.

### CSV
- Standard RFC-4180 format
- UTF-8 encoding
- Quoting and escaping for commas, newlines, and double-quotes
- Opens directly in Excel, Numbers, LibreOffice Calc, etc.

### Excel (`.xls`)
- HTML table written with `.xls` extension — Excel opens this natively
- Includes styled header row (dark navy + white text), cell borders, summary
- No third-party library (no Apache POI dependency)

### PDF
- Real PDF 1.4 documents — written from scratch, no external libraries
- Multi-page automatic pagination (rows split across pages as needed)
- Title at top, table with header row + data rows
- "Page N of M" footer on every page
- Summary block on the last page
- Helvetica font (built-in PDF Type 1 font)

The chosen format opens a `JFileChooser` with a date-stamped suggested filename like `all_tenants_2026-05-07.csv`.

If you click an export button before generating a report, you get a "Nothing to export" prompt instead of an empty file.

---

## 11. Troubleshooting

### "Communications link failure" on login
MySQL isn't running. Check:
```powershell
Test-NetConnection localhost -Port 3306 -InformationLevel Quiet
```
If `False`, restart `mysqld` per **§7.3**.

### "Access denied for user 'root'@'localhost'"
The password isn't `toor`. Either run **§7.4** again or edit `PASS` in [DBConnection.java](src/prgproject/utils/DBConnection.java).

### "Unknown database 'property_management'"
The schema wasn't loaded. Run **§7.5**.

### "Table 'property_management.tenants' doesn't exist"
You loaded the legacy [james_property_sql.sql](james_property_sql.sql) instead of [schema_dao_aligned.sql](schema_dao_aligned.sql) — the legacy script creates `TenantTable`, `PropertyTable`, etc., which the DAOs don't query. Use the aligned schema.

### Login dialog appears but pressing Login does nothing
Check the terminal where you launched the app. SQL exceptions are printed to `System.out`. Common cause: the `admin` table is missing or empty. Run **§7.5** to recreate it.

### Compile fails with `error: class TownHouseDao is public, should be declared in a file named TownHouseDao.java`
Your filesystem has the file as `TownHouseDAO.java` (capital). Rename it:
```powershell
git mv src/prgproject/DAO/TownHouseDAO.java src/prgproject/DAO/TownHouseDao_tmp.java
git mv src/prgproject/DAO/TownHouseDao_tmp.java src/prgproject/DAO/TownHouseDao.java
```

### Compile fails with `error: '(' or '[' expected` at `TownHouseService.java:71`
You're looking at an unfinished `throw new Ill` statement that was committed to an older branch and re-introduced by a merge. Fix it:

```java
// src/prgproject/services/TownHouseService.java line 67–75 — replace this block
public void removeTownHouse(int id) {
    try {
        int result = townHouseDAO.deleteTownHouse(id);
        if (result == 0) {
            throw new RuntimeException("Failed to delete townhouse: " + id);
        }
    } catch (RuntimeException e) {
        throw e;
    }
}
```

After a merge, also re-check that `TownHouseService` exposes `saveTownHouse / updateTownHouse / deleteTownHouse` (called by `PropertyPanel`); the older branch only had `addTownHouse / modifyTownHouse / removeTownHouse`.

### After a merge, multiple `Reports/*Report.java` files fail to compile
If you see `error: interface expected here` on `TenantReport`, `LeaseReport`, `RevenueReport`, or `PaymentReport`, the older branch had `implements Report` against a class (not an interface). Strip `implements Report` and the `@Override` annotation from `generate()` so each becomes a plain class. The active reporting path is `ReportPanel` + `ReportData` + `*Exporter` — those legacy `*Report.java` files are unused stubs.

### `mysqld` won't start — port 3306 already in use
Another MySQL or MariaDB instance is already running. Either stop it, or pass a different port to `mysqld` and update the URL in [DBConnection.java](src/prgproject/utils/DBConnection.java).

---

## 12. Project conventions

- **Naming.** `*Dao` classes are JDBC adapters. `*Service` classes hold business rules. `*Panel` classes are Swing presentation. Models are POJOs only.
- **Validation.** Argument validation happens in the service layer (`validateLease`, `validateTenant`, …). DAOs assume already-validated input.
- **Error handling.** DAOs wrap `SQLException` in `RuntimeException`; services wrap further with semantic messages. UI panels catch `Exception` at button-handler boundaries and surface errors via `JOptionPane`.
- **Connections.** One `Connection` per DAO call, opened with try-with-resources. No connection pool (this is a desktop client; pool unnecessary).
- **Threading.** Currently all DB calls run on the Swing EDT. Acceptable for a course project; would need `SwingWorker` for production.

---

## 13. Known limitations

These are open work items — none block normal use, but worth knowing:

1. **Lease form doesn't collect tenant/property IDs.** [LeasePanel.buildLeaseFromForm()](src/prgproject/ui/LeasePanel.java) defaults `tenantID` and `propertyID` to `0`. To wire real foreign keys, add two more form fields to the panel.
2. **Single admin account.** No user management UI; new admin rows must be inserted via SQL.
3. **Password hashing is unsalted SHA-256.** Acceptable for coursework, vulnerable to rainbow-table attacks in the wild. Replace with `BCrypt` or `Argon2` for production.
4. **Hard-coded DB credentials.** [DBConnection.java](src/prgproject/utils/DBConnection.java) embeds `root` / `toor`. Move to a config file or environment variables for any non-local deployment.
5. **`mysqld` runs as a process, not a service.** Won't survive reboot. Run `mysqld --install` from an elevated PowerShell to fix.
6. **The four legacy `*Report.java` classes** in `src/prgproject/Reports/` (TenantReport, LeaseReport, RevenueReport, PaymentReport) are stubs that throw `UnsupportedOperationException`. The active reporting path is `ReportPanel + ReportData + *Exporter`.
7. **No automated tests.** The project relies on manual verification.

---

## Authors

Per source code attribution: **Bruce**, **Collin**, and **amute** (NetBeans `application.vendor` and `@author` tags).
