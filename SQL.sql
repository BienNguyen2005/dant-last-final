USE master
GO
CREATE DATABASE STORE
GO
USE STORE
GO
CREATE TABLE USERS(
	id_user VARCHAR(50) PRIMARY KEY,
	sdt VARCHAR(10) NOT NULL,
	hinh VARCHAR(255) NULL,
	hoten NVARCHAR(50) NOT NULL,
	matkhau VARCHAR(50) NOT NULL,
	kichhoat BIT NOT NULL,
	vaitro BIT NOT NULL
)
GO
CREATE TABLE LOAI(
	id_loai INT IDENTITY(1,1) PRIMARY KEY,
	ten_loai NVARCHAR(255) NOT NULL
)
GO
CREATE TABLE SANPHAM(
	id_sanpham INT IDENTITY(1,1) PRIMARY KEY,
	ten_sanpham NVARCHAR(255) NOT NULL,
	soluong INT NOT NULL,
	hinh VARCHAR(255) NULL,
	mota NVARCHAR(MAX) NOT NULL,
	motangan NVARCHAR(MAX) NULL,
	gia INT NOT NULL,
	giamgia INT NOT NULL,
	ngaytao DATE NOT NULL,
	id_loai INT FOREIGN KEY REFERENCES Loai(id_loai)
)
GO
CREATE TABLE HOADON(
	id_hoadon INT IDENTITY(1,1) PRIMARY KEY,
	ngaytao DATE NOT NULL,
	trangthai VARCHAR(30) NOT NULL,
	diachi NVARCHAR(50) NOT NULL,
	giaohang NVARCHAR(MAX) NULL,
	id_user VARCHAR(50) FOREIGN KEY REFERENCES Users(id_user)
)
GO
CREATE TABLE HOADONCHITIET(
	id_hoadon INT,
	id_sanpham INT,
	soluong INT NOT NULL,
	giamgia INT NULL,
	gia INT NOT NULL,
	PRIMARY KEY (id_hoadon, id_sanpham),
    FOREIGN KEY (id_hoadon) REFERENCES HoaDon(id_hoadon),
    FOREIGN KEY (id_sanpham) REFERENCES SanPham(id_sanpham)
)
GO
CREATE TABLE GIOHANG(
	id_giohang INT IDENTITY(1,1) PRIMARY KEY,
	id_user VARCHAR(50) FOREIGN KEY REFERENCES Users(id_user)
)
GO
CREATE TABLE GIOHANGCHITIET(
	id_giohang INT,
	id_sanpham INT,
	soluong INT NOT NULL,
	PRIMARY KEY (id_giohang, id_sanpham),
    FOREIGN KEY (id_giohang) REFERENCES GioHang(id_giohang),
    FOREIGN KEY (id_sanpham) REFERENCES SanPham(id_sanpham)
)
GO
-- Tạo bảng KHACH_HANG nếu chưa tồn tại
IF OBJECT_ID('KHACH_HANG', 'U') IS NULL
BEGIN
    CREATE TABLE KHACH_HANG (
        id_khach_hang VARCHAR(50) PRIMARY KEY,
        hoten VARCHAR(255) NOT NULL,
        sdt VARCHAR(10) NOT NULL,
        email VARCHAR(255),
        dia_chi VARCHAR(255),
        trang_thai BIT NOT NULL,
        phan_loai VARCHAR(50)
    );
END
GO

-- Tạo bảng NHAN_VIEN nếu chưa tồn tại
IF OBJECT_ID('NHAN_VIEN', 'U') IS NULL
BEGIN
    CREATE TABLE NHAN_VIEN (
        id_nhan_vien VARCHAR(50) PRIMARY KEY,
        hoten VARCHAR(255) NOT NULL,
        sdt VARCHAR(10) NOT NULL,
        email VARCHAR(255),
        dia_chi VARCHAR(255),
        trang_thai BIT NOT NULL,
        luong DECIMAL(18,2),
        ca_lam_viec VARCHAR(100)
    );
END
GO

GO
select * from GIOHANGCHITIET
delete from GIOHANGCHITIET