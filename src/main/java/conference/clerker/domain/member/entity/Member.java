package conference.clerker.domain.member.entity;

import conference.clerker.domain.organization.entity.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false, name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    public static Member create(String username, String email) {
        return Member.builder()
                .email(email)
                .username(username)
                .build();
    }
}
