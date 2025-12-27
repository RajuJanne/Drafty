package com.drafty.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drafty.entity.DraftSeries;

@Repository
public interface DraftSeriesRepository extends JpaRepository<DraftSeries, String> {
}