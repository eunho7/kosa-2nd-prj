package com.example._team.dto.report;

import com.example._team.domain.Board;
import com.example._team.domain.Reports;
import com.example._team.domain.Users;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReportsRequestDto {
    private String content;
    private Integer boardIdx;

    public static Reports toSaveEntity(ReportsRequestDto request, Users user, Board board) {
        Reports reports = new Reports();

        String content = request.getContent();
        String contentConvert = null;
        switch (content){
            case "commercial_promotion" : contentConvert = "영리목적/홍보성"; break;
            case "illegal_information" : contentConvert = "불법정보"; break;
            case "abuse_personal_attack" : contentConvert = "욕설/인신공격"; break;
            case "spam_repetition": contentConvert = "같은 내용 반복(도배)"; break;
            case  "personal_information_exposure": contentConvert = "개인 정보 노출"; break;
            case "obscene_content": contentConvert = "음란성/선정성"; break;
            case "other": contentConvert = "기타"; break;
        }
        reports.setContent(contentConvert);
        reports.setUserIdx(user);
        reports.setBoardIdx(board);
        return reports;
    }
}
