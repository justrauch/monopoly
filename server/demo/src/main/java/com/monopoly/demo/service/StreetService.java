package com.monopoly.demo.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import com.monopoly.demo.repository.StreetRepository;
import com.monopoly.demo.model.Street;
import com.monopoly.demo.model.User;

@Service
public class StreetService {

    private final StreetRepository srepository;
    private final Map<String, List<Integer>> colorIndicesMap = Map.of(
        "brown", Arrays.asList(1, 3),
        "lightblue", Arrays.asList(6, 8, 9),
        "pink", Arrays.asList(11, 13, 14),
        "orange", Arrays.asList(16, 18, 19),
        "red", Arrays.asList(21, 23, 24),
        "yellow", Arrays.asList(26, 27, 29),
        "green", Arrays.asList(31, 32, 34),
        "blue", Arrays.asList(37, 39),
        "station", Arrays.asList(5, 15, 25, 35),
        "utility", Arrays.asList(12, 28)
    );

    public StreetService(StreetRepository srepository) {
        this.srepository = srepository;
    }

    /**
     * Berechnet den Prozentsatz (0-100) eines Sets, das ein Spieler besitzt.
     * @param owner Der Spieler
     * @param index Ein beliebiger Index aus dem Set
     * @return Prozentsatz des Sets, das der Spieler besitzt
     */
    public double getOwnershipPercentByIndex(User owner, int index) {
        // Finde zu welchem Set/Color der Index gehört
        String setKey = colorIndicesMap.entrySet().stream()
                .filter(entry -> entry.getValue().contains(index))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (setKey == null) {
            return 0.0; // kein Set für diesen Index
        }

        List<Integer> setIndices = colorIndicesMap.get(setKey);
        List<Street> ownedStreets = srepository.findByOwnerId(owner.getId());

        long ownedCount = ownedStreets.stream()
                .map(Street::getIndex)
                .filter(setIndices::contains)
                .count();

        return (ownedCount * 100.0) / setIndices.size();
    }

    
    /**
     * Gibt alle dazu gehörigen Straßen für einen Index zurück.
     * @param index Ein beliebiger Index aus dem Set
     * @return Array an allen dazu gehörigen Straßen zurück
     */
    public List<Street> getallstreetsofanindex(int index) {
        // Finde zu welchem Set/Color der Index gehört
        String setKey = colorIndicesMap.entrySet().stream()
                .filter(entry -> entry.getValue().contains(index))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (setKey == null) {
            return List.of();
        }

        List<Integer> setIndices = colorIndicesMap.get(setKey);

        List<Street> ownedCount = srepository.findAll().stream()
        .filter(s -> setIndices.contains(s.getIndex()))
        .toList();

        return ownedCount;
    }
}
