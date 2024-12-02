package com.mysite.sbb.user;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.comment.CommentService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	private final QuestionService questionService;
	private final AnswerService answerService;
	private final CommentService commentService;


	// 회원가입 페이지 표시
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("userCreateForm", new UserCreateForm()); // UserCreateForm 객체를 명시적으로 추가
		return "signup_form";
	}

	// 회원가입 처리
	@PostMapping("/signup")
	public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			// 유효성 검사 실패 시 다시 회원가입 폼으로 이동
			return "signup_form";
		}

		// 비밀번호 확인
		if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
			bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
			return "signup_form";
		}

		// 회원 생성 로직
		try {
			userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1());
		} catch (DataIntegrityViolationException e) {
			// 데이터 무결성 오류 (중복 사용자)
			e.printStackTrace();
			bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
			return "signup_form";
		} catch (Exception e) {
			// 기타 오류
			e.printStackTrace();
			bindingResult.reject("signupFailed", e.getMessage());
			return "signup_form";
		}

		// 회원가입 성공 시 메인 페이지로 리다이렉트
		return "redirect:/";
	}

	// 로그인 페이지 표시
	@GetMapping("/login")
	public String login() {
		return "login_form";
	}
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/profile")
	public String profile(UserUpdateForm userUpdateForm, Model model, Principal principal) {
		String username = principal.getName();

		model.addAttribute("userUpdateForm", userUpdateForm);
		model.addAttribute("username", username);
		model.addAttribute("questionList",
				questionService.getCurrentListByUser(username, 5));
		model.addAttribute("answerList",
				answerService.getCurrentListByUser(username, 5));
		model.addAttribute("commentList",
				commentService.getCurrentListByUser(username, 5));
		return "profile";
	}


}