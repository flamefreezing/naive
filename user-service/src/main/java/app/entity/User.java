package app.entity;

import common.constant.Gender;
import common.constant.PrivacyLevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(length = 255)
    private String fullName;

    @Column(length = 255)
    private String profilePictureUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(length = 255)
    private String location;

    @Column(length = 255)
    private String website;

    @Builder.Default
    private Boolean isVerified = false;

    @Builder.Default
    private Boolean isPrivate = false;

    @Builder.Default
    private Boolean isActive = true;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserSetting userSetting;
}
