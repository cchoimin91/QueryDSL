package study.querydsl.dto;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class MemberTeamDto {

    private Long MemberId;
    private String username;
    private int age;
    private Long teamId;
    private String teamname;

    @QueryProjection
    public MemberTeamDto(Long memberId, String username, int age, Long teamId, String teamname) {
        MemberId = memberId;
        this.username = username;
        this.age = age;
        this.teamId = teamId;
        this.teamname = teamname;
    }
}
