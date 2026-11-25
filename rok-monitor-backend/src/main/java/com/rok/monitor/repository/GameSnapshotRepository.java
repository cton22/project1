package com.rok.monitor.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.rok.monitor.entity.GameSnapshot;

public interface GameSnapshotRepository extends JpaRepository<GameSnapshot, Long> {
    List<GameSnapshot> findTop100ByAccountIdOrderByCapturedAtDesc(Long accountId);
    List<GameSnapshot> findTop100ByOrderByCapturedAtDesc();
}
