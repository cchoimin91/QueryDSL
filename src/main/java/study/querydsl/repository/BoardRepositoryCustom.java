package study.querydsl.repository;

import study.querydsl.dto.BoardListDto;

import java.util.List;

public interface BoardRepositoryCustom {
    //게시물의 총 리스트
    List<BoardListDto> getList();

}
