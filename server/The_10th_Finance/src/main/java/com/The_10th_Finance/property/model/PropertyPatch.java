package com.The_10th_Finance.property.model;

import lombok.*;

import javax.validation.constraints.Pattern;
import java.math.BigDecimal;



@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyPatch {

    private String title;

    private String content;

    private BigDecimal amount;



}
