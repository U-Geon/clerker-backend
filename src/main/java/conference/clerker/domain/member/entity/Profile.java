package conference.clerker.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Profile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false, name = "profile_id")
    private Long id;

    @Column(name = "url")
    private String url;

    @Column(name = "filename")
    private String filename;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static Profile create(Member member, String url, String filename) {
        return Profile.builder()
                .member(member)
                .url(url)
                .filename(filename)
                .build();
    }
}
