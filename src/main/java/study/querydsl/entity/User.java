package study.querydsl.entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
@ToString(of={"id","name"})
public class User {

    @Id
    @GeneratedValue
    @Column(name="user_id")
    private long id;

    private String name;

    @OneToMany(mappedBy = "user")
    private List<Board> boards = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime regDate;

    @LastModifiedDate
    private LocalDateTime updateDate;




    public User(String name) {
        this.name=name;
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
