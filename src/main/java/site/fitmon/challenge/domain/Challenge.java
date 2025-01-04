package site.fitmon.challenge.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;
import site.fitmon.common.domain.BaseEntity;
import site.fitmon.gathering.domain.Gathering;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@FilterDef(name = "deletedChallengeFilter", parameters = @ParamDef(name = "deleted", type = Boolean.class))
@Filter(name = "deletedChallengeFilter", condition = "deleted = :deleted")
@SQLDelete(sql = "UPDATE challenge SET deleted = true WHERE id = ?")
public class Challenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id", nullable = false)
    private Gathering gathering;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Builder
    public Challenge(Gathering gathering, String title, String description, String imageUrl, LocalDateTime startDate,
        LocalDateTime endDate) {
        this.gathering = gathering;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
