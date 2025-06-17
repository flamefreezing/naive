package app.entity;

import common.constant.PrivacyLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_settings")
public class UserSetting extends BaseEntity {

    @Id
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private PrivacyLevel privacyLevel;

    private Boolean allowFriendRequests = true;

    private Boolean showOnlineStatus = true;

    private Boolean emailNotifications = true;

    private Boolean pushNotifications = true;

    @Column(length = 20)
    private String language = "vi";

    private String timezone = "Asia/Ho_Chi_Minh";
}
