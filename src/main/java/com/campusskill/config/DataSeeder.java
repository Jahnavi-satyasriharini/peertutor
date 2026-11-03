package com.campusskill.config;

import com.campusskill.model.GroupMember;
import com.campusskill.model.RequestStatus;
import com.campusskill.model.Role;
import com.campusskill.model.Skill;
import com.campusskill.model.SkillType;
import com.campusskill.model.StudyGroup;
import com.campusskill.model.StudyMode;
import com.campusskill.model.SwapRequest;
import com.campusskill.model.User;
import com.campusskill.model.UserSkill;
import com.campusskill.repository.GroupMemberRepository;
import com.campusskill.repository.SkillRepository;
import com.campusskill.repository.StudyGroupRepository;
import com.campusskill.repository.SwapRequestRepository;
import com.campusskill.repository.UserRepository;
import com.campusskill.repository.UserSkillRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataSeeder implements ApplicationRunner {

    private static final String DEMO_PASSWORD = "Password@123";

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final SwapRequestRepository swapRequestRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean seedDemoData;
    private final String adminEmail;
    private final String adminPassword;

    public DataSeeder(UserRepository userRepository,
                      SkillRepository skillRepository,
                      UserSkillRepository userSkillRepository,
                      StudyGroupRepository studyGroupRepository,
                      GroupMemberRepository groupMemberRepository,
                      SwapRequestRepository swapRequestRepository,
                      PasswordEncoder passwordEncoder,
                      @Value("${app.seed-demo-data}") boolean seedDemoData,
                      @Value("${app.admin.email}") String adminEmail,
                      @Value("${app.admin.password}") String adminPassword) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.userSkillRepository = userSkillRepository;
        this.studyGroupRepository = studyGroupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.swapRequestRepository = swapRequestRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedDemoData = seedDemoData;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (skillRepository.count() == 0) {
            seedSkills();
        }
        if (seedDemoData && userRepository.count() == 0) {
            seedDemoData();
        }
        createAdminIfConfigured();
    }

    private void createAdminIfConfigured() {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            return;
        }
        String email = adminEmail.trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            return;
        }
        userRepository.save(new User("Campus Admin", email, passwordEncoder.encode(adminPassword),
                "Administration", 4, "Keeps CampusSkill running smoothly.", Role.ADMIN));
    }

    private void seedDemoData() {
        Map<String, Skill> skills = new HashMap<>();
        skillRepository.findAll().forEach(skill -> skills.put(skill.getName(), skill));
        Map<String, User> users = seedStudents();
        seedSkillLinks(users, skills);
        seedGroups(users);
        seedSwapRequests(users, skills);
    }

    private void seedSkills() {
        skillRepository.save(new Skill("Java Programming", "Programming"));
        skillRepository.save(new Skill("Python Basics", "Programming"));
        skillRepository.save(new Skill("Web Development", "Programming"));
        skillRepository.save(new Skill("Data Structures", "Computer Science"));
        skillRepository.save(new Skill("Machine Learning", "AI & Data"));
        skillRepository.save(new Skill("Statistics", "Mathematics"));
        skillRepository.save(new Skill("Linear Algebra", "Mathematics"));
        skillRepository.save(new Skill("UI/UX Design", "Design"));
        skillRepository.save(new Skill("Photography", "Creative Arts"));
        skillRepository.save(new Skill("Public Speaking", "Soft Skills"));
        skillRepository.save(new Skill("Technical Writing", "Soft Skills"));
        skillRepository.save(new Skill("Digital Electronics", "Electronics"));
    }

    private Map<String, User> seedStudents() {
        String password = passwordEncoder.encode(DEMO_PASSWORD);
        Map<String, User> users = new HashMap<>();
        saveStudent(users, password, "Aarav", "Aarav Mehta", "aarav.mehta@srmap.edu.in", "B.Tech CSE", 3,
                "Competitive programmer who loves building small games in Java.");
        saveStudent(users, password, "Priya", "Priya Nair", "priya.nair@srmap.edu.in", "B.Tech CSE (AI & ML)", 2,
                "Exploring neural networks one notebook at a time.");
        saveStudent(users, password, "Rohan", "Rohan Verma", "rohan.verma@srmap.edu.in", "B.Tech ECE", 3,
                "Circuits by day, Python scripts by night.");
        saveStudent(users, password, "Sneha", "Sneha Iyer", "sneha.iyer@srmap.edu.in", "B.Tech IT", 2,
                "Frontend developer who designs before she codes.");
        saveStudent(users, password, "Arjun", "Arjun Reddy", "arjun.reddy@srmap.edu.in", "B.Tech Mechanical", 4,
                "Final-year project lead and confident presenter.");
        saveStudent(users, password, "Meera", "Meera Kapoor", "meera.kapoor@srmap.edu.in", "B.Tech CSE", 1,
                "New to campus and eager to learn. Weekend photographer.");
        saveStudent(users, password, "Kabir", "Kabir Singh", "kabir.singh@srmap.edu.in", "B.Tech CSE (AI & ML)", 4,
                "Believes linear algebra is the secret key to AI.");
        saveStudent(users, password, "Ananya", "Ananya Das", "ananya.das@srmap.edu.in", "B.Tech IT", 3,
                "Writes clear documentation and loves a well-built spreadsheet.");
        return users;
    }

    private void saveStudent(Map<String, User> users, String password, String key, String name, String email,
                             String branch, int year, String bio) {
        users.put(key, userRepository.save(new User(name, email, password, branch, year, bio, Role.STUDENT)));
    }

    private void seedSkillLinks(Map<String, User> users, Map<String, Skill> skills) {
        link(users.get("Aarav"), SkillType.TEACH, skills, "Java Programming", "Data Structures");
        link(users.get("Aarav"), SkillType.LEARN, skills, "Machine Learning", "Web Development");

        link(users.get("Priya"), SkillType.TEACH, skills, "Machine Learning", "Python Basics");
        link(users.get("Priya"), SkillType.LEARN, skills, "Linear Algebra", "Public Speaking");

        link(users.get("Rohan"), SkillType.TEACH, skills, "Digital Electronics", "Statistics");
        link(users.get("Rohan"), SkillType.LEARN, skills, "Python Basics");

        link(users.get("Sneha"), SkillType.TEACH, skills, "Web Development", "UI/UX Design");
        link(users.get("Sneha"), SkillType.LEARN, skills, "Java Programming");

        link(users.get("Arjun"), SkillType.TEACH, skills, "Public Speaking", "Technical Writing");
        link(users.get("Arjun"), SkillType.LEARN, skills, "Statistics");

        link(users.get("Meera"), SkillType.TEACH, skills, "Photography", "Python Basics");
        link(users.get("Meera"), SkillType.LEARN, skills, "Data Structures");

        link(users.get("Kabir"), SkillType.TEACH, skills, "Linear Algebra", "Statistics", "Machine Learning");
        link(users.get("Kabir"), SkillType.LEARN, skills, "Technical Writing");

        link(users.get("Ananya"), SkillType.TEACH, skills, "Technical Writing", "Data Structures");
        link(users.get("Ananya"), SkillType.LEARN, skills, "UI/UX Design", "Photography");
    }

    private void link(User user, SkillType type, Map<String, Skill> skills, String... skillNames) {
        for (String skillName : skillNames) {
            userSkillRepository.save(new UserSkill(user, skills.get(skillName), type));
        }
    }

    private void seedGroups(Map<String, User> users) {
        createGroup(users.get("Aarav"), "DSA Crunch Circle", "Data Structures",
                "Weekly problem solving sessions on arrays, trees and graphs.", 6, StudyMode.ONLINE,
                List.of(users.get("Meera"), users.get("Ananya")));
        createGroup(users.get("Priya"), "ML Study Lab", "Machine Learning",
                "Hands-on notebooks covering regression to neural networks.", 5, StudyMode.OFFLINE,
                List.of(users.get("Kabir"), users.get("Aarav")));
        createGroup(users.get("Sneha"), "Web Builders Club", "Web Development",
                "Build a real website together every fortnight.", 8, StudyMode.ONLINE,
                List.of(users.get("Aarav"), users.get("Ananya"), users.get("Meera")));
        createGroup(users.get("Kabir"), "Linear Algebra Sprint", "Linear Algebra",
                "Intensive revision in the library before the end-semester exam.", 4, StudyMode.OFFLINE,
                List.of(users.get("Priya"), users.get("Rohan"), users.get("Arjun")));
    }

    private void createGroup(User creator, String name, String subject, String description,
                             int maxMembers, StudyMode mode, List<User> members) {
        StudyGroup group = studyGroupRepository.save(
                new StudyGroup(name, subject, description, maxMembers, mode, creator));
        groupMemberRepository.save(new GroupMember(group, creator));
        for (User member : members) {
            groupMemberRepository.save(new GroupMember(group, member));
        }
    }

    private void seedSwapRequests(Map<String, User> users, Map<String, Skill> skills) {
        createRequest(users.get("Meera"), users.get("Aarav"), skills.get("Data Structures"),
                RequestStatus.COMPLETED, 20, 5);
        createRequest(users.get("Rohan"), users.get("Priya"), skills.get("Python Basics"),
                RequestStatus.ACCEPTED, 3, null);
        createRequest(users.get("Sneha"), users.get("Aarav"), skills.get("Java Programming"),
                RequestStatus.PENDING, 1, null);
        createRequest(users.get("Aarav"), users.get("Priya"), skills.get("Machine Learning"),
                RequestStatus.PENDING, 2, null);
        createRequest(users.get("Ananya"), users.get("Sneha"), skills.get("UI/UX Design"),
                RequestStatus.COMPLETED, 9, null);
        createRequest(users.get("Arjun"), users.get("Kabir"), skills.get("Statistics"),
                RequestStatus.REJECTED, 6, null);
        createRequest(users.get("Priya"), users.get("Kabir"), skills.get("Linear Algebra"),
                RequestStatus.COMPLETED, 12, 4);
        createRequest(users.get("Kabir"), users.get("Ananya"), skills.get("Technical Writing"),
                RequestStatus.ACCEPTED, 4, null);
        createRequest(users.get("Ananya"), users.get("Meera"), skills.get("Photography"),
                RequestStatus.PENDING, 1, null);
    }

    private void createRequest(User requester, User receiver, Skill skill, RequestStatus status,
                               int daysAgo, Integer rating) {
        SwapRequest request = new SwapRequest(requester, receiver, skill, status, LocalDate.now().minusDays(daysAgo));
        request.setRating(rating);
        swapRequestRepository.save(request);
    }
}
