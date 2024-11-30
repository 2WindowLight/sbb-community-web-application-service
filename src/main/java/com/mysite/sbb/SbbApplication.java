package com.mysite.sbb;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class SbbApplication {

	private final CategoryService categoryService;
	private final JdbcTemplate jdbcTemplate;

	public SbbApplication(CategoryService categoryService, JdbcTemplate jdbcTemplate) {
		this.categoryService = categoryService;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Bean
	CommandLineRunner initCategoriesAndAddColumn() {
		return args -> {
			// 카테고리 초기화
			if (categoryService.getAllCategories().isEmpty()) { // 기존 카테고리가 없을 경우에만 추가
				categoryService.createCategory("자유게시판");
				categoryService.createCategory("질문게시판");
			}

			// view_count 컬럼 추가
			addViewCountColumn();
		};
	}

	private void addViewCountColumn() {
		try {
			String sql = "ALTER TABLE question ADD COLUMN IF NOT EXISTS view_count INT DEFAULT 0";
			jdbcTemplate.execute(sql);
			System.out.println("view_count 컬럼이 성공적으로 추가되었습니다.");
		} catch (Exception e) {
			System.err.println("view_count 컬럼 추가 중 오류 발생: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(SbbApplication.class, args);
	}
}