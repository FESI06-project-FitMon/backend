package site.fitmon.fitmon.gatherings.domain;

import lombok.Getter;

@Getter
public enum SubType {
    런닝("RUNNING", MainType.유산소형),
    자전거("CYCLING", MainType.유산소형),
    유산소_기타("AEROBIC_ETC", MainType.유산소형),

    헬스("WEIGHT_TRAINING", MainType.헬스형),
    헬스_기타("WEIGHT_ETC", MainType.헬스형),

    축구("SOCCER", MainType.경기형),
    배드민턴("BADMINTON", MainType.경기형),
    풋살("FUTSAL", MainType.경기형),
    경기_기타("SPORTS_ETC", MainType.경기형);

    private final String code;
    private final MainType mainType;

    SubType(String code, MainType mainType) {
        this.code = code;
        this.mainType = mainType;
    }
}
