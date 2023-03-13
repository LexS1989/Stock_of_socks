package ru.skypro.stock_of_socks.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "stock_cotton_socks")
public class CottonSocks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int cottonPart;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "color_id")
    private Color color;
}
