package com.hpms.userservice.service.impl;

import com.hpms.userservice.model.ConfirmationToken;
import com.hpms.userservice.model.User;
import com.hpms.userservice.repository.ConfirmationTokenRepository;
import com.hpms.userservice.service.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ConfirmationTokenServiceImp implements ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Override
    public ConfirmationToken getConfirmationToken(String token) {
        return confirmationTokenRepository.getByToken(token).orElse(null);
    }

    @Override
    public ConfirmationToken generateConfirmationToken(User user) {
        String token = UUID.randomUUID().toString();
        confirmationTokenRepository.expireAllToken(user.getId());
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user);
        confirmationTokenRepository.save(confirmationToken);
        return confirmationToken;
    }

    @Override
    public int confirmToken(String token) {
        return confirmationTokenRepository.confirmToken(token, LocalDateTime.now());
    }

}
