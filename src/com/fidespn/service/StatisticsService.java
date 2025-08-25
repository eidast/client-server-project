package com.fidespn.service;

import com.fidespn.model.Match;
import com.fidespn.model.MatchEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Aggregates basic statistics from match events.
 */
public class StatisticsService {

    public static class MatchStats {
        public int totalEvents;
        public int goals;
        public int yellowCards;
        public int redCards;
        public int substitutions;
        public Map<String, Integer> typeCounts = new HashMap<>();
    }

    /**
     * Compute simple statistics for a match based on its events.
     * @param match Match instance
     * @return MatchStats aggregated
     */
    public MatchStats computeMatchStats(Match match) {
        MatchStats stats = new MatchStats();
        List<MatchEvent> events = match.getEvents();
        stats.totalEvents = events.size();
        for (MatchEvent e : events) {
            String type = e.getType() == null ? "" : e.getType().trim().toLowerCase();
            stats.typeCounts.put(type, stats.typeCounts.getOrDefault(type, 0) + 1);
            switch (type) {
                case "gol":
                case "goal":
                    stats.goals++;
                    break;
                case "tarjeta amarilla":
                case "yellow card":
                    stats.yellowCards++;
                    break;
                case "tarjeta roja":
                case "red card":
                    stats.redCards++;
                    break;
                case "sustitución":
                case "substitution":
                    stats.substitutions++;
                    break;
                default:
                    break;
            }
        }
        return stats;
    }
}


