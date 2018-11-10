package com.tram.restapi.controller;

import com.tram.restapi.domain.Event;
import com.tram.restapi.domain.EventRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EventRepository eventRepository;
    @PostMapping
    public ResponseEntity create(@RequestBody EventDto eventDto,
                                 Errors errors) {
        Event event = modelMapper.map(eventDto, Event.class);
        Event savedEvent = eventRepository.save(event);
        URI uri = ControllerLinkBuilder.linkTo(EventController.class).slash(savedEvent.getId()).toUri();
        return ResponseEntity.created(uri).body(savedEvent);
    }
}
