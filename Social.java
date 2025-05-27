package social;

import java.util.*;

public class Social {

  private final PersonRepository personRepository = new PersonRepository();
  
  // Map برای نگهداری دوستان هر شخص (کلید: کد شخص، مقدار: مجموعه کد دوستان)
  private final Map<String, Set<String>> friendships = new HashMap<>();

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
    // بررسی وجود هر دو شخص در مخزن داده‌ها
    if (!personRepository.findById(codePerson1).isPresent()) {
      throw new NoSuchCodeException("Person code not found: " + codePerson1);
    }
    if (!personRepository.findById(codePerson2).isPresent()) {
      throw new NoSuchCodeException("Person code not found: " + codePerson2);
    }

    // جلوگیری از اضافه کردن دوستی به خود شخص
    if (codePerson1.equals(codePerson2)) {
      return;  // یا می‌توانید Exception اختصاصی پرتاب کنید
    }

    // اضافه کردن دوطرفه
    friendships.computeIfAbsent(codePerson1, k -> new HashSet<>()).add(codePerson2);
    friendships.computeIfAbsent(codePerson2, k -> new HashSet<>()).add(codePerson1);
  }

  public Collection<String> listOfFriends(String codePerson) throws NoSuchCodeException {
    // بررسی وجود شخص
    if (!personRepository.findById(codePerson).isPresent()) {
      throw new NoSuchCodeException("Person code not found: " + codePerson);
    }
    // بازگرداندن لیست دوستان یا مجموعه خالی اگر دوستانی نداشته باشد
    return friendships.getOrDefault(codePerson, Collections.emptySet());
  }

  // سایر متدها بدون تغییر
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
