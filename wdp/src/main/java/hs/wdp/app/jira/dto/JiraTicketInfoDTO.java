package hs.wdp.app.jira.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JiraTicketInfoDTO {

    /**
     * 내부 상태 코드
     * 정상: "IN_PROGRESS" | "DONE" | "PREPARING"
     * 티켓 없음: "404"
     * 오류: "ERROR"
     */
    private final String status;

    /**
     * 담당자 ID (예: 홍길동(gildong))
     * 담당자 없을 경우: "-"
     */
    private final String userId;

    /** 티켓 제목 */
    private final String title;

    /** Jira 상태 이름 (예: "In Progress", "Done") */
    private final String jiraStatus;

    /**
     * 정적 팩토리 - 404
     * */
    public static JiraTicketInfoDTO notFound(String errorMessage) {
        return JiraTicketInfoDTO.builder()
                .status("404")
                .userId("-")
                .title("-")
                .jiraStatus(errorMessage)
                .build();
    }

    /**
     * 정적 팩토리 - 오류
     * */
    public static JiraTicketInfoDTO error(String errorMessage) {
        return JiraTicketInfoDTO.builder()
                .status("ERROR")
                .userId("-")
                .title("-")
                .jiraStatus(errorMessage)
                .build();
    }
}