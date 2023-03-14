package ru.skypro.stock_of_socks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.stock_of_socks.entity.CottonSocks;

import java.util.List;
import java.util.Optional;

@Repository
public interface CottonSocksRepository extends JpaRepository<CottonSocks, Long> {

    Optional<CottonSocks> findByColorIdAndCottonPart(long colorId, int cottonPart);

    List<CottonSocks> findAllByColorId(long colorId);
}
