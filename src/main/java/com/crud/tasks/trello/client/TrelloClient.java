package com.crud.tasks.trello.client;

import com.crud.tasks.domain.TrelloBoardDto;
import com.crud.tasks.domain.TrelloCardDto;
import com.crud.tasks.trello.config.TrelloConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Optional.ofNullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Component
public class TrelloClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrelloClient.class);

    @Autowired
    private TrelloConfig trelloConfig;

    @Autowired
    private RestTemplate restTemplate;


    private URI buildUrl(String trelloEndpoint, String username, String trelloKey, String token, String fields, String list) {
        URI url = UriComponentsBuilder.fromHttpUrl(trelloEndpoint + "/members/" + username + "/boards")
                .queryParam("key", trelloKey)
                .queryParam("token", token)
                .queryParam("fields", fields)
                .queryParam("lists",list).build().encode().toUri();
        return url;

    }

    public List<TrelloBoardDto> getTrelloBoards() {


        try {
            TrelloBoardDto[] boardsResponse = restTemplate.getForObject(
                    buildUrl(trelloConfig.getTrelloApiEndpoint(), trelloConfig.getTrelloUsername(),
                            trelloConfig.getTrelloAppKey(), trelloConfig.getTrelloToken(),
                            "name,id", "all"),
                    TrelloBoardDto[].class);
            return Arrays.asList(ofNullable(boardsResponse).orElse(new TrelloBoardDto[0]));
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public CreatedTrelloCard createNewCard(TrelloCardDto trelloCardDto){
        URI url = UriComponentsBuilder.fromHttpUrl(trelloConfig.getTrelloApiEndpoint() + "/cards")
                .queryParam("key",trelloConfig.getTrelloAppKey())
                .queryParam("token",trelloConfig.getTrelloToken())
                .queryParam("name",trelloCardDto.getName())
                .queryParam("desc",trelloCardDto.getDescription())
                .queryParam("pos",trelloCardDto.getPos())
                .queryParam("idList",trelloCardDto.getListId()).build().encode().toUri();

        System.out.println(url);
        return restTemplate.postForObject(url,null,CreatedTrelloCard.class);
    }
}
