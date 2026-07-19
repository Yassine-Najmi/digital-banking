package ma.enset.ebankbackend.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private static final String SYSTEM_PROMPT = """
            Tu es le conseiller virtuel d'E-Bank, une banque digitale.
            Tu réponds uniquement en français, de façon claire et professionnelle.
            Tu t'appuies sur le contexte documentaire fourni (produits, frais, découvert, épargne, procédures).
            Si la question n'est pas liée à E-Bank ou si l'information n'est pas dans le contexte,
            refuse poliment d'y répondre et propose de reformuler une question sur les produits bancaires.
            Ne invente pas de tarifs ni de procédures absents du contexte.
            """;

    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder().topK(4).similarityThreshold(0.5d).build())
                                .build()
                )
                .build();
    }

    public String ask(String question) {
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }
}
