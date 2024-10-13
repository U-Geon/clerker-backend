package conference.clerker.domain.member.schema;

// Image 클래스
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Image {
    private Member member;
    private String url;

    // URL을 업데이트하는 메서드
    public void updateUrl(String newUrl) {
        this.url = newUrl;
    }

    // getUrl 메서드 (Lombok @Getter가 자동 생성)
}