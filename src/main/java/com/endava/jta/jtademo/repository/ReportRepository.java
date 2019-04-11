package com.endava.jta.jtademo.repository;

import org.springframework.data.repository.CrudRepository;

import com.endava.jta.jtademo.domain.Report;

public interface ReportRepository extends CrudRepository<Report, Long> {

}
