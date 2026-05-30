package com.privetmedved.controller.mvc;

import com.privetmedved.dto.WeatherDto;
import com.privetmedved.service.RoomService;
import com.privetmedved.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final RoomService roomService;
    private final WeatherService weatherService;

    public HomeController(RoomService roomService, WeatherService weatherService) {
        this.roomService = roomService;
        this.weatherService = weatherService;
    }



    @GetMapping("/")
    public String home(Model model) {
        WeatherDto weather = weatherService.getWeather("Moscow");
        if (weather == null) {
            weather = new WeatherDto("Moscow", 0.0, "No data", "unknown");
        }
        model.addAttribute("weather", weather);
        model.addAttribute("rooms", roomService.findPublicRooms());
        model.addAttribute("weather", weatherService.getWeather("Moscow"));
        return "index";
    }
}