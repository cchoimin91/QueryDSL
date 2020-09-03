package study.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

}