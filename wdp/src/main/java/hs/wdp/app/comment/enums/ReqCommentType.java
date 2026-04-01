package hs.wdp.app.comment.enums;

public enum ReqCommentType {
    req("req"), // 신청서
    tbl("tbl"), // 테이블
    col("col"), // 컬럼
    t("t"), // 용어
    w("w"); // 단어

    private final String code;

    ReqCommentType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ReqCommentType fromCode(String code) {
        for (ReqCommentType t : values()) {
            if (t.code.equals(code)) return t;
        }
        throw new IllegalArgumentException("Unknown type code: " + code);
    }
}
