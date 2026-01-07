package com.hpms.userservice.repository.shared;

import com.hpms.userservice.model.shared.LanguageName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageNameRepository extends JpaRepository<LanguageName, String> {
}
