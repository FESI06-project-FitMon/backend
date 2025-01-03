package site.fitmon.fitmon.gatherings.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;
import site.fitmon.fitmon.common.domain.BaseEntity;
import site.fitmon.fitmon.common.exception.ApiException;
import site.fitmon.fitmon.common.exception.ErrorCode;
import site.fitmon.fitmon.member.domain.Member;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@FilterDef(name = "deletedFilter", parameters = @ParamDef(name = "deleted", type = Boolean.class))
@Filter(name = "deletedFilter", condition = "deleted = :deleted")
@SQLDelete(sql = "UPDATE gathering SET deleted = true WHERE id = ?")
public class Gatherings extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member captain;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MainType mainType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SubType subType;

    private String imageUrl;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private String mainLocation;

    @Column(nullable = false)
    private String subLocation;

    @Column(nullable = false)
    private Integer totalCount;

    @Column(nullable = false)
    private Integer minCount;

    @Column(nullable = false)
    private Integer participantCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GatheringStatus status = GatheringStatus.시작전;

    private String tags;

    @Builder
    public Gatherings(String title, Member captain, MainType mainType, SubType subType, String imageUrl,
        LocalDateTime startDate,
        LocalDateTime endDate, String mainLocation, String subLocation, Integer totalCount, Integer minCount,
        Integer participantCount, List<String> tags) {
        this.title = title;
        this.captain = captain;
        this.mainType = mainType;
        this.subType = subType;
        this.imageUrl = imageUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.mainLocation = mainLocation;
        this.subLocation = subLocation;
        this.totalCount = totalCount;
        this.minCount = minCount;
        this.participantCount = participantCount;
        this.status = GatheringStatus.시작전;
        setTags(tags);
    }

    public void setTags(List<String> tagList) {
        if (tagList == null || tagList.isEmpty()) {
            this.tags = null;
            return;
        }

        if (tagList.size() > 3) {
            throw new ApiException(ErrorCode.EXCEEDS_MAX_TAGS);
        }

        this.tags = String.join(",", tagList);
    }

    public List<String> getTagList() {
        return tags != null ? Arrays.asList(tags.split(",")) : new ArrayList<>();
    }

    public void start() {
        validateStatusChange(GatheringStatus.진행중);
        this.status = GatheringStatus.진행중;
    }

    public void complete() {
        validateStatusChange(GatheringStatus.종료됨);
        this.status = GatheringStatus.종료됨;
    }

    public void cancel() {
        validateStatusChange(GatheringStatus.취소됨);
        this.status = GatheringStatus.취소됨;
    }

    private void validateStatusChange(GatheringStatus newStatus) {
        if (this.status == GatheringStatus.취소됨) {
            throw new IllegalStateException("취소된 모임은 상태를 변경할 수 없습니다.");
        }
        if (this.status == GatheringStatus.종료됨) {
            throw new IllegalStateException("종료된 모임은 상태를 변경할 수 없습니다.");
        }
    }
}
