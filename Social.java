package social;

import java.util.*;

public class Social {

  private final PersonRepository personRepository = new PersonRepository();

  // دوستان هر شخص (کد شخص -> مجموعه کد دوستان)
  private final Map<String, Set<String>> friendships = new HashMap<>();

  // گروه‌ها و اعضای آنها (نام گروه -> مجموعه کد اعضا)
  private final Map<String, Set<String>> groups = new HashMap<>();

  // پست‌ها: شناسه پست -> Post object
  private final Map<String, Post> posts = new HashMap<>();

  // پست‌های هر کاربر (کد کاربر -> لیست شناسه پست‌ها مرتب شده بر اساس زمان نزولی)
  private final Map<String, List<String>> userPosts = new HashMap<>();

  // --- Person, Friendship, Group methods ---

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
      return;
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

  // ---- گروه‌ها ----

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

  // --- R4: Statistics ---

  public String personWithLargestNumberOfFriends() {
    String result = null;
    int maxFriends = -1;
    for (String code : personRepository.findAllCodes()) {
      int friendsCount = friendships.getOrDefault(code, Collections.emptySet()).size();
      if (friendsCount > maxFriends) {
        maxFriends = friendsCount;
        result = code;
      }
    }
    return result;
  }

  public String largestGroup() {
    String result = null;
    int maxMembers = -1;
    for (Map.Entry<String, Set<String>> entry : groups.entrySet()) {
      int size = entry.getValue().size();
      if (size > maxMembers) {
        maxMembers = size;
        result = entry.getKey();
      }
    }
    return result;
  }

  public String personInLargestNumberOfGroups() {
    // شمارش تعداد گروه‌هایی که هر فرد در آن عضو است
    Map<String, Integer> countGroups = new HashMap<>();
    for (Set<String> members : groups.values()) {
      for (String member : members) {
        countGroups.put(member, countGroups.getOrDefault(member, 0) + 1);
      }
    }
    String result = null;
    int maxCount = -1;
    for (Map.Entry<String, Integer> entry : countGroups.entrySet()) {
      if (entry.getValue() > maxCount) {
        maxCount = entry.getValue();
        result = entry.getKey();
      }
    }
    return result;
  }

  // --- R5: Posts ---

  public String post(String authorCode, String text) {
    if (!personRepository.findById(authorCode).isPresent()) {
      throw new IllegalArgumentException("Author does not exist: " + authorCode);
    }
    // تولید شناسه یکتا (ترکیب حروف و اعداد) می‌توان UUID را به کار برد
    String postId = UUID.randomUUID().toString().replace("-", "");
    long timestamp = System.currentTimeMillis();
    Post post = new Post(postId, authorCode, text, timestamp);
    posts.put(postId, post);

    // افزودن پست به لیست پست‌های کاربر به ترتیب نزولی زمان
    userPosts.computeIfAbsent(authorCode, k -> new ArrayList<>()).add(0, postId);

    return postId;
  }

  public String getPostContent(String pid) {
    Post p = posts.get(pid);
    return (p != null) ? p.getText() : null;
  }

  public long getTimestamp(String pid) {
    Post p = posts.get(pid);
    return (p != null) ? p.getTimestamp() : -1;
  }

  public List<String> getPaginatedUserPosts(String author, int pageNo, int pageLength) {
    List<String> allPosts = userPosts.getOrDefault(author, Collections.emptyList());
    return paginateList(allPosts, pageNo, pageLength);
  }

  public List<String> getPaginatedFriendPosts(String author, int pageNo, int pageLength) {
    Set<String> friends = friendships.getOrDefault(author, Collections.emptySet());
    List<Post> friendPosts = new ArrayList<>();
    for (String friendCode : friends) {
      List<String> fPosts = userPosts.getOrDefault(friendCode, Collections.emptyList());
      for (String pid : fPosts) {
        Post p = posts.get(pid);
        if (p != null) {
          friendPosts.add(p);
        }
      }
    }
    // مرتب‌سازی پست‌ها بر اساس زمان نزولی
    friendPosts.sort((p1, p2) -> Long.compare(p2.getTimestamp(), p1.getTimestamp()));

    // استخراج شناسه پست‌ها به همراه نام نویسنده به صورت "authorName:postId"
    List<String> result = new ArrayList<>();
    for (Post p : friendPosts) {
      try {
        Person author = personRepository.findById(p.getAuthorCode()).orElse(null);
        if (author != null) {
          result.add(author.getName() + ":" + p.getId());
        }
      } catch (Exception e) {
        // نادیده گرفتن خطاها
      }
    }
    return paginateList(result, pageNo, pageLength);
  }

  // --- متد کمکی برای صفحه‌بندی ---
  private <T> List<T> paginateList(List<T> list, int pageNo, int pageLength) {
    if (pageNo <= 0 || pageLength <= 0) {
      return Collections.emptyList();
    }
    int fromIndex = (pageNo - 1) * pageLength;
    if (fromIndex >= list.size()) {
      return Collections.emptyList();
    }
    int toIndex = Math.min(fromIndex + pageLength, list.size());
    return list.subList(fromIndex, toIndex);
  }

  // کلاس داخلی برای نمایش پست
  private static class Post {
    private final String id;
    private final String authorCode;
    private final String text;
   
