package com.nishanth.ecommerce.service;

import com.nishanth.ecommerce.dto.UpdateProfileRequest;
import com.nishanth.ecommerce.dto.UpdateProfileResponse;
import com.nishanth.ecommerce.dto.UserProfileResponse;
import com.nishanth.ecommerce.entity.User;
import com.nishanth.ecommerce.exception.EmailAlreadyExistsException;
import com.nishanth.ecommerce.exception.UserNotFoundException;
import com.nishanth.ecommerce.repository.UserRepository;
import com.nishanth.ecommerce.security.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nishanth.ecommerce.dto.ChangePasswordRequest;
import com.nishanth.ecommerce.exception.InvalidPasswordException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder=passwordEncoder;
        this.jwtService=jwtService;
    }

    public UserProfileResponse getMyProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
    @Transactional
    public UpdateProfileResponse updateMyProfile(
            String currentEmail,
            UpdateProfileRequest request
    ) {

        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        if (!currentEmail.equalsIgnoreCase(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {

            throw new EmailAlreadyExistsException(
                    "Email already exists"
            );
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        User updatedUser = userRepository.save(user);

        String token = jwtService.generateToken(
                updatedUser.getId(),
                updatedUser.getEmail(),
                updatedUser.getRole().name()
        );

        UserProfileResponse profile = new UserProfileResponse(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getRole(),
                updatedUser.getCreatedAt()
        );

        return new UpdateProfileResponse(profile, token);
    }
    @Transactional
    public void changePassword(
            String email,
            ChangePasswordRequest request
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }
}