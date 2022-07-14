package com.nono.deluxe.domain.user.participant;

import com.nono.deluxe.domain.BaseTimeEntity;
import com.nono.deluxe.domain.user.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class ParticipantUser {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String authCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
