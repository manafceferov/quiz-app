//package com.quiz.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.quiz.dto.ai.AiAnswer;
//import com.quiz.dto.ai.AiQuestion;
//import com.quiz.dto.ai.AiQuizResponse;
//import com.quiz.entity.Answer;
//import com.quiz.entity.Participant;
//import com.quiz.entity.Question;
//import com.quiz.entity.Topic;
//import com.quiz.repository.AnswerRepository;
//import com.quiz.repository.ParticipantRepository;
//import com.quiz.repository.QuestionRepository;
//import com.quiz.repository.TopicRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import org.springframework.beans.factory.annotation.Value;
//
//@Service
//public class AiQuizService {
//
//    private final ObjectMapper objectMapper;
//    private final String apiKey;
//    private final String model;
//    private final HttpClient httpClient;
//    private final TopicRepository topicRepository;
//    private final QuestionRepository questionRepository;
//    private final AnswerRepository answerRepository;
//    private final ParticipantRepository participantRepository;
//
//    public AiQuizService(ObjectMapper objectMapper,
//                         @Value("${anthropic.api.key}") String apiKey,
//                         @Value("${anthropic.model}") String model,
//                         TopicRepository topicRepository,
//                         QuestionRepository questionRepository,
//                         AnswerRepository answerRepository,
//                         ParticipantRepository participantRepository
//    ) {
//        this.objectMapper = objectMapper;
//        this.apiKey = apiKey;
//        this.model = model;
//        this.httpClient = HttpClient.newBuilder().build();
//        this.topicRepository = topicRepository;
//        this.questionRepository = questionRepository;
//        this.answerRepository = answerRepository;
//        this.participantRepository = participantRepository;
//    }
//
//    public AiQuizResponse generateQuiz(String prompt) throws Exception {
//        if (apiKey == null || apiKey.isBlank() ||
//                apiKey.contains("BURAYA") || apiKey.contains("your-actual-key") ||
//                apiKey.equals("sk-ant-api03-BURAYA_COPY_ETDIYIN_KEY_GELIR")) {
//            System.out.println("⚠️  MOCK MODE: Real API key tapılmadı, test datası qaytarılır");
//            return createMockResponse(prompt);
//        }
//
//        String systemPrompt = """
//                    Sən Azərbaycan dilində test sualları yaradan peşəkar AI köməkçisən.
//                    // ...
//                """;
//
//        Map<String, Object> requestBody = Map.of(
//                "model", model,
//                "max_tokens", 8000,
//                "temperature", 0.7,
//                "messages", List.of(Map.of("role", "user", "content", prompt)),
//                "system", systemPrompt
//        );
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("https://api.anthropic.com/v1/messages"))
//                .header("Content-Type", "application/json")
//                .header("x-api-key", apiKey)
//                .header("anthropic-version", "2023-06-01")
//                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
//                .build();
//        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//
//        if (response.statusCode() != 200) {
//            throw new RuntimeException("AI API xətası: " + response.body());
//        }
//        Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
//        List<Map<String, Object>> content = (List<Map<String, Object>>) responseMap.get("content");
//        String text = (String) content.get(0).get("text");
//        String cleanJson = text.replace("```json", "").replace("```", "").trim();
//        return objectMapper.readValue(cleanJson, AiQuizResponse.class);
//    }
//
//    @Transactional
//    public Topic saveQuizToDatabase(String email,
//                                    String topicName,
//                                    String questionsJson
//    ) throws Exception {
//        System.out.println("=== SAVE QUIZ START ===");
//        System.out.println("Topic: " + topicName);
//        System.out.println("Email: " + email);
//
//        Participant participant = participantRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("Participant tapılmadı: " + email));
//
//        Long participantId = participant.getId();
//        System.out.println("Participant ID: " + participantId);
//
//        Topic topic = new Topic();
//        topic.setName(topicName);
//        topic.setActive(true);
//        topic.setByParticipant(participantId);
//        Topic savedTopic = topicRepository.save(topic);
//        System.out.println("Topic saved with ID: " + savedTopic.getId());
//
//        List<Map<String, Object>> questions = objectMapper.readValue(
//                questionsJson,
//                objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
//        );
//        System.out.println("Questions parsed: " + questions.size());
//
//        for (Map<String, Object> questionData : questions) {
//            Question question = new Question();
//            question.setQuestion((String) questionData.get("questionText"));
//            question.setActive(true);
//            question.setTopicId(savedTopic.getId());
//            question.setByParticipant(participantId);
//            Question savedQuestion = questionRepository.save(question);
//            System.out.println("Question saved: " + savedQuestion.getId());
//            List<Map<String, Object>> answers = (List<Map<String, Object>>) questionData.get("answers");
//            for (Map<String, Object> answerData : answers) {
//                Answer answer = new Answer();
//                answer.setAnswer((String) answerData.get("text"));
//
//                Boolean isCorrect = (Boolean) answerData.get("isCorrect");
//                if (isCorrect == null) {
//                    isCorrect = (Boolean) answerData.get("correct");
//                }
//                answer.setCorrect(isCorrect != null ? isCorrect : false);
//                answer.setActive(true);
//                answer.setQuestionId(savedQuestion.getId());
//                answer.setByParticipant(participantId);
//                answerRepository.save(answer);
//                System.out.println("Answer saved: " + answer.getAnswer() + " (correct: " + answer.isCorrect() + ")");
//            }
//        }
//        System.out.println("=== SAVE SUCCESS ===");
//        return savedTopic;
//    }
//
//    private AiQuizResponse createMockResponse(String prompt) {
//        AiQuizResponse response = new AiQuizResponse();
//
//        int questionCount = 5;
//        Pattern pattern = Pattern.compile("(\\d+)\\s*sual");
//        Matcher matcher = pattern.matcher(prompt.toLowerCase());
//        if (matcher.find()) {
//            questionCount = Integer.parseInt(matcher.group(1));
//        }
//        String topicName = "Ümumi Bilik Testi";
//        String p = prompt.toLowerCase();
//
//        if (p.contains("javascript") || p.contains("js")) topicName = "JavaScript";
//        else if (p.contains("java")) topicName = "Java";
//        else if (p.contains("spring")) topicName = "Spring";
//        else if (p.contains("hibernate")) topicName = "Hibernate";
//        else if (p.contains("sql") || p.contains("postgres") || p.contains("database")) topicName = "SQL";
//        else if (p.contains("docker")) topicName = "Docker";
//        else if (p.contains("liquibase")) topicName = "Liquibase";
//        else if (p.contains("git")) topicName = "Git";
//        response.setTopicName(topicName);
//
//        List<com.quiz.dto.ai.AiQuestion> questions = new ArrayList<>();
//
//        switch (topicName) {
//
//            case "Java" -> {
//                questions.add(new AiQuestion("JVM nədir ?",
//                        List.of(new AiAnswer("Java Virtual Machine", true),
//                                new AiAnswer("Java Vendor Machine", false),
//                                new AiAnswer("Just Virtual Method", false),
//                                new AiAnswer("Java Variable Model", false))));
//
//                questions.add(new AiQuestion("JDK nədir ?",
//                        List.of(new AiAnswer("Java Runtime Environment", false),
//                                new AiAnswer("Java Development Kit", true),
//                                new AiAnswer("Java Virtual Machine", false),
//                                new AiAnswer("Java Debug Kit", false))));
//
//                questions.add(new AiQuestion("OOP prinsiplərinə aid deyil ?",
//                        List.of(new AiAnswer("Encapsulation", false),
//                                new AiAnswer("Inheritance", false),
//                                new AiAnswer("Polymorphism", false),
//                                new AiAnswer("Compilation", true))));
//
//                questions.add(new AiQuestion("final keyword nə edir ?",
//                        List.of(new AiAnswer("Dəyişməz edir", true),
//                                new AiAnswer("Loop yaradır", false),
//                                new AiAnswer("Exception tutur", false),
//                                new AiAnswer("Class silir", false))));
//
//                questions.add(new AiQuestion("Interface nədir ?",
//                        List.of(new AiAnswer("Database cədvəli", false),
//                                new AiAnswer("Loop strukturu", false),
//                                new AiAnswer("Abstrakt metodlar toplusu", true),
//                                new AiAnswer("Exception tipi", false))));
//            }
//
//            case "Spring" -> {
//                questions.add(new AiQuestion("Spring Boot nədir ?",
//                        List.of(new AiAnswer("Database sistemi", false),
//                                new AiAnswer("Frontend framework", false),
//                                new AiAnswer("OS", false),
//                                new AiAnswer("Sadələşdirilmiş Spring framework", true))));
//
//                questions.add(new AiQuestion("@Autowired nə edir ?",
//                        List.of(new AiAnswer("Dependency injection edir", true),
//                                new AiAnswer("Class yaradır", false),
//                                new AiAnswer("DB yaradır", false),
//                                new AiAnswer("Loop yaradır", false))));
//
//                questions.add(new AiQuestion("@RestController nədir ?",
//                        List.of(new AiAnswer("Database class", false),
//                                new AiAnswer("REST API controller", true),
//                                new AiAnswer("Service layer", false),
//                                new AiAnswer("Entity class", false))));
//
//                questions.add(new AiQuestion("Spring Bean nədir ?",
//                        List.of(new AiAnswer("Database", false),
//                                new AiAnswer("Loop", false),
//                                new AiAnswer("Container tərəfindən idarə olunan obyekt", true),
//                                new AiAnswer("Thread", false))));
//
//                questions.add(new AiQuestion("ApplicationContext nədir ?",
//                        List.of(new AiAnswer("Spring container", true),
//                                new AiAnswer("Database", false),
//                                new AiAnswer("API", false),
//                                new AiAnswer("Server", false))));
//            }
//
//            case "Hibernate" -> {
//                questions.add(new AiQuestion("Hibernate nədir ?",
//                        List.of(new AiAnswer("Frontend library", false),
//                                new AiAnswer("DB engine", false),
//                                new AiAnswer("ORM framework", true),
//                                new AiAnswer("OS", false))));
//
//                questions.add(new AiQuestion("Lazy loading nədir ?",
//                        List.of(new AiAnswer("Data lazım olduqda yüklənir", true),
//                                new AiAnswer("Həmişə yüklənir", false),
//                                new AiAnswer("Silinir", false),
//                                new AiAnswer("Cache olur", false))));
//
//                questions.add(new AiQuestion("Entity nədir ?",
//                        List.of(new AiAnswer("API endpoint", false),
//                                new AiAnswer("Loop", false),
//                                new AiAnswer("DB cədvəlinin Java qarşılığı", true),
//                                new AiAnswer("Thread", false))));
//
//                questions.add(new AiQuestion("@OneToMany nədir ?",
//                        List.of(new AiAnswer("Bir-bir əlaqə", false),
//                                new AiAnswer("Join query", false),
//                                new AiAnswer("Transaction", false),
//                                new AiAnswer("Bir-çox əlaqə", true))));
//
//                questions.add(new AiQuestion("Session nədir ?",
//                        List.of(new AiAnswer("DB ilə əlaqə obyektidir", true),
//                                new AiAnswer("Frontend", false),
//                                new AiAnswer("Server", false),
//                                new AiAnswer("API", false))));
//            }
//
//            case "SQL" -> {
//                questions.add(new AiQuestion("INNER JOIN nə edir ?",
//                        List.of(new AiAnswer("Hamısını qaytarır", false),
//                                new AiAnswer("Uyğun sətrləri qaytarır", true),
//                                new AiAnswer("Solu qaytarır", false),
//                                new AiAnswer("Sağı qaytarır", false))));
//
//                questions.add(new AiQuestion("PRIMARY KEY nədir ?",
//                        List.of(new AiAnswer("Unikal identifikator", true),
//                                new AiAnswer("Sadə sütun", false),
//                                new AiAnswer("Null sahə", false),
//                                new AiAnswer("Indexsiz sahə", false))));
//
//                questions.add(new AiQuestion("ACID-də 'A' nədir ?",
//                        List.of(new AiAnswer("Access", false),
//                                new AiAnswer("Accuracy", false),
//                                new AiAnswer("Atomicity", true),
//                                new AiAnswer("Alignment", false))));
//
//                questions.add(new AiQuestion("LEFT JOIN nə edir ?",
//                        List.of(new AiAnswer("Sol cədvəli tam qaytarır", true),
//                                new AiAnswer("Sağı qaytarır", false),
//                                new AiAnswer("Hamısını qaytarır", false),
//                                new AiAnswer("Heç nə qaytarmır", false))));
//
//                questions.add(new AiQuestion("Index nədir ?",
//                        List.of(new AiAnswer("Data silir", false),
//                                new AiAnswer("Table yaradır", false),
//                                new AiAnswer("Join edir", false),
//                                new AiAnswer("Sorğunu sürətləndirir", true))));
//            }
//
//            case "Docker" -> {
//                questions.add(new AiQuestion("Docker nədir ?",
//                        List.of(
//                                new AiAnswer("Database", false),
//                                new AiAnswer("Container platforması", true),
//                                new AiAnswer("Programming language", false),
//                                new AiAnswer("IDE", false)
//                        )));
//
//                questions.add(new AiQuestion("Docker image nədir ?",
//                        List.of(
//                                new AiAnswer("Running container", false),
//                                new AiAnswer("Database", false),
//                                new AiAnswer("Container üçün template", true),
//                                new AiAnswer("Server", false)
//                        )));
//
//                questions.add(new AiQuestion("Docker container nədir ?",
//                        List.of(
//                                new AiAnswer("Virtual machine", false),
//                                new AiAnswer("İzolə olunmuş işləmə mühiti", true),
//                                new AiAnswer("Database engine", false),
//                                new AiAnswer("API gateway", false)
//                        )));
//
//                questions.add(new AiQuestion("Dockerfile nə üçün istifadə olunur ?",
//                        List.of(
//                                new AiAnswer("Container işlətmək üçün", false),
//                                new AiAnswer("Image build etmək üçün", true),
//                                new AiAnswer("Database yaratmaq üçün", false),
//                                new AiAnswer("Frontend yazmaq üçün", false)
//                        )));
//
//                questions.add(new AiQuestion("docker run nə edir ?",
//                        List.of(
//                                new AiAnswer("Image silir", false),
//                                new AiAnswer("Container yaradır və işə salır", true),
//                                new AiAnswer("Repo yaradır", false),
//                                new AiAnswer("Server dayandırır", false)
//                        )));
//            }
//
//            case "Liquibase" -> {
//                questions.add(new AiQuestion("Liquibase nə edir ?",
//                        List.of(
//                                new AiAnswer("Frontend yaradır", false),
//                                new AiAnswer("API yaradır", false),
//                                new AiAnswer("Database migration idarə edir", true),
//                                new AiAnswer("Server idarə edir", false)
//                        )));
//
//                questions.add(new AiQuestion("ChangeSet nədir ?",
//                        List.of(
//                                new AiAnswer("API endpoint", false),
//                                new AiAnswer("DB dəyişiklik bloku", true),
//                                new AiAnswer("Controller", false),
//                                new AiAnswer("Service class", false)
//                        )));
//
//                questions.add(new AiQuestion("Liquibase hansı formatları dəstəkləyir ?",
//                        List.of(
//                                new AiAnswer("JSON, XML, YAML", true),
//                                new AiAnswer("Only SQL", false),
//                                new AiAnswer("Only Java", false),
//                                new AiAnswer("Only HTML", false)
//                        )));
//
//                questions.add(new AiQuestion("Rollback nədir ?",
//                        List.of(
//                                new AiAnswer("DB silmək", false),
//                                new AiAnswer("Dəyişiklikləri geri qaytarmaq", true),
//                                new AiAnswer("Yeni table yaratmaq", false),
//                                new AiAnswer("API çağırmaq", false)
//                        )));
//
//                questions.add(new AiQuestion("Liquibase harada istifadə olunur ?",
//                        List.of(
//                                new AiAnswer("Frontend development", false),
//                                new AiAnswer("Database versioning", true),
//                                new AiAnswer("Game development", false),
//                                new AiAnswer("UI design", false)
//                        )));
//            }
//
//            case "JavaScript" -> {
//                questions.add(new AiQuestion("JavaScript nədir ?",
//                        List.of(
//                                new AiAnswer("Database", false),
//                                new AiAnswer("Proqramlaşdırma dili", true),
//                                new AiAnswer("Operating system", false),
//                                new AiAnswer("IDE", false)
//                        )));
//
//                questions.add(new AiQuestion("let və var fərqi nədir ?",
//                        List.of(
//                                new AiAnswer("let block scope-dur", true),
//                                new AiAnswer("var block scope-dur", false),
//                                new AiAnswer("let global-dır", false),
//                                new AiAnswer("heç fərq yoxdur", false)
//                        )));
//
//                questions.add(new AiQuestion("JavaScript-də '===’ nə edir ?",
//                        List.of(
//                                new AiAnswer("Yalnız dəyəri yoxlayır", false),
//                                new AiAnswer("Dəyər və tipi yoxlayır", true),
//                                new AiAnswer("Assignment edir", false),
//                                new AiAnswer("Loop yaradır", false)
//                        )));
//
//                questions.add(new AiQuestion("Promise nədir ?",
//                        List.of(
//                                new AiAnswer("Async əməliyyat nəticəsi", true),
//                                new AiAnswer("Database", false),
//                                new AiAnswer("Loop", false),
//                                new AiAnswer("Thread", false)
//                        )));
//
//                questions.add(new AiQuestion("DOM nədir ?",
//                        List.of(
//                                new AiAnswer("Database Object Model", false),
//                                new AiAnswer("Document Object Model", true),
//                                new AiAnswer("Data Object Manager", false),
//                                new AiAnswer("Digital Output Mode", false)
//                        )));
//            }
//        }
//
//        if (questions.size() > questionCount) {
//            questions = questions.subList(0, questionCount);
//        }
//        response.setQuestions(questions);
//        return response;
//    }
//}