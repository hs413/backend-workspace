package hs.wdp.app.jira.service;

import in.woowa.data.portal.common.dto.JiraTicketInfoDTO;
import in.woowa.data.portal.common.mapper.JiraMapper;
import in.woowa.data.portal.common.model.JiraTemplateModel;
import in.woowa.data.portal.common.util.JiraUtil;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraService {
    private final JiraUtil jiraUtil;
    private final JiraMapper jiraMapper;

    /**
     * 템플릿 기반으로 지라 티켓 생성
     *
     * @param templateId 템플릿 ID
     * @param params     템플릿에 치환할 파라미터 Map (예: { "title": { "schemaNm": "스키마명" }, "desc": {"schemaDesc": "스키마 설명"} })
     * @return 생성된 Jira 티켓 Key (예: "DATA-123")
     */
    public String createTicketWithTemplate(String templateId, Map<String, Map<String, String>> params) {
        JiraTemplateModel template = jiraMapper.selectJiraTemplate(templateId);

        if (template == null) {
            log.error("존재하지 않는 지라 템플릿 ID입니다: {}", templateId);
            throw new IllegalArgumentException("유효하지 않은 지라 템플릿입니다.");
        }

        Map<String, String> titleParams = params != null ? params.getOrDefault("title", Map.of()) : Map.of();
        Map<String, String> descParams = params != null ? params.getOrDefault("desc", Map.of()) : Map.of();

        JiraTicketInfoDTO dto = JiraTicketInfoDTO.builder()
                .title(makeContent(template.getTicketTitle(), template.getTicketTitleParam(), titleParams))
                .desc(makeContent(template.getTicketDesc(), template.getTicketDescParam(), descParams))
                .projectId(template.getProjectId())
                .issueType(template.getIssueType())
                .build();

        return jiraUtil.createTicket(dto);
    }

    public String makeContent(String template, List<String> templateParam, Map<String, String> contentParam) {
        // 템플릿이 존재하지 않을 때
        if (template == null) {
            log.warn("템플릿이 비어있습니다.");
            return null;
        }

        //대체 필요한 파라미터가 존재하지 않을 때
        if ((templateParam == null || templateParam.isEmpty()) && (contentParam == null || contentParam.isEmpty())) {
            return template;
        }

        String content = template;

        if (templateParam.size() != contentParam.size()) {
            log.error("템플릿 매개변수 개수와 매핑 데이터 개수가 일치하지 않습니다. templateParam 크기: {}, contentParam 크기: {}",
                    templateParam.size(), contentParam.size());
            throw new IllegalArgumentException("템플릿 매개변수 개수와 매핑 데이터의 개수가 일치하지 않습니다.");
        }

        try {
            for (String param : templateParam) {
                String placeholder = "${" + param + "}";

                String value = contentParam.getOrDefault(param, "");

                content = content.replace(placeholder, value);
            }
        } catch (Exception e) {
            log.error("템플릿 변환 중 오류 발생: {}", e.getMessage(), e);
        }

        return content;

    }
}
