package com.mysite.sbb.user;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.comment.CommentService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
import java.security.SecureRandom;
import java.util.Random;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	private final QuestionService questionService;
	private final AnswerService answerService;
	private final CommentService commentService;
	private final JavaMailSender mailSender;


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
	public String profile(Model model, Principal principal) {
		String username = principal.getName();
		SiteUser siteUser = this.userService.getUser(username);

		UserUpdateForm userUpdateForm = new UserUpdateForm(); // UserUpdateForm 생성
		model.addAttribute("userUpdateForm", userUpdateForm); // Model에 추가
		model.addAttribute("username", siteUser.getUsername());
		model.addAttribute("userEmail", siteUser.getEmail());
		model.addAttribute("questionList",
				questionService.getCurrentListByUser(username, 5));
		model.addAttribute("answerList",
				answerService.getCurrentListByUser(username, 5));
		model.addAttribute("commentList",
				commentService.getCurrentListByUser(username, 5));
		return "profile"; // profile.html
	}
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/profile_modify")
	public String update(@Valid UserUpdateForm userUpdateForm, BindingResult bindingResult,
						 Model model, Principal principal) {
		SiteUser siteUser = this.userService.getUser(principal.getName());

		if (bindingResult.hasErrors()) {
			// 유효성 검사 실패 시 데이터를 다시 모델에 추가
			model.addAttribute("username", siteUser.getUsername());
			model.addAttribute("userEmail", siteUser.getEmail());
			return "profile_modify"; // 유효성 검사 실패 시 profile_modify.html로 이동
		}

		if (!this.userService.isMatch(userUpdateForm.getOriginPassword(), siteUser.getPassword())) {
			// 기존 비밀번호 불일치
			bindingResult.rejectValue("originPassword", "passwordInCorrect", "기존 비밀번호가 일치하지 않습니다.");
			model.addAttribute("username", siteUser.getUsername());
			model.addAttribute("userEmail", siteUser.getEmail());
			return "profile_modify";
		}

		if (!userUpdateForm.getPassword1().equals(userUpdateForm.getPassword2())) {
			// 새 비밀번호와 확인 비밀번호 불일치
			bindingResult.rejectValue("password2", "passwordInCorrect", "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
			model.addAttribute("username", siteUser.getUsername());
			model.addAttribute("userEmail", siteUser.getEmail());
			return "profile_modify";
		}

		try {
			this.userService.update(siteUser, userUpdateForm.getPassword1());
		} catch (Exception e) {
			bindingResult.reject("updateFailed", "비밀번호 변경 중 문제가 발생했습니다.");
			e.printStackTrace();
			return "profile_modify";
		}

		// 성공적으로 업데이트되면 프로필 페이지로 리다이렉트
		return "redirect:/user/profile";
	}

	@GetMapping("/find-account")
	public String findAccount(Model model) {
		model.addAttribute("sendConfirm", false);
		model.addAttribute("error", false);
		return "find_account";
	}
	public static class PasswordGenerator {
		private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
		private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
		private static final String NUMBER = "0123456789";
		private static final String OTHER_CHAR = "!@#$%&*()_+-=[]?";

		private static final String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR;
		private static final int PASSWORD_LENGTH = 12;

		public static String generateRandomPassword() {
			if (PASSWORD_LENGTH < 1) throw new IllegalArgumentException("Password length must be at least 1");

			StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
			Random random = new SecureRandom();
			for (int i = 0; i < PASSWORD_LENGTH; i++) {
				int rndCharAt = random.nextInt(PASSWORD_ALLOW_BASE.length());
				char rndChar = PASSWORD_ALLOW_BASE.charAt(rndCharAt);
				sb.append(rndChar);
			}

			return sb.toString();
		}
	}
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/profile_modify")
	public String profileModify(Model model, Principal principal) {
		String username = principal.getName();
		SiteUser siteUser = this.userService.getUser(username);

		model.addAttribute("username", siteUser.getUsername());
		model.addAttribute("userEmail", siteUser.getEmail());
		model.addAttribute("userUpdateForm", new UserUpdateForm());
		return "profile_modify";
	}
	@PostMapping("/find-account")
	public String findAccount(Model model, @RequestParam(value="email") String email) {
		try {
			SiteUser siteUser = this.userService.getUserByEmail(email);
			model.addAttribute("sendConfirm", true);
			model.addAttribute("userEmail", email);
			model.addAttribute("error", false);
			SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
			simpleMailMessage.setTo(email);
			simpleMailMessage.setSubject("계정 정보입니다.");
			StringBuffer sb = new StringBuffer();

			String newPassword = PasswordGenerator.generateRandomPassword();
			sb.append(siteUser.getUsername()).append("계정의 비밀번호를 새롭게 초기화 했습니다..\n").append("새 비밀번호는 ")
					.append(newPassword).append("입니다.\n")
					.append("로그인 후 내 정보에서 새로 비밀번호를 지정해주세요.");
			simpleMailMessage.setText(sb.toString());
			this.userService.update(siteUser, newPassword);
			new Thread(new Runnable() {
				@Override
				public void run() {
					mailSender.send(simpleMailMessage);
				}
			}).start();
		} catch(DataNotFoundException e) {
			model.addAttribute("sendConfirm", false);
			model.addAttribute("error", true);
		}
		return "find_account";
	}







}