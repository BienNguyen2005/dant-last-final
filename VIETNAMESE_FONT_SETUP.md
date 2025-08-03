# Cấu hình Font Tiếng Việt cho In Hóa Đơn

## Vấn đề đã được khắc phục

Ứng dụng đã được cập nhật để hỗ trợ font tiếng Việt tốt hơn khi in hóa đơn. Các thay đổi bao gồm:

### 1. Font Stack được tối ưu hóa
- **Segoe UI**: Font chính cho Windows
- **Arial Unicode MS**: Hỗ trợ Unicode tốt
- **DejaVu Sans**: Font mã nguồn mở hỗ trợ đa ngôn ngữ
- **Times New Roman**: Font serif fallback
- **Inter**: Font Google Fonts hiện đại
- **Arial**: Font fallback cuối cùng

### 2. Các file đã được cập nhật

#### `src/main/resources/templates/admin/hoadon/hoadonPrintView.html`
- Thêm charset UTF-8
- Cập nhật font-family với hỗ trợ tiếng Việt
- Thêm CSS cho print media
- Sử dụng class `vietnamese-font` và `invoice-print`

#### `src/main/resources/templates/admin/hoadon/hoadonManagerDetailView.html`
- Cập nhật print media styles
- Thêm font smoothing
- Sử dụng CSS chung cho font tiếng Việt

#### `resources/static/pos.html`
- Cập nhật hàm `printReceipt()`
- Thêm charset UTF-8 cho print window
- Sử dụng font stack tối ưu

#### `resources/static/css/vietnamese-fonts.css` (Mới)
- File CSS chung cho font tiếng Việt
- Import Google Fonts Inter
- Print-specific styles
- Fallback fonts cho các hệ điều hành khác nhau

### 3. Tính năng mới

- **Font Smoothing**: Cải thiện hiển thị font trên màn hình
- **Print Color Adjust**: Đảm bảo màu sắc in chính xác
- **Text Rendering**: Tối ưu hóa hiển thị text
- **East Asian Font Variants**: Hỗ trợ cho các ký tự đặc biệt

## Cách sử dụng

### Trong HTML
```html
<body class="vietnamese-font">
  <div class="invoice-print">
    <!-- Nội dung hóa đơn -->
  </div>
</body>
```

### Trong CSS
```css
.vietnamese-text {
  font-family: 'Segoe UI', 'Arial Unicode MS', 'DejaVu Sans', 'Times New Roman', 'Inter', Arial, sans-serif;
}
```

## Kiểm tra

Để kiểm tra font tiếng Việt hoạt động tốt:

1. In hóa đơn từ trang admin
2. In hóa đơn từ POS
3. Kiểm tra các ký tự đặc biệt: ă, â, ê, ô, ơ, ư, đ
4. Kiểm tra trên các trình duyệt khác nhau
5. Kiểm tra khi in thực tế

## Lưu ý

- Đảm bảo server có thể serve file CSS `/static/css/vietnamese-fonts.css`
- Font Inter sẽ được tải từ Google Fonts nếu có kết nối internet
- Fallback fonts sẽ được sử dụng nếu font chính không có sẵn
- Print styles sẽ override font settings khi in

## Troubleshooting

Nếu vẫn gặp vấn đề với font:

1. Kiểm tra charset UTF-8 trong HTML
2. Đảm bảo file CSS được load thành công
3. Kiểm tra console browser để xem lỗi
4. Thử thay đổi font stack trong CSS
5. Kiểm tra cài đặt font trên hệ điều hành 