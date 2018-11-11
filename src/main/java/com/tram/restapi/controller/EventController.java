package com.tram.restapi.controller;

import com.tram.restapi.domain.Event;
import com.tram.restapi.domain.EventRepository;
import com.tram.restapi.domain.EventResource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventDtoValidator eventDtoValidator;
    @PostMapping
    public ResponseEntity create(@RequestBody @Valid EventDto eventDto,
                                 Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
            //body에 Errors를 담아 리턴하지만 Java Bean 스펙이 준수되지 않아 Serialize 되지 않음.
        }

        eventDtoValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        //이벤트 가격에 따라 상태 무료로 전환
        event.update();

        Event savedEvent = eventRepository.save(event);
        //HTTP Header Location에 넣어줄 URI 생성
        //ex) http://localhost/api/events/1
        URI uri = linkTo(EventController.class).slash(savedEvent.getId()).toUri();

        //HATEOAS를 만족하기 위해서 Link, Profile 정보 제공
        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(linkTo(EventController.class).withRel("events"));
        eventResource.add(linkTo(EventController.class).slash(savedEvent.getId()).withRel("update"));
        eventResource.add(new Link("/docs/index.html#resources-events-create", "profile"));

        return ResponseEntity.created(uri).body(eventResource);
    }
}
