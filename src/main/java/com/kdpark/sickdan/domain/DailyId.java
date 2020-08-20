package com.kdpark.sickdan.domain;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class DailyId implements Serializable {
    private Long memberId;
    private String date;

    public DailyId() {}

    public DailyId(Long memberId, String date) {
        this.memberId = memberId;
        this.date = date;
    }
}
