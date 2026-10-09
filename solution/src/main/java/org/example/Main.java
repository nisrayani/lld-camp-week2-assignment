package org.example;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
class User {
    int id;
    String name;
    String email;

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public boolean isLoggedIn(String password) {
        return false;
    }
}

@Getter
@Setter
class Instrcutor extends User {

    List<Course> courses;

    public Instrcutor(int id, String name, String email) {
        super(id, name, email);
    }
}

@Getter
@Setter
class Student extends User {

    List<Enrollment> enrollments = new ArrayList<>();

    public Student(int id, String name, String email) {
        super(id, name, email);
    }

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
    }
}

@Getter
@Setter
class Enrollment {
    int id;
    Date enrollmentDate;
    EnrollmentStatus status;
    float progressPercent;

    Student student;
    Course course;
    List<Lesson> completedLessons;

    public Enrollment(int id, Date enrollmentDate, Student student, Course course) {
        this.id = id;
        this.enrollmentDate = enrollmentDate;
        this.status = EnrollmentStatus.ENROLLED;
        this.progressPercent = 0;
        this.student = student;
        this.course = course;
        this.completedLessons = new ArrayList<>();
    }

    public void markLessonCompleted(Lesson lesson) {
        if (lesson == null) {
            throw new IllegalArgumentException("Lesson cannot be null");
        }

        boolean belongsToCourse = course.getModules().stream()
                .anyMatch(module -> module.getLessons().contains(lesson));

        if (!belongsToCourse) {
            throw new IllegalArgumentException(
                    "Lesson does not belong to this course");
        }

        if (!completedLessons.contains(lesson)) {
            completedLessons.add(lesson);
        }
    }

    public float getProgressPercent() {
        int totalLessonsCnt = 0;
        for (Module module : course.modules) {
            totalLessonsCnt += module.getLessons().size();
        }
        if (totalLessonsCnt == 0) return 0.0f;

        int completedLessonCnt = completedLessons.size();

        progressPercent = ((float) completedLessonCnt / totalLessonsCnt) * 100f;

        return progressPercent;
    }
}

enum EnrollmentStatus {
    ENROLLED,
    FINISHED,
}

@Getter
@Setter
class Course {
    int id;
    String title;
    String description;
    Instrcutor instrcutor;
    List<Module> modules = new ArrayList<>();
    List<Enrollment> enrollments = new ArrayList<>();

    public Course(int id, String title, String description, Instrcutor instructor) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.instrcutor = instructor;
    }

//    public List<Module> getModules(){
//        return  modules;
//    }

    public void addModule(Module module) {
        modules.add(module);
    }

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
    }
}

@Getter
@Setter
class Module {
    int id;
    String title;
    List<Lesson> lessons = new ArrayList<>();

    public Module(int id, String title) {
        this.id = id;
        this.title = title;
    }

//    public List<Lesson> getLessons(){
//        return lessons;
//    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
    }
}

@Getter
@Setter
class Lesson {
    int id;
    String title;
    String contentUrl;
    int durationInMinutes;

    public Lesson(int id, String title, String contentUrl, int durationInMinutes) {
        this.id = id;
        this.title = title;
        this.contentUrl = contentUrl;
        this.durationInMinutes = durationInMinutes;
    }
}

public class Main {
    static void main() {
        Student student1 = new Student(1, "Nishu1", "nishu1@gmail.com");
        Student student2 = new Student(2, "Nishu2", "nishu2@gmail.com");
        Student student3 = new Student(3, "Nishu3", "nishu3@gmail.com");
        Student student4 = new Student(4, "Nishu4", "nishu4@gmail.com");
        Student student5 = new Student(5, "Nishu5", "nishu5@gmail.com");

        Instrcutor instrcutor1 = new Instrcutor(1, "instructor1", "instructor1@gmail.com");
        Instrcutor instrcutor2 = new Instrcutor(2, "instructor2", "instructor2@gmail.com");

        Lesson lesson1 = new Lesson(1, "lesson1", "lessonUrl1", 10);
        Lesson lesson2 = new Lesson(2, "lesson2", "lessonUrl2", 10);
        Lesson lesson3 = new Lesson(3, "lesson3", "lessonUrl3", 10);
        Lesson lesson4 = new Lesson(4, "lesson4", "lessonUrl4", 10);
        Lesson lesson5 = new Lesson(5, "lesson5", "lessonUrl5", 10);

        Module module = new Module(1, "module1");
        module.addLesson(lesson1);
        module.addLesson(lesson2);
        module.addLesson(lesson3);
        module.addLesson(lesson4);
        module.addLesson(lesson5);


        Course course1 = new Course(1, "course1", "cousre1 desp", instrcutor1);
        course1.addModule(module);
        Course course2 = new Course(2, "course2", "cousre2 desp", instrcutor2);

        INotificationChannel notificationChannel = new EmailNotificationChannel();

        EnrollmentService enrollmentService = new EnrollmentService(notificationChannel);

        Enrollment enrollment1 = enrollmentService.enroll(student1, course1);
        Enrollment enrollment2 = enrollmentService.enroll(student2, course1);
        Enrollment enrollment3 = enrollmentService.enroll(student3, course1);

        enrollmentService.completeLesson(enrollment1, lesson1);


        enrollmentService.completeLesson(enrollment1, lesson2);


        enrollmentService.completeLesson(enrollment1, lesson3);


        enrollmentService.completeLesson(enrollment1, lesson4);


        enrollmentService.completeLesson(enrollment1, lesson5);

    }
}


class EnrollmentService {
    INotificationChannel notificationChannel;

    public EnrollmentService(INotificationChannel notificationChannel) {
        this.notificationChannel = notificationChannel;
    }

    public Enrollment enroll(Student student, Course course) {
        List<Enrollment> enrollments = course.getEnrollments();
        int nextId = enrollments.size();
        Date date = new Date();
        Enrollment enrollment = new Enrollment(nextId, date, student, course);
        course.addEnrollment(enrollment);
        student.addEnrollment(enrollment);
        return enrollment;
    }

    public void completeLesson(Enrollment enrollment, Lesson lesson) {
        enrollment.markLessonCompleted(lesson);
        float progressPercent = enrollment.getProgressPercent();
        if (progressPercent == 100.0f) {
            enrollment.setStatus(EnrollmentStatus.FINISHED);
        }
        notificationChannel.send(enrollment.student, "You have completed " + enrollment.getProgressPercent() + "% of the course " + enrollment.course.getTitle());
    }
}

interface INotificationChannel {
    public void send(User user, String message);
}


class EmailNotificationChannel implements INotificationChannel {

    @Override
    public void send(User user, String message) {
        System.out.println(user.getEmail() + " got a message " + message + " via email");
    }
}

class SmaNotificationChannel implements INotificationChannel {

    @Override
    public void send(User user, String message) {
        System.out.println(user + " got a message " + message + " via sms");
    }
}

interface ICourseRepository {
    public Course findbyId(int id);

    public List<Course> findbyInstructor(Instrcutor instrcutor);

    public void save(Course course);
}

interface IEnrollmentRepository {
    public Enrollment findbyId(int id);

    public Enrollment findbyStudentAndCourse(Student student, Course course);

    public void save(Enrollment enrollment);
}