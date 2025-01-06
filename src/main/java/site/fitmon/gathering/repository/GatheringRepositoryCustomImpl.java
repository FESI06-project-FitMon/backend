package site.fitmon.gathering.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import site.fitmon.gathering.domain.Gathering;
import site.fitmon.gathering.domain.GatheringStatus;
import site.fitmon.gathering.domain.MainType;
import site.fitmon.gathering.domain.QGathering;
import site.fitmon.gathering.domain.QGatheringParticipant;
import site.fitmon.gathering.domain.SubType;
import site.fitmon.gathering.dto.request.GatheringSearchCondition;
import site.fitmon.gathering.dto.response.GatheringDetailResponse;
import site.fitmon.gathering.dto.response.GatheringResponse;
import site.fitmon.gathering.dto.response.ParticipantsResponse;
import site.fitmon.member.domain.QMember;
import site.fitmon.review.domain.QReview;

@Repository
@RequiredArgsConstructor
public class GatheringRepositoryCustomImpl implements GatheringRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<GatheringResponse> searchGatherings(GatheringSearchCondition condition, Pageable pageable) {
        QGathering gathering = QGathering.gathering;

        NumberTemplate<Long> participantCount = Expressions.numberTemplate(Long.class,
            "(select count(*) from GatheringParticipant gp where gp.gathering.id = {0})",
            gathering.id);

        JPAQuery<GatheringResponse> query = queryFactory
            .select(Projections.constructor(GatheringResponse.class,
                gathering.id,
                gathering.title,
                gathering.description,
                gathering.mainType,
                gathering.subType,
                gathering.imageUrl,
                gathering.startDate,
                gathering.endDate,
                gathering.mainLocation,
                gathering.subLocation,
                gathering.minCount,
                gathering.totalCount,
                participantCount,
                gathering.status,
                gathering.tags))
            .from(gathering)
            .where(
                mainTypeEq(condition.getMainType()),
                subTypeEq(condition.getSubType()),
                mainLocationEq(condition.getMainLocation()),
                subLocationEq(condition.getSubLocation()),
                dateInclude(condition.getSearchDate()),
                gathering.deleted.eq(false)
            );

        OrderSpecifier<?>[] orderSpecifiers = createOrderSpecifiers(condition.getSortBy(),
            condition.getSortDirection(), gathering, participantCount);
        query.orderBy(orderSpecifiers);

        List<GatheringResponse> content = query
            .offset(pageable.getPageNumber() * pageable.getPageSize())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    public GatheringDetailResponse findGatheringDetail(Gathering gathering) {
        List<ParticipantsResponse> recentParticipants = queryFactory
            .select(Projections.constructor(ParticipantsResponse.class,
                QMember.member.id,
                QMember.member.nickName,
                QMember.member.profileImageUrl,
                QGatheringParticipant.gatheringParticipant.captainStatus))
            .from(QGatheringParticipant.gatheringParticipant)
            .join(QGatheringParticipant.gatheringParticipant.member, QMember.member)
            .where(QGatheringParticipant.gatheringParticipant.gathering.id.eq(gathering.getId()))
            .orderBy(QGatheringParticipant.gatheringParticipant.createdAt.desc())
            .limit(5)
            .fetch();

        // 참가자 총 수 계산
        Long participantCount = queryFactory
            .select(QGatheringParticipant.gatheringParticipant.count())
            .from(QGatheringParticipant.gatheringParticipant)
            .where(QGatheringParticipant.gatheringParticipant.gathering.id.eq(gathering.getId()))
            .fetchOne();

        Double avgRating = queryFactory
            .select(QReview.review.rating.avg())
            .from(QReview.review)
            .where(QReview.review.gathering.id.eq(gathering.getId()))
            .fetchOne();

        Long reviewCount = queryFactory
            .select(QReview.review.count())
            .from(QReview.review)
            .where(QReview.review.gathering.id.eq(gathering.getId()))
            .fetchOne();

        return GatheringDetailResponse.builder()
            .gatheringId(gathering.getId())
            .title(gathering.getTitle())
            .description(gathering.getDescription())
            .mainType(gathering.getMainType())
            .subType(gathering.getSubType())
            .imageUrl(gathering.getImageUrl())
            .startDate(gathering.getStartDate())
            .endDate(gathering.getEndDate())
            .mainLocation(gathering.getMainLocation())
            .subLocation(gathering.getSubLocation())
            .minCount(gathering.getMinCount())
            .totalCount(gathering.getTotalCount())
            .participantCount(participantCount)
            .status(gathering.getStatus())
            .tags(((gathering.getTags() != null ?
                Arrays.asList(gathering.getTags().split(",")) :
                Collections.emptyList()))
            ).participants(recentParticipants)
            .rating(avgRating != null ? Math.round(avgRating) : 0)
            .guestBookCount(reviewCount)
            .build();
    }

    private OrderSpecifier<?>[] createOrderSpecifiers(String sortBy, String direction,
        QGathering gathering, NumberExpression<Long> participantCount) {
        Order order = direction.equalsIgnoreCase("DESC") ? Order.DESC : Order.ASC;

        NumberExpression<Integer> statusPriority = new CaseBuilder()
            .when(gathering.status.eq(GatheringStatus.시작전)).then(1)
            .when(gathering.status.eq(GatheringStatus.진행중)).then(1)
            .when(gathering.status.eq(GatheringStatus.종료됨)).then(2)
            .when(gathering.status.eq(GatheringStatus.취소됨)).then(3)
            .otherwise(4);

        OrderSpecifier<?> sortBySpecifier;
        switch (sortBy) {
            case "deadline":
                sortBySpecifier = new OrderSpecifier<>(order, gathering.endDate);
                break;
            case "participants":
                sortBySpecifier = new OrderSpecifier<>(order, participantCount);
                break;
            default:
                sortBySpecifier = new OrderSpecifier<>(Order.DESC, gathering.id);
        }

        return new OrderSpecifier<?>[]{
            new OrderSpecifier<>(Order.ASC, statusPriority),
            sortBySpecifier
        };
    }

    private BooleanExpression mainTypeEq(MainType mainType) {
        return mainType != null ? QGathering.gathering.mainType.eq(mainType) : null;
    }

    private BooleanExpression subTypeEq(SubType subType) {
        return subType != null ? QGathering.gathering.subType.eq(subType) : null;
    }

    private BooleanExpression mainLocationEq(String mainLocation) {
        return StringUtils.hasText(mainLocation) ?
            QGathering.gathering.mainLocation.eq(mainLocation) : null;
    }

    private BooleanExpression subLocationEq(String subLocation) {
        return StringUtils.hasText(subLocation) ?
            QGathering.gathering.subLocation.eq(subLocation) : null;
    }

    private BooleanExpression dateInclude(LocalDate searchDate) {
        if (searchDate == null) {
            return null;
        }

        LocalDateTime startOfDay = searchDate.atStartOfDay();
        LocalDateTime endOfDay = searchDate.atTime(LocalTime.MAX);

        return QGathering.gathering.startDate.loe(endOfDay)
            .and(QGathering.gathering.endDate.goe(startOfDay));
    }
}
