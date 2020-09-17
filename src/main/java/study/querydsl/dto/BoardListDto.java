package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 게시물의 리스트
 */
@Data
public class BoardListDto {

    private Long boardNo;
    private String title;
    private String writer;
    private LocalDateTime regDate;


    @QueryProjection
    public BoardListDto(Long boardNo, String title, String writer, LocalDateTime regDate) {
        this.boardNo = boardNo;
        this.title = title;
        this.writer = writer;
        this.regDate = regDate;
    }
}
