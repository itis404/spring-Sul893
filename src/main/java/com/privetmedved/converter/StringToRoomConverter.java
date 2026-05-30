package com.privetmedved.converter;

import com.privetmedved.entity.Room;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToRoomConverter implements Converter<String, Room> {

    @Override
    public Room convert(String source) {
        Room room = new Room();
        room.setId(Long.parseLong(source));
        return room;
    }
}