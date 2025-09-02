## DANT Pharmacy — Hướng dẫn cài đặt và chạy

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
- Tải JDK 17 từ Adoptium: `https://adoptium.net/`. Cài đặt và đặt biến môi trường `JAVA_HOME`, thêm `%JAVA_HOME%\bin` vào `Path`.

Kiểm tra (Verify):
```bash
java -version
javac -version
```

#### 2.2 Cài Maven (Install Maven)
- Tải Maven từ `https://maven.apache.org/download.cgi` (Binary zip). Giải nén vào `C:\Program Files\Apache\maven`, đặt `MAVEN_HOME`, thêm `%MAVEN_HOME%\bin` vào `Path`.


Kiểm tra (Verify):
```bash
mvn -version
```

#### 2.3 Cài SQL Server + SSMS (Install SQL Server + SSMS)
- VI: Tải SQL Server Developer 2022 và SSMS từ Microsoft. Bật chế độ xác thực Mixed Mode, bật TCP/IP (SQL Server Configuration Manager), đảm bảo port `1433` mở.

---

### 3) Lấy mã nguồn (Get the source)

- VI: Nếu đã có thư mục, bỏ qua bước clone. Nếu không, clone repo (thay URL thật của bạn):

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
-  Mở SSMS → Kết nối `localhost,1433` → Mở `SQL.sql` → Chạy. Lưu ý file có đường dẫn MDF/LDF mẫu theo instance `MSSQL16.STORE`. Nếu instance của bạn khác, nên dùng Cách B.

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

> Bạn có thể đổi thông tin đăng nhập theo ý muốn, nhớ cập nhật lại ứng dụng.

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

- Thay đổi toàn bộ secrets mặc định trước khi triển khai thật. Không commit secrets.

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
- Nếu gặp lỗi, đính kèm ảnh/chụp log console và cấu hình `application.properties` (ẩn secrets) để được hỗ trợ.



---

### 13) Checklist nhanh (làm theo lần lượt)
- Cài JDK 17 → kiểm tra `java -version` ra 17
- Cài Maven → kiểm tra `mvn -version`
- Cài SQL Server + SSMS, bật Mixed Mode, bật TCP/IP, port 1433, mở firewall
- Tạo DB và tài khoản: dùng Cách A (chạy `SQL.sql`) hoặc Cách B (lệnh tạo nhanh)
- Sửa `src/main/resources/application.properties` trỏ đúng DB, user, mật khẩu
- (Tùy chọn) Đặt biến môi trường PAYOS nếu dùng tính năng thanh toán
- Mở VS Code → đảm bảo dùng JDK 17 → Spring Boot Dashboard → Start
- Mở `http://localhost:8080` → kiểm tra log không lỗi

---

### 14) Hướng dẫn siêu chi tiết cho người mới

1) Cài SQL Server và bật các cấu hình quan trọng:
   - Mở "SQL Server Configuration Manager" → SQL Server Network Configuration → Protocols for <Instance> → Enable TCP/IP → OK.
   - Nhấn đúp TCP/IP → tab IP Addresses → kéo xuống IPAll → TCP Port: nhập `1433` → OK.
   - Vào SQL Server Services → restart dịch vụ SQL Server (và SQL Server Browser nếu dùng Named Instance).
   - Mở "Windows Defender Firewall" → Inbound Rules → New Rule → Port → TCP → Specific local ports: `1433` → Allow → Next → Finish.

2) Tạo Database và tài khoản đăng nhập:
   - Mở SSMS → Connect `localhost,1433` (Authentication: SQL Server Authentication nếu đã bật Mixed Mode).
   - Thực thi Cách B trong README để tạo `STORE`, `LOGIN sa`, `USER sa`, cấp quyền `db_owner`.

3) Sửa cấu hình ứng dụng:
   - Mở `src/main/resources/application.properties` → cập nhật `spring.datasource.username` và `spring.datasource.password` đúng với LOGIN vừa tạo.
   - Nếu dùng Gmail gửi mail: bật 2FA, tạo App Password 16 ký tự, dán vào `spring.mail.password`.
   - Nếu dùng PayOS: chạy lệnh `setx` như hướng dẫn để đặt biến môi trường.

4) Chạy ứng dụng bằng VS Code:
   - Cài các extension Java + Spring.
   - Command Palette → "Java: Configure Java Runtime" → chọn JDK 17 làm default.
   - Mở lớp `com.poly.AsmJava5Application` → bấm Run hoặc Debug.

5) Kiểm tra ứng dụng:
   - Truy cập `http://localhost:8080`. Nếu thấy trang chủ hoặc trang đăng nhập là OK.
   - Kiểm tra `http://localhost:8080/actuator/health` trả về `{"status":"UP"}`.

---

### 15) Tạo tài khoản người dùng/quản trị (nếu cần)
- Ứng dụng dùng BCrypt để mã hóa mật khẩu (SecurityConfig dùng `BCryptPasswordEncoder`).
- Cách đơn giản nhất: đăng ký tài khoản qua giao diện `/signup` rồi nâng quyền trong DB nếu cần.
- Nếu muốn chèn trực tiếp vào bảng `USERS` bằng SQL, bạn cần tự tạo hash BCrypt cho mật khẩu (dùng công cụ tạo BCrypt online) rồi chạy `INSERT` với giá trị hash đó vào cột `matkhau`. Cột `vaitro`: `1` là ADMIN, `0` là USER. Cột `kichhoat` cần là `1` để đăng nhập được.

Ví dụ khung lệnh (thay các giá trị thật bằng của bạn):
```sql
INSERT INTO USERS(id_user, sdt, hinh, hoten, matkhau, kichhoat, vaitro)
VALUES ('admin', '0900000000', NULL, 'Quản trị', '<BCRYPT_HASH>', 1, 1);
```

---

### 16) Các lỗi thường gặp và cách khắc phục (chi tiết)
- Không kết nối được DB:
  - Lỗi "The TCP/IP connection to the host..." → Bật TCP/IP, đặt port 1433, mở firewall, restart dịch vụ.
  - Lỗi "Login failed for user 'sa'" → Sai mật khẩu hoặc chưa map `USER` vào DB `STORE`/chưa cấp quyền `db_owner`. Làm lại bước tạo LOGIN/USER.
  - Lỗi SSL/Certificate khi kết nối SQL Server → giữ `encrypt=true;trustServerCertificate=true;` như cấu hình hiện tại hoặc cài chứng chỉ rồi đặt `trustServerCertificate=false`.

- Lỗi Java version (Unsupported class file major/minor):
  - `java -version` phải là 17; kiểm tra VS Code đang dùng JDK 17 trong Java Runtime.

- Lỗi port 8080 đã dùng:
  - Đổi `server.port=8081` trong `application.properties` hoặc tắt tiến trình đang chạy cổng 8080.

- Gmail không gửi mail:
  - Bắt buộc dùng App Password (16 ký tự), không dùng mật khẩu thường. Kiểm tra cổng 587 và `starttls` bật.

- 404/403 khi truy cập:
  - `/admin/**` yêu cầu tài khoản có vai trò ADMIN (`vaitro=1`).
  - Các API còn lại yêu cầu đăng nhập trừ danh sách public đã liệt kê trong README.

---

### 17) Biết khi nào ứng dụng đã chạy OK
- Console log có dòng "Started AsmJava5Application" và không có stacktrace ERROR.
- Truy cập `http://localhost:8080/actuator/health` trả `UP`.
- Kết nối DB thành công: không có lỗi `Cannot determine target DataSource`/`Login failed`.
