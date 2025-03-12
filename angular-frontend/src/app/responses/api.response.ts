export interface ApiResponse<T = any> {
    status: string; // "success" hoặc "error"
    message: string; // Thông báo từ server
    data: T; // Dữ liệu trả về từ server (token, user info, ...)
  }
  