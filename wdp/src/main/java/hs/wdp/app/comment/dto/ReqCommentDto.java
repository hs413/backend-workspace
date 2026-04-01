package hs.wdp.app.comment.dto;


import in.woowa.data.portal.app.core.reqcomment.enums.ReqCommentType;
import in.woowa.data.portal.common.dto.DpReqDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ReqCommentDto {
    private String commentId;
    private String upCommentId;
    private String type;
    private String refId;
    private String refKey;
    private String cn;
    private String rgstId;
    private String rgstDt;
    private String mngrYn;
    private int depth;
    private String userNm;
    private String deptNm;
    private String companyNm;

    @Getter
    @Setter
    public static class Create {
        @NotBlank
        private String refId;
        private String refKey;

        @NotBlank(message = "내용은 필수 항목입니다.")
        private String cn;
        private String mngrYn;

        @NotNull
        private ReqCommentType type;
        private String upCommentId;
    }

    @Getter
    @Setter
    public static class Update {
        @NotBlank(message = "내용은 필수 항목입니다.")
        private String cn;
        private String mngrYn;
        private String upCommentId;
    }

    @Getter
    @Builder
    public static class Response {
        private List<ReqCommentDto> comments;
        private ReqInfo reqDetail;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ReqInfo {
        private String rgstId;
        private String reqId;
        private String reqCmt;

        public static ReqInfo from(DpReqDto dpReqDto) {
            return new ReqInfo(dpReqDto.getRgstId(), dpReqDto.getReqId(), dpReqDto.getReqCmt() );
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchParam {
        private String type;
        private String refId;
        private String refKey;
        private boolean isAdmin;
    }

    @Getter
    @Builder
    public static class CountResponse {
        private int count;
        private String type;
        private String refKey;
    }
}
