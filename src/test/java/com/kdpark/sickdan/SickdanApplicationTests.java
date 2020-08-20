package com.kdpark.sickdan;

import com.kdpark.sickdan.domain.Daily;
import com.kdpark.sickdan.domain.Meal;
import com.kdpark.sickdan.domain.MealCategory;
import com.kdpark.sickdan.domain.Member;
import com.kdpark.sickdan.repository.DailyRepository;
import com.kdpark.sickdan.repository.MealReposity;
import com.kdpark.sickdan.repository.MemberRepository;
import com.kdpark.sickdan.service.DailyService;
import com.kdpark.sickdan.service.MealService;
import com.kdpark.sickdan.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
class SickdanApplicationTests {

	@Autowired
	private MemberService memberService;
	@Autowired
	private DailyService dailyService;
	@Autowired
	private MealService mealService;

	@Test
	public void 회원가입부터_식단작성까지() throws Exception {

	}
}
