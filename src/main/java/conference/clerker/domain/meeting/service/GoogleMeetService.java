package conference.clerker.domain.meeting.service;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import conference.clerker.domain.meeting.schema.Meeting;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;

import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleMeetService {

    private final OAuth2AuthorizedClientService authorizedClientService;

    // JSON 처리에 사용할 JsonFactory 설정 (Gson 사용)
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // 애플리케이션 이름 설정
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";

    @SneakyThrows
    public String createMeeting(String summary, LocalDateTime startDateTime) {

        // SecurityContextHolder에 저장된 google access token 불러오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauth2Token.getAuthorizedClientRegistrationId(),
                oauth2Token.getName());

        String accessToken = client.getAccessToken().getTokenValue();

        // BearerToken을 사용하여 Credential 객체를 생성하고 액세스 토큰을 설정
        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);

        // Google Calendar API 서비스 객체를 생성
        Calendar service = new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        // 시작 시간을 파싱하고 ZonedDateTime 객체 생성 (Asia/Seoul 타임존 사용)
        ZonedDateTime startZonedDateTime = startDateTime.atZone(ZoneOffset.ofHours(9)); // +09:00으로 설정

        // 종료 시간을 시작 시간으로부터 24시간 후로 설정
        ZonedDateTime endZonedDateTime = startZonedDateTime.plusHours(24);

        // RFC 3339 형식으로 변환 (시간대 오프셋 +09:00만 포함)
        DateTimeFormatter rfc3339Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        String startDateTimeRFC3339 = startZonedDateTime.format(rfc3339Formatter);
        String endDateTimeRFC3339 = endZonedDateTime.format(rfc3339Formatter);

        // 이벤트 객체 생성 및 제목 설정
        Event event = new Event().setSummary(summary);

        // 이벤트 시작 시간 설정 (KST 타임존 사용)
        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(startDateTimeRFC3339)) // 시작 날짜 및 시간 설정
                .setTimeZone("Asia/Seoul");  // 한국 표준시(KST) 설정
        event.setStart(start);

        // 이벤트 종료 시간 설정 (KST 타임존 사용)
        EventDateTime end = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(endDateTimeRFC3339)) // 종료 날짜 및 시간 설정
                .setTimeZone("Asia/Seoul"); // 한국 표준시(KST) 설정
        event.setEnd(end);

        // Google Meet 회의 데이터를 설정
        ConferenceData conferenceData = new ConferenceData();
        ConferenceSolutionKey conferenceSolutionKey = new ConferenceSolutionKey();
        conferenceSolutionKey.setType("hangoutsMeet"); // Google Meet 회의 타입 설정
        CreateConferenceRequest createConferenceRequest = new CreateConferenceRequest();
        createConferenceRequest.setRequestId("some-random-string"); // 유니크한 요청 ID 설정
        createConferenceRequest.setConferenceSolutionKey(conferenceSolutionKey); // 회의 솔루션 키 설정
        conferenceData.setCreateRequest(createConferenceRequest); // 회의 데이터에 요청 설정
        event.setConferenceData(conferenceData); // 이벤트에 회의 데이터 설정

        // Google Calendar API를 통해 이벤트를 삽입하고 생성된 이벤트를 반환
        Event createdEvent = service.events().insert("primary", event)
                .setConferenceDataVersion(1) // 회의 데이터 버전 설정
                .execute(); // 이벤트 생성 요청 실행

        // Google Meet URL을 추출
        ConferenceData createdConferenceData = createdEvent.getConferenceData();
        String googleMeetLink = null;
        if (createdConferenceData != null && createdConferenceData.getEntryPoints() != null) {
            for (EntryPoint entryPoint : createdConferenceData.getEntryPoints()) {
                if ("video".equals(entryPoint.getEntryPointType())) {  // Google Meet 링크인지 확인
                    googleMeetLink = entryPoint.getUri();  // Google Meet URL 추출
                    break;
                }
            }
        }

        // Google Meet URL 반환 (없을 경우 기본 메시지 반환)
        return googleMeetLink != null ? googleMeetLink : "No Google Meet link available";
    }
}
