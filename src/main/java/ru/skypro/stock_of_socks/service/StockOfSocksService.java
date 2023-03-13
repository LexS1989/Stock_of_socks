package ru.skypro.stock_of_socks.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skypro.stock_of_socks.constant.OperationFilterEnum;
import ru.skypro.stock_of_socks.constant.TextMessageExceptionEnum;
import ru.skypro.stock_of_socks.dto.CottonSocksDto;
import ru.skypro.stock_of_socks.entity.Color;
import ru.skypro.stock_of_socks.entity.CottonSocks;
import ru.skypro.stock_of_socks.repository.ColorRepository;
import ru.skypro.stock_of_socks.repository.CottonSocksRepository;

import java.util.List;

@Service
@Slf4j
public class StockOfSocksService {

    private final ColorRepository colorRepository;
    private final CottonSocksRepository cottonSocksRepository;

    public StockOfSocksService(ColorRepository colorRepository,
                               CottonSocksRepository cottonSocksRepository) {
        this.colorRepository = colorRepository;
        this.cottonSocksRepository = cottonSocksRepository;
    }

    /**
     * Adding a new item to the DB
     *
     * @param cottonSocksDto cottonPart, quantity, color {@link CottonSocksDto}
     *                       the repository method {@link ColorRepository#findByNameIgnoreCase(String)} is used
     *                       the repository method {@link CottonSocksRepository#findByColorIdAndCottonPart(long, int)} is used
     *                       the repository method {@link CottonSocksRepository#save(Object)} is used
     * @return created or updated record from the DB in the form {@link CottonSocksDto}
     */
    public CottonSocksDto addSocks(CottonSocksDto cottonSocksDto) {
        log.info("addSocks called from StockOfSocksService");
        Color foundColor = colorRepository.findByNameIgnoreCase(cottonSocksDto.getColor())
                .orElseGet(() -> createColor(cottonSocksDto));

        CottonSocks foundQuantitySocks = findQuantitySocks(foundColor, cottonSocksDto.getCottonPart());

        if (foundQuantitySocks == null) {
            foundQuantitySocks = mappingSocksDtoToEntity(cottonSocksDto, foundColor);
        } else {
            foundQuantitySocks.setQuantity(foundQuantitySocks.getQuantity() + cottonSocksDto.getQuantity());
        }

        cottonSocksRepository.save(foundQuantitySocks);
        return mappingEntityToSocksDto(foundQuantitySocks);
    }

    /**
     * Writing off the quantity from the DB
     *
     * @param cottonSocksDto cottonPart, quantity, color {@link CottonSocksDto}
     *                       the repository method {@link CottonSocksRepository#save(Object)} is used
     * @return updated record from the DB in the form {@link CottonSocksDto}
     */
    public CottonSocksDto takeSocks(CottonSocksDto cottonSocksDto) {
        log.info("addSocks called from StockOfSocksService");

        Color foundColor = findColor(cottonSocksDto.getColor());
        if (foundColor == null) {
            log.info("logger: {}", TextMessageExceptionEnum.NOT_FOUND_COLOR.getMessage());
            return null;
        }

        CottonSocks foundQuantitySocks = findQuantitySocks(foundColor, cottonSocksDto.getCottonPart());
        if (foundQuantitySocks == null) {
            log.info("logger: {}", TextMessageExceptionEnum.NOT_FOUND_SOCKS_BY_COTTON_PART.getMessage());
            return null;
        }

        foundQuantitySocks.setQuantity(foundQuantitySocks.getQuantity() - cottonSocksDto.getQuantity());
        if (foundQuantitySocks.getQuantity() < 0) {
            log.info("logger: {}", TextMessageExceptionEnum.NO_REQUIRED_QUANTITY_IN_STOCK.getMessage());
            return null;
        }

        cottonSocksRepository.save(foundQuantitySocks);
        return mappingEntityToSocksDto(foundQuantitySocks);
    }

    /**
     * Method for obtaining all socks by color and amount of cotton content
     *
     * @param color      instance entity {@link Color}
     * @param operation enum filter {@link OperationFilterEnum}
     * @param cottonPart the amount of cotton in the product, the request from the user
     * @return sum of socks of a certain color and amount of cotton
     */
    public Integer getSocksByParam(String color, OperationFilterEnum operation, int cottonPart) {
        log.info("getSocksByParam called from StockOfSocksService");

        Color foundColor = findColor(color);
        if (foundColor == null) {
            log.info("logger: {}", TextMessageExceptionEnum.NOT_FOUND_COLOR.getMessage());
            return null;
        }

        List<CottonSocks> allFoundSocksByColor = cottonSocksRepository.findAllByColorId(foundColor.getId());

        Integer result = 0;
        if (operation == OperationFilterEnum.moreThan) {
            result = allFoundSocksByColor.stream()
                    .filter(socks -> socks.getCottonPart() > cottonPart)
                    .map(cotton -> cotton.getQuantity())
                    .reduce(0, (a, b) -> Integer.sum(a, b));
        }
        else if (operation == OperationFilterEnum.equal) {
            CottonSocks foundQuantitySocks = findQuantitySocks(foundColor, cottonPart);
            if (foundQuantitySocks == null) {
                return result;
            }
            result = foundQuantitySocks.getQuantity();
        }
        else if (operation == OperationFilterEnum.lessThan) {
            result = allFoundSocksByColor.stream()
                    .filter(socks -> socks.getCottonPart() < cottonPart)
                    .map(cotton -> cotton.getQuantity())
                    .reduce(0, (a, b) -> Integer.sum(a, b));
        }

        return result;
    }

    /**
     * Find color by name in BD
     *
     * @param name string with color name
     *                       the repository method {@link ColorRepository#findByNameIgnoreCase(String)} is used
     * @return instance entity {@link Color}
     */
    public Color findColor(String name) {
        return colorRepository.findByNameIgnoreCase(name)
                .orElse(null);
    }

    /**
     * Find quantity socks by color and cotton part
     *
     * @param color      instance entity {@link Color}
     * @param cottonPart the amount of cotton in the product, the request from the user
     *                   the repository method {@link CottonSocksRepository#findByColorIdAndCottonPart(long, int)} is used
     * @return instance {@link CottonSocks} from DB
     */
    public CottonSocks findQuantitySocks(Color color, int cottonPart) {
        return cottonSocksRepository.findByColorIdAndCottonPart(color.getId(), cottonPart)
                .orElse(null);
    }

    /**
     * Method for creating a new color in BD
     *
     * @param cottonSocksDto cottonPart, quantity, color {@link CottonSocksDto}
     *                       the repository method {@link ColorRepository#save(Object)} is used
     * @return new record from database entity {@link Color}
     */
    public Color createColor(CottonSocksDto cottonSocksDto) {
        log.info("createColor called from StockOfSocksService");
        Color color = new Color();
        color.setName(cottonSocksDto.getColor());
        return colorRepository.save(color);
    }

    /**
     * Method for transformation SocksDto in entity StockCottonSocks
     *
     * @param cottonSocksDto cottonPart, quantity, color {@link CottonSocksDto}
     * @param color          instance entity {@link Color}
     * @return entity {@link CottonSocks}
     */
    public CottonSocks mappingSocksDtoToEntity(CottonSocksDto cottonSocksDto, Color color) {
        log.info("mappingSocksDtoToEntity called from StockOfSocksService");
        CottonSocks result = new CottonSocks();
        result.setCottonPart(cottonSocksDto.getCottonPart());
        result.setQuantity(cottonSocksDto.getQuantity());
        result.setColor(color);
        return result;
    }

    /**
     * Method for transformation entity StockCottonSocks in SocksDto
     *
     * @param cottonSocks instance entity {@link CottonSocks}
     * @return DTO {@link CottonSocksDto}
     */
    public CottonSocksDto mappingEntityToSocksDto(CottonSocks cottonSocks) {
        log.info("mappingEntityToSocksDto called from StockOfSocksService");
        CottonSocksDto result = new CottonSocksDto();
        result.setColor(cottonSocks.getColor().getName());
        result.setCottonPart(cottonSocks.getCottonPart());
        result.setQuantity(cottonSocks.getQuantity());
        return result;
    }
}
