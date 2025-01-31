package site.fitmon.challenge.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import site.fitmon.challenge.domain.QChallenge;
import site.fitmon.challenge.domain.QChallengeEvidence;
import site.fitmon.challenge.domain.QChallengeParticipant;
import site.fitmon.challenge.dto.request.ChallengeSearchCondition;
import site.fitmon.challenge.dto.request.ChallengeSearchCondition.ChallengeStatus;
import site.fitmon.challenge.dto.response.GatheringChallengesResponse;
import site.fitmon.gathering.domain.QGatheringParticipant;
import site.fitmon.member.dto.response.MemberChallengeResponse;
import site.fitmon.member.dto.response.OwnedGatheringChallengeResponse;

@Repository
@RequiredArgsConstructor
public class ChallengeRepositoryCustomImpl implements ChallengeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<GatheringChallengesResponse> getGatheringChallenges(
        Long gatheringId,
        Long memberId,
        ChallengeSearchCondition condition,
        Pageable pageable
    ) {
        QChallenge challenge = QChallenge.challenge;
        QChallengeParticipant participant = QChallengeParticipant.challengeParticipant;
        QChallengeEvidence evidence = QChallengeEvidence.challengeEvidence;

        List<GatheringChallengesResponse> content = queryFactory
            .select(Projections.constructor(GatheringChallengesResponse.class,
                challenge.gathering.id,
                challenge.id,
                challenge.title,
                challenge.description,
                challenge.imageUrl,
                ExpressionUtils.as(
                    JPAExpressions
                        .select(participant.count())
                        .from(participant)
                        .where(participant.challenge.id.eq(challenge.id)),
                    "participantCount"
                ),
                ExpressionUtils.as(
                    JPAExpressions
                        .select(evidence.member.countDistinct())
                        .from(evidence)
                        .where(evidence.challenge.id.eq(challenge.id)),
                    "successParticipantCount"
                ),
                ExpressionUtils.as(
                    memberId != null ?
                        JPAExpressions
                            .select(participant.count().gt(0))
                            .from(participant)
                            .where(
                                participant.challenge.id.eq(challenge.id),
                                participant.member.id.eq(memberId)
                            )
                        : Expressions.constant(false),
                    "participantStatus"
                ),
                ExpressionUtils.as(
                    memberId != null ?
                        JPAExpressions
                            .select(evidence.count().gt(0))
                            .from(evidence)
                            .where(
                                evidence.challenge.id.eq(challenge.id),
                                evidence.member.id.eq(memberId)
                            )
                        : Expressions.constant(false),
                    "verificationStatus"
                ),
                challenge.startDate,
                challenge.endDate
            ))
            .from(challenge)
            .where(
                challenge.gathering.id.eq(gatheringId),
                challenge.deleted.eq(false),
                challengeStatusEq(condition.getStatus())
            )
            .orderBy(challenge.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Page<MemberChallengeResponse> getMemberChallenges(Long memberId, Pageable pageable) {
        QChallenge challenge = QChallenge.challenge;
        QChallengeParticipant participant = QChallengeParticipant.challengeParticipant;
        QChallengeEvidence evidence = QChallengeEvidence.challengeEvidence;

        List<MemberChallengeResponse> content = queryFactory
            .select(Projections.constructor(MemberChallengeResponse.class,
                challenge.gathering.id,
                challenge.id,
                challenge.title,
                challenge.description,
                challenge.imageUrl,
                JPAExpressions
                    .select(participant.count())
                    .from(participant)
                    .where(participant.challenge.id.eq(challenge.id)),
                JPAExpressions
                    .select(evidence.member.countDistinct())
                    .from(evidence)
                    .where(evidence.challenge.id.eq(challenge.id)),
                memberId != null ?
                    JPAExpressions
                        .select(participant.count().gt(0))
                        .from(participant)
                        .where(
                            participant.challenge.id.eq(challenge.id),
                            participant.member.id.eq(memberId)
                        )
                    : Expressions.constant(false),
                memberId != null ?
                    JPAExpressions
                        .select(evidence.count().gt(0))
                        .from(evidence)
                        .where(
                            evidence.challenge.id.eq(challenge.id),
                            evidence.member.id.eq(memberId)
                        )
                    : Expressions.constant(false),
                challenge.startDate,
                challenge.endDate
            ))
            .from(challenge)
            .where(
                challenge.gathering.id.in(
                    JPAExpressions
                        .select(participant.challenge.gathering.id)
                        .from(participant)
                        .where(participant.member.id.eq(memberId))
                ),
                challenge.deleted.eq(false)
            )
            .orderBy(challenge.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .select(challenge.count())
            .from(challenge)
            .where(
                challenge.gathering.id.in(
                    JPAExpressions
                        .select(participant.challenge.gathering.id)
                        .from(participant)
                        .where(participant.member.id.eq(memberId))
                ),
                challenge.deleted.eq(false)
            )
            .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<OwnedGatheringChallengeResponse> getOwnedGatheringChallenges(Long memberId, Pageable pageable) {
        QChallenge challenge = QChallenge.challenge;
        QGatheringParticipant gatheringParticipant = QGatheringParticipant.gatheringParticipant;
        QChallengeParticipant challengeParticipant = QChallengeParticipant.challengeParticipant;
        QChallengeEvidence evidence = QChallengeEvidence.challengeEvidence;

        List<OwnedGatheringChallengeResponse> content = queryFactory
            .select(Projections.constructor(OwnedGatheringChallengeResponse.class,
                challenge.gathering.id,
                challenge.id,
                challenge.title,
                challenge.description,
                challenge.imageUrl,
                JPAExpressions
                    .select(challengeParticipant.count())
                    .from(challengeParticipant)
                    .where(challengeParticipant.challenge.id.eq(challenge.id)),
                JPAExpressions
                    .select(evidence.member.countDistinct())
                    .from(evidence)
                    .where(evidence.challenge.id.eq(challenge.id)),
                JPAExpressions
                    .select(challengeParticipant.count().gt(0))
                    .from(challengeParticipant)
                    .where(
                        challengeParticipant.challenge.id.eq(challenge.id),
                        challengeParticipant.member.id.eq(memberId)
                    ),
                JPAExpressions
                    .select(evidence.count().gt(0))
                    .from(evidence)
                    .where(
                        evidence.challenge.id.eq(challenge.id),
                        evidence.member.id.eq(memberId)
                    ),
                challenge.startDate,
                challenge.endDate
            ))
            .from(challenge)
            .join(gatheringParticipant).on(
                challenge.gathering.id.eq(gatheringParticipant.gathering.id)
                    .and(gatheringParticipant.member.id.eq(memberId))
                    .and(gatheringParticipant.captainStatus.eq(true))
            )
            .where(
                challenge.deleted.eq(false)
            )
            .orderBy(challenge.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .select(challenge.count())
            .from(challenge)
            .join(gatheringParticipant).on(
                challenge.gathering.id.eq(gatheringParticipant.gathering.id)
                    .and(gatheringParticipant.member.id.eq(memberId))
                    .and(gatheringParticipant.captainStatus.eq(true))
            )
            .where(
                challenge.deleted.eq(false)
            )
            .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression challengeStatusEq(ChallengeStatus status) {
        if (status == null) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        QChallenge challenge = QChallenge.challenge;

        return status == ChallengeStatus.IN_PROGRESS ?
            challenge.endDate.after(now) :
            challenge.endDate.before(now);
    }
}
