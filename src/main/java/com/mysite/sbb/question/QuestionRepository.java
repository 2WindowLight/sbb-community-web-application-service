package com.mysite.sbb.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    Question findBySubject(String subject);
    Question findBySubjectAndContent(String subject, String content);
    List<Question> findBySubjectLike(String subject);
    Page<Question> findAll(Pageable pageable);
    Page<Question> findAll(Specification<Question> spec, Pageable pageable);
    @Query("select q "
            + "from Question q "
            + "join SiteUser u on q.author=u "
            + "where u.username = :username "
            + "order by q.createDate desc ")
    List<Question> findCurrentQuestion(@Param("username") String username,
                                       Pageable pageable);

    Page<Question> findBySubjectContaining(String subject, Pageable pageable);

    Page<Question> findByContentContaining(String content, Pageable pageable);

    @Query("SELECT q FROM Question q JOIN q.category c WHERE c.name LIKE %:category%")
    Page<Question> findByCategoryNameContaining(@Param("category") String category, Pageable pageable);

    @Query("SELECT q FROM Question q JOIN q.tags t WHERE t.name LIKE %:tag%")
    Page<Question> findByTagsNameContaining(@Param("tag") String tag, Pageable pageable);

    @Query("SELECT q FROM Question q " +
            "LEFT JOIN q.category c " +
            "LEFT JOIN q.tags t " +
            "WHERE q.subject LIKE %:kw% " +
            "OR q.content LIKE %:kw% " +
            "OR c.name LIKE %:kw% " +
            "OR t.name LIKE %:kw%")
    Page<Question> findByKeyword(@Param("kw") String kw, Pageable pageable);


}

