package com.quiz.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.dto.ai.AiQuizResponse;
import com.quiz.entity.Answer;
import com.quiz.entity.Participant;
import com.quiz.entity.Question;
import com.quiz.entity.Topic;
import com.quiz.repository.AnswerRepository;
import com.quiz.repository.ParticipantRepository;
import com.quiz.repository.QuestionRepository;
import com.quiz.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;

@Service
public class AiQuizService {

    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;
    private final HttpClient httpClient;
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ParticipantRepository participantRepository;

    public AiQuizService(ObjectMapper objectMapper,
                         @Value("${anthropic.api.key}") String apiKey,
                         @Value("${anthropic.model}") String model,
                         TopicRepository topicRepository,
                         QuestionRepository questionRepository,
                         AnswerRepository answerRepository,
                         ParticipantRepository participantRepository
    ) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.httpClient = HttpClient.newBuilder().build();
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.participantRepository = participantRepository;
    }

    public AiQuizResponse generateQuiz(String prompt) throws Exception {
        if (apiKey == null || apiKey.isBlank() ||
                apiKey.contains("BURAYA") || apiKey.contains("your-actual-key") ||
                apiKey.equals("sk-ant-api03-BURAYA_COPY_ETDIYIN_KEY_GELIR")) {
            System.out.println("⚠️  MOCK MODE: Real API key tapılmadı, test datası qaytarılır");
            return createMockResponse(prompt);
        }

        String systemPrompt = """
            Sən Azərbaycan dilində test sualları yaradan peşəkar AI köməkçisən.
            // ...
        """;

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", 8000,
                "temperature", 0.7,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "system", systemPrompt
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.anthropic.com/v1/messages"))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("AI API xətası: " + response.body());
        }

        Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
        List<Map<String, Object>> content = (List<Map<String, Object>>) responseMap.get("content");
        String text = (String) content.get(0).get("text");

        String cleanJson = text.replace("```json", "").replace("```", "").trim();
        return objectMapper.readValue(cleanJson, AiQuizResponse.class);
    }

    @Transactional
    public Topic saveQuizToDatabase(String email, String topicName, String questionsJson) throws Exception {
        System.out.println("=== SAVE QUIZ START ===");
        System.out.println("Topic: " + topicName);
        System.out.println("Email: " + email);

        Participant participant = participantRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Participant tapılmadı: " + email));

        Long participantId = participant.getId();
        System.out.println("Participant ID: " + participantId);

        Topic topic = new Topic();
        topic.setName(topicName);
        topic.setActive(true);
        topic.setByParticipant(participantId);
        Topic savedTopic = topicRepository.save(topic);
        System.out.println("Topic saved with ID: " + savedTopic.getId());

        List<Map<String, Object>> questions = objectMapper.readValue(
                questionsJson,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
        );
        System.out.println("Questions parsed: " + questions.size());

        for (Map<String, Object> questionData : questions) {
            Question question = new Question();
            question.setQuestion((String) questionData.get("questionText"));
            question.setActive(true);
            question.setTopicId(savedTopic.getId());
            question.setByParticipant(participantId);
            Question savedQuestion = questionRepository.save(question);
            System.out.println("Question saved: " + savedQuestion.getId());

            List<Map<String, Object>> answers = (List<Map<String, Object>>) questionData.get("answers");
            for (Map<String, Object> answerData : answers) {
                Answer answer = new Answer();
                answer.setAnswer((String) answerData.get("text"));

                Boolean isCorrect = (Boolean) answerData.get("isCorrect");
                if (isCorrect == null) {
                    isCorrect = (Boolean) answerData.get("correct");
                }
                answer.setCorrect(isCorrect != null ? isCorrect : false);
                answer.setActive(true);
                answer.setQuestionId(savedQuestion.getId());
                answer.setByParticipant(participantId);
                answerRepository.save(answer);
                System.out.println("Answer saved: " + answer.getAnswer() + " (correct: " + answer.isCorrect() + ")");
            }
        }
        System.out.println("=== SAVE SUCCESS ===");
        return savedTopic;
    }

    private AiQuizResponse createMockResponse(String prompt) {
        AiQuizResponse response = new AiQuizResponse();

        int questionCount = 5;
        Pattern pattern = Pattern.compile("(\\d+)\\s*sual");
        Matcher matcher = pattern.matcher(prompt.toLowerCase());
        if (matcher.find()) {
            questionCount = Integer.parseInt(matcher.group(1));
        }

        String topicName = "Ümumi Bilik Testi";
        if (prompt.toLowerCase().contains("java")) {
            topicName = "Java Proqramlaşdırma";
        } else if (prompt.toLowerCase().contains("spring")) {
            topicName = "Spring Framework";
        } else if (prompt.toLowerCase().contains("sql") || prompt.toLowerCase().contains("postgresql")) {
            topicName = "SQL və Verilənlər Bazası";
        } else if (prompt.toLowerCase().contains("javascript") || prompt.toLowerCase().contains("js")) {
            topicName = "JavaScript";
        } else if (prompt.toLowerCase().contains("python")) {
            topicName = "Python Proqramlaşdırma";
        } else if (prompt.toLowerCase().contains("html") || prompt.toLowerCase().contains("css")) {
            topicName = "HTML və CSS";
        }
        response.setTopicName(topicName);

        List<com.quiz.dto.ai.AiQuestion> questions = new ArrayList<>();
        for (int i = 1; i <= questionCount; i++) {
            com.quiz.dto.ai.AiQuestion question = new com.quiz.dto.ai.AiQuestion();
            question.setQuestionText(i + " ?");

            List<com.quiz.dto.ai.AiAnswer> answers = new ArrayList<>();
            answers.add(new com.quiz.dto.ai.AiAnswer("Doğru cavab " + i, true));
            answers.add(new com.quiz.dto.ai.AiAnswer("Səhv cavab A", false));
            answers.add(new com.quiz.dto.ai.AiAnswer("Səhv cavab B", false));
            answers.add(new com.quiz.dto.ai.AiAnswer("Səhv cavab C", false));
            question.setAnswers(answers);
            questions.add(question);
        }
        response.setQuestions(questions);
        return response;
    }
}