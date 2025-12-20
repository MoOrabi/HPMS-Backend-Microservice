package com.hpms.userservice.repository.shared;

import com.hpms.userservice.model.UserSocialLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSocialLinkRepository extends JpaRepository<UserSocialLink, Long> {

}
