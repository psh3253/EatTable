package com.astar.eattable.common.repository;

import com.astar.eattable.common.model.ExternalEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExternalEventRepository extends JpaRepository<ExternalEvent, UUID> {
}
