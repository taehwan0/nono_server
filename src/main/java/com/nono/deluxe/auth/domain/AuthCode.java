package com.nono.deluxe.auth.domain;

import com.nono.deluxe.common.domain.BaseTimeEntity;
import com.nono.deluxe.user.domain.User;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class AuthCode extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "authCode_user"))
    private User user;

    @Column(nullable = false)
    private String code;

    @Builder
    public AuthCode(User user, String verifyCode) {
        this.user = user;
        this.code = verifyCode;
    }
}
