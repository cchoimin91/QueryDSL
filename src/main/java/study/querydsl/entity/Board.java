package study.querydsl.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;



@Setter // 테스트 편의상
@Entity
@ToString(of = {"id","title","content"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {

    @Id
    @GeneratedValue
    private long id ;

    private String title;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @CreatedDate
    private LocalDateTime regDate;

    @LastModifiedDate
    private LocalDateTime updateDate;





    public Board(String title) {
        this.title=title;
    }

    public Board(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.regDate = regDate;
        this.updateDate = updateDate;
    }

    public Board(String content, User user) {
        this.content = content;
        if(user != null){
            this.user = user;
        }
    }

    public void changeUser(User user){
        this.user=user;
        user.getBoards().add(this);
    }

    @PrePersist
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        this.regDate= now;
        this.updateDate=now;
    }

    @PreUpdate
    public void preUpdate(){
        this.updateDate=LocalDateTime.now();
    }
}
