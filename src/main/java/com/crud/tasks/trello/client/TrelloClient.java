package com.crud.tasks.trello.client;

import com.crud.tasks.domain.TrelloBoardDto;
import com.crud.tasks.domain.TrelloCardDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class TrelloClient {

    @Value("${trello.api.endpoint.prod}")
    private String trelloApiEndpoint;

    @Value("${trello.app.key}")
    private String trelloAppKey;

    @Value("${trello.app.token}")
    private String trelloToken;

    @Value(("${trello.app.username}"))
    private String trelloUsername;

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

        TrelloBoardDto[] boardsResponse = restTemplate.getForObject(
                buildUrl(trelloApiEndpoint, trelloUsername, trelloAppKey, trelloToken, "name,id","all"), TrelloBoardDto[].class);

        if (boardsResponse != null) {
            return Arrays.asList(boardsResponse);
        }
        return new ArrayList<>();
    }

    public CreatedTrelloCard createNewCard(TrelloCardDto trelloCardDto){
        URI url = UriComponentsBuilder.fromHttpUrl(trelloApiEndpoint + "/cards")
                .queryParam("key",trelloApiEndpoint)
                .queryParam("token",trelloToken)
                .queryParam("name",trelloCardDto.getName())
                .queryParam("desc",trelloCardDto.getDescription())
                .queryParam("pos",trelloCardDto.getPos())
                .queryParam("idList",trelloCardDto.getListId()).build().encode().toUri();

        return restTemplate.postForObject(url,null,CreatedTrelloCard.class);
    }
}
