package com.lili.model.vo;

import com.lili.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@Data
@AllArgsConstructor
public class PageSafetyUserVO{

    private long total;

    private List<SafetyUserVO> userList;

}
