package ru.skypro.stock_of_socks.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.stock_of_socks.constant.OperationFilterEnum;
import ru.skypro.stock_of_socks.dto.CottonSocksDto;
import ru.skypro.stock_of_socks.service.StockOfSocksService;

@RestController
@RequestMapping("/api/socks")
@Slf4j
public class StockOfSocksController {

    private final StockOfSocksService stockOfSocksService;

    public StockOfSocksController(StockOfSocksService stockOfSocksService) {
        this.stockOfSocksService = stockOfSocksService;
    }

    @Operation(summary = "addSocksToStock",
            tags = "Приход на склад",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CottonSocksDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = CottonSocksDto.class))
                    ),
                    @ApiResponse(responseCode = "400",
                            description = "Possible reasons: cottonPart < 0 or quantity <= 0",
                            content = @Content),
                    @ApiResponse(responseCode = "500", content = @Content),
            }
    )
    @PostMapping("/income")
    public ResponseEntity<CottonSocksDto> addSocks(@RequestBody CottonSocksDto cottonSocksDto) {
        log.info("addSocksToStock called from StockOfSocksController");
        if (cottonSocksDto.getCottonPart() < 0 || cottonSocksDto.getQuantity() <= 0) {
            log.info("Bad request, cottonPart < 0 or quantity <= 0");
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(stockOfSocksService.addSocks(cottonSocksDto));
    }

    @Operation(summary = "takeSocksFromStock",
            tags = "Отпуск со склада",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CottonSocksDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = CottonSocksDto.class))
                    ),
                    @ApiResponse(responseCode = "400",
                            description ="Possible reasons: cottonPart < 0 or quantity <= 0, or not found color, " +
                                    "or not found cotton part, or written off more than the balance in the warehouse",
                            content = @Content),
                    @ApiResponse(responseCode = "500", content = @Content),
            }
    )
    @PostMapping("/outcome")
    public ResponseEntity<CottonSocksDto> takeSocks(@RequestBody CottonSocksDto cottonSocksDto) {
        log.info("takeSocks called from StockOfSocksController");
        if (cottonSocksDto.getCottonPart() < 0 || cottonSocksDto.getQuantity() <= 0) {
            log.info("Bad request, cottonPart < 0 or quantity <= 0");
            return ResponseEntity.badRequest().build();
        }
        CottonSocksDto result = stockOfSocksService.takeSocks(cottonSocksDto);
        if (result == null) {
            log.info("Bad request");
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "getQuantitySocks",
            tags = "Остаток на складе",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = Integer.class))
                    ),
                    @ApiResponse(responseCode = "400",
                            description = "found color not",
                            content = @Content),
                    @ApiResponse(responseCode = "500", content = @Content),
            }
    )
    @GetMapping
    public ResponseEntity<Integer> getQuantitySocks(@RequestParam String color,
                                                    @RequestParam OperationFilterEnum operation,
                                                    @RequestParam int cottonPart) {
        log.info("getQuantitySocks called from StockOfSocksController");
        Integer result = stockOfSocksService.getSocksByParam(color, operation, cottonPart);
        if (result == null) {
            log.info("Bad request");
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(result);
    }
}
