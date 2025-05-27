package social;

import java.util.*;

public class Social {

  private final PersonRepository personRepository = new PersonRepository();

  // نگهداری دوستان هر شخص (کلید: کد شخص، مقدار: مجموعه کد دوستان)
  private final Map<String, Set<String>> friendships = new HashMap<>();

  // نگهداری گروه‌ها و اعضای هر گروه (کلید: نام گروه، مقدار: مجموعه کدهای اعضا)
  private final Map<String, Set<String>> groups = new HashMap<>();

  public void addPerson(String code, String name, String surname) throws PersonExistsException {
    if (personRepository.findById(code).isPresent()) {
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
    if (!personRepository.findById(codePerson1).isPresent()) {
      throw new NoSuchCodeException("Person code not found: " + codePerson1);
    }
    if (!personRepository.findById(codePerson2).isPresent()) {
      throw new NoSuchCodeException("Person code not found: " + codePerson2);
    }

    if (codePerson1.equals(codePerson2)) {
      return;  // یا می‌توانید Exception اختصاصی پرتاب کنید
    }

    friendships.computeIfAbsent(codePerson1, k -> new HashSet<>()).add(codePerson2);
    friendships.computeIfAbsent(codePerson2, k -> new HashSet<>()).add(codePerson1);
  }

  public Collection<String> listOfFriends(String codePerson) throws NoSuchCodeException {
    if (!personRepository.findById(codePerson).isPresent()) {
      throw new NoSuchCodeException("Person code not found: " + codePerson);
    }
    return friendships.getOrDefault(codePerson, Collections.emptySet());
  }

  // ---- بخش مدیریت گروه‌ها ----

  public void addGroup(String groupName) throws GroupExistsException {
    if (groupName == null || groupName.trim().isEmpty() || groupName.contains(" ")) {
      throw new IllegalArgumentException("Group name must be a single word");
    }
    if (groups.containsKey(groupName)) {
      throw new GroupExistsException("Group already exists: " + groupName);
    }
    groups.put(groupName, new HashSet<>());
  }

  public void deleteGroup(String groupName) throws NoSuchCodeException {
    if (!groups.containsKey(groupName)) {
      throw new NoSuchCodeException("Group not found: " + groupName);
    }
    groups.remove(groupName);
  }

  public void updateGroupName(String groupName, String newName) throws NoSuchCodeException, GroupExistsException {
    if (!groups.containsKey(groupName)) {
      throw new NoSuchCodeException("Group not found: " + groupName);
    }
    if (newName == null || newName.trim().isEmpty() || newName.contains(" ")) {
      throw new IllegalArgumentException("Group name must be a single word");
    }
    if (groups.containsKey(newName)) {
      throw new GroupExistsException("New group name already exists: " + newName);
    }
    Set<String> members = groups.remove(groupName);
    groups.put(newName, members);
  }

  public Collection<String> listOfGroups() {
    return new ArrayList<>(groups.keySet());
  }

  public void addPersonToGroup(String codePerson, String groupName) throws NoSuchCodeException {
    if (!personRepository.findById(codePerson).isPresent()) {
      throw new NoSuchCodeException("Person code not found: " + codePerson);
    }
    if (!groups.containsKey(groupName)) {
      throw new NoSuchCodeException("Group not found: " + groupName);
    }
    groups.get(groupName).add(codePerson);
  }

  public Collection<String> listOfPeopleInGroup(String groupName) {
    return groups.getOrDefault(groupName, null);
  }

  // --- سایر متدها ---

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
