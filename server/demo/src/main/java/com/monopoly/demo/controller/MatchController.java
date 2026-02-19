package com.monopoly.demo.controller;

import com.monopoly.demo.controller.MatchController.StreetTemplate;
import com.monopoly.demo.model.Match;
import com.monopoly.demo.model.Street;
import com.monopoly.demo.model.User;
import com.monopoly.demo.service.StreetService;
import com.monopoly.demo.repository.MatchRepository;
import com.monopoly.demo.repository.UserRepository;
import com.monopoly.demo.repository.StreetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import java.util.Random;
import java.util.Map;

@RestController
@RequestMapping("/matches")
@CrossOrigin(origins = "http://localhost:5173")
public class MatchController {

    private final MatchRepository repository;
    private final UserRepository urepository;
    private final StreetRepository srepository;
    private final StreetService sservice;
    private int last = 0;
    private boolean pasch = false;

    public MatchController(StreetService sservice, MatchRepository repository, UserRepository urepository, StreetRepository srepository) {
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

    public static class Card {

        private String text;
        private Integer money;
        private Integer moveTo;
        private String action;
        private boolean fromEachPlayer;
        private boolean toEachPlayer;

        public Card(String text, Integer money, Integer moveTo, String action,
                    boolean fromEachPlayer, boolean toEachPlayer) {
            this.text = text;
            this.money = money;
            this.moveTo = moveTo;
            this.action = action;
            this.fromEachPlayer = fromEachPlayer;
            this.toEachPlayer = toEachPlayer;
        }

        public String getText() { return text; }
        public Integer getMoney() { return money; }
        public Integer getMoveTo() { return moveTo; }
        public String getAction() { return action; }
        public boolean isFromEachPlayer() { return fromEachPlayer; }
        public boolean isToEachPlayer() { return toEachPlayer; }
    }

    public static final List<Card> COMMUNITY_CHEST = List.of(
        new Card("Bankfehler zu deinen Gunsten", +200, null, null, false, false),
        new Card("Arztrechnung bezahlen", -50, null, null, false, false),
        new Card("Du kommst aus dem Gefängnis frei", null, null, "jail_free", false, false),
        new Card("Du erbst", +100, null, null, false, false),
        new Card("Krankenhausgebühren", -100, null, null, false, false),
        new Card("Steuerrückerstattung", +20, null, null, false, false),
        new Card("Geburtstag – jeder zahlt dir", +10, null, null, true, false),
        new Card("Lebensversicherung wird fällig", +100, null, null, false, false),
        new Card("Arztkosten", -50, null, null, false, false),
        new Card("Aktiengewinn", +50, null, null, false, false),
        new Card("Straßenreparaturen", null, null, "repairs", false, false),
        new Card("Schönheitswettbewerb gewonnen", +10, null, null, false, false),
        new Card("Erbe", +100, null, null, false, false),
        new Card("Gehe ins Gefängnis", null, 10, null, false, false),
        new Card("Du erhältst Beratungsgebühren", +25, null, null, false, false)
    );

    public static final List<Card> CHANCE_CARDS = List.of(
        new Card("Gehe auf Los", null, 0, null, false, false),
        new Card("Gehe ins Gefängnis", null, 10, null, false, false),
        new Card("Gehe zur Turmstraße", null, 39, null, false, false),
        new Card("Gehe zur Schlossallee", null, 37, null, false, false),
        new Card("Gehe zum nächsten Bahnhof", null, null, "next_station", false, false),
        new Card("Gehe zum nächsten Bahnhof", null, null, "next_station", false, false),
        new Card("Gehe zum nächsten Werk", null, null, "next_utility", false, false),
        new Card("Bank zahlt Dividende", +50, null, null, false, false),
        new Card("Strafzettel", -15, null, null, false, false),
        new Card("Mache eine Reise", null, 5, null, false, false),
        new Card("Rücke drei Felder zurück", null, null, "back_3", false, false),
        new Card("Haus- und Hotelreparaturen", null, null, "repairs", false, false),
        new Card("Zum Vorstand gewählt", -50, null, null, false, true),
        new Card("Darlehen wird fällig", +150, null, null, false, false),
        new Card("Du kommst aus dem Gefängnis frei", null, null, "jail_free", false, false),
        new Card("Bußgeld", -20, null, null, false, false)
    );

    // --- MATCH SUCHEN / BEITRETEN ---
    @PostMapping("/searchMatch")
    public ResponseEntity<?> searchMatch(HttpSession session) {
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

        if (!matches.isEmpty()) {
            return ResponseEntity.ok("Already in Match!");
        }

        List<Match> listmatches = repository.findByIsActive(0);

        if (listmatches.isEmpty()) {
            Match newmatch = new Match();
            newmatch.setCreater(user);
            newmatch.setIsActive(0);
            user.setTurn_number(1);
            user.setMoney(1500);
            urepository.save(user);
            repository.save(newmatch);

            return ResponseEntity.ok("Match created");
        }

        Match firstMatch = listmatches.get(0);
        String message = "Match not full";

        if (firstMatch.getSecondplayer() == null) {
            firstMatch.setSecondplayer(user);
            user.setTurn_number(2);
        } else if (firstMatch.getThirdplayer() == null) {
            firstMatch.setThirdplayer(user);
            user.setTurn_number(3);
        } else if (firstMatch.getFourthplayer() == null) {
            firstMatch.setFourthplayer(user);
            firstMatch.setIsActive(1);
            user.setTurn_number(4);
            message = "Match full";
        }
        
        user.setMoney(1500);
        urepository.save(user);
        repository.save(firstMatch);
        return ResponseEntity.ok(message);
    }

    public record GameStateResponse(
        Match match,
        List<Street> streets
    ) {}

    // --- GAMESTATE ABRUFEN ---
    @GetMapping("/getGamestate")
    public ResponseEntity<?> getGamestate(HttpSession session) {
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
            return ResponseEntity.ok("In Search");
        }

        Match match = matches.get(0);
        List<Street> streets = srepository.findByMatchId(match.getId());

        GameStateResponse response = new GameStateResponse(match, streets);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/makeMove")
    public ResponseEntity<?> makeMove(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        Optional<User> userOpt = urepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();

        if (user.getMoney() <= 0) {
            return ResponseEntity.status(404).body("Game Over!");
        }

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

        
        if (match.getIsActive() < 0){
            return ResponseEntity.status(404).body("Du hast schon gewürfelt");
        }

        if (user.getTurn_number() != match.getIsActive()) {
            return ResponseEntity.status(404).body("Not ur Turn!");
        }

        Integer isActive = match.getIsActive();
        if (isActive == null) isActive = 0;

        int randomInt = 1;
        //new Random().nextInt(6) + 1;
        int randomInt2 = 0;
        //new Random().nextInt(6) + 1;

        if (randomInt != randomInt2){
            pasch = false;
        }

        else if (randomInt == randomInt2 && last != user.getTurn_number()) {
            pasch = true;
        }

        //Gefängnis später
        else if (randomInt == randomInt2 && last == user.getTurn_number()) {
            pasch = false;
        }

        match.setIsActive(isActive * - 1);

        int newPosition = (user.getPosition() + randomInt + randomInt2) % 40;
        
        if (newPosition < user.getPosition()){
            user.setMoney(user.getMoney() + 200);
        }

        user.setPosition(newPosition);

        Street street = srepository
        .findByMatchIdAndStreetIndex(match.getId(), newPosition)
        .orElse(null);

        Card retcard = new Card("", 0, 0, null, false, false);;

        if (street == null && !BOARD[newPosition].canBeBought){
            if (BOARD[newPosition].price != null)
            {
                user.setMoney(user.getMoney() + BOARD[newPosition].price);
            }
            else {
                Card card = new Card("null", 0, 0, null, false, false);
                if (newPosition == 2 || newPosition == 17 || newPosition == 33) {
                    int card_number = new Random().nextInt(COMMUNITY_CHEST.size());
                    card = COMMUNITY_CHEST.get(card_number);
                } else if (newPosition == 7 || newPosition == 22) {
                    int card_number = new Random().nextInt(CHANCE_CARDS.size());
                    card = CHANCE_CARDS.get(card_number);
                }
                if (!card.text.equals("null")){

                    retcard = card;

                    if (card.getMoney() != null) {
                        user.setMoney(user.getMoney() + card.getMoney());
                    }

                    if (card.getMoveTo() != null) {
                        user.setPosition(card.getMoveTo());
                        street = srepository.findByMatchIdAndStreetIndex(match.getId(), card.moveTo).orElse(null);
                    }

                    if (card.action != null)
                    {
                        if ("back_3".equals(card.getAction())) {
                            int newPos = user.getPosition() - 3;
                            if (newPos < 0) newPos += 40;
                            user.setPosition(newPos);
                            street = srepository.findByMatchIdAndStreetIndex(match.getId(), newPos).orElse(null);
                        }

                        if ("next_station".equals(card.getAction())) {
                            int newPos = newPosition < 10 ? 5 : newPosition < 20 ? 15 : newPosition < 30 ? 25 : 35;
                            user.setPosition(newPos);
                            street = srepository.findByMatchIdAndStreetIndex(match.getId(), newPos).orElse(null);
                        }

                        if ("next_utility".equals(card.getAction())) {
                            int newPos = newPosition < 20 ? 12 : 28;
                            user.setPosition(newPos);
                            street = srepository.findByMatchIdAndStreetIndex(match.getId(), newPos).orElse(null);
                        }

                        if ("jail_free".equals(card.getAction())) {
                            // später
                        }

                        if ("go_to_jail".equals(card.getAction())) {
                            user.setPosition(10);
                            // später player.setInJail(true);
                        }

                        if ("repairs".equals(card.getAction())) {
                            int cost = 0;
                            for (Street s : srepository.findByOwnerId(user.getId())) {
                                cost += s.getHouses() * 25;
                                if (s.getHotels() > 0) cost += 100;
                            }
                            user.setMoney(user.getMoney() - cost);
                        }
                    }

                    if (card.fromEachPlayer) {
                        int total = 0;
                        for (User other : urepository.findAll()) {
                            if (other != user) {
                                other.setMoney(other.getMoney() - card.getMoney());
                                total += card.getMoney();
                            }
                        }
                        user.setMoney(user.getMoney() + total);
                    }

                    if (card.toEachPlayer) {
                        int total = 0;
                        for (User other : urepository.findAll()) {
                            if (other != user) {
                                other.setMoney(other.getMoney() + card.getMoney());
                                total += card.getMoney();
                            }
                        }
                        user.setMoney(user.getMoney() - total);
                    }
                }
            }
        }
        if (street != null && street.getOwner() != user) {
            int rent = street.getPrice();

            if (newPosition == 12 || newPosition == 28) {
                double utilitiesOwned = sservice.getOwnershipPercentByIndex(street.getOwner(), newPosition);
                int diceSum = randomInt + randomInt2;
                rent = diceSum * (utilitiesOwned == 50.0 ? 4 : 10);
            }

            // Geld abziehen / dem Besitzer geben
            user.setMoney(user.getMoney() - rent);
            street.getOwner().setMoney(street.getOwner().getMoney() + rent);
            urepository.save(street.getOwner());
        }

        last = user.getTurn_number();

        urepository.save(user);
        repository.save(match);

        Map<String, String> response = new HashMap<>();
        response.put("dice1", String.valueOf(randomInt));
        response.put("dice2", String.valueOf(randomInt2));
        response.put("cardtext", retcard.text != "" ? (retcard.text + 
            (retcard.money != null ? (retcard.money < 0 ? " Bezahle " : " Bekomme ") + retcard.money : "") 
            + (retcard.fromEachPlayer ? " an jeden" : "") 
            + (retcard.isFromEachPlayer() ? " von jedem" : "")
            + (retcard.moveTo != null ? ((" Gehe zum: " + retcard.moveTo) + ". Feld ") : "")
            + ((retcard.action != null ? " du musst " +
            (retcard.action.equals("jail_free") ? "Gefängnisfreikarte" : 
            retcard.action.equals("repairs") ? " alle deine Häuser reparieren" :
            retcard.action.equals("back_3") ? "drei Felder zurück" :
            retcard.action.equals("next_utility") ? "zum nächstes Werk" :
            retcard.action.equals("next_station") ? "zum nächster Bahnhof" : "") : ""))) : ""
        );

        return ResponseEntity.ok(response);
    }

        // --- GAMESTATE ABRUFEN ---
    @GetMapping("/endTurn")
    public ResponseEntity<?> endTurn(HttpSession session) {
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
            return ResponseEntity.ok("In Search");
        }

        Match match = matches.get(0);

        if (!pasch){
            int newisActive = (user.getTurn_number() % 4) + 1;
            User tmpuser = urepository.findByTurnNumber(newisActive);

            while (tmpuser == null) {
                newisActive = (newisActive % 4) + 1;
                tmpuser = urepository.findByTurnNumber(newisActive);
            }
            match.setIsActive(newisActive);
        }
        else {
            match.setIsActive(user.getTurn_number());
        }

        repository.save(match);

        return ResponseEntity.ok("Dein Zug ist vorbei");
    }

}
