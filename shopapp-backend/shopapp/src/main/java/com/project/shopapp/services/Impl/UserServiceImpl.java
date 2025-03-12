package com.project.shopapp.services.Impl;

import com.project.shopapp.components.JwtTokenUtils;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.UpdateUserDTO;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.dtos.UserLoginDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidPasswordException;
import com.project.shopapp.exceptions.PermissionDenyException;
import com.project.shopapp.model.Role;
import com.project.shopapp.model.Token;
import com.project.shopapp.model.User;
import com.project.shopapp.repositories.RoleRepository;
import com.project.shopapp.repositories.TokenRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.services.UserService;
import com.project.shopapp.ultils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final TokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenUtils jwtTokenUtil;

    private final AuthenticationManager authenticationManager;

    private final LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public User createUser(UserDTO userDTO) throws Exception {
        String phoneNumber = userDTO.getPhoneNumber();
        String email = userDTO.getEmail();
        // Nếu phoneNumber không null thì mới kiểm tra tồn tại
        if (phoneNumber != null && userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException(localizationUtils.getLocalizedMessage(MessageKeys.PHONE_EXISTS));
        }

        // Nếu email không null thì mới kiểm tra tồn tại
        if (email != null && userRepository.existsByEmail(email)) {
            throw new DataIntegrityViolationException(localizationUtils.getLocalizedMessage(MessageKeys.EMAIL_EXISTS));
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_NOT_FOUND)));
        if (role.getName().toUpperCase().equals(Role.ADMIN)) {
            throw new PermissionDenyException(localizationUtils.getLocalizedMessage(MessageKeys.CANNOT_REGISTER_ADMIN));
        }

        //conver từ userDTO sang user
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .isActive(true)
                .build();

        newUser.setRole(role);
        //Kiểm tra nếu có accountId, không yêu cầu password
        if (userDTO.getFacebookAccountId() == null && userDTO.getGoogleAccountId() == null) {
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }
        return userRepository.save(newUser);
    }

    @Override
    public String login(UserLoginDTO userLoginDTO) throws Exception {
        Optional<User> optionalUser = Optional.empty();
        String subject = null;
        if (roleRepository.findByName(Role.USER) == null) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_NOT_FOUND));
        }

        if (userLoginDTO.getGoogleAccountId() != null) {
            optionalUser = userRepository.findByGoogleAccountId(userLoginDTO.getGoogleAccountId());
            subject = "Google:" + userLoginDTO.getGoogleAccountId();
            //Nếu không tìm thấy người dùng tạo bản ghi mới
            if (optionalUser.isEmpty()) {
                User newUser = User.builder()
                        .fullName(userLoginDTO.getFullName() != null ? userLoginDTO.getFullName() : "")
                        .email(userLoginDTO.getEmail() != null ? userLoginDTO.getEmail() : "")
                        .role(roleRepository.findByName(Role.USER))
                        .googleAccountId(userLoginDTO.getGoogleAccountId())
                        .password("")
                        .isActive(true)
                        .build();
                newUser = userRepository.save(newUser);
                //Optional trở thành có giá trị với người dùng mới
                optionalUser = Optional.of(newUser);
            }

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("email", userLoginDTO.getEmail());
            return jwtTokenUtil.generationToken(optionalUser.get());
        }

        //Check if the user exists by phone number
        if (userLoginDTO.getPhoneNumber() !=null && !userLoginDTO.getPhoneNumber().isBlank()) {
            optionalUser = userRepository.findByPhoneNumber(userLoginDTO.getPhoneNumber());
            subject = userLoginDTO.getPhoneNumber();
        }
        //Check if the user is not found by phone number, check by email
        if (userLoginDTO.getEmail() !=null && optionalUser.isEmpty()) {
            optionalUser = userRepository.findByEmail(userLoginDTO.getEmail());
            subject = userLoginDTO.getEmail();
        }

        //If the user is not found, throw an exception
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.INVALID_PHONE_PASS));
        }

        //Get the existing user
        User existingUser = optionalUser.get();

        //check password
        if (existingUser.getFacebookAccountId() == null
                && existingUser.getGoogleAccountId() == null) {
            if (!passwordEncoder.matches(userLoginDTO.getPassword(), existingUser.getPassword())) {
                throw new BadCredentialsException(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PASS_WORD));
            }
        }

//        Optional<Role> optionalRole = roleRepository.findById(roleId);
//        if (optionalRole.isEmpty() || !roleId.equals(existingUser.getRole().getId())) {
//            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_NOT_FOUND));
//        }

        if (!optionalUser.get().getIsActive()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_IS_LOCKED));
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                subject, userLoginDTO.getPassword(),
                existingUser.getAuthorities()
        );
        //authenticate with Java Spring security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generationToken(existingUser);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if (jwtTokenUtil.isTokenExpired(token)) {
            throw new Exception("Token is expired");
        }

        // Lấy subject từ token (số điện thoại hoặc email)
        String subject = jwtTokenUtil.extractSubject(token);

        // Lấy email từ claims nếu có
        String email = jwtTokenUtil.extractSubject(token);

        // Kiểm tra xem subject là số điện thoại hay email
        Optional<User> user = userRepository.findByPhoneNumber(subject);
        if (user.isEmpty()) {
            user = userRepository.findByEmail(email); // Nếu không tìm thấy bằng phone, thử tìm bằng email
        }

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new Exception("User not found");
        }
    }


    @Override
    @Transactional
    public User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception {
        // Tìm user theo ID
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataIntegrityViolationException(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND)));

        // Kiểm tra nếu số điện thoại mới đã tồn tại và không phải của chính user đang cập nhật
        String newPhoneNumber = updatedUserDTO.getPhoneNumber();
        if (!existingUser.getPhoneNumber().equals(newPhoneNumber) && userRepository.existsByPhoneNumber(newPhoneNumber)) {
            throw new DataIntegrityViolationException(localizationUtils.getLocalizedMessage(MessageKeys.PHONE_EXISTS));
        }

        // Cập nhật thông tin từ DTO
        existingUser.setFullName(
                updatedUserDTO.getFullName() != null ? updatedUserDTO.getFullName() : existingUser.getFullName()
        );
        existingUser.setPhoneNumber(
                updatedUserDTO.getPhoneNumber() != null ? updatedUserDTO.getPhoneNumber() : existingUser.getPhoneNumber()
        );
        existingUser.setAddress(
                updatedUserDTO.getAddress() != null ? updatedUserDTO.getAddress() : existingUser.getAddress()
        );
        existingUser.setDateOfBirth(
                updatedUserDTO.getDateOfBirth() != null ? updatedUserDTO.getDateOfBirth() : existingUser.getDateOfBirth()
        );
        existingUser.setFacebookAccountId(
                updatedUserDTO.getFacebookAccountId() != null ? updatedUserDTO.getFacebookAccountId() : existingUser.getFacebookAccountId()
        );
        existingUser.setGoogleAccountId(
                updatedUserDTO.getGoogleAccountId() != null ? updatedUserDTO.getGoogleAccountId() : existingUser.getGoogleAccountId()
        );
//        existingUser.setRole(role);
        // Nếu tài khoản liên kết Facebook/Google không tồn tại và có thay đổi mật khẩu
        if (updatedUserDTO.getPassword() != null
                && !updatedUserDTO.getPassword().isEmpty()
                && updatedUserDTO.getFacebookAccountId() == null
                && updatedUserDTO.getGoogleAccountId() == null) {
            if (!updatedUserDTO.getPassword().equals(updatedUserDTO.getRetypePassword())) {
                throw new DataNotFoundException("Password and retype password not the same");
            }
            String encodedPassword = passwordEncoder.encode(updatedUserDTO.getPassword());
            existingUser.setPassword(encodedPassword);
        }

        return userRepository.save(existingUser);
    }

    @Override
    public User getUserDetailsFromRefreshToken(String refreshToken) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        return getUserDetailsFromToken(existingToken.getToken());
    }

    @Override
    public Page<User> findAllUser(String keyword, Pageable pageable) throws Exception {
        return userRepository.findAllUser(keyword, pageable);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, String newPassword) throws InvalidPasswordException, DataNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found."));
        String encodedPassword = passwordEncoder.encode(newPassword);
        existingUser.setPassword(encodedPassword);
        userRepository.save(existingUser);
        //Reset password => clear token
        List<Token> tokens = tokenRepository.findByUser(existingUser);
        for (Token token : tokens) {
            tokenRepository.delete(token);
        }
    }

    @Override
    @Transactional
    public void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found."));

        existingUser.setIsActive(active);
        userRepository.save(existingUser);
    }

}
