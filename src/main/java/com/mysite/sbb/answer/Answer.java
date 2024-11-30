package com.mysite.sbb.answer;

import com.mysite.sbb.comment.Comment;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

// 테이블에 직접 연관된 클래스는 엔티티 클래스
// DTO 사용 - 엔티티 클래스는 민감한 db와 연관돼 있기 때문에 컨트롤러나 중요 코드에서 직접 사용 x
@Getter
@Setter
@Entity
public class Answer {
    @Id // id를 pk로 쓰겠다
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne  // many 가 나
    private Question question;

    @ManyToOne
    private SiteUser siteUser;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    @ManyToMany
    Set<SiteUser> voter;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.REMOVE)
    private List<Comment> commentList;

}
