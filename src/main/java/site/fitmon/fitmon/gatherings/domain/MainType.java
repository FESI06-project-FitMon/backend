package site.fitmon.fitmon.gatherings.domain;

import lombok.Getter;

@Getter
public enum MainType {
    유산소형("AEROBIC"),
    헬스형("WEIGHT"),
    경기형("SPORTS");

    private final String code;

    MainType(String code) {
        this.code = code;
    }
}
