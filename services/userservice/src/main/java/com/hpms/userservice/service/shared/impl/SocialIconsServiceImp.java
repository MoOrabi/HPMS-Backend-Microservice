package com.hpms.userservice.service.shared.impl;

import com.hpms.userservice.model.shared.SocialIconInfo;
import com.hpms.userservice.repository.shared.SocialIconsInfoRepository;
import com.hpms.userservice.service.shared.SocialIconsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SocialIconsServiceImp implements SocialIconsService {

    @Autowired
    private SocialIconsInfoRepository socialIconsInfoRepository;

    @Override
    public List<SocialIconInfo> getAllIcons() {
        return socialIconsInfoRepository.findAll();
    }

}
