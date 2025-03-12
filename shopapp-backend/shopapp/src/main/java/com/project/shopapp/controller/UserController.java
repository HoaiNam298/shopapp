package com.project.shopapp.controller;

import com.project.shopapp.dtos.RefreshTokenDTO;
import com.project.shopapp.dtos.UpdateUserDTO;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.dtos.UserLoginDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidPasswordException;
import com.project.shopapp.model.Token;
import com.project.shopapp.model.User;
import com.project.shopapp.response.*;
import com.project.shopapp.services.AuthService;
import com.project.shopapp.services.TokenService;
import com.project.shopapp.services.UserService;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.ultils.MessageKeys;
import com.project.shopapp.ultils.ValidationUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final LocalizationUtils localizationUtils;
    private final TokenService tokenService;
    private final AuthService authService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ){
        try {
            PageRequest pageRequest = PageRequest.of(
                    page, limit,
                    Sort.by("id").ascending()
            );
            Page<UserResponse> userPage = userService.findAllUser(keyword, pageRequest)
                    .map(UserResponse::fromUser);

            //Lay tong so trang
            int totalPages = userPage.getTotalPages();
            List<UserResponse> userResponses = userPage.getContent();

            return ResponseEntity.ok(UserListResponse
                    .builder()
                    .users(userResponses)
                    .totalPages(totalPages)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result
            ) {
        try {
            if (result.hasErrors()){
                List<String> errorMessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(
                        ResponseObject.builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .data(null)
                                .message(errorMessage.toString())
                                .build());
            }
            if (userDTO.getEmail() == null || userDTO.getEmail().trim().isBlank()) {
                if (userDTO.getPhoneNumber() == null || userDTO.getPhoneNumber().isBlank()) {
                    return ResponseEntity.badRequest().body(
                            ResponseObject.builder()
                                    .status(HttpStatus.BAD_REQUEST)
                                    .data(null)
                                    .message("At least email or phone number is required!")
                                    .build());
                } else {
                    if (!ValidationUtils.isValidPhoneNumber(userDTO.getPhoneNumber())) {
                        throw  new Exception("Invalid phone number");
                    }
                }
            } else {
                if (!ValidationUtils.isValidEmail(userDTO.getEmail())) {
                    throw new Exception("Invalid email format");
                }
            }
            if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body(
                        ResponseObject.builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .data(null)
                                .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH))
                                .build());
            }
            User user = userService.createUser(userDTO);
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.CREATED)
                            .data(UserResponse.fromUser(user))
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_SUCCESSFULLY))
                            .build());
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(RegisterResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_FAILED, e.getMessage()))
                    .build());
        }
    }

    private boolean isMobileDevice(String userAgent) {
        return userAgent.toLowerCase().contains("mobile");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO,
            HttpServletRequest request
            ) throws Exception{
        // Kiểm tra thông tin đăng nhập và sinh token
        try {
            String token = userService.login(userLoginDTO);

            String userAgent = request.getHeader("User-Agent");
            User user = userService.getUserDetailsFromToken(token);
            Token jwtToken = tokenService.addToken(user, token, isMobileDevice(userAgent));

//            return ResponseEntity.ok(LoginResponse.builder()
//                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
//                            .id(user.getId())
//                            .token(jwtToken.getToken())
//                            .tokenType(jwtToken.getTokenType())
//                            .refreshToken(jwtToken.getRefreshToken())
//                            .username(user.getPhoneNumber())
//                            .roles(user.getAuthorities().stream()
//                                    .map(item -> item.getAuthority()).toList())
//                            .build());

            LoginResponse loginResponse = LoginResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                    .token(jwtToken.getToken())
                    .tokenType(jwtToken.getTokenType())
                    .refreshToken(jwtToken.getRefreshToken())
                    .username(user.getUsername())
                    .roles(user.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                    .id(user.getId())
                    .build();

            return ResponseEntity.ok(ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                            .data(loginResponse)
                            .status(HttpStatus.OK)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(LoginResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED, e.getMessage()))
                    .build());
        }
        // Trả về token trong respon

    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenDTO refreshTokenDTO
    ) {
        try {
            User user = userService.getUserDetailsFromRefreshToken(refreshTokenDTO.getRefreshToken());
            Token jwtToken = tokenService.refreshToken(refreshTokenDTO.getRefreshToken(), user);
            return ResponseEntity.ok(LoginResponse.builder()
                    .id(user.getId())
                    .message("Refresh toke successfully")
                    .token(jwtToken.getToken())
                    .tokenType(jwtToken.getTokenType())
                    .refreshToken(jwtToken.getRefreshToken())
                    .username(user.getPhoneNumber())
                    .roles(user.getAuthorities().stream()
                            .map(item -> item.getAuthority()).toList())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(LoginResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED))
                    .build());
        }
    }

    @PostMapping("/details")
    public ResponseEntity<UserResponse> getUserDetails(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String extractedToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            return ResponseEntity.ok(UserResponse.fromUser(user));
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/details/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable long userId,
            @RequestBody UpdateUserDTO updateUserDto,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String extractedToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            if (user.getId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            User updateUser = userService.updateUser(userId, updateUserDto);
            return ResponseEntity.ok(UserResponse.fromUser(updateUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/reset-password/{userId}")
    public ResponseEntity<?> resetPassword(
            @Valid @PathVariable long userId
    ){
        try {
//            String newPassword = UUID.randomUUID().toString().substring(0, 5);
            String newPassword = "123456";
            userService.resetPassword(userId, newPassword);
            return ResponseEntity.ok(newPassword);
        } catch (InvalidPasswordException e) {
            return ResponseEntity.badRequest().body("Invalid password");
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body("User not found");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/block-enabled/{userId}/{active}")
    public ResponseEntity<?> blockOrEnable(
            @Valid @PathVariable long userId,
            @Valid @PathVariable int active
    ){
        try {
            userService.blockOrEnable(userId, active > 0);
            String message = active > 0 ? "Successfully enabled the user." : "Successfully block the user.";
            return ResponseEntity.ok(message);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body("User not found");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    //Frontend login google -> trang đăng nhập gg -> có code
    //code -> google token -> lấy các thông tin khác
    @GetMapping("/auth/social-login")
    public ResponseEntity<?> socialAuth(
            @RequestParam("login_type") String loginType,
            HttpServletRequest request
    ) {
        loginType = loginType.trim().toLowerCase();
        String url = authService.generateAuthUrl(loginType);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/auth/social/callback")
    public ResponseEntity<?> callback(
            @RequestParam("code") String code,
            @RequestParam("login_type") String loginType,
            HttpServletRequest request
    ) throws Exception {
        //Call the AuthService to get user info
        Map<String, Object> userInfor = authService.authenticateAndFetchProfile(code, loginType);

        if (userInfor == null) {
            return ResponseEntity.badRequest().body(new ResponseObject(
                    "Failed to authenticate", HttpStatus.BAD_REQUEST, null
            ));
        }

        //Extract user information from userInfo map
        String accountId = "";
        String name = "";
        String picture = "";
        String email = "";

        if (loginType.trim().equals("google")) {
            accountId = (String) Objects.requireNonNullElse(userInfor.get("sub"), "");
            name = (String) Objects.requireNonNullElse(userInfor.get("name"), "");
            picture = (String) Objects.requireNonNullElse(userInfor.get("picture"), "");
            email = (String) Objects.requireNonNullElse(userInfor.get("email"), "");
        } else if (loginType.trim().equals("facebook")) {
            accountId = (String) Objects.requireNonNullElse(userInfor.get("id"), "");
            name = (String) Objects.requireNonNullElse(userInfor.get("name"), "");
            email = (String) Objects.requireNonNullElse(userInfor.get("email"), "");
            //Lấy URL ảnh từ ctdl của fb
            Object pictureObj = userInfor.get("picture");
            if (pictureObj instanceof Map) {
                Map<?, ?> pictureData = (Map<?, ?>) pictureObj;
                Object dataObj = pictureData.get("data");
                if (dataObj instanceof Map) {
                    Map<?, ?> dataMap = (Map<?, ?>) dataObj;
                    Object urlObj = dataMap.get("url");
                    if (urlObj instanceof String) {
                        picture = (String) urlObj;
                    }
                }
            }
        }

        //Tạo đối tượng userLoginDTO
        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .email(email)
                .fullName(name)
                .password("")
                .phoneNumber("")
                .build();
        if (loginType.trim().equals("google")) {
            userLoginDTO.setGoogleAccountId(accountId);
//            userLoginDTO.setFacebookAccountId("");
        } else if (loginType.trim().equals("facebook")) {
            userLoginDTO.setFacebookAccountId(accountId);
//            userLoginDTO.setGoogleAccountId("");
        }

        return this.login(userLoginDTO, request);
    }
}
