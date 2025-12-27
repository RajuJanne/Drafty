package com.drafty.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drafty.entity.Champion;

@Repository
public interface ChampionRepository extends JpaRepository<Champion, String> {
}