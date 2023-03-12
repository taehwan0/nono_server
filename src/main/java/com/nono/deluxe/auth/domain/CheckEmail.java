package com.nono.deluxe.auth.domain;

import com.nono.deluxe.common.domain.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class CheckEmail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String verifyCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckType type;

    @Column(nullable = false)
    private boolean verified;

    @Builder
    public CheckEmail(String email, String verifyCode, CheckType type) {
        this.email = email;
        this.verifyCode = verifyCode;
        this.type = type;
        this.verified = false;
    }

    public void verify() {
        this.verified = true;
    }
}
