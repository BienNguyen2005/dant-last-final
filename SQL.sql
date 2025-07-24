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
select * from GIOHANGCHITIET
delete from GIOHANGCHITIET