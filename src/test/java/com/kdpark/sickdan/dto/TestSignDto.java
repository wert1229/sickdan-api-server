package com.kdpark.sickdan.dto;

import lombok.Data;

public class TestSignDto {
    @Data
    public static class WrongFormatNaverUser {
        private Long userId;
        private Integer email;
        private String nickname;
        private String name;
    }
}
