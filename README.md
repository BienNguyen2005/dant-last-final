## DANT Pharmacy — Hướng dẫn cài đặt và chạy (VI/EN)

> Spring Boot 3.3 (Java 17), SQL Server, Thymeleaf, Spring Security, JPA, Mail, PayOS.

---

### 1) Yêu cầu hệ thống (System Requirements)
- **Java JDK**: 17+
- **Maven**: 3.6+
- **Database**: Microsoft SQL Server 2019/2022 + SSMS
- **IDE**: Visual Studio Code (khuyến nghị) hoặc IntelliJ IDEA
- **OS**: Windows 10/11 (khuyến nghị)

---

### 2) Chuẩn bị công cụ (Tooling Setup)

#### 2.1 Cài Java 17 (Install Java 17)
- VI: Tải JDK 17 từ Adoptium: `https://adoptium.net/`. Cài đặt và đặt biến môi trường `JAVA_HOME`, thêm `%JAVA_HOME%\bin` vào `Path`.
- EN: Download JDK 17 from Adoptium: `https://adoptium.net/`. Install, set `JAVA_HOME`, and add `%JAVA_HOME%\bin` to `Path`.

Kiểm tra (Verify):
```bash
java -version
javac -version
```

#### 2.2 Cài Maven (Install Maven)
- VI: Tải Maven từ `https://maven.apache.org/download.cgi` (Binary zip). Giải nén vào `C:\Program Files\Apache\maven`, đặt `MAVEN_HOME`, thêm `%MAVEN_HOME%\bin` vào `Path`.
- EN: Download Maven from `https://maven.apache.org/download.cgi` (Binary zip). Extract to `C:\Program Files\Apache\maven`, set `MAVEN_HOME`, add `%MAVEN_HOME%\bin` to `Path`.

Kiểm tra (Verify):
```bash
mvn -version
```

#### 2.3 Cài SQL Server + SSMS (Install SQL Server + SSMS)
- VI: Tải SQL Server Developer 2022 và SSMS từ Microsoft. Bật chế độ xác thực Mixed Mode, bật TCP/IP (SQL Server Configuration Manager), đảm bảo port `1433` mở.
- EN: Download SQL Server Developer 2022 and SSMS. Enable Mixed Mode auth, enable TCP/IP, ensure port `1433` is open.

---

### 3) Lấy mã nguồn (Get the source)

- VI: Nếu đã có thư mục, bỏ qua bước clone. Nếu không, clone repo (thay URL thật của bạn):
- EN: If you already have the folder, skip clone. Otherwise, clone (replace with your real URL):

```bash
git clone https://github.com/BienNguyen2005/dant-last-final
cd dant-last-final
```

Mở trong VS Code (Open in VS Code):
```bash
code .
```

Thư mục làm việc chuẩn (Typical path): `D:\dant-last-final`

---

### 4) Tạo Database và tài khoản (Create Database and login)

Bạn có 2 cách (You have 2 options):

#### Cách A — Dùng SSMS chạy `SQL.sql` (Use SSMS to run `SQL.sql`)
- VI: Mở SSMS → Kết nối `localhost,1433` → Mở `SQL.sql` → Chạy. Lưu ý file có đường dẫn MDF/LDF mẫu theo instance `MSSQL16.STORE`. Nếu instance của bạn khác, nên dùng Cách B.
- EN: Open SSMS → Connect `localhost,1433` → Open `SQL.sql` → Execute. Note it references MDF/LDF paths for `MSSQL16.STORE`. If your instance differs, prefer Option B.

#### Cách B — Tạo nhanh với đường dẫn mặc định (Simple create with default paths)
Chạy các lệnh sau trong SSMS (Run in SSMS):
```sql
-- 1) Create database with default file locations
CREATE DATABASE [STORE];
GO

-- 2) Create SQL login
IF NOT EXISTS (SELECT 1 FROM sys.sql_logins WHERE name = 'sa')
BEGIN
    CREATE LOGIN [sa] WITH PASSWORD = 'Nhanhtam456', CHECK_POLICY = OFF;
END;
GO

-- 3) Map user to db and grant permissions
USE [STORE];
GO
IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = 'sa')
BEGIN
    CREATE USER [sa] FOR LOGIN [sa] WITH DEFAULT_SCHEMA = [dbo];
END;
ALTER ROLE [db_owner] ADD MEMBER [sa];
GO
```

> VI: Bạn có thể đổi thông tin đăng nhập theo ý muốn, nhớ cập nhật lại ứng dụng.
> EN: You may change credentials; remember to update the app config accordingly.

---

### 5) Cấu hình ứng dụng (Configure the application)

File: `src/main/resources/application.properties`

Hiện dự án để sẵn cấu hình mẫu. Bạn nên cập nhật như sau (The project ships with sample values. Update as needed):
```properties
server.port=8080

spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=STORE;encrypt=true;trustServerCertificate=true;
spring.datasource.username=sa
spring.datasource.password=<YOUR_DB_PASSWORD>

# SMTP (Gmail): dùng App Password, KHÔNG dùng mật khẩu tài khoản
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<your-email@gmail.com>
spring.mail.password=<your-gmail-app-password>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# PayOS (khuyến nghị đặt qua biến môi trường)
PAYOS_CLIENT_ID=<your-payos-client-id>
PAYOS_API_KEY=<your-payos-api-key>
PAYOS_CHECKSUM_KEY=<your-payos-checksum-key>

spring.messages.encoding=UTF-8
```

- VI: Không commit mật khẩu/email/keys thật vào Git. Dùng biến môi trường hoặc `.env` (nếu có) và cấu hình trong máy.
- EN: Do not commit real secrets to Git. Use environment variables and local config.

Ứng dụng đọc các khóa PayOS qua `@Value` trong `AsmJava5Application`. Có thể đặt dưới dạng biến môi trường Windows (Set as Windows env vars):
```powershell
setx PAYOS_CLIENT_ID "<your-client-id>"
setx PAYOS_API_KEY "<your-api-key>"
setx PAYOS_CHECKSUM_KEY "<your-checksum-key>"
```

---

### 6) Chạy ứng dụng (Run the application)

#### 6.1 Bằng Maven Wrapper (CLI)
Trong thư mục dự án `D:\dant-last-final`:
```bat
:: Windows
mvnw.cmd clean spring-boot:run
```

Hoặc (Or):
```bat
mvnw.cmd -DskipTests spring-boot:run
```

Trên macOS/Linux (nếu áp dụng):
```bash
./mvnw clean spring-boot:run
```

Ứng dụng sẽ chạy tại (App will be running at): `http://localhost:8080`

Kiểm tra sức khỏe (Health check): `http://localhost:8080/actuator/health`

#### 6.2 Bằng VS Code
1) Cài extensions:
   - Extension Pack for Java (Microsoft)
   - Spring Boot Extension Pack
   - Maven for Java

2) Mở thư mục dự án (Open project folder): `D:\dant-last-final` → Trust.

3) Spring Boot Dashboard → Start ứng dụng, hoặc nhấn Run trên lớp `com.poly.AsmJava5Application`.

4) Debug: Chọn Run and Debug → Java → Spring Boot.

---

### 7) Build, đóng gói (Build and package)

Chạy build:
```bash
mvn clean package -DskipTests
```

Lưu ý: `pom.xml` đang dùng `packaging=war` và `spring-boot-starter-tomcat` scope `provided`. Khuyến nghị chạy dev bằng `spring-boot:run`. Khi cần triển khai ngoài Tomcat, dùng file WAR trong `target/`.

---

### 8) Sự cố thường gặp (Troubleshooting)

- Port 8080 bận (Port in use): đổi `server.port` hoặc tắt tiến trình đang chiếm port.
- Lỗi kết nối DB (DB connection): kiểm tra SQL Server service, TCP/IP, firewall, user/pass, `encrypt`/`trustServerCertificate`.
- Sai Java version: đảm bảo `java -version` là 17, kiểm tra `JAVA_HOME`.
- Gmail SMTP: bật 2FA, tạo App Password; không dùng mật khẩu thường.
- SQL file đường dẫn MDF/LDF: nếu lỗi, hãy tạo DB bằng lệnh đơn giản như phần 4B.

---

### 9) Kiến trúc nhanh (Quick architecture)

- Main class: `com.poly.AsmJava5Application`
- View layer: Thymeleaf (`src/main/resources/templates`)
- Static assets: `src/main/resources/static`
- Config: `src/main/resources/application.properties`
- Bảo mật: Spring Security + JWT (filter, entrypoint, config)
- Tích hợp: PayOS, SMTP Mail

---

### 10) Ghi chú bảo mật (Security notes)

- VI: Thay đổi toàn bộ secrets mặc định trước khi triển khai thật. Không commit secrets.
- EN: Replace all default secrets before real deployment. Never commit secrets.

---

### 11) Lệnh tóm tắt (Quick commands)

```bat
:: Vào thư mục dự án (Enter project folder)
cd /d D:\dant-last-final

:: Chạy ứng dụng (Run app)
mvnw.cmd spring-boot:run

:: Build WAR (Package)
mvnw.cmd clean package -DskipTests
```

---

### 12) Liên hệ/Trợ giúp (Support)
- VI: Nếu gặp lỗi, đính kèm ảnh/chụp log console và cấu hình `application.properties` (ẩn secrets) để được hỗ trợ.
- EN: If issues arise, share console logs and sanitized `application.properties` for support.


