package social;

import java.util.*;

public class Social {

  private final PersonRepository personRepository = new PersonRepository();
  
  public void addPerson(String code, String name, String surname) throws PersonExistsException {
    if (personRepository.findById(code).isPresent()){
        throw new PersonExistsException();
    }
    Person person = new Person(code, name, surname);
    personRepository.save(person);
  }

  public String getPerson(String code) throws NoSuchCodeException {
    return personRepository.findById(code)
        .map(p -> p.getCode() + " " + p.getName() + " " + p.getSurname())
        .orElseThrow(NoSuchCodeException::new);
  }

  public void addFriendship(String codePerson1, String codePerson2) throws NoSuchCodeException {
    // TO BE IMPLEMENTED
  }

  public Collection<String> listOfFriends(String codePerson) throws NoSuchCodeException {
    return null; // TO BE IMPLEMENTED
  }

  public void addGroup(String groupName) throws GroupExistsException {
    // TO BE IMPLEMENTED
  }

  public void deleteGroup(String groupName) throws NoSuchCodeException {
    // TO BE IMPLEMENTED
  }

  public void updateGroupName(String groupName, String newName) throws NoSuchCodeException, GroupExistsException {
    // TO BE IMPLEMENTED
  }

  public Collection<String> listOfGroups() {
    return null; // TO BE IMPLEMENTED
  }

  public void addPersonToGroup(String codePerson, String groupName) throws NoSuchCodeException {
    // TO BE IMPLEMENTED
  }

  public Collection<String> listOfPeopleInGroup(String groupName) {
    return null; // TO BE IMPLEMENTED
  }

  public String personWithLargestNumberOfFriends() {
    return null; // TO BE IMPLEMENTED
  }

  public String largestGroup() {
    return null; // TO BE IMPLEMENTED
  }

  public String personInLargestNumberOfGroups() {
    return null; // TO BE IMPLEMENTED
  }

  public String post(String authorCode, String text) {
    return null; // TO BE IMPLEMENTED
  }

  public String getPostContent(String pid) {
    return null; // TO BE IMPLEMENTED
  }

  public long getTimestamp(String pid) {
    return -1; // TO BE IMPLEMENTED
  }

  public List<String> getPaginatedUserPosts(String author, int pageNo, int pageLength) {
    return null; // TO BE IMPLEMENTED
  }

  public List<String> getPaginatedFriendPosts(String author, int pageNo, int pageLength) {
    return null; // TO BE IMPLEMENTED
  }

}
