package hs.wdp.app.jira.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import in.woowa.data.portal.common.dto.JiraTicketInfoDTO;
import java.util.Base64;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JiraUtil {

	private final CommonUtil commonUtil;
	
	@Value("${jira.api.url}")
	private String apiUrl;

	@Value("${jira.api.system-email}")
	private String systemEmail;

	@Value("${jira.api.token}")
	private String apiToken;

	@Value("${jira.project.id}")
	private String projectId;

	@Value("${jira.project.key}")
	private String projectKey;

	@Value("${jira.project.issue-type}")
	private String issueType;

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private APIClientUtil apiClientUtil;

	/**
	 * 초기화: APIClientUtil 재사용
	 * */
	@PostConstruct
	public void init() {
		this.apiClientUtil = new APIClientUtil(commonUtil);
	}

	/**
	 * 공통 헤더 생성
	 * */
	private HttpHeaders buildHeaders() {
		String auth = systemEmail + ":" + apiToken;
		String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", "application/json");
		headers.set("Authorization", "Basic " + encodedAuth);
		return headers;
	}

	/**
	 * 단순 텍스트를 ADF(Atlassian Document Format)로 변환
	 * */
	private JsonNode buildAdf(String text) {
		ObjectNode doc = objectMapper.createObjectNode();
		doc.put("type", "doc");
		doc.put("version", 1);

		ObjectNode paragraph = objectMapper.createObjectNode();
		paragraph.put("type", "paragraph");

		ObjectNode textNode = objectMapper.createObjectNode();
		textNode.put("type", "text");
		textNode.put("text", text != null ? text : "");

		paragraph.putArray("content").add(textNode);
		doc.putArray("content").add(paragraph);

		return doc;
	}

	/**
	 * 티켓 정보 조회
	 * */
	public JiraTicketInfoDTO getJiraTicketInfo(String key) {
		String restUrl = apiUrl + "/rest/api/3/issue/" + key;

		try {
			String res = apiClientUtil.getForObject(restUrl, buildHeaders(), String.class);

			if (res == null) {
				throw new RuntimeException("jira에서 정보를 가져오는데 실패했습니다.");
			}

			JsonNode fields = objectMapper.readTree(res).path("fields");

			String userId = "-";
			JsonNode assigneeNode = fields.get("assignee");
			if (assigneeNode != null && !assigneeNode.isNull()) {
				String email = assigneeNode.path("emailAddress").asText("");
				String userName = email.split("@")[0];
				String displayName = assigneeNode.path("displayName").asText("")
						.split("/")[0].split("\\(")[0].trim();
				userId = displayName + "(" + userName + ")";
			}

			return JiraTicketInfoDTO.builder()
					.status(fields.path("status").path("id").asText("-"))
					.userId(userId)
					.title(fields.path("summary").asText("-"))
					.jiraStatus(fields.path("status").path("name").asText("-"))
					.build();

		} catch (Exception e) {
			log.error("Jira 티켓 조회 실패 [{}]: {}", key, e.getMessage());
			if (e.getMessage() != null && e.getMessage().contains("404")) {
				return JiraTicketInfoDTO.builder().status("404").userId("-").title("-").jiraStatus("Not Found").build();
			}
			throw new RuntimeException("Jira 정보 조회 실패", e);
		}
	}

	/**
	 * 티켓 생성
	 * */
	public String createTicket(String title, String desc, String reporterAccountId) {
		String restUrl = apiUrl + "/rest/api/3/issue";

		try {
			ObjectNode fields = objectMapper.createObjectNode();
			fields.putObject("project").put("id", projectId);
			fields.put("summary", title);
			fields.putObject("issuetype").put("name", issueType);

			fields.set("description", buildAdf(desc));

			if (reporterAccountId != null && !reporterAccountId.isBlank()) {
				fields.putObject("reporter").put("accountId", reporterAccountId);
			}

			String requestBody = objectMapper.createObjectNode()
					.set("fields", fields)
					.toString();

			String res = apiClientUtil.postForObject(restUrl, buildHeaders(), requestBody, String.class);

			if (res == null) {
				throw new RuntimeException("Jira API 응답이 null입니다.");
			}

			String ticketKey = objectMapper.readTree(res).path("key").asText();
			log.info("Jira 티켓 생성 완료: {}", ticketKey);

			return ticketKey;
		} catch (Exception e) {
			log.error("Jira 티켓 생성 실패 - title: {}, error: {}", title, e.getMessage(), e);
			throw new RuntimeException("jira 티켓 생성에 실패했습니다.", e);
		}
	}

	/**
	 * 티켓에 댓글 추가
	 * */
	public void addCommentToJiraTicket(String key, String comment) {
		String restUrl = apiUrl + "/rest/api/3/issue/" + key + "/comment";

		try {
			ObjectNode body = objectMapper.createObjectNode();
			body.put("body", buildAdf(comment));

			String res = apiClientUtil.postForObject(restUrl, buildHeaders(), body.toString(), String.class);

			if (res == null) {
				throw new RuntimeException("댓글 추가 응답이 null입니다.");
			}

			log.info("Jira 댓글 추가 완료 - 티켓: {}", key);

		} catch (Exception e) {
			log.error("Jira 댓글 추가 실패 - key: {}, error: {}", key, e.getMessage(), e);
			throw new RuntimeException("jira에 정보를 추가하는데 실패했습니다.", e);
		}
	}

	/**
	 * 유저 AccountId 조회
	 * */
	public String findJiraAccountId(String userName) {
		String restUrl = apiUrl + "/rest/api/3/user/search?query=" + userName;

		try {
			String res = apiClientUtil.getForObject(restUrl, buildHeaders(), String.class);

			if (res == null) {
				throw new RuntimeException("유저 검색 응답이 null입니다.");
			}

			JsonNode users = objectMapper.readTree(res);
			for (JsonNode user : users) {
				String emailPrefix = user.get("emailAddress").asText().split("@")[0];
				if (userName.equals(emailPrefix)) {
					return user.get("accountId").asText();
				}
			}

			throw new RuntimeException("jira에서 " + userName + "을 찾지 못했습니다.");

		} catch (Exception e) {
			log.error("Jira 유저 조회 실패 - userName: {}, error: {}", userName, e.getMessage(), e);
			throw new RuntimeException("jira에서 유저를 찾지 못했습니다.", e);
		}
	}

	/**
	 * Key -> URL 변환 유틸
	 * */
	public String getJiraTicketUrl(String key) {
		return apiUrl + "/browse/" + key;
	}
}
