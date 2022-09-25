package com.nono.deluxe.domain.authcode;

import com.nono.deluxe.domain.BaseTimeEntity;
import com.nono.deluxe.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
