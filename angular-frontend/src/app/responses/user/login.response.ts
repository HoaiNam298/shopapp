// export interface LoginResponse {
//     message: string,
//     token: string
// }
export interface LoginResponse<T = any> {
    status: string; // "success" hoặc "error"
    message: string; // Thông báo từ server
    data: T; // Dữ liệu trả về từ server (token, user info, ...)
}