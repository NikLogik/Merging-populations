package ru.nachos;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.population.io.PopulationReader;

import java.util.*;

public class PopulationContainer {

    Logger log = Logger.getLogger(PopulationContainer.class);

    private static Scenario scenario_origin = null;

    private static Scenario scenario_merge;

    private String firstPlan;

    private String secondPlan;

    public PopulationContainer(Scenario origin, Scenario merge, String firstPlan, String secondPlan) {
        scenario_origin = origin;
        scenario_merge = merge;
        this.firstPlan = firstPlan;
        this.secondPlan = secondPlan;
    }

    public void readPopulations(){
        log.info("-------------Start reading first plan from " + firstPlan + "-------------");
        new PopulationReader(scenario_origin).readFile(firstPlan);
        Population origin_population = scenario_origin.getPopulation();

        log.info("-------------Start reading second plan from " + secondPlan + "-------------");
        new PopulationReader(scenario_merge).readFile(secondPlan);
        Population merge_population = scenario_merge.getPopulation();

        log.info("Origin population file contains: " + origin_population.getPersons().size() + " persons");
        log.info("Merge population file contains: " + merge_population.getPersons().size() + " persons");
    }

    private static List<Person> merge_persons = new ArrayList<>();
    private static List<Person> remove_persons = new ArrayList<>();

    public void mergePopulations() {
        Map<Id<Person>, ? extends Person> persons = scenario_origin.getPopulation().getPersons();
        persons.values().forEach(PersonsCashe::addPerson);

        int candidate_persons = 0;

        Population merge_population = scenario_merge.getPopulation();

        for (Map.Entry<Id<Person>, ? extends Person> person : merge_population.getPersons().entrySet()){
            if (PersonsCashe.isExist(person.getValue())){
                createMergePerson(person);
                candidate_persons++;
            }
        }

        log.info(candidate_persons + " unique persons was created");

        int removed = 0;
        for (int i = 0; i < remove_persons.size(); i++){
            merge_population.removePerson(remove_persons.get(i).getId());
            removed++;
        }
        log.info(removed + " persons was removed");

        int merged = 0;
        for (int i = 0; i< merge_persons.size(); i++){
            merge_population.addPerson(merge_persons.get(i));
            merged++;
        }
        log.info(merged + " persons was merged");

        merge_population.getPersons().values().stream().forEach(scenario_origin.getPopulation()::addPerson);

        log.info("Origin population contains " + scenario_origin.getPopulation().getPersons().size() + " after merging");
    }

    private static void createMergePerson(Map.Entry<Id<Person>,? extends Person> person) {
        final String postfix = "_merge";
        Id<Person> new_id = Id.createPersonId(person.getKey().toString() + postfix);
        Person new_person = scenario_merge.getPopulation().getFactory().createPerson(new_id);
        new_person.addPlan(person.getValue().getSelectedPlan());
        merge_persons.add(new_person);
        remove_persons.add(person.getValue());
    }

    public void writeNewPopulation(String output) {
        new PopulationWriter(scenario_origin.getPopulation()).write(output);
    }
}
