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

@Repository
@RequiredArgsConstructor
public class ChallengeRepositoryCustomImpl implements ChallengeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<GatheringChallengesResponse> getGatheringChallenges(
        Long gatheringId,
        String email,
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
                    email != null ?
                        JPAExpressions
                            .select(participant.count().gt(0))
                            .from(participant)
                            .where(
                                participant.challenge.id.eq(challenge.id),
                                participant.member.email.eq(email)
                            )
                        : Expressions.constant(false),
                    "participantStatus"
                ),
                ExpressionUtils.as(
                    email != null ?
                        JPAExpressions
                            .select(evidence.count().gt(0))
                            .from(evidence)
                            .where(
                                evidence.challenge.id.eq(challenge.id),
                                evidence.member.email.eq(email)
                            )
                        : Expressions.constant(false),
                    "verificationStatus"
                )
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

    private BooleanExpression challengeStatusEq(ChallengeStatus status) {
        if (status == null) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        QChallenge challenge = QChallenge.challenge;

        return status == ChallengeStatus.IN_PROGRESS ?
            challenge.endDate.before(now) :
            challenge.endDate.after(now);
    }
}
