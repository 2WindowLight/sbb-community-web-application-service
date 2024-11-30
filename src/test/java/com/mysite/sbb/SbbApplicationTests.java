package com.mysite.sbb;

import com.mysite.sbb.answer.AnswerRepository;
import com.mysite.sbb.question.QuestionRepository;
import com.mysite.sbb.question.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SbbApplicationTests {
	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@Autowired
	private QuestionService questionService;

	@Test
	void testJpa() {
		for (int i = 0; i < 150; i++) {
			String subject = String.format("테스트 데이터입니다:[%03d]", i);
			String content = "내용 없음";
			this.questionService.create(subject, content);
		}


		/*Optional<Question> question = questionRepository.findById(1);
		assertTrue(question.isPresent());
		Question question1 = question.get();

		List<Answer> answerList = question1.getAnswerList();

		assertEquals(1, answerList.size());
		assertEquals("네 이창희의 학번은 20194080이 맞습니다.", answerList.get(0).getContent());*/


		// 답변 쿼리 생성
		/*Optional<Question> oq = this.questionRepository.findById(1);
		assertTrue(oq.isPresent());
		Question q = oq.get();

		Answer a = new Answer();
		a.setContent("네 이창희의 학번은 20194080이 맞습니다.");
		a.setQuestion(q);
		a.setCreateDate(LocalDateTime.now());
		this.answerRepository.save(a);*/

		// 쿼리 삭제
		/*assertEquals(2, this.questionRepository.count());
		Optional<Question> oq2 = this.questionRepository.findById(2);
		assertTrue(oq2.isPresent());
		Question q2 = oq2.get();
		this.questionRepository.delete(q2);
		assertEquals(1, this.questionRepository.count());*/

		// 쿼리 수정
		/*Optional<Question> oq = this.questionRepository.findById(1);
		assertTrue(oq.isPresent());
		Question q = oq.get();
		q.setSubject("수정된 제목 - 이창희의 학번은 무엇인가요?");
		q.setContent("20194080 가 맞나요?");
		this.questionRepository.save(q);*/

		/*Question q = this.questionRepository.findBySubjectAndContent("sbb가 무엇인가요?","sbb에 대해서 알고 싶습니다.");
		List<Question> q2 = this.questionRepository.findBySubjectLike("sbb%");
		System.out.println(q2.get(0).getContent());
		System.out.println(q.getId());
		System.out.println(q.getCreateDate());*/

		/*List<Question> all = questionRepository.findAll();
		assertEquals(4, all.size());
		System.out.println(all.get(0).getSubject());
		System.out.println(all.get(0).getContent());

		Question q = all.get(0);
		assertEquals("sbb가 무엇인가요?", q.getSubject());

		Optional<Question> oq = questionRepository.findById(1);
		if(oq.isPresent()) {
			Question q1 = oq.get();
			System.out.println(q1.getSubject());
			System.out.println(q1.getContent());
			System.out.println(q1.getCreateDate());
			assertEquals("sbb가 무엇인가요?",q1.getSubject() );
		}*/
		/*Question q1 = new Question();
		q1.setSubject("당신의 이름은 이창희 인가요?");
		q1.setContent("이창희의 학번은 20194080 맞나요?");
		q1.setCreateDate(LocalDateTime.now());
		this.questionRepository.save(q1);

		Question q2 = new Question();
		q2.setSubject("스프링 모델 질문입니다.");
		q2.setContent("id는 자동으로 생성되나요");
		q2.setCreateDate(LocalDateTime.now());
		this.questionRepository.save(q2);*/

	}

}
