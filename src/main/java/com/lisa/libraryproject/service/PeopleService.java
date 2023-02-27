package com.lisa.libraryproject.service;

import com.lisa.libraryproject.LibraryProjectApplication;
import com.lisa.libraryproject.model.Book;
import com.lisa.libraryproject.model.Person;
import com.lisa.libraryproject.repository.PeopleRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final Integer TEN_DAYS = 864000000;
    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Person findOneById(int id) {
        return peopleRepository.findById(id).orElse(null);
    }

    @Transactional
    public void update(int id, Person updatePerson) {
        updatePerson.setId(id);
        peopleRepository.save(updatePerson);
    }

    @Transactional
    public void save(Person person) {
        peopleRepository.save(person);
    }

    @Transactional
    public void delete(int id) {
        peopleRepository.deleteById(id);
    }


    public Optional<Person> getPersonByFullName(String fullName) {
        return peopleRepository.findByFullName(fullName);
    }

    public List<Book> getBookByPersonId(int id) {
        Optional<Person> person = peopleRepository.findById(id);

        if (person.isPresent()) {
            Hibernate.initialize(person.get().getBooks());
            person.get().getBooks().forEach(book ->
            {
                long diffInMillies = Math.abs(book.getTakenAt().getTime() - new Date().getTime());
                if (diffInMillies > TEN_DAYS)
                    book.setExpired(true);
            });
            return person.get().getBooks();
        } else {
            return Collections.emptyList();
        }

    }


}
