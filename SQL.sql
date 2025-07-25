USE [STORE]
GO
/****** Object:  Table [dbo].[GIOHANG]    Script Date: 7/26/2025 12:14:54 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[GIOHANG](
	[id_giohang] [int] IDENTITY(1,1) NOT NULL,
	[id_user] [varchar](50) NULL,
	[id_khach_hang] [varchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[id_giohang] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[GIOHANGCHITIET]    Script Date: 7/26/2025 12:14:54 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[GIOHANGCHITIET](
	[id_giohang] [int] NOT NULL,
	[id_sanpham] [int] NOT NULL,
	[soluong] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id_giohang] ASC,
	[id_sanpham] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[HOADON]    Script Date: 7/26/2025 12:14:54 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[HOADON](
	[id_hoadon] [int] IDENTITY(1,1) NOT NULL,
	[ngaytao] [date] NOT NULL,
	[trangthai] [varchar](30) NOT NULL,
	[diachi] [nvarchar](50) NOT NULL,
	[giaohang] [nvarchar](max) NULL,
	[id_user] [varchar](50) NULL,
	[id_khach_hang] [varchar](50) NULL,
	[discount_percent] [int] NULL,
	[discount_amount] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[id_hoadon] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[HOADONCHITIET]    Script Date: 7/26/2025 12:14:54 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[HOADONCHITIET](
	[id_hoadon] [int] NOT NULL,
	[id_sanpham] [int] NOT NULL,
	[soluong] [int] NOT NULL,
	[giamgia] [int] NULL,
	[gia] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id_hoadon] ASC,
	[id_sanpham] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[KHACH_HANG]    Script Date: 7/26/2025 12:14:54 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[KHACH_HANG](
	[id_khach_hang] [bigint] IDENTITY(1,1) NOT NULL,
	[hoten] [varchar](255) NOT NULL,
	[sdt] [varchar](10) NOT NULL,
	[email] [varchar](255) NULL,
	[dia_chi] [varchar](255) NULL,
	[trang_thai] [bit] NOT NULL,
	[phan_loai] [nvarchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id_khach_hang] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[LOAI]    Script Date: 7/26/2025 12:14:54 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[LOAI](
	[id_loai] [int] IDENTITY(1,1) NOT NULL,
	[ten_loai] [nvarchar](255) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id_loai] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[NHAN_VIEN]    Script Date: 7/26/2025 12:14:54 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[NHAN_VIEN](
	[id_nhan_vien] [varchar](50) NOT NULL,
	[hoten] [varchar](255) NOT NULL,
	[sdt] [varchar](10) NOT NULL,
	[email] [varchar](255) NULL,
	[dia_chi] [varchar](255) NULL,
	[trang_thai] [bit] NOT NULL,
	[luong] [decimal](18, 2) NULL,
	[ca_lam_viec] [varchar](100) NULL,
PRIMARY KEY CLUSTERED 
(
	[id_nhan_vien] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[NOTIFICATION]    Script Date: 7/26/2025 12:14:54 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[NOTIFICATION](
	[id_notification] [int] IDENTITY(1,1) NOT NULL,
	[id_user] [varchar](50) NULL,
	[title] [nvarchar](255) NOT NULL,
	[content] [nvarchar](max) NOT NULL,
	[is_read] [bit] NOT NULL,
	[created_at] [datetime] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id_notification] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[SANPHAM]    Script Date: 7/26/2025 12:14:54 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[SANPHAM](
	[id_sanpham] [int] IDENTITY(1,1) NOT NULL,
	[ten_sanpham] [nvarchar](255) NOT NULL,
	[soluong] [int] NOT NULL,
	[hinh] [varchar](255) NULL,
	[mota] [nvarchar](max) NOT NULL,
	[motangan] [nvarchar](max) NULL,
	[gia] [int] NOT NULL,
	[giamgia] [int] NOT NULL,
	[ngaytao] [date] NOT NULL,
	[id_loai] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[id_sanpham] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[USERS]    Script Date: 7/26/2025 12:14:54 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[USERS](
	[id_user] [varchar](50) NOT NULL,
	[sdt] [varchar](10) NOT NULL,
	[hinh] [varchar](255) NULL,
	[hoten] [nvarchar](50) NOT NULL,
	[matkhau] [varchar](50) NOT NULL,
	[kichhoat] [bit] NOT NULL,
	[vaitro] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id_user] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[NOTIFICATION] ADD  DEFAULT ((0)) FOR [is_read]
GO
ALTER TABLE [dbo].[NOTIFICATION] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[GIOHANG]  WITH CHECK ADD FOREIGN KEY([id_user])
REFERENCES [dbo].[USERS] ([id_user])
GO
ALTER TABLE [dbo].[GIOHANGCHITIET]  WITH CHECK ADD FOREIGN KEY([id_giohang])
REFERENCES [dbo].[GIOHANG] ([id_giohang])
GO
ALTER TABLE [dbo].[GIOHANGCHITIET]  WITH CHECK ADD FOREIGN KEY([id_sanpham])
REFERENCES [dbo].[SANPHAM] ([id_sanpham])
GO
ALTER TABLE [dbo].[HOADON]  WITH CHECK ADD FOREIGN KEY([id_user])
REFERENCES [dbo].[USERS] ([id_user])
GO
ALTER TABLE [dbo].[HOADONCHITIET]  WITH CHECK ADD FOREIGN KEY([id_hoadon])
REFERENCES [dbo].[HOADON] ([id_hoadon])
GO
ALTER TABLE [dbo].[HOADONCHITIET]  WITH CHECK ADD FOREIGN KEY([id_sanpham])
REFERENCES [dbo].[SANPHAM] ([id_sanpham])
GO
ALTER TABLE [dbo].[NOTIFICATION]  WITH CHECK ADD FOREIGN KEY([id_user])
REFERENCES [dbo].[USERS] ([id_user])
GO
ALTER TABLE [dbo].[SANPHAM]  WITH CHECK ADD FOREIGN KEY([id_loai])
REFERENCES [dbo].[LOAI] ([id_loai])
GO
