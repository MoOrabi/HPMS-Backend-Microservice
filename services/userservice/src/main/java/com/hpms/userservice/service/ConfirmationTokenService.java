package com.hpms.userservice.service;

import com.hpms.userservice.model.ConfirmationToken;
import com.hpms.userservice.model.User;

public interface ConfirmationTokenService {


    ConfirmationToken getConfirmationToken(String token);

    ConfirmationToken generateConfirmationToken(User user);

    int confirmToken(String token);

}
