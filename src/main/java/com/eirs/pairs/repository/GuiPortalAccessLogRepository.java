package com.eirs.pairs.repository;

import com.eirs.pairs.repository.entity.GuiPortalAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GuiPortalAccessLogRepository extends JpaRepository<GuiPortalAccessLog, Long> {

}
