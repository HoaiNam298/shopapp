package com.project.shopapp.ultils;

import java.util.regex.Pattern;

public class ValidationUtils {
    public static boolean isValidEmail(String email) {
        // Chỉ chấp nhận email có đuôi @gmail.com
        String emailRegex = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
        Pattern pattern = Pattern.compile(emailRegex);

        return email != null && pattern.matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^0\\d{9}$"; // Số điện thoại Việt Nam: Bắt đầu bằng 0, có 10 số
        Pattern pattern = Pattern.compile(phoneRegex);
        return phoneNumber != null && pattern.matcher(phoneNumber).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

}
