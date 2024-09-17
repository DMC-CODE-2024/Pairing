package com.eirs.pairs.repository;

import com.eirs.pairs.repository.entity.GuiAuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GuiAuditTrailRepository extends JpaRepository<GuiAuditTrail, Long> {

}
