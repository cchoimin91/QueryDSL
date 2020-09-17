package study.querydsl.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import study.querydsl.dto.BoardListDto;
import study.querydsl.entity.Board;
import study.querydsl.entity.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@Transactional
@SpringBootTest
class BoardRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    BoardRepository boardRepository;

    @Test
    @Commit
    public void queryMethod(){
        Board board1 = new Board("안녕3242");
        Board board2 = new Board("안녕하23424세요");
        em.persist(board1);
        em.persist(board2);

        Optional<Board> board = boardRepository.findById(1L);
        System.out.println("board = " + board);
    }

    @Test
    @Commit
    public void 리스트조회(){
        User user1 = new User("피카츄");
        User user2 = new User("파이리");
        User user3 = new User("꼬부기");
        em.persist(user1);
        em.persist(user2);
        em.persist(user3);

        Board board1 = new Board("제목111","내용111", user1 );
        Board board2 = new Board("제목222","내용222", user2 );
        Board board3 = new Board("제목333","내용333", user3 );
        em.persist(board1);
        em.persist(board2);
        em.persist(board3);

        List<BoardListDto> list = boardRepository.getList();

        assertThat(list).size().isEqualTo(3);
    }

}