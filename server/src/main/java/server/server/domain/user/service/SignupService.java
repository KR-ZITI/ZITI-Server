package server.server.domain.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import server.server.domain.user.domain.User;
import server.server.domain.user.domain.repository.UserRepository;
import server.server.domain.user.enums.Role;
import server.server.domain.user.presentation.dto.request.SignupRequest;
import server.server.global.error.ErrorCode;
import server.server.global.error.exception.CustomException;

@Service
@RequiredArgsConstructor
public class SignupService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void execute(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EXIST_EMAIL);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);
    }
}
