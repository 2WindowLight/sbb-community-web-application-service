package com.mysite.sbb;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SbbApplication {

	private final CategoryService categoryService;

	public SbbApplication(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@Bean
	CommandLineRunner initCategoriesAndAddColumn() {
		return args -> {
			// 카테고리 초기화
			if (categoryService.getAllCategories().isEmpty()) { // 기존 카테고리가 없을 경우에만 추가
				categoryService.createCategory("자유게시판");
				categoryService.createCategory("질문게시판");
			}

		};
	}



	public static void main(String[] args) {
		SpringApplication.run(SbbApplication.class, args);
	}
}