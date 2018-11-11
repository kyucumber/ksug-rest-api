package com.tram.restapi.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void crudTest() {
        Event event = Event.builder()
                    .name("Hello")
                    .description("Test")
                    .build();
        Event savedEvent = eventRepository.save(event);
        assertThat(savedEvent.getId()).isNotNull();

        List<Event> all = eventRepository.findAll();
        assertThat(all.size()).isEqualTo(1);
    }
}
