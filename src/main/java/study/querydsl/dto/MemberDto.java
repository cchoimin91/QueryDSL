package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
public class MemberDto {

    private String username;
    private int age;

    @QueryProjection // QFile생성 필요, QueryDSL에 대한 의존성을 갖게됨.
    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
