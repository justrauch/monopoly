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

    @PostMapping("/buystreet/{index}")
    public ResponseEntity<String> getStreet(@PathVariable Integer index, HttpSession session) {
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
            Street newstreet = new Street();
            newstreet.setOwner(user);
            newstreet.setMatch(match);
            newstreet.setPrice((int)(BOARD[index].price * 0.1));
            newstreet.setIndex(index);
            newstreet.setIsSpecial(BOARD[index].isSpecial);
            newstreet.setHotels(0);
            newstreet.setHouses(0);
            user.setMoney(user.getMoney() - BOARD[index].price);
            try {
                urepository.save(user);
                srepository.save(newstreet);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Fehler beim Speichern: " + e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Straße gekauft");
        }
    }

    @PostMapping("/buybuilding/{index}/{kind}")
    public ResponseEntity<String> getStreet(@PathVariable Integer index, @PathVariable String kind, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (!"hotelhouse".contains(kind)) {
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

        if (street.getIsSpecial()){
            return ResponseEntity.status(404).body("Hier kann kein gebäude gebaut werden");
        }
        else if (index >= BOARD.length || index < 0) {
            return ResponseEntity.status(404).body("Index ungültig");
        }
        else if (street.getOwner() != user){
            return ResponseEntity.status(404).body("Die Straße gehört dir nicht");
        }
        else if (!(sservice.getOwnershipPercentByIndex(user, index) == 1)){
            return ResponseEntity.status(404).body("Du musst zu erst alle Straßen dieser Farbe besitzen");
        }
        else {
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
            // Bahnhöfe
            if (newPosition == 5 || newPosition == 15 || newPosition == 25 || newPosition == 35) {
                double stationsOwned = sservice.getOwnershipPercentByIndex(street.getOwner(), newPosition);
                rent = (int) (25 * (stationsOwned / 100.0) * 4);
            } 
            else {
                int price_street = BOARD[newPosition].price;
                int doubleRent = Math.abs(sservice.getOwnershipPercentByIndex(street.getOwner(), newPosition) - 100.0) < 0.01 ? 2 : 1;
                int baseRent = (int) (price_street * 0.1);
                int houseRent = (int) (street.getHouses() * 0.25 * price_street);
                int hotelRent = (int) (street.getHotels() * 0.5 * price_street);
                rent = doubleRent * (baseRent + houseRent + hotelRent);
            }
            street.setPrice(rent);
            urepository.save(user);
            srepository.save(street);
            return ResponseEntity.status(HttpStatus.CREATED).body(kind + " gekauft");
        }
    }
}
