package com.ale.filso.models.Dictionary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DictionaryRepo extends JpaRepository<Dictionary, Integer> {

    @Query(value = "SELECT * FROM dictionary WHERE dictionary_group = ?1 AND is_active = 1", nativeQuery = true)
    List<Dictionary> findByGroup(Integer groupId);

    @Query(value = "SELECT * FROM dictionary WHERE is_active = 1", nativeQuery = true)
    List<Dictionary> findAllActive();

}
