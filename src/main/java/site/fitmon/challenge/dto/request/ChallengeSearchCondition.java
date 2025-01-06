package site.fitmon.challenge.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChallengeSearchCondition {

    private ChallengeStatus status;

    public enum ChallengeStatus {
        IN_PROGRESS, CLOSED
    }
}

