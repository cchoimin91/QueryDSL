package study.querydsl.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;

import javax.persistence.EntityManager;
import java.util.List;

import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

// Impl <- 네이밍 맞춰줘야함 ex) AbcdRepository + Impl
public class MemberRepositoryImpl implements  MemberRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<MemberTeamDto> search(MemberSearchCondition condition){
        return jpaQueryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId")
                        , member.username
                        , member.age
                        , team.id.as("teamId")
                        , team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUserName())
                        , teamNameEq(condition.getTeamName())
                )
                .fetch();
    }

    private BooleanExpression usernameEq(String username){
        return StringUtils.hasText(username) ? member.username.eq(username) : null;
    }

    private BooleanExpression teamNameEq(String teamName) {
        return StringUtils.hasText(teamName) ? team.name.eq(teamName) : null;
    }

    /**
     * count 쿼리가 간단 할 때 fetchResult();
     */
    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
         QueryResults<MemberTeamDto> result = jpaQueryFactory
                 .select(new QMemberTeamDto(
                        member.id.as("memberId")
                        , member.username
                        , member.age
                        , team.id.as("teamId")
                        , team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUserName())
                        , teamNameEq(condition.getTeamName())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults(); // count쿼리와, content 쿼리 같이 날림

         List<MemberTeamDto> content = result.getResults();
         long count = result.getTotal();

         return new PageImpl<>(content, pageable,count);
    }

    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
        List<MemberTeamDto> content = jpaQueryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId")
                        , member.username
                        , member.age
                        , team.id.as("teamId")
                        , team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUserName())
                        , teamNameEq(condition.getTeamName())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long count = jpaQueryFactory
                .select(member)
                .from(member)
                //.leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUserName())
                        , teamNameEq(condition.getTeamName())
                )
                .fetchCount();

        return new PageImpl<>(content, pageable,count);

    }
}
