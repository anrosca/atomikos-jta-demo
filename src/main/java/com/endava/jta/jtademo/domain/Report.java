package com.endava.jta.jtademo.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "REPORT")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
@Builder
public class Report {

    @Id
    @Column(name = "REPORT_ID")
    private Long id;

    @Column(name = "REPORT_NAME", unique = true)
    private String reportName;

    @Column(name = "CODE")
    private String code;

    @Column(name = "PARTICIPANT_ID")
    private String participantId;

    @Column(name = "PARTICIPANT_NAME")
    private String participantName;

    @Column(name = "CHANNEL")
    private String channel;

    @Column(name = "CREATION_TMSTMP", insertable = false, updatable = false)
    @JsonIgnore
    private LocalDateTime creationTmstmp;

    @Column(name = "REPORT_DATE")
    private LocalDate reportDate;

    @Column(name = "DAILY_MONTHLY")
    private String dailyMonthly;

    @Column(name = "DESTINATION")
    private String destination;

    @Column(name = "TARGET")
    private String target;

    @Column(name = "SCHEDULE")
    private String schedule;
}
