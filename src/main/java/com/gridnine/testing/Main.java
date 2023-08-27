package com.gridnine.testing;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalDateTime.*;

public class Main {
    public static void main(String[] args) {

        List<Flight> flights = FlightBuilder.createFlights();
        // Фильтр 1: Вылет до текущего момента времени
        List<Flight> flightsBeforeNow = flights.stream()
                .filter(flight -> flight.getSegments().stream()
                        .allMatch(segment -> segment.getDepartureDate().isBefore(now())))
                .collect(Collectors.toList());

        // Фильтр 2: Имеются сегменты с датой прилёта раньше даты вылета
        List<Flight> flightsWithBadSegments = flights.stream()
                .filter(flight -> flight.getSegments().stream()
                        .anyMatch(segment -> segment.getArrivalDate().isBefore(segment.getDepartureDate())))
                .collect(Collectors.toList());

        // Фильтр 3: Общее время на земле превышает два часа
        List<Flight> flightsWithLongGroundTime = flights.stream()
                .filter(flight -> {
                    Duration totalGroundTime = Duration.ZERO;
                    List<Segment> segments = flight.getSegments();
                    for (int i = 0; i < segments.size() - 1; i++) {
                        LocalDateTime currentArrival = segments.get(i).getArrivalDate();
                        LocalDateTime nextDeparture = segments.get(i + 1).getDepartureDate();
                        Duration groundTime = Duration.between(currentArrival, nextDeparture);

                        if (groundTime.toHours() > 0) {
                            totalGroundTime = totalGroundTime.plus(groundTime);
                        }
                    }

                    return totalGroundTime.toHours() > 2;
                })
                .collect(Collectors.toList());

        // Вывод результатов
        System.out.println("Вылет до текущего момента времени:");
        flightsBeforeNow.forEach(System.out::println);

        System.out.println("\nИмеются сегменты с датой прилёта раньше даты вылета:");
        flightsWithBadSegments.forEach(System.out::println);

        System.out.println("\nОбщее время, проведённое на земле превышает два часа:");
        flightsWithLongGroundTime.forEach(System.out::println);

        System.out.println("\nОтфильтрованый лист полетов: ");
        Stream<Flight> flightStream = flights.stream()
                .filter(flight -> !flightsBeforeNow.contains(flight)
                        && !flightsWithBadSegments.contains(flight)
                        && !flightsWithLongGroundTime.contains(flight));
        flightStream.forEach(System.out::println);

    }
}
