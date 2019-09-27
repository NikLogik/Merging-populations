package ru.nachos;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;

import java.util.LinkedList;
import java.util.List;

public class PersonsCashe {

    public static List<Id<Person>> persons = new LinkedList<>();

    public List<Id<Person>> getPersons() {
        return persons;
    }

    public static boolean addPerson(Person person){
        return persons.add(person.getId());
    }

    public static boolean isExist(Person person){
        return persons.contains(person.getId());
    }
}
