package com.nono.deluxe.domain.checkemail;

import com.nono.deluxe.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class CheckEmail extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String verifyCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckType type;

    @Builder
    public CheckEmail(String email, String verifyCode, CheckType type) {
        this.email = email;
        this.verifyCode = verifyCode;
        this.type = type;
    }
}
