package study.querydsl;


import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sun.xml.bind.v2.schemagen.xmlschema.AttrDecls;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;

import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.team;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @PersistenceUnit
    EntityManagerFactory emf;



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

    /**
     *  복잡하고, 성능이 중요한 페이지에서는 사용 주의
     *  totalCount쿼리를 더 심플하게 가져오는 경우가 있음.
     */
    @Test
    public void fetchResults(){
        QueryResults<Member> result = queryFactory
                .selectFrom(member)
                .fetchResults();

        System.out.println("results.getResults() = " + result.getResults());
        System.out.println("result.getTotal() = " + result.getTotal());

    }

    @Test
    public void sort(){
        em.persist(new Member("member100",100));
        List<Member> members = queryFactory
                .selectFrom(member)
                .orderBy(member.age.desc())
                .fetch();

        assertThat(members.get(0).getAge()).isEqualTo(100);
    }

    @Test
    public void paging1(){
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();

        assertThat(result.size()).isEqualTo(2);
    }

    /**
     * count쿼리 확인필요.
     */
    @Test
    public void paging2(){
        QueryResults<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();

        assertThat(result.getTotal()).isEqualTo(4);
        assertThat(result.getResults().size()).isEqualTo(2);
    }

    /**
     *  tuple실무에서 자주 사용하지 않음
     */
    @Test
    public void aggregation(){
        List<Tuple> result = queryFactory
                .select(
                        member.count()
                        , member.age.sum()
                        , member.age.max()
                        , member.age.min()
                        , member.age.avg()
                )
                .from(member)
                .fetch();

        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
    }

    @Test
    public void group(){

        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(25);
    }

    /**
     * teamA에 소속된 모든 회원
     */
    @Test
    public void join(){
        List<Member> teamA = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        assertThat(teamA)
                .extracting("username")
                .containsExactly("member1", "member2", "member3", "member4");
    }

    /**
     * 세타조인
     * 회원의 이름이 팀 이름과 같은 회원 조회
     * 외부조인 -> on절
     */
    @Test
    public void theta_join(){
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Member> result = queryFactory
                .select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("teamA","teamB");
    }
    
    @Test
    public void join_on_filtering(){
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("teamA"))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * 연관관계 없는 엔티티의 left join
     * 회원의 이름이 팀 이름과 같은 것
     *
     * 하이버네이트5.1부터 on을 사용해서 서로 관계가 없는 컬럼으로 left join 기능이 추가됨
     *
     * 일반조인 : leftJoin(member.team , team) // fk와 join
     * on조인 : from(member).leftJoin(team).on("abc")
     */
    @Test
    public void join_on_no_relation(){
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team).on(member.username.eq(team.name))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    /**
     * fetch join?
     * 연관된 엔티티를 SQL에서 한번에 조회 하는 기능.
     * - SQL에서 기본적으로 제공하는 기능은 아님
     * - 주로 성능 최적화에 사용함
     */
    @Test
    public void fetchJoinNotUse(){
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        // 이미 로딩된 entity인지 판단
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("패치조인 미적용").isFalse();
    }

    @Test
    public void fetchJoinUse(){
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        // 이미 로딩된 entity인지 판단
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("패치조인 적용").isTrue();
    }




}
