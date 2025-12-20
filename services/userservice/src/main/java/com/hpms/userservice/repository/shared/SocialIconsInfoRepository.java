package com.hpms.userservice.repository.shared;

import com.hpms.userservice.model.SocialIconInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialIconsInfoRepository extends JpaRepository<SocialIconInfo, Long> {

}
