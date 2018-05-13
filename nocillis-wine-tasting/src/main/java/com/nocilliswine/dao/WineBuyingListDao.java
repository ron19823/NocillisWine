package com.nocilliswine.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nocilliswine.model.WineBuyingList;

/**
 * @author Rohan Sharma
 *
 */
@Repository
public interface WineBuyingListDao extends JpaRepository<WineBuyingList, String>{

	int countByPersonId(@Param("personId")String personId);
	
	Page<WineBuyingList> findAll(Pageable pageable);
}
