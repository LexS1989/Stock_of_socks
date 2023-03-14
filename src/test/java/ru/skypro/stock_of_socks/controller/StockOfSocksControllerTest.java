package ru.skypro.stock_of_socks.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.stock_of_socks.dto.CottonSocksDto;
import ru.skypro.stock_of_socks.entity.Color;
import ru.skypro.stock_of_socks.entity.CottonSocks;
import ru.skypro.stock_of_socks.repository.ColorRepository;
import ru.skypro.stock_of_socks.repository.CottonSocksRepository;
import ru.skypro.stock_of_socks.service.StockOfSocksService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StockOfSocksController.class)
class StockOfSocksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ColorRepository colorRepository;

    @MockBean
    private CottonSocksRepository cottonSocksRepository;

    @SpyBean
    private StockOfSocksService stockOfSocksService;

    @InjectMocks
    private StockOfSocksController stockOfSocksController;

    @Test
    public void addSocksPositiveTest() throws Exception {
        CottonSocksDto cottonSocksDto = new CottonSocksDto();
        cottonSocksDto.setCottonPart(90);
        cottonSocksDto.setQuantity(100);
        cottonSocksDto.setColor("green");

        Color color = new Color();
        color.setId(1L);
        color.setName(cottonSocksDto.getColor());

        CottonSocks cottonSocks = new CottonSocks();
        cottonSocks.setId(1L);
        cottonSocks.setCottonPart(cottonSocksDto.getCottonPart());
        cottonSocks.setQuantity(cottonSocksDto.getQuantity());
        cottonSocks.setColor(color);

        CottonSocksDto expectedCottonSocksDto = new CottonSocksDto();
        expectedCottonSocksDto.setCottonPart(90);
        expectedCottonSocksDto.setQuantity(100);
        expectedCottonSocksDto.setColor("green");

        JSONObject cottonSocksObject = new JSONObject();
        cottonSocksObject.put("cottonPart", cottonSocksDto.getCottonPart());
        cottonSocksObject.put("quantity", cottonSocksDto.getQuantity());
        cottonSocksObject.put("color", cottonSocksDto.getColor());

        when(colorRepository.findByNameIgnoreCase(anyString()))
                .thenReturn(Optional.empty());
        when(colorRepository.save(any(Color.class)))
                .thenReturn(color);
        when(cottonSocksRepository.findByColorIdAndCottonPart(anyLong(), anyInt()))
                .thenReturn(Optional.empty());
        when(cottonSocksRepository.save(any(CottonSocks.class)))
                .thenReturn(cottonSocks);

        mockMvc.perform(post("/api/socks/income")
                        .content(cottonSocksObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cottonPart").value(expectedCottonSocksDto.getCottonPart()))
                .andExpect(jsonPath("$.quantity").value(expectedCottonSocksDto.getQuantity()))
                .andExpect(jsonPath("$.color").value(expectedCottonSocksDto.getColor()));
    }

    @Test
    public void takeSocksPositiveTest() throws Exception {
        CottonSocksDto cottonSocksDto = new CottonSocksDto();
        cottonSocksDto.setCottonPart(90);
        cottonSocksDto.setQuantity(10);
        cottonSocksDto.setColor("green");

        Color color = new Color();
        color.setId(1L);
        color.setName(cottonSocksDto.getColor());

        CottonSocks cottonSocks = new CottonSocks();
        cottonSocks.setId(1L);
        cottonSocks.setCottonPart(cottonSocksDto.getCottonPart());
        cottonSocks.setQuantity(100);
        cottonSocks.setColor(color);

        CottonSocksDto expectedCottonSocksDto = new CottonSocksDto();
        expectedCottonSocksDto.setCottonPart(90);
        expectedCottonSocksDto.setQuantity(90);
        expectedCottonSocksDto.setColor("green");

        JSONObject cottonSocksObject = new JSONObject();
        cottonSocksObject.put("cottonPart", cottonSocksDto.getCottonPart());
        cottonSocksObject.put("quantity", cottonSocksDto.getQuantity());
        cottonSocksObject.put("color", cottonSocksDto.getColor());

        when(colorRepository.findByNameIgnoreCase(anyString()))
                .thenReturn(Optional.of(color));
        when(cottonSocksRepository.findByColorIdAndCottonPart(anyLong(), anyInt()))
                .thenReturn(Optional.of(cottonSocks));
        when(cottonSocksRepository.save(any(CottonSocks.class)))
                .thenReturn(cottonSocks);

        mockMvc.perform(post("/api/socks/outcome")
                        .content(cottonSocksObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cottonPart").value(expectedCottonSocksDto.getCottonPart()))
                .andExpect(jsonPath("$.quantity").value(expectedCottonSocksDto.getQuantity()))
                .andExpect(jsonPath("$.color").value(expectedCottonSocksDto.getColor()));
    }
}