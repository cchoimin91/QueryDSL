package study.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Commit;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
    class MemberRepositoryTest {

        @Autowired
        EntityManager em;

        @Autowired
        MemberRepository memberRepository;


        @Test
        public void dataJpa_search(){
            Team teamA = new Team("teamA");
            Team teamB = new Team("teamB");
            em.persist(teamA);
            em.persist(teamB);

            Member member1 = new Member("member1", 10, teamA);
            Member member2 = new Member("member2", 20, teamA);
            Member member3 = new Member("member3", 30, teamB);
            Member member4 = new Member("member4", 40, teamB);

            em.persist(member1);
            em.persist(member2);
            em.persist(member3);
            em.persist(member4);

            //검색조건
            MemberSearchCondition condition = new MemberSearchCondition();
            condition.setUserName("member4");
            condition.setTeamName("teamB");

            List<MemberTeamDto> result = memberRepository.search(condition);
            for (MemberTeamDto memberTeamDto : result) {
                System.out.println("memberTeamDto = " + memberTeamDto);
            }

            assertThat(result).extracting("username").containsExactly("member4");

        }

    @Test
    public void paging(){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        //검색조건
        MemberSearchCondition condition = new MemberSearchCondition();

        PageRequest pageRequest = PageRequest.of(0,3);

        Page<MemberTeamDto> result = memberRepository.searchPageSimple(condition,pageRequest);
        Page<MemberTeamDto> result2 = memberRepository.searchPageComplex(condition,pageRequest);

       assertThat(result.getSize()).isEqualTo(3);
        assertThat(result2.getSize()).isEqualTo(3);

    }

    /**
     * count 쿼리가 생략 가능 할 경우, 생략 해서 처리 가능
     * ex) 페이지 시작 이면서 컨텐츠 사이즈가 페이지보다 작을 때
     * ex) 마지막 페이지 일 때
     */
    @Test
    public void paging_최적화(){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        //검색조건
        MemberSearchCondition condition = new MemberSearchCondition();

        PageRequest pageRequest = PageRequest.of(1,3); // 마지막 페이지 조회 limit 3 offset 1

        Page<MemberTeamDto> search = memberRepository.searchPageOptimization(condition,pageRequest);

        assertThat(search).extracting("username").containsExactly("member4");

    }

}