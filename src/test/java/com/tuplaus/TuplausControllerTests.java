package com.tuplaus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuplaus.component.ResultType;
import com.tuplaus.dto.TuplausWinningCheckResult;
import com.tuplaus.entity.User;
import com.tuplaus.repository.UserRepository;
import com.tuplaus.service.AccountService;
import com.tuplaus.service.TuplausGameWinningService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TuplausControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountService accountService;

    @MockBean
    private TuplausGameWinningService tuplausGameWinningService;

    private User user;
    private User otherUser;

    @BeforeEach
    public void setUp() {
        user = User.builder()
            .name("Pekka Pelaaja")
            .build();

        otherUser = User.builder()
                .name("Keijo Kasino")
                .build();

        userRepository.save(user);
        userRepository.save(otherUser);

        accountService.deposit(user, "50");
    }

    @Test
    @Transactional
    @Rollback
    public void testGameCanBeStarted() throws Exception {
        when(tuplausGameWinningService.determineWinning(any())).thenReturn(new TuplausWinningCheckResult(ResultType.WIN, 1));
        String payload = String.format("{\"playerId\":\"%d\",\"bet\":\"20\",\"pick\":\"SMALL\"}", user.getId());

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/api/start")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(payload))
                .andExpect(MockMvcResultMatchers.status().isOk());

        response.andExpect(jsonPath("$.status", equalTo("success")))
                .andExpect(jsonPath("$.data.gameId", notNullValue()));
    }

    private int getGameIdFromResponse(String response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response);

        return rootNode.path("data").path("gameId").asInt();
    }

    @Test
    @Transactional
    @Rollback
    public void testRoundCanBeDoubled() throws Exception {
        when(tuplausGameWinningService.determineWinning(any())).thenReturn(new TuplausWinningCheckResult(ResultType.WIN, 1));

        String startPayload = String.format("{\"playerId\":\"%d\",\"bet\":\"20\",\"pick\":\"SMALL\"}", user.getId());

         var startResponse = mockMvc.perform(
                     MockMvcRequestBuilders.post("/api/start")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(startPayload)
                 ).andReturn().getResponse().getContentAsString();

        var gameId = getGameIdFromResponse(startResponse);

        String roundPayload = String.format("{\"playerId\":\"%d\",\"gameId\":\"%d\",\"pick\":\"SMALL\"}", user.getId(), gameId);

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/api/double")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(roundPayload))
                .andExpect(MockMvcResultMatchers.status().isOk());

        response.andExpect(jsonPath("$.status", equalTo("success")));
        response.andExpect(jsonPath("$.data.card", equalTo(1)));
        response.andExpect(jsonPath("$.data.result", equalTo("WIN")));
        response.andExpect(jsonPath("$.data.potentialPot", equalTo("80.00")));
        response.andExpect(jsonPath("$.data.balance", equalTo("30.00")));
    }

    @Test
    @Transactional
    @Rollback
    public void testGameCantBePlayedWithTooBigBet() throws Exception {
        String payload = String.format("{\"playerId\":\"%d\",\"bet\":\"200\",\"pick\":\"SMALL\"}", user.getId());

        var response = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/start")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(payload)
        ).andExpect(MockMvcResultMatchers.status().is4xxClientError());

        response.andExpect(jsonPath("$.status", equalTo("error")));
    }

    @Test
    @Transactional
    @Rollback
    public void cantInteractWithOtherPlayersGame() throws Exception {
        when(tuplausGameWinningService.determineWinning(any())).thenReturn(new TuplausWinningCheckResult(ResultType.WIN, 1));

        String startPayload = String.format("{\"playerId\":\"%d\",\"bet\":\"20\",\"pick\":\"SMALL\"}", user.getId());

        var startResponse = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/start")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(startPayload)
        ).andReturn().getResponse().getContentAsString();

        var gameId = getGameIdFromResponse(startResponse);

        String roundPayload = String.format("{\"playerId\":\"%d\",\"gameId\":\"%d\",\"pick\":\"SMALL\"}", otherUser.getId(), gameId);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/double")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(roundPayload))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }
}
