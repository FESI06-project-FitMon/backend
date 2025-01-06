package site.fitmon.gathering.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.fitmon.gathering.repository.GatheringRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GatheringStatusService {

    private final GatheringRepository gatheringRepository;

    @Transactional
    public void updateGatheringStatus() {
        LocalDateTime now = LocalDateTime.now();
        gatheringRepository.updateStatusToInProgress(now);
        gatheringRepository.updateStatusToCompleted(now);
    }
}
