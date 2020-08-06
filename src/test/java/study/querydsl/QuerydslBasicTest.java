package study.querydsl;


import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;

import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before(){
        System.out.println("########################################");
        queryFactory = new JPAQueryFactory(em); // 멀티스레드 환경에서도 동시성 이슈없이 설계되어짐
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamA);
        Member member4 = new Member("member4", 40, teamA);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startQuerydsl(){
        //같은 테이블 join시만
        //QMember qm2 = new QMember("m2");

        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search(){
        Member searchMember = queryFactory
                .select(QMember.member)
                .from(QMember.member)
                .where(QMember.member.username.eq("member1")
                        .and(QMember.member.age.eq(10))
                )
                .fetchOne();
        assertThat(searchMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void searchNe(){
        Member neMember = queryFactory
                .select(QMember.member)
                .from(QMember.member)
                .where(QMember.member.username.ne("member1"))
                .fetchFirst();

        assertThat(neMember.getUsername()).isNotEqualTo("member1");
    }

    @Test
    public void searchNot(){
        Member notMember = queryFactory
                .select(QMember.member)
                .from(QMember.member)
                .where(QMember.member.username.eq("member1").not())
                .fetchFirst();

        assertThat(notMember.getUsername()).isNotEqualTo("member1");
    }

    @Test
    public void searchIsNotNull(){
        Member notMember = queryFactory
                .select(QMember.member)
                .from(QMember.member)
                .where(QMember.member.username.isNotNull())
                .fetchFirst();

        assertThat(notMember.getUsername()).isNotEqualTo("member1");
    }

    @Test
    public void searchIn(){
        long inMember = queryFactory
                .select(QMember.member)
                .from(QMember.member)
                .where(QMember.member.username.in("member1", "member2"))
                .fetchCount();

        assertThat(inMember).isEqualTo(2);
    }

    /*
         member.age.goe(30) // age >= 30
         member.age.gt(30) // age > 30
         member.age.loe(30) // age <= 30
         member.age.lt(30) // age < 30
         member.username.like("member%") //like 검색
         member.username.contains("member") // like ‘%member%’ 검색
         member.username.startsWith("member") //like ‘member%’ 검색
     */
}
