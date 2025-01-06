package site.fitmon.gathering.dto.request;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.fitmon.gathering.domain.MainType;
import site.fitmon.gathering.domain.SubType;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GatheringSearchCondition {

    private MainType mainType;
    private SubType subType;
    private String mainLocation;
    private String subLocation;
    private LocalDate searchDate;
    private String sortBy;
    private String sortDirection;

    @Builder
    public GatheringSearchCondition(MainType mainType, SubType subType, String mainLocation, String subLocation,
        LocalDate searchDate, String sortBy, String sortDirection) {
        this.mainType = mainType;
        this.subType = subType;
        this.mainLocation = mainLocation;
        this.subLocation = subLocation;
        this.searchDate = searchDate;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }
}
