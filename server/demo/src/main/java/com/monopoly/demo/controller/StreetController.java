package com.monopoly.demo.controller;

import com.monopoly.demo.model.Street;

import com.monopoly.demo.repository.StreetRepository;

import com.monopoly.demo.controller.MatchController.StreetTemplate;
import com.monopoly.demo.model.Match;
import com.monopoly.demo.model.Street;
import com.monopoly.demo.model.User;
import com.monopoly.demo.service.StreetService;
import com.monopoly.demo.repository.MatchRepository;
import com.monopoly.demo.repository.UserRepository;
import com.monopoly.demo.repository.StreetRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.io.Console;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import java.util.Random;
import java.util.Map;

@RestController
@RequestMapping("/streets")
@CrossOrigin(origins = "http://localhost:5173")
public class StreetController {

    private final MatchRepository repository;
    private final UserRepository urepository;
    private final StreetRepository srepository;
    private final StreetService sservice;

    public StreetController(StreetService sservice, MatchRepository repository, UserRepository urepository, StreetRepository srepository) {
        this.sservice = sservice;
        this.repository = repository;
        this.urepository = urepository;
        this.srepository = srepository;
    }

    public record StreetTemplate(String name, Integer price, boolean canBeBought, boolean isSpecial) {}

    public static final StreetTemplate[] BOARD = new StreetTemplate[] {
        new StreetTemplate("Los", 0, false, true),
        new StreetTemplate("Badstraße", 60, true, false),
        new StreetTemplate("Gemeinschaftsfeld", null, false, true),
        new StreetTemplate("Turmstraße", 60, true, false),
        new StreetTemplate("Einkommensteuer", -200, false, true),
        new StreetTemplate("Südbahnhof", 200, true, true),
        new StreetTemplate("Chausseestraße", 100, true, false),
        new StreetTemplate("Ereignisfeld", null, false, true),
        new StreetTemplate("Elisenstraße", 100, true, false),
        new StreetTemplate("Poststraße", 120, true, false),
        new StreetTemplate("Gefängnis / Nur zu Besuch", null, false, true),

        new StreetTemplate("Seestraße", 140, true, false),
        new StreetTemplate("Elektrizitätswerk", 150, true, true),
        new StreetTemplate("Hafenstraße", 140, true, false),
        new StreetTemplate("Neue Straße", 160, true, false),
        new StreetTemplate("Westbahnhof", 200, true, true),
        new StreetTemplate("Münchener Straße", 180, true, false),
        new StreetTemplate("Gemeinschaftsfeld", null, false, true),
        new StreetTemplate("Wiener Straße", 180, true, false),
        new StreetTemplate("Berliner Straße", 200, true, false),
        new StreetTemplate("Frei Parken", 100, false, true),

        new StreetTemplate("Theaterstraße", 220, true, false),
        new StreetTemplate("Ereignisfeld", null, false, true),
        new StreetTemplate("Museumstraße", 220, true, false),
        new StreetTemplate("Opernplatz", 240, true, false),
        new StreetTemplate("Nordbahnhof", 200, true, true),
        new StreetTemplate("Lessingstraße", 260, true, false),
        new StreetTemplate("Schillerstraße", 260, true, false),
        new StreetTemplate("Wasserwerk", 150, true, true),
        new StreetTemplate("Goethestraße", 280, true, false),
        new StreetTemplate("Gehe ins Gefängnis", 0, false, true),

        new StreetTemplate("Rathausplatz", 300, true, false),
        new StreetTemplate("Hauptstraße", 300, true, false),
        new StreetTemplate("Gemeinschaftsfeld", null, false, true),
        new StreetTemplate("Bahnhofstraße", 320, true, false),
        new StreetTemplate("Hauptbahnhof", 200, true, true),
        new StreetTemplate("Ereignisfeld", null, false, true),
        new StreetTemplate("Parkstraße", 350, true, false),
        new StreetTemplate("Zusatzsteuer", -100, false, true),
        new StreetTemplate("Schlossallee", 400, true, false)
    };

    public static final Map<Integer, int[]> RENT_TABLE = Map.ofEntries(

        Map.entry(1,  new int[]{2, 4, 10, 30, 90, 160, 250}), // Badstraße
        Map.entry(3,  new int[]{4, 8, 20, 60, 180, 320, 450}), // Turmstraße

        Map.entry(6,  new int[]{6, 12, 30, 90, 270, 400, 550}), // Chausseestraße
        Map.entry(8,  new int[]{6, 12, 30, 90, 270, 400, 550}), // Elisenstraße
        Map.entry(9,  new int[]{8, 16, 40, 100, 300, 450, 600}), // Poststraße

        Map.entry(11, new int[]{10, 20, 50, 150, 450, 625, 750}), // Seestraße
        Map.entry(13, new int[]{10, 20, 50, 150, 450, 625, 750}), // Hafenstraße
        Map.entry(14, new int[]{12, 24, 60, 180, 500, 700, 900}), // Neue Straße

        Map.entry(16, new int[]{14, 28, 70, 200, 550, 750, 950}), // Münchener Straße
        Map.entry(18, new int[]{14, 28, 70, 200, 550, 750, 950}), // Wiener Straße
        Map.entry(19, new int[]{16, 32, 80, 220, 600, 800, 1000}), // Berliner Straße

        Map.entry(21, new int[]{18, 36, 90, 250, 700, 875, 1050}), // Theaterstraße
        Map.entry(23, new int[]{18, 36, 90, 250, 700, 875, 1050}), // Museumstraße
        Map.entry(24, new int[]{20, 40, 100, 300, 750, 925, 1100}), // Opernplatz

        Map.entry(26, new int[]{22, 44, 110, 330, 800, 975, 1150}), // Lessingstraße
        Map.entry(27, new int[]{22, 44, 110, 330, 800, 975, 1150}), // Schillerstraße
        Map.entry(29, new int[]{24, 48, 120, 360, 850, 1025, 1200}), // Goethestraße

        Map.entry(31, new int[]{26, 52, 130, 390, 900, 1100, 1275}), // Rathausplatz
        Map.entry(32, new int[]{26, 52, 130, 390, 900, 1100, 1275}), // Hauptstraße
        Map.entry(34, new int[]{28, 56, 150, 450, 1000, 1200, 1400}), // Bahnhofstraße

        Map.entry(37, new int[]{35, 70, 175, 500, 1100, 1300, 1500}), // Parkstraße
        Map.entry(39, new int[]{50, 100, 200, 600, 1400, 1700, 2000}) // Schlossallee
    );

    @PostMapping("/buystreet/{index}")
    public ResponseEntity<String> buystreet(@PathVariable Integer index, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        Optional<User> userOpt = urepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();

        List<Match> matches = repository.findByCreaterOrSecondplayerOrThirdplayerOrFourthplayer(
                user, user, user, user
        );

        if (matches.isEmpty()) {
            return ResponseEntity.status(404).body("No active match found");
        }

        Match match = matches.get(0);

        if (match.getWinner() != null){
            return ResponseEntity.status(404).body("Game is over");
        }

        Optional<Street> streetOpt = srepository.findByMatchIdAndStreetIndex(match.getId(), index);

        if (!BOARD[index].canBeBought){
            return ResponseEntity.status(404).body("Straße kann nicht gekauft werden");
        }
        else if (index >= BOARD.length || index < 0) {
            return ResponseEntity.status(404).body("Index ungültig");
        }
        else if (!streetOpt.isEmpty()){
            return ResponseEntity.status(404).body("Straße wurde bereits gekauft");
        }
        else if (user.getMoney() < BOARD[index].price){
            return ResponseEntity.status(404).body("Straße ist zu teuer");
        }
        else {
            try {
                Street newstreet = new Street();
                newstreet.setOwner(user);
                newstreet.setMatch(match);
                if (!BOARD[index].isSpecial)
                {
                    double perc = sservice.getOwnershipPercentByIndex(user, index);
                    if (((index <= 3 || index >= 37) && perc >= 50.0) || perc >= (2.0 / 3)){
                        newstreet.setPrice((int)(RENT_TABLE.get(index)[1]));
                        for (Street s : sservice.getallstreetsofanindex(index)){
                            s.setPrice(RENT_TABLE.get(s.getIndex())[1]);
                            srepository.save(s);
                        }
                    }
                    else {
                        newstreet.setPrice((int)(RENT_TABLE.get(index)[0]));
                    }
                }
                // Bahnhöfe
                else if (index == 5 || index == 15 || index == 25 || index == 35) {
                    double stationsOwned = sservice.getOwnershipPercentByIndex(user, index);
                    for (Street s : sservice.getallstreetsofanindex(index)){
                        s.setPrice((int) (25 * ((stationsOwned + 25.0) / 100.0) * 4));
                    }
                    newstreet.setPrice((int) (25 * ((stationsOwned + 25.0) / 100.0) * 4));
                } 
                else {
                    newstreet.setPrice(0);
                }
                newstreet.setIndex(index);
                newstreet.setIsSpecial(BOARD[index].isSpecial);
                newstreet.setHotels(0);
                newstreet.setHouses(0);
                user.setMoney(user.getMoney() - BOARD[index].price);
                urepository.save(user);
                srepository.save(newstreet);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Fehler beim Speichern: " + e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Straße gekauft");
        }
    }

    @PostMapping("/sellstreet/{index}")
    public ResponseEntity<String> sellstreet(@PathVariable Integer index, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        Optional<User> userOpt = urepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();

        List<Match> matches = repository.findByCreaterOrSecondplayerOrThirdplayerOrFourthplayer(
                user, user, user, user
        );

        if (matches.isEmpty()) {
            return ResponseEntity.status(404).body("No active match found");
        }

        Match match = matches.get(0);

        if (match.getWinner() != null){
            return ResponseEntity.status(404).body("Game is over");
        }

        Optional<Street> streetOpt = srepository.findByMatchIdAndStreetIndex(match.getId(), index);

        if (!BOARD[index].canBeBought){
            return ResponseEntity.status(404).body("Straße kann nicht verkauft werden");
        }
        else if (index >= BOARD.length || index < 0) {
            return ResponseEntity.status(404).body("Index ungültig");
        }
        else if (streetOpt.isEmpty()){
            return ResponseEntity.status(404).body("Straße exestiert nicht");
        }
        else {
            try {
                Street street = streetOpt.get();
                if (!BOARD[index].isSpecial)
                {
                    for (Street s : sservice.getallstreetsofanindex(index)){
                        s.setPrice(RENT_TABLE.get(s.getIndex())[0]);
                        srepository.save(s);
                    }
                }
                // Bahnhöfe
                else if (index == 5 || index == 15 || index == 25 || index == 35) {
                    double stationsOwned = sservice.getOwnershipPercentByIndex(user, index);
                    for (Street s : sservice.getallstreetsofanindex(index)){
                        s.setPrice((int) (25 * ((stationsOwned - 25.0) / 100.0) * 4));
                    }
                } 


                user.setMoney(user.getMoney() + BOARD[index].price / 2);
                urepository.save(user);
                srepository.delete(street);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Fehler beim Speichern: " + e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Straße gekauft");
        }
    }

    @PostMapping("/buybuilding/{index}/{kind}")
    public ResponseEntity<String> buybuilding(@PathVariable Integer index, @PathVariable String kind, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (!("hotel".equals(kind) || "house".equals(kind))) {
            return ResponseEntity.status(401).body("Art des Gebäudes falsch");
        }

        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        Optional<User> userOpt = urepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();

        List<Match> matches = repository.findByCreaterOrSecondplayerOrThirdplayerOrFourthplayer(
                user, user, user, user
        );

        if (matches.isEmpty()) {
            return ResponseEntity.status(404).body("No active match found");
        }

        Match match = matches.get(0);

        if (match.getIsActive() > 0){
            return ResponseEntity.status(404).body("Du musst zuerst würfeln");
        }

        if ((match.getIsActive() * - 1) != user.getTurn_number()){
            return ResponseEntity.status(404).body("Du bist nicht dran");
        }

        if (match.getWinner() != null){
            return ResponseEntity.status(404).body("Game is over");
        }

        Optional<Street> streetOpt = srepository.findByMatchIdAndStreetIndex(match.getId(), index);

        if (streetOpt.isEmpty()){
            return ResponseEntity.status(404).body("Straße wurde noch nicht gekauft");
        }

        Street street = streetOpt.get();

        if (street.getHotels() >= 1){
            return ResponseEntity.status(404).body("Maximal 1 Hotel pro Strasse");
        }
        if (kind.equals("house") && street.getHouses() == 4){
            return ResponseEntity.status(404).body("Du musst hier ein Hotel bauen");
        }
        if (street.getIsSpecial()){
            return ResponseEntity.status(404).body("Hier kann kein gebäude gebaut werden");
        }
        else if (index >= BOARD.length || index < 0) {
            return ResponseEntity.status(404).body("Index ungültig");
        }
        else if (street.getOwner() != user){
            return ResponseEntity.status(404).body("Die Straße gehört dir nicht");
        }
        else if (!(sservice.getOwnershipPercentByIndex(user, index) == 100.0)){
            return ResponseEntity.status(404).body("Du musst zu erst alle Straßen dieser Farbe besitzen");
        }
        else {
            int diff = kind.equals("hotel") ? (street.getHotels() + 1) * 5 : street.getHouses() + 1;

            for (Street s : sservice.getallstreetsofanindex(index)) {
                int other = s.getHotels() != 0 ? s.getHotels() * 5 : s.getHouses();
                if (Math.abs(diff - other) > 1) {
                    return ResponseEntity.status(404)
                        .body("Du musst die Häuser gleichmäßig bauen");
                }
            }

            int price = 0;
            if (index == 1 || index == 3 || index == 6 || index == 8 || index == 9) {
                price = 50;
            }

            else if (index == 11 || index == 13 || index == 14 || index == 16 || index == 18 || index == 19) {
                price = 100;
            }

            else if (index == 21 || index == 23 || index == 24 || index == 26 || index == 27 || index == 29) {
                price = 150;
            }

            else if (index == 31 || index == 32 || index == 34 || index == 37 || index == 39) {
                price = 200;
            }

            else {
                return ResponseEntity.status(404).body("Index ungültig");
            }

            if (user.getMoney() < price){
                return ResponseEntity.status(404).body("Zu teuer");
            }

            else if ("hotel".equals(kind)) {
                if(street.getHouses() >= 4){
                    street.setHouses(street.getHouses() - 4);
                    street.setHotels(street.getHotels() + 1);
                }
                else{
                    return ResponseEntity.status(404).body("Du brauchst " + (4 - street.getHouses()) + " mehr Häuser" );
                }
            }

            else if ("house".equals(kind)) {
                street.setHouses(street.getHouses() + 1);
            }

            user.setMoney(user.getMoney() - price);

            int newPosition = index;
            int rent = 0;

            int[] rents = RENT_TABLE.get(index);
            if (street.getHotels() > 0) {
                rent = rents[6];
            } else if (street.getHouses() > 0) {
                rent = rents[street.getHouses() + 1];
            } else if (sservice.getOwnershipPercentByIndex(street.getOwner(), newPosition) == 1) {
                rent = rents[1];
            } else {
                rent = rents[0];
            }

            street.setPrice(rent);
            urepository.save(user);
            srepository.save(street);
            return ResponseEntity.status(HttpStatus.CREATED).body(kind + " gekauft");
        }
    }

    @PostMapping("/sellbuilding/{index}/{kind}")
    public ResponseEntity<String> sellbuilding(@PathVariable Integer index, @PathVariable String kind, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (!("hotel".equals(kind) || "house".equals(kind))) {
            return ResponseEntity.status(401).body("Art des Gebäudes falsch");
        }

        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        Optional<User> userOpt = urepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();

        List<Match> matches = repository.findByCreaterOrSecondplayerOrThirdplayerOrFourthplayer(
                user, user, user, user
        );

        if (matches.isEmpty()) {
            return ResponseEntity.status(404).body("No active match found");
        }

        Match match = matches.get(0);

        if (match.getIsActive() > 0){
            return ResponseEntity.status(404).body("Du musst zuerst würfeln");
        }

        if ((match.getIsActive() * - 1) != user.getTurn_number()){
            return ResponseEntity.status(404).body("Du bist nicht dran");
        }

        if (match.getWinner() != null){
            return ResponseEntity.status(404).body("Game is over");
        }

        Optional<Street> streetOpt = srepository.findByMatchIdAndStreetIndex(match.getId(), index);

        if (streetOpt.isEmpty()){
            return ResponseEntity.status(404).body("Straße wurde noch nicht gekauft");
        }

        Street street = streetOpt.get();

        if ("hotel".equals(kind) && street.getHotels() <= 0) {
            return ResponseEntity.status(404).body("Kein Hotel zu verkaufen");
        }

        if ("house".equals(kind) && street.getHouses() <= 0) {
            return ResponseEntity.status(404).body("Kein Haus zu verkaufen");
        }
        if (street.getIsSpecial()){
            return ResponseEntity.status(404).body("Hier kann kein gebäude verkauft werden");
        }
        else if (index >= BOARD.length || index < 0) {
            return ResponseEntity.status(404).body("Index ungültig");
        }
        else if (street.getOwner() != user){
            return ResponseEntity.status(404).body("Die Straße gehört dir nicht");
        }
        else if (!(sservice.getOwnershipPercentByIndex(user, index) == 100.0)){
            return ResponseEntity.status(404).body("Du musst zu erst alle Straßen dieser Farbe besitzen");
        }
        else {
            int diff = kind.equals("hotel") ? (street.getHotels() - 1) + 4 : street.getHouses() - 1;

            for (Street s : sservice.getallstreetsofanindex(index)) {
                int other = s.getHotels() != 0 ? s.getHotels() * 5 : s.getHouses();
                if (Math.abs(diff - other) > 1) {
                    return ResponseEntity.status(404)
                        .body("Du musst die Häuser gleichmäßig bauen");
                }
            }

            int price = 0;
            if (index == 1 || index == 3 || index == 6 || index == 8 || index == 9) {
                price = 50;
            }

            else if (index == 11 || index == 13 || index == 14 || index == 16 || index == 18 || index == 19) {
                price = 100;
            }

            else if (index == 21 || index == 23 || index == 24 || index == 26 || index == 27 || index == 29) {
                price = 150;
            }

            else if (index == 31 || index == 32 || index == 34 || index == 37 || index == 39) {
                price = 200;
            }

            else {
                return ResponseEntity.status(404).body("Index ungültig");
            }

            if ("hotel".equals(kind)) {
                if(street.getHotels() >= 1){
                    street.setHouses(street.getHouses() + 4);
                    street.setHotels(street.getHotels() - 1);
                }
                else{
                    return ResponseEntity.status(404).body("Hier steht kein Hotel");
                }
            }

            else if ("house".equals(kind)) {
                street.setHouses(street.getHouses() - 1);
            }

            user.setMoney(user.getMoney() + price/2);

            int newPosition = index;
            int rent = 0;

            int[] rents = RENT_TABLE.get(index);
            if (street.getHotels() > 0) {
                rent = rents[6];
            } else if (street.getHouses() > 0) {
                rent = rents[street.getHouses() + 1];
            } else if (sservice.getOwnershipPercentByIndex(street.getOwner(), newPosition) == 1) {
                rent = rents[1];
            } else {
                rent = rents[0];
            }

            street.setPrice(rent);
            urepository.save(user);
            srepository.save(street);
            return ResponseEntity.status(HttpStatus.CREATED).body(kind + " verkauft");
        }
    }
}
