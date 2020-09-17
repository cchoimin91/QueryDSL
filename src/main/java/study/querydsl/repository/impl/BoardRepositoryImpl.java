package study.querydsl.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import study.querydsl.dto.BoardListDto;
import study.querydsl.dto.QBoardListDto;
import study.querydsl.entity.QBoard;
import study.querydsl.entity.QUser;
import study.querydsl.repository.BoardRepositoryCustom;

import javax.persistence.EntityManager;
import java.util.List;

import static study.querydsl.entity.QBoard.board;
import static study.querydsl.entity.QUser.user;

public class BoardRepositoryImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public BoardRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<BoardListDto> getList() {
       return jpaQueryFactory
        .select(new QBoardListDto(
                board.id.as("boardNo")
                ,board.title
                , user.name.as("writer")
                , board.regDate
                )
        )
        .from(board)
        .leftJoin(board.user, user)
        .fetch();
    }


}
