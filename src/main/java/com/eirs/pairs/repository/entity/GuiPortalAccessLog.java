package com.eirs.pairs.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "portal_access_log", catalog = "oam")
public class GuiPortalAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_agent")
    private String userAgent;
    @Column(name = "public_ip")
    private String publicIp;
    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;
    @Column(name = "user_name")
    private String username;
    @Column(name = "browser")
    private String browser;


}
