package com.ale.filso.models.Brew;

import com.ale.filso.models.AbstractEntity;
import lombok.Getter;

import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Brew extends AbstractEntity {

    /**
     * Nazwa
     */
    @Size(max = 300, message = "Max 300 znaków")
    private String name;

    /**
     * Numer
     */
    private Integer number;

    /**
     * Zakładany BLG
     */
    private BigDecimal assumedBlg; //one decimal place

//    /**
//     * Rzeczywisty BLG
//     */
//    private BigDecimal realBlg;

    /**
     * Zakładana ilość
     */
    private Integer assumedAmount;

    /**
     * Przepis
     */
    private String recipe;

//    /**
//     * Rzeczywisty ilość
//     */
//    private BigDecimal realAmount;

    //todo Lista blg - mierzenie w trakcie fermentacji

//    /**
//     * Zawartość alkoholu
//     */
//    private BigDecimal alcohol;


    //todo Zbiornik

}
