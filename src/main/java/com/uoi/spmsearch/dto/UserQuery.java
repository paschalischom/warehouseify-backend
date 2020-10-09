package com.uoi.spmsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserQuery {

    QueryRanking queryRanking;
    QueryFilter queryFilter;
}
